package com.coding24h.mall_spring.mapper;

import com.coding24h.mall_spring.dto.FollowerInfoDTO;
import com.coding24h.mall_spring.entity.Role;
import com.coding24h.mall_spring.entity.User;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT user_id, username, email, phone, avatar_path, password, credit_score FROM users WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    @Select("SELECT u.*, r.role_name AS role " +
            "FROM users u " +
            "JOIN user_roles ur ON u.user_id = ur.user_id " +
            "JOIN roles r ON ur.role_id = r.role_id " +
            "WHERE u.username = #{username}")
    List<User> findRoleByUsername(@Param("username") String username);

    @Select("SELECT * FROM users")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "username", column = "username"),
            @Result(property = "password", column = "password")
    })
    List<User> findAllUsersToChange();

    @Update("UPDATE users SET password = #{password} WHERE user_id = #{userId}")
    void updateOldPassword(@Param("userId") Long userId, @Param("password") String password);

    // 检查用户名是否存在
    @Select("SELECT COUNT(*) > 0 FROM users WHERE username = #{username}")
    boolean existsByUsername(@Param("username") String username);


    @Insert("INSERT INTO users(username, password, email, phone, is_seller, credit_score, created_at, enabled) " +
            "VALUES(#{username}, #{password}, #{email}, #{phone}, #{isSeller}, #{creditScore}, NOW(), 1)")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insertUser(User user);

    /**
     * 根据用户ID删除用户
     * @param userId 用户ID
     * @return 删除的行数
     */
    @Delete("DELETE FROM users WHERE user_id = #{userId}")
    int deleteUserById(@Param("userId") Long userId);

    /**
     * 根据用户ID删除其所有角色关联
     * @param userId 用户ID
     * @return 删除的行数
     */
    @Delete("DELETE FROM user_roles WHERE user_id = #{userId}")
    int deleteUserRolesByUserId(@Param("userId") Long userId);


    @Select("SELECT role_id FROM roles WHERE role_name = #{roleName}")
    Integer findRoleIdByName(@Param("roleName") String roleName);

    /**
     * 获取所有用户的ID
     * @return 包含所有用户ID的列表
     */
    @Select("SELECT user_id FROM users")
    List<Integer> getAllUserIds();

    // 根据ID查询用户（原XML中的selectById）
    @Select("SELECT * FROM users WHERE user_id = #{userId}")
    User selectById(@Param("userId") Integer userId);

    // 更新用户余额（原XML中的updateBalance）
    @Update("UPDATE users SET balance = #{balance} WHERE user_id = #{userId}")
    int updateBalance(@Param("userId") Integer userId, @Param("balance") BigDecimal balance);

    /**
     * 根据分页和搜索条件查询用户列表
     * @param limit 每页数量
     * @param offset 偏移量
     * @param query 搜索关键词 (用户名或邮箱)
     * @return 用户列表
     */
    @Select("<script>" +
            "SELECT * FROM users " +
            "<where>" +
            "  <if test='query != null and query != \"\"'>" +
            "    (username LIKE CONCAT('%', #{query}, '%') OR email LIKE CONCAT('%', #{query}, '%'))" +
            "  </if>" +
            "</where>" +
            "ORDER BY created_at DESC " +
            "LIMIT #{limit} OFFSET #{offset}" +
            "</script>")
    List<User> findUsersWithPagination(@Param("limit") int limit, @Param("offset") int offset, @Param("query") String query);

    /**
     * 根据搜索条件统计用户总数
     * @param query 搜索关键词 (用户名或邮箱)
     * @return 用户总数
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM users " +
            "<where>" +
            "  <if test='query != null and query != \"\"'>" +
            "    (username LIKE CONCAT('%', #{query}, '%') OR email LIKE CONCAT('%', #{query}, '%'))" +
            "  </if>" +
            "</where>" +
            "</script>")
    long countUsers(@Param("query") String query);

    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param enabled 新的状态 (true for 1, false for 0)
     * @return 更新的行数
     */
    @Update("UPDATE users SET enabled = #{enabled} WHERE user_id = #{userId}")
    int updateUserStatus(@Param("userId") Long userId, @Param("enabled") boolean enabled);


    @Update("<script>" +
            "UPDATE users " +
            "<set>" +
            "  <if test='username != null'>username = #{username},</if>" +
            "  <if test='email != null'>email = #{email},</if>" +
            "  <if test='phone != null'>phone = #{phone},</if>" +
            "  <if test='isSeller != null'>is_seller = #{isSeller},</if>" +
            "  <if test='creditScore != null'>credit_score = #{creditScore},</if>" +
            "  last_updated = NOW()" +
            "</set>" +
            "WHERE user_id = #{userId}" +
            "</script>")
    int updateUser(User user);

    /**
     * 【新增】查询所有管理员角色的用户ID
     * @return 管理员用户ID列表
     */
    @Select("SELECT ur.user_id FROM user_roles ur " +
            "JOIN roles r ON ur.role_id = r.role_id " +
            "WHERE r.role_name = 'ROLE_ADMIN'")
    List<Integer> findAdminUserIds();

    /**
     * 根据前端定义的组类型查找用户ID
     * 'all' -> 所有用户
     * 'vip' -> 拥有 'ROLE_ADMIN' 角色的用户 (这是一个业务约定)
     * 'normal' -> 不拥有 'ROLE_ADMIN' 角色的用户
     * @param groupType 组类型 ('all', 'vip', 'normal')
     * @return 用户ID列表
     */
    @Select("<script>" +
            "SELECT u.user_id FROM users u " +
            "<choose>" +
            "  <when test='groupType == \"vip\"'>" +
            "    JOIN user_roles ur ON u.user_id = ur.user_id " +
            "    JOIN roles r ON ur.role_id = r.role_id " +
            "    WHERE r.role_name = 'ROLE_ADMIN'" +
            "  </when>" +
            "  <when test='groupType == \"normal\"'>" +
            "    WHERE u.user_id NOT IN (" +
            "      SELECT ur.user_id FROM user_roles ur " +
            "      JOIN roles r ON ur.role_id = r.role_id " +
            "      WHERE r.role_name = 'ROLE_ADMIN'" +
            "    )" +
            "  </when>" +
            // 默认情况，包括 'all' 和其他未识别的类型，都返回所有用户
            "  <otherwise></otherwise>" +
            "</choose>" +
            "</script>")
    List<Long> findUserIdsByGroupType(@Param("groupType") String groupType);

    @Select("SELECT COUNT(*) > 0 FROM users WHERE email = #{email}")
    boolean existsByEmail(@Param("email") String email);

    // 【新增】根据用户ID查询其拥有的所有角色
    @Select("SELECT r.role_id, r.role_name, r.description FROM roles r " +
            "JOIN user_roles ur ON r.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<Role> findRolesByUserId(@Param("userId") Integer userId);

    // 插入用户角色关联
    @Insert("INSERT INTO user_roles(user_id, role_id) VALUES(#{userId}, #{roleId})")
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Integer roleId);

    /**
     * 根据卖家ID分页查询其关注者列表，并附带关注时间
     * @param sellerId 卖家（被关注者）的用户ID
     * @param limit 每页数量
     * @param offset 偏移量
     * @return 关注者信息列表 (包含关注时间)
     */
    @Select("SELECT u.*, uf.created_at as followCreatedAt FROM users u " +
            "JOIN user_follows uf ON u.user_id = uf.follower_id " +
            "WHERE uf.followed_id = #{sellerId} " +
            "ORDER BY uf.created_at DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<FollowerInfoDTO> findFollowersBySellerId(@Param("sellerId") Integer sellerId, @Param("limit") int limit, @Param("offset") int offset);

    /**
     * 根据卖家ID统计其关注者总数
     * @param sellerId 卖家（被关注者）的用户ID
     * @return 关注者总数
     */
    @Select("SELECT COUNT(*) FROM user_follows WHERE followed_id = #{sellerId}")
    long countFollowersBySellerId(@Param("sellerId") Integer sellerId);

    /**
     * 删除一条关注记录
     * @param followerId 关注者ID
     * @param followedId 被关注者ID
     * @return 删除的行数
     */
    @Delete("DELETE FROM user_follows WHERE follower_id = #{followerId} AND followed_id = #{followedId}")
    int deleteFollow(@Param("followerId") Integer followerId, @Param("followedId") Integer followedId);

    /**
     * 将指定用户的粉丝数减一
     * @param userId 用户ID
     */
    @Update("UPDATE users SET follower_count = follower_count - 1 WHERE user_id = #{userId} AND follower_count > 0")
    void decrementFollowerCount(@Param("userId") Integer userId);

    /**
     * 将指定用户的关注数减一
     * @param userId 用户ID
     */
    @Update("UPDATE users SET following_count = following_count - 1 WHERE user_id = #{userId} AND following_count > 0")
    void decrementFollowingCount(@Param("userId") Integer userId);


    // ===============================================
    // [新增] 关注功能所需的Mapper方法
    // ===============================================

    /**
     * 检查关注关系是否存在
     * @param followerId 关注者
     * @param followedId 被关注者
     * @return 存在则返回 > 0 的数
     */
    @Select("SELECT COUNT(*) FROM user_follows WHERE follower_id = #{followerId} AND followed_id = #{followedId}")
    int checkFollowExists(@Param("followerId") Integer followerId, @Param("followedId") Integer followedId);

    /**
     * 插入一条新的关注记录
     * @param followerId 关注者
     * @param followedId 被关注者
     * @return 插入的行数
     */
    @Insert("INSERT INTO user_follows (follower_id, followed_id) VALUES (#{followerId}, #{followedId})")
    int insertFollow(@Param("followerId") Integer followerId, @Param("followedId") Integer followedId);

    /**
     * 将指定用户的关注数加一
     * @param userId 用户ID
     */
    @Update("UPDATE users SET following_count = following_count + 1 WHERE user_id = #{userId}")
    void incrementFollowingCount(@Param("userId") Integer userId);

    /**
     * 将指定用户的粉丝数加一
     * @param userId 用户ID
     */
    @Update("UPDATE users SET follower_count = follower_count + 1 WHERE user_id = #{userId}")
    void incrementFollowerCount(@Param("userId") Integer userId);

}
