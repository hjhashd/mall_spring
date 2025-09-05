package com.coding24h.mall_spring.mapper;

import com.coding24h.mall_spring.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartMapper {
    void addOrUpdateCartItem(Cart cart);

    // 根据用户ID查询购物车
    List<Cart> selectByUserId(Integer userId);

    // 根据ID查询购物车项
    Cart selectById(Integer cartId);

    // 更新数量
    void updateQuantity(@Param("cartId") Integer cartId, @Param("quantity") Integer quantity);

    // 根据ID删除
    void deleteById(Integer cartId);

    // 根据用户ID删除所有购物车项
    void deleteByUserId(Integer userId);
}
