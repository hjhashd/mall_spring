package com.coding24h.mall_spring.mapper;

import com.coding24h.mall_spring.entity.Seller;
import com.coding24h.mall_spring.entity.vo.SellerShopVO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SellerMapper {

    /**
     * 插入基础卖家信息（认证时使用）
     */
    @Insert({
            "INSERT INTO sellers (",
            "seller_id, shop_name, description, location, contact_phone, contact_email, ",
            "is_verified, open_date, created_at, updated_at",
            ") VALUES (",
            "#{sellerId}, #{shopName}, #{description}, #{location}, #{contactPhone}, #{contactEmail}, ",
            "#{isVerified}, NOW(), NOW(), NOW()",
            ")"
    })
    int insertBasicInfo(Seller seller);

    /**
     * 根据卖家ID查询
     */
    @Select("SELECT * FROM sellers WHERE seller_id = #{sellerId}")
    Seller selectBySellerId(Integer sellerId);

    /**
     * 检查卖家是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM sellers WHERE seller_id = #{sellerId}")
    boolean existsBySellerId(Integer sellerId);


    /**
     * 根据卖家ID获取在售商品数量
     * 商品状态: 1-上架
     * @param sellerId 卖家ID
     * @return 在售商品数量
     */
    @Select("SELECT COUNT(*) FROM products WHERE seller_id = #{sellerId} AND status = 1")
    long getOnSaleProductCount(Long sellerId);

    /**
     * 根据卖家ID获取店铺基础统计信息
     * @param sellerId 卖家ID
     * @return 店铺统计视图对象
     */
    @Select("SELECT " +
            "    total_sales as totalSales, " +
            "    total_reviews as totalReviews, " +
            "    average_rating as averageRating, " +
            "    avg_response_time as avgResponseTime, " +
            "    logistics_policy as logisticsPolicy, " +
            "    after_sale_policy as afterSalePolicy, " +
            "    guarantee_policy as guaranteePolicy " +
            "FROM sellers WHERE seller_id = #{sellerId}")
    SellerShopVO getSellerStats(Long sellerId);

    /**
     * 根据卖家ID获取待处理订单数量
     * 订单状态：假设 1-待付款, 2-待发货
     * @param sellerId 卖家ID
     * @return 待处理订单数量
     */
    @Select("SELECT COUNT(DISTINCT o.order_id) " +
            "FROM orders o " +
            "JOIN order_items oi ON o.order_id = oi.order_id " +
            "JOIN products p ON oi.product_id = p.product_id " +
            "WHERE p.seller_id = #{sellerId} AND o.status IN (1, 2)")
    long getPendingOrderCount(Long sellerId);


    @Update("<script>" +
            "UPDATE sellers " +
            "<set>" +
            "  <if test='shopName != null'>shop_name = #{shopName},</if>" +
            "  <if test='logoUrl != null'>logo_url = #{logoUrl},</if>" +
            "  <if test='bannerUrl != null'>banner_url = #{bannerUrl},</if>" +
            "  <if test='description != null'>description = #{description},</if>" +
            "  <if test='location != null'>location = #{location},</if>" +
            "  <if test='contactPhone != null'>contact_phone = #{contactPhone},</if>" +
            "  <if test='contactEmail != null'>contact_email = #{contactEmail},</if>" +
            "  <if test='logisticsPolicy != null'>logistics_policy = #{logisticsPolicy},</if>" +
            "  <if test='afterSalePolicy != null'>after_sale_policy = #{afterSalePolicy},</if>" +
            "  <if test='guaranteePolicy != null'>guarantee_policy = #{guaranteePolicy},</if>" +
            "  <if test='announcement != null'>announcement = #{announcement},</if>" +
            "  <if test='businessHours != null'>business_hours = #{businessHours, typeHandler=com.coding24h.mall_spring.util.JsonMapTypeHandler},</if>" +
            "  updated_at = NOW()" +
            "</set>" +
            "WHERE seller_id = #{sellerId}" +
            "</script>")
    int updateShopSettings(Seller seller);

    // ================== 新增方法 ==================

    /**
     * 增加卖家的总销量
     * @param sellerId 卖家ID
     * @param quantity 增加的商品总件数
     * @return 受影响的行数
     */
    @Update("UPDATE sellers SET total_sales = total_sales + #{quantity} WHERE seller_id = #{sellerId}")
    int increaseTotalSales(@Param("sellerId") Integer sellerId, @Param("quantity") int quantity);

    /**
     * 增加卖家的总评价数
     * @param sellerId 卖家ID
     * @param count 增加的数量 (通常为1)
     * @return 受影响的行数
     */
    @Update("UPDATE sellers SET total_reviews = total_reviews + #{count} WHERE seller_id = #{sellerId}")
    int increaseTotalReviews(@Param("sellerId") Integer sellerId, @Param("count") int count);
}

