package com.coding24h.mall_spring.controller.admin;

import com.coding24h.mall_spring.dto.ApiResponse;
import com.coding24h.mall_spring.dto.ProductQueryDTO;
import com.coding24h.mall_spring.entity.Product;
import com.coding24h.mall_spring.entity.vo.PageResult;
import com.coding24h.mall_spring.service.AdminProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    @Autowired
    private AdminProductService adminProductService;

    /**
     * 获取商品列表（后台管理）
     * 支持分页、搜索、分类和状态筛选
     */
    @GetMapping
    public ApiResponse<PageResult<Product>> getProducts(@RequestParam(defaultValue = "1") Integer page,
                                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                                        ProductQueryDTO query) {
        try {
            // 设置分页参数
            query.setPage(page);
            query.setPageSize(pageSize);

            PageResult<Product> result = adminProductService.getProductsForAdmin(query);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("获取商品列表失败: " + e.getMessage());
        }
    }

    /**
     * 创建新商品
     */
    @PostMapping
    public ApiResponse<Product> createProduct(@RequestBody Product product) {
        try {
            Product createdProduct = adminProductService.createProduct(product);
            return ApiResponse.success(createdProduct);
        } catch (Exception e) {
            return ApiResponse.error("创建商品失败: " + e.getMessage());
        }
    }

    /**
     * 更新商品信息
     */
    @PutMapping("/{id}")
    public ApiResponse<Product> updateProduct(@PathVariable("id") Integer id, @RequestBody Product product) {
        try {
            product.setProductId(id);
            Product updatedProduct = adminProductService.updateProduct(product);
            return ApiResponse.success(updatedProduct);
        } catch (Exception e) {
            return ApiResponse.error("更新商品失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable("id") Integer id) {
        try {
            adminProductService.deleteProduct(id);
            // 只传入数据，使用默认消息
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error("删除商品失败: " + e.getMessage());
        }
    }
}
