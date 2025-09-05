package com.coding24h.mall_spring.entity.event;

import com.coding24h.mall_spring.entity.AfterSales;
import com.coding24h.mall_spring.entity.OrderItem;
import org.springframework.context.ApplicationEvent;

public class AfterSaleApplicationEvent extends ApplicationEvent {
    private final AfterSales afterSale;
    private final OrderItem orderItem;

    public AfterSaleApplicationEvent(Object source, AfterSales afterSale, OrderItem orderItem) {
        super(source);
        this.afterSale = afterSale;
        this.orderItem = orderItem;
    }

    public AfterSales getAfterSale() {
        return afterSale;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }
}
