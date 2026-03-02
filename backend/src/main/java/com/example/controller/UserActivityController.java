package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.AccountService;
import com.example.service.TopicService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 用户活跃度统计控制器
 */
@RestController
@RequestMapping("/api/user/activity")
public class UserActivityController {

    @Resource
    AccountService accountService;

    @Resource
    TopicService topicService;

    /**
     * 获取用户活跃度统计数据
     *
     * @param userId 用户ID
     * @return 活跃度统计数据
     */
    @GetMapping("/stats")
    public RestBean<Map<String, Object>> getUserActivityStats(
            @RequestAttribute(Const.ATTR_USER_ID) int userId) {
        Map<String, Object> stats = new HashMap<>();

        // 获取用户基本信息
        var account = accountService.findAccountById(userId);
        if (account == null) {
            return RestBean.failure(404, "用户不存在");
        }

        // 基础统计
        stats.put("username", account.getUsername());
        stats.put("joinDate", account.getRegisterTime().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .toString());

        // 计算等级和经验值（基于发帖数和互动数）
        int topicCount = topicService.getUserTopicCount(userId);
        int commentCount = topicService.getUserCommentCount(userId);
        int likeCount = topicService.getUserReceivedLikesCount(userId);

        int totalExp = topicCount * 10 + commentCount * 5 + likeCount * 2;
        int level = calculateLevel(totalExp);
        int nextLevelExp = getNextLevelExp(level);

        stats.put("totalTopics", topicCount);
        stats.put("totalComments", commentCount);
        stats.put("totalLikes", likeCount);
        stats.put("level", level);
        stats.put("exp", totalExp);
        stats.put("nextLevelExp", nextLevelExp);

        // 计算排名（基于经验值）
        int rank = calculateRank(totalExp);
        stats.put("rank", rank);

        return RestBean.success(stats);
    }

    /**
     * 获取用户最近7天活跃度趋势
     *
     * @param userId 用户ID
     * @return 7天活跃度数据
     */
    @GetMapping("/trend")
    public RestBean<List<Map<String, Object>>> getUserActivityTrend(
            @RequestAttribute(Const.ATTR_USER_ID) int userId) {
        List<Map<String, Object>> trend = new ArrayList<>();

        // 获取最近7天的数据
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        String[] weekDays = { "周一", "周二", "周三", "周四", "周五", "周六", "周日" };
        int currentDayOfWeek = now.getDayOfWeek().getValue(); // 1-7

        for (int i = 6; i >= 0; i--) {
            LocalDateTime date = now.minus(i, ChronoUnit.DAYS);
            String dateStr = date.format(formatter);

            // 计算星期几
            int dayIndex = (currentDayOfWeek - i - 1 + 7) % 7;
            String weekDay = weekDays[dayIndex];

            // 获取当天的统计数据（这里使用模拟数据，实际应从数据库查询）
            int topics = topicService.getUserTopicCountByDate(userId, date.toLocalDate());
            int comments = topicService.getUserCommentCountByDate(userId, date.toLocalDate());
            int likes = topicService.getUserLikesCountByDate(userId, date.toLocalDate());

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", weekDay);
            dayData.put("fullDate", dateStr);
            dayData.put("topics", topics);
            dayData.put("comments", comments);
            dayData.put("likes", likes);

            trend.add(dayData);
        }

        return RestBean.success(trend);
    }

    /**
     * 获取用户成就徽章
     *
     * @param userId 用户ID
     * @return 成就徽章列表
     */
    @GetMapping("/badges")
    public RestBean<List<Map<String, Object>>> getUserBadges(
            @RequestAttribute(Const.ATTR_USER_ID) int userId) {
        List<Map<String, Object>> badges = new ArrayList<>();

        // 获取用户统计数据
        int topicCount = topicService.getUserTopicCount(userId);
        int commentCount = topicService.getUserCommentCount(userId);
        int likeCount = topicService.getUserReceivedLikesCount(userId);

        // 定义徽章
        badges.add(createBadge(1, "初来乍到", "User", "#909399",
                "完成首次登录", true));
        badges.add(createBadge(2, "话痨新手", "ChatDotSquare", "#67C23A",
                "发布10条评论", commentCount >= 10));
        badges.add(createBadge(3, "内容创作者", "Document", "#409EFF",
                "发布5个主题", topicCount >= 5));
        badges.add(createBadge(4, "受欢迎的人", "Star", "#E6A23C",
                "获得50个赞", likeCount >= 50));
        badges.add(createBadge(5, "论坛达人", "Trophy", "#F56C6C",
                "发布50个主题", topicCount >= 50));
        badges.add(createBadge(6, "意见领袖", "Medal", "#F56C6C",
                "获得500个赞", likeCount >= 500));
        badges.add(createBadge(7, "活跃分子", "Fire", "#F56C6C",
                "连续7天登录", false)); // 需要额外的登录记录
        badges.add(createBadge(8, "社区守护者", "Shield", "#F56C6C",
                "举报10条违规内容", false)); // 需要举报记录

        return RestBean.success(badges);
    }

    /**
     * 获取用户最近动态
     *
     * @param userId 用户ID
     * @return 最近动态列表
     */
    @GetMapping("/recent")
    public RestBean<List<Map<String, Object>>> getRecentActivities(
            @RequestAttribute(Const.ATTR_USER_ID) int userId) {
        List<Map<String, Object>> activities = topicService.getUserRecentActivities(userId, 10);
        return RestBean.success(activities);
    }

    /**
     * 计算用户等级
     *
     * @param exp 经验值
     * @return 等级
     */
    private int calculateLevel(int exp) {
        if (exp < 100)
            return 1;
        if (exp < 300)
            return 2;
        if (exp < 600)
            return 3;
        if (exp < 1000)
            return 4;
        if (exp < 1500)
            return 5;
        if (exp < 2200)
            return 6;
        if (exp < 3000)
            return 7;
        if (exp < 4000)
            return 8;
        if (exp < 5500)
            return 9;
        return 10;
    }

    /**
     * 获取下一级所需经验值
     *
     * @param currentLevel 当前等级
     * @return 下一级所需经验值
     */
    private int getNextLevelExp(int currentLevel) {
        int[] levelExps = { 0, 100, 300, 600, 1000, 1500, 2200, 3000, 4000, 5500, 7500 };
        if (currentLevel >= 10)
            return levelExps[10];
        return levelExps[currentLevel + 1];
    }

    /**
     * 计算用户排名
     *
     * @param exp 经验值
     * @return 排名
     */
    private int calculateRank(int exp) {
        // 这里应该查询数据库中经验值比当前用户高的用户数量
        // 暂时返回模拟数据
        return Math.max(1, 1000 - exp / 10);
    }

    /**
     * 创建徽章对象
     */
    private Map<String, Object> createBadge(int id, String name, String icon,
            String color, String desc, boolean earned) {
        Map<String, Object> badge = new HashMap<>();
        badge.put("id", id);
        badge.put("name", name);
        badge.put("icon", icon);
        badge.put("color", color);
        badge.put("desc", desc);
        badge.put("earned", earned);
        return badge;
    }
}
