package com.coding24h.mall_spring.service.impl;

import com.coding24h.mall_spring.dto.message.*;
import com.coding24h.mall_spring.entity.Message;
import com.coding24h.mall_spring.entity.MessageRecipientGroup;
import com.coding24h.mall_spring.entity.UserMessage;
import com.coding24h.mall_spring.entity.event.BroadcastMessageEvent; // 1. 【导入】广播事件类
import com.coding24h.mall_spring.mapper.message.MessageMapper;
import com.coding24h.mall_spring.mapper.message.MessageRecipientGroupMapper;
import com.coding24h.mall_spring.mapper.UserMapper;
import com.coding24h.mall_spring.mapper.message.UserMessageMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private MessageRecipientGroupMapper recipientGroupMapper;
    @Autowired
    private UserMessageMapper userMessageMapper;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher; // 2. 【确认】Spring事件发布器已注入

    @Transactional
    public MessageDTO saveMessage(MessageCreateDTO createDTO) {
        Message message = new Message();
        BeanUtils.copyProperties(createDTO, message);
        // 注意：你的原代码中 messageId 是 int 类型，但数据库是 bigint，这里做了转换
        message.setMessageId(createDTO.getId());

        if (StringUtils.hasText(createDTO.getScheduledTime())) {
            String dateTimeString = createDTO.getScheduledTime() + ":00";
            message.setScheduledTime(LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }

        if (message.getMessageId() == null) {
            messageMapper.insert(message);
        } else {
            recipientGroupMapper.deleteByMessageId(message.getMessageId());
            messageMapper.updateById(message);
        }

        if (createDTO.getRecipients() != null && !createDTO.getRecipients().isEmpty()) {
            List<MessageRecipientGroup> groups = createDTO.getRecipients().stream().map(groupType -> {
                MessageRecipientGroup group = new MessageRecipientGroup();
                group.setMessageId(message.getMessageId());
                group.setGroupType(groupType);
                return group;
            }).collect(Collectors.toList());
            recipientGroupMapper.batchInsert(groups);
        }

        // 如果状态是“发送”，则调用发送逻辑
        if ("sent".equals(createDTO.getStatus())) {
            sendMessage(message, createDTO.getRecipients());
        }

        MessageDTO resultDTO = new MessageDTO();
        BeanUtils.copyProperties(message, resultDTO);
        resultDTO.setRecipients(createDTO.getRecipients());
        return resultDTO;
    }

    private void sendMessage(Message message, List<String> recipientGroups) {
        // 确定所有接收者的用户ID
        Set<Long> recipientUserIdsLong = new HashSet<>();
        for (String groupType : recipientGroups) {
            List<Long> userIds = userMapper.findUserIdsByGroupType(groupType);
            recipientUserIdsLong.addAll(userIds);
        }

        if (recipientUserIdsLong.isEmpty()) {
            message.setStatus("failed");
            messageMapper.updateById(message);
            return;
        }

        // 3. 【核心逻辑】为每个用户创建离线消息记录
        // 这一步确保了无论用户是否在线，消息都会被保存
        List<UserMessage> userMessages = recipientUserIdsLong.stream().map(userId -> {
            UserMessage um = new UserMessage();
            um.setUserId(userId);
            um.setMessageId(message.getMessageId());
            um.setIsRead(false);
            return um;
        }).collect(Collectors.toList());
        userMessageMapper.batchInsert(userMessages);

        // 更新消息主表状态
        message.setStatus("sent");
        message.setSendTime(LocalDateTime.now());
        messageMapper.updateById(message);

        // 4. 【核心逻辑】发布广播事件，通知WebSocket Handler进行实时推送
        // 将 Set<Long> 转换为 List<Integer> 以匹配事件处理器的参数类型
        List<Integer> recipientUserIdsInt = recipientUserIdsLong.stream()
                .map(Long::intValue)
                .collect(Collectors.toList());

        eventPublisher.publishEvent(new BroadcastMessageEvent(this, message, recipientUserIdsInt));
    }

    @Transactional
    public void deleteMessage(Long messageId) {
        // 级联删除已配置，所以只需要删除主表
        messageMapper.deleteById(messageId);
    }

    public Page<MessageDTO> getMessages(MessageFilterDTO filterDTO) {
        PageHelper.startPage(filterDTO.getPage() + 1, filterDTO.getSize());

        List<MessageDTO> messages;
        // 假设 findMessagesWithRecipientCount 已经能正确查询
        messages = messageMapper.findMessagesWithRecipientCount(filterDTO);

        PageInfo<MessageDTO> pageInfo = new PageInfo<>(messages);

        PageRequest pageable = PageRequest.of(filterDTO.getPage(), filterDTO.getSize());
        return new PageImpl<>(pageInfo.getList(), pageable, pageInfo.getTotal());
    }

    // 【新增】获取当前用户的消息列表和未读数
    public UserMessagesResponseDTO getUserMessages(Long userId) {
        // 从数据库查询该用户的所有消息详情
        List<UserMessageDTO> messages = userMessageMapper.findMessagesByUserId(userId);
        // 单独查询未读数量，更高效
        int unreadCount = userMessageMapper.countUnreadByUserId(userId);

        UserMessagesResponseDTO response = new UserMessagesResponseDTO();
        response.setList(messages);
        response.setUnreadCount(unreadCount);

        return response;
    }

    // 【新增】将单条消息标记为已读
    @Transactional
    public void markMessageAsRead(Long userId, Long messageId) {
        userMessageMapper.markAsRead(userId, messageId);
    }

    // 【新增】将所有消息标记为已读
    @Transactional
    public void markAllMessagesAsRead(Long userId) {
        userMessageMapper.markAllAsRead(userId);
    }
}
