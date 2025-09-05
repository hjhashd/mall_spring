package com.coding24h.mall_spring.controller;

import com.coding24h.mall_spring.dto.ApiResponse;
import com.coding24h.mall_spring.dto.order.OrderListDTO;
import com.coding24h.mall_spring.dto.order.ShipmentInfoDTO;
import com.coding24h.mall_spring.entity.CustomUserDetails;
import com.coding24h.mall_spring.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller")
public class SellerOrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 获取当前登录卖家的所有订单列表
     */
    @GetMapping("/orders")
    public ApiResponse<List<OrderListDTO>> getSellerOrders() {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            List<OrderListDTO> orders = orderService.getOrdersBySellerId(currentUserId.intValue());
            return new ApiResponse<>(true, "获取卖家订单成功", orders);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取卖家订单失败: " + e.getMessage());
        }
    }

    /**
     * 为指定订单发货
     * @param orderId 订单ID
     * @param shipmentInfoDTO 包含物流公司和运单号的请求体
     */
    @PostMapping("/orders/{orderId}/ship")
    public ApiResponse<String> shipOrder(@PathVariable String orderId, @RequestBody ShipmentInfoDTO shipmentInfoDTO) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            orderService.shipOrder(
                    currentUserId.intValue(),
                    orderId,
                    shipmentInfoDTO.getShippingCompany(),
                    shipmentInfoDTO.getTrackingNumber()
            );
            return new ApiResponse<>(true, "发货成功");
        } catch (Exception e) {
            return new ApiResponse<>(false, "发货失败: " + e.getMessage());
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
