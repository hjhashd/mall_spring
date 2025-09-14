package com.coding24h.mall_spring.service.impl;

import com.coding24h.mall_spring.entity.ai.ChatRecord;
import com.coding24h.mall_spring.entity.ai.StreamingRequest;
import com.coding24h.mall_spring.mapper.ChatMapper;
import com.coding24h.mall_spring.service.AIService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AIServiceImpl implements AIService {

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.api.url}")
    private String apiUrl;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChatMapper chatMapper;

    private static final int MAX_TOKENS = 4096;

    // 用于存储已生成摘要的会话ID
    private final Set<String> summarizedConversations = Collections.newSetFromMap(new ConcurrentHashMap<>());

    // 处理完成后，异步生成并更新对话摘要
    private void handleCompleteEvent(String userId, String question, String answer,
                                     String conversationId, WebSocketSession session) {
        try {
            String finalConversationId = (conversationId == null || conversationId.isEmpty())
                    ? "conv_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12)
                    : conversationId;

            ChatRecord record = new ChatRecord();
            record.setUserId(userId);
            record.setQuestion(question);
            record.setAnswer(answer);
            record.setConversationId(finalConversationId);
            record.setModel("deepseek-chat");

            chatMapper.insertChat(record);

            // 检查是否已经为该会话生成过摘要
            if (!summarizedConversations.contains(finalConversationId)) {
                // 异步调用摘要生成方法
                String finalUserId = userId;
                new Thread(() -> updateConversationSummary(finalConversationId, finalUserId)).start();
            }

            Map<String, Object> completeMsg = new HashMap<>();
            completeMsg.put("type", "complete");
            completeMsg.put("conversationId", finalConversationId);

            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(completeMsg)));

        } catch (Exception e) {
            System.err.println("Error in handleCompleteEvent: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 【更新】增加了详细的调试日志
     * 调用AI模型生成并更新对话摘要
     * @param conversationId 会话ID
     * @param userId 用户ID
     */
    private void updateConversationSummary(String conversationId, String userId) {
        // 检查是否已经生成过摘要
        if (summarizedConversations.contains(conversationId)) {
            System.out.println("[SUMMARY DEBUG] Summary already generated for conversation: " + conversationId + ". Skipping.");
            return;
        }

        // --- 调试日志 ---
        System.out.println("\n[SUMMARY DEBUG] ---------------------------------------------------");
        System.out.println("[SUMMARY DEBUG] Starting summary generation for conversationId: " + conversationId);

        try {
            // 1. 确保会话记录在摘要表中存在
            chatMapper.touchConversation(conversationId, userId);
            System.out.println("[SUMMARY DEBUG] Step 1: Touched conversation in database.");

            // 2. 获取最近的10条对话记录用于生成摘要
            List<ChatRecord> history = chatMapper.getRecentChatRecords(conversationId, 10);
            System.out.println("[SUMMARY DEBUG] Step 2: Fetched recent chat records. History size: " + history.size());

            if (history.size() < 1) { // 对话太短，不生成摘要
                System.out.println("[SUMMARY DEBUG] History too short. Aborting summary generation.");
                System.out.println("[SUMMARY DEBUG] ---------------------------------------------------\n");
                return;
            }
            Collections.reverse(history); // 反转列表，确保按时间正序排列

            // 3. 构建摘要提示
            StringBuilder conversationText = new StringBuilder();
            for (ChatRecord record : history) {
                conversationText.append("User: ").append(record.getQuestion()).append("\n");
                conversationText.append("AI: ").append(record.getAnswer()).append("\n");
            }
            String summaryPrompt = "Please provide a very short, concise title (in Chinese, under 10 words) for the following conversation:\n\n"
                    + conversationText;
            System.out.println("[SUMMARY DEBUG] Step 3: Built summary prompt.");

            // 4. 调用AI API (非流式)
            List<StreamingRequest.Message> messages = new ArrayList<>();
            messages.add(new StreamingRequest.Message("system", "You are an expert at summarizing conversations into short titles. Respond in Chinese."));
            messages.add(new StreamingRequest.Message("user", summaryPrompt));

            StreamingRequest request = new StreamingRequest();
            request.setModel("deepseek-chat");
            request.setMessages(messages);
            request.setStream(false);
            request.setMaxTokens(60);

            String requestBody = objectMapper.writeValueAsString(request);

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            System.out.println("[SUMMARY DEBUG] Step 4: Sending request to AI API at " + apiUrl);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            // 5. 解析响应并获取摘要
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            System.out.println("[SUMMARY DEBUG] Step 5: Received AI API Response: " + response.toString());

            JsonNode node = objectMapper.readTree(response.toString());
            JsonNode choices = node.get("choices");
            if (choices != null && choices.isArray() && !choices.isEmpty()) {
                String summary = choices.get(0).get("message").get("content").asText();
                summary = summary.trim().replaceAll("^\"|\"$|^'|'$|^《|》$|^【|】$", ""); // 清理摘要内容
                System.out.println("[SUMMARY DEBUG] Step 6: Parsed summary: \"" + summary + "\"");

                // 6. 更新数据库
                chatMapper.updateConversationSummary(conversationId, summary);
                // 标记该会话已生成摘要
                summarizedConversations.add(conversationId);
                System.out.println("[SUMMARY DEBUG] Step 7: Successfully updated database with summary.");
            } else {
                System.err.println("[SUMMARY DEBUG] ERROR: 'choices' field is missing or empty in API response.");
            }

        } catch (Exception e) {
            System.err.println("[SUMMARY DEBUG] FATAL ERROR: An exception occurred during summary generation.");
            e.printStackTrace(); // 打印完整的错误堆栈
        } finally {
            System.out.println("[SUMMARY DEBUG] ---------------------------------------------------\n");
        }
    }

    // =================================================================
    // 以下是原有的流式处理逻辑，保持不变
    // =================================================================

    public void processQuestionViaWebSocket(String userId, String question, String conversationId, WebSocketSession session) {
        new Thread(() -> {
            try {
                List<StreamingRequest.Message> contextMessages = new ArrayList<>();
                contextMessages.add(new StreamingRequest.Message("system", "你是有帮助的助手"));

                if (conversationId != null && !conversationId.isEmpty()) {
                    List<ChatRecord> history = chatMapper.getChatRecordsByConversationId(conversationId);
                    for (ChatRecord record : history) {
                        contextMessages.add(new StreamingRequest.Message("user", record.getQuestion()));
                        contextMessages.add(new StreamingRequest.Message("assistant", record.getAnswer()));
                    }
                }

                contextMessages.add(new StreamingRequest.Message("user", question));

                StreamingRequest request = new StreamingRequest();
                request.setModel("deepseek-chat");
                request.setMessages(contextMessages);
                request.setStream(true);
                request.setMaxTokens(MAX_TOKENS);

                String requestBody = objectMapper.writeValueAsString(request);

                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Bearer " + apiKey);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                connection.getOutputStream().write(requestBody.getBytes(StandardCharsets.UTF_8));

                StringBuilder fullResponse = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null && session.isOpen()) {
                        if (line.startsWith("data: ")) {
                            String json = line.substring(6).trim();
                            if ("[DONE]".equals(json)) {
                                handleCompleteEvent(userId, question, fullResponse.toString(), conversationId, session);
                                break;
                            }

                            JsonNode node = objectMapper.readTree(json);
                            JsonNode choices = node.get("choices");
                            if (choices != null && choices.isArray() && !choices.isEmpty()) {
                                JsonNode delta = choices.get(0).get("delta");
                                if (delta != null && delta.has("content")) {
                                    String content = delta.get("content").asText();
                                    fullResponse.append(content);

                                    Map<String, String> contentMsg = new HashMap<>();
                                    contentMsg.put("type", "message");
                                    contentMsg.put("content", content);
                                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(contentMsg)));
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                try {
                    if (session.isOpen()) {
                        Map<String, String> errorMsg = new HashMap<>();
                        errorMsg.put("type", "error");
                        errorMsg.put("content", "处理失败: " + e.getMessage());
                        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(errorMsg)));
                        session.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            }
        }).start();
    }
}
