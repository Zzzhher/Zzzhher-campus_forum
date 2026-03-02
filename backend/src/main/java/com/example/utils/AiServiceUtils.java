package com.example.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.entity.dto.ModerationLog;
import com.example.mapper.ModerationLogMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class AiServiceUtils {

    // 从配置文件读取AI服务配置
    @Value("${spring.ai.moderation.service-url:http://localhost:5000}")
    private String aiServiceUrl;

    @Value("${spring.ai.moderation.failure-threshold:3}")
    private int failureThreshold;

    @Value("${spring.ai.moderation.circuit-breaker-timeout:60000}")
    private long circuitBreakerTimeout;

    private static String AI_SERVICE_URL;
    private static int FAILURE_THRESHOLD;
    private static long CIRCUIT_BREAKER_TIMEOUT;

    private static final String MODERATE_ENDPOINT = "/moderate";
    private static final String HEALTH_ENDPOINT = "/health";
    private static final String SENSITIVE_CHECK_ENDPOINT = "/sensitive_words/check";

    // 服务降级相关配置
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
    private static ModerationLogMapper moderationLogMapper;

    @PostConstruct
    public void init() {
        AI_SERVICE_URL = this.aiServiceUrl;
        FAILURE_THRESHOLD = this.failureThreshold;
        CIRCUIT_BREAKER_TIMEOUT = this.circuitBreakerTimeout;
        log.info("AI审核服务配置加载完成: URL={}, 失败阈值={}, 熔断超时={}ms",
                AI_SERVICE_URL, FAILURE_THRESHOLD, CIRCUIT_BREAKER_TIMEOUT);

        // 从数据库加载历史审核统计数据
        loadStatsFromDatabase();
    }

    /**
     * 从数据库加载历史审核统计数据
     */
    private void loadStatsFromDatabase() {
        try {
            if (moderationLogMapper != null) {
                // 查询所有审核记录的统计
                List<Map<String, Object>> stats = moderationLogMapper.selectActionStatsLast7Days();
                if (stats != null && !stats.isEmpty()) {
                    // 重置现有统计
                    resetStats();

                    // 从数据库加载数据
                    int total = 0;
                    for (Map<String, Object> stat : stats) {
                        String action = (String) stat.get("action");
                        int count = ((Number) stat.get("count")).intValue();
                        total += count;

                        switch (action != null ? action.toUpperCase() : "") {
                            case "ALLOW", "APPROVE" -> moderationStats.get("allow").addAndGet(count);
                            case "BLOCK", "REJECT" -> moderationStats.get("block").addAndGet(count);
                            case "MANUAL_REVIEW", "MANUAL", "REVIEW" -> moderationStats.get("manual").addAndGet(count);
                        }
                    }
                    moderationStats.get("total").set(total);
                    log.info("从数据库加载审核统计数据完成，总审核数: {}", total);
                } else {
                    log.info("数据库中没有审核记录，统计数据保持为0");
                }
            }
        } catch (Exception e) {
            log.error("从数据库加载审核统计数据失败", e);
        }
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        AiServiceUtils.restTemplate = restTemplate;
    }

    @Autowired
    public void setProhibitedUtils(ProhibitedUtils prohibitedUtils) {
        AiServiceUtils.prohibitedUtils = prohibitedUtils;
    }

    @Autowired
    public void setModerationLogMapper(ModerationLogMapper moderationLogMapper) {
        AiServiceUtils.moderationLogMapper = moderationLogMapper;
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
    public static void logModeration(int contentId, String contentType, String text, String reason,
            JSONObject moderationResult, String action) {
        // 记录日志
        log.info("审核日志 - 内容ID: {}, 类型: {}, 原因: {}, 文本: {}",
                contentId, contentType, reason,
                text != null && text.length() > 50 ? text.substring(0, 50) + "..." : text);

        // 从审核结果中提取信息
        String sentimentLabel = null;
        Double sentimentConfidence = null;
        String sensitiveWords = null;
        Integer riskScore = 0;

        if (moderationResult != null && moderationResult.getBoolean("success")) {
            JSONObject data = moderationResult.getJSONObject("data");
            if (data != null) {
                // 提取情感分析信息
                JSONObject sentiment = data.getJSONObject("sentiment");
                if (sentiment != null) {
                    sentimentLabel = sentiment.getString("label");
                    sentimentConfidence = sentiment.getDouble("confidence");
                }

                // 提取敏感词信息
                JSONObject sensitiveWordsObj = data.getJSONObject("sensitive_words");
                if (sensitiveWordsObj != null && sensitiveWordsObj.getBoolean("matched")) {
                    JSONArray wordsArray = sensitiveWordsObj.getJSONArray("words");
                    if (wordsArray != null && !wordsArray.isEmpty()) {
                        sensitiveWords = String.join(",", wordsArray.toJavaList(String.class));
                    }
                    riskScore = sensitiveWordsObj.getIntValue("risk_score");
                }
            }
        }

        // 保存到数据库
        try {
            if (moderationLogMapper != null) {
                ModerationLog moderationLog = new ModerationLog();
                moderationLog.setContentId(contentId);
                moderationLog.setContentType(contentType);
                moderationLog.setText(text);
                moderationLog.setAction(action != null ? action.toUpperCase() : "ALLOW");
                moderationLog.setReason(reason);
                moderationLog.setSentimentLabel(sentimentLabel);
                moderationLog.setSentimentConfidence(sentimentConfidence);
                moderationLog.setSensitiveWords(sensitiveWords);
                moderationLog.setRiskScore(riskScore);
                moderationLog.setIsFallback(0);
                moderationLog.setCreateTime(new Date());
                moderationLogMapper.insert(moderationLog);
                log.info("审核日志已保存到数据库");
            }
        } catch (Exception e) {
            log.error("保存审核日志到数据库失败", e);
        }
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
            log.error("敏感词检测失败", e);
            JSONObject result = new JSONObject();
            result.put("success", false);
            result.put("error", e.getMessage());
            return result;
        }
    }

    /**
     * 获取审核统计信息
     *
     * @return 统计信息
     */
    public static Map<String, Integer> getModerationStats() {
        Map<String, Integer> stats = new HashMap<>();
        moderationStats.forEach((k, v) -> stats.put(k, v.get()));

        // 计算百分比
        int total = stats.getOrDefault("total", 0);
        if (total > 0) {
            stats.put("allowRate", (int) Math.round(stats.getOrDefault("allow", 0) * 100.0 / total));
            stats.put("blockRate", (int) Math.round(stats.getOrDefault("block", 0) * 100.0 / total));
            stats.put("manualRate", (int) Math.round(stats.getOrDefault("manual", 0) * 100.0 / total));
        }

        // 添加熔断器状态
        stats.put("circuitBreakerOpen", circuitBreakerOpen.get() ? 1 : 0);
        stats.put("consecutiveFailures", consecutiveFailures.get());

        return stats;
    }

    /**
     * 重置审核统计
     */
    public static void resetStats() {
        moderationStats.forEach((k, v) -> v.set(0));
        log.info("审核统计已重置");
    }

    /**
     * 重置熔断器
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
