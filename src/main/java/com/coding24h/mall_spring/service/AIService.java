package com.coding24h.mall_spring.service;


import org.springframework.web.socket.WebSocketSession;

public interface AIService {
    void  processQuestionViaWebSocket(String userId, String question, String conversationId, WebSocketSession session);
}
