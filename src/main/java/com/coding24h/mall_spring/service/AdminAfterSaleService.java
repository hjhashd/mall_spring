package com.coding24h.mall_spring.service;

import com.coding24h.mall_spring.dto.order.AfterSaleApplicationDTO;
import com.coding24h.mall_spring.entity.AfterSales;
import com.coding24h.mall_spring.entity.Order;
import com.coding24h.mall_spring.entity.OrderItem;
import com.coding24h.mall_spring.entity.Payment; // 1. 导入 Payment 实体
import com.coding24h.mall_spring.entity.Product;
import com.coding24h.mall_spring.entity.User;
import com.coding24h.mall_spring.entity.event.AdminNotificationEvent;
import com.coding24h.mall_spring.exception.BusinessException;
import com.coding24h.mall_spring.mapper.AfterSaleMapper;
import com.coding24h.mall_spring.mapper.OrderItemMapper;
import com.coding24h.mall_spring.mapper.OrderMapper;
import com.coding24h.mall_spring.mapper.PaymentMapper; // 2. 导入 PaymentMapper
import com.coding24h.mall_spring.mapper.ProductMapper;
import com.coding24h.mall_spring.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminAfterSaleService {

    @Autowired
    private AfterSaleMapper afterSaleMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private PaymentMapper paymentMapper; // 3. 注入 PaymentMapper

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    // 定义售后状态常量
    private static final int STATUS_PENDING_ADMIN = 5; // 待平台处理
    private static final int STATUS_ADMIN_APPROVED = 6; // 管理员同意
    private static final int STATUS_ADMIN_REJECTED = 7; // 管理员拒绝
    private static final int ITEM_STATUS_REFUNDED = 3; // 已退款
    private static final int ITEM_STATUS_NORMAL = 1; // 正常
    private static final int ORDER_STATUS_REFUNDED = 7; // 已退款

    /**
     * 获取所有待管理员处理的售后申请
     * @return 待处理的售后申请列表
     */
    public List<AfterSaleApplicationDTO> getPendingApplications() {
        return afterSaleMapper.findPendingAdminApplications();
    }

    /**
     * 获取所有售后申请
     * @return 所有售后申请列表
     */
    public List<AfterSaleApplicationDTO> getAllApplications() {
        return afterSaleMapper.findAllAdminApplications();
    }

    /**
     * 根据状态获取售后申请
     * @param status 状态码
     * @return 指定状态的售后申请列表
     */
    public List<AfterSaleApplicationDTO> getApplicationsByStatus(Integer status) {
        return afterSaleMapper.findApplicationsByStatus(status);
    }

    /**
     * 管理员处理售后申请
     * @param afterSaleId 售后申请ID
     * @param isApproved 是否同意
     * @param adminRemark 管理员备注
     */
    @Transactional
    public void judgeApplication(Integer afterSaleId, boolean isApproved, String adminRemark) {
        AfterSales afterSale = afterSaleMapper.selectById(afterSaleId);
        if (afterSale == null) {
            throw new BusinessException("售后申请不存在");
        }

        if (afterSale.getStatus() != STATUS_PENDING_ADMIN) {
            throw new BusinessException("只有待平台处理的申请才能被管理员处理");
        }

        int newStatus = isApproved ? STATUS_ADMIN_APPROVED : STATUS_ADMIN_REJECTED;
        afterSale.setStatus(newStatus);
        afterSale.setAdminRemark(adminRemark);
        afterSale.setProcessedAt(LocalDateTime.now());
        if(isApproved){
            afterSale.setCompletedAt(LocalDateTime.now());
        }

        afterSaleMapper.updateStatus(afterSale);

        OrderItem orderItem = orderItemMapper.selectById(afterSale.getOrderItemId());
        if (orderItem == null) {
            throw new BusinessException("订单项不存在");
        }

        Order order = orderMapper.selectByOrderId(orderItem.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        Product product = productMapper.selectById(orderItem.getProductId());
        if (product == null) {
            throw new BusinessException("商品信息不存在");
        }

        if (isApproved) {
            processApprovedApplication(afterSale, orderItem, order);
        } else {
            processRejectedApplication(orderItem);
        }

        Integer buyerId = order.getUserId();
        Integer sellerId = product.getSellerId();

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("afterSaleId", afterSale.getAfterSaleId());
        notificationData.put("result", isApproved ? "平台同意退款" : "平台拒绝退款");
        notificationData.put("reason", afterSale.getReason());
        notificationData.put("adminRemark", adminRemark);
        notificationData.put("orderItemId", orderItem.getItemId());
        notificationData.put("productName", product.getTitle());
        notificationData.put("refundAmount", afterSale.getRefundAmount());

        eventPublisher.publishEvent(new AdminNotificationEvent(
                this,
                Arrays.asList(buyerId, sellerId),
                "ADMIN_JUDGMENT",
                notificationData
        ));
    }

    /**
     * 处理同意的售后申请
     */
    private void processApprovedApplication(AfterSales afterSale, OrderItem orderItem, Order order) {
        if (orderItem.getItemStatus() != ITEM_STATUS_REFUNDED) {
            orderItemMapper.updateItemStatus(orderItem.getItemId(), ITEM_STATUS_REFUNDED);
            orderMapper.updateOrderStatus(order.getOrderId(), ORDER_STATUS_REFUNDED, LocalDateTime.now(), null);

            User buyer = userMapper.selectById(order.getUserId());
            BigDecimal refundAmount = afterSale.getRefundAmount();
            if (buyer != null && refundAmount != null) {
                userMapper.updateBalance(buyer.getUserId().intValue(), buyer.getBalance().add(refundAmount));
            }

            // 4. 新增逻辑：插入退款记录到 payments 表
            Payment refundPayment = new Payment();
            refundPayment.setOrderId(order.getOrderId());
            refundPayment.setAmount(refundAmount.negate()); // 金额存为负数
            refundPayment.setPaymentMethod("REFUND_ADMIN"); // 标识为管理员操作
            refundPayment.setPaymentStatus(3);
            refundPayment.setTransactionId("REFUND_ADMIN_" + System.currentTimeMillis());
            refundPayment.setCreatedAt(LocalDateTime.now());
            refundPayment.setPaidAt(LocalDateTime.now()); // 退款完成时间
            refundPayment.setRefundAmount(refundAmount);
            refundPayment.setRefundedAt(LocalDateTime.now());

            paymentMapper.insertPayment(refundPayment);
        }
    }

    /**
     * 处理拒绝的售后申请
     */
    private void processRejectedApplication(OrderItem orderItem) {
        if (orderItem.getItemStatus() != ITEM_STATUS_NORMAL) {
            orderItemMapper.updateItemStatus(orderItem.getItemId(), ITEM_STATUS_NORMAL);
        }
    }
}
