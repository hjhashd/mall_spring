package com.coding24h.mall_spring.mapper.message;

import com.coding24h.mall_spring.dto.message.UserMessageDTO; // 【导入】新的DTO
import com.coding24h.mall_spring.entity.UserMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMessageMapper {
    // 批量插入，用于消息发送后为每个用户创建一条记录
    @Insert("<script>" +
            "INSERT INTO user_message (user_id, message_id, is_read, read_time) VALUES " +
            "<foreach collection='userMessages' item='um' separator=','>" +
            "(#{um.userId}, #{um.messageId}, #{um.isRead}, #{um.readTime})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("userMessages") List<UserMessage> userMessages);

    // 【修复】查询指定用户的所有消息，并在SQL层面处理好布尔逻辑
    @Select("SELECT m.message_id, m.title, m.content, m.send_time, " +
            "CASE um.is_read WHEN 0 THEN 'true' ELSE 'false' END AS is_unread " + // <-- 核心修改在这里
            "FROM message m " +
            "JOIN user_message um ON m.message_id = um.message_id " +
            "WHERE um.user_id = #{userId} " +
            "ORDER BY m.send_time DESC")
    @Results({
            @Result(property = "messageId", column = "message_id"),
            @Result(property = "sendTime", column = "send_time"),
            // 现在 SQL 直接返回了正确的布尔值字符串 'true'/'false'，MyBatis 可以正确映射
            @Result(property = "isUnread", column = "is_unread", javaType = Boolean.class)
    })
    List<UserMessageDTO> findMessagesByUserId(@Param("userId") Long userId);

    // 【新增】统计指定用户的未读消息数量
    @Select("SELECT COUNT(*) FROM user_message WHERE user_id = #{userId} AND is_read = 0")
    int countUnreadByUserId(@Param("userId") Long userId);

    // 【新增】将用户的单条消息标记为已读
    @Update("UPDATE user_message SET is_read = 1, read_time = NOW() " +
            "WHERE user_id = #{userId} AND message_id = #{messageId} AND is_read = 0")
    int markAsRead(@Param("userId") Long userId, @Param("messageId") Long messageId);

    // 【新增】将用户的所有未读消息标记为已读
    @Update("UPDATE user_message SET is_read = 1, read_time = NOW() " +
            "WHERE user_id = #{userId} AND is_read = 0")
    int markAllAsRead(@Param("userId") Long userId);
}
