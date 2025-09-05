// 用于从前端接收审核数据，支持批量操作
package com.coding24h.mall_spring.dto;

import java.util.List;

public class ImageReviewRequestDTO {
    private List<ImageReviewItem> reviews;

    public List<ImageReviewItem> getReviews() {
        return reviews;
    }

    public void setReviews(List<ImageReviewItem> reviews) {
        this.reviews = reviews;
    }

    public static class ImageReviewItem {
        private Integer imageId;
        private Integer status; // 1-通过, 2-拒绝
        private String reason; // 拒绝原因

        // Getters and Setters
        public Integer getImageId() { return imageId; }
        public void setImageId(Integer imageId) { this.imageId = imageId; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
