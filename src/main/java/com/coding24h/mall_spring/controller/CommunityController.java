package com.coding24h.mall_spring.controller;

import com.coding24h.mall_spring.dto.ApiResponse;
import com.coding24h.mall_spring.entity.CustomUserDetails;
import com.coding24h.mall_spring.entity.community.CommunityPost;
import com.coding24h.mall_spring.entity.community.PostComment;
import com.coding24h.mall_spring.service.CommunityService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    @Resource
    private CommunityService communityService;

    /**
     * 获取帖子列表（支持筛选和分页）
     * GET /api/community/posts?category=家居&keyword=沙发&pageNum=1&pageSize=10
     */
    @GetMapping("/posts")
    public ApiResponse<PageInfo<CommunityPost>> getCommunityPosts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer minLikes,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<CommunityPost> posts = communityService.getPosts(category, keyword, minLikes, pageNum, pageSize);
        return ApiResponse.ok(posts);
    }

    /**
     * 发布新帖子
     * POST /api/community/posts
     * 使用 @RequestParam 接收所有表单字段，包括文件
     */
    @PostMapping("/posts")
    public ApiResponse<CommunityPost> createPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("category") String category,
            @RequestParam(value = "featuredImage", required = false) MultipartFile featuredImage) {

        Long userIdLong = getCurrentUserId();
        if (userIdLong == null) {
            return ApiResponse.error("用户未登录");
        }
        Integer userId = userIdLong.intValue();

        // 构造 CommunityPost 对象
        CommunityPost post = new CommunityPost();
        post.setUserId(userId);
        post.setTitle(title);
        post.setContent(content);
        post.setCategory(category);
        // featuredImage的URL将在service层设置

        try {
            CommunityPost newPost = communityService.createPost(post, featuredImage);
            return ApiResponse.ok(newPost);
        } catch (Exception e) {
            return ApiResponse.error("帖子发布失败: " + e.getMessage());
        }
    }

    /**
     * 获取帖子评论
     * GET /api/community/posts/{postId}/comments
     */
    @GetMapping("/posts/{postId}/comments")
    public ApiResponse<List<PostComment>> getPostComments(@PathVariable Integer postId) {
        List<PostComment> comments = communityService.getPostComments(postId);
        return ApiResponse.ok(comments);
    }

    /**
     * 发表评论
     * POST /api/community/posts/{postId}/comments
     */
    @PostMapping("/posts/{postId}/comments")
    public ApiResponse<PostComment> addComment(
            @PathVariable Integer postId,
            @RequestBody Map<String, Object> body) {
        Long userIdLong = getCurrentUserId();
        if (userIdLong == null) {
            return ApiResponse.error("用户未登录");
        }
        Integer userId = userIdLong.intValue();

        String content = (String) body.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ApiResponse.error("评论内容不能为空");
        }

        PostComment comment = new PostComment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content);

        PostComment newComment = communityService.addComment(comment);
        return ApiResponse.ok(newComment);
    }

    /**
     * 提取的获取当前用户ID方法
     * 依赖于 Spring Security 的 Authentication
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        }
        return null;
    }
}
