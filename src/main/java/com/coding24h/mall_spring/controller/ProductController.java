package com.coding24h.mall_spring.controller;

import com.coding24h.mall_spring.dto.*;
import com.coding24h.mall_spring.entity.Category;
import com.coding24h.mall_spring.entity.CustomUserDetails;
import com.coding24h.mall_spring.entity.vo.PageResult;
import com.coding24h.mall_spring.entity.vo.ProductDetailVO;
import com.coding24h.mall_spring.entity.vo.ProductVO;
import com.coding24h.mall_spring.jwt.JwtTokenUtil;
import com.coding24h.mall_spring.service.ProductService;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping("/search")
    public ApiResponse<ProductSearchResponse> searchProducts(
            @ModelAttribute ProductQueryDTO query,
            HttpServletRequest request) {

        Long currentUserId = getCurrentUserId();

        try {
            ProductSearchResponse result = productService.searchProducts(query, currentUserId);
            return new ApiResponse<>(true, "搜索成功", result);
        } catch (Exception e) {
            return new ApiResponse<>(false, "搜索失败: " + e.getMessage());
        }
    }

    // 新增：推荐商品接口
    @GetMapping("/recommendations")
    public ApiResponse<ProductSearchResponse> getRecommendations(@ModelAttribute ProductQueryDTO query) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录后才能使用推荐功能", null);
        }

        try {
            ProductSearchResponse result = productService.getRecommendedProducts(query, currentUserId);
            return new ApiResponse<>(true, "获取推荐商品成功", result);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取推荐商品失败: " + e.getMessage(), null);
        }
    }

    /**
     * "看了又看" - 获取相似商品推荐
     * @param productId 当前正在浏览的商品ID
     * @param page      页码
     * @param size      每页数量
     * @return 相似商品列表
     */
    @GetMapping("/{productId}/similar")
    public ApiResponse<ProductSearchResponse> findSimilarProducts(
            @PathVariable Integer productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size // 相似推荐通常数量较少
    ) {
        Long currentUserId = getCurrentUserId(); // 获取用户ID以优化推荐（例如排除已购买）
        try {
            ProductSearchResponse result = productService.findSimilarProducts(productId, currentUserId, page, size);
            return new ApiResponse<>(true, "获取相似商品成功", result);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取相似商品失败: " + e.getMessage());
        }
    }

    /**
     * "购物车配套推荐" - 根据购物车内容获取推荐商品
     * @param query 分页参数
     * @return 推荐的搭配商品列表
     */
    @GetMapping("/cart/recommendations")
    public ApiResponse<ProductSearchResponse> getCartRecommendations(@ModelAttribute ProductQueryDTO query) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录", null);
        }

        try {
            ProductSearchResponse result = productService.getCartRecommendations(query, currentUserId);
            return new ApiResponse<>(true, "获取购物车推荐成功", result);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取购物车推荐失败: " + e.getMessage(), null);
        }
    }

    @GetMapping("/categories")
    public ApiResponse<List<Category>> getAllCategories() {
        try {
            List<Category> categories = productService.getAllActiveCategories();
            return new ApiResponse<>(true, "获取分类成功", categories);
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取分类失败: " + e.getMessage());
        }
    }

    @GetMapping("/categoriesTree")
    public ApiResponse<List<Category>> getAllCategoriesTree() {
        try {
            // 调用新的方法获取树形结构的分类数据
            List<Category> categories = productService.getAllCategoriesAsTree();
            return new ApiResponse<>(true, "获取分类成功", categories);
        } catch (Exception e) {
            // 建议记录日志
            // log.error("Error fetching categories", e);
            return new ApiResponse<>(false, "获取分类失败: " + e.getMessage());
        }
    }

    /**
     * 新增：创建分类接口
     */
    @PostMapping("/categories")
    public ApiResponse<Category> createCategory(@RequestBody Category category) {
        try {
            Category createdCategory = productService.createCategory(category);
            return new ApiResponse<>(true, "创建分类成功", createdCategory);
        } catch (Exception e) {
            return new ApiResponse<>(false, "创建分类失败: " + e.getMessage());
        }
    }

    /**
     * 新增：更新分类接口
     */
    @PutMapping("/categories/{id}")
    public ApiResponse<Void> updateCategory(@PathVariable Integer id, @RequestBody Category category) {
        try {
            category.setCategoryId(id);
            int result = productService.updateCategory(category);
            if (result > 0) {
                return new ApiResponse<>(true, "更新分类成功", null);
            } else {
                return new ApiResponse<>(false, "更新分类失败或未找到该分类", null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "更新分类失败: " + e.getMessage());
        }
    }

    /**
     * 新增：删除分类接口
     */
    @DeleteMapping("/categories/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Integer id) {
        try {
            productService.deleteCategoryAndChildren(id);
            return new ApiResponse<>(true, "删除分类成功", null);
        } catch (Exception e) {
            return new ApiResponse<>(false, "删除分类失败: " + e.getMessage());
        }
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductDetailVO> getProductDetail(@PathVariable Integer productId) {
        Long currentUserId = getCurrentUserId();

        try {
            ProductDetailVO productDetail = productService.getProductDetailById(productId, currentUserId);
            if (productDetail != null) {
                return new ApiResponse<>(true, "获取商品详情成功", productDetail);
            } else {
                return new ApiResponse<>(false, "未找到该商品", null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "获取商品详情失败: " + e.getMessage());
        }
    }

    @PostMapping("/{productId}/favorite")
    public ApiResponse<String> toggleFavorite(@PathVariable Integer productId) {
        Long currentUserId = getCurrentUserId();

        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录", null);
        }

        try {
            productService.toggleFavorite(currentUserId, productId);
            return new ApiResponse<>(true, "收藏状态已更新");
        } catch (Exception e) {
            return new ApiResponse<>(false, "操作失败: " + e.getMessage());
        }
    }

    @GetMapping("/favorites")
    public ApiResponse<PageResult<FavoriteProductDTO>> getUserFavorites(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer size,
            @RequestParam(defaultValue = "newest") String sort
    ) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return new ApiResponse<>(false, "请先登录", null);
        }

        PageResult<FavoriteProductDTO> result = productService.listUserFavorites(currentUserId, page, size, sort);
        return new ApiResponse<>(true, "OK", result);
    }

    @PostMapping("/create")
    public ApiResponse<Integer> createProduct(@RequestBody ProductFormDTO productForm) {
        Long sellerId = getCurrentUserId();
        if (sellerId == null) {
            return new ApiResponse<>(false, "请先登录", null);
        }

        try {
            // 安全地将 Long 转换为 Integer
            Integer sellerIdInt = Math.toIntExact(sellerId);
            Integer productId = productService.createProduct(sellerIdInt, productForm);
            return new ApiResponse<>(true, "商品添加成功", productId);
        } catch (ArithmeticException e) {
            // 处理转换溢出异常
            return new ApiResponse<>(false, "用户ID超出范围", null);
        } catch (Exception e) {
            return new ApiResponse<>(false, "添加失败: " + e.getMessage(), null);
        }
    }


    @DeleteMapping("/{productId}")
    public ApiResponse<String> deleteProduct(@PathVariable Integer productId) {
        Long sellerId = getCurrentUserId();
        if (sellerId == null) {
            return new ApiResponse<>(false, "请先登录", null);
        }

        try {
            productService.deleteProduct(sellerId, productId);
            return new ApiResponse<>(true, "商品删除成功");
        } catch (Exception e) {
            return new ApiResponse<>(false, "删除失败: " + e.getMessage());
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


    @PostMapping("/submit-product-review")
    public ApiResponse<Integer> submitForReview(
            @ModelAttribute ProductSubmitDTO dto,
            HttpServletRequest request) {

        Long sellerId = getCurrentUserId();
        if (sellerId == null) {
            return new ApiResponse<>(false, "请先登录", null);
        }

        try {
            // 处理主图索引（确保在合理范围内）
            if (dto.getImages() != null && !dto.getImages().isEmpty()) {
                int maxIndex = dto.getImages().size() - 1;
                if (dto.getMainImageIndex() < 0 || dto.getMainImageIndex() > maxIndex) {
                    dto.setMainImageIndex(0); // 索引无效时重置为第一张
                }
            } else {
                dto.setMainImageIndex(0); // 没有图片时设为0
            }

            // 后续处理不变
            Integer sellerIdInt = Math.toIntExact(sellerId);
            Integer productId = productService.submitProductForReview(dto, sellerIdInt);
            return new ApiResponse<>(true, "商品已提交审核", productId);
        } catch (Exception e) {
            return new ApiResponse<>(false, "提交失败: " + e.getMessage(), null);
        }
    }

    @GetMapping("/ForSeller")
    public ApiResponse<PageResult<ProductForSellerDTO>> getSellerProducts(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        Long sellerId = getCurrentUserId();

        return productService.getSellerProducts(sellerId, page, size);
    }

    // 在控制器中添加编辑方法
    @PutMapping("/update-product/{productId}")
    public ApiResponse<Integer> updateProduct(
            @PathVariable Integer productId,
            @ModelAttribute ProductSubmitDTO dto,
            HttpServletRequest request) {

        Long sellerId = getCurrentUserId();
        if (sellerId == null) {
            return new ApiResponse<>(false, "请先登录", null);
        }

        try {
            // 处理主图索引
            if (dto.getImages() != null && !dto.getImages().isEmpty()) {
                int maxIndex = dto.getImages().size() - 1;
                if (dto.getMainImageIndex() < 0 || dto.getMainImageIndex() > maxIndex) {
                    dto.setMainImageIndex(0);
                }
            } else {
                dto.setMainImageIndex(0);
            }

            Integer sellerIdInt = Math.toIntExact(sellerId);
            Integer updatedProductId = productService.updateProduct(productId, dto, sellerIdInt);
            return new ApiResponse<>(true, "商品更新成功", updatedProductId);
        } catch (Exception e) {
            return new ApiResponse<>(false, "更新失败: " + e.getMessage(), null);
        }
    }

    /**
     * 获取商品主图
     * @param productId 商品ID
     * @return 主图URL
     */
    @GetMapping("/{productId}/main-image")
    public ApiResponse<String> getMainImageByProductId(@PathVariable Integer productId) {
        try {
            String mainImage = productService.getMainImageByProductId(productId);
            if (mainImage != null) {
                return ApiResponse.success(mainImage);
            } else {
                return ApiResponse.success(null);
            }
        } catch (Exception e) {
            return ApiResponse.error("获取主图失败：" + e.getMessage());
        }
    }
}
