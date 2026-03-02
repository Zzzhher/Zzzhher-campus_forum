package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.ModerationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ModerationLogMapper extends BaseMapper<ModerationLog> {

        @Select("SELECT action, COUNT(*) as count FROM db_moderation_log " +
                        "WHERE create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                        "GROUP BY action")
        List<Map<String, Object>> selectActionStatsLast7Days();

        @Select("SELECT sentiment_label as sentimentLabel, COUNT(*) as count FROM db_moderation_log " +
                        "WHERE sentiment_label IS NOT NULL " +
                        "AND create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                        "GROUP BY sentiment_label")
        List<Map<String, Object>> selectSentimentStatsLast7Days();

        @Select("SELECT DATE(create_time) as date, COUNT(*) as count FROM db_moderation_log " +
                        "WHERE create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY) " +
                        "GROUP BY DATE(create_time) ORDER BY date")
        List<Map<String, Object>> selectDailyStatsLast30Days();

        @Select("SELECT sensitive_words FROM db_moderation_log " +
                        "WHERE sensitive_words IS NOT NULL AND sensitive_words != '' " +
                        "AND create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY)")
        List<String> selectSensitiveWordsLast7Days();

        @Select("SELECT content_type as name, " +
                        "SUM(CASE WHEN sentiment_label = '正向' THEN 1 ELSE 0 END) * 100.0 / COUNT(*) as positive, "
                        +
                        "SUM(CASE WHEN sentiment_label = '中性' THEN 1 ELSE 0 END) * 100.0 / COUNT(*) as neutral, " +
                        "SUM(CASE WHEN sentiment_label = '负向' THEN 1 ELSE 0 END) * 100.0 / COUNT(*) as negative "
                        +
                        "FROM db_moderation_log " +
                        "WHERE sentiment_label IS NOT NULL " +
                        "AND create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                        "GROUP BY content_type")
        List<Map<String, Object>> selectSentimentStatsByContentType();
}
