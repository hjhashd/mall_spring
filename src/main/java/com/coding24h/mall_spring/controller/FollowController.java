package com.coding24h.mall_spring.controller;

import com.coding24h.mall_spring.dto.ApiResponse;
import com.coding24h.mall_spring.dto.FollowerInfoDTO;
import com.coding24h.mall_spring.dto.PageDTO;
import com.coding24h.mall_spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/follows")
public class FollowController {

    @Autowired
    private UserService userService;

    /**
     * 获取指定卖家的关注者列表（粉丝列表）
     * @param sellerId 卖家ID
     * @param page     页码
     * @param pageSize 每页数量
     * @return 关注者分页数据
     */
    @GetMapping("/followers")
    public ApiResponse<PageDTO<FollowerInfoDTO>> getFollowers(
            @RequestParam("sellerId") Integer sellerId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            PageDTO<FollowerInfoDTO> followersPage = userService.getFollowersPage(sellerId, page, pageSize);
            return new ApiResponse<>(true, "获取关注者列表成功", followersPage);
        } catch (Exception e) {
            // 实际项目中建议使用日志记录异常
            return new ApiResponse<>(false, "获取关注者列表失败: " + e.getMessage(), null);
        }
    }

    /**
     * [新增] 添加一个关注关系 (用户关注店铺)
     * @param followerId 关注者ID (当前登录用户)
     * @param followedId 被关注者ID (店铺卖家)
     * @return 操作结果
     */
    @PostMapping("/follow")
    public ApiResponse<Void> followUser(
            @RequestParam("followerId") Integer followerId,
            @RequestParam("followedId") Integer followedId) {
        try {
            userService.addFollow(followerId, followedId);
            return new ApiResponse<>(true, "关注成功", null);
        } catch (Exception e) {
            return new ApiResponse<>(false, "关注失败: " + e.getMessage(), null);
        }
    }


    /**
     * 移除一个关注关系 (用户取关店铺 或 卖家移除粉丝)
     * @param followerId 关注者ID
     * @param followedId 被关注者ID (卖家ID)
     * @return 操作结果
     */
    @DeleteMapping("/unfollow")
    public ApiResponse<Void> removeFollower(
            @RequestParam("followerId") Integer followerId,
            @RequestParam("followedId") Integer followedId) {
        try {
            userService.removeFollow(followerId, followedId);
            return new ApiResponse<>(true, "操作成功", null);
        } catch (Exception e) {
            return new ApiResponse<>(false, "操作失败: " + e.getMessage(), null);
        }
    }

    /**
     * [新增] 检查用户是否已关注另一个用户
     * @param followerId 关注者ID (当前登录用户)
     * @param followedId 被关注者ID (店铺卖家)
     * @return 是否关注的状态
     */
    @GetMapping("/status")
    public ApiResponse<Map<String, Boolean>> getFollowStatus(
            @RequestParam("followerId") Integer followerId,
            @RequestParam("followedId") Integer followedId) {
        try {
            boolean isFollowing = userService.checkFollowStatus(followerId, followedId);
            return new ApiResponse<>(true, "查询成功", Collections.singletonMap("isFollowing", isFollowing));
        } catch (Exception e) {
            return new ApiResponse<>(false, "查询失败: " + e.getMessage(), null);
        }
    }
}
