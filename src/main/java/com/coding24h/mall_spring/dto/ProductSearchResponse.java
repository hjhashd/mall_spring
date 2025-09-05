package com.coding24h.mall_spring.dto;

import com.coding24h.mall_spring.entity.vo.ProductVO;

import java.util.List;

public class ProductSearchResponse {
    private long total;
    private List<ProductVO> items;

    // 构造函数、getter和setter
    public ProductSearchResponse() {}

    public ProductSearchResponse(long total, List<ProductVO> items) {
        this.total = total;
        this.items = items;
    }

    // getter和setter
    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<ProductVO> getItems() {
        return items;
    }

    public void setItems(List<ProductVO> items) {
        this.items = items;
    }
}
