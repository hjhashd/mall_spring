package com.coding24h.mall_spring.mapper;

import com.coding24h.mall_spring.entity.Role;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RoleMapper {

    @Select("SELECT role_id, role_name, description FROM roles")
    List<Role> findAll();

    // 【新增】根据ID查找角色
    @Select("SELECT role_id, role_name, description FROM roles WHERE role_id = #{roleId}")
    Role findById(@Param("roleId") Integer roleId);

    // 【新增】插入一个新角色
    @Insert("INSERT INTO roles(role_name, description) VALUES(#{roleName}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "roleId")
    int insert(Role role);

    // 【新增】更新一个角色
    @Update("UPDATE roles SET role_name = #{roleName}, description = #{description} WHERE role_id = #{roleId}")
    int update(Role role);

    // 【新增】根据ID删除一个角色
    @Delete("DELETE FROM roles WHERE role_id = #{roleId}")
    int deleteById(@Param("roleId") Integer roleId);

    // 【新增】检查角色是否被任何用户使用
    @Select("SELECT COUNT(*) FROM user_roles WHERE role_id = #{roleId}")
    int countUsersWithRole(@Param("roleId") Integer roleId);
}
