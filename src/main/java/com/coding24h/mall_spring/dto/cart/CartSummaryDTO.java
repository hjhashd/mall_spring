package com.coding24h.mall_spring.dto.cart;

import java.math.BigDecimal;

public class CartSummaryDTO {
    private Integer totalQuantity;
    private BigDecimal totalPrice;
    private Integer itemCount;

    // 无参构造函数
    public CartSummaryDTO() {
    }

    // 有参构造函数
    public CartSummaryDTO(Integer totalQuantity, BigDecimal totalPrice, Integer itemCount) {
        this.totalQuantity = totalQuantity;
        this.totalPrice = totalPrice;
        this.itemCount = itemCount;
    }

    // Getter 方法
    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    // Setter 方法
    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }
}