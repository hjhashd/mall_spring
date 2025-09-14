package com.coding24h.mall_spring.entity.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StreamingRequest {
    private String model;
    private List<Message> messages = new ArrayList<>();
    private boolean stream = true; // 启用流式响应
    private int maxTokens = 4096;
    private double temperature = 0.7;
    private Map<String, Boolean> responseOptions; // 新增字段

    // 新增getter/setter
    public Map<String, Boolean> getResponseOptions() {
        return responseOptions;
    }

    public void setResponseOptions(Map<String, Boolean> responseOptions) {
        this.responseOptions = responseOptions;
    }

    // Getter and Setter for model
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    // Getter and Setter for messages
    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    // Method to add a message
    public void addMessage(String role, String content) {
        messages.add(new Message(role, content));
    }

    // Getter and Setter for stream
    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }

    // Getter and Setter for maxTokens
    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    // Getter and Setter for temperature
    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }



    public static class Message {
        private String role;
        private String content;

        // Constructor
        public Message() {
        }

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        // Getter and Setter for content
        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
