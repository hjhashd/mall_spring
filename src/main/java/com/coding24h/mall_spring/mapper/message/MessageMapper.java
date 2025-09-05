package com.coding24h.mall_spring.mapper.message;
import com.coding24h.mall_spring.dto.message.MessageDTO;
import com.coding24h.mall_spring.dto.message.MessageFilterDTO;
import com.coding24h.mall_spring.entity.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

import com.coding24h.mall_spring.util.StringListTypeHandler; // 导入 TypeHandler

@Mapper
public interface MessageMapper {

    @Insert("INSERT INTO message (type, title, content, status, scheduled_time, send_time, created_at, updated_at) " +
            "VALUES (#{type}, #{title}, #{content}, #{status}, #{scheduledTime}, #{sendTime}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "messageId")
    int insert(Message message);

    @Update("<script>" +
            "UPDATE message " +
            "<set>" +
            "  <if test='type != null'>type = #{type},</if>" +
            "  <if test='title != null'>title = #{title},</if>" +
            "  <if test='content != null'>content = #{content},</if>" +
            "  <if test='status != null'>status = #{status},</if>" +
            "  <if test='scheduledTime != null'>scheduled_time = #{scheduledTime},</if>" +
            "  <if test='sendTime != null'>send_time = #{sendTime},</if>" +
            "  updated_at = NOW()" +
            "</set>" +
            "WHERE message_id = #{messageId}" +
            "</script>")
    int updateById(Message message);

    @Delete("DELETE FROM message WHERE message_id = #{messageId}")
    int deleteById(@Param("messageId") Long messageId);

    @Select("SELECT * FROM message WHERE message_id = #{messageId}")
    Message findById(@Param("messageId") Long messageId);

    // 【关键改动 1】移除 SQL 中的 LIMIT 和 OFFSET
    @Select("<script>" +
            "SELECT " +
            "  m.message_id, m.type, m.title, m.content, m.status, m.scheduled_time, m.send_time, " +
            "  (SELECT COUNT(DISTINCT um.user_id) FROM user_message um WHERE um.message_id = m.message_id) AS recipientCount, " +
            "  (SELECT GROUP_CONCAT(mrg.group_type) FROM message_recipient_group mrg WHERE mrg.message_id = m.message_id) AS recipients " +
            "FROM message m " +
            "<where>" +
            "  <if test='filter.type != null and filter.type != \"\"'>AND m.type = #{filter.type}</if>" +
            "  <if test='filter.status != null and filter.status != \"\"'>AND m.status = #{filter.status}</if>" +
            "  <if test='filter.startDate != null'>AND m.created_at &gt;= #{filter.startDate}</if>" +
            "  <if test='filter.endDate != null'>AND m.created_at &lt;= #{filter.endDate}</if>" +
            "</where>" +
            "ORDER BY m.created_at DESC " +
            // "LIMIT #{filter.size} OFFSET #{filter.page * filter.size}" // 这一行被移除
            "</script>")
    @Results({
            @Result(property = "messageId", column = "message_id"),
            @Result(property = "scheduledTime", column = "scheduled_time"),
            @Result(property = "sendTime", column = "send_time"),
            @Result(property = "recipients", column = "recipients", typeHandler = StringListTypeHandler.class)
    })
    List<MessageDTO> findMessagesWithRecipientCount(@Param("filter") MessageFilterDTO filter);

    // 【关键改动 2】删除不再需要的 countMessages 方法
    // long countMessages(MessageFilterDTO filter);
}
