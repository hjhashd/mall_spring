package com.coding24h.mall_spring.controller;

import com.coding24h.mall_spring.dto.ApiResponse;
import com.coding24h.mall_spring.entity.CustomUserDetails;
import com.coding24h.mall_spring.entity.chat.ChatMessage;
import com.coding24h.mall_spring.entity.chat.UserChat;
import com.coding24h.mall_spring.service.impl.ChatService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coding24h.mall_spring.dto.ApiResponse.error;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Resource
    private ChatService chatService;

    // 获取当前用户ID的辅助方法
    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                return ((CustomUserDetails) principal).getUserId().intValue();
            } else if (principal instanceof String && "anonymousUser".equals(principal)) {
                return null;
            }
        }
        return null;
    }

    @GetMapping("/chats")
    public ApiResponse<List<UserChat>> getUserChats() {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        return ApiResponse.success(chatService.getUserChats(userId));
    }

    @GetMapping("/chats/{chatId}/messages")
    public ApiResponse<PageInfo<ChatMessage>> getChatMessages(@PathVariable Integer chatId,
                                                              @RequestParam(defaultValue="1") Integer pageNum,
                                                              @RequestParam(defaultValue="50") Integer pageSize) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return error("用户未登录");
        }
        return ApiResponse.success(chatService.getMessages(chatId, pageNum, pageSize));
    }

    @PostMapping("/messages")
    public ApiResponse<ChatMessage> sendMessage(@RequestBody Map<String,Object> body) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return error("用户未登录");
        }

        Integer chatId = (Integer) body.get("chatId");
        String content = (String) body.getOrDefault("content", "");
        Integer messageType = (Integer) body.getOrDefault("messageType", 1);
        String attachmentUrl = (String) body.get("attachmentUrl");

        ChatMessage msg = chatService.sendMessage(userId, chatId, content, messageType, attachmentUrl);
        return ApiResponse.success(msg);
    }

    @PostMapping("/chats/{chatId}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Integer chatId) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return error("用户未登录");
        }

        chatService.markAsRead(userId, chatId);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/messages/{messageId}")
    public ApiResponse<?> deleteMessage(@PathVariable Integer messageId) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return error("用户未登录");
        }

        try {
            chatService.deleteMessage(messageId, userId);
            return ApiResponse.success(null); // Return success with no data
        } catch (SecurityException e) {
            return ApiResponse.error("403", e.getMessage()); // Forbidden
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("500", "删除消息失败");
        }
    }

    @DeleteMapping("/chats/{chatId}")
    public ApiResponse<?> deleteChat(@PathVariable Integer chatId) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return error("用户未登录");
        }

        try {
            chatService.deleteChat(chatId, userId);
            return ApiResponse.success(null);
        } catch (SecurityException e) {
            return ApiResponse.error("403", e.getMessage()); // Forbidden
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("500", "删除会话失败");
        }
    }

    @PostMapping("/chats/find-or-create")
// 返回值类型改为 ApiResponse<UserChat>
    public ApiResponse<UserChat> findOrCreateChat(@RequestBody Map<String, Integer> payload) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }

        Integer peerId = payload.get("peerId");
        if (peerId == null) {
            return ApiResponse.error("对方用户ID不能为空");
        }

        try {
            UserChat chat = chatService.findOrCreateChat(userId, peerId);
            return ApiResponse.success(chat); // 使用 ApiResponse.success 包装
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("无法开始会话");
        }
    }
}
