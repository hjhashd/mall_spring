package com.coding24h.mall_spring.util;

import java.time.LocalDateTime;

// MessageFilter.java
public class MessageFilter {
    private String type; // 消息类型: system, user, marketing
    private String status; // 状态: draft, sent, failed
    private LocalDateTime startDate; // 开始时间
    private LocalDateTime endDate; // 结束时间

    // 无参构造方法
    public MessageFilter() {
    }

    // 全参构造方法
    public MessageFilter(String type, String status, LocalDateTime startDate, LocalDateTime endDate) {
        this.type = type;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getter方法
    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    // Setter方法
    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    // toString方法
    @Override
    public String toString() {
        return "MessageFilter{" +
                "type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}