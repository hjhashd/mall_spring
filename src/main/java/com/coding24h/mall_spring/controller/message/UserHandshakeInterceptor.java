package com.coding24h.mall_spring.controller.message;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.Map;


@Component
public class UserHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(UserHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        // 解析URL中的查询参数
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            String userId = servletRequest.getServletRequest().getParameter("userId");

            if (userId != null && !userId.isEmpty()) {
                try {
                    attributes.put("userId", Integer.parseInt(userId));
                    logger.info("WebSocket握手: 解析到userId={}", userId);
                } catch (NumberFormatException e) {
                    logger.warn("WebSocket握手: userId参数格式错误={}", userId);
                    return false;
                }
            } else {
                logger.warn("WebSocket握手: 缺少userId参数");
                return false;
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        logger.info("WebSocket handshake completed");
    }
}
