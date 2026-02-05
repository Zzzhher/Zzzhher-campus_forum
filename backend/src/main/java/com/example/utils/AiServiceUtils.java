package com.example.utils;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class AiServiceUtils {

    private static final String AI_SERVICE_URL = "http://localhost:5000";
    private static final String MODERATE_ENDPOINT = "/moderate";
    private static final String HEALTH_ENDPOINT = "/health";

    private static RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        AiServiceUtils.restTemplate = restTemplate;
    }

    /**
     * 调用AI服务进行内容审核
     * 
     * @param text 需要审核的文本内容
     * @return 审核结果
     */
    public static JSONObject moderateContent(String text) {
        try {
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("text", text);

            // 构建请求实体
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                    AI_SERVICE_URL + MODERATE_ENDPOINT,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            // 解析响应
            return JSONObject.parseObject(response.getBody());
        } catch (Exception e) {
            // 记录错误日志
            e.printStackTrace();
            // 返回错误结果
            JSONObject errorResult = new JSONObject();
            errorResult.put("success", false);
            errorResult.put("error", "AI服务调用失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 检查AI服务健康状态
     * 
     * @return 健康状态
     */
    public static boolean checkHealth() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    AI_SERVICE_URL + HEALTH_ENDPOINT,
                    String.class);
            JSONObject result = JSONObject.parseObject(response.getBody());
            return result.getBoolean("status") != null && result.getBoolean("status");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从审核结果中提取审核决策
     * 
     * @param moderationResult 审核结果
     * @return 审核决策
     */
    public static ModerationDecision getModerationDecision(JSONObject moderationResult) {
        System.out.println("AI服务返回的审核结果: " + moderationResult.toJSONString());

        if (moderationResult == null || !moderationResult.getBoolean("success")) {
            System.out.println("审核结果解析失败，返回人工审核");
            return ModerationDecision.MANUAL_REVIEW;
        }

        // 尝试多种方式获取action字段
        String action = null;

        // 方式1: 从data字段中获取
        JSONObject data = moderationResult.getJSONObject("data");
        if (data != null) {
            action = data.getString("action");
            System.out.println("从data字段中获取action: " + action);
        }

        // 方式2: 直接从审核结果中获取
        if (action == null) {
            action = moderationResult.getString("action");
            System.out.println("直接从审核结果中获取action: " + action);
        }

        // 方式3: 从result字段中获取
        if (action == null) {
            action = moderationResult.getString("result");
            System.out.println("从result字段中获取action: " + action);
        }

        if (action == null) {
            System.out.println("未找到action字段，返回人工审核");
            return ModerationDecision.MANUAL_REVIEW;
        }

        // 统一转换为大写进行比较
        action = action.toUpperCase();
        System.out.println("最终action值: " + action);

        switch (action) {
            case "ALLOW":
            case "APPROVE":
                return ModerationDecision.ALLOW;
            case "BLOCK":
            case "REJECT":
                return ModerationDecision.BLOCK;
            case "FLAG":
            case "MANUAL":
            case "REVIEW":
                return ModerationDecision.MANUAL_REVIEW;
            default:
                System.out.println("未知action值，返回人工审核");
                return ModerationDecision.MANUAL_REVIEW;
        }
    }

    /**
     * 审核决策枚举
     */
    public enum ModerationDecision {
        ALLOW, // 允许通过
        BLOCK, // 拒绝通过
        MANUAL_REVIEW // 需要人工审核
    }
}
