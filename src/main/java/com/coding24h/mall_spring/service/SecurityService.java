package com.coding24h.mall_spring.service;


import com.coding24h.mall_spring.entity.CustomUserDetails;
import com.coding24h.mall_spring.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List; // 新增导入

@Service
public class SecurityService implements UserDetailsService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<User> users = userService.findRoleByUsername(username);

        // 检查用户是否存在
        if (users == null || users.isEmpty()) {
            System.err.println("用户不存在: " + username);
            throw new UsernameNotFoundException("用户不存在");
        }

        User firstUser = users.get(0);
        // 检查用户是否被禁用
        if (!firstUser.getEnabled()) {
            throw new DisabledException("用户已被禁用");
        }

        // 检查密码一致性
        String firstPassword = firstUser.getPassword();
        for (User user : users) {
            if (!user.getPassword().equals(firstPassword)) {
                throw new IllegalStateException("用户 '" + username + "' 存在密码不一致的记录");
            }
        }


        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (User user : users) {
            String role = user.getRole();
            if (role != null && !role.trim().isEmpty()) {
                if (!role.startsWith("ROLE_")) {
                    role = "ROLE_" + role;
                }
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }

        return new CustomUserDetails(firstUser, authorities);
    }
}

