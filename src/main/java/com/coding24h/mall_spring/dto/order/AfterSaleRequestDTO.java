package com.coding24h.mall_spring.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AfterSaleRequestDTO {

    // 对应数据库中的 order_item_id
    // 使用 @JsonProperty 来匹配前端传来下划线风格的字段名
    @JsonProperty("order_item_id")
    private Integer orderItemId;

    // 售后类型：1-退货, 2-换货, 3-仅退款
    private Integer type;

    // 申请原因
    private String reason;

    // 退款金额
    @JsonProperty("refund_amount")
    private Double refundAmount;

    // 凭证图片URL，多个用逗号分隔
    @JsonProperty("evidence_images")
    private String evidenceImages;

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

    public Double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getEvidenceImages() {
        return evidenceImages;
    }

    public void setEvidenceImages(String evidenceImages) {
        this.evidenceImages = evidenceImages;
    }
}
