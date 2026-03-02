package com.example.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.example.service.AiService;
import com.example.utils.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * AI服务代理，通过消息队列实现松耦合集成
 */
@Slf4j
@Service
@Primary
public class AiServiceProxy implements AiService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AiServiceImpl aiService;

    // 用于存储响应的Future
    private final ConcurrentMap<String, CompletableFuture<JSONObject>> responseFutures = new ConcurrentHashMap<>();

    @Override
    public SseEmitter chatWithAi(com.alibaba.fastjson2.JSONArray context) {
        // 对于实时聊天，仍然使用直接调用
        return aiService.chatWithAi(context);
    }

    @Override
    public Integer analyzeSentiment(String text) {
        try {
            // 生成请求ID
            String requestId = UUID.randomUUID().toString();

            // 创建CompletableFuture用于等待响应
            CompletableFuture<JSONObject> future = new CompletableFuture<>();
            responseFutures.put(requestId, future);

            // 构建请求消息
            Map<String, Object> message = Map.of(
                    "text", text,
                    "contentType", "comment",
                    "isConfession", false,
                    "requestId", requestId);

            // 发送到消息队列
            rabbitTemplate.convertAndSend(Const.MQ_AI_MODERATION, message);
            log.info("情感分析请求已发送到消息队列: {}", message);

            // 等待响应，设置超时
            JSONObject result = future.get(10, TimeUnit.SECONDS);

            // 从响应中提取情感分析结果
            if (result != null && result.getBoolean("success")) {
                JSONObject data = result.getJSONObject("data");
                if (data != null) {
                    String sentimentLabel = data.getString("sentiment");
                    if (sentimentLabel != null) {
                        switch (sentimentLabel) {
                            case "正向":
                            case "positive":
                                return 1;
                            case "负向":
                            case "negative":
                                return -1;
                            case "中性":
                            case "neutral":
                            default:
                                return 0;
                        }
                    }

                    // 如果没有情感标签，根据置信度判断
                    Double confidence = data.getDouble("confidence");
                    if (confidence != null) {
                        if (confidence > 0.7) {
                            return 1;
                        } else if (confidence < 0.3) {
                            return -1;
                        }
                    }
                }
            }

            return 0;
        } catch (Exception e) {
            log.error("情感分析失败", e);
            // 失败时使用直接调用作为降级方案
            return aiService.analyzeSentiment(text);
        } finally {
            // 清理Future
            responseFutures.values().removeIf(CompletableFuture::isDone);
        }
    }

    /**
     * 处理AI服务响应
     */
    public void handleAiResponse(String requestId, JSONObject result) {
        CompletableFuture<JSONObject> future = responseFutures.remove(requestId);
        if (future != null) {
            future.complete(result);
        }
    }
}
