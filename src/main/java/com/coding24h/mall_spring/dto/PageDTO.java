package com.coding24h.mall_spring.dto;

import java.util.List;

/**
 * 分页数据传输对象
 * @param <T> 列表中的数据类型
 */
public class PageDTO<T> {
    private List<T> items;      // 当前页的数据列表
    private long totalItems;    // 总记录数
    private int currentPage;    // 当前页码
    private int pageSize;       // 每页大小
    private int totalPages;     // 总页数

    public PageDTO(List<T> items, long totalItems, int currentPage, int pageSize) {
        this.items = items;
        this.totalItems = totalItems;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) totalItems / pageSize);
    }

    // --- Getters and Setters ---
    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
