package com.coding24h.mall_spring.controller.message;


import com.coding24h.mall_spring.service.AIService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    // 添加日志记录器
    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketHandler.class);

    @Autowired
    private AIService aiService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从连接URL中获取参数
        Map<String, String> params = getQueryParams(session.getUri().getQuery());
        String userId = params.get("userId");
        String conversationId = params.get("conversationId");

        // 添加详细的日志输出
        logger.info("WebSocket connection established. Session ID: {}, URI: {}", session.getId(), session.getUri());
        logger.info("Extracted params -> userId: {}, conversationId: {}", userId, conversationId);


        // 将参数保存到会话属性中
        if (userId != null) {
            session.getAttributes().put("userId", userId);
        }
        if (conversationId != null) {
            session.getAttributes().put("conversationId", conversationId);
        }

        sessions.put(session.getId(), session);
        logger.info("Session {} added. Current session count: {}", session.getId(), sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        logger.info("Received message from session {}: {}", session.getId(), payload);

        JsonNode jsonNode = objectMapper.readTree(payload);
        String question = jsonNode.get("question").asText();

        String userId = (String) session.getAttributes().get("userId");
        String conversationId = (String) session.getAttributes().get("conversationId");

        // 调用服务层处理问题
        aiService.processQuestionViaWebSocket(userId, question, conversationId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        logger.info("Session {} closed. Status: {}. Current session count: {}", session.getId(), status, sessions.size());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("WebSocket transport error for session " + session.getId(), exception);
    }


    private Map<String, String> getQueryParams(String query) {
        if (query == null || query.isEmpty()) return Collections.emptyMap();
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("="))
                .filter(arr -> arr.length > 0) // 过滤掉无效的参数
                .collect(Collectors.toMap(
                        arr -> arr[0],
                        arr -> {
                            if (arr.length > 1) {
                                try {
                                    return URLDecoder.decode(arr[1], StandardCharsets.UTF_8.toString());
                                } catch (Exception e) {
                                    logger.error("Error decoding param: " + arr[1], e);
                                    return arr[1]; // 解码失败则返回原始值
                                }
                            }
                            return "";
                        },
                        (v1, v2) -> v2 // 如果有重复的key，保留后面的
                ));
    }
}
