package com.coding24h.mall_spring.dto;

import org.springframework.web.multipart.MultipartFile;

public class CertificationDTO {

    private MultipartFile businessLicense;
    private String businessName;
    private String businessType;
    private String contactPhone;
    private String contactEmail;
    private String businessAddress;
    private String businessDescription;

    // ------------------- Getter -------------------

    public MultipartFile getBusinessLicense() {
        return businessLicense;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public String getBusinessDescription() {
        return businessDescription;
    }

    // ------------------- Setter -------------------

    public void setBusinessLicense(MultipartFile businessLicense) {
        this.businessLicense = businessLicense;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public void setBusinessDescription(String businessDescription) {
        this.businessDescription = businessDescription;
    }
}
