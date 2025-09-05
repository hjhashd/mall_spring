package com.coding24h.mall_spring.entity;

public class MessageRecipientGroup {
    private Long id;
    private Long messageId;
    private String groupType; // 'all', 'vip', 'normal'

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
