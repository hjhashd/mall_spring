package com.coding24h.mall_spring.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderListDTO {
    private String orderId;                    // 订单号
    private BigDecimal totalAmount;            // 订单总金额
    private Integer status;                    // 订单状态
    private String paymentMethod;              // 支付方式
    private LocalDateTime createdAt;           // 订单创建时间
    private String userRemark;                 // 买家备注
    private String shippingCompany;            // 物流公司名称
    private List<OrderItemDTO> items;          // 订单商品项

    // === 新增字段 ===
    private Integer buyerId;
    private String sellerId;
    private String buyerAvatarUrl;

    // === 缺失的字段 ===
    private String receiverName;               // 收货人姓名
    private String receiverPhone;              // 收货人电话
    private String shippingAddress;            // 收货地址
    private String trackingNumber;             // 快递单号
    private Integer afterSaleId;
    private String buyerUsername;// 售后状态
    private String sellerUsername;

    public String getBuyerUsername() {
        return buyerUsername;
    }

    public void setBuyerUsername(String buyerUsername) {
        this.buyerUsername = buyerUsername;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public void setSellerUsername(String sellerUsername) {
        this.sellerUsername = sellerUsername;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getAfterSaleId() {
        return afterSaleId;
    }

    public void setAfterSaleId(Integer afterSaleId) {
        this.afterSaleId = afterSaleId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserRemark() {
        return userRemark;
    }

    public void setUserRemark(String userRemark) {
        this.userRemark = userRemark;
    }

    public String getShippingCompany() {
        return shippingCompany;
    }

    public void setShippingCompany(String shippingCompany) {
        this.shippingCompany = shippingCompany;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerAvatarUrl() {
        return buyerAvatarUrl;
    }

    public void setBuyerAvatarUrl(String buyerAvatarUrl) {
        this.buyerAvatarUrl = buyerAvatarUrl;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
}
