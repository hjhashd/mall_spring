package com.coding24h.mall_spring.entity;

import java.time.LocalDateTime;

/**
 * 回复实体类
 */
public class Reply {

    /**
     * 回复ID
     */
    private Integer replyId;

    /**
     * 回复内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 评价ID
     */
    private Integer reviewId;

    /**
     * 回复给哪个用户ID
     */
    private Integer repliedToUserId;

    /**
     * 回复给哪个用户名
     */
    private String repliedToUsername;

    /**
     * 是否为用户追评：0-否, 1-是
     */
    private Boolean isAppend;

    /**
     * 逻辑删除标志：0-未删除, 1-已删除
     */
    private Boolean deleted;

    // 关联字段（非数据库字段）
    private String username;
    private String userAvatar;

    // Getters and Setters

    public Integer getReplyId() {
        return replyId;
    }

    public void setReplyId(Integer replyId) {
        this.replyId = replyId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public Integer getRepliedToUserId() {
        return repliedToUserId;
    }

    public void setRepliedToUserId(Integer repliedToUserId) {
        this.repliedToUserId = repliedToUserId;
    }

    public String getRepliedToUsername() {
        return repliedToUsername;
    }

    public void setRepliedToUsername(String repliedToUsername) {
        this.repliedToUsername = repliedToUsername;
    }

    public Boolean getIsAppend() {
        return isAppend;
    }

    public void setIsAppend(Boolean isAppend) {
        this.isAppend = isAppend;
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
}
