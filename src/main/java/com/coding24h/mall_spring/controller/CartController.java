package com.coding24h.mall_spring.controller;

import com.coding24h.mall_spring.dto.ApiResponse;
import com.coding24h.mall_spring.dto.cart.CartItemDTO;
import com.coding24h.mall_spring.dto.cart.CartSummaryDTO;
import com.coding24h.mall_spring.entity.CustomUserDetails;
import com.coding24h.mall_spring.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // 添加商品到购物车
    @PostMapping("/{productId}/add-to-cart")
    public ApiResponse<String> addToCart(
            @PathVariable Integer productId,
            @RequestBody Map<String, Integer> payload) {

        Integer quantity = payload.getOrDefault("quantity", 1);

        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            cartService.addToCart(currentUserId.intValue(), productId, quantity);
            return new ApiResponse<>(true, "商品已加入购物车");
        } catch (Exception e) {
            return new ApiResponse<>(false, "加入购物车失败: " + e.getMessage());
        }
    }

    // 获取购物车列表
    @GetMapping("/items")
    public ApiResponse<List<CartItemDTO>> getCartItems() {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            List<CartItemDTO> cartItems = cartService.getCartItems(currentUserId.intValue());
            return new ApiResponse<>(true, "获取成功", cartItems);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取购物车失败: " + e.getMessage());
        }
    }

    // 更新购物车商品数量
    @PutMapping("/items/{cartItemId}/quantity")
    public ApiResponse<String> updateCartItemQuantity(
            @PathVariable Integer cartItemId,
            @RequestBody Map<String, Integer> payload) {

        Integer quantity = payload.get("quantity");
        if (quantity == null || quantity < 1) {
            return new ApiResponse<>(false, "数量必须大于0");
        }

        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            cartService.updateCartItemQuantity(currentUserId.intValue(), cartItemId, quantity);
            return new ApiResponse<>(true, "数量更新成功");
        } catch (Exception e) {
            return new ApiResponse<>(false, "更新数量失败: " + e.getMessage());
        }
    }

    // 删除购物车商品
    @DeleteMapping("/items/{cartItemId}")
    public ApiResponse<String> deleteCartItem(@PathVariable Integer cartItemId) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            cartService.deleteCartItem(currentUserId.intValue(), cartItemId);
            return new ApiResponse<>(true, "删除成功");
        } catch (Exception e) {
            return new ApiResponse<>(false, "删除失败: " + e.getMessage());
        }
    }

    // 清空购物车
    @DeleteMapping("/clear")
    public ApiResponse<String> clearCart() {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            cartService.clearCart(currentUserId.intValue());
            return new ApiResponse<>(true, "购物车已清空");
        } catch (Exception e) {
            return new ApiResponse<>(false, "清空购物车失败: " + e.getMessage());
        }
    }

    // 获取购物车统计信息
    @GetMapping("/summary")
    public ApiResponse<CartSummaryDTO> getCartSummary() {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            CartSummaryDTO summary = cartService.getCartSummary(currentUserId.intValue());
            return new ApiResponse<>(true, "获取成功", summary);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取统计信息失败: " + e.getMessage());
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
