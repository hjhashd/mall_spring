package com.coding24h.mall_spring.mapper.chat;

import com.coding24h.mall_spring.entity.chat.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMessageMapper {
    List<ChatMessage> selectByChatId(@Param("chatId") Integer chatId);
    int insert(ChatMessage msg);
    int markReadByChatAndReader(@Param("chatId") Integer chatId, @Param("readerId") Integer readerId);

    // Method to delete a message by its ID
    void deleteById(Integer messageId);

    // Method to select a message by its ID
    ChatMessage selectById(Integer messageId);

    List<ChatMessage> findAllByChatId(Integer chatId);
}
