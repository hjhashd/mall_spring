package com.coding24h.mall_spring.controller;

import com.coding24h.mall_spring.dto.ApiResponse;
import com.coding24h.mall_spring.dto.review.ReplySubmitDTO;
import com.coding24h.mall_spring.dto.review.ReviewQueryDTO;
import com.coding24h.mall_spring.dto.review.ReviewSubmitDTO;
import com.coding24h.mall_spring.entity.CustomUserDetails;
import com.coding24h.mall_spring.entity.Review;
import com.coding24h.mall_spring.entity.vo.RatingSummaryVO;
import com.coding24h.mall_spring.entity.vo.ReplyVO;
import com.coding24h.mall_spring.entity.vo.ReviewVO;
import com.coding24h.mall_spring.service.ReplyService;
import com.coding24h.mall_spring.service.ReviewService;
import com.coding24h.mall_spring.service.impl.FileStorageService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 评价控制器
 */
@RestController
@RequestMapping("/api")
@Validated
public class ReviewController {

    private final ReviewService reviewService;
    private final ReplyService replyService;

    @Autowired
    private FileStorageService fileStorageService;

    // 构造函数注入
    public ReviewController(ReviewService reviewService, ReplyService replyService) {
        this.reviewService = reviewService;
        this.replyService = replyService;
    }

    /**
     * 获取所有商品评价列表 (用于管理后台)
     * 推荐为该接口添加管理员权限校验
     * 【关键修改】增加 sellerId 参数
     */
    @GetMapping("/reviews/all")
    public ApiResponse<PageInfo<ReviewVO>> getAllReviews(@RequestParam(defaultValue = "1") Integer page,
                                                         @RequestParam(defaultValue = "10") Integer size,
                                                         @RequestParam(required = false) Integer rating,
                                                         @RequestParam(defaultValue = "created_at") String sort,
                                                         @RequestParam(defaultValue = "desc") String order,
                                                         @RequestParam(required = false) Boolean hasImage,
                                                         @RequestParam(required = false) Boolean hasSellerReply,
                                                         @RequestParam(required = false) Long sellerId) { // 1. 接收 sellerId
        try {
            ReviewQueryDTO queryDTO = new ReviewQueryDTO();
            queryDTO.setPage(page);
            queryDTO.setSize(size);
            queryDTO.setRating(rating);
            queryDTO.setSort(sort);
            queryDTO.setOrder(order);
            queryDTO.setHasImage(hasImage);
            queryDTO.setHasSellerReply(hasSellerReply);
            queryDTO.setSellerId(sellerId); // 2. 将 sellerId 放入 DTO

            PageInfo<ReviewVO> result = reviewService.getAllReviews(queryDTO);
            return new ApiResponse<>(true, "获取所有评价成功", result);
        } catch (Exception e) {
            System.err.println("获取所有评价失败: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse<>(false, "获取所有评价失败: " + e.getMessage(), null);
        }
    }

    /**
     * 获取商品评价列表
     */
    @GetMapping("/products/{productId}/reviews")
    public ApiResponse<PageInfo<ReviewVO>> getProductReviews(@PathVariable Integer productId,
                                                             @RequestParam(defaultValue = "1") Integer page,
                                                             @RequestParam(defaultValue = "10") Integer size,
                                                             @RequestParam(required = false) Integer rating,
                                                             @RequestParam(defaultValue = "created_at") String sort,
                                                             @RequestParam(defaultValue = "desc") String order,
                                                             @RequestParam(required = false) Boolean hasImage,
                                                             @RequestParam(required = false) Boolean hasSellerReply) {
        try {
            ReviewQueryDTO queryDTO = new ReviewQueryDTO();
            queryDTO.setProductId(productId);
            queryDTO.setPage(page);
            queryDTO.setSize(size);
            queryDTO.setRating(rating);
            queryDTO.setSort(sort);
            queryDTO.setOrder(order);
            queryDTO.setHasImage(hasImage);
            queryDTO.setHasSellerReply(hasSellerReply);

            PageInfo<ReviewVO> result = reviewService.getProductReviews(queryDTO);
            return new ApiResponse<>(true, "获取商品评价成功", result);
        } catch (Exception e) {
            System.err.println("获取商品评价失败: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse<>(false, "获取商品评价失败: " + e.getMessage(), null);
        }
    }
    /**
     * 获取商品评分统计
     */
    @GetMapping("/products/{productId}/rating-summary")
    public ApiResponse<RatingSummaryVO> getProductRatingSummary(@PathVariable Integer productId) {
        try {
            RatingSummaryVO ratingSummary = reviewService.getProductRatingSummary(productId);
            if (ratingSummary != null) {
                return new ApiResponse<>(true, "获取评分统计成功", ratingSummary);
            } else {
                return new ApiResponse<>(false, "未找到该商品的评分信息", null);
            }
        } catch (Exception e) {
            System.err.println("获取评分统计失败: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse<>(false, "获取评分统计失败: " + e.getMessage());
        }
    }

    /**
     * 提交商品评价
     */
    @PostMapping("/reviews")
    public ApiResponse<String> submitReview(@ModelAttribute ReviewSubmitDTO dto) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录");
        }

        try {
            List<String> imageUrls = new ArrayList<>();
            if (dto.getImages() != null && !dto.getImages().isEmpty()) {
                for (MultipartFile image : dto.getImages()) {
                    if (image != null && !image.isEmpty()) {
                        // 直接调用 Service，让其负责所有校验
                        String imageUrl = fileStorageService.storeFile(image, "review_images");
                        imageUrls.add(imageUrl);
                    }
                }
            }

            dto.setImageUrls(String.join(",", imageUrls));
            boolean success = reviewService.submitReview(dto, currentUserId);
            if (success) {
                return new ApiResponse<>(true, "评价提交成功！");
            } else {
                return new ApiResponse<>(false, "评价提交失败，可能已评价过该订单项");
            }
        } catch (Exception e) {
            System.err.println("评价提交失败: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse<>(false, "评价提交失败: " + e.getMessage());
        }
    }

    /**
     * 删除评价
     */
    @DeleteMapping("/reviews/{reviewId}")
    public ApiResponse<Object> deleteReview(@PathVariable Integer reviewId) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "用户未登录", null);
        }

