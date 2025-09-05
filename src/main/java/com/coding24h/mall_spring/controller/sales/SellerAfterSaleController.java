package com.coding24h.mall_spring.controller.sales;

import com.coding24h.mall_spring.dto.ApiResponse;
import com.coding24h.mall_spring.dto.order.AfterSaleApplicationDTO;
import com.coding24h.mall_spring.entity.CustomUserDetails;
import com.coding24h.mall_spring.service.SellerAfterSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seller/after-sales")
public class SellerAfterSaleController {

    @Autowired
    private SellerAfterSaleService sellerAfterSaleService;

    /**
     * 获取当前卖家的所有售后申请
     * @return 售后申请列表
     */
    @GetMapping
    public ApiResponse<List<AfterSaleApplicationDTO>> getAfterSaleApplications() {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }
        try {
            List<AfterSaleApplicationDTO> applications = sellerAfterSaleService.getAfterSaleApplicationsBySellerId(currentUserId.intValue());
            return new ApiResponse<>(true, "获取成功", applications);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取售后申请失败: " + e.getMessage());
        }
    }

    /**
     * 卖家处理售后申请（同意或拒绝）
     * @param afterSaleId 售后记录ID
     * @param payload 包含 action ("approve" or "reject") 和 admin_remark
     * @return 处理结果
     */
    @PostMapping("/{afterSaleId}/process")
    public ApiResponse<String> processApplication(@PathVariable Integer afterSaleId, @RequestBody Map<String, String> payload) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        String action = payload.get("action");
        String sellerRemark = payload.get("seller_remark"); // 修改变量名

        if (!"approve".equals(action) && !"reject".equals(action)) {
            return new ApiResponse<>(false, "无效的操作");
        }

        try {
            boolean isApproved = "approve".equals(action);
            sellerAfterSaleService.processApplication(currentUserId.intValue(), afterSaleId, isApproved, sellerRemark); // 传递 sellerRemark
            return new ApiResponse<>(true, "处理成功");
        } catch (Exception e) {
            return new ApiResponse<>(false, "处理失败: " + e.getMessage());
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        }
        return null;
    }
}
