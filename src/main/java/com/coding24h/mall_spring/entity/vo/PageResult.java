package com.coding24h.mall_spring.entity.vo;

import java.util.List;

public class PageResult<T> {
    private long total;
    private List<T> items;

    // 无参构造函数
    public PageResult() {
    }

    // 有参构造函数
    public PageResult(long total, List<T> items) {
        this.total = total;
        this.items = items;
    }

    // getter 和 setter 方法
    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
