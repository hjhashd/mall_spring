package com.coding24h.mall_spring.entity.vo;


/**
 * 评分分布VO类
 */
public class RatingDistributionVO {

    /**
     * 星级
     */
    private Integer stars;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 百分比
     */
    private Integer percentage;


    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }
}
