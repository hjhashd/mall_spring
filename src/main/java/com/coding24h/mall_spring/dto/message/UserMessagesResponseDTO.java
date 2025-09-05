package com.coding24h.mall_spring.dto.message;

import java.util.List;

public class UserMessagesResponseDTO {
    private List<UserMessageDTO> list;
    private int unreadCount;

    public List<UserMessageDTO> getList() {
        return list;
    }

    public void setList(List<UserMessageDTO> list) {
        this.list = list;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
