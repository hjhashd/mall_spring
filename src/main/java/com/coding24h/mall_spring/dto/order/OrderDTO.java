package com.coding24h.mall_spring.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {
    private String orderId;              // 订单号
    private Integer userId;              // 买家用户ID
    private Integer sellerId;            // 卖家用户ID
    private BigDecimal totalAmount;      // 订单总金额
    private Integer status;              // 订单状态：1-待付款, 2-待发货, 3-待收货, 4-已完成, 5-已取消, 6-退款中, 7-已退款
    private Integer shippingAddressId;   // 收货地址ID
    private String paymentMethod;        // 支付方式：wechat, alipay, balance
    private String transactionId;        // 第三方支付平台交易号

    // 收货信息
    private String receiverName;         // 收货人姓名
    private String receiverPhone;        // 收货人电话
    private String shippingAddress;      // 收货地址详情
    private String shippingCompany;      // 物流公司名称
    private String trackingNumber;       // 物流单号

    // 时间字段
    private LocalDateTime createdAt;     // 订单创建时间
    private LocalDateTime paidAt;        // 支付时间
    private LocalDateTime shippedAt;     // 发货时间
    private LocalDateTime receivedAt;    // 收货时间
    private LocalDateTime completedAt;   // 完成时间
    private LocalDateTime cancelledAt;   // 取消时间

    // 其他字段
    private String userRemark;           // 买家备注
    private String logisticsStatus;      // 物流状态
    private Integer isDeleted;           // 软删除标记：0-正常, 1-删除

    // 订单项列表
    private List<OrderItemDTO> items;    // 订单商品项

    // getter 和 setter 方法
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
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

    public Integer getShippingAddressId() {
        return shippingAddressId;
    }

    public void setShippingAddressId(Integer shippingAddressId) {
        this.shippingAddressId = shippingAddressId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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

    public String getShippingCompany() {
        return shippingCompany;
    }

    public void setShippingCompany(String shippingCompany) {
        this.shippingCompany = shippingCompany;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public LocalDateTime getShippedAt() {
        return shippedAt;
    }

    public void setShippedAt(LocalDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getUserRemark() {
        return userRemark;
    }

    public void setUserRemark(String userRemark) {
        this.userRemark = userRemark;
    }

    public String getLogisticsStatus() {
        return logisticsStatus;
    }

    public void setLogisticsStatus(String logisticsStatus) {
        this.logisticsStatus = logisticsStatus;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "orderId='" + orderId + '\'' +
                ", userId=" + userId +
                ", sellerId=" + sellerId +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", shippingAddressId=" + shippingAddressId +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", receiverPhone='" + receiverPhone + '\'' +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", shippingCompany='" + shippingCompany + '\'' +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", createdAt=" + createdAt +
                ", paidAt=" + paidAt +
                ", shippedAt=" + shippedAt +
                ", receivedAt=" + receivedAt +
                ", completedAt=" + completedAt +
                ", cancelledAt=" + cancelledAt +
                ", userRemark='" + userRemark + '\'' +
                ", logisticsStatus='" + logisticsStatus + '\'' +
                ", isDeleted=" + isDeleted +
                ", items=" + items +
                '}';
    }
}