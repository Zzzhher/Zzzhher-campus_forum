package com.example.utils;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class AiServiceUtils {

    private static final String AI_SERVICE_URL = "http://localhost:5000";
    private static final String MODERATE_ENDPOINT = "/moderate";
    private static final String HEALTH_ENDPOINT = "/health";
    private static final String SENSITIVE_CHECK_ENDPOINT = "/sensitive_words/check";

    // 服务降级相关配置
    private static final int FAILURE_THRESHOLD = 3; // 连续失败阈值
    private static final long CIRCUIT_BREAKER_TIMEOUT = 60000; // 熔断超时时间（毫秒）
    private static final AtomicInteger consecutiveFailures = new AtomicInteger(0);
    private static final AtomicBoolean circuitBreakerOpen = new AtomicBoolean(false);
    private static volatile long circuitBreakerOpenTime = 0;

    // 审核统计
    private static final Map<String, AtomicInteger> moderationStats = new HashMap<>();

    static {
        moderationStats.put("total", new AtomicInteger(0));
        moderationStats.put("allow", new AtomicInteger(0));
        moderationStats.put("block", new AtomicInteger(0));
        moderationStats.put("manual", new AtomicInteger(0));
        moderationStats.put("fallback", new AtomicInteger(0));
    }

    private static RestTemplate restTemplate;
    private static ProhibitedUtils prohibitedUtils;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        AiServiceUtils.restTemplate = restTemplate;
    }

    @Autowired
    public void setProhibitedUtils(ProhibitedUtils prohibitedUtils) {
        AiServiceUtils.prohibitedUtils = prohibitedUtils;
    }

    /**
     * 调用AI服务进行内容审核（带熔断降级机制）
     *
     * @param text         需要审核的文本内容
     * @param contentType  内容类型 (topic/comment/username/signature)
     * @param isConfession 是否为表白墙内容
     * @return 审核结果
     */
    public static JSONObject moderateContent(String text, String contentType, boolean isConfession) {
        // 检查熔断器状态
        if (circuitBreakerOpen.get()) {
            if (System.currentTimeMillis() - circuitBreakerOpenTime > CIRCUIT_BREAKER_TIMEOUT) {
                // 熔断超时，尝试恢复
                circuitBreakerOpen.set(false);
                consecutiveFailures.set(0);
                log.info("熔断器超时，尝试恢复AI服务调用");
            } else {
                // 熔断器开启，直接走降级逻辑
                log.warn("熔断器开启，使用本地降级审核");
                return fallbackModeration(text, "AI服务熔断中");
            }
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("text", text);
            requestBody.put("content_type", contentType);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    AI_SERVICE_URL + MODERATE_ENDPOINT,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            // 调用成功，重置失败计数
            consecutiveFailures.set(0);

            JSONObject result = JSONObject.parseObject(response.getBody());
            updateStats(result);
            return result;

        } catch (ResourceAccessException e) {
            // 连接失败，增加失败计数
            int failures = consecutiveFailures.incrementAndGet();
            log.error("AI服务连接失败，连续失败次数: {}", failures, e);

            if (failures >= FAILURE_THRESHOLD) {
                // 开启熔断
                circuitBreakerOpen.set(true);
                circuitBreakerOpenTime = System.currentTimeMillis();
                log.warn("连续失败{}次，开启熔断器", FAILURE_THRESHOLD);
            }

            return fallbackModeration(text, "AI服务不可用: " + e.getMessage());

        } catch (Exception e) {
            log.error("AI服务调用异常", e);
            return fallbackModeration(text, "AI服务异常: " + e.getMessage());
        }
    }

    /**
     * 降级审核 - 使用本地敏感词检测
     */
    private static JSONObject fallbackModeration(String text, String reason) {
        moderationStats.get("fallback").incrementAndGet();
        moderationStats.get("total").incrementAndGet();

        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("fallback", true);

        JSONObject data = new JSONObject();

        // 使用本地敏感词检测
        boolean hasProhibitedWord = prohibitedUtils != null && prohibitedUtils.containsProhibitedWord(text);

        if (hasProhibitedWord) {
            data.put("action", "BLOCK");
            data.put("reason", "【降级审核】包含敏感词");
            moderationStats.get("block").incrementAndGet();
        } else {
            // 降级时默认人工审核，确保安全
            data.put("action", "MANUAL_REVIEW");
            data.put("reason", "【降级审核】AI服务不可用，转人工审核: " + reason);
            moderationStats.get("manual").incrementAndGet();
        }

        data.put("sentiment", "unknown");
        data.put("confidence", 0.0);
        result.put("data", data);

        log.info("降级审核结果: {}, 原因: {}", data.getString("action"), data.getString("reason"));
        return result;
    }

    /**
     * 更新审核统计
     */
    private static void updateStats(JSONObject result) {
        moderationStats.get("total").incrementAndGet();

        if (result != null && result.getBoolean("success")) {
            JSONObject data = result.getJSONObject("data");
            if (data != null) {
                String action = data.getString("action");
                if (action != null) {
                    switch (action.toUpperCase()) {
                        case "ALLOW", "APPROVE" -> moderationStats.get("allow").incrementAndGet();
                        case "BLOCK", "REJECT" -> moderationStats.get("block").incrementAndGet();
                        case "MANUAL_REVIEW", "MANUAL", "REVIEW" -> moderationStats.get("manual").incrementAndGet();
                    }
                }
            }
        }
    }

    /**
     * 调用AI服务进行内容审核 (简化版，默认不是表白墙)
     *
     * @param text 需要审核的文本内容
     * @return 审核结果
     */
    public static JSONObject moderateContent(String text) {
        return moderateContent(text, "comment", false);
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
        log.debug("AI服务返回的审核结果: {}", moderationResult != null ? moderationResult.toJSONString() : "null");

        if (moderationResult == null || !moderationResult.getBoolean("success")) {
            log.warn("审核结果解析失败，返回人工审核");
            return ModerationDecision.MANUAL_REVIEW;
        }

        JSONObject data = moderationResult.getJSONObject("data");
        if (data == null) {
            log.warn("审核结果中data字段为空，返回人工审核");
            return ModerationDecision.MANUAL_REVIEW;
        }

        String action = data.getString("action");
        if (action == null) {
            log.warn("审核结果中action字段为空，返回人工审核");
            return ModerationDecision.MANUAL_REVIEW;
        }

        action = action.toUpperCase();
        log.debug("审核决策: {}", action);

        return switch (action) {
            case "ALLOW", "APPROVE" -> ModerationDecision.ALLOW;
            case "BLOCK", "REJECT" -> ModerationDecision.BLOCK;
            case "FLAG", "MANUAL", "REVIEW", "MANUAL_REVIEW" -> ModerationDecision.MANUAL_REVIEW;
            default -> {
                log.warn("未知的审核决策: {}，返回人工审核", action);
                yield ModerationDecision.MANUAL_REVIEW;
            }
        };
    }

    /**
     * 从审核结果中提取审核原因
     *
     * @param moderationResult 审核结果
     * @return 审核原因
     */
    public static String getModerationReason(JSONObject moderationResult) {
        if (moderationResult == null || !moderationResult.getBoolean("success")) {
            return null;
        }

        JSONObject data = moderationResult.getJSONObject("data");
        if (data == null) {
            return null;
        }

        return data.getString("reason");
    }

    /**
     * 记录审核日志
     *
     * @param contentId   内容ID
     * @param contentType 内容类型
     * @param text        审核文本
     * @param reason      审核原因
     */
    public static void logModeration(int contentId, String contentType, String text, String reason) {
        // 这里可以实现将审核日志写入数据库的逻辑
        log.info("审核日志 - 内容ID: {}, 类型: {}, 原因: {}, 文本: {}",
                contentId, contentType, reason,
                text != null && text.length() > 50 ? text.substring(0, 50) + "..." : text);
    }

    /**
     * 敏感词检测
     *
     * @param text 待检测文本
     * @return 检测结果
     */
    public static JSONObject checkSensitiveWords(String text) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("text", text);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    AI_SERVICE_URL + SENSITIVE_CHECK_ENDPOINT,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            return JSONObject.parseObject(response.getBody());
        } catch (Exception e) {
            log.error("敏感词检测调用失败", e);
            JSONObject errorResult = new JSONObject();
            errorResult.put("success", false);
            errorResult.put("error", "敏感词检测调用失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 获取审核统计信息
     *
     * @return 统计信息
     */
    public static JSONObject getModerationStats() {
        JSONObject stats = new JSONObject();
        stats.put("total", moderationStats.get("total").get());
        stats.put("allow", moderationStats.get("allow").get());
        stats.put("block", moderationStats.get("block").get());
        stats.put("manual", moderationStats.get("manual").get());
        stats.put("fallback", moderationStats.get("fallback").get());

        // 计算通过率
        int total = moderationStats.get("total").get();
        if (total > 0) {
            stats.put("allowRate", String.format("%.2f%%", moderationStats.get("allow").get() * 100.0 / total));
            stats.put("blockRate", String.format("%.2f%%", moderationStats.get("block").get() * 100.0 / total));
            stats.put("manualRate", String.format("%.2f%%", moderationStats.get("manual").get() * 100.0 / total));
        }

        // 服务状态
        stats.put("circuitBreakerOpen", circuitBreakerOpen.get());
        stats.put("consecutiveFailures", consecutiveFailures.get());
        stats.put("aiServiceAvailable", checkHealth());
        stats.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return stats;
    }

    /**
     * 重置审核统计
     */
    public static void resetStats() {
        moderationStats.get("total").set(0);
        moderationStats.get("allow").set(0);
        moderationStats.get("block").set(0);
        moderationStats.get("manual").set(0);
        moderationStats.get("fallback").set(0);
        log.info("审核统计已重置");
    }

    /**
     * 手动重置熔断器
     */
    public static void resetCircuitBreaker() {
        circuitBreakerOpen.set(false);
        consecutiveFailures.set(0);
        log.info("熔断器已手动重置");
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
