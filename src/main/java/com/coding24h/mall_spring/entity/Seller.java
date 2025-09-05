package com.coding24h.mall_spring.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public class Seller {
    private Integer sellerId;
    private String shopName;
    private String logoUrl;
    private String bannerUrl;
    private String description;
    private String location;
    private String contactPhone;
    private String contactEmail;
    private Date openDate; // 使用 java.util.Date
    private Integer totalSales;
    private Integer totalReviews;
    private String logisticsPolicy;
    private String afterSalePolicy;
    private String guaranteePolicy;
    private String announcement;


    /**
     * 营业时间，数据库中为 JSON 格式。
     * MyBatis 将通过 JsonMapTypeHandler 自动处理与数据库 JSON 字段的映射。
     * [FIXED] 将泛型类型从 Object 改为 String，以匹配 DTO，确保 BeanUtils.copyProperties 能够成功复制。
     */
    private Map<String, String> businessHours;

    private Boolean isVerified;
    private Integer followerCount;
    private Date createdAt; // 使用 java.util.Date
    private Date updatedAt; // 使用 java.util.Date
    // 构造函数和 getter/setter...
    public Seller() {}

    // 简单的 getter/setter
    public Integer getSellerId() { return sellerId; }
    public void setSellerId(Integer sellerId) { this.sellerId = sellerId; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public Date getOpenDate() { return openDate; }
    public void setOpenDate(Date openDate) { this.openDate = openDate; }

    public Integer getTotalSales() { return totalSales; }
    public void setTotalSales(Integer totalSales) { this.totalSales = totalSales; }

    public Integer getTotalReviews() { return totalReviews; }
    public void setTotalReviews(Integer totalReviews) { this.totalReviews = totalReviews; }

    public String getLogisticsPolicy() { return logisticsPolicy; }
    public void setLogisticsPolicy(String logisticsPolicy) { this.logisticsPolicy = logisticsPolicy; }

    public String getAfterSalePolicy() { return afterSalePolicy; }
    public void setAfterSalePolicy(String afterSalePolicy) { this.afterSalePolicy = afterSalePolicy; }

    public String getGuaranteePolicy() { return guaranteePolicy; }
    public void setGuaranteePolicy(String guaranteePolicy) { this.guaranteePolicy = guaranteePolicy; }

    public String getAnnouncement() { return announcement; }
    public void setAnnouncement(String announcement) { this.announcement = announcement; }

    public Map<String, String> getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(Map<String, String> businessHours) {
        this.businessHours = businessHours;
    }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public Integer getFollowerCount() { return followerCount; }
    public void setFollowerCount(Integer followerCount) { this.followerCount = followerCount; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
