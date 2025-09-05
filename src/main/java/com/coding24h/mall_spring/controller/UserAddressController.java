package com.coding24h.mall_spring.controller;

import com.coding24h.mall_spring.dto.ApiResponse;
import com.coding24h.mall_spring.entity.UserAddress;
import com.coding24h.mall_spring.entity.CustomUserDetails;
import com.coding24h.mall_spring.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/addresses")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    // 获取用户地址列表
    @GetMapping
    public ApiResponse<List<UserAddress>> getUserAddresses() {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            List<UserAddress> addresses = userAddressService.getUserAddresses(currentUserId.intValue());
            return new ApiResponse<>(true, "获取成功", addresses);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取地址列表失败: " + e.getMessage());
        }
    }

    // 新增地址
    @PostMapping
    public ApiResponse<String> addAddress(@RequestBody UserAddress address) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            address.setUserId(currentUserId.intValue());
            userAddressService.addAddress(address);
            return new ApiResponse<>(true, "地址添加成功");
        } catch (Exception e) {
            return new ApiResponse<>(false, "添加地址失败: " + e.getMessage());
        }
    }

    // 更新地址
    @PutMapping("/{addressId}")
    public ApiResponse<String> updateAddress(
            @PathVariable Integer addressId,
            @RequestBody UserAddress address) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            userAddressService.updateAddress(addressId, address, currentUserId.intValue());
            return new ApiResponse<>(true, "地址更新成功");
        } catch (Exception e) {
            return new ApiResponse<>(false, "更新地址失败: " + e.getMessage());
        }
    }

    // 删除地址
    @DeleteMapping("/{addressId}")
    public ApiResponse<String> deleteAddress(@PathVariable Integer addressId) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            userAddressService.deleteAddress(addressId, currentUserId.intValue());
            return new ApiResponse<>(true, "地址删除成功");
        } catch (Exception e) {
            return new ApiResponse<>(false, "删除地址失败: " + e.getMessage());
        }
    }

    // 设为默认地址
    @PutMapping("/{addressId}/default")
    public ApiResponse<String> setDefaultAddress(@PathVariable Integer addressId) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            userAddressService.setDefaultAddress(addressId, currentUserId.intValue());
            return new ApiResponse<>(true, "设置默认地址成功");
        } catch (Exception e) {
            return new ApiResponse<>(false, "设置默认地址失败: " + e.getMessage());
        }
    }

    // 获取默认地址
    @GetMapping("/default")
    public ApiResponse<UserAddress> getDefaultAddress() {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            UserAddress address = userAddressService.getDefaultAddress(currentUserId.intValue());
            return new ApiResponse<>(true, "获取成功", address);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取默认地址失败: " + e.getMessage());
        }
    }

    /**
     * (新增) 管理员获取所有地址的接口
     * 建议添加Spring Security权限控制，例如: @PreAuthorize("hasRole('ADMIN')")
     */
    @GetMapping("/admin/addresses")
    public ApiResponse<List<UserAddress>> getAllAddressesForAdmin() {
        try {
            List<UserAddress> addresses = userAddressService.getAllUserAddressesForAdmin();
            return new ApiResponse<>(true, "获取成功", addresses);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取地址列表失败: " + e.getMessage());
        }
    }

    /**
     * [Admin] 更新指定ID的地址
     */
    @PutMapping("/admin/{addressId}")
    public ApiResponse<String> adminUpdateAddress(@PathVariable Integer addressId, @RequestBody UserAddress address) {
        try {
            userAddressService.updateAddressByAdmin(addressId, address);
            return new ApiResponse<>(true, "地址更新成功");
        } catch (Exception e) {
            return new ApiResponse<>(false, "更新地址失败: " + e.getMessage());
        }
    }

    /**
     * [Admin] 删除指定ID的地址
     */
    @DeleteMapping("/admin/{addressId}")
    public ApiResponse<String> adminDeleteAddress(@PathVariable Integer addressId) {
        try {
            userAddressService.deleteAddressByAdmin(addressId);
            return new ApiResponse<>(true, "地址删除成功");
        } catch (Exception e) {
            return new ApiResponse<>(false, "删除地址失败: " + e.getMessage());
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
}
