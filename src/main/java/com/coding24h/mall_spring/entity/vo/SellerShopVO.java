package com.coding24h.mall_spring.entity.vo;

import java.math.BigDecimal;

/**
 * 店铺统计视图对象
 * 用于封装卖家中心首页所需的数据
 */
public class SellerShopVO {

    /**
     * 在售商品数量
     */
    private long onSaleCount;

    /**
     * 店铺评分 (平均分)
     */
    private BigDecimal averageRating;

    /**
     * 平均响应时间 (直接从卖家表获取)
     */
    private String avgResponseTime;

    /**
     * 物流政策
     */
    private String logisticsPolicy;

    /**
     * 售后服务政策
     */
    private String afterSalePolicy;

    /**
     * 保障服务政策
     */
    private String guaranteePolicy;

    /**
     * 店铺总销量
     */
    private int totalSales;

    /**
     * 待处理订单数
     */
    private long pendingOrders;

    /**
     * 总评价数
     */
    private int totalReviews;


    // Getters and Setters

    public long getOnSaleCount() {
        return onSaleCount;
    }

    public void setOnSaleCount(long onSaleCount) {
        this.onSaleCount = onSaleCount;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public String getAvgResponseTime() {
        return avgResponseTime;
    }

    public void setAvgResponseTime(String avgResponseTime) {
        this.avgResponseTime = avgResponseTime;
    }

    public String getLogisticsPolicy() {
        return logisticsPolicy;
    }

    public void setLogisticsPolicy(String logisticsPolicy) {
        this.logisticsPolicy = logisticsPolicy;
    }

    public String getAfterSalePolicy() {
        return afterSalePolicy;
    }

    public void setAfterSalePolicy(String afterSalePolicy) {
        this.afterSalePolicy = afterSalePolicy;
    }

    public String getGuaranteePolicy() {
        return guaranteePolicy;
    }

    public void setGuaranteePolicy(String guaranteePolicy) {
        this.guaranteePolicy = guaranteePolicy;
    }

    public int getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(int totalSales) {
        this.totalSales = totalSales;
    }

    public long getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(long pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }
}
