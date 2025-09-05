package com.coding24h.mall_spring.controller.admin;

import com.coding24h.mall_spring.dto.ApiResponse;
import com.coding24h.mall_spring.dto.ImageForReviewDTO;
import com.coding24h.mall_spring.dto.ImageReviewRequestDTO;
import com.coding24h.mall_spring.entity.vo.PageResult;
import com.coding24h.mall_spring.service.AdminProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class ProductImageController {

    @Autowired
    private AdminProductService adminService;

    /**
     * 获取图片列表以供审核（支持分页、状态筛选和关键词搜索）
     * @param status 审核状态: 0-未审核, 1-通过, 2-拒绝
     * @param query  搜索关键词 (可选, 可搜索商品ID, 图片ID, 商品标题)
     * @param page   页码
     * @param size   每页数量
     * @return 图片分页结果
     */
    @GetMapping("/images/review-list")
    public ApiResponse<PageResult<ImageForReviewDTO>> getImagesForReview(
            @RequestParam(defaultValue = "0") Integer status,
            @RequestParam(required = false) String query, // 新增: 接收搜索关键词
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        try {
            // 将 query 参数传递给 Service 层
            PageResult<ImageForReviewDTO> result = adminService.getImagesByStatus(status, query, page, size);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("获取图片列表失败: " + e.getMessage());
        }
    }

    /**
     * 批量审核图片
     * @param request 包含多个图片审核结果的请求体
     * @return 操作结果
     */
    @PostMapping("/images/review")
    public ApiResponse<Void> reviewImages(@RequestBody ImageReviewRequestDTO request) {
        // 在真实应用中，这里应该获取当前登录的管理员ID
        Integer moderatorId = 101; // 模拟管理员ID
        try {
            adminService.processImageReviews(request.getReviews(), moderatorId);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error("审核失败: " + e.getMessage());
        }
    }
}
