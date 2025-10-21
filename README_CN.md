# Azure 实时聊天搜索系统

基于 Azure AI 服务的实时聊天问答系统，整合语音识别、GPT对话和Bing实时搜索。

## 功能特性

- ✅ **语音识别** - Azure Speech Service 语音转文字
- ✅ **智能对话** - Azure OpenAI GPT 智能问答  
- ✅ **实时搜索** - Grounding with Bing Search 联网搜索
- ✅ **语音合成** - 文字转语音输出
- ✅ **多种模式** - 语音/文字/流式/异步交互
- ✅ **低延迟** - 异步处理和流式响应优化

## 系统架构

用户语音/文字输入 → Azure Speech SDK → Azure OpenAI GPT → Grounding with Bing Search → 生成回答 → Azure Speech SDK → 语音/文字输出

## 快速开始

### 1. 前置要求

- JDK 11+
- Maven 3.6+
- Azure 订阅账号

### 2. 创建 Azure 资源

1. **Azure OpenAI Service** - 部署 GPT-4 或 GPT-3.5-turbo
2. **Azure Speech Service** - 创建语音服务
3. **Azure AI Foundry Project** - 配置 Bing Search 连接

### 3. 配置环境变量

export AZURE_OPENAI_ENDPOINT="https://your-resource.openai.azure.com/"
export AZURE_OPENAI_API_KEY="your-key"
export AZURE_OPENAI_DEPLOYMENT_NAME="gpt-4"
export AZURE_SPEECH_KEY="your-speech-key"
export AZURE_SPEECH_REGION="eastus"
export AZURE_AI_PROJECT_ENDPOINT="https://your-project.api.azureml.ms"
export AZURE_AI_PROJECT_KEY="your-project-key"
export BING_CONNECTION_NAME="your-bing-connection"

### 4. 编译运行

mvn clean package
mvn exec:java -Dexec.mainClass="com.azure.ai.chat.RealTimeChatApplication"

## 使用模式

### 模式1: 语音交互模式
语音输入 + AI处理 + 联网搜索 + 语音输出

### 模式2: 文字交互模式  
文字输入 + AI处理 + 联网搜索 + 文字输出

### 模式3: 流式响应模式
文字输入 + 流式输出（低延迟，无搜索）

### 模式4: 异步语音模式
异步语音处理 + 联网搜索（最低延迟）

## 示例对话

您: 今天北京的天气怎么样？
AI: [使用Bing搜索] 北京今天晴，15-25℃ [来源: weather.com.cn]

## 性能优化建议

1. 使用流式响应降低用户感知延迟
2. 异步处理提升整体响应速度
3. 选择地理位置最近的Azure区域
4. 调整max_tokens和temperature参数
5. 对常见问题进行缓存

## 项目结构

src/main/java/com/azure/ai/chat/
├── config/
│   └── AzureConfig.java
├── service/
│   ├── SpeechService.java
│   ├── ChatService.java
│   └── AgentService.java
└── RealTimeChatApplication.java

## 技术栈

- Java 11+
- Azure OpenAI SDK
- Azure Speech SDK  
- Azure AI Projects SDK
- SLF4J + Logback
- Maven

## 许可证

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request！

## 联系方式

如有问题请提交 Issue