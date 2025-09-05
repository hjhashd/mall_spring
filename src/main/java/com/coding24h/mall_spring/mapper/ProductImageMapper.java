package com.coding24h.mall_spring.mapper;


import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductImageMapper {

    @Select("SELECT image_url FROM product_images WHERE product_id = #{productId}")
    List<String> selectImageUrlsByProductId(Integer productId);

    @Delete("DELETE FROM product_images WHERE product_id = #{productId}")
    void deleteByProductId(Integer productId);
}
