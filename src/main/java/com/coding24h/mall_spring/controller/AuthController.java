package com.coding24h.mall_spring.controller;

import com.coding24h.mall_spring.dto.*;
import com.coding24h.mall_spring.entity.User;
import com.coding24h.mall_spring.jwt.JwtTokenUtil;
import com.coding24h.mall_spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            final UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // 获取完整用户信息
            User user = userService.findByUsername(userDetails.getUsername());

            // 生成包含用户ID的Token
            final String token = jwtTokenUtil.generateToken(userDetails, loginRequest.isRememberMe(), user.getUserId());

            // 构建详细响应
            UserDetailDTO userDetail = new UserDetailDTO();
            userDetail.setUserId(user.getUserId());
            userDetail.setUsername(user.getUsername());
            userDetail.setEmail(user.getEmail());
            userDetail.setPhone(user.getPhone());
            userDetail.setAvatarPath(user.getAvatarPath());
            userDetail.setCreditScore(user.getCreditScore());
            // 提取角色
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            userDetail.setRoles(roles);

            return ResponseEntity.ok(new JwtResponse(token, userDetail));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest registerRequest) {
        System.out.println("=== 收到注册请求 ===");
        System.out.println("用户名: " + registerRequest.getUsername());
        System.out.println("邮箱: " + registerRequest.getEmail());

        try {
            // 检查用户名是否已存在
            if (userService.existsByUsername(registerRequest.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "用户名已存在"));
            }

            // 检查邮箱是否已存在
            if (userService.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "邮箱已被注册"));
            }


            // 创建新用户并加密密码
            User user = new User();
            user.setEmail(registerRequest.getEmail());
            user.setUsername(registerRequest.getUsername());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setRole("ROLE_USER"); // 分配默认角色
            user.setCreatedAt(LocalDateTime.now());
            user.setEnabled(true);

            // 保存用户
            userService.save(user);

            System.out.println("=== 用户注册成功 ===");
            return ResponseEntity.ok(new ApiResponse(true, "注册成功"));

        } catch (Exception e) {
            System.err.println("=== 注册失败 ===");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "注册失败，请稍后重试"));
        }
    }
}

