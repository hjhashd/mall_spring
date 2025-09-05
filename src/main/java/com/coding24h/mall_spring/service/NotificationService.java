package com.coding24h.mall_spring.service;

import com.coding24h.mall_spring.entity.event.MyNotification;
import com.coding24h.mall_spring.mapper.event.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    public List<MyNotification> getUnreadNotifications(Integer userId) {
        return notificationMapper.findUnreadByUserId(userId);
    }

    public void markAsRead(Integer userId, Integer notificationId) {
        // Optional: Check if the notification belongs to the user before marking as read
        notificationMapper.markAsRead(notificationId);
    }

    public void markAllAsRead(Integer userId) {
        notificationMapper.markAllAsReadByUserId(userId);
    }
}
