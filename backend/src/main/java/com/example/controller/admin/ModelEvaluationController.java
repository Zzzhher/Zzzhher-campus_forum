package com.example.controller.admin;

import com.example.entity.RestBean;
import com.example.entity.dto.ModelEvaluation;
import com.example.mapper.ModelEvaluationMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/admin/model")
public class ModelEvaluationController {

    @Resource
    ModelEvaluationMapper modelEvaluationMapper;

    /**
     * 获取所有模型评估记录
     *
     * @return 评估记录列表
     */
    @GetMapping("/evaluations")
    public RestBean<List<ModelEvaluation>> getAllEvaluations() {
        List<ModelEvaluation> evaluations = modelEvaluationMapper.selectList(null);
        return RestBean.success(evaluations);
    }

    /**
     * 获取最近的评估记录
     *
     * @return 最近10条评估记录
     */
    @GetMapping("/evaluations/recent")
    public RestBean<List<ModelEvaluation>> getRecentEvaluations() {
        List<ModelEvaluation> evaluations = modelEvaluationMapper.selectRecentEvaluations();
        return RestBean.success(evaluations);
    }

    /**
     * 获取最佳模型
     *
     * @return 最佳模型评估记录
     */
    @GetMapping("/evaluations/best")
    public RestBean<ModelEvaluation> getBestModel() {
        ModelEvaluation evaluation = modelEvaluationMapper.selectBestModel();
        return RestBean.success(evaluation);
    }

    /**
     * 根据版本号获取评估记录
     *
     * @param version 模型版本号
     * @return 评估记录
     */
    @GetMapping("/evaluations/version/{version}")
    public RestBean<ModelEvaluation> getEvaluationByVersion(@PathVariable String version) {
        ModelEvaluation evaluation = modelEvaluationMapper.selectByVersion(version);
        return RestBean.success(evaluation);
    }

    /**
     * 添加模型评估记录
     *
     * @param evaluation 评估记录
     * @return 操作结果
     */
    @PostMapping("/evaluations")
    public RestBean<Void> addEvaluation(@RequestBody ModelEvaluation evaluation) {
        if (evaluation.getEvalTime() == null) {
            evaluation.setEvalTime(new Date());
        }
        if (evaluation.getEvalDate() == null) {
            evaluation.setEvalDate(new Date());
        }
        modelEvaluationMapper.insert(evaluation);
        return RestBean.success();
    }

    /**
     * 更新模型评估记录
     *
     * @param id 记录ID
     * @param evaluation 评估记录
     * @return 操作结果
     */
    @PutMapping("/evaluations/{id}")
    public RestBean<Void> updateEvaluation(@PathVariable Integer id, @RequestBody ModelEvaluation evaluation) {
        evaluation.setId(id);
        modelEvaluationMapper.updateById(evaluation);
        return RestBean.success();
    }

    /**
     * 删除模型评估记录
     *
     * @param id 记录ID
     * @return 操作结果
     */
    @DeleteMapping("/evaluations/{id}")
    public RestBean<Void> deleteEvaluation(@PathVariable Integer id) {
        modelEvaluationMapper.deleteById(id);
        return RestBean.success();
    }

    /**
     * 设置最佳模型
     *
     * @param id 记录ID
     * @return 操作结果
     */
    @PostMapping("/evaluations/{id}/set-best")
    public RestBean<Void> setBestModel(@PathVariable Integer id) {
        // 先将所有模型设为非最佳
        List<ModelEvaluation> allModels = modelEvaluationMapper.selectList(null);
        for (ModelEvaluation model : allModels) {
            model.setIsBest(0);
            modelEvaluationMapper.updateById(model);
        }

        // 设置指定模型为最佳
        ModelEvaluation bestModel = new ModelEvaluation();
        bestModel.setId(id);
        bestModel.setIsBest(1);
        modelEvaluationMapper.updateById(bestModel);

        return RestBean.success();
    }

    /**
     * 获取模型评估统计
     *
     * @return 评估统计数据
     */
    @GetMapping("/evaluations/stats")
    public RestBean<java.util.Map<String, Object>> getEvaluationStats() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();

        // 获取最佳模型
        ModelEvaluation bestModel = modelEvaluationMapper.selectBestModel();
        stats.put("bestModel", bestModel);

        // 获取评估次数
        Long totalEvaluations = modelEvaluationMapper.selectCount(null);
        stats.put("totalEvaluations", totalEvaluations);

        // 获取最近评估
        List<ModelEvaluation> recentEvaluations = modelEvaluationMapper.selectRecentEvaluations();
        stats.put("recentEvaluations", recentEvaluations);

        return RestBean.success(stats);
    }
}
