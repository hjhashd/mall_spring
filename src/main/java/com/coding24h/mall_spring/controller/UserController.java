package com.coding24h.mall_spring.controller;

import com.coding24h.mall_spring.dto.ApiResponse;
import com.coding24h.mall_spring.entity.CustomUserDetails;
import com.coding24h.mall_spring.entity.vo.UserBasicInfoVO;
import com.coding24h.mall_spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 获取用户余额
    @GetMapping("/balance")
    public ApiResponse<BigDecimal> getUserBalance() {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            BigDecimal balance = userService.getUserBalance(currentUserId.intValue());
            return new ApiResponse<>(true, "获取成功", balance);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取余额失败: " + e.getMessage());
        }
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

    @GetMapping("/basic-info")
    public ApiResponse<UserBasicInfoVO> getUserBasicInfo() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return new ApiResponse<>(false, "请先登录");
        }
        UserBasicInfoVO userBasicInfoVO = userService.getUserBasicInfo(userId.intValue());
        return new ApiResponse<>(true, "获取成功", userBasicInfoVO);
    }
}
