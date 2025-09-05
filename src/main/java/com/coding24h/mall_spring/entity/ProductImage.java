package com.coding24h.mall_spring.entity;

import java.time.LocalDateTime;
import java.util.Date;

public class ProductImage {
    private Integer imageId;
    private Integer productId;
    private String imageUrl;
    private Integer sortOrder;
    private Boolean isMain;
    private LocalDateTime createdAt;
    private Integer verificationStatus; // 0-未审核, 1-通过, 2-拒绝

    public ProductImage(Integer productId, String imageUrl, Boolean isMain) {
        this.productId = productId;
        this.imageUrl = imageUrl;
        this.isMain = isMain;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getMain() {
        return isMain;
    }

    public void setMain(Boolean main) {
        isMain = main;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(Integer verificationStatus) {
        this.verificationStatus = verificationStatus;
    }
}
