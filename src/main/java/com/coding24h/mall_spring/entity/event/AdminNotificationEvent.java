package com.coding24h.mall_spring.entity.event;

import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.Map;

public class AdminNotificationEvent extends ApplicationEvent {
    private final List<Integer> receiverIds;
    private final String type;
    private final Map<String, Object> data;

    public AdminNotificationEvent(Object source, List<Integer> receiverIds, String type, Map<String, Object> data) {
        super(source);
        this.receiverIds = receiverIds;
        this.type = type;
        this.data = data;
    }

    public List<Integer> getReceiverIds() {
        return receiverIds;
    }

    public String getType() {
        return type;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
