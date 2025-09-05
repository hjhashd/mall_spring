package com.coding24h.mall_spring.entity;

import java.math.BigDecimal;

import java.time.LocalDateTime;

public class OrderItem {
    private Integer itemId;            // 订单项ID
    private String orderId;            // 关联订单ID
    private Integer productId;         // 商品ID
    private String productName;        // 商品名称（下单时快照）
    private String productImage;       // 商品图片（下单时快照）
    private Integer quantity;          // 购买数量
    private BigDecimal unitPrice;      // 成交单价
    private BigDecimal totalPrice;     // 小计金额
    private Integer condition;         // 商品成色
    private Integer itemStatus;        // 订单项状态
    private LocalDateTime createdAt;   // 创建时间
    // 关联数据
    private Product product;           // 商品信息
    private boolean isReviewed;
    private Integer afterSaleId;

    public Integer getAfterSaleId() {
        return afterSaleId;
    }

    public void setAfterSaleId(Integer afterSaleId) {
        this.afterSaleId = afterSaleId;
    }

    public boolean getIsReviewed() {
        return isReviewed;
    }

    public void setIsReviewed(boolean reviewed) {
        isReviewed = reviewed;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getCondition() {
        return condition;
    }

    public void setCondition(Integer condition) {
        this.condition = condition;
    }

    public Integer getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(Integer itemStatus) {
        this.itemStatus = itemStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
