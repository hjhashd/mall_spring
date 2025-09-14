package com.coding24h.mall_spring.service;

import com.coding24h.mall_spring.dto.ProductQueryDTO;
import com.coding24h.mall_spring.entity.Product;
import com.coding24h.mall_spring.entity.vo.ProductVO;
import com.coding24h.mall_spring.mapper.ProductMapper;
import com.coding24h.mall_spring.mapper.recommend.RecommendMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 商品推荐服务 (简化版)
 * 负责所有与商品推荐相关的业务逻辑, 所有推荐均返回单个随机商品
 */
@Service
public class RecommendationService {

    @Autowired
    private RecommendMapper recMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 获取推荐商品 (猜你喜欢)
     * 策略: 优先从用户感兴趣的分类中随机推荐一个, 如果没有, 则从全站所有商品中随机推荐一个作为兜底。
     * @param currentUserId 当前用户ID
     * @return 返回包含零个或一个推荐商品的列表
     */
    public List<ProductVO> getRecommendedProduct(Long currentUserId) {
        // --- 策略1: 基于用户兴趣的个性化随机推荐 ---
        List<Integer> interestedCategoryIds = recMapper.findUserInterestedCategories(currentUserId);
        if (interestedCategoryIds != null && !interestedCategoryIds.isEmpty()) {
            ProductVO product = recMapper.findRecommendedProduct(currentUserId, interestedCategoryIds);
            if (product != null) {
                return Collections.singletonList(product);
            }
        }

        // --- 策略2: 终极兜底 - 随机返回任何一个可用的商品 ---
        ProductVO anyProduct = recMapper.findAnyAvailableProduct(currentUserId, null);
        if (anyProduct != null) {
            return Collections.singletonList(anyProduct);
        }

        // 如果数据库中确实没有任何符合条件的商品，最后才返回空
        System.out.println("所有策略均失败，数据库可能为空");
        return Collections.emptyList();
    }

    /**
     * "看了又看" (相似商品推荐)
     * 策略: 优先随机推荐一个"经常被一起购买"的商品。
     * 如果没有，则降级为随机推荐一个"同分类"下的商品。
     * @param productId 当前商品ID
     * @param currentUserId 当前用户ID
     * @return 返回包含零个或一个推荐商品的列表
     */
    public List<ProductVO> findSimilarProduct(Integer productId, Long currentUserId) {
        // --- 策略1: 基于协同过滤 (Item-Based Collaborative Filtering) 的随机推荐 ---
        ProductVO collaborativeItem = recMapper.findProductFrequentlyBoughtTogether(productId, currentUserId);
        if (collaborativeItem != null) {
            return Collections.singletonList(collaborativeItem);
        }

        // --- 策略2: 基于内容推荐 (作为降级策略) 的随机推荐 ---
        Product currentProduct = productMapper.selectById(productId);
        if (currentProduct == null || currentProduct.getCategoryId() == null) {
            return Collections.emptyList();
        }
        Integer categoryId = currentProduct.getCategoryId();

        ProductVO contentBasedItem = recMapper.findSimilarProductByCategory(categoryId, productId, currentUserId);
        if (contentBasedItem != null) {
            return Collections.singletonList(contentBasedItem);
        }

        System.out.println("[看了又看]所有策略均失败");
        return Collections.emptyList();
    }

    /**
     * 获取购物车配套推荐商品
     * 策略: 依次尝试 "共同购买" -> "同类商品" -> "全站商品" 的逻辑, 每一步都随机推荐一个, 直到找到为止。
     * @param currentUserId 当前用户ID
     * @return 返回包含零个或一个推荐商品的列表
     */
    public List<ProductVO> getCartRecommendation(Long currentUserId) {
        List<Integer> cartProductIds = recMapper.findProductIdsInCart(currentUserId);
        if (cartProductIds == null || cartProductIds.isEmpty()) {
            return Collections.emptyList();
        }

        // --- 策略1: "Frequently Bought Together" (共同购买) ---
        ProductVO product = recMapper.findComplementaryProductForCart(currentUserId, cartProductIds);
        if (product != null) {
            return Collections.singletonList(product);
        }

        // --- 策略2: "Similar Items in Same Categories" (同类商品) ---
        List<Integer> categoryIds = recMapper.findCategoryIdsByProductIds(cartProductIds);
        if (categoryIds != null && !categoryIds.isEmpty()) {
            product = recMapper.findSimilarProductByCategories(currentUserId, categoryIds, cartProductIds);
            if (product != null) {
                return Collections.singletonList(product);
            }
        }

        // --- 策略3: "Any Random Products" (终极兜底, 已优化) ---
        product = recMapper.findAnyAvailableProduct(currentUserId, cartProductIds);
        if (product != null) {
            return Collections.singletonList(product);
        }

        System.out.println("[购物车]所有策略均失败");
        return Collections.emptyList();
    }
}
