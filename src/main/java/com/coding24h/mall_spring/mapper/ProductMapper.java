package com.coding24h.mall_spring.mapper;

import com.coding24h.mall_spring.dto.ProductDetailDTO;
import com.coding24h.mall_spring.dto.ProductForSellerDTO;
import com.coding24h.mall_spring.dto.ProductQueryDTO;
import com.coding24h.mall_spring.entity.Category;
import com.coding24h.mall_spring.entity.Product;
import com.coding24h.mall_spring.entity.ProductImage;
import com.coding24h.mall_spring.entity.vo.ProductDetailVO;
import com.coding24h.mall_spring.entity.vo.ProductVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductMapper {
    List<ProductVO> searchProducts(
            @Param("query") ProductQueryDTO query,
            @Param("currentUserId") Long currentUserId,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    long countProducts(
            @Param("query") ProductQueryDTO query,
            @Param("currentUserId") Long currentUserId
    );

    // 获取所有启用的顶级分类（parent_id=0）
    List<Category> findAllActiveCategories();

    /**
     * 获取所有启用的分类（或全部分类）
     */
    List<Category> findAllCategoriesTree();

    /**
     * 根据父ID查找子分类
     */
    List<Category> findCategoriesByParentId(Integer parentId);

    /**
     * 插入一个新的分类
     */
    int insertCategory(Category category);

    /**
     * 更新一个分类
     */
    int updateCategory(Category category);

    /**
     * 根据ID删除一个分类
     */
    int deleteCategoryById(Integer categoryId);

    /**
     * 根据商品ID获取商品详情，包含分类、图片等信息
     * @param productId 商品ID
     * @return 商品详情VO
     */
    ProductDetailVO getProductDetailById(@Param("productId") Integer productId);

    /**
     * 更新商品的浏览量
     * @param productId 商品ID
     */
    void incrementViewCount(@Param("productId") Integer productId);


    //根据商品id，优先查找主图，没有主图就取第一张图片
    String selectMainImageByProductId(Integer productId);

    // 根据商品ID查询图片列表
    List<ProductImage> selectImagesByProductId(Integer productId);

    // 更新库存
    void updateStock(@Param("productId") Integer productId, @Param("stock") Integer stock);

    // 更新收藏量
    void updateFavoriteCount(@Param("productId") Integer productId, @Param("increment") Integer increment);

    // 新增方法声明
    void insertProduct(Product product);
    void updateProduct(Product product);

    Product selectById(Integer productId);
    void updateProductStatus(@Param("productId") Integer productId, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM products " +
            "WHERE seller_id = #{sellerId} AND status = 1") // 1=上架
    int countOnSaleProducts(@Param("sellerId") Long sellerId);

    @Select("SELECT SUM(view_count) FROM products " +
            "WHERE seller_id = #{sellerId} " +
            "AND MONTH(updated_at) = MONTH(CURRENT_DATE()) " +
            "AND YEAR(updated_at) = YEAR(CURRENT_DATE())")
    int sumMonthlyViews(@Param("sellerId") Long sellerId);

    void deleteById(Integer productId);  // 添加物理删除方法


    List<ProductForSellerDTO> selectSellerProducts(
            @Param("sellerId") Long sellerId,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    int countSellerProducts(@Param("sellerId") Long sellerId);

    /**
     * 批量插入商品图片
     * @param images 图片列表
     */
    void batchInsertImages(@Param("list") List<ProductImage> images);

    ProductDetailDTO selectProductDetailById(@Param("productId") Integer productId,
                                             @Param("userId") Integer userId);

    long countProducts(ProductQueryDTO query);

}
