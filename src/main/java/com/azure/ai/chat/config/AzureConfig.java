package com.azure.ai.chat.config;

/**
 * Azure服务配置类
 * 请在环境变量或配置文件中设置这些值
 */
public class AzureConfig {
    
    // Azure OpenAI 配置
    public static final String OPENAI_ENDPOINT = System.getenv("AZURE_OPENAI_ENDPOINT");
    public static final String OPENAI_API_KEY = System.getenv("AZURE_OPENAI_API_KEY");
    public static final String OPENAI_DEPLOYMENT_NAME = System.getenv("AZURE_OPENAI_DEPLOYMENT_NAME"); // 如 "gpt-4"
    
    // Azure Speech 配置
    public static final String SPEECH_KEY = System.getenv("AZURE_SPEECH_KEY");
    public static final String SPEECH_REGION = System.getenv("AZURE_SPEECH_REGION"); // 如 "eastus"
    
    // Azure AI Projects 配置 (用于 Grounding with Bing Search)
    public static final String AI_PROJECT_ENDPOINT = System.getenv("AZURE_AI_PROJECT_ENDPOINT");
    public static final String AI_PROJECT_KEY = System.getenv("AZURE_AI_PROJECT_KEY");
    public static final String BING_CONNECTION_NAME = System.getenv("BING_CONNECTION_NAME"); // Bing连接名称
    
    // 语音配置
    public static final String SPEECH_RECOGNITION_LANGUAGE = "zh-CN"; // 中文识别
    public static final String SPEECH_SYNTHESIS_VOICE = "zh-CN-XiaoxiaoNeural"; // 中文女声
    
    public static void validate() {
        if (OPENAI_ENDPOINT == null || OPENAI_API_KEY == null) {
            throw new IllegalStateException("请设置 AZURE_OPENAI_ENDPOINT 和 AZURE_OPENAI_API_KEY 环境变量");
        }
        if (SPEECH_KEY == null || SPEECH_REGION == null) {
            throw new IllegalStateException("请设置 AZURE_SPEECH_KEY 和 AZURE_SPEECH_REGION 环境变量");
        }
        if (AI_PROJECT_ENDPOINT == null || AI_PROJECT_KEY == null) {
            throw new IllegalStateException("请设置 AZURE_AI_PROJECT_ENDPOINT 和 AZURE_AI_PROJECT_KEY 环境变量");
        }
    }
}