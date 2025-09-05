package com.coding24h.mall_spring.service;

import com.coding24h.mall_spring.dto.ImageForReviewDTO;
import com.coding24h.mall_spring.dto.ImageReviewRequestDTO;
import com.coding24h.mall_spring.dto.ProductQueryDTO;
import com.coding24h.mall_spring.entity.Product;
import com.coding24h.mall_spring.entity.vo.PageResult;
import com.coding24h.mall_spring.mapper.AdminProductMapper;
import com.coding24h.mall_spring.mapper.ContentModerationMapper;
import com.coding24h.mall_spring.mapper.ProductMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AdminProductService {

    @Autowired
    private AdminProductMapper adminProductMapper;
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ContentModerationMapper contentModerationMapper;

    public PageResult<ImageForReviewDTO> getImagesByStatus(Integer status, String query, int page, int size) {
        PageHelper.startPage(page, size);
        // 将 query 参数传递给 Mapper 层
        List<ImageForReviewDTO> images = adminProductMapper.findImagesByStatus(status, query);
        PageInfo<ImageForReviewDTO> pageInfo = new PageInfo<>(images);
        return new PageResult<>(pageInfo.getTotal(), images);
    }
    @Transactional
    public void processImageReviews(List<ImageReviewRequestDTO.ImageReviewItem> reviews, Integer moderatorId) {
        if (reviews == null || reviews.isEmpty()) {
            return;
        }

        for (ImageReviewRequestDTO.ImageReviewItem review : reviews) {
            // 更新单张图片的状态
            adminProductMapper.updateImageReviewStatus(
                    review.getImageId(),
                    review.getStatus(),
                    review.getReason(),
                    moderatorId,
                    new Date()
            );

            // 关键逻辑：在每次图片审核后，检查关联的商品是否满足上架条件
            Integer productId = adminProductMapper.findProductIdByImageId(review.getImageId());
            if (productId != null) {
                checkAndPublishProduct(productId);
            }
        }
    }

    /**
     * 检查并发布商品。
     * 上架条件：
     * 1. 商品本身的文本内容审核已通过 (content_moderations.status = 1)
     * 2. 商品的所有图片都已审核 (product_images.verification_status != 0)
     * 3. 至少有一张图片审核通过 (product_images.verification_status = 1)
     */
    private void checkAndPublishProduct(Integer productId) {
        // 1. 检查文本内容审核状态
        Integer moderationStatus = contentModerationMapper.getStatusByContentIdAndType(productId, "product");
        if (moderationStatus == null || moderationStatus != 1) {
            return; // 文本未通过，直接返回
        }

        // 2. 检查图片审核状态
        int pendingImageCount = adminProductMapper.countPendingImagesForProduct(productId);
        if (pendingImageCount > 0) {
            return; // 还有图片待审核，直接返回
        }

        // 3. 检查是否有通过的图片
        int approvedImageCount = adminProductMapper.countApprovedImagesForProduct(productId);
        if (approvedImageCount == 0) {
            // 所有图片都被拒绝了，可以考虑将商品状态设为审核失败
            // productMapper.updateProductStatus(productId, 5); // 假设 5 是审核失败
            return; // 没有通过的图片，不上架
        }

        // 所有条件满足，更新商品状态为 "上架"
        productMapper.updateProductStatus(productId, 1); // 1 = 上架
    }


    /**
     * 为后台管理系统获取商品列表
     * @param query 查询参数
     * @return 分页后的商品列表
     */
    public PageResult<Product> getProductsForAdmin(ProductQueryDTO query) {
        // 设置分页
        PageHelper.startPage(query.getPage(), query.getPageSize());
        // 执行查询
        List<Product> products = adminProductMapper.findProducts(query);
        // 用 PageInfo 对结果进行包装
        PageInfo<Product> pageInfo = new PageInfo<>(products);

        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 创建商品
     * @param product 商品信息
     * @return 创建后的商品（包含ID）
     */
    @Transactional
    public Product createProduct(Product product) {
        // 设置默认值
        product.setCreatedAt(new Date());
        product.setUpdatedAt(new Date());
        // 可以在这里设置默认的 seller_id
        // product.setSellerId(1); // 假设管理员ID为1
        adminProductMapper.insertProduct(product);
        return product;
    }

    /**
     * 更新商品
     * @param product 商品信息
     * @return 更新后的商品
     */
    @Transactional
    public Product updateProduct(Product product) {
        product.setUpdatedAt(new Date());
        adminProductMapper.updateProduct(product);
        return adminProductMapper.findById(product.getProductId());
    }

    /**
     * 删除商品
     * @param productId 商品ID
     */
    @Transactional
    public void deleteProduct(Integer productId) {
        // 在实际业务中，你可能需要先删除关联的图片、收藏记录等
        adminProductMapper.deleteProduct(productId);
    }
}
