package com.coding24h.mall_spring.service;

import com.coding24h.mall_spring.dto.review.ReviewQueryDTO;
import com.coding24h.mall_spring.dto.review.ReviewSubmitDTO;
import com.coding24h.mall_spring.entity.Review;
import com.coding24h.mall_spring.entity.vo.RatingSummaryVO;
import com.coding24h.mall_spring.entity.vo.ReviewVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 评价服务接口
 */
public interface ReviewService {

    PageInfo<ReviewVO> getAllReviews(ReviewQueryDTO queryDTO);
    /**
     * 分页查询商品评价
     */
    PageInfo<ReviewVO> getProductReviews(ReviewQueryDTO queryDTO);

    /**
     * 获取商品评分统计
     */
    RatingSummaryVO getProductRatingSummary(Integer productId);

    /**
     * 提交评价
     */
    boolean submitReview(ReviewSubmitDTO reviewDTO, Long userId);

    /**
     * 删除评价
     */
    boolean deleteReview(Integer reviewId, Long userId);

    /**
     * 获取评价详情
     */
    ReviewVO getReviewDetail(Integer reviewId);

    /**
     * 获取用户评价列表
     */
    PageInfo<ReviewVO> getUserReviews(Long userId, Integer page, Integer size);

    /**
     * 获取待评价订单列表
     */
    List<Review> getPendingReviews(Long userId, Long productId);

    /**
     * 添加卖家回复
     */
    boolean addSellerReply(Integer reviewId, String sellerReply);

    /**
     * 检查用户是否已评价该订单项
     */
    boolean hasUserReviewed(Integer orderItemId);

    /**
     * 获取评价总数
     */
    Integer getReviewCount(Integer productId);

    /**
     * 获取平均评分
     */
    Double getAverageRating(Integer productId);
}
