package com.coding24h.mall_spring.dto.review;


/**
 * 评价查询DTO
 */
public class ReviewQueryDTO {

    /**
     * 商品ID
     */
    private Integer productId;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 评分筛选
     */
    private Integer rating;

    /**
     * 排序字段
     */
    private String sort = "created_at";

    /**
     * 排序方向：asc, desc
     */
    private String order = "desc";

    /**
     * 是否有图片
     */
    private Boolean hasImage;

    /**
     * 是否有卖家回复
     */
    private Boolean hasSellerReply;

    private Long sellerId;


    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Boolean getHasImage() {
        return hasImage;
    }

    public void setHasImage(Boolean hasImage) {
        this.hasImage = hasImage;
    }

    public Boolean getHasSellerReply() {
        return hasSellerReply;
    }

    public void setHasSellerReply(Boolean hasSellerReply) {
        this.hasSellerReply = hasSellerReply;
    }
}
