package com.coding24h.mall_spring.entity;

import java.time.LocalDateTime;
import java.util.List;

public class Message {
    private Long messageId;  // 改为Integer
    private Integer senderId;   // 改为Integer
    private String type;
    private String title;
    private String content;
    private String status;
    private LocalDateTime scheduledTime;
    private LocalDateTime sendTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer recipientCount;
    private List<String> recipientGroups;
    /**
     * 这个字段不是数据库 message 表的列。
     * 它是通过 Mapper 中的 JOIN 查询动态计算出来的，用于表示当前用户是否已读这条消息。
     * @Transient 注解告诉JPA/Hibernate等ORM框架，不要将此字段映射到数据库列。
     * 如果你没有使用JPA，也可以不加注解，只要确保数据库操作不涉及此字段即可。
     */
    private boolean isUnread;

    // --- 新增的 Getter 和 Setter ---
    public boolean isUnread() {
        return isUnread;
    }

    public void setUnread(boolean unread) {
        isUnread = unread;
    }
    // 无参构造函数
    public Message() {
    }

    // 全参构造函数（参数类型同步修改）
    public Message(Long messageId, Integer senderId, String type, String title,
                   String content, String status, LocalDateTime scheduledTime,
                   LocalDateTime sendTime, LocalDateTime createdAt,
                   LocalDateTime updatedAt, Integer recipientCount,
                   List<String> recipientGroups) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.type = type;
        this.title = title;
        this.content = content;
        this.status = status;
        this.scheduledTime = scheduledTime;
        this.sendTime = sendTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.recipientCount = recipientCount;
        this.recipientGroups = recipientGroups;
    }

    // Getter方法（返回值类型同步修改）
    public Long getMessageId() {
        return messageId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Integer getRecipientCount() {
        return recipientCount;
    }

    public List<String> getRecipientGroups() {
        return recipientGroups;
    }

    // Setter方法（参数类型同步修改）
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setRecipientCount(Integer recipientCount) {
        this.recipientCount = recipientCount;
    }

    public void setRecipientGroups(List<String> recipientGroups) {
        this.recipientGroups = recipientGroups;
    }

    // toString方法
    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", senderId=" + senderId +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", status='" + status + '\'' +
                ", scheduledTime=" + scheduledTime +
                ", sendTime=" + sendTime +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", recipientCount=" + recipientCount +
                ", recipientGroups=" + recipientGroups +
                '}';
    }
}
