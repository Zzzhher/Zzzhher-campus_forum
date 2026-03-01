import { get, post } from "@/net";

/**
 * 获取待审核帖子列表
 * @param page 页码
 * @param size 每页大小
 * @param success 成功回调
 */
export const apiModerationPendingTopics = (page, size, success) =>
    get(`/api/admin/moderation/pending-topics?page=${page}&size=${size}`, success);

/**
 * 获取待审核评论列表
 * @param page 页码
 * @param size 每页大小
 * @param success 成功回调
 */
export const apiModerationPendingComments = (page, size, success) =>
    get(`/api/admin/moderation/pending-comments?page=${page}&size=${size}`, success);

/**
 * 批准帖子
 * @param tid 帖子ID
 * @param success 成功回调
 */
export const apiModerationApproveTopic = (tid, success) =>
    get(`/api/admin/moderation/approve-topic?tid=${tid}`, success);

/**
 * 拒绝帖子
 * @param tid 帖子ID
 * @param success 成功回调
 */
export const apiModerationRejectTopic = (tid, success) =>
    get(`/api/admin/moderation/reject-topic?tid=${tid}`, success);

/**
 * 批准评论
 * @param id 评论ID
 * @param success 成功回调
 */
export const apiModerationApproveComment = (id, success) =>
    get(`/api/admin/moderation/approve-comment?id=${id}`, success);

/**
 * 拒绝评论
 * @param id 评论ID
 * @param success 成功回调
 */
export const apiModerationRejectComment = (id, success) =>
    get(`/api/admin/moderation/reject-comment?id=${id}`, success);

/**
 * 批量批准帖子
 * @param ids 帖子ID列表
 * @param success 成功回调
 */
export const apiModerationBatchApproveTopics = (ids, success) =>
    post(`/api/admin/moderation/batch-approve-topics`, { ids }, success);

/**
 * 批量拒绝帖子
 * @param ids 帖子ID列表
 * @param success 成功回调
 */
export const apiModerationBatchRejectTopics = (ids, success) =>
    post(`/api/admin/moderation/batch-reject-topics`, { ids }, success);

/**
 * 批量批准评论
 * @param ids 评论ID列表
 * @param success 成功回调
 */
export const apiModerationBatchApproveComments = (ids, success) =>
    post(`/api/admin/moderation/batch-approve-comments`, { ids }, success);

/**
 * 批量拒绝评论
 * @param ids 评论ID列表
 * @param success 成功回调
 */
export const apiModerationBatchRejectComments = (ids, success) =>
    post(`/api/admin/moderation/batch-reject-comments`, { ids }, success);

/**
 * 获取审核统计信息
 * @param success 成功回调
 */
export const apiModerationStats = (success) =>
    get(`/api/admin/moderation/stats`, success);

/**
 * 重置审核统计
 * @param success 成功回调
 */
export const apiModerationResetStats = (success) =>
    get(`/api/admin/moderation/stats/reset`, success);

/**
 * 重置熔断器
 * @param success 成功回调
 */
export const apiModerationResetCircuitBreaker = (success) =>
    get(`/api/admin/moderation/circuit-breaker/reset`, success);

/**
 * 检查AI服务健康状态
 * @param success 成功回调
 */
export const apiModerationHealth = (success) =>
    get(`/api/admin/moderation/health`, success);
