package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("db_moderation_log")
public class ModerationLog {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer contentId;
    private String contentType;
    private String text;
    private String action;
    private String reason;
    private String sentimentLabel;
    private Double sentimentConfidence;
    private String sensitiveWords;
    private Integer riskScore;
    private Integer isFallback;
    private Date createTime;
}
