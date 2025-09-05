package com.coding24h.mall_spring.entity.event;

import com.coding24h.mall_spring.entity.Order;
import org.springframework.context.ApplicationEvent;

/**
 * 订单已发货事件
 * 当卖家成功发货后，发布此事件。
 */
public class OrderShippedEvent extends ApplicationEvent {

    private final Order order;

    /**
     * @param source 事件源，通常是 'this'
     * @param order 已发货的订单对象
     */
    public OrderShippedEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
}
