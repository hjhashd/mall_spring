package com.coding24h.mall_spring.mapper.community;

import com.coding24h.mall_spring.entity.community.PostComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PostComment 数据访问层接口
 */
@Mapper
@Repository
public interface PostCommentMapper {
    /**
     * 根据帖子ID查询评论列表
     * @param postId 帖子ID
     * @return 评论列表
     */
    List<PostComment> selectCommentsByPostId(@Param("postId") Integer postId);

    /**
     * 插入新评论
     * @param comment 评论对象
     * @return 影响的行数
     */
    int insertComment(PostComment comment);

    /**
     * 更新帖子评论数
     * @param postId 帖子ID
     */
    void updatePostCommentCount(@Param("postId") Integer postId);
}
