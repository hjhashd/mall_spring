package com.coding24h.mall_spring.mapper;

import com.coding24h.mall_spring.entity.ShippingCompany;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShippingCompanyMapper {

    List<ShippingCompany> selectActiveCompanies();

    ShippingCompany selectById(@Param("companyId") Integer companyId);
}