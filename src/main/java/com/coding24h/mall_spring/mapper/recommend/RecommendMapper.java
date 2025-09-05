package com.coding24h.mall_spring.mapper.recommend;

import com.coding24h.mall_spring.entity.vo.ProductVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecommendMapper {

    /**
     * 查询用户感兴趣的商品分类ID列表
     * @param userId 用户ID
     * @return 感兴趣的分类ID列表
     */
    List<Integer> findUserInterestedCategories(@Param("userId") Long userId);

    /**
     * 查询推荐商品列表
     * @param userId 用户ID
     * @param categoryIds 感兴趣的分类ID列表
     * @param offset 分页偏移量
     * @param pageSize 每页大小
     * @return 推荐商品列表
     */
    List<ProductVO> findRecommendedProducts(
            @Param("userId") Long userId,
            @Param("categoryIds") List<Integer> categoryIds,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    /**
     * 计算推荐商品的总数
     * @param userId 用户ID
     * @param categoryIds 感兴趣的分类ID列表
     * @return 推荐商品总数
     */
    long countRecommendedProducts(
            @Param("userId") Long userId,
            @Param("categoryIds") List<Integer> categoryIds
    );

    /**
     * 查找经常一起购买的商品
     * @param productId 当前商品ID
     * @param currentUserId 当前用户ID
     * @param limit 返回数量限制
     * @return 经常一起购买的商品列表
     */
    List<ProductVO> findProductsFrequentlyBoughtTogether(
            @Param("productId") Integer productId,
            @Param("currentUserId") Long currentUserId,
            @Param("limit") int limit
    );

    /**
     * 根据分类查找相似商品
     * @param categoryId 分类ID
     * @param originalProductId 原始商品ID
     * @param currentUserId 当前用户ID
     * @param offset 分页偏移量
     * @param size 每页大小
     * @return 相似商品列表
     */
    List<ProductVO> findSimilarProductsByCategory(
            @Param("categoryId") Integer categoryId,
            @Param("originalProductId") Integer originalProductId,
            @Param("currentUserId") Long currentUserId,
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * 统计同类商品数量
     * @param categoryId 分类ID
     * @param originalProductId 原始商品ID
     * @param currentUserId 当前用户ID
     * @return 同类商品总数
     */
    long countSimilarProductsByCategory(
            @Param("categoryId") Integer categoryId,
            @Param("originalProductId") Integer originalProductId,
            @Param("currentUserId") Long currentUserId
    );


    /**
     * 查询指定用户购物车中的所有商品ID
     * @param userId 用户ID
     * @return 商品ID列表
     */
    List<Integer> findProductIdsInCart(@Param("userId") Long userId);

    /**
     * 查找与购物车商品最常搭配的商品（分页）
     * @param currentUserId 当前用户ID（用于排除自己的商品）
     * @param cartProductIds 购物车中的商品ID集合
     * @param offset 分页偏移量
     * @param pageSize 每页大小
     * @return 推荐商品列表
     */
    List<ProductVO> findComplementaryProductsForCart(
            @Param("currentUserId") Long currentUserId,
            @Param("cartProductIds") List<Integer> cartProductIds,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);

    /**
     * 统计可推荐的搭配商品总数
     * @param currentUserId 当前用户ID
     * @param cartProductIds 购物车中的商品ID集合
     * @return 总数
     */
    long countComplementaryProductsForCart(
            @Param("currentUserId") Long currentUserId,
            @Param("cartProductIds") List<Integer> cartProductIds);
}
