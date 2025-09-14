package com.coding24h.mall_spring.controller;

import com.coding24h.mall_spring.dto.ConversationHistoryDTO;
import com.coding24h.mall_spring.entity.ai.ChatRecord;
import com.coding24h.mall_spring.mapper.ChatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StreamingController {

    @Autowired
    private ChatMapper chatMapper;

    // 获取历史记录接口
    @GetMapping("/history")
    public List<ConversationHistoryDTO> getChatHistory(
            // 【修复】将 userId 的类型从 Long 修改为 String，以保持一致性
            @RequestParam String userId,
            @RequestParam(defaultValue = "20") int limit) {
        return chatMapper.getConversationHistory(userId, limit);
    }

    @GetMapping("/conversation/{conversationId}")
    public List<ChatRecord> getConversation(
            @PathVariable String conversationId) {
        return chatMapper.getChatRecordsByConversationId(conversationId);
    }

    // 更新删除接口，确保事务性
    @DeleteMapping("/conversation/{conversationId}")
    @Transactional
    public ResponseEntity<?> deleteConversation(@PathVariable String conversationId) {
        chatMapper.deleteConversationRecords(conversationId);
        chatMapper.deleteConversationSummary(conversationId);
        return ResponseEntity.ok().build();
    }
}
