package com.coding24h.mall_spring.mapper;

import com.coding24h.mall_spring.dto.ConversationHistoryDTO;
import com.coding24h.mall_spring.entity.ai.ChatRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatMapper {

    // 插入聊天记录
    @Insert("INSERT INTO ai_record(user_id, question, answer, model, create_time, conversation_id) " +
            "VALUES(#{userId}, #{question}, #{answer}, 'deepseek-chat', NOW(), #{conversationId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertChat(ChatRecord record);

    // 添加根据会话ID获取记录的方法
    @Select("SELECT * FROM ai_record WHERE conversation_id = #{conversationId} ORDER BY create_time ASC")
    List<ChatRecord> getChatRecordsByConversationId(@Param("conversationId") String conversationId);

    // 获取最近的聊天记录，用于生成摘要
    @Select("SELECT * FROM ai_record WHERE conversation_id = #{conversationId} " +
            "ORDER BY create_time DESC LIMIT #{limit}")
    List<ChatRecord> getRecentChatRecords(
            @Param("conversationId") String conversationId,
            @Param("limit") int limit);

    // 查询历史记录（按时间倒序）
    @Select("SELECT id, user_id, question, answer, model, create_time " +
            "FROM ai_record " +
            "WHERE user_id = #{userId} " +
            "ORDER BY create_time DESC " +
            "LIMIT #{limit}")
    List<ChatRecord> getChatHistory(
            @Param("userId") String userId,
            @Param("limit") int limit);

    @Select("SELECT * FROM ai_record WHERE id = #{id}")
    ChatRecord getChatRecordById(Long id);

    // 【更新】获取会话列表，并关联查询摘要
    @Select("SELECT " +
            "  t.conversation_id as conversationId, " +
            "  t.last_time as lastTime, " +
            "  ac.summary, " +
            "  t.first_question as firstQuestion, " +
            "  t.record_count as recordCount " +
            "FROM (" +
            "  SELECT " +
            "    conversation_id, " +
            "    MAX(create_time) as last_time, " +
            "    SUBSTRING_INDEX(GROUP_CONCAT(question ORDER BY create_time ASC), ',', 1) as first_question, " +
            "    COUNT(*) as record_count " +
            "  FROM ai_record " +
            "  WHERE user_id = #{userId} " +
            "  GROUP BY conversation_id" +
            ") t " +
            "LEFT JOIN ai_conversation ac ON t.conversation_id = ac.conversation_id " +
            "ORDER BY t.last_time DESC " +
            "LIMIT #{limit}")
    List<ConversationHistoryDTO> getConversationHistory(
            @Param("userId") String userId,
            @Param("limit") int limit);

    @Delete("DELETE FROM ai_record WHERE conversation_id = #{conversationId}")
    void deleteConversationRecords(@Param("conversationId") String conversationId);

    @Delete("DELETE FROM ai_conversation WHERE conversation_id = #{conversationId}")
    void deleteConversationSummary(@Param("conversationId") String conversationId);

    // --- 新增：用于操作 ai_conversation 表 ---

    /**
     * 插入或更新会话记录，确保会话存在
     */
    @Insert("INSERT INTO ai_conversation (conversation_id, user_id, create_time, update_time) " +
            "VALUES (#{conversationId}, #{userId}, NOW(), NOW()) " +
            "ON DUPLICATE KEY UPDATE update_time = NOW()")
    void touchConversation(@Param("conversationId") String conversationId, @Param("userId") String userId);

    /**
     * 更新会话的摘要
     */
    @Update("UPDATE ai_conversation SET summary = #{summary}, update_time = NOW() WHERE conversation_id = #{conversationId}")
    void updateConversationSummary(@Param("conversationId") String conversationId, @Param("summary") String summary);
}
