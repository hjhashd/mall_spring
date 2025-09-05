package com.coding24h.mall_spring.dto;

// 用于创建和更新用户时接收前端数据
public class UserDTO {
    private String username;
    private String email;
    private String password; // 仅在创建时使用
    private String phone;
    private Boolean isSeller;
    private Integer creditScore;

    // --- Getters and Setters ---
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Boolean getIsSeller() { return isSeller; }
    public void setIsSeller(Boolean seller) { isSeller = seller; }
    public Integer getCreditScore() { return creditScore; }
    public void setCreditScore(Integer creditScore) { this.creditScore = creditScore; }
}
