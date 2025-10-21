#!/bin/bash

# Azure 实时聊天搜索系统 - 环境变量配置脚本
# 请替换为您自己的 Azure 资源信息

echo "正在配置 Azure 环境变量..."

# Azure OpenAI 配置
export AZURE_OPENAI_ENDPOINT="https://your-resource.openai.azure.com/"
export AZURE_OPENAI_API_KEY="your-openai-api-key"
export AZURE_OPENAI_DEPLOYMENT_NAME="gpt-4"

# Azure Speech 配置
export AZURE_SPEECH_KEY="your-speech-key"
export AZURE_SPEECH_REGION="eastus"

# Azure AI Projects 配置
export AZURE_AI_PROJECT_ENDPOINT="https://your-project.api.azureml.ms"
export AZURE_AI_PROJECT_KEY="your-project-key"
export BING_CONNECTION_NAME="your-bing-connection"

echo "环境变量配置完成！"
echo ""
echo "已设置的环境变量:"
echo "AZURE_OPENAI_ENDPOINT: $AZURE_OPENAI_ENDPOINT"
echo "AZURE_SPEECH_REGION: $AZURE_SPEECH_REGION"
echo ""
echo "运行应用: mvn exec:java -Dexec.mainClass=\"com.azure.ai.chat.RealTimeChatApplication\""