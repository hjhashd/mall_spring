package com.coding24h.mall_spring.dto.message;

import java.util.List;

public class MessageCreateDTO {
    private Long id; // 用于更新时传递 messageId
    private String type;
    private String title;
    private String content;
    private List<String> recipients; // 对应前端的 recipients: ['all', 'vip']
    private String scheduledTime; // 前端传的是字符串，后端需要转换
    private String status; // 'draft' 或 'sent'

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
