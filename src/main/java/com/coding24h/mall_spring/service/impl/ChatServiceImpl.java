package com.coding24h.mall_spring.service.impl;


import com.coding24h.mall_spring.entity.ChatMessageSendEvent;
import com.coding24h.mall_spring.entity.chat.ChatMessage;
import com.coding24h.mall_spring.entity.chat.UserChat;
import com.coding24h.mall_spring.mapper.chat.ChatMessageMapper;
import com.coding24h.mall_spring.mapper.chat.UserChatMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ChatServiceImpl implements ChatService {

    @Resource
    private ChatMessageMapper chatMessageMapper;
    @Resource private UserChatMapper userChatMapper;
    @Autowired
    private FileStorageService fileStorageService;
    // 注入事件发布器
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Override
    public List<UserChat> getUserChats(Integer userId) {
        return userChatMapper.selectChatsByUser(userId);
    }

    @Override
    public PageInfo<ChatMessage> getMessages(Integer chatId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 50);
        List<ChatMessage> list = chatMessageMapper.selectByChatId(chatId);
        return new PageInfo<>(list);
    }

    @Override
    @Transactional
    public ChatMessage sendMessage(Integer senderId, Integer chatId, String content, Integer messageType, String attachmentUrl) {
        // 查询会话是否存在
        UserChat chat = userChatMapper.selectById(chatId);
        if (chat == null) throw new IllegalArgumentException("Chat not found");

        // 检查发送者是否属于该会话
        if (!Objects.equals(chat.getUser1Id(), senderId) && !Objects.equals(chat.getUser2Id(), senderId)) {
            throw new SecurityException("Sender not in chat");
        }

        // 构造消息对象
        ChatMessage msg = new ChatMessage();
        msg.setChatId(chatId);
        msg.setSenderId(senderId);
        msg.setContent(content == null ? "" : content);
        msg.setIsRead(0); // 默认未读
        msg.setMessageType(messageType == null ? 1 : messageType);
        msg.setAttachmentUrl(attachmentUrl);

        // 插入消息
        chatMessageMapper.insert(msg);

        // 根据消息类型决定最后显示的内容
        String lastMessageContent;
        if (messageType == 2) {
            lastMessageContent = "[图片]";
        } else if (messageType == 3) {
            lastMessageContent = "[商品卡片]";
        } else {
            lastMessageContent = msg.getContent();
        }

        // 更新会话中的最后一条消息和时间
        userChatMapper.updateLastMessage(
                chatId,
                lastMessageContent,
                LocalDateTime.now()
        );

        // --- 关键修改点：正确地为接收方增加未读数 ---
        Integer receiverId = Objects.equals(chat.getUser1Id(), senderId) ? chat.getUser2Id() : chat.getUser1Id();
        if (Objects.equals(receiverId, chat.getUser1Id())) {
            userChatMapper.incrementUnreadForUser1(chatId);
        } else {
            userChatMapper.incrementUnreadForUser2(chatId);
        }

        // --- 新增：发布消息发送事件 ---
        // 获取完整的消息对象（包含数据库生成的ID和时间等信息）
        ChatMessage fullMessage = chatMessageMapper.selectById(msg.getMessageId());
        eventPublisher.publishEvent(new ChatMessageSendEvent(this, fullMessage));

        return fullMessage; // 返回完整的消息对象
    }


    @Override
    @Transactional
    public void markAsRead(Integer readerId, Integer chatId) {
        UserChat chat = userChatMapper.selectById(chatId);
        if (chat == null) return;
        chatMessageMapper.markReadByChatAndReader(chatId, readerId);

        // --- 关键修改：重置正确的未读计数 ---
        if (Objects.equals(readerId, chat.getUser1Id())) {
            userChatMapper.resetUnreadForUser1(chatId);
        } else if (Objects.equals(readerId, chat.getUser2Id())) {
            userChatMapper.resetUnreadForUser2(chatId);
        }
    }

    /**
     * Deletes a message.
     *
     * @param messageId The ID of the message to delete.
     * @param currentUserId The ID of the user requesting the deletion.
     */
    @Transactional
    public void deleteMessage(Integer messageId, Integer currentUserId) {
        // 1. Find the message by its ID
        ChatMessage message = chatMessageMapper.selectById(messageId);
        if (message == null) {
            // If message doesn't exist, do nothing.
            return;
        }

        // 2. Security Check: Ensure the user is the sender of the message
        if (!message.getSenderId().equals(currentUserId)) {
            throw new SecurityException("User is not authorized to delete this message.");
        }

        // 3. If the message is an image, delete the associated file from storage
        if (message.getMessageType() == 2 && message.getAttachmentUrl() != null && !message.getAttachmentUrl().isEmpty()) {
            try {
                fileStorageService.deleteFileByUrl(message.getAttachmentUrl());
            } catch (Exception e) {
                // Log the error but don't block the message deletion from DB
                System.err.println("Failed to delete file for message " + messageId + ": " + e.getMessage());
            }
        }

        // 4. Delete the message record from the database
        chatMessageMapper.deleteById(messageId);

        // Optional: You could add logic here to update the last_message in the user_chats table
        // if the deleted message was the most recent one. For simplicity, we'll omit this for now.
    }

    /**
     * Deletes an entire chat conversation.
     *
     * @param chatId The ID of the chat to delete.
     * @param currentUserId The ID of the user requesting the deletion.
     */
    @Transactional
    public void deleteChat(Integer chatId, Integer currentUserId) {
        // 1. Find the chat conversation
        UserChat chat = userChatMapper.selectById(chatId);
        if (chat == null) {
            // Chat already deleted or does not exist
            return;
        }

        // 2. Security Check: Ensure the user is part of this conversation
        if (!chat.getUser1Id().equals(currentUserId) && !chat.getUser2Id().equals(currentUserId)) {
            throw new SecurityException("User is not authorized to delete this chat.");
        }

        // 3. CRITICAL STEP: Find all image messages in this chat BEFORE deleting
        List<ChatMessage> messages = chatMessageMapper.findAllByChatId(chatId); // You need to add this method to your mapper

        for (ChatMessage message : messages) {
            // If the message is an image, delete the associated file
            if (message.getMessageType() == 2 && message.getAttachmentUrl() != null && !message.getAttachmentUrl().isEmpty()) {
                try {
                    fileStorageService.deleteFileByUrl(message.getAttachmentUrl());
                } catch (Exception e) {
                    System.err.println("Failed to delete file for message " + message.getMessageId() + ": " + e.getMessage());
                    // We log the error but continue, to ensure the chat record is deleted
                }
            }
        }

        // 4. Delete the chat record. Thanks to ON DELETE CASCADE, all associated
        //    messages in chat_messages will be deleted automatically by the database.
        userChatMapper.deleteById(chatId);
    }

    /**
     * Finds an existing chat between two users or creates a new one if it doesn't exist.
     *
     * @param currentUserId The ID of the current user.
     * @param peerId The ID of the other user.
     * @return The existing or newly created UserChat object.
     */
    @Transactional
    public UserChat findOrCreateChat(Integer currentUserId, Integer peerId) {
        if (currentUserId.equals(peerId)) {
            throw new IllegalArgumentException("不能和自己发起聊天。");
        }

        UserChat chat = userChatMapper.findByUserIds(currentUserId, peerId);

        if (chat != null) {
            return userChatMapper.findDetailedChatById(chat.getChatId());
        } else {
            // 如果不存在，创建新的
            UserChat newChat = new UserChat();
            newChat.setUser1Id(currentUserId);
            newChat.setUser2Id(peerId);

            userChatMapper.insert(newChat); // 这会回填 newChat.chatId

            // 创建成功后，使用新 chatId 去获取包含双方用户信息的完整数据
            return userChatMapper.findDetailedChatById(newChat.getChatId());
        }
    }
}
