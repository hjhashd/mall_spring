package com.coding24h.mall_spring.service.impl;

import com.coding24h.mall_spring.entity.chat.ChatMessage;
import com.coding24h.mall_spring.entity.chat.UserChat;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface ChatService {
    List<UserChat> getUserChats(Integer userId);
    PageInfo<ChatMessage> getMessages(Integer chatId, Integer pageNum, Integer pageSize);
    ChatMessage sendMessage(Integer senderId, Integer chatId, String content, Integer messageType, String attachmentUrl);
    void markAsRead(Integer readerId, Integer chatId);

    void deleteMessage(Integer messageId, Integer currentUserId);

    void deleteChat(Integer chatId, Integer currentUserId);
    UserChat findOrCreateChat(Integer currentUserId, Integer peerId);
}
