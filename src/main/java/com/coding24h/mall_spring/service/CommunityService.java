package com.coding24h.mall_spring.service;

import com.coding24h.mall_spring.entity.community.CommunityPost;
import com.coding24h.mall_spring.entity.community.PostComment;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommunityService {
    /**
     * 获取帖子列表，支持分页和筛选
     * @param category 分类
     * @param keyword 关键词
     * @param minLikes 最低点赞数
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页后的帖子数据
     */
    PageInfo<CommunityPost> getPosts(String category, String keyword, Integer minLikes, Integer pageNum, Integer pageSize);

    /**
     * 获取单个帖子的评论列表
     * @param postId 帖子ID
     * @return 评论列表
     */
    List<PostComment> getPostComments(Integer postId);

    /**
     * 发布新帖子
     * @param post 帖子对象
     * @return 发布的帖子对象
     */
    CommunityPost createPost(CommunityPost post, MultipartFile featuredImage);

    /**
     * 发表评论
     * @param comment 评论对象
     * @return 发表的评论对象
     */
    PostComment addComment(PostComment comment);
}
