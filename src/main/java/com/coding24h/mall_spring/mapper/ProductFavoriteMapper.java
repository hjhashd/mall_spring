package com.coding24h.mall_spring.mapper;

import com.coding24h.mall_spring.dto.FavoriteProductDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductFavoriteMapper {

    /**
     * 检查用户是否已收藏某商品
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 收藏数量（0或1）
     */
    int isFavorited(@Param("userId") Long userId, @Param("productId") Integer productId);

    /**
     * 添加收藏记录
     * @param userId 用户ID
     * @param productId 商品ID
     */
    void addFavorite(@Param("userId") Long userId, @Param("productId") Integer productId);

    /**
     * 删除收藏记录
     * @param userId 用户ID
     * @param productId 商品ID
     */
    void removeFavorite(@Param("userId") Long userId, @Param("productId") Integer productId);

    /**
     * 更新商品的收藏数量
     * @param productId 商品ID
     * @param increment 增量（+1或-1）
     */
    void updateFavoriteCount(@Param("productId") Integer productId, @Param("increment") int increment);


        int countUserFavorites(@Param("userId") Long userId);

    List<FavoriteProductDTO> listUserFavorites(@Param("userId") Long userId, @Param("sort") String sort);
}
