// com/coding24h/mall_spring/controller/message/UnifiedWebSocketHandler.java

package com.coding24h.mall_spring.controller.message;

import com.coding24h.mall_spring.dto.NotificationDTO;
import com.coding24h.mall_spring.entity.*;
import com.coding24h.mall_spring.entity.chat.ChatMessage;
import com.coding24h.mall_spring.entity.chat.UserChat;
import com.coding24h.mall_spring.entity.event.*;
import com.coding24h.mall_spring.mapper.OrderItemMapper;
import com.coding24h.mall_spring.mapper.OrderMapper;
import com.coding24h.mall_spring.mapper.ProductMapper;
import com.coding24h.mall_spring.mapper.UserMapper;
import com.coding24h.mall_spring.mapper.chat.UserChatMapper;
import com.coding24h.mall_spring.mapper.event.NotificationMapper;
import com.coding24h.mall_spring.service.impl.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class UnifiedWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(UnifiedWebSocketHandler.class);

    // 用户ID到WebSocket会话的映射，保持不变
    private final Map<Integer, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    private final UserChatMapper userChatMapper; // 用于反查接收者ID
    private final NotificationMapper notificationMapper;
    private final OrderItemMapper orderItemMapper;
    private  final OrderMapper orderMapper;
    private final ProductMapper productMapper; // 【新增】
    private final UserMapper userMapper; // 【新增】

    // 【修改】构造函数，新增Mapper注入
    public UnifiedWebSocketHandler(ObjectMapper objectMapper, ChatService chatService, UserChatMapper userChatMapper,
                                   NotificationMapper notificationMapper, OrderItemMapper orderItemMapper,
                                   OrderMapper orderMapper, ProductMapper productMapper, UserMapper userMapper) {
        this.objectMapper = objectMapper;
        this.chatService = chatService;
        this.userChatMapper = userChatMapper;
        this.notificationMapper = notificationMapper;
        this.orderItemMapper = orderItemMapper;
        this.orderMapper = orderMapper;
        this.productMapper = productMapper;
        this.userMapper = userMapper;
    }

    // ===================================================================================
    // ========================= 【核心新增】监听新订单创建事件 =========================
    // ===================================================================================
    /**
     * 监听新订单创建事件，为卖家发送待发货提醒
     * @param event 订单创建事件，假设它包含一个 Order 对象
     */
    @EventListener
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        Order order = event.getOrder();
        if (order == null) {
            logger.error("OrderCreatedEvent 中不包含订单信息。");
            return;
        }

        // 1. 根据订单ID获取所有订单项
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getOrderId());
        if (orderItems == null || orderItems.isEmpty()) {
            logger.error("新订单 {} 没有任何订单项，无法通知卖家。", order.getOrderId());
            return;
        }

        // 2. 查找此订单关联的所有卖家
        List<Integer> sellerIds = orderItems.stream()
                .map(item -> productMapper.selectById(item.getProductId()).getSellerId())
                .distinct()
                .collect(Collectors.toList());

        // 3. 为每个相关的卖家创建并推送通知
        for (Integer sellerId : sellerIds) {
            MyNotification notification = new MyNotification();
            try {
                // a. 构建通知内容
                Map<String, Object> contentMap = new HashMap<>();
                contentMap.put("title", "您有新的待发货订单");
                contentMap.put("message", "您收到了一个新的订单，请尽快处理发货。订单号: " + order.getOrderId());
                contentMap.put("link", "/seller-order?category=pending-shipment"); // 卖家待发货页面链接
                contentMap.put("orderId", order.getOrderId());

                // b. 存入数据库
                notification.setUserId(sellerId);
                notification.setType("PENDING_SHIPMENT_REMINDER"); // 定义一个新的通知类型
                notification.setContent(contentMap);
                notification.setRead(false);
                notificationMapper.insert(notification);
                logger.info("新的待发货订单通知已为卖家 {} 保存到数据库。", sellerId);

                // c. 实时推送（如果卖家在线）
                WebSocketSession sellerSession = userSessions.get(sellerId);
                if (sellerSession != null && sellerSession.isOpen()) {
                    NotificationDTO<MyNotification> wsMessage = new NotificationDTO<>("PENDING_SHIPMENT_REMINDER", notification);
                    sellerSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(wsMessage)));
                    logger.info("新的待发货订单通知已实时推送给卖家: {}", sellerId);
                } else {
                    logger.warn("卖家 {} 不在线，待发货通知已作为离线消息存储。", sellerId);
                }

            } catch (Exception e) {
                logger.error("为卖家 {} 推送待发货通知失败: {}", sellerId, e.getMessage(), e);
            }
        }
    }


    // 1. 监听我们自定义的 `ChatMessageSendEvent`
    @EventListener
    public void handleChatMessageSendEvent(ChatMessageSendEvent event) {
        ChatMessage message = event.getChatMessage();
        try {
            // 找到会话信息，从而确定接收者是谁
            UserChat chat = userChatMapper.selectById(message.getChatId());
            if (chat == null) return;

            Integer senderId = message.getSenderId();
            Integer receiverId = Objects.equals(chat.getUser1Id(), senderId) ? chat.getUser2Id() : chat.getUser1Id();

            // 查找接收者的WebSocket会话
            WebSocketSession receiverSession = userSessions.get(receiverId);

            // 如果接收者在线，就推送消息
            if (receiverSession != null && receiverSession.isOpen()) {
                Map<String, Object> payload = Map.of(
                        "type", "CHAT_MESSAGE", // 消息类型为 CHAT_MESSAGE
                        "data", message          // 直接将完整的消息对象作为数据
                );
                receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
                logger.info("实时消息已推送给用户: {}, 内容: {}", receiverId, message.getContent());
            } else {
                logger.warn("用户 {} 不在线，消息将作为离线消息存储。", receiverId);
            }
        } catch (IOException e) {
            logger.error("推送实时消息失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 监听订单发货事件
     * @param event 订单发货事件
     */
    @EventListener
    public void handleOrderShippedEvent(OrderShippedEvent event) {
        Order order = event.getOrder();
        Integer buyerId = order.getUserId();

        // 1. 构建通知内容
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("orderId", order.getOrderId());
        contentMap.put("message", "您的订单 " + order.getOrderId() + " 已发货！");
        contentMap.put("title", "订单已发货");
        contentMap.put("shippingCompany", order.getShippingCompany());
        contentMap.put("trackingNumber", order.getTrackingNumber());
        contentMap.put("link", "/orders");

        // 2. 存储到数据库（无论是否在线）
        MyNotification notification = new MyNotification();
        try {
            notification.setUserId(buyerId);
            notification.setType("ORDER_SHIPPED");
            notification.setContent(contentMap); // 使用 Map 类型
            notification.setRead(false);
            notificationMapper.insert(notification); // insert后，notification对象通常会被MyBatis填充ID和默认值
            logger.info("订单发货通知已为用户 {} 保存到数据库。ID: {}", buyerId, notification.getNotificationId());
        } catch (Exception e) {
            logger.error("保存离线通知失败 for user {}: {}", buyerId, e.getMessage(), e);
        }

        // 3. 实时推送（如果在线）
        WebSocketSession buyerSession = userSessions.get(buyerId);
        if (buyerSession != null && buyerSession.isOpen()) {
            try {
                // 【核心修复】推送完整的、刚存入数据库的 Notification 对象，而不是只推送 contentMap
                // 这样前端收到的实时消息结构就和通过API获取的旧消息结构完全一致了
                NotificationDTO<MyNotification> wsMessage = new NotificationDTO<>("ORDER_SHIPPED", notification);

                buyerSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(wsMessage)));
                logger.info("订单发货通知已实时推送给用户: {}", buyerId);
            } catch (IOException e) {
                logger.error("推送订单发货通知失败: {}", e.getMessage(), e);
            }
        } else {
            logger.warn("用户 {} 不在线，订单发货通知已作为离线消息存储。", buyerId);
        }
    }

    /**
     * 【新方法】监听售后申请事件，并推送通知给卖家
     * @param event 售后申请事件
     */
    @EventListener
    public void handleAfterSaleApplicationEvent(AfterSaleApplicationEvent event) {
        AfterSales afterSale = event.getAfterSale();
        OrderItem orderItem = event.getOrderItem();
        MyNotification notification = new MyNotification();

        try {
            // 1. 找到卖家ID
            Product product = productMapper.selectById(orderItem.getProductId());
            if (product == null) {
                logger.error("未找到商品信息，无法推送售后申请通知。");
                return;
            }
            Integer sellerId = product.getSellerId();

            // 2. 构建通知内容
            Map<String, Object> contentMap = new HashMap<>();
            contentMap.put("title", "新的售后申请");
            contentMap.put("message", "买家对商品“" + product.getTitle() + "”发起了售后申请，请及时处理。");
            contentMap.put("link", "/seller-order?category=after-sales");
            contentMap.put("orderItemId", orderItem.getItemId());
            contentMap.put("afterSaleId", afterSale.getAfterSaleId());

            // 3. 存储到数据库
            notification.setUserId(sellerId);
            notification.setType("AFTER_SALE_APPLICATION"); // 新的通知类型
            notification.setContent(contentMap);
            notification.setRead(false);
            notificationMapper.insert(notification);
            logger.info("新的售后申请通知已为卖家 {} 保存到数据库。", sellerId);

            // 4. 实时推送（如果卖家在线）
            WebSocketSession sellerSession = userSessions.get(sellerId);
            if (sellerSession != null && sellerSession.isOpen()) {
                // 【保持一致性修复】推送完整的 Notification 对象
                NotificationDTO<MyNotification> wsMessage = new NotificationDTO<>("AFTER_SALE_APPLICATION", notification);
                sellerSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(wsMessage)));
                logger.info("新的售后申请通知已实时推送给卖家: {}", sellerId);
            } else {
                logger.warn("卖家 {} 不在线，售后申请通知已作为离线消息存储。", sellerId);
            }
        } catch (Exception e) {
            logger.error("推送售后申请通知失败: {}", e.getMessage(), e);
        }
    }


    @EventListener
    public void handleAfterSaleProcessedEvent(AfterSaleProcessedEvent event) {
        AfterSales afterSale = event.getAfterSale();
        OrderItem orderItem = orderItemMapper.selectById(afterSale.getOrderItemId());
        Order order = orderMapper.selectByOrderId(orderItem.getOrderId());
        Integer buyerId = order.getUserId();
        MyNotification notification = new MyNotification();

        // 1. 构建通知内容
        String resultText = afterSale.getStatus() == 1 ? "已同意" : "已拒绝";
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("title", "售后申请已处理");
        contentMap.put("message", "您对商品“" + event.getProductName() + "”的售后申请，商家已处理：" + resultText + "。");
        contentMap.put("link", "/orders");
        contentMap.put("afterSaleId", afterSale.getAfterSaleId());

        // 2. 存入数据库
        notification.setUserId(buyerId);
        notification.setType("AFTER_SALE_PROCESSED");
        notification.setContent(contentMap);
        notificationMapper.insert(notification);

        // 3. 实时推送
        WebSocketSession buyerSession = userSessions.get(buyerId);
        if (buyerSession != null && buyerSession.isOpen()) {
            try {
                // 【保持一致性修复】推送完整的 Notification 对象
                NotificationDTO<MyNotification> wsMessage = new NotificationDTO<>("AFTER_SALE_PROCESSED", notification);
                buyerSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(wsMessage)));
                logger.info("售后处理通知已推送给用户: {}", buyerId);
            } catch (IOException e) {
                logger.error("推送售后处理通知失败: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 【新增方法】监听通用的管理员通知事件，并推送给指定的用户
     * @param event 管理员通知事件
     */
    @EventListener
    public void handleAdminNotificationEvent(AdminNotificationEvent event) {
        // 循环遍历所有接收者
        for (Integer receiverId : event.getReceiverIds()) {
            MyNotification notification = new MyNotification();
            try {
                // 1. 存储到数据库（无论是否在线）
                notification.setUserId(receiverId);
                notification.setType(event.getType()); // 使用事件中的类型
                notification.setContent(event.getData()); // 使用事件中的数据
                notification.setRead(false);
                notificationMapper.insert(notification);
                logger.info("管理员通知已为用户 {} 保存到数据库。", receiverId);

                // 2. 实时推送（如果在线）
                WebSocketSession receiverSession = userSessions.get(receiverId);
                if (receiverSession != null && receiverSession.isOpen()) {
                    // 【保持一致性修复】推送完整的 Notification 对象
                    NotificationDTO<MyNotification> wsMessage = new NotificationDTO<>(event.getType(), notification);
                    receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(wsMessage)));
                    logger.info("管理员通知已实时推送给用户: {}", receiverId);
                } else {
                    logger.warn("用户 {} 不在线，管理员通知已作为离线消息存储。", receiverId);
                }
            } catch (Exception e) {
                logger.error("推送管理员通知失败 for user {}: {}", receiverId, e.getMessage(), e);
            }
        }
    }

    // 2. 处理从客户端发来的WebSocket消息
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Integer senderId = (Integer) session.getAttributes().get("userId");
        if (senderId == null) {
            session.close(CloseStatus.POLICY_VIOLATION.withReason("User ID not found in session"));
            return;
        }

        try {
            Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
            String type = (String) payload.get("type");

            if ("CHAT_MESSAGE".equals(type)) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                if (data == null) return;

                Integer chatId = (Integer) data.get("chatId");
                String content = (String) data.get("content");
                Integer messageType = (Integer) data.getOrDefault("messageType", 1);
                String attachmentUrl = (String) data.get("attachmentUrl");

                chatService.sendMessage(senderId, chatId, content, messageType, attachmentUrl);
            } else {
                logger.warn("收到未知的消息类型: {}", type);
            }
        } catch (Exception e) {
            logger.error("处理WebSocket消息失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Integer userId = (Integer) session.getAttributes().get("userId");
        if (userId != null) {
            logger.info("WebSocket连接建立: 用户ID={}, 会话ID={}", userId, session.getId());
            userSessions.put(userId, session);
            broadcastStatusUpdate(userId, true);
        } else {
            logger.warn("WebSocket连接建立失败: 未提供userId");
            try {
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("缺少用户ID参数"));
            } catch (IOException e) {
                logger.error("关闭会话失败", e);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Integer userId = (Integer) session.getAttributes().get("userId");
        if (userId != null) {
            logger.info("WebSocket连接关闭: 用户ID={}, 原因={}", userId, status.getReason());
            userSessions.remove(userId);
            broadcastStatusUpdate(userId, false);
        }
    }

    private void broadcastStatusUpdate(Integer changedUserId, boolean isOnline) {
        Map<String, Object> statusPayload = new HashMap<>();
        statusPayload.put("userId", changedUserId);
        statusPayload.put("isOnline", isOnline);

        NotificationDTO<Map<String, Object>> wsMessage = new NotificationDTO<>("USER_STATUS_UPDATE", statusPayload);

        try {
            String messageString = objectMapper.writeValueAsString(wsMessage);
            TextMessage textMessage = new TextMessage(messageString);
            long successfulSends = 0;

            for (Map.Entry<Integer, WebSocketSession> entry : userSessions.entrySet()) {
                Integer userId = entry.getKey();
                WebSocketSession session = entry.getValue();

                if (Objects.equals(userId, changedUserId)) {
                    continue;
                }

                if (session != null && session.isOpen()) {
                    try {
                        session.sendMessage(textMessage);
                        successfulSends++;
                    } catch (IOException e) {
                        logger.error("向用户 {} 推送状态更新失败: {}", userId, e.getMessage(), e);
                    }
                }
            }
            logger.info("用户 {} 状态变更为{}，已向 {} 个在线用户广播此状态更新",
                    changedUserId,
                    isOnline ? "在线" : "离线",
                    successfulSends);
        } catch (Exception e) {
            logger.error("创建或广播用户 {} 状态更新消息失败", changedUserId, e);
        }
    }

    @EventListener
    public void handleUserFollowedEvent(UserFollowedEvent event) {
        Integer followerId = event.getFollowerId();
        Integer sellerId = event.getFollowedId();
        MyNotification notification = new MyNotification();

        try {
            User follower = userMapper.selectById(followerId);
            String followerUsername = (follower != null) ? follower.getUsername() : "一位新用户";

            Map<String, Object> contentMap = new HashMap<>();
            contentMap.put("title", "您有新的粉丝");
            contentMap.put("message", "用户 “" + followerUsername + "” 刚刚关注了您的店铺！");
            contentMap.put("link", "/seller-dashboard/followers");
            contentMap.put("followerId", followerId);

            notification.setUserId(sellerId);
            notification.setType("NEW_FOLLOWER");
            notification.setContent(contentMap);
            notification.setRead(false);
            notificationMapper.insert(notification);
            logger.info("新的粉丝通知已为卖家 {} 保存到数据库。", sellerId);

            WebSocketSession sellerSession = userSessions.get(sellerId);
            if (sellerSession != null && sellerSession.isOpen()) {
                // 【保持一致性修复】推送完整的 Notification 对象
                NotificationDTO<MyNotification> wsMessage = new NotificationDTO<>("NEW_FOLLOWER", notification);
                sellerSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(wsMessage)));
                logger.info("新的粉丝通知已实时推送给卖家: {}", sellerId);
            } else {
                logger.warn("卖家 {} 不在线，新的粉丝通知已作为离线消息存储。", sellerId);
            }
        } catch (Exception e) {
            logger.error("推送新的粉丝通知失败: {}", e.getMessage(), e);
        }
    }

    @EventListener
    public void handleBroadcastMessageEvent(BroadcastMessageEvent event) {
        Message message = event.getMessage();
        List<Integer> recipientUserIds = event.getRecipientUserIds();
        NotificationDTO<Message> wsMessage = new NotificationDTO<>("NEW_MESSAGE", message);

        try {
            String messagePayload = objectMapper.writeValueAsString(wsMessage);
            TextMessage textMessage = new TextMessage(messagePayload);
            int onlineCount = 0;

            for (Integer userId : recipientUserIds) {
                WebSocketSession userSession = userSessions.get(userId);
                if (userSession != null && userSession.isOpen()) {
                    try {
                        userSession.sendMessage(textMessage);
                        onlineCount++;
                    } catch (IOException e) {
                        logger.error("向用户 {} 推送系统消息失败: {}", userId, e.getMessage());
                    }
                }
            }
            logger.info("系统消息 (ID: {}) 已成功推送给 {} 位在线用户。", message.getMessageId(), onlineCount);

        } catch (Exception e) {
            logger.error("序列化或广播系统消息失败: {}", e.getMessage(), e);
        }
    }
}
