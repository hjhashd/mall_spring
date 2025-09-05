package com.coding24h.mall_spring.controller.message;

import com.coding24h.mall_spring.dto.ApiResponse;
import com.coding24h.mall_spring.dto.message.MessageCreateDTO;
import com.coding24h.mall_spring.dto.message.MessageDTO;
import com.coding24h.mall_spring.dto.message.MessageFilterDTO;
import com.coding24h.mall_spring.dto.message.UserMessagesResponseDTO;
import com.coding24h.mall_spring.entity.CustomUserDetails;
import com.coding24h.mall_spring.service.impl.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 获取消息列表（带筛选和分页）
     * @param filterDTO 筛选条件
     * @return 分页后的消息数据
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<MessageDTO>>> getMessages(MessageFilterDTO filterDTO) {
        Page<MessageDTO> messages = messageService.getMessages(filterDTO);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    /**
     * 创建或更新消息
     * 前端通过 `messageForm.id` 是否存在来区分是创建还是更新，
     * 后端统一由此接口处理。
     * @param createDTO 消息数据
     * @return 操作结果
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MessageDTO>> saveMessage(@RequestBody MessageCreateDTO createDTO) {
        try {
            MessageDTO savedMessage = messageService.saveMessage(createDTO);
            return ResponseEntity.ok(ApiResponse.success(savedMessage));
        } catch (Exception e) {
            // 在实际项目中，这里应该有更完善的异常处理
            return ResponseEntity.badRequest().body(ApiResponse.error("操作失败: " + e.getMessage()));
        }
    }

    /**
     * 删除消息
     * @param id 消息ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(@PathVariable("id") Long id) {
        try {
            messageService.deleteMessage(id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("删除失败: " + e.getMessage()));
        }
    }

    /**
     * 【新增】获取当前登录用户的系统消息列表
     * @return 消息列表及未读数量
     */
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<UserMessagesResponseDTO>> getUserMessages() {
        Long currentUserId = getCurrentUserId();
        UserMessagesResponseDTO userMessages = messageService.getUserMessages(currentUserId);
        return ResponseEntity.ok(ApiResponse.success(userMessages));
    }

    /**
     * 【新增】将当前用户的某条消息标记为已读
     * @param messageId 消息ID
     * @return 操作结果
     */
    @PostMapping("/user/{messageId}/read")
    public ResponseEntity<ApiResponse<Void>> markMessageAsRead(@PathVariable("messageId") Long messageId) {
        Long currentUserId = getCurrentUserId();
        messageService.markMessageAsRead(currentUserId, messageId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 【新增】将当前用户的所有消息标记为已读
     * @return 操作结果
     */
    @PostMapping("/user/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllMessagesAsRead() {
        Long currentUserId = getCurrentUserId();
        messageService.markAllMessagesAsRead(currentUserId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 获取当前用户ID的辅助方法
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        }
        return null;
    }
}
