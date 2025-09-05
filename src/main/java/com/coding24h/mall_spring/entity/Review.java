package com.coding24h.mall_spring.entity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品评价实体类
 */
public class Review {
    /**
     * 评价ID
     */
    private Integer reviewId;

    /**
     * 关联订单项ID
     */
    private Integer orderItemId;

    /**
     * 评价用户ID
     */
    private Integer userId;

    /**
     * 评分（1-5分）
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价图片URL，多个用逗号分隔
     */
    private String imageUrls;

    /**
     * 评价时间
     */
    private LocalDateTime createdAt;

    /**
     * 卖家回复内容
     */
    private String sellerReply;

    /**
     * 是否匿名：0-否, 1-是
     */
    private Boolean isAnonymous;

    /**
     * 是否存在用户追评：0-否, 1-是
     */
    private Boolean hasAppend;

    /**
     * 乐观锁版本号
     */
    private Integer version;

    /**
     * 逻辑删除标志：0-未删除, 1-已删除
     */
    private Boolean deleted;

    // 关联字段（非数据库字段）
    private String username;
    private String userAvatar;
    private String productName;
    private List<Reply> replies;
    private List<String> imageUrlList;
    private Integer productId;
    private String productImage;  // 新增属性

    // getter 和 setter 方法
    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    // Getters and Setters
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public Integer getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Integer orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getSellerReply() {
        return sellerReply;
    }

    public void setSellerReply(String sellerReply) {
        this.sellerReply = sellerReply;
    }

    public Boolean getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(Boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public Boolean getHasAppend() {
        return hasAppend;
    }

    public void setHasAppend(Boolean hasAppend) {
        this.hasAppend = hasAppend;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<Reply> getReplies() {
        return replies;
    }

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }

    // 修改 getImageUrlList() 方法，处理空字符串的情况
    public List<String> getImageUrlList() {
        if (imageUrls == null || imageUrls.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        // 过滤空字符串并去除空白
        return java.util.Arrays.stream(imageUrls.split(","))
                .map(String::trim)
                .filter(url -> !url.isEmpty())
                .collect(java.util.stream.Collectors.toList());
    }

    // 修改 setImageUrlList() 方法，处理 null 和空列表
    public void setImageUrlList(List<String> imageUrlList) {
        if (imageUrlList == null || imageUrlList.isEmpty()) {
            this.imageUrls = null;
        } else {
            // 过滤空字符串并连接
            this.imageUrls = imageUrlList.stream()
                    .map(String::trim)
                    .filter(url -> !url.isEmpty())
                    .collect(java.util.stream.Collectors.joining(","));
        }
    }
}
