package com.coding24h.mall_spring.mapper.review;

import com.coding24h.mall_spring.entity.Reply;
import com.coding24h.mall_spring.entity.Review;
import com.coding24h.mall_spring.entity.vo.RatingDistributionVO;
import com.coding24h.mall_spring.entity.vo.RatingSummaryVO;
import com.coding24h.mall_spring.entity.vo.SellerReviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 评价Mapper接口
 */
@Mapper
public interface ReviewMapper {
    //管理员用的
    // 【关键修改】增加 sellerId 参数
    List<Review> selectAllReviews(@Param("rating") Integer rating,
                                  @Param("hasImage") Boolean hasImage,
                                  @Param("hasSellerReply") Boolean hasSellerReply,
                                  @Param("sort") String sort,
                                  @Param("order") String order,
                                  @Param("sellerId") Long sellerId);

    /**
     * 分页查询商品评价
     */
    List<Review> selectReviewsByProductId(
            @Param("productId") Integer productId,
            @Param("rating") Integer rating,
            @Param("hasImage") Boolean hasImage,
            @Param("hasSellerReply") Boolean hasSellerReply,
            @Param("sort") String sort,
            @Param("order") String order
    );
    /**
     * 根据卖家ID查询其所有商品的评价列表（多表连接）
     * @param sellerId 卖家ID
     * @return 评价VO列表
     */
    @Select({
            "<script>",
            "SELECT ",
            "    r.review_id as reviewId, r.rating, r.content, r.image_urls as imageUrls, ",
            "    r.created_at as createdAt, r.seller_reply as sellerReply, ",
            "    u.username as userName, u.avatar_path as userAvatar, ",
            "    p.product_id as productId, p.title as productName ",
            "FROM reviews r ",
            "JOIN users u ON r.user_id = u.user_id ",
            "JOIN order_items oi ON r.order_item_id = oi.item_id ",
            "JOIN products p ON oi.product_id = p.product_id ",
            "WHERE p.seller_id = #{sellerId} ",
            "ORDER BY r.created_at DESC",
            "</script>"
    })
    List<SellerReviewVO> findReviewsBySellerId(Long sellerId);


    int countByOrderItemId(@Param("orderItemId") Integer orderItemId);
    /**
     * 根据评价ID查询回复列表
     */
    List<Reply> selectRepliesByReviewId(@Param("reviewId") Integer reviewId);

    /**
     * 根据ID查询评价
     */
    Review selectReviewById(@Param("reviewId") Integer reviewId);

    /**
     * 获取商品评分统计
     */
    RatingSummaryVO selectRatingSummaryByProductId(@Param("productId") Integer productId);

    /**
     * 获取评分分布
     */
    List<RatingDistributionVO> selectRatingDistributionByProductId(@Param("productId") Integer productId);

    /**
     * 获取评价总数
     */
    Integer selectReviewCountByProductId(@Param("productId") Integer productId);

    /**
     * 获取平均评分
     */
    Double selectAverageRatingByProductId(@Param("productId") Integer productId);

    /**
     * 检查用户是否已评价该订单项
     */
    Review selectReviewByOrderItemId(@Param("orderItemId") Integer orderItemId);

    /**
     * 获取用户评价列表
     */
    List<Review> selectReviewsByUserId(@Param("userId") Integer userId);

    /**
     * 获取待评价订单列表
     */
    // 修改为 Long 类型，与服务层保持一致
    List<Review> selectPendingReviewsByUserId(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 更新评价的追评状态
     */
    int updateAppendStatus(@Param("reviewId") Integer reviewId, @Param("hasAppend") Boolean hasAppend);

    /**
     * 添加卖家回复
     */
    int updateSellerReply(@Param("reviewId") Integer reviewId, @Param("sellerReply") String sellerReply);

    /**
     * 插入评价
     */
    int insertReview(Review review);

    /**
     * 根据ID删除评价（逻辑删除）
     */
    int deleteReviewById(@Param("reviewId") Integer reviewId);

    /**
     * 获取用户评价总数
     */
    Integer selectReviewCountByUserId(@Param("userId") Integer userId);


    @Select("SELECT AVG(r.rating) FROM reviews r " +
            "JOIN order_items oi ON r.order_item_id = oi.item_id " +
            "JOIN products p ON oi.product_id = p.product_id " +
            "WHERE p.seller_id = #{sellerId}")
    Double getAverageRating(@Param("sellerId") Long sellerId);
}
