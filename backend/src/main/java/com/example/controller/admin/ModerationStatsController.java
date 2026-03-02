package com.example.controller.admin;

import com.alibaba.fastjson2.JSONObject;
import com.example.entity.RestBean;
import com.example.mapper.ModerationLogMapper;
import com.example.utils.AiServiceUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/moderation")
public class ModerationStatsController {

    @Resource
    ModerationLogMapper moderationLogMapper;

    /**
     * 获取情感分布统计
     *
     * @return 情感分布数据
     */
    @GetMapping("/stats/sentiment")
    public RestBean<List<Map<String, Object>>> getSentimentStats() {
        List<Map<String, Object>> stats = moderationLogMapper.selectSentimentStatsLast7Days();
        return RestBean.success(stats);
    }

    /**
     * 获取审核决策分布统计
     *
     * @return 审核决策分布数据
     */
    @GetMapping("/stats/action")
    public RestBean<List<Map<String, Object>>> getActionStats() {
        List<Map<String, Object>> stats = moderationLogMapper.selectActionStatsLast7Days();
        return RestBean.success(stats);
    }

    /**
     * 获取每日审核趋势
     *
     * @return 每日审核数量趋势
     */
    @GetMapping("/stats/daily")
    public RestBean<List<Map<String, Object>>> getDailyStats() {
        List<Map<String, Object>> stats = moderationLogMapper.selectDailyStatsLast30Days();
        return RestBean.success(stats);
    }

    /**
     * 获取敏感词词云数据
     *
     * @return 敏感词频率统计
     */
    @GetMapping("/stats/sensitive-words")
    public RestBean<List<Map<String, Object>>> getSensitiveWordsStats() {
        List<String> wordsList = moderationLogMapper.selectSensitiveWordsLast7Days();

        // 统计词频
        Map<String, Integer> wordCount = new HashMap<>();
        for (String words : wordsList) {
            if (words != null && !words.isEmpty()) {
                String[] wordArray = words.split(",");
                for (String word : wordArray) {
                    word = word.trim();
                    if (!word.isEmpty()) {
                        wordCount.merge(word, 1, Integer::sum);
                    }
                }
            }
        }

        // 转换为列表格式
        List<Map<String, Object>> result = wordCount.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", entry.getKey());
                    map.put("value", entry.getValue());
                    return map;
                })
                .sorted((a, b) -> ((Integer) b.get("value")).compareTo((Integer) a.get("value")))
                .limit(50)
                .collect(Collectors.toList());

        return RestBean.success(result);
    }

    /**
     * 获取综合统计面板数据
     *
     * @return 综合统计数据
     */
    @GetMapping("/stats/dashboard")
    public RestBean<Map<String, Object>> getDashboardStats() {
        Map<String, Object> dashboard = new HashMap<>();

        // 获取实时统计
        Map<String, Integer> realtimeStats = AiServiceUtils.getModerationStats();
        dashboard.put("realtime", realtimeStats);

        // 获取情感分布
        List<Map<String, Object>> sentimentStats = moderationLogMapper.selectSentimentStatsLast7Days();
        dashboard.put("sentiment", sentimentStats);

        // 获取审核决策分布
        List<Map<String, Object>> actionStats = moderationLogMapper.selectActionStatsLast7Days();
        dashboard.put("action", actionStats);

        // 获取每日趋势
        List<Map<String, Object>> dailyStats = moderationLogMapper.selectDailyStatsLast30Days();
        dashboard.put("daily", dailyStats);

        // AI服务健康状态
        dashboard.put("aiServiceAvailable", AiServiceUtils.checkHealth());

        return RestBean.success(dashboard);
    }

    /**
     * 获取情感热力图数据（按内容类型）
     *
     * @return 情感热力图数据
     */
    @GetMapping("/stats/emotion-heatmap")
    public RestBean<List<Map<String, Object>>> getEmotionHeatmapStats() {
        // 从数据库查询按内容类型的情感分布数据
        List<Map<String, Object>> heatmapData = moderationLogMapper.selectSentimentStatsByContentType();

        // 如果没有数据，返回默认数据
        if (heatmapData.isEmpty()) {
            heatmapData = new ArrayList<>();

            Map<String, Object> data1 = new HashMap<>();
            data1.put("name", "校园活动");
            data1.put("positive", 65);
            data1.put("neutral", 20);
            data1.put("negative", 15);
            heatmapData.add(data1);

            Map<String, Object> data2 = new HashMap<>();
            data2.put("name", "表白墙");
            data2.put("positive", 75);
            data2.put("neutral", 15);
            data2.put("negative", 10);
            heatmapData.add(data2);

            Map<String, Object> data3 = new HashMap<>();
            data3.put("name", "失物招领");
            data3.put("positive", 80);
            data3.put("neutral", 15);
            data3.put("negative", 5);
            heatmapData.add(data3);

            Map<String, Object> data4 = new HashMap<>();
            data4.put("name", "帖子广场");
            data4.put("positive", 60);
            data4.put("neutral", 25);
            data4.put("negative", 15);
            heatmapData.add(data4);
        }

        return RestBean.success(heatmapData);
    }
}
