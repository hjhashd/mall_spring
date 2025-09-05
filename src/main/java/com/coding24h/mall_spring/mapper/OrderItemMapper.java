package com.coding24h.mall_spring.mapper;

import com.coding24h.mall_spring.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    /**
     * 根据订单ID查询订单项列表
     */
    List<OrderItem> selectByOrderId(@Param("orderId") String orderId);

    /**
     * 更新订单项的评价状态
     * @param itemId 订单项ID
     * @param isReviewed 是否已评价 (true/false)
     * @return 受影响的行数
     */
    int updateReviewedStatus(@Param("itemId") Integer itemId, @Param("isReviewed") boolean isReviewed);
    /**
     * 批量插入订单项
     */
    int insertOrderItem(OrderItem orderItem);

    /**
     * 根据订单项ID查询订单项
     */
    OrderItem selectById(@Param("itemId") Integer itemId);

    /**
     * 根据订单ID删除订单项（逻辑删除或物理删除）
     */
    int deleteByOrderId(@Param("orderId") String orderId);

    /**
     * 根据订单ID列表批量查询订单项
     */
    List<OrderItem> selectByOrderIds(@Param("orderIds") List<String> orderIds);

    /**
     * 更新订单项状态
     */
    int updateItemStatus(@Param("itemId") Integer itemId, @Param("itemStatus") Integer itemStatus);

    /**
     * 根据商品ID查询相关订单项
     */
    List<OrderItem> selectByProductId(@Param("productId") Integer productId);

    /**
     * 查询用户的所有订单项
     */
    List<OrderItem> selectByUserId(@Param("userId") Integer userId);
}
