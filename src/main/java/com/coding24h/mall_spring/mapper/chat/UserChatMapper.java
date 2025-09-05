package com.coding24h.mall_spring.mapper.chat;

import com.coding24h.mall_spring.entity.chat.UserChat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserChatMapper {
    List<UserChat> selectChatsByUser(@Param("userId") Integer userId);
   void updateLastMessage(@Param("chatId") Integer chatId,
                          @Param("lastMessage") String lastMessage,
                          @Param("lastMessageTime") LocalDateTime lastMessageTime);
    void incrementUnreadForUser1(@Param("chatId") Integer chatId);

    void incrementUnreadForUser2(@Param("chatId") Integer chatId);
    void resetUnreadForUser1(@Param("chatId") Integer chatId);

    void resetUnreadForUser2(@Param("chatId") Integer chatId);
    UserChat selectById(@Param("chatId") Integer chatId);

    void deleteById(Integer chatId);

    UserChat findByUserIds(@Param("user1Id") Integer user1Id, @Param("user2Id") Integer user2Id);
    int insert(UserChat chat);

    UserChat findDetailedChatById(Integer chatId);
}
