package com.coding24h.mall_spring.controller.sales;

import com.coding24h.mall_spring.dto.ApiResponse;
import com.coding24h.mall_spring.dto.order.AfterSaleApplicationDTO;
import com.coding24h.mall_spring.service.AdminAfterSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/after-sales")
public class AdminAfterSaleController {

    @Autowired
    private AdminAfterSaleService adminAfterSaleService;

    /**
     * 获取所有售后申请（支持按状态筛选）
     * @param status 可选参数，用于筛选特定状态的申请
     * @return 售后申请列表
     */
    @GetMapping
    public ApiResponse<List<AfterSaleApplicationDTO>> getAllApplications(
            @RequestParam(required = false) Integer status) {
        try {
            List<AfterSaleApplicationDTO> applications;
            if (status != null) {
                applications = adminAfterSaleService.getApplicationsByStatus(status);
            } else {
                applications = adminAfterSaleService.getAllApplications();
            }
            return new ApiResponse<>(true, "获取成功", applications);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取申请列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取待管理员处理的售后申请列表
     * @return 待处理的售后申请列表
     */
    @GetMapping("/pending")
    public ApiResponse<List<AfterSaleApplicationDTO>> getPendingApplications() {
        try {
            List<AfterSaleApplicationDTO> applications = adminAfterSaleService.getPendingApplications();
            return new ApiResponse<>(true, "获取成功", applications);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取待处理申请失败: " + e.getMessage());
        }
    }

    /**
     * 管理员强制处理售后申请
     * @param afterSaleId 售后记录ID
     * @param payload 包含 action ("approve" or "reject") 和 admin_remark
     * @return 处理结果
     */
    @PostMapping("/{afterSaleId}/judge")
    public ApiResponse<String> judgeApplication(@PathVariable Integer afterSaleId, @RequestBody Map<String, String> payload) {
        // 假设已经通过安全框架验证了管理员身份
        String action = payload.get("action");
        String adminRemark = payload.get("admin_remark");

        if (!"approve".equals(action) && !"reject".equals(action)) {
            return new ApiResponse<>(false, "无效的操作");
        }
        if (adminRemark == null || adminRemark.trim().isEmpty()) {
            return new ApiResponse<>(false, "管理员备注不能为空");
        }

        try {
            boolean isApproved = "approve".equals(action);
            adminAfterSaleService.judgeApplication(afterSaleId, isApproved, adminRemark);
            return new ApiResponse<>(true, "管理员仲裁成功");
        } catch (Exception e) {
            return new ApiResponse<>(false, "处理失败: " + e.getMessage());
        }
    }
}
