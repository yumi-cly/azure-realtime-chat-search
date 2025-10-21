package com.azure.ai.chat.service;

import com.azure.ai.chat.config.AzureConfig;
import com.azure.ai.projects.AIProjectClient;
import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.models.*;
import com.azure.core.credential.AzureKeyCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Azure AI Agent 服务 - 支持 Grounding with Bing Search
 */
public class AgentService {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentService.class);
    private final AIProjectClient projectClient;
    private Agent agent;
    private AgentThread thread;
    
    public AgentService() {
        this.projectClient = new AIProjectClientBuilder()
            .endpoint(AzureConfig.AI_PROJECT_ENDPOINT)
            .credential(new AzureKeyCredential(AzureConfig.AI_PROJECT_KEY))
            .buildClient();
        
        initializeAgent();
    }
    
    private void initializeAgent() {
        try {
            BingGroundingToolDefinition bingTool = new BingGroundingToolDefinition();
            bingTool.setConnectionName(AzureConfig.BING_CONNECTION_NAME);
            
            List<ToolDefinition> tools = new ArrayList<>();
            tools.add(bingTool);
            
            this.agent = projectClient.createAgent(
                AzureConfig.OPENAI_DEPLOYMENT_NAME,
                new AgentCreationOptions()
                    .setName("RealTimeSearchAgent")
                    .setInstructions(
                        "你是一个智能助手，具备实时联网搜索能力。" +
                        "当用户询问最新信息时，使用 Bing Search 工具搜索互联网。" +
                        "回答要简洁明了，并标注信息来源。"
                    )
                    .setTools(tools)
            );
            
            logger.info("Agent 创建成功: {}", agent.getId());
            
            this.thread = projectClient.createThread(new AgentThreadCreationOptions());
            logger.info("Thread 创建成功: {}", thread.getId());
        } catch (Exception e) {
            logger.error("初始化 Agent 失败", e);
            throw new RuntimeException("Agent 初始化失败", e);
        }
    }
    
    public String chatWithSearch(String userMessage) {
        try {
            logger.info("用户消息: {}", userMessage);
            
            ThreadMessage message = projectClient.createMessage(
                thread.getId(),
                new MessageCreationOptions("user", userMessage)
            );
            
            ThreadRun run = projectClient.createRun(
                thread.getId(),
                new RunCreationOptions(agent.getId())
            );
            
            run = waitForRunCompletion(thread.getId(), run.getId());
            
            if (run.getStatus() == RunStatus.COMPLETED) {
                PageableList<ThreadMessage> messages = projectClient.listMessages(
                    thread.getId(),
                    new ListMessagesOptions().setOrder(ListSortOrder.DESCENDING).setLimit(1)
                );
                
                if (messages != null && !messages.getData().isEmpty()) {
                    ThreadMessage latestMessage = messages.getData().get(0);
                    String response = extractTextFromMessage(latestMessage);
                    logger.info("Agent 回复: {}", response);
                    return response;
                }
            } else if (run.getStatus() == RunStatus.FAILED) {
                logger.error("Agent 运行失败: {}", run.getLastError());
                return "抱歉，搜索过程中遇到了问题。";
            }
            
            return "未获取到回复";
        } catch (Exception e) {
            logger.error("Agent 聊天失败", e);
            return "抱歉，处理您的请求时出错了。";
        }
    }
    
    private ThreadRun waitForRunCompletion(String threadId, String runId) throws InterruptedException {
        ThreadRun run;
        int maxAttempts = 60;
        int attempts = 0;
        
        do {
            Thread.sleep(1000);
            run = projectClient.getRun(threadId, runId);
            attempts++;
            
            logger.debug("Run 状态: {}", run.getStatus());
            
            if (attempts >= maxAttempts) {
                logger.warn("等待 Run 完成超时");
                break;
            }
        } while (run.getStatus() == RunStatus.QUEUED || 
                 run.getStatus() == RunStatus.IN_PROGRESS ||
                 run.getStatus() == RunStatus.REQUIRES_ACTION);
        
        return run;
    }
    
    private String extractTextFromMessage(ThreadMessage message) {
        StringBuilder text = new StringBuilder();
        
        for (MessageContent content : message.getContent()) {
            if (content instanceof MessageTextContent) {
                MessageTextContent textContent = (MessageTextContent) content;
                text.append(textContent.getText().getValue());
                
                if (textContent.getText().getAnnotations() != null) {
                    for (MessageTextAnnotation annotation : textContent.getText().getAnnotations()) {
                        if (annotation instanceof MessageTextFileCitationAnnotation) {
                            MessageTextFileCitationAnnotation citation = 
                                (MessageTextFileCitationAnnotation) annotation;
                            text.append(" [来源: ").append(citation.getFileCitation()).append("]");
                        }
                    }
                }
            }
        }
        
        return text.toString();
    }
    
    public void resetThread() {
        try {
            this.thread = projectClient.createThread(new AgentThreadCreationOptions());
            logger.info("新 Thread 创建成功: {}", thread.getId());
        } catch (Exception e) {
            logger.error("重置 Thread 失败", e);
        }
    }
    
    public void cleanup() {
        try {
            if (thread != null) {
                projectClient.deleteThread(thread.getId());
            }
            if (agent != null) {
                projectClient.deleteAgent(agent.getId());
            }
            logger.info("Agent 资源清理完成");
        } catch (Exception e) {
            logger.error("清理资源失败", e);
        }
    }
}