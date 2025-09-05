package com.coding24h.mall_spring.controller;

import com.coding24h.mall_spring.dto.ApiResponse;
import com.coding24h.mall_spring.dto.ShopSettingsUpdateDTO;
import com.coding24h.mall_spring.entity.CustomUserDetails;
import com.coding24h.mall_spring.entity.Seller;
import com.coding24h.mall_spring.entity.vo.SellerReviewVO;
import com.coding24h.mall_spring.entity.vo.SellerShopVO;
import com.coding24h.mall_spring.entity.vo.ShopStatsVO;
import com.coding24h.mall_spring.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @GetMapping("/stats")
// 使用 @RequestParam(required = false) 来接收一个可选的 sellerId
    public ApiResponse<ShopStatsVO> getShopStats(@RequestParam(required = false) Long sellerId) {

        Long targetSellerId = sellerId;

        // 如果前端没有传递 sellerId，则从当前登录的用户上下文中获取
        if (targetSellerId == null) {
            targetSellerId = getCurrentUserId(); // 这是一个获取当前登录用户ID的方法
            if (targetSellerId == null) {
                return new ApiResponse<>(false, "请先登录", null);
            }
        }

        try {
            // 使用最终确定的 targetSellerId 来查询店铺数据
            ShopStatsVO stats = shopService.getShopStats(targetSellerId);
            return new ApiResponse<>(true, "获取成功", stats);
        } catch (Exception e) {
            // 建议加上日志记录
            // log.error("Error getting shop stats for sellerId: {}", targetSellerId, e);
            return new ApiResponse<>(false, "获取失败: " + e.getMessage());
        }
    }

    // [MODIFIED] 修改此接口以接收可选的 sellerId
    @GetMapping("/SellerShop")
    public ApiResponse<SellerShopVO> getSellerShop(@RequestParam(required = false) Long sellerId) {
        Long targetSellerId = sellerId;
        if (targetSellerId == null) {
            targetSellerId = getCurrentUserId();
            if (targetSellerId == null) {
                return new ApiResponse<>(false, "请先登录", null);
            }
        }
        try {
            SellerShopVO stats = shopService.getSellerShopStats(targetSellerId);
            return new ApiResponse<>(true, "获取成功", stats);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取店铺统计数据失败: " + e.getMessage(), null);
        }
    }

    /**
     * 获取指定卖家或当前登录卖家的所有商品评价
     * [MODIFIED] 修改此接口以接收可选的 sellerId
     */
    @GetMapping("/seller")
    public ApiResponse<List<SellerReviewVO>> getSellerReviews(@RequestParam(required = false) Long sellerId) {
        Long targetSellerId = sellerId;
        if (targetSellerId == null) {
            targetSellerId = getCurrentUserId();
            if (targetSellerId == null) {
                return new ApiResponse<>(false, "请先登录", null);
            }
        }
        try {
            List<SellerReviewVO> reviews = shopService.getReviewsForSeller(targetSellerId);
            return new ApiResponse<>(true, "获取评价成功", reviews);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取评价失败: " + e.getMessage(), null);
        }
    }

    /**
     * 获取店铺设置信息（修改为支持可选sellerId）
     */
    @GetMapping("/settings")
    public ApiResponse<Seller> getShopSettings(@RequestParam(required = false) Long sellerId) {
        Long targetSellerId = sellerId;
        if (targetSellerId == null) {
            targetSellerId = getCurrentUserId();
            if (targetSellerId == null) {
                return new ApiResponse<>(false, "请先登录", null);
            }
        }
        try {
            Seller shopInfo = shopService.getShopInfo(targetSellerId);
            return new ApiResponse<>(true, "获取成功", shopInfo);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取店铺信息失败: " + e.getMessage(), null);
        }
    }

    /**
     * 更新店铺设置
     */
    @PutMapping("/settings")
    public ApiResponse<?> updateShopSettings(@RequestBody ShopSettingsUpdateDTO dto) {
        Long sellerId = getCurrentUserId();
        if (sellerId == null) {
            return new ApiResponse<>(false, "请先登录", null);
        }
        try {
            boolean success = shopService.updateShopSettings(sellerId, dto);
            if (success) {
                return new ApiResponse<>(true, "店铺设置更新成功", null);
            } else {
                return new ApiResponse<>(false, "店铺设置更新失败", null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "更新店铺设置失败: " + e.getMessage(), null);
        }
    }

    /**
     * 上传店铺Logo
     */
    @PostMapping("/uploadLogo")
    public ApiResponse<String> uploadShopLogo(@RequestParam("file") MultipartFile file) {
        Long sellerId = getCurrentUserId();
        if (sellerId == null) {
            return new ApiResponse<>(false, "请先登录", null);
        }
        try {
            String fileUrl = shopService.uploadShopLogo(sellerId, file);
            return new ApiResponse<>(true, "Logo上传成功", fileUrl);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Logo上传失败: " + e.getMessage(), null);
        }
    }

    /**
     * 上传店铺Banner
     */
    @PostMapping("/uploadBanner")
    public ApiResponse<String> uploadShopBanner(@RequestParam("file") MultipartFile file) {
        Long sellerId = getCurrentUserId();
        if (sellerId == null) {
            return new ApiResponse<>(false, "请先登录", null);
        }
        try {
            String fileUrl = shopService.uploadShopBanner(sellerId, file);
            return new ApiResponse<>(true, "Banner上传成功", fileUrl);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Banner上传失败: " + e.getMessage(), null);
        }
    }

    // 提取的获取当前用户ID方法
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        }
        return null;
    }
}
