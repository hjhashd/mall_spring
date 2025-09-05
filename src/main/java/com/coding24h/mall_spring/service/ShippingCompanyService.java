package com.coding24h.mall_spring.service;

import com.coding24h.mall_spring.entity.ShippingCompany;
import com.coding24h.mall_spring.mapper.ShippingCompanyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShippingCompanyService {

    @Autowired
    private ShippingCompanyMapper shippingCompanyMapper;

    /**
     * 获取所有启用的物流公司
     */
    public List<ShippingCompany> getActiveCompanies() {
        return shippingCompanyMapper.selectActiveCompanies();
    }

    /**
     * 根据ID获取物流公司
     */
    public ShippingCompany getCompanyById(Integer companyId) {
        return shippingCompanyMapper.selectById(companyId);
    }
}