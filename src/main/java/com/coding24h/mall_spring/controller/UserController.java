package com.coding24h.mall_spring.controller;

import com.coding24h.mall_spring.dto.ApiResponse;
import com.coding24h.mall_spring.entity.CustomUserDetails;
import com.coding24h.mall_spring.entity.vo.UserAccountSettingsVO;
import com.coding24h.mall_spring.entity.vo.UserBasicInfoVO;
import com.coding24h.mall_spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 更新用户头像
    @PostMapping("/update-avatar")
    public ApiResponse<String> updateUserAvatar(@RequestParam("avatar") MultipartFile file) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        // 更新用户在数据库中的头像路径（伪代码）
        String url = userService.updateUserAvatarPath(currentUserId, file);
        return new ApiResponse<>(true, url);
    }

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

    // 获取基本信息
    @GetMapping("/basic-info")
    public ApiResponse<UserBasicInfoVO> getUserBasicInfo() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return new ApiResponse<>(false, "请先登录");
        }
        UserBasicInfoVO userBasicInfoVO = userService.getUserBasicInfo(userId.intValue());
        return new ApiResponse<>(true, "获取成功", userBasicInfoVO);
    }

    // 获取用户账户设置信息
    @GetMapping("/account-info")
    public ApiResponse<UserAccountSettingsVO> getUserAccountInfo() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        UserAccountSettingsVO userAccountInfo = userService.getUserAccountInfo(userId.intValue());
        return new ApiResponse<>(true, "获取成功", userAccountInfo);
    }
}
