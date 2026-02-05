package com.example.controller.admin;

import com.example.entity.PageRestBean;
import com.example.entity.RestBean;
import com.example.entity.dto.Topic;
import com.example.entity.dto.TopicComment;
import com.example.service.TopicService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/admin/moderation")
public class ModerationAdminController {

    @Resource
    TopicService topicService;

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
    public RestBean<Void> batchApproveTopics(@RequestParam List<Integer> ids) {
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
    public RestBean<Void> batchRejectTopics(@RequestParam List<Integer> ids) {
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
    public RestBean<Void> batchApproveComments(@RequestParam List<Integer> ids) {
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
    public RestBean<Void> batchRejectComments(@RequestParam List<Integer> ids) {
        topicService.batchRejectComments(ids);
        return RestBean.success();
    }
}
