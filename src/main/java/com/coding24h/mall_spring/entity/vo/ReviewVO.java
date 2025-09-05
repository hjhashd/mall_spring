package com.coding24h.mall_spring.entity.vo;

import com.coding24h.mall_spring.entity.Reply;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价VO类
 */
public class ReviewVO {

    /**
     * 评价ID
     */
    private Integer reviewId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 评分
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 图片URL列表
     */
    private List<String> imageUrls;

    /**
     * 评价时间
     */
    private LocalDateTime createdAt;

    /**
     * 卖家回复
     */
    private String sellerReply;

    /**
     * 是否匿名
     */
    private Boolean isAnonymous;

    /**
     * 是否有追评
     */
    private Boolean hasAppend;

    /**
     * 回复列表
     */
    private List<Reply> replies;

    /**
     * 关联的商品名称
     */
    private String productName;

    /**
     * 关联的商品主图
     */
    private String productImage;


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
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

    public Boolean getAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        isAnonymous = anonymous;
    }

    public Boolean getHasAppend() {
        return hasAppend;
    }

    public void setHasAppend(Boolean hasAppend) {
        this.hasAppend = hasAppend;
    }

    public List<Reply> getReplies() {
        return replies;
    }

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }
}
