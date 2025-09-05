package com.coding24h.mall_spring.entity.event;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 通知实体类，对应数据库中的 notifications 表
 */

public class MyNotification {

    /**
     * 通知ID，主键自增
     */
    private Integer notificationId;

    /**
     * 接收通知的用户ID
     */
    private Integer userId;

    /**
     * 通知类型 (e.g., ORDER_SHIPPED, NEW_MESSAGE, SYSTEM_ALERT)
     */
    private String type;

    /**
     * 通知内容 (JSON格式，自动映射为Map)
     */
    private Map<String, Object> content; // <--- 类型已更改

    /**
     * 是否已读: false-未读, true-已读
     */
    private boolean isRead;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getContent() {
        return content;
    }

    public void setContent(Map<String, Object> content) {
        this.content = content;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
