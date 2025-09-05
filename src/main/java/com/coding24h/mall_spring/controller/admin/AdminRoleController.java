package com.coding24h.mall_spring.controller.admin;

import com.coding24h.mall_spring.dto.ApiResponse;
import com.coding24h.mall_spring.dto.PageDTO;
import com.coding24h.mall_spring.dto.UserRoleInfoDTO;
import com.coding24h.mall_spring.entity.Role;
import com.coding24h.mall_spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')") // 保护整个控制器，只有ADMIN角色的用户才能访问
public class AdminRoleController {

    @Autowired
    private UserService userService;

    // 获取带角色的用户列表（用于角色管理页面）
    @GetMapping("/users-with-roles")
    public ApiResponse<PageDTO<UserRoleInfoDTO>> getUsersWithRoles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String query) {
        try {
            PageDTO<UserRoleInfoDTO> userPage = userService.getUsersWithRolesPage(page, pageSize, query);
            return new ApiResponse<>(true, "获取成功", userPage);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取用户列表失败: " + e.getMessage());
        }
    }

    // 更新指定用户的角色
    @PutMapping("/users/{userId}/roles")
    public ApiResponse<Void> updateUserRoles(@PathVariable Integer userId, @RequestBody Map<String, List<Integer>> payload) {
        try {
            List<Integer> roleIds = payload.get("roleIds");
            userService.updateUserRoles(userId, roleIds);
            return new ApiResponse<>(true, "角色更新成功");
        } catch (Exception e) {
            return new ApiResponse<>(false, "角色更新失败: " + e.getMessage());
        }
    }

    // =================================================
    // 【新增】角色池 (Role Pool) CRUD API
    // =================================================

    // 获取所有可用角色列表
    @GetMapping("/roles")
    public ApiResponse<List<Role>> getAllRoles() {
        try {
            List<Role> roles = userService.getAllRoles();
            return new ApiResponse<>(true, "获取成功", roles);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取角色列表失败: " + e.getMessage());
        }
    }

    // 新增一个角色
    @PostMapping("/roles")
    public ApiResponse<Role> createRole(@RequestBody Role role) {
        try {
            Role newRole = userService.createRole(role);
            return new ApiResponse<>(true, "角色创建成功", newRole);
        } catch (Exception e) {
            return new ApiResponse<>(false, "角色创建失败: " + e.getMessage());
        }
    }

    // 更新一个角色
    @PutMapping("/roles/{roleId}")
    public ApiResponse<Role> updateRole(@PathVariable Integer roleId, @RequestBody Role roleDetails) {
        try {
            Role updatedRole = userService.updateRole(roleId, roleDetails);
            return new ApiResponse<>(true, "角色更新成功", updatedRole);
        } catch (Exception e) {
            return new ApiResponse<>(false, "角色更新失败: " + e.getMessage());
        }
    }

    // 删除一个角色
    @DeleteMapping("/roles/{roleId}")
    public ApiResponse<Void> deleteRole(@PathVariable Integer roleId) {
        try {
            userService.deleteRole(roleId);
            return new ApiResponse<>(true, "角色删除成功");
        } catch (Exception e) {
            return new ApiResponse<>(false, "角色删除失败: " + e.getMessage());
        }
    }
}
