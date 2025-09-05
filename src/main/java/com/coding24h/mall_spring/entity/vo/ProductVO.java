package com.coding24h.mall_spring.entity.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

public class ProductVO {
    private Long id;
    private String title;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer discountRate;
    private Integer condition;
    private String conditionText;
    private Integer stock;
    private String location;
    private String createdAt;
    private String image;
    private Boolean isFavorite;
    private Long categoryId;

    // 新增字段：状态码和状态文本
    private Integer status;
    private String statusText;

    // 新增字段：分类名称（前端需要）
    private String categoryName;

    // 构造函数
    public ProductVO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Integer getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(Integer discountRate) {
        this.discountRate = discountRate;
    }

    public Integer getCondition() {
        return condition;
    }

    public void setCondition(Integer condition) {
        this.condition = condition;
    }

    public void setConditionText(String conditionText) {
        this.conditionText = conditionText;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getFavorite() {
        return isFavorite;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    // 状态码映射为文本
    private String mapStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 1: return "上架中";
            case 2: return "已下架";
            case 3: return "已售罄";
            case 4: return "审核中";
            default: return "未知状态";
        }
    }

    // 成色映射为文本（可选）
    public String getConditionText() {
        if (condition == null) return "未知";
        switch (condition) {
            case 1: return "全新";
            case 2: return "95新";
            case 3: return "9成新";
            case 4: return "8成新";
            case 5: return "7成新及以下";
            default: return "未知成色";
        }
    }
}
