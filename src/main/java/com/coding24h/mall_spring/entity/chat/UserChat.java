package com.coding24h.mall_spring.entity.chat;

import java.time.LocalDateTime;

public class UserChat {
    private Integer chatId;
    private Integer user1Id;
    private Integer user2Id;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private LocalDateTime createdAt;
    private Integer unreadCount; // 针对 user1 的未读数
    private String orderId;

    // 新增的用户名字段和头像
    private String user1Name;
    private String user2Name;
    private String user1Avatar;
    private String user2Avatar;

    public String getUser1Avatar() {
        return user1Avatar;
    }

    public void setUser1Avatar(String user1Avatar) {
        this.user1Avatar = user1Avatar;
    }

    public String getUser2Avatar() {
        return user2Avatar;
    }

    public void setUser2Avatar(String user2Avatar) {
        this.user2Avatar = user2Avatar;
    }

    public String getUser1Name() {
        return user1Name;
    }

    public void setUser1Name(String user1Name) {
        this.user1Name = user1Name;
    }

    public String getUser2Name() {
        return user2Name;
    }

    public void setUser2Name(String user2Name) {
        this.user2Name = user2Name;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public Integer getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(Integer user1Id) {
        this.user1Id = user1Id;
    }

    public Integer getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(Integer user2Id) {
        this.user2Id = user2Id;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
