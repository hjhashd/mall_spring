package com.coding24h.mall_spring.mapper.community;

import com.coding24h.mall_spring.entity.community.CommunityPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * CommunityPost 数据访问层接口
 */
@Mapper
@Repository
public interface CommunityPostMapper {
    /**
     * 根据筛选条件查询帖子列表
     * @param category 帖子分类
     * @param keyword 关键词
     * @param minLikes 最低点赞数
     * @return 帖子列表
     */
    List<CommunityPost> selectPostsByFilters(@Param("category") String category,
                                             @Param("keyword") String keyword,
                                             @Param("minLikes") Integer minLikes);

    /**
     * 根据ID查询单个帖子
     * @param postId 帖子ID
     * @return 帖子对象
     */
    CommunityPost selectPostById(@Param("postId") Integer postId);

    /**
     * 插入新帖子
     * @param post 帖子对象
     * @return 影响的行数
     */
    int insertPost(CommunityPost post);
}
