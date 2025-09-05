package com.coding24h.mall_spring.dto.review;

/**
 * 回复提交DTO
 */
public class ReplySubmitDTO {

    /**
     * 评价ID
     */
    private Integer reviewId;

    /**
     * 回复内容
     */
    private String content;

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
    private Boolean isAppend = false;

    // Getters and Setters

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    @Override
    public String toString() {
        return "ReplySubmitDTO{" +
                "reviewId=" + reviewId +
                ", content='" + content + '\'' +
                ", repliedToUserId=" + repliedToUserId +
                ", repliedToUsername='" + repliedToUsername + '\'' +
                ", isAppend=" + isAppend +
                '}';
    }
}
