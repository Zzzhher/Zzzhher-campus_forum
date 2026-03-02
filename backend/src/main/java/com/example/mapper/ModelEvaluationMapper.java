package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.ModelEvaluation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ModelEvaluationMapper extends BaseMapper<ModelEvaluation> {

    @Select("SELECT * FROM db_model_evaluation WHERE is_best = 1 ORDER BY eval_time DESC LIMIT 1")
    ModelEvaluation selectBestModel();

    @Select("SELECT * FROM db_model_evaluation ORDER BY eval_time DESC LIMIT 10")
    List<ModelEvaluation> selectRecentEvaluations();

    @Select("SELECT * FROM db_model_evaluation WHERE model_version = #{version}")
    ModelEvaluation selectByVersion(String version);
}
