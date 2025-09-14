package com.coding24h.mall_spring.config;

import com.coding24h.mall_spring.controller.message.ChatHandshakeInterceptor;
import com.coding24h.mall_spring.controller.message.ChatWebSocketHandler;
import com.coding24h.mall_spring.controller.message.UnifiedWebSocketHandler;
import com.coding24h.mall_spring.controller.message.UserHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final UnifiedWebSocketHandler unifiedWebSocketHandler;
    private final UserHandshakeInterceptor userHandshakeInterceptor;
    private final ChatWebSocketHandler chatWebSocketHandler;
    private final ChatHandshakeInterceptor chatHandshakeInterceptor; // 引入新的专用拦截器

    @Autowired
    public WebSocketConfig(
            UnifiedWebSocketHandler unifiedWebSocketHandler,
            UserHandshakeInterceptor userHandshakeInterceptor,
            ChatWebSocketHandler chatWebSocketHandler,
            ChatHandshakeInterceptor chatHandshakeInterceptor // 自动注入新的拦截器
    ) {
        this.unifiedWebSocketHandler = unifiedWebSocketHandler;
        this.userHandshakeInterceptor = userHandshakeInterceptor;
        this.chatWebSocketHandler = chatWebSocketHandler;
        this.chatHandshakeInterceptor = chatHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 交易系统 WebSocket (使用 UserHandshakeInterceptor)
        registry.addHandler(unifiedWebSocketHandler, "/ws")
                .addInterceptors(userHandshakeInterceptor)
                .setAllowedOriginPatterns("*");

        // AI 聊天 WebSocket (使用新的、专用的 ChatHandshakeInterceptor)
        registry.addHandler(chatWebSocketHandler, "/ws-chat")
                .addInterceptors(chatHandshakeInterceptor)
                .setAllowedOriginPatterns("*");
    }
}

