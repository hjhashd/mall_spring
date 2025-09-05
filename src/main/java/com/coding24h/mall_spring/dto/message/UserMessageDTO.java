package com.coding24h.mall_spring.dto.message;

import java.time.LocalDateTime;


public class UserMessageDTO {
    private Long messageId;
    private String title;
    private String content;
    private LocalDateTime sendTime;
    private boolean isUnread; // is_read=0 -> true, is_read=1 -> false

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
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

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }

    public boolean getIsUnread() {
        return isUnread;
    }

    public void setIsUnread(boolean unread) {
        isUnread = unread;
    }
}
