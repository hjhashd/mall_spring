package com.coding24h.mall_spring.mapper;

import com.coding24h.mall_spring.dto.order.OrderListDTO;
import com.coding24h.mall_spring.entity.Order;
import com.coding24h.mall_spring.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    // 插入订单
    int insertOrder(Order order);

    // 根据订单ID查询订单
    Order selectByOrderId(@Param("orderId") String orderId);

    // 根据用户ID查询订单列表
    List<Order> selectByUserId(@Param("userId") Integer userId);

    // 根据卖家ID查询订单列表
    List<Order> selectBySellerId(@Param("sellerId") Integer sellerId);

    // 更新订单状态方法，添加cancelledAt参数
    int updateOrderStatus(@Param("orderId") String orderId,
                          @Param("status") Integer status,
                          @Param("completedAt") LocalDateTime completedAt,
                          @Param("cancelledAt") LocalDateTime cancelledAt);

    // 更新物流信息
    int updateLogisticsInfo(@Param("orderId") String orderId,
                            @Param("trackingNumber") String trackingNumber,
                            @Param("logisticsStatus") String logisticsStatus);

    // 软删除订单
    int softDeleteOrder(@Param("orderId") String orderId);

    @Select("SELECT COUNT(*) FROM orders " +
            "WHERE seller_id = #{sellerId} " +
            "AND status = 4 " + // 4=已完成
            "AND MONTH(created_at) = MONTH(CURRENT_DATE()) " +
            "AND YEAR(created_at) = YEAR(CURRENT_DATE())")
    int countMonthlyOrders(@Param("sellerId") Long sellerId);
    // 统计指定订单项的评价数量

    /**
     * 根据订单项ID查询订单ID
     * @param orderItemId 订单项ID
     * @return 订单ID
     */
    String findOrderIdByOrderItemId(@Param("orderItemId") Integer orderItemId);

    /**
     * 根据用户ID查询其所有订单及订单项
     * @param userId 用户ID
     * @return 包含订单项的订单列表
     */
    List<OrderListDTO> findOrdersByUserId(@Param("userId") Long userId);

    /**
     * 根据卖家ID查询订单列表，并包含订单项
     * @param sellerId 卖家ID
     * @return 订单列表
     */
    List<OrderListDTO> findOrdersBySellerId(@Param("sellerId") Integer sellerId);

    /**
     * 更新订单为发货状态
     * @param orderId 订单ID
     * @param sellerId 卖家ID (用于权限校验)
     * @param shippingCompany 物流公司
     * @param trackingNumber 物流单号
     * @param shippedAt 发货时间
     * @return 更新的行数
     */
    int updateOrderForShipment(@Param("orderId") String orderId,
                               @Param("sellerId") Integer sellerId,
                               @Param("shippingCompany") String shippingCompany,
                               @Param("trackingNumber") String trackingNumber,
                               @Param("shippedAt") LocalDateTime shippedAt);
}
