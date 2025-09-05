package com.coding24h.mall_spring.dto;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public class ProductSubmitDTO {

    private String title;
    private String description;
    private Integer categoryId;
    private BigDecimal price;
    private BigDecimal originalPrice; // 可选
    private String condition;
    private Integer stock;
    private String location;
    private String subCategory; // 可选

    // 自定义属性（JSON 字符串）
    private String customAttributes;

    // 图片文件列表（多个）
    private List<MultipartFile> images;

    // 添加新字段：主图索引
    private Integer mainImageIndex = 0; // 默认第一张为主图

    // Getter 和 Setter
    public Integer getMainImageIndex() {
        return mainImageIndex;
    }

    public void setMainImageIndex(Integer mainImageIndex) {
        this.mainImageIndex = mainImageIndex != null ? mainImageIndex : 0;
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

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
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

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
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

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getCustomAttributes() {
        return customAttributes;
    }

    public void setCustomAttributes(String customAttributes) {
        this.customAttributes = customAttributes;
    }

    public List<MultipartFile> getImages() {
        return images;
    }

    public void setImages(List<MultipartFile> images) {
        this.images = images;
    }
}
