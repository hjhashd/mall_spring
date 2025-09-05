package com.coding24h.mall_spring.entity;

import java.util.Date;

public class SellerCertification {

    private Integer certificationId;
    private Long userId;
    private String businessLicense;
    private String businessName;
    private String businessType;
    private String contactPhone;
    private String contactEmail;
    private String businessAddress;
    private String businessDescription;
    private Integer status;          // 0-待审核, 1-已认证, 2-拒绝
    private Integer reviewedBy;
    private Date reviewedAt;
    private Date createdAt;
    private String rejectReason;

    // 查询用
    private String username;
    private String email;
    // 构造函数
    public SellerCertification() {}

    public SellerCertification(Long userId,
                               String businessLicense,
                               String businessName,
                               String businessType,
                               String contactPhone,
                               String contactEmail,
                               String businessAddress,
                               String businessDescription) {
        this.userId = userId;
        this.businessLicense = businessLicense;
        this.businessName = businessName;
        this.businessType = businessType;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.businessAddress = businessAddress;
        this.businessDescription = businessDescription;

        // 其余字段默认值
        this.status = 0;          // 默认待审核
        this.createdAt = new Date();
    }

    public SellerCertification(Long userId, String businessLicense) {
        this.userId = userId;
        this.businessLicense = businessLicense;
        this.status = 0;
        this.createdAt = new Date();
    }

    // Getter和Setter方法
    public Integer getCertificationId() {
        return certificationId;
    }

    public void setCertificationId(Integer certificationId) {
        this.certificationId = certificationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Integer reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public Date getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Date reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
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

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getBusinessDescription() {
        return businessDescription;
    }

    public void setBusinessDescription(String businessDescription) {
        this.businessDescription = businessDescription;
    }
}
