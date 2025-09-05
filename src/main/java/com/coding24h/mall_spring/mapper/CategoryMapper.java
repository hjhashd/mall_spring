package com.coding24h.mall_spring.mapper;

import com.coding24h.mall_spring.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {
    /**
     * 根据分类ID查询分类信息
     * @param categoryId 分类ID
     * @return Category对象
     */
    Category selectById(Integer categoryId);
}
