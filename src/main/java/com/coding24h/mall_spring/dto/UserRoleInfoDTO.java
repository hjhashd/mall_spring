package com.coding24h.mall_spring.dto;

import com.coding24h.mall_spring.entity.Role;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用于向前台返回包含完整角色对象列表的用户信息
 * 这个DTO不继承User类，以避免字段冲突
 */
public class UserRoleInfoDTO {
    private Long userId;
    private String username;
    private String email;
    private String phone;
    private String avatarPath;
    private Boolean enabled;
    private Boolean isSeller;
    private Integer creditScore;
    private LocalDateTime createdAt;

    // 关键字段：使用完整的Role对象列表
    private List<Role> roles;

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getIsSeller() {
        return isSeller;
    }

    public void setIsSeller(Boolean isSeller) {
        this.isSeller = isSeller;
    }

    public Integer getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