        try {
            boolean success = reviewService.deleteReview(reviewId, currentUserId);
            if (success) {
                return new ApiResponse<>(true, "评价删除成功", null);
            } else {
                return new ApiResponse<>(false, "评价删除失败，可能无权限或评价不存在", null);
            }
        } catch (Exception e) {
            System.err.println("评价删除失败: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse<>(false, "评价提交失败: " + e.getMessage());
        }
    }

    /**
     * 获取评价详情
     */
    @GetMapping("/reviews/{reviewId}")
    public ApiResponse<ReviewVO> getReviewDetail(@PathVariable Integer reviewId) {
        try {
            ReviewVO review = reviewService.getReviewDetail(reviewId);
            if (review != null) {
                return new ApiResponse<>(true, "获取评价详情成功", review);
            } else {
                return new ApiResponse<>(false, "未找到该评价", null);
            }
        } catch (Exception e) {
            System.err.println("获取评价详情失败: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse<>(false, "获取评价详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户评价列表
     */
    @GetMapping("/users/reviews")
    public ApiResponse<PageInfo<ReviewVO>> getUserReviews(@RequestParam(defaultValue = "1") Integer page,
                                                          @RequestParam(defaultValue = "10") Integer size) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "用户未登录", null);
        }

        try {
            PageInfo<ReviewVO> result = reviewService.getUserReviews(currentUserId, page, size);
            return new ApiResponse<>(true, "获取用户评价成功", result);
        } catch (Exception e) {
            System.err.println("获取用户评价失败: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse<>(false, "获取用户评价失败: " + e.getMessage());
        }
    }

    /**
     * 获取待评价订单列表
     */
    @GetMapping("/reviews/pending")
    public ApiResponse<List<Review>> getPendingReviews(
            @RequestParam(required = false) Long productId) { // 添加可选参数

        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "用户未登录", null);
        }

        try {
            List<Review> result = reviewService.getPendingReviews(currentUserId, productId);
            return new ApiResponse<>(true, "获取待评价订单成功", result);
        } catch (Exception e) {
            System.err.println("获取待评价订单失败: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse<>(false, "获取待评价订单失败: " + e.getMessage());
        }
    }

    /**
     * 添加卖家回复
     */
    @PostMapping("/reviews/{reviewId}/seller-reply")
    public ApiResponse<Object> addSellerReply(@PathVariable Integer reviewId,
                                              @RequestParam String sellerReply) {
        try {
            boolean success = reviewService.addSellerReply(reviewId, sellerReply);
            if (success) {
                return new ApiResponse<>(true, "卖家回复添加成功", null);
            } else {
                return new ApiResponse<>(false, "卖家回复添加失败", null);
            }
        } catch (Exception e) {
            System.err.println("卖家回复添加失败: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse<>(false, "卖家回复添加失败: " + e.getMessage());
        }
    }

    /**
     * 提交回复
     */
    @PostMapping("/replies")
    public ApiResponse<Object> submitReply( @RequestBody ReplySubmitDTO replyDTO) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "用户未登录", null);
        }
        try {
            boolean success = replyService.submitReply(replyDTO, currentUserId);
            if (success) {
                return new ApiResponse<>(true, "回复提交成功", null);
            } else {
                return new ApiResponse<>(false, "回复提交失败", null);
            }
        } catch (Exception e) {
            System.err.println("回复提交失败: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse<>(false, "回复提交失败: " + e.getMessage());
        }
    }

    /**
     * 删除回复
     */
    @DeleteMapping("/replies/{replyId}")
    public ApiResponse<Object> deleteReply(@PathVariable Integer replyId) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "用户未登录", null);
        }

        try {
            boolean success = replyService.deleteReply(replyId, currentUserId);
            if (success) {
                return new ApiResponse<>(true, "回复删除成功", null);
            } else {
                return new ApiResponse<>(false, "回复删除失败，可能无权限或回复不存在", null);
            }
        } catch (Exception e) {
            System.err.println("回复删除失败: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse<>(false, "回复删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取评价的回复列表
     */
    @GetMapping("/reviews/{reviewId}/replies")
    public ApiResponse<List<ReplyVO>> getRepliesByReviewId(@PathVariable Integer reviewId) {
        try {
            List<ReplyVO> result = replyService.getRepliesByReviewId(reviewId);
            return new ApiResponse<>(true, "获取回复列表成功", result);
        } catch (Exception e) {
            System.err.println("获取回复列表失败: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse<>(false, "获取回复列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        }
        return null;
    }
}
