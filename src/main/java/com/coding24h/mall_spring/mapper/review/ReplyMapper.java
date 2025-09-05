package com.coding24h.mall_spring.mapper.review;

import com.coding24h.mall_spring.entity.Reply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 回复Mapper接口
 */
@Mapper
public interface ReplyMapper {

    /**
     * 根据评价ID获取回复列表
     */
    List<Reply> selectRepliesByReviewId(@Param("reviewId") Integer reviewId);

    /**
     * 获取评价的回复数量
     */
    Integer selectReplyCountByReviewId(@Param("reviewId") Integer reviewId);

    /**
     * 检查用户是否已追评
     */
    Reply selectAppendReplyByReviewIdAndUserId(@Param("reviewId") Integer reviewId, @Param("userId") Integer userId);

    /**
     * 插入回复
     */
    int insertReply(Reply reply);

    /**
     * 根据ID查询回复
     */
    Reply selectReplyById(@Param("replyId") Integer replyId);

    /**
     * 根据ID删除回复（逻辑删除）
     */
    int deleteReplyById(@Param("replyId") Integer replyId);
}
