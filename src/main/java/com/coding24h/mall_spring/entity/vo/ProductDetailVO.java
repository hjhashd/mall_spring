package com.coding24h.mall_spring.entity.vo;

import com.coding24h.mall_spring.entity.Category;
import com.coding24h.mall_spring.entity.ProductImage;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ProductDetailVO {
    private Integer productId;
    private String title;
    private String description;
    private BigDecimal currentPrice;
    private BigDecimal originalPrice;
    private String discount; // 折扣信息，可以后端计算或前端计算
    private Integer stock;
    private Integer viewCount;
    private Integer favoriteCount;
    private Double rating; // 评分，需要从评论表计算
    private Integer reviewCount; // 评论数
    private Category category; // 商品分类
    private List<ProductImage> images; // 商品图片列表
    private Map<String, Object> specs; // 直接接收 JSON 数据
    private boolean isFavoritedByCurrentUser = false; // 用户是否已收藏
    private String condition; // 商品成色
    private String location;  // 商品位置
    private String conditionText;
    private Long sellerId;


    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getConditionText() {
        return conditionText;
    }

    public void setConditionText(String conditionText) {
        this.conditionText = conditionText;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
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

    public boolean isFavoritedByCurrentUser() {
        return isFavoritedByCurrentUser;
    }

    public void setFavoritedByCurrentUser(boolean favoritedByCurrentUser) {
        isFavoritedByCurrentUser = favoritedByCurrentUser;
    }
}
