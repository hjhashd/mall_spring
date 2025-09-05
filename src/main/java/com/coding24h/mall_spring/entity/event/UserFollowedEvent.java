package com.coding24h.mall_spring.entity.event;

import org.springframework.context.ApplicationEvent;

/**
 * 用户关注事件
 * 当一个用户成功关注另一个用户（卖家）时发布。
 */
public class UserFollowedEvent extends ApplicationEvent {

    private final Integer followerId; // 关注者的ID
    private final Integer followedId; // 被关注者（卖家）的ID

    public UserFollowedEvent(Object source, Integer followerId, Integer followedId) {
        super(source);
        this.followerId = followerId;
        this.followedId = followedId;
    }

    public Integer getFollowerId() {
        return followerId;
    }

    public Integer getFollowedId() {
        return followedId;
    }
}
