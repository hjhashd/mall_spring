package com.coding24h.mall_spring.service;

import com.coding24h.mall_spring.dto.order.AfterSaleApplicationDTO;
import com.coding24h.mall_spring.entity.*;
import com.coding24h.mall_spring.entity.event.AfterSaleProcessedEvent;
import com.coding24h.mall_spring.exception.BusinessException;
import com.coding24h.mall_spring.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SellerAfterSaleService {

    @Autowired
    private AfterSaleMapper afterSaleMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private PaymentMapper paymentMapper;

    public List<AfterSaleApplicationDTO> getAfterSaleApplicationsBySellerId(Integer sellerId) {
        return afterSaleMapper.findAfterSaleApplicationsBySellerId(sellerId);
    }

    @Transactional
    public void processApplication(Integer sellerId, Integer afterSaleId, boolean isApproved, String sellerRemark) {
        AfterSales afterSale = afterSaleMapper.selectById(afterSaleId);
        if (afterSale == null) {
            throw new BusinessException("售后申请不存在");
        }

        OrderItem orderItem = orderItemMapper.selectById(afterSale.getOrderItemId());
        Order order = orderMapper.selectByOrderId(orderItem.getOrderId());
        if (!order.getSellerId().equals(sellerId)) {
            throw new BusinessException("无权操作该售后申请");
        }

        if (afterSale.getStatus() != 0) { // 0-申请中
            throw new BusinessException("该申请已被处理，请勿重复操作");
        }

        int newStatus = isApproved ? 1 : 2; // 1-卖家同意, 2-卖家拒绝
        afterSale.setStatus(newStatus);
        afterSale.setSellerRemark(sellerRemark);
        afterSale.setProcessedAt(LocalDateTime.now());
        if(isApproved){
            afterSale.setCompletedAt(LocalDateTime.now());
        }
        afterSaleMapper.updateStatus(afterSale);

        if (isApproved) {
            // 更新订单和订单项状态
            orderItemMapper.updateItemStatus(orderItem.getItemId(), 3); // 3-已退款
            orderMapper.updateOrderStatus(order.getOrderId(), 7, LocalDateTime.now(), null); // 7-已退款

            // 更新用户余额
            User buyer = userMapper.selectById(order.getUserId());
            if (buyer != null && afterSale.getRefundAmount() != null) {
                userMapper.updateBalance(buyer.getUserId().intValue(), buyer.getBalance().add(afterSale.getRefundAmount()));
            }

            // 2. 新增逻辑：插入退款记录到 payments 表
            Payment refundPayment = new Payment();
            refundPayment.setOrderId(order.getOrderId());
            // 退款金额建议存为负数，以便于计算总收入
            refundPayment.setAmount(afterSale.getRefundAmount().negate());
            refundPayment.setPaymentMethod("REFUND"); // 或者其他能标识退款的方式
            refundPayment.setPaymentStatus(3); // 退款成功
            // 可以生成一个唯一的退款事务ID
            refundPayment.setTransactionId("REFUND_" + System.currentTimeMillis());
            refundPayment.setCreatedAt(LocalDateTime.now());
            refundPayment.setPaidAt(LocalDateTime.now()); // 此处可理解为退款完成时间
            refundPayment.setRefundAmount(afterSale.getRefundAmount()); // 记录正数的退款额
            refundPayment.setRefundedAt(LocalDateTime.now());

            paymentMapper.insertPayment(refundPayment); // 3. 执行插入操作

        } else {
            // 卖家拒绝
            orderItemMapper.updateItemStatus(orderItem.getItemId(), 4);
        }

        eventPublisher.publishEvent(new AfterSaleProcessedEvent(this, afterSale, orderItem.getProductName()));
    }
}
