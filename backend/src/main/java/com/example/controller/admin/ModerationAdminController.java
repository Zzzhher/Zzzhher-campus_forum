package com.example.controller.admin;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.PageRestBean;
import com.example.entity.RestBean;
import com.example.entity.dto.ModerationLog;
import com.example.entity.dto.Topic;
import com.example.entity.dto.TopicComment;
import com.example.mapper.ModerationLogMapper;
import com.example.service.TopicService;
import com.example.utils.AiServiceUtils;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/moderation")
public class ModerationAdminController {

    @Resource
    TopicService topicService;

    @Resource
    ModerationLogMapper moderationLogMapper;

    /**
     * 获取待审核的帖子列表
     *
     * @param page 页码
     * @param size 每页大小
     * @return 待审核帖子列表
     */
    @GetMapping("/pending-topics")
    public PageRestBean<Topic> listPendingTopics(@RequestParam int page, @RequestParam int size) {
        return PageRestBean.success(topicService.listPendingReviewTopics(page, size));
    }

    /**
     * 获取待审核的评论列表
     *
     * @param page 页码
     * @param size 每页大小
     * @return 待审核评论列表
     */
    @GetMapping("/pending-comments")
    public PageRestBean<TopicComment> listPendingComments(@RequestParam int page, @RequestParam int size) {
        return PageRestBean.success(topicService.listPendingReviewComments(page, size));
    }

    /**
     * 批准帖子
     *
     * @param tid 帖子ID
     * @return 操作结果
     */
    @GetMapping("/approve-topic")
    public RestBean<Void> approveTopic(@RequestParam int tid) {
        topicService.approveTopic(tid);
        return RestBean.success();
    }

    /**
     * 拒绝帖子
     *
     * @param tid 帖子ID
     * @return 操作结果
     */
    @GetMapping("/reject-topic")
    public RestBean<Void> rejectTopic(@RequestParam int tid) {
        topicService.rejectTopic(tid);
        return RestBean.success();
    }

    /**
     * 批准评论
     *
     * @param id 评论ID
     * @return 操作结果
     */
    @GetMapping("/approve-comment")
    public RestBean<Void> approveComment(@RequestParam int id) {
        topicService.approveComment(id);
        return RestBean.success();
    }

    /**
     * 拒绝评论
     *
     * @param id 评论ID
     * @return 操作结果
     */
    @GetMapping("/reject-comment")
    public RestBean<Void> rejectComment(@RequestParam int id) {
        topicService.rejectComment(id);
        return RestBean.success();
    }

    /**
     * 批量批准帖子
     *
     * @param ids 帖子ID列表
     * @return 操作结果
     */
    @PostMapping("/batch-approve-topics")
    public RestBean<Void> batchApproveTopics(@RequestBody List<Integer> ids) {
        topicService.batchApproveTopics(ids);
        return RestBean.success();
    }

    /**
     * 批量拒绝帖子
     *
     * @param ids 帖子ID列表
     * @return 操作结果
     */
    @PostMapping("/batch-reject-topics")
    public RestBean<Void> batchRejectTopics(@RequestBody List<Integer> ids) {
        topicService.batchRejectTopics(ids);
        return RestBean.success();
    }

    /**
     * 批量批准评论
     *
     * @param ids 评论ID列表
     * @return 操作结果
     */
    @PostMapping("/batch-approve-comments")
    public RestBean<Void> batchApproveComments(@RequestBody List<Integer> ids) {
        topicService.batchApproveComments(ids);
        return RestBean.success();
    }

    /**
     * 批量拒绝评论
     *
     * @param ids 评论ID列表
     * @return 操作结果
     */
    @PostMapping("/batch-reject-comments")
    public RestBean<Void> batchRejectComments(@RequestBody List<Integer> ids) {
        topicService.batchRejectComments(ids);
        return RestBean.success();
    }

    /**
     * 获取审核统计信息
     *
     * @return 审核统计数据
     */
    @GetMapping("/stats")
    public RestBean<Map<String, Integer>> getModerationStats() {
        return RestBean.success(AiServiceUtils.getModerationStats());
    }

    /**
     * 重置审核统计
     *
     * @return 操作结果
     */
    @GetMapping("/stats/reset")
    public RestBean<Void> resetModerationStats() {
        AiServiceUtils.resetStats();
        return RestBean.success();
    }

    /**
     * 重置熔断器
     *
     * @return 操作结果
     */
    @GetMapping("/circuit-breaker/reset")
    public RestBean<Void> resetCircuitBreaker() {
        AiServiceUtils.resetCircuitBreaker();
        return RestBean.success();
    }

