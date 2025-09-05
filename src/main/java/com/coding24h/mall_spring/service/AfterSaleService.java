package com.coding24h.mall_spring.service;

import com.coding24h.mall_spring.dto.order.AfterSaleRequestDTO;
import com.coding24h.mall_spring.entity.AfterSales;
import com.coding24h.mall_spring.entity.Order;
import com.coding24h.mall_spring.entity.OrderItem;
import com.coding24h.mall_spring.entity.event.AfterSaleApplicationEvent;
import com.coding24h.mall_spring.exception.BusinessException;
import com.coding24h.mall_spring.mapper.AfterSaleMapper;
import com.coding24h.mall_spring.mapper.OrderItemMapper;
import com.coding24h.mall_spring.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AfterSaleService {

    @Autowired
    private AfterSaleMapper afterSaleMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    /**
     * 创建售后申请的核心方法
     * @param userId 用户ID
     * @param request 售后申请的数据传输对象
     */
    @Transactional
    public void createApplication(Integer userId, AfterSaleRequestDTO request) {
        // 1. 数据校验
        if (request.getOrderItemId() == null) {
            throw new BusinessException("订单项ID不能为空");
        }

        // 2. 获取订单项信息
        OrderItem orderItem = orderItemMapper.selectById(request.getOrderItemId());
        if (orderItem == null) {
            throw new BusinessException("申请售后的商品不存在");
        }

        // 3. 验证订单归属权
        Order order = orderMapper.selectByOrderId(orderItem.getOrderId());
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        // 4. 检查订单项状态，防止重复申请
        if (orderItem.getItemStatus() != 1) { // 1-正常
            throw new BusinessException("该商品已在售后中或已退款，请勿重复申请");
        }

        // 5. 校验退款金额
        if (request.getRefundAmount() != null) {
            BigDecimal requestAmount = BigDecimal.valueOf(request.getRefundAmount());
            if (requestAmount.compareTo(orderItem.getTotalPrice()) > 0) {
                throw new BusinessException("退款金额不能超过商品总价");
            }
        }

        // 6. 创建 AfterSale 实体并填充数据
        AfterSales afterSale = new AfterSales();
        afterSale.setOrderItemId(request.getOrderItemId());
        afterSale.setType(request.getType());
        afterSale.setReason(request.getReason());
        afterSale.setRefundAmount(request.getRefundAmount() != null ? BigDecimal.valueOf(request.getRefundAmount()) : null);
        afterSale.setEvidenceImages(request.getEvidenceImages());
        afterSale.setStatus(0); // 0-申请中
        afterSale.setCreatedAt(LocalDateTime.now());

        // 7. 插入售后记录到数据库
        afterSaleMapper.insert(afterSale);

        // 8. 更新订单项的状态为 "售后中"
        orderItemMapper.updateItemStatus(orderItem.getItemId(), 2); // 2-售后中

        eventPublisher.publishEvent(new AfterSaleApplicationEvent(this, afterSale, orderItem));
    }

    @Transactional
    public void appealToPlatform(Integer userId, Integer afterSaleId) {
        // 1. 获取售后记录
        AfterSales afterSale = afterSaleMapper.selectById(afterSaleId);
        if (afterSale == null) {
            throw new BusinessException("售后申请不存在");
        }

        // 2. 验证订单所有权
        String orderId = orderMapper.findOrderIdByOrderItemId(afterSale.getOrderItemId());
        Order order = orderMapper.selectByOrderId(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该售后申请");
        }

        // 3. 检查售后状态是否为“卖家已拒绝”
        if (afterSale.getStatus() != 2) { // 2-卖家拒绝
            throw new BusinessException("当前状态无法申诉");
        }

        // 4. 更新售后表状态为“待管理员处理”
        afterSale.setStatus(5); // 5-待管理员处理
        afterSaleMapper.updateStatus(afterSale);
        orderItemMapper.updateItemStatus(afterSale.getOrderItemId(), 5); // 5-待管理员处理
    }
}
