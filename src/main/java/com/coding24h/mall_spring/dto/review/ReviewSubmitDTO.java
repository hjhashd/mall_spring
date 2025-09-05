package com.coding24h.mall_spring.dto.review;

import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ReviewSubmitDTO {
    private Integer orderItemId;
    private Integer productId;
    private Integer rating;
    private String content;
    private List<MultipartFile> images; // 上传的图片文件
    private String imageUrls; // 存储处理后的图片URL
    private Boolean isAnonymous = false;

    // 构造函数
    public ReviewSubmitDTO() {}

    // getter 和 setter
    public Integer getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Integer orderItemId) { this.orderItemId = orderItemId; }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<MultipartFile> getImages() { return images; }
    public void setImages(List<MultipartFile> images) { this.images = images; }

    public String getImageUrls() { return imageUrls; }
    public void setImageUrls(String imageUrls) { this.imageUrls = imageUrls; }

    public Boolean getIsAnonymous() { return isAnonymous; }
    public void setIsAnonymous(Boolean isAnonymous) { this.isAnonymous = isAnonymous; }
}
