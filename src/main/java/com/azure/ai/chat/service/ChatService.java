package com.azure.ai.chat.service;

import com.azure.ai.chat.config.AzureConfig;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.*;
import com.azure.core.credential.AzureKeyCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Azure OpenAI 聊天服务
 */
public class ChatService {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private final OpenAIClient client;
    private final String deploymentName;
    private final List<ChatRequestMessage> conversationHistory;
    
    public ChatService() {
        this.client = new OpenAIClientBuilder()
            .endpoint(AzureConfig.OPENAI_ENDPOINT)
            .credential(new AzureKeyCredential(AzureConfig.OPENAI_API_KEY))
            .buildClient();
        
        this.deploymentName = AzureConfig.OPENAI_DEPLOYMENT_NAME;
        this.conversationHistory = new ArrayList<>();
        
        conversationHistory.add(new ChatRequestSystemMessage(
            "你是一个智能助手，可以帮助用户回答问题。"
        ));
    }
    
    public String chat(String userMessage) {
        conversationHistory.add(new ChatRequestUserMessage(userMessage));
        
        ChatCompletionsOptions options = new ChatCompletionsOptions(conversationHistory);
        options.setMaxTokens(1000);
        options.setTemperature(0.7);
        
        try {
            ChatCompletions chatCompletions = client.getChatCompletions(deploymentName, options);
            ChatChoice choice = chatCompletions.getChoices().get(0);
            String assistantMessage = choice.getMessage().getContent();
            
            conversationHistory.add(new ChatRequestAssistantMessage(assistantMessage));
            logger.info("GPT回复: {}", assistantMessage);
            return assistantMessage;
        } catch (Exception e) {
            logger.error("聊天请求失败", e);
            return "抱歉，我遇到了一些问题，请稍后再试。";
        }
    }
    
    public void chatStream(String userMessage, StreamCallback callback) {
        conversationHistory.add(new ChatRequestUserMessage(userMessage));
        
        ChatCompletionsOptions options = new ChatCompletionsOptions(conversationHistory);
        options.setMaxTokens(1000);
        options.setTemperature(0.7);
        
        try {
            StringBuilder fullResponse = new StringBuilder();
            
            client.getChatCompletionsStream(deploymentName, options)
                .forEach(chatCompletions -> {
                    if (chatCompletions.getChoices() != null && !chatCompletions.getChoices().isEmpty()) {
                        ChatChoice choice = chatCompletions.getChoices().get(0);
                        if (choice.getDelta() != null && choice.getDelta().getContent() != null) {
                            String content = choice.getDelta().getContent();
                            fullResponse.append(content);
                            callback.onChunk(content);
                        }
                    }
                });
            
            conversationHistory.add(new ChatRequestAssistantMessage(fullResponse.toString()));
            callback.onComplete(fullResponse.toString());
        } catch (Exception e) {
            logger.error("流式聊天失败", e);
            callback.onError(e);
        }
    }
    
    public void clearHistory() {
        conversationHistory.clear();
        conversationHistory.add(new ChatRequestSystemMessage("你是一个智能助手。"));
    }
    
    public interface StreamCallback {
        void onChunk(String chunk);
        void onComplete(String fullResponse);
        void onError(Exception e);
    }
}