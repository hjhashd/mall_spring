package com.coding24h.mall_spring.service;

import com.coding24h.mall_spring.dto.cart.CartItemDTO;
import com.coding24h.mall_spring.dto.cart.CartSummaryDTO;
import com.coding24h.mall_spring.entity.Cart;
import com.coding24h.mall_spring.entity.Product;
import com.coding24h.mall_spring.mapper.CartMapper;
import com.coding24h.mall_spring.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    @Autowired
    public CartService(CartMapper cartMapper, ProductMapper productMapper) {
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
    }

    @Transactional
    public void addToCart(Integer userId, Integer productId, Integer quantity) {
        // 检查商品是否存在且上架
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        if (product.getStatus() != 1) {
            throw new RuntimeException("商品未上架或已售罄");
        }

        // 检查库存
        if (product.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }

        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setProductId(productId);
        cart.setQuantity(quantity);
        cart.setIsSelected(true);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());

        cartMapper.addOrUpdateCartItem(cart);
    }

    // 获取购物车列表
    public List<CartItemDTO> getCartItems(Integer userId) {
        List<Cart> cartItems = cartMapper.selectByUserId(userId);
        List<CartItemDTO> result = new ArrayList<>();

        for (Cart cart : cartItems) {
            Product product = productMapper.selectById(cart.getProductId());
            if (product != null && product.getStatus() == 1) { // 只显示上架商品
                CartItemDTO dto = new CartItemDTO();
                dto.setCartId(cart.getCartId());
                dto.setProductId(cart.getProductId());
                dto.setTitle(product.getTitle());
                dto.setDescription(product.getDescription());
                dto.setPrice(product.getPrice());
                dto.setOriginalPrice(product.getOriginalPrice());
                dto.setCondition(product.getCondition());
                dto.setStock(product.getStock());
                dto.setLocation(product.getLocation());
                dto.setImages(productMapper.selectImagesByProductId(product.getProductId()));
                dto.setQuantity(cart.getQuantity());
                dto.setIsSelected(cart.getIsSelected());
                dto.setCreatedAt(cart.getCreatedAt());
                dto.setUpdatedAt(cart.getUpdatedAt());
                result.add(dto);
            }
        }

        return result;
    }

    // 更新购物车商品数量
    @Transactional
    public void updateCartItemQuantity(Integer userId, Integer cartItemId, Integer quantity) {
        // 验证购物车项是否属于当前用户
        Cart cart = cartMapper.selectById(cartItemId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new RuntimeException("购物车项不存在或无权限");
        }

        // 检查库存
        Product product = productMapper.selectById(cart.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        if (product.getStock() < quantity) {
            throw new RuntimeException("库存不足，最多只能购买" + product.getStock() + "件");
        }

        cartMapper.updateQuantity(cartItemId, quantity);
    }

    // 删除购物车商品
    @Transactional
    public void deleteCartItem(Integer userId, Integer cartItemId) {
        // 验证购物车项是否属于当前用户
        Cart cart = cartMapper.selectById(cartItemId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new RuntimeException("购物车项不存在或无权限");
        }

        cartMapper.deleteById(cartItemId);
    }

    // 清空购物车
    @Transactional
    public void clearCart(Integer userId) {
        cartMapper.deleteByUserId(userId);
    }

    // 获取购物车统计信息
    public CartSummaryDTO getCartSummary(Integer userId) {
        List<CartItemDTO> cartItems = getCartItems(userId);

        int totalQuantity = cartItems.stream()
                .mapToInt(CartItemDTO::getQuantity)
                .sum();

        BigDecimal totalPrice = cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartSummaryDTO(totalQuantity, totalPrice, cartItems.size());
    }

}
