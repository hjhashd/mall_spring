package com.coding24h.mall_spring.controller.message;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionManager {
    // 用户ID到会话的映射
    private final Map<Integer, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    // 添加会话
    public void addSession(Integer userId, WebSocketSession session) {
        userSessions.put(userId, session);
    }

    // 移除会话
    public void removeSession(Integer userId) {
        userSessions.remove(userId);
    }

    // 获取会话
    public WebSocketSession getSession(Integer userId) {
        return userSessions.get(userId);
    }

    // 获取所有在线用户ID
    public Set<Integer> getOnlineUserIds() {
        return userSessions.keySet();
    }

    // 获取在线用户数量
    public int getOnlineCount() {
        return userSessions.size();
    }
}
