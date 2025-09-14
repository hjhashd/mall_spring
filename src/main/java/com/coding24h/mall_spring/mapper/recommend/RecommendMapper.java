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
     * 随机查询一个推荐商品
     * @param userId 用户ID
     * @param categoryIds 感兴趣的分类ID列表
     * @return 单个推荐商品
     */
    ProductVO findRecommendedProduct(
            @Param("userId") Long userId,
            @Param("categoryIds") List<Integer> categoryIds
    );

    /**
     * 随机查找一个经常一起购买的商品
     * @param productId 当前商品ID
     * @param currentUserId 当前用户ID
     * @return 单个商品
     */
    ProductVO findProductFrequentlyBoughtTogether(
            @Param("productId") Integer productId,
            @Param("currentUserId") Long currentUserId
    );

    /**
     * 根据分类随机查找一个相似商品
     * @param categoryId 分类ID
     * @param originalProductId 原始商品ID
     * @param currentUserId 当前用户ID
     * @return 单个相似商品
     */
    ProductVO findSimilarProductByCategory(
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
     * 随机查找一个与购物车商品最常搭配的商品
     * @param currentUserId 当前用户ID（用于排除自己的商品）
     * @param cartProductIds 购物车中的商品ID集合
     * @return 单个推荐商品
     */
    ProductVO findComplementaryProductForCart(
            @Param("currentUserId") Long currentUserId,
            @Param("cartProductIds") List<Integer> cartProductIds
    );

    /**
     * 根据多个分类，随机查找一个相似商品
     * @param userId 当前用户ID
     * @param categoryIds 分类ID列表
     * @param excludeProductIds 需要排除的商品ID列表
     * @return 单个商品
     */
    ProductVO findSimilarProductByCategories(
            @Param("currentUserId") Long userId,
            @Param("categoryIds") List<Integer> categoryIds,
            @Param("excludeProductIds") List<Integer> excludeProductIds
    );

    /**
     * 随机查询任何一个可用的商品（用于最终兜底策略）
     * @param currentUserId 当前用户ID
     * @param excludeProductIds 需要排除的商品ID列表
     * @return 单个随机商品
     */
    ProductVO findAnyAvailableProduct(
            @Param("currentUserId") Long currentUserId,
            @Param("excludeProductIds") List<Integer> excludeProductIds
    );

    /**
     * 根据商品ID列表查询它们所属的、不重复的分类ID列表
     * @param productIds 商品ID列表
     * @return 分类ID列表
     */
    List<Integer> findCategoryIdsByProductIds(@Param("list") List<Integer> productIds);
}
