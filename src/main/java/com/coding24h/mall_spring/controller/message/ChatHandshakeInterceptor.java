package com.coding24h.mall_spring.controller.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class ChatHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ChatHandshakeInterceptor.class);

    /**
     * 在 AI 聊天 WebSocket 握手前执行。
     * 仅负责从 URL 提取参数并传递到 session attributes。
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        logger.info("Entering AI Chat specific beforeHandshake interceptor for URI: {}", request.getURI());

        // 从请求 URI 中解析查询参数
        Map<String, String> queryParams = UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams().toSingleValueMap();
        String userId = queryParams.get("userId");
        String conversationId = queryParams.get("conversationId");

        // 对 AI 聊天来说，只要有 userId 就可以通过
        if (userId != null && !userId.isEmpty()) {
            attributes.put("userId", userId);
            if (conversationId != null) {
                attributes.put("conversationId", conversationId);
            }
            logger.info("Parameters extracted for AI Chat WebSocket. UserId: {}, ConversationId: {}", userId, conversationId);
            return true; // 允许握手
        } else {
            logger.warn("AI Chat WebSocket handshake rejected: userId parameter is missing. URI: {}", request.getURI());
            return false; // 拒绝握手
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        if (exception == null) {
            logger.info("AI Chat Handshake successful for URI: {}", request.getURI());
        } else {
            logger.error("AI Chat Handshake failed for URI: {}", request.getURI(), exception);
        }
    }
}
