package com.coding24h.mall_spring.controller;

import com.coding24h.mall_spring.dto.ApiResponse;
import com.coding24h.mall_spring.dto.order.AfterSaleRequestDTO; // 新增导入
import com.coding24h.mall_spring.dto.order.OrderDTO;
import com.coding24h.mall_spring.dto.order.OrderListDTO;
import com.coding24h.mall_spring.dto.order.CheckoutRequestDTO;
import com.coding24h.mall_spring.entity.CustomUserDetails;
import com.coding24h.mall_spring.entity.ShippingCompany;
import com.coding24h.mall_spring.service.AfterSaleService;
import com.coding24h.mall_spring.service.OrderService;
// 假设你有一个 AfterSaleService 来处理售后逻辑
// import com.coding24h.mall_spring.service.AfterSaleService;
import com.coding24h.mall_spring.service.ShippingCompanyService;
import com.coding24h.mall_spring.service.impl.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api") // 将根路径改为 /api，让路由更清晰
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 注入 AfterSaleService
     @Autowired
     private AfterSaleService afterSaleService;

     @Autowired
     private FileStorageService fileStorageService; // 注入文件存储服务

    @Autowired
    private ShippingCompanyService shippingCompanyService;

    // ===============================================================
    // ====================== 新增售后申请接口 =========================
    // ===============================================================
    /**
     * 统一处理售后申请，包含文件上传和表单数据
     * @param orderItemId 订单项ID
     * @param type 售后类型
     * @param reason 申请原因
     * @param refundAmount 退款金额 (可选)
     * @param files 上传的凭证文件 (可选)
     * @return ApiResponse
     */
    @PostMapping("/after-sales/apply")
    public ApiResponse<String> submitAfterSaleApplication(
            // 4. 使用 @RequestParam 接收表单字段
            @RequestParam("order_item_id") Integer orderItemId,
            @RequestParam("type") Integer type,
            @RequestParam("reason") String reason,
            @RequestParam(value = "refund_amount", required = false) Double refundAmount,
            // 5. 使用 @RequestParam 接收文件数组
            @RequestParam(value = "evidence_images", required = false) MultipartFile[] files) {

        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            List<String> imageUrls = new ArrayList<>();
            // 6. 处理文件上传
            if (files != null && files.length > 0) {
                // 遍历文件数组，逐个上传
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        // 调用文件服务存储文件，存储在 "after-sales-evidence" 目录下
                        String fileUrl = fileStorageService.storeFile(file, "after-sales-evidence");
                        imageUrls.add(fileUrl);
                    }
                }
            }

            // 7. 组装 DTO 对象
            AfterSaleRequestDTO request = new AfterSaleRequestDTO();
            request.setOrderItemId(orderItemId);
            request.setType(type);
            request.setReason(reason);
            request.setRefundAmount(refundAmount);
            // 将图片URL列表合并为逗号分隔的字符串
            if (!imageUrls.isEmpty()) {
                request.setEvidenceImages(String.join(",", imageUrls));
            } else {
                request.setEvidenceImages(null);
            }

            // 8. 调用业务层服务
            afterSaleService.createApplication(currentUserId.intValue(), request);

            return new ApiResponse<>(true, "售后申请提交成功");
        } catch (Exception e) {
            return new ApiResponse<>(false, "提交失败: " + e.getMessage());
        }
    }


    // 【新增】获取单个订单项详情的接口，供售后页面使用
    @GetMapping("/order/item/{orderItemId}")
    public ApiResponse<Object> getOrderItemDetail(@PathVariable Integer orderItemId) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }
        try {
            Object itemDetail = orderService.getOrderItemDetailForUser(currentUserId.intValue(), orderItemId);
            if (itemDetail != null) {
                return new ApiResponse<>(true, "获取成功", itemDetail);
            } else {
                return new ApiResponse<>(false, "未找到该商品或无权访问");
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取商品详情失败: " + e.getMessage());
        }
    }
    /**
     * 用户对被拒绝的售后申请提起申诉
     * @param afterSaleId 售后记录ID
     * @return 操作结果
     */
    @PostMapping("/user/after-sales/{afterSaleId}/appeal")
    public ApiResponse<String> appealAfterSale(@PathVariable Integer afterSaleId) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            afterSaleService.appealToPlatform(currentUserId.intValue(), afterSaleId);
            return new ApiResponse<>(true, "申诉已提交，平台将介入处理");
        } catch (Exception e) {
            return new ApiResponse<>(false, "申诉失败: " + e.getMessage());
        }
    }
    // 取消订单
    @PostMapping("/order/{orderId}/cancel")
    public ApiResponse<String> cancelOrder(@PathVariable String orderId) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            orderService.cancelOrder(currentUserId.intValue(), orderId);
            return new ApiResponse<>(true, "订单取消成功");
        } catch (Exception e) {
            return new ApiResponse<>(false, "取消订单失败: " + e.getMessage());
        }
    }


    // 确认收货
    @PostMapping("/order/{orderId}/confirm-receive")
    public ApiResponse<String> confirmReceive(@PathVariable String orderId) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            orderService.confirmReceive(currentUserId.intValue(), orderId);
            return new ApiResponse<>(true, "确认收货成功");
        } catch (Exception e) {
            return new ApiResponse<>(false, "确认收货失败: " + e.getMessage());
        }
    }

    // 提交订单（结账）- 支持多卖家
    @PostMapping("/order/checkout")
    public ApiResponse<List<OrderDTO>> checkout(@RequestBody CheckoutRequestDTO request) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            List<OrderDTO> orders = orderService.processMultiSellerCheckout(
                    currentUserId.intValue(),
                    request.getPaymentMethod(),
                    request.getAddressId(),
                    request.getRemark(),
                    request.getShippingCompany()
            );
            return new ApiResponse<>(true, "订单创建成功", orders);
        } catch (Exception e) {
            return new ApiResponse<>(false, "创建订单失败: " + e.getMessage());
        }
    }

    // 获取物流公司列表
    @GetMapping("/order/shipping-companies")
    public ApiResponse<List<ShippingCompany>> getShippingCompanies() {
        try {
            List<ShippingCompany> companies = shippingCompanyService.getActiveCompanies();
            return new ApiResponse<>(true, "获取成功", companies);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取物流公司失败: " + e.getMessage());
        }
    }

    // 获取订单列表
    @GetMapping("/order/list")
    public ApiResponse<List<OrderListDTO>> getOrderList() {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            List<OrderListDTO> orders = orderService.getOrderList(currentUserId.intValue());
            return new ApiResponse<>(true, "获取成功", orders);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取订单列表失败: " + e.getMessage());
        }
    }

    // 获取订单详情
    @GetMapping("/order/{orderId}")
    public ApiResponse<OrderDTO> getOrderDetail(@PathVariable String orderId) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            OrderDTO order = orderService.getOrderDetail(currentUserId.intValue(), orderId);
            return new ApiResponse<>(true, "获取成功", order);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取订单详情失败: " + e.getMessage());
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
