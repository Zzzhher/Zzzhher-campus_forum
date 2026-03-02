package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("db_model_evaluation")
public class ModelEvaluation {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String modelVersion;
    private Double accuracy;
    private Double precisionScore;
    private Double recall;
    private Double f1Score;
    private Date evalDate;
    private Date evalTime;
    private Integer testSamples;
    private Integer trainingSamples;
    private Integer epochs;
    private Integer batchSize;
    private Double learningRate;
    private String modelPath;
    private String confusionMatrix;
    private String classReport;
    private Integer isBest;
    private String remark;
}
