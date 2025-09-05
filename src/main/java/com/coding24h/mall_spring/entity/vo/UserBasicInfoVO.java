package com.coding24h.mall_spring.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;

public class UserBasicInfoVO {
    private String username;
    private String email;
    private String phone;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("registeredAt")
    private LocalDateTime createdAt;

    public UserBasicInfoVO() {
    }

    public UserBasicInfoVO(String username, String email, String phone, LocalDateTime createdAt) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.createdAt = createdAt;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserBasicInfoVO that = (UserBasicInfoVO) o;
        return Objects.equals(username, that.username) && Objects.equals(email, that.email) && Objects.equals(phone, that.phone) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email, phone, createdAt);
    }
}
