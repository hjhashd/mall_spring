// WebSocketConfig.java
package com.coding24h.mall_spring.config;

import com.coding24h.mall_spring.controller.message.UserHandshakeInterceptor;
import com.coding24h.mall_spring.controller.message.UnifiedWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final UnifiedWebSocketHandler unifiedWebSocketHandler;
    private final UserHandshakeInterceptor userHandshakeInterceptor;

    public WebSocketConfig(UnifiedWebSocketHandler unifiedWebSocketHandler,
                           UserHandshakeInterceptor userHandshakeInterceptor) {
        this.unifiedWebSocketHandler = unifiedWebSocketHandler;
        this.userHandshakeInterceptor = userHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(unifiedWebSocketHandler, "/ws")
                .addInterceptors(userHandshakeInterceptor)
                .setAllowedOriginPatterns("*");
    }
}
