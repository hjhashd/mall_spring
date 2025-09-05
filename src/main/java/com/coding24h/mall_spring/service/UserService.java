package com.coding24h.mall_spring.service;

import com.coding24h.mall_spring.dto.FollowerInfoDTO;
import com.coding24h.mall_spring.dto.PageDTO;
import com.coding24h.mall_spring.dto.UserDTO;
import com.coding24h.mall_spring.entity.User;
import com.coding24h.mall_spring.entity.Role;
import com.coding24h.mall_spring.entity.event.UserFollowedEvent;
import com.coding24h.mall_spring.entity.vo.UserBasicInfoVO;
import com.coding24h.mall_spring.mapper.UserMapper;
import com.coding24h.mall_spring.mapper.RoleMapper;
import com.coding24h.mall_spring.dto.UserRoleInfoDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public List<User> findRoleByUsername(String username) {
        return userMapper.findRoleByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userMapper.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userMapper.existsByEmail(email);
    }

    public List<Integer> getAllUserIds() {
        return userMapper.getAllUserIds();
    }

    public BigDecimal getUserBalance(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user.getBalance();
    }

    public void save(User user) {
        Integer defaultRoleId = getDefaultRoleId();
        userMapper.insertUser(user);
        userMapper.insertUserRole(user.getUserId(), defaultRoleId);
    }

    private Integer getDefaultRoleId() {
        return userMapper.findRoleIdByName("ROLE_USER");
    }

    public User findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        return userMapper.findByUsername(username);
    }

    public UserBasicInfoVO getUserBasicInfo(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        UserBasicInfoVO userBasicInfoVO = new UserBasicInfoVO();
        BeanUtils.copyProperties(user, userBasicInfoVO);
        return userBasicInfoVO;
    }

    public PageDTO<UserRoleInfoDTO> getUsersWithRolesPage(int page, int pageSize, String query) {
        int offset = (page - 1) * pageSize;
        List<User> users = userMapper.findUsersWithPagination(pageSize, offset, query);

        List<UserRoleInfoDTO> userRoleInfoDTOs = users.stream().map(user -> {
            UserRoleInfoDTO dto = new UserRoleInfoDTO();
            BeanUtils.copyProperties(user, dto);
            List<Role> roles = userMapper.findRolesByUserId(user.getUserId().intValue());
            dto.setRoles(roles);
            return dto;
        }).collect(Collectors.toList());

        long total = userMapper.countUsers(query);
        return new PageDTO<>(userRoleInfoDTOs, total, page, pageSize);
    }

    public PageDTO<User> getUsersPage(int page, int pageSize, String query) {
        int offset = (page - 1) * pageSize;
        List<User> users = userMapper.findUsersWithPagination(pageSize, offset, query);
        long total = userMapper.countUsers(query);
        return new PageDTO<>(users, total, page, pageSize);
    }

    @Transactional
    public void toggleUserStatus(Long userId, boolean enabled) {
        int updatedRows = userMapper.updateUserStatus(userId, enabled);
        if (updatedRows == 0) {
            throw new RuntimeException("用户不存在或状态更新失败");
        }
    }

    @Transactional
    public void updateUserRoles(Integer userId, List<Integer> roleIds) {
        // 1. 删除该用户的所有旧角色
        userMapper.deleteUserRolesByUserId(userId.longValue());
        // 2. 插入新的角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Integer roleId : roleIds) {
                userMapper.insertUserRole(userId.longValue(), roleId);
            }
        }
    }

    @Transactional
    public User createUser(UserDTO userDTO) {
        if (userMapper.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (userMapper.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setPhone(userDTO.getPhone());
        user.setIsSeller(userDTO.getIsSeller() != null && userDTO.getIsSeller());
        user.setCreditScore(userDTO.getCreditScore() != null ? userDTO.getCreditScore() : 100);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        userMapper.insertUser(user);

        Integer roleId = userMapper.findRoleIdByName("buyer");
        if (roleId != null) {
            userMapper.insertUserRole(user.getUserId(), roleId);
        }

        if (user.getIsSeller()) {
            Integer sellerRoleId = userMapper.findRoleIdByName("seller");
            if (sellerRoleId != null) {
                userMapper.insertUserRole(user.getUserId(), sellerRoleId);
            }
        }

        return user;
    }

    @Transactional
    public User updateUser(Long userId, UserDTO userDTO) {
        User existingUser = userMapper.selectById(userId.intValue());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        existingUser.setEmail(userDTO.getEmail());
        existingUser.setPhone(userDTO.getPhone());
        existingUser.setIsSeller(userDTO.getIsSeller());
        existingUser.setCreditScore(userDTO.getCreditScore());

        userMapper.updateUser(existingUser);
        return existingUser;
    }

    @Transactional
    public void deleteUser(Long userId) {
        userMapper.deleteUserRolesByUserId(userId);
        int deletedRows = userMapper.deleteUserById(userId);
        if (deletedRows == 0) {
            throw new RuntimeException("用户不存在或已被删除");
        }
    }

    // =================================================
    // 【新增】角色管理相关方法
    // =================================================

    public List<Role> getAllRoles() {
        return roleMapper.findAll();
    }

    @Transactional
    public Role createRole(Role role) {
        // 确保roleName以 "ROLE_" 开头，这是Spring Security的约定
        if (!role.getRoleName().startsWith("ROLE_")) {
            role.setRoleName("ROLE_" + role.getRoleName().toUpperCase());
        }
        roleMapper.insert(role);
        return role;
    }

    @Transactional
    public Role updateRole(Integer roleId, Role roleDetails) {
        Role existingRole = roleMapper.findById(roleId);
        if (existingRole == null) {
            throw new RuntimeException("角色不存在");
        }
        existingRole.setRoleName(roleDetails.getRoleName());
        existingRole.setDescription(roleDetails.getDescription());

        // 同样确保roleName以 "ROLE_" 开头
        if (!existingRole.getRoleName().startsWith("ROLE_")) {
            existingRole.setRoleName("ROLE_" + existingRole.getRoleName().toUpperCase());
        }

        roleMapper.update(existingRole);
        return existingRole;
    }

    @Transactional
    public void deleteRole(Integer roleId) {
        // 检查是否有用户正在使用此角色
        int userCount = roleMapper.countUsersWithRole(roleId);
        if (userCount > 0) {
            throw new RuntimeException("无法删除，仍有 " + userCount + " 个用户关联此角色。");
        }

        int deletedRows = roleMapper.deleteById(roleId);
        if (deletedRows == 0) {
            throw new RuntimeException("角色不存在或已被删除");
        }
    }

    // =================================================
    // 【新增】关注管理相关方法
    // =================================================

    /**
     * 根据卖家ID获取其关注者分页数据
     * @param sellerId 卖家ID
     * @param page      当前页码
     * @param pageSize  每页数量
     * @return 关注者分页DTO
     */
    public PageDTO<FollowerInfoDTO> getFollowersPage(Integer sellerId, int page, int pageSize) {
        if (sellerId == null) {
            throw new IllegalArgumentException("卖家ID不能为空");
        }
        int offset = (page - 1) * pageSize;
        List<FollowerInfoDTO> followers = userMapper.findFollowersBySellerId(sellerId, pageSize, offset);
        long total = userMapper.countFollowersBySellerId(sellerId);
        return new PageDTO<>(followers, total, page, pageSize);
    }

    /**
     * 添加关注关系，并更新双方的关注数和粉丝数
     * @param followerId 关注者ID
     * @param followedId 被关注者ID
     */
    @Transactional
    public void addFollow(Integer followerId, Integer followedId) {
        if (followerId.equals(followedId)) {
            throw new RuntimeException("不能关注自己");
        }
        // 检查是否已关注
        if (userMapper.checkFollowExists(followerId, followedId) > 0) {
            throw new RuntimeException("已经关注过了");
        }
        int insertedRows = userMapper.insertFollow(followerId, followedId);
        if (insertedRows > 0) {
            userMapper.incrementFollowerCount(followedId);
            userMapper.incrementFollowingCount(followerId);

            // [核心新增] 发布用户关注事件，通知其他模块（如此处的WebSocket）
            eventPublisher.publishEvent(new UserFollowedEvent(this, followerId, followedId));

        } else {
            throw new RuntimeException("关注失败");
        }
    }

    /**
     * 移除关注关系，并更新双方的关注数和粉丝数
     * @param followerId 关注者ID
     * @param followedId 被关注者ID
     */
    @Transactional
    public void removeFollow(Integer followerId, Integer followedId) {
        int deletedRows = userMapper.deleteFollow(followerId, followedId);
        if (deletedRows > 0) {
            // 更新被关注者的粉丝数
            userMapper.decrementFollowerCount(followedId);
            // 更新关注者的关注数
            userMapper.decrementFollowingCount(followerId);
        } else {
            throw new RuntimeException("关注关系不存在或已被移除");
        }
    }

    /**
     * [新增] 检查关注状态
     * @param followerId 关注者ID
     * @param followedId 被关注者ID
     * @return 是否已关注
     */
    public boolean checkFollowStatus(Integer followerId, Integer followedId) {
        if (followerId == null || followedId == null) {
            return false;
        }
        return userMapper.checkFollowExists(followerId, followedId) > 0;
    }
}
