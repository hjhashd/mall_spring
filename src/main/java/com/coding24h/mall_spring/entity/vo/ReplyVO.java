package com.coding24h.mall_spring.entity.vo;


import java.time.LocalDateTime;

/**
 * 回复VO类
 */
public class ReplyVO {

    /**
     * 回复ID
     */
    private Integer replyId;

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
     * 回复内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 回复给哪个用户ID
     */
    private Integer repliedToUserId;

    /**
     * 回复给哪个用户名
     */
    private String repliedToUsername;

    /**
     * 是否为追评
     */
    private Boolean isAppend;


    public Integer getReplyId() {
        return replyId;
    }

    public void setReplyId(Integer replyId) {
        this.replyId = replyId;
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

    public Boolean getAppend() {
        return isAppend;
    }

    public void setAppend(Boolean append) {
        isAppend = append;
    }
}
