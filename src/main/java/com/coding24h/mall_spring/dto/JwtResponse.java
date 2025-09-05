 package com.coding24h.mall_spring.dto;

 import java.util.List;

 public class JwtResponse {
     private String token;
     private UserDetailDTO user;

     public JwtResponse(String token, UserDetailDTO user) {
         this.token = token;
         this.user = user;
     }

     public String getToken() {
         return token;
     }

     public void setToken(String token) {
         this.token = token;
     }

     public UserDetailDTO getUser() {
         return user;
     }

     public void setUser(UserDetailDTO user) {
         this.user = user;
     }
 }
