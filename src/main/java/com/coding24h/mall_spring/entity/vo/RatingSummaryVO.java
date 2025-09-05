package com.coding24h.mall_spring.entity.vo;


import java.util.List;

/**
 * 评分统计VO类
 */
public class RatingSummaryVO {

    /**
     * 总评价数
     */
    private Integer total;

    /**
     * 平均评分
     */
    private Double average;

    /**
     * 评分分布
     */
    private List<RatingDistributionVO> distribution;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Double getAverage() {
        return average;
    }

    public void setAverage(Double average) {
        this.average = average;
    }

    public List<RatingDistributionVO> getDistribution() {
        return distribution;
    }

    public void setDistribution(List<RatingDistributionVO> distribution) {
        this.distribution = distribution;
    }
}
