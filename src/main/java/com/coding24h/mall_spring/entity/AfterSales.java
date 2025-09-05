package com.coding24h.mall_spring.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AfterSales {

    private Integer afterSaleId;        // 售后记录ID
    private Integer orderItemId;        // 关联订单项ID
    private Integer type;               // 售后类型：1-退货, 2-换货, 3-仅退款
    private String reason;              // 申请原因
    private Integer status;             // 处理状态：0-申请中, 1-已同意, 2-已拒绝, 3-处理中, 4-已完成
    private LocalDateTime createdAt;    // 申请时间
    private LocalDateTime processedAt;  // 处理时间
    private LocalDateTime completedAt;  // 完成时间
    private BigDecimal refundAmount;    // 退款金额
    private String evidenceImages;      // 凭证图片URL，多个用逗号分隔
    private String trackingNumber;      // 退货物流单号
    private String adminRemark;         // 平台处理备注
    private String sellerRemark;

    public String getSellerRemark() {
        return sellerRemark;
    }

    public void setSellerRemark(String sellerRemark) {
        this.sellerRemark = sellerRemark;
    }

    public Integer getAfterSaleId() {
        return afterSaleId;
    }

    public void setAfterSaleId(Integer afterSaleId) {
        this.afterSaleId = afterSaleId;
    }

    public Integer getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Integer orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getEvidenceImages() {
        return evidenceImages;
    }

    public void setEvidenceImages(String evidenceImages) {
        this.evidenceImages = evidenceImages;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getAdminRemark() {
        return adminRemark;
    }

    public void setAdminRemark(String adminRemark) {
        this.adminRemark = adminRemark;
    }
}
