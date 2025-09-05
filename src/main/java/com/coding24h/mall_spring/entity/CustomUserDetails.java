package com.coding24h.mall_spring.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private final User user;
    private final Collection<? extends GrantedAuthority> authorities; // 存储权限集合

    // 修改构造函数，接收权限集合
    public CustomUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    // 直接返回传入的权限集合
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled(); // 确保 User 类有 isEnabled() 方法
    }

    // 添加一个方法来获取用户ID
    public Long getUserId() {
        return user.getUserId();
    }
}
