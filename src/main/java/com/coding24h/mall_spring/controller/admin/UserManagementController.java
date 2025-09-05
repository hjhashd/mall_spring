package com.coding24h.mall_spring.controller.admin;

import com.coding24h.mall_spring.dto.ApiResponse;
import com.coding24h.mall_spring.dto.PageDTO;
import com.coding24h.mall_spring.dto.UserDTO;
import com.coding24h.mall_spring.entity.User;
import com.coding24h.mall_spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
// @PreAuthorize("hasRole('ADMIN')") // 可以在这里统一设置权限，要求用户必须有ADMIN角色
public class UserManagementController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户列表（分页、可搜索）
     * @param page 页码，默认为1
     * @param pageSize 每页数量，默认为10
     * @param query 搜索关键词，可选
     * @return 分页后的用户数据
     */
    @GetMapping
    public ApiResponse<PageDTO<User>> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String query) {
        try {
            PageDTO<User> userPage = userService.getUsersPage(page, pageSize, query);
            return new ApiResponse<>(true, "获取用户列表成功", userPage);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 切换用户状态（启用/禁用）
     * @param userId 用户ID
     * @param payload 请求体，应包含 "enabled": true/false
     * @return 操作结果
     */
    @PostMapping("/{userId}/status")
    public ApiResponse<?> toggleUserStatus(@PathVariable Long userId, @RequestBody Map<String, Boolean> payload) {
        Boolean isEnabled = payload.get("enabled");
        if (isEnabled == null) {
            return new ApiResponse<>(false, "请求参数错误，缺少 'enabled' 字段");
        }
        try {
            userService.toggleUserStatus(userId, isEnabled);
            String message = isEnabled ? "用户已启用" : "用户已禁用";
            return new ApiResponse<>(true, message);
        } catch (Exception e) {
            return new ApiResponse<>(false, "操作失败: " + e.getMessage());
        }
    }

    /**
     * 创建新用户
     * @param userDTO 用户数据
     * @return 创建后的用户信息
     */
    @PostMapping
    public ApiResponse<User> createUser(@RequestBody UserDTO userDTO) {
        try {
            User newUser = userService.createUser(userDTO);
            return new ApiResponse<>(true, "用户创建成功", newUser);
        } catch (Exception e) {
            return new ApiResponse<>(false, "创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param userDTO 更新的用户数据
     * @return 更新后的用户信息
     */
    @PutMapping("/{userId}")
    public ApiResponse<User> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
        try {
            User updatedUser = userService.updateUser(userId, userDTO);
            return new ApiResponse<>(true, "用户信息更新成功", updatedUser);
        } catch (Exception e) {
            return new ApiResponse<>(false, "更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户
     * @param userId 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{userId}")
    public ApiResponse<?> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return new ApiResponse<>(true, "用户删除成功");
        } catch (Exception e) {
            return new ApiResponse<>(false, "删除失败: " + e.getMessage());
        }
    }

}
