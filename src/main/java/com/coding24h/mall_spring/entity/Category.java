package com.coding24h.mall_spring.entity;

import java.util.List;

public class Category {
    private Integer categoryId;
    private Integer parentId;
    private String categoryName;
    private Integer sortOrder;

    // 字段名保持不变
    private Boolean isShow;

    private List<Category> children;

    // --- Getter and Setter (已修正) ---

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * 修正 Getter 方法
     * 对于名为 "isShow" 的 Boolean 类型字段，
     * 正确的 getter 方法名是 getIsShow()
     */
    public Boolean getIsShow() {
        return isShow;
    }

    /**
     * 修正 Setter 方法
     * 正确的 setter 方法名是 setIsShow()
     */
    public void setIsShow(Boolean isShow) {
        this.isShow = isShow;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }
}
