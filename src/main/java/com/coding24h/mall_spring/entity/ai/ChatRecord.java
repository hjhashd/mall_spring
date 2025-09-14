package com.coding24h.mall_spring.entity.ai;

import java.time.LocalDateTime;

public class ChatRecord {
    private Long id;
    private String userId;
    private String question;
    private String answer;
    private String model;
    private LocalDateTime createTime;
    private String conversationId;

    // Constructor
    public ChatRecord() {
    }

    public ChatRecord(Long id, String userId, String question, String answer, String model, LocalDateTime createTime, String conversationId) {
        this.id = id;
        this.userId = userId;
        this.question = question;
        this.answer = answer;
        this.model = model;
        this.createTime = createTime;
        this.conversationId = conversationId;
    }

    // Getter and Setter for id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter and Setter for userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter and Setter for question
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    // Getter and Setter for answer
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    // Getter and Setter for model
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    // Getter and Setter for createTime
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
}
