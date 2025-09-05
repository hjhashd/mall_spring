package com.coding24h.mall_spring.dto;

import com.coding24h.mall_spring.entity.User;

import java.time.LocalDateTime;

/**
 * 用于封装关注者信息的DTO，比User实体多了关注时间
 */
public class FollowerInfoDTO extends User {
    /**
     * 关注发生的时间
     */
    private LocalDateTime followCreatedAt;

    public LocalDateTime getFollowCreatedAt() {
        return followCreatedAt;
    }

    public void setFollowCreatedAt(LocalDateTime followCreatedAt) {
        this.followCreatedAt = followCreatedAt;
    }
}
