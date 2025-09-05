package com.coding24h.mall_spring.mapper.message;

import com.coding24h.mall_spring.entity.MessageRecipientGroup;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageRecipientGroupMapper {

    @Insert("INSERT INTO message_recipient_group (message_id, group_type) VALUES (#{messageId}, #{groupType})")
    int insert(MessageRecipientGroup record);

    // 批量插入
    @Insert("<script>" +
            "INSERT INTO message_recipient_group (message_id, group_type) VALUES " +
            "<foreach collection='groups' item='group' separator=','>" +
            "(#{group.messageId}, #{group.groupType})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("groups") List<MessageRecipientGroup> groups);

    @Delete("DELETE FROM message_recipient_group WHERE message_id = #{messageId}")
    int deleteByMessageId(@Param("messageId") Long messageId);

    @Select("SELECT * FROM message_recipient_group WHERE message_id = #{messageId}")
    List<MessageRecipientGroup> findByMessageId(@Param("messageId") Long messageId);
}
