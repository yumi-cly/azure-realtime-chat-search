package com.azure.ai.chat.service;

import com.azure.ai.chat.config.AzureConfig;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Azure 语音服务封装
 * 提供语音识别和语音合成功能
 */
public class SpeechService {
    
    private static final Logger logger = LoggerFactory.getLogger(SpeechService.class);
    
    private final SpeechConfig speechConfig;
    
    public SpeechService() {
        this.speechConfig = SpeechConfig.fromSubscription(
            AzureConfig.SPEECH_KEY, 
            AzureConfig.SPEECH_REGION
        );
        
        // 设置识别语言
        this.speechConfig.setSpeechRecognitionLanguage(AzureConfig.SPEECH_RECOGNITION_LANGUAGE);
        
        // 设置合成语音
        this.speechConfig.setSpeechSynthesisVoiceName(AzureConfig.SPEECH_SYNTHESIS_VOICE);
    }
    
    /**
     * 语音转文字（同步）
     * 从麦克风识别语音
     */
    public String recognizeSpeechFromMicrophone() {
        try (SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig)) {
            
            logger.info("请开始说话...");
            
            SpeechRecognitionResult result = recognizer.recognizeOnceAsync().get();
            
            if (result.getReason() == ResultReason.RecognizedSpeech) {
                logger.info("识别到: {}", result.getText());
                return result.getText();
            } else if (result.getReason() == ResultReason.NoMatch) {
                logger.warn("未识别到语音");
                return null;
            } else if (result.getReason() == ResultReason.Canceled) {
                CancellationDetails cancellation = CancellationDetails.fromResult(result);
                logger.error("语音识别取消: {}", cancellation.getReason());
                if (cancellation.getReason() == CancellationReason.Error) {
                    logger.error("错误详情: {}", cancellation.getErrorDetails());
                }
                return null;
            }
            
            return null;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("语音识别失败", e);
            return null;
        }
    }
    
    /**
     * 语音转文字（异步）
     */
    public CompletableFuture<String> recognizeSpeechAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try (SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig)) {
                SpeechRecognitionResult result = recognizer.recognizeOnceAsync().get();
                
                if (result.getReason() == ResultReason.RecognizedSpeech) {
                    return result.getText();
                }
                return null;
            } catch (Exception e) {
                logger.error("异步语音识别失败", e);
                return null;
            }
        });
    }
    
    /**
     * 文字转语音（同步）
     * 通过扬声器播放
     */
    public boolean synthesizeSpeech(String text) {
        try (SpeechSynthesizer synthesizer = new SpeechSynthesizer(speechConfig)) {
            
            logger.info("正在合成语音...");
            
            SpeechSynthesisResult result = synthesizer.SpeakTextAsync(text).get();
            
            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                logger.info("语音合成完成");
                return true;
            } else if (result.getReason() == ResultReason.Canceled) {
                CancellationDetails cancellation = CancellationDetails.fromResult(result);
                logger.error("语音合成取消: {}", cancellation.getReason());
                if (cancellation.getReason() == CancellationReason.Error) {
                    logger.error("错误详情: {}", cancellation.getErrorDetails());
                }
                return false;
            }
            
            return false;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("语音合成失败", e);
            return false;
        }
    }
    
    /**
     * 文字转语音（异步）
     */
    public CompletableFuture<Boolean> synthesizeSpeechAsync(String text) {
        return CompletableFuture.supplyAsync(() -> synthesizeSpeech(text));
    }
}