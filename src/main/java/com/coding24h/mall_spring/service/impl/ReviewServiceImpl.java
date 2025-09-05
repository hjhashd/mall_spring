package com.coding24h.mall_spring.service.impl;

import com.coding24h.mall_spring.dto.review.ReviewQueryDTO;
import com.coding24h.mall_spring.dto.review.ReviewSubmitDTO;
import com.coding24h.mall_spring.entity.Reply;
import com.coding24h.mall_spring.entity.Review;
import com.coding24h.mall_spring.entity.vo.RatingDistributionVO;
import com.coding24h.mall_spring.entity.vo.RatingSummaryVO;
import com.coding24h.mall_spring.entity.vo.ReviewVO;
import com.coding24h.mall_spring.mapper.OrderItemMapper;
import com.coding24h.mall_spring.mapper.OrderMapper;
import com.coding24h.mall_spring.mapper.review.ReviewMapper;
import com.coding24h.mall_spring.service.ReviewService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评价服务实现类
 */
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final OrderItemMapper orderItemMapper;

    // 构造函数注入
    public ReviewServiceImpl(ReviewMapper reviewMapper, OrderItemMapper orderItemMapper) {
        this.reviewMapper = reviewMapper;
        this.orderItemMapper = orderItemMapper;
    }



    // 【关键修改】 getAllReviews 方法，确保调用了我们修正后的 convertToVO
    @Override
    public PageInfo<ReviewVO> getAllReviews(ReviewQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        // 3. 将 sellerId 传递给 Mapper
        List<Review> reviews = reviewMapper.selectAllReviews(
                queryDTO.getRating(),
                queryDTO.getHasImage(),
                queryDTO.getHasSellerReply(),
                queryDTO.getSort(),
                queryDTO.getOrder(),
                queryDTO.getSellerId()
        );

        List<ReviewVO> reviewVOList = reviews.stream()
                .map(review -> {
                    // 调用已修正的转换方法
                    ReviewVO vo = convertToVO(review);
                    List<Reply> replies = reviewMapper.selectRepliesByReviewId(review.getReviewId());
                    vo.setReplies(replies);
                    return vo;
                })
                .collect(Collectors.toList());

        return new PageInfo<>(reviewVOList);
    }


    /**
     * 【关键修改】将Review实体转换为VO的私有方法
     * 修正了 imageUrls 的转换逻辑，并添加了商品信息的映射。
     */
    private ReviewVO convertToVO(Review review) {
        ReviewVO vo = new ReviewVO();
        // 复制大部分相同名称和类型的属性
        BeanUtils.copyProperties(review, vo);

        // --- 1. 手动处理 imageUrls (String -> List<String>) ---
        if (StringUtils.hasText(review.getImageUrls())) {
            // 按逗号分割字符串，并转换为List
            List<String> imageUrlList = Arrays.asList(review.getImageUrls().split(","));
            vo.setImageUrls(imageUrlList);
        } else {
            // 如果数据库中为null或空字符串，确保前端得到一个空数组而不是null
            vo.setImageUrls(Collections.emptyList());
        }

        // --- 2. 手动处理关联查询出的商品信息 ---
        // 这些字段在Review实体类中存在（因为MyBatis的resultMap），需要手动映射到VO
        vo.setProductName(review.getProductName());
        vo.setProductImage(review.getProductImage());


        // --- 3. 处理匿名显示 ---
        // 注意：请确保你的 ReviewVO 中有 setIsAnonymous 方法，如果没有，请添加
        // 或者直接在VO中处理该逻辑。此处我们假设 isAnonymous 字段已通过 BeanUtils 复制。
        if (Boolean.TRUE.equals(review.getIsAnonymous())) {
            vo.setUsername("匿名用户");
            // 头像可以设置为一个默认的匿名头像URL
            vo.setUserAvatar("https://placehold.co/40x40/95a5a6/ffffff?text=?");
        }

        return vo;
    }

    @Override
    public PageInfo<ReviewVO> getProductReviews(ReviewQueryDTO queryDTO) {
        // 使用PageHelper进行分页
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());

        // 查询评价列表
        List<Review> reviews = reviewMapper.selectReviewsByProductId(
                queryDTO.getProductId(),
                queryDTO.getRating(),
                queryDTO.getHasImage(),
                queryDTO.getHasSellerReply(),
                queryDTO.getSort(),
                queryDTO.getOrder()
        );

        // 转换为VO，并手动加载回复列表
        List<ReviewVO> reviewVOList = reviews.stream()
                .map(review -> {
                    ReviewVO vo = convertToVO(review);
                    // 手动加载回复列表
                    List<Reply> replies = reviewMapper.selectRepliesByReviewId(review.getReviewId());
                    vo.setReplies(replies);
                    return vo;
                })
                .collect(Collectors.toList());

        // 返回PageInfo对象
        return new PageInfo<>(reviewVOList);
    }

    @Override
    public RatingSummaryVO getProductRatingSummary(Integer productId) {
        RatingSummaryVO summary = new RatingSummaryVO();

        // 获取总数和平均分
        RatingSummaryVO basicInfo = reviewMapper.selectRatingSummaryByProductId(productId);
        if (basicInfo != null) {
            summary.setTotal(basicInfo.getTotal());
            summary.setAverage(basicInfo.getAverage());
        }

        // 获取评分分布
        List<RatingDistributionVO> distribution = reviewMapper.selectRatingDistributionByProductId(productId);
        summary.setDistribution(distribution);

        return summary;
    }

    /**
     * 提交商品评价 (推荐的实现方式)
     * 这个方法保证了操作的原子性，并且只更新单个订单项的状态，
     * 完美支持一个订单包含多个商品项的场景。
     * @param reviewDTO 评价内容
     * @param userId 用户ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitReview(ReviewSubmitDTO reviewDTO, Long userId) {
        // 1. 检查该订单项是否已经评价过，防止重复提交
        if (reviewMapper.countByOrderItemId(reviewDTO.getOrderItemId()) > 0) {
            System.err.println("用户 " + userId + " 已评价过订单项 " + reviewDTO.getOrderItemId());
            return false; // 或者可以抛出一个自定义异常，例如：throw new AlreadyReviewedException("您已评价过此商品");
        }

        // 2. 创建并填充Review实体
        Review review = new Review();
        review.setOrderItemId(reviewDTO.getOrderItemId());
        review.setUserId(userId.intValue());
        review.setRating(reviewDTO.getRating());
        review.setContent(reviewDTO.getContent());
        review.setImageUrls(reviewDTO.getImageUrls()); // 假设URL已在Controller处理好
        review.setIsAnonymous(reviewDTO.getIsAnonymous());
        review.setCreatedAt(LocalDateTime.now());
        review.setHasAppend(false);
        review.setDeleted(false);
        review.setProductId(reviewDTO.getProductId());

        // 3. 插入评价记录到 reviews 表
        int reviewInsertResult = reviewMapper.insertReview(review);
        if (reviewInsertResult <= 0) {
            // 插入失败，事务将回滚，无需手动处理
            return false;
        }

        // 4. 【核心逻辑】更新 order_items 表中对应项的 is_reviewed 状态为 true (1)
        //    这样就不会影响订单中其他未评价的商品。
        int statusUpdateResult = orderItemMapper.updateReviewedStatus(reviewDTO.getOrderItemId(), true);
        if (statusUpdateResult <= 0) {
            // 如果更新失败，可能意味着订单项不存在。
            // 抛出异常，让 @Transactional 注解回滚第3步中已插入的评价数据，保证数据一致性。
            throw new RuntimeException("更新订单项评价状态失败，找不到ID为 " + reviewDTO.getOrderItemId() + " 的订单项。");
        }

        return true;
        // 注意：这里不需要 try-catch 块。如果发生异常，@Transactional 会自动处理回滚。
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteReview(Integer reviewId, Long userId) {
        try {
            // 检查权限
            Review review = reviewMapper.selectReviewById(reviewId);
            if (review == null || !review.getUserId().equals(userId.intValue())) {
                return false;
            }

            // 逻辑删除
            int result = reviewMapper.deleteReviewById(reviewId);
            return result > 0;

        } catch (Exception e) {
            System.err.println("删除评价失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("删除评价失败: " + e.getMessage());
        }
    }

    @Override
    public ReviewVO getReviewDetail(Integer reviewId) {
        Review review = reviewMapper.selectReviewById(reviewId);
        if (review == null) {
            return null;
        }
        return convertToVO(review);
    }

    @Override
    public PageInfo<ReviewVO> getUserReviews(Long userId, Integer page, Integer size) {
        // 使用PageHelper进行分页
        PageHelper.startPage(page, size);

        // 查询评价列表
        List<Review> reviews = reviewMapper.selectReviewsByUserId(userId.intValue());

        // 转换为VO
        List<ReviewVO> reviewVOList = reviews.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 返回PageInfo对象
        return new PageInfo<>(reviewVOList);
    }

    @Override
    public List<Review> getPendingReviews(Long userId, Long productId) {
        System.out.println("查询待评价订单 - userId: " + userId + ", productId: " + productId);
        List<Review> result = reviewMapper.selectPendingReviewsByUserId(userId, productId);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addSellerReply(Integer reviewId, String sellerReply) {
        try {
            int result = reviewMapper.updateSellerReply(reviewId, sellerReply);
            return result > 0;
        } catch (Exception e) {
            System.err.println("添加卖家回复失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("添加卖家回复失败: " + e.getMessage());
        }
    }

    @Override
    public boolean hasUserReviewed(Integer orderItemId) {
        Review review = reviewMapper.selectReviewByOrderItemId(orderItemId);
        return review != null;
    }

    @Override
    public Integer getReviewCount(Integer productId) {
        return reviewMapper.selectReviewCountByProductId(productId);
    }

    @Override
    public Double getAverageRating(Integer productId) {
        return reviewMapper.selectAverageRatingByProductId(productId);
    }

}
