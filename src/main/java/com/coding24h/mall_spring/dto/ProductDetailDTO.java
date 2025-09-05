
package com.coding24h.mall_spring.dto;

import com.coding24h.mall_spring.entity.ProductImage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ProductDetailDTO {
    private Integer productId;
    private String title;
    private String description;
    private BigDecimal currentPrice;
    private BigDecimal originalPrice;
    private String discount;
    private List<ProductImage> images;
    private Map<String, Object> specs;
    private Boolean isFavoritedByCurrentUser;
    private String categoryPath;

    // 移除了 rating 和 reviewCount 字段
    // 新增字段（根据实际表结构）
    private Integer condition;
    private String conditionText;
    private Integer stock;
    private String location;
    private String createdAt;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    public Map<String, Object> getSpecs() {
        return specs;
    }

    public void setSpecs(Map<String, Object> specs) {
        this.specs = specs;
    }

    public Boolean getFavoritedByCurrentUser() {
        return isFavoritedByCurrentUser;
    }

    public void setFavoritedByCurrentUser(Boolean favoritedByCurrentUser) {
        isFavoritedByCurrentUser = favoritedByCurrentUser;
    }

    public String getCategoryPath() {
        return categoryPath;
    }

    public void setCategoryPath(String categoryPath) {
        this.categoryPath = categoryPath;
    }

    public Integer getCondition() {
        return condition;
    }

    public void setCondition(Integer condition) {
        this.condition = condition;
    }

    public String getConditionText() {
        return conditionText;
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
}
