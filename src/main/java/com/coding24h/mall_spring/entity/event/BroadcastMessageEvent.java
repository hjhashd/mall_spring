package com.coding24h.mall_spring.entity.event;

import com.coding24h.mall_spring.entity.Message;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * 定义一个新的事件，专门用于广播系统消息
 * 当一个新的 Message 被发送时，此事件将被发布。
 */
public class BroadcastMessageEvent extends ApplicationEvent {
    private final Message message;
    private final List<Integer> recipientUserIds;

    public BroadcastMessageEvent(Object source, Message message, List<Integer> recipientUserIds) {
        super(source);
        this.message = message;
        this.recipientUserIds = recipientUserIds;
    }

    public Message getMessage() {
        return message;
    }

    public List<Integer> getRecipientUserIds() {
        return recipientUserIds;
    }
}
