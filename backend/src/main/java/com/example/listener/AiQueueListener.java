package com.example.listener;

import com.alibaba.fastjson2.JSONObject;
import com.example.service.impl.AiServiceProxy;
import com.example.utils.AiServiceUtils;
import com.example.utils.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * AI消息队列监听器
 */
@Slf4j
@Component
public class AiQueueListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AiServiceProxy aiServiceProxy;

    /**
     * 处理AI审核请求
     */
    @RabbitListener(queues = Const.MQ_AI_MODERATION)
    public void handleAiModeration(Map<String, Object> message) {
        try {
            log.info("接收到AI审核请求: {}", message);

            // 提取请求参数
            String text = (String) message.get("text");
            String contentType = (String) message.get("contentType");
            boolean isConfession = message.get("isConfession") != null && (boolean) message.get("isConfession");
            String requestId = (String) message.get("requestId");

            if (text == null) {
                log.error("AI审核请求缺少text参数");
                return;
            }

            // 调用AI服务进行审核
            JSONObject result = AiServiceUtils.moderateContent(text, contentType, isConfession);

            // 发送响应
            Map<String, Object> response = Map.of(
                    "requestId", requestId,
                    "result", result);

            rabbitTemplate.convertAndSend(Const.MQ_AI_RESPONSE, response);
            log.info("AI审核响应已发送: {}", response);

        } catch (Exception e) {
            log.error("处理AI审核请求失败", e);
        }
    }

    /**
     * 处理AI响应
     */
    @RabbitListener(queues = Const.MQ_AI_RESPONSE)
    public void handleAiResponse(Map<String, Object> message) {
        try {
            log.info("接收到AI审核响应: {}", message);

            // 提取响应参数
            String requestId = (String) message.get("requestId");
            JSONObject result = (JSONObject) message.get("result");

            if (requestId != null && result != null) {
                // 调用AI服务代理处理响应
                aiServiceProxy.handleAiResponse(requestId, result);
            }

        } catch (Exception e) {
            log.error("处理AI响应失败", e);
        }
    }
}
