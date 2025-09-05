package com.coding24h.mall_spring.entity.vo;

public class ShopStatsVO {
    private Integer onSaleCount;      // 在售商品数量
    private Integer monthlyOrders;    // 本月订单数
    private Double rating;            // 店铺评分
    private Integer monthlyViews;     // 本月浏览量

    public Integer getMonthlyOrders() {
        return monthlyOrders;
    }

    public void setMonthlyOrders(Integer monthlyOrders) {
        this.monthlyOrders = monthlyOrders;
    }

    public Integer getOnSaleCount() {
        return onSaleCount;
    }

    public void setOnSaleCount(Integer onSaleCount) {
        this.onSaleCount = onSaleCount;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getMonthlyViews() {
        return monthlyViews;
    }

    public void setMonthlyViews(Integer monthlyViews) {
        this.monthlyViews = monthlyViews;
    }
}
