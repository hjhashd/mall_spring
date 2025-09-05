package com.coding24h.mall_spring.mapper;

import com.coding24h.mall_spring.dto.ImageForReviewDTO;
import com.coding24h.mall_spring.dto.ProductQueryDTO;
import com.coding24h.mall_spring.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface AdminProductMapper {

    /**
     * 根据查询条件查找商品列表
     * @param query 查询 DTO
     * @return 商品列表
     */
    List<Product> findProducts(ProductQueryDTO query);

    /**
     * 根据 ID 查找商品
     * @param productId 商品 ID
     * @return 商品实体
     */
    Product findById(Integer productId);

    /**
     * 插入新商品
     * @param product 商品实体
     * @return 影响行数
     */
    int insertProduct(Product product);

    /**
     * 更新商品
     * @param product 商品实体
     * @return 影响行数
     */
    int updateProduct(Product product);

    /**
     * 删除商品
     * @param productId 商品 ID
     * @return 影响行数
     */
    int deleteProduct(Integer productId);


    //-------------------------
    List<ImageForReviewDTO> findImagesByStatus(@Param("status") Integer status, @Param("query") String query);

    void updateImageReviewStatus(
            @Param("imageId") Integer imageId,
            @Param("status") Integer status,
            @Param("reason") String reason,
            @Param("verifierId") Integer verifierId,
            @Param("verifiedAt") Date verifiedAt
    );

    Integer findProductIdByImageId(@Param("imageId") Integer imageId);

    int countPendingImagesForProduct(@Param("productId") Integer productId);

    int countApprovedImagesForProduct(@Param("productId") Integer productId);
}
