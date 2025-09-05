package com.coding24h.mall_spring.entity.event;

import com.coding24h.mall_spring.entity.Order;
import org.springframework.context.ApplicationEvent;

/**
 * 订单创建成功后发布的事件。
 * 当一个新的订单被成功持久化到数据库后，应该发布此事件，
 * 以便其他模块（如通知服务）可以监听并做出相应处理（例如，通知卖家有新订单）。
 */
public class OrderCreatedEvent extends ApplicationEvent {

    private final Order order;

    /**
     * 创建一个新的订单创建事件.
     *
     * @param source the object on which the event initially occurred or with
     * which the event is associated (never {@code null})
     * @param order  新创建的订单对象
     */
    public OrderCreatedEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }

    /**
     * 获取事件关联的订单对象.
     *
     * @return Order 订单实体
     */
    public Order getOrder() {
        return order;
    }
}
