package com.coding24h.mall_spring.dto;

import java.util.Map;

/**
 * 用于接收前端更新店铺设置请求的数据传输对象 (DTO)
 * (businessHours 类型更新为 Map<String, Object> 以保持一致性)
 */

public class ShopSettingsUpdateDTO {
    private String shopName;
    private String logoUrl;
    private String bannerUrl;
    private String description;
    private String location;
    private String contactPhone;
    private String contactEmail;
    private String logisticsPolicy;
    private String afterSalePolicy;
    private String guaranteePolicy;
    private String announcement;
    private Map<String, String> businessHours;


    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
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

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public Map<String, String> getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(Map<String, String> businessHours) {
        this.businessHours = businessHours;
    }
}

