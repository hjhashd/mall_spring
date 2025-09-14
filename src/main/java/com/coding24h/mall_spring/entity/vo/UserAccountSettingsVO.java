package com.coding24h.mall_spring.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

public class UserAccountSettingsVO implements Serializable {
    // 头像
    private String avatarPath;
    // 名称
    private String username;
    // 账号安全强度
    private String passwordLevel;
    // 最近登录
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLogin;

    public UserAccountSettingsVO(String avatarPath, String username, String passwordLevel, LocalDateTime lastLogin) {
        this.avatarPath = avatarPath;
        this.username = username;
        this.passwordLevel = passwordLevel;
        this.lastLogin = lastLogin;
    }

    public UserAccountSettingsVO() {
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordLevel() {
        return passwordLevel;
    }

    public void setPasswordLevel(String passwordLevel) {
        this.passwordLevel = passwordLevel;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public String toString() {
        return "UserAccountSettingsVO{" +
                "avatarPath='" + avatarPath + '\'' +
                ", username='" + username + '\'' +
                ", passwordLevel='" + passwordLevel + '\'' +
                ", lastLogin=" + lastLogin +
                '}';
    }
}
