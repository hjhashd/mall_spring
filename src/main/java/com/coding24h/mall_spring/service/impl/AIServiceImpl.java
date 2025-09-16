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

    // 【FIX for Summary Delay】
    // This method is now SYNCHRONOUS and returns the generated summary.
    // It will be called directly before sending the 'complete' message.
    private String generateAndSaveSummary(String conversationId, String userId) {
        // Check if a summary has already been generated for this session to prevent duplicate work.
        if (summarizedConversations.contains(conversationId)) {
            System.out.println("[SUMMARY DEBUG] Summary already generated for conversation: " + conversationId + ". Skipping.");
            return null;
        }

        System.out.println("\n[SUMMARY DEBUG] ---------------------------------------------------");
        System.out.println("[SUMMARY DEBUG] Starting summary generation for conversationId: " + conversationId);

        try {
            // Step 1: Ensure the conversation record exists in the summary table.
            chatMapper.touchConversation(conversationId, userId);
            System.out.println("[SUMMARY DEBUG] Step 1: Touched conversation in database.");

            // Step 2: Get the last 10 chat records to generate the summary.
            List<ChatRecord> history = chatMapper.getRecentChatRecords(conversationId, 10);
            System.out.println("[SUMMARY DEBUG] Step 2: Fetched recent chat records. History size: " + history.size());

            if (history.isEmpty()) { // Do not generate a summary for a very short conversation.
                System.out.println("[SUMMARY DEBUG] History too short. Aborting summary generation.");
                System.out.println("[SUMMARY DEBUG] ---------------------------------------------------\n");
                return null;
            }
            Collections.reverse(history); // Reverse the list to be in chronological order.

            // Step 3: Construct the prompt for the summary.
            StringBuilder conversationText = new StringBuilder();
            for (ChatRecord record : history) {
                conversationText.append("User: ").append(record.getQuestion()).append("\n");
                conversationText.append("AI: ").append(record.getAnswer()).append("\n");
            }
            String summaryPrompt = "Please provide a very short, concise title (in Chinese, under 10 words) for the following conversation:\n\n"
                    + conversationText;
            System.out.println("[SUMMARY DEBUG] Step 3: Built summary prompt.");

            // Step 4: Call the AI API (non-streaming) to get the summary.
            List<StreamingRequest.Message> messages = new ArrayList<>();
            messages.add(new StreamingRequest.Message("system", "You are an expert at summarizing conversations into short titles. Respond in Chinese."));
            messages.add(new StreamingRequest.Message("user", summaryPrompt));

            StreamingRequest request = new StreamingRequest();
            request.setModel("deepseek-chat");
            request.setMessages(messages);
            request.setStream(false); // Non-streaming for a single response
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

            // Step 5: Parse the response to get the summary content.
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
                summary = summary.trim().replaceAll("^\"|\"$|^'|'$|^《|》$|^【|】$", ""); // Clean the summary content
                System.out.println("[SUMMARY DEBUG] Step 6: Parsed summary: \"" + summary + "\"");

                // Step 6: Update the database with the new summary.
                chatMapper.updateConversationSummary(conversationId, summary);
                summarizedConversations.add(conversationId); // Mark as summarized
                System.out.println("[SUMMARY DEBUG] Step 7: Successfully updated database with summary.");

                // Step 7: Return the summary to be sent to the client.
                return summary;
            } else {
                System.err.println("[SUMMARY DEBUG] ERROR: 'choices' field is missing or empty in API response.");
            }

        } catch (Exception e) {
            System.err.println("[SUMMARY DEBUG] FATAL ERROR: An exception occurred during summary generation.");
            e.printStackTrace();
        } finally {
            System.out.println("[SUMMARY DEBUG] ---------------------------------------------------\n");
        }
        return null; // Return null if summary generation fails.
    }

    // 【MODIFIED】This method now calls the summary generation synchronously.
    private void handleCompleteEvent(String userId, String question, String answer,
                                     String conversationId, WebSocketSession session) {
        try {
            // Determine the final conversation ID.
            String finalConversationId = (conversationId == null || conversationId.isEmpty() || conversationId.startsWith("web_"))
                    ? "conv_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12)
                    : conversationId;

            // Save the chat record to the database.
            ChatRecord record = new ChatRecord();
            record.setUserId(userId);
            record.setQuestion(question);
            record.setAnswer(answer);
            record.setConversationId(finalConversationId);
            record.setModel("deepseek-chat");
            chatMapper.insertChat(record);

            // **KEY CHANGE**: Generate summary synchronously and get the result.
            // The async thread is removed to eliminate the race condition.
            String summary = generateAndSaveSummary(finalConversationId, userId);

            // Prepare the 'complete' message for the frontend.
            Map<String, Object> completeMsg = new HashMap<>();
            completeMsg.put("type", "complete");
            completeMsg.put("conversationId", finalConversationId);

            // **KEY CHANGE**: Include the summary in the message if it was generated.
            if (summary != null) {
                completeMsg.put("summary", summary);
            }

            // Send the final message via WebSocket.
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(completeMsg)));

        } catch (Exception e) {
            System.err.println("Error in handleCompleteEvent: " + e.getMessage());
            e.printStackTrace();
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
