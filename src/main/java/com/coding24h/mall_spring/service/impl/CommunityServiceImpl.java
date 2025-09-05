package com.coding24h.mall_spring.service.impl;

import com.coding24h.mall_spring.entity.community.CommunityPost;
import com.coding24h.mall_spring.entity.community.PostComment;
import com.coding24h.mall_spring.mapper.community.CommunityPostMapper;
import com.coding24h.mall_spring.mapper.community.PostCommentMapper;
import com.coding24h.mall_spring.service.CommunityService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class CommunityServiceImpl implements CommunityService {

    @Resource
    private CommunityPostMapper communityPostMapper;

    @Resource
    private PostCommentMapper postCommentMapper;

    @Resource
    private FileStorageService fileStorageService; // 注入你提供的文件存储服务

    @Override
    public PageInfo<CommunityPost> getPosts(String category, String keyword, Integer minLikes, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        List<CommunityPost> list = communityPostMapper.selectPostsByFilters(category, keyword, minLikes);
        return new PageInfo<>(list);
    }

    @Override
    public List<PostComment> getPostComments(Integer postId) {
        return postCommentMapper.selectCommentsByPostId(postId);
    }

    @Override
    @Transactional
    public CommunityPost createPost(CommunityPost post, MultipartFile featuredImage) {
        // 1. 处理图片上传，调用真实的文件存储服务
        if (featuredImage != null && !featuredImage.isEmpty()) {
            try {
                // 调用 FileStorageService 的 storeFile 方法进行文件存储
                String imageUrl = fileStorageService.storeFile(featuredImage, "community_posts");
                post.setFeaturedImage(imageUrl);
            } catch (Exception e) {
                // 捕获 FileStorageException 或 IOException，并抛出新的异常，让事务回滚
                throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
            }
        }

        // 2. 保存帖子基本信息
        communityPostMapper.insertPost(post);

        // 3. 返回完整的帖子信息
        return communityPostMapper.selectPostById(post.getPostId());
    }

    @Override
    @Transactional
    public PostComment addComment(PostComment comment) {
        postCommentMapper.insertComment(comment);
        // 更新帖子评论数
        postCommentMapper.updatePostCommentCount(comment.getPostId());
        // 如果需要返回完整的评论信息，可以再次查询
        return postCommentMapper.selectCommentsByPostId(comment.getPostId())
                .stream()
                .filter(c -> c.getCommentId().equals(comment.getCommentId()))
                .findFirst()
                .orElse(null);
    }
}
