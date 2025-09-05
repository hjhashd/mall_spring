package com.coding24h.mall_spring.entity.event;

import com.coding24h.mall_spring.entity.AfterSales; // 你的售后实体类
import org.springframework.context.ApplicationEvent;

public class AfterSaleProcessedEvent extends ApplicationEvent {

    private final AfterSales afterSale;
    private final String productName; // 额外传递商品名，方便生成消息

    public AfterSaleProcessedEvent(Object source, AfterSales afterSale, String productName) {
        super(source);
        this.afterSale = afterSale;
        this.productName = productName;
    }

    public AfterSales getAfterSale() {
        return afterSale;
    }

    public String getProductName() {
        return productName;
    }
}