    /**
     * 检查AI服务健康状态
     *
     * @return 健康状态
     */
    @GetMapping("/health")
    public RestBean<JSONObject> checkAiServiceHealth() {
        JSONObject health = new JSONObject();
        health.put("available", AiServiceUtils.checkHealth());
        health.put("stats", AiServiceUtils.getModerationStats());
        return RestBean.success(health);
    }

    /**
     * 获取AI审核案例列表
     *
     * @param page     页码
     * @param size     每页大小
     * @param search   搜索关键词
     * @param category 分类筛选
     * @param decision 审核结果筛选
     * @return 审核案例列表
     */
    @GetMapping("/cases")
    public PageRestBean<Map<String, Object>> listAICases(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String decision) {

        Page<ModerationLog> pageParam = new Page<>(page, size);
        QueryWrapper<ModerationLog> queryWrapper = getModerationLogQueryWrapper(search, category, decision);

        Page<ModerationLog> resultPage = moderationLogMapper.selectPage(pageParam, queryWrapper);

        // 转换为前端需要的格式
        List<Map<String, Object>> records = resultPage.getRecords().stream().map(log -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", log.getId());
            map.put("content", log.getText());
            map.put("category", log.getContentType());
            map.put("decision", log.getAction());
            map.put("decisionText", getActionText(log.getAction()));
            map.put("reason", log.getReason());
            map.put("difficulty", "low");
            map.put("createdAt", log.getCreateTime());
            return map;
        }).collect(Collectors.toList());

        return PageRestBean.success(records, resultPage.getTotal(), resultPage.getCurrent());
    }

    @NotNull
    private static QueryWrapper<ModerationLog> getModerationLogQueryWrapper(String search, String category,
            String decision) {
        QueryWrapper<ModerationLog> queryWrapper = new QueryWrapper<>();

        // 搜索关键词
        if (search != null && !search.isEmpty()) {
            queryWrapper.like("text", search);
        }

        // 分类筛选
        if (category != null && !category.isEmpty()) {
            queryWrapper.eq("content_type", category);
        }

        // 审核结果筛选
        if (decision != null && !decision.isEmpty()) {
            queryWrapper.eq("action", decision.toUpperCase());
        }

        queryWrapper.orderByDesc("create_time");
        return queryWrapper;
    }

    /**
     * 获取审核日志列表
     *
     * @param page      页码
     * @param size      每页大小
     * @param search    搜索关键词
     * @param status    状态筛选
     * @param timeRange 时间范围
     * @return 审核日志列表
     */
    @GetMapping("/logs")
    public PageRestBean<Map<String, Object>> listAuditLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String timeRange) {

        Page<ModerationLog> pageParam = new Page<>(page, size);
        QueryWrapper<ModerationLog> queryWrapper = new QueryWrapper<>();

        // 搜索关键词
        if (search != null && !search.isEmpty()) {
            queryWrapper.like("text", search);
        }

        // 状态筛选
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("action", status.toUpperCase());
        }

        // 时间范围筛选
        if (timeRange != null) {
            switch (timeRange) {
                case "today":
                    queryWrapper.apply("DATE(create_time) = CURDATE()");
                    break;
                case "week":
                    queryWrapper.apply("create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY)");
                    break;
                case "month":
                    queryWrapper.apply("create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY)");
                    break;
            }
        }

        queryWrapper.orderByDesc("create_time");

        Page<ModerationLog> resultPage = moderationLogMapper.selectPage(pageParam, queryWrapper);

        // 转换为前端需要的格式
        List<Map<String, Object>> records = resultPage.getRecords().stream().map(log -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", log.getId());
            map.put("content", log.getText());
            map.put("type", log.getContentType());
            map.put("user", "user" + log.getContentId());
            map.put("status", log.getAction());
            map.put("statusText", getActionText(log.getAction()));
            map.put("reason", log.getReason());
            map.put("time", log.getCreateTime() != null ? log.getCreateTime().toString().substring(11, 16) : "");
            return map;
        }).collect(Collectors.toList());

        return PageRestBean.success(records, resultPage.getTotal(), resultPage.getCurrent());
    }

    private String getActionText(String action) {
        if ("allowed".equals(action) || "ALLOW".equals(action))
            return "允许";
        if ("blocked".equals(action) || "BLOCK".equals(action))
            return "拦截";
        if ("reviewed".equals(action) || "MANUAL_REVIEW".equals(action))
            return "人工复核";
        return "未知";
    }
}
