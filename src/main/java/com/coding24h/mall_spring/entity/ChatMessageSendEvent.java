package com.coding24h.mall_spring.entity;

import com.coding24h.mall_spring.entity.chat.ChatMessage;
import org.springframework.context.ApplicationEvent;

public class ChatMessageSendEvent extends ApplicationEvent {

    private final ChatMessage chatMessage;

    public ChatMessageSendEvent(Object source, ChatMessage chatMessage) {
        super(source);
        this.chatMessage = chatMessage;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }
}
