package com.coding24h.mall_spring.controller;

import com.coding24h.mall_spring.dto.ApiResponse;
import com.coding24h.mall_spring.entity.CustomUserDetails;
import com.coding24h.mall_spring.entity.event.MyNotification;
import com.coding24h.mall_spring.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 获取当前用户的所有未读通知
     */
    @GetMapping("/unread")
    public ApiResponse<List<MyNotification>> getUnreadNotifications() {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "用户未登录");
        }
        List<MyNotification> notifications = notificationService.getUnreadNotifications(currentUserId.intValue());
        return new ApiResponse<>(true, "获取成功", notifications);
    }

    /**
     * 将单个通知标记为已读
     */
    @PostMapping("/{notificationId}/read")
    public ApiResponse<String> markAsRead(@PathVariable Integer notificationId) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "用户未登录");
        }
        notificationService.markAsRead(currentUserId.intValue(), notificationId);
        return new ApiResponse<>(true, "标记成功");
    }

    /**
     * 将所有通知标记为已读
     */
    @PostMapping("/read-all")
    public ApiResponse<String> markAllAsRead() {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "用户未登录");
        }
        notificationService.markAllAsRead(currentUserId.intValue());
        return new ApiResponse<>(true, "全部标记成功");
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        }
        return null;
    }
}
