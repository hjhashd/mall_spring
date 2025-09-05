package com.coding24h.mall_spring.controller;

import com.coding24h.mall_spring.dto.ApiResponse;
import com.coding24h.mall_spring.dto.CertificationDTO;
import com.coding24h.mall_spring.entity.CustomUserDetails;
import com.coding24h.mall_spring.entity.SellerCertification;
import com.coding24h.mall_spring.entity.vo.PageResult;
import com.coding24h.mall_spring.service.SellerCertificationService;
import com.coding24h.mall_spring.service.impl.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/seller-certifications")
public class SellerCertificationController {

    @Autowired
    private SellerCertificationService sellerCertificationService;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * 提交商家认证申请
     */
    @PostMapping("/submit")
    public ApiResponse<String> submitCertification(
            @ModelAttribute CertificationDTO dto) { // 使用@ModelAttribute接收混合类型

        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            if (dto.getBusinessLicense() == null || dto.getBusinessLicense().isEmpty()) {
                return new ApiResponse<>(false, "请上传营业执照");
            }

            String filePath = fileStorageService.storeFile(dto.getBusinessLicense(), "business_licenses");

            // 创建认证申请时传入所有字段
            SellerCertification certification = new SellerCertification(
                    currentUserId,
                    filePath,
                    dto.getBusinessName(),
                    dto.getBusinessType(),
                    dto.getContactPhone(),
                    dto.getContactEmail(),
                    dto.getBusinessAddress(),
                    dto.getBusinessDescription()
            );

            sellerCertificationService.submitCertification(certification);
            return new ApiResponse<>(true, "认证申请提交成功！");
        } catch (Exception e) {
            return new ApiResponse<>(false, "提交失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户的认证状态
     */
    @GetMapping("/status")
    public ApiResponse<SellerCertification> getCertificationStatus() {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            SellerCertification certification = sellerCertificationService.getByUserId(currentUserId);
            if (certification == null) {
                return new ApiResponse<>(true, "暂无认证记录", null);
            }
            return new ApiResponse<>(true, "获取成功", certification);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取认证状态失败: " + e.getMessage());
        }
    }

    /**
     * 管理员审核商家认证
     */
    @PostMapping("/{certificationId}/review")
    public ApiResponse<String> reviewCertification(
            @PathVariable Integer certificationId,
            @RequestBody Map<String, Object> payload) {

        try {
            Integer status = (Integer) payload.get("status"); // 1-通过, 2-拒绝
            String rejectReason = (String) payload.get("reject_reason");

            if (status == null || (status != 1 && status != 2)) {
                return new ApiResponse<>(false, "无效的审核状态");
            }

            if (status == 2 && (rejectReason == null || rejectReason.trim().isEmpty())) {
                return new ApiResponse<>(false, "拒绝时必须填写拒绝原因");
            }

            // 获取当前管理员ID
            Long adminId = getCurrentUserId();

            sellerCertificationService.reviewCertification(certificationId, status, adminId, rejectReason);

            String message = status == 1 ? "认证审核通过" : "认证审核拒绝";
            return new ApiResponse<>(true, message);

        } catch (Exception e) {
            return new ApiResponse<>(false, "审核失败: " + e.getMessage());
        }
    }

    /**
     * 管理员获取所有认证申请列表
     */
    @GetMapping("/admin/list")
    public ApiResponse<PageResult<SellerCertification>> getCertificationList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {

        try {
            PageResult<SellerCertification> result = sellerCertificationService.getCertificationList(page, size, status);
            return new ApiResponse<>(true, "获取成功", result);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前登录用户ID - 基于你提供的示例修改
     */
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
