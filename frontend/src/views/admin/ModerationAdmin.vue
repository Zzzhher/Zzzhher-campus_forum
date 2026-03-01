<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { CircleCheck } from '@element-plus/icons-vue';
import ProhibitedAdmin from './section/ProhibitedAdmin.vue';
import {
  apiModerationPendingTopics,
  apiModerationPendingComments,
  apiModerationApproveTopic,
  apiModerationRejectTopic,
  apiModerationApproveComment,
  apiModerationRejectComment,
  apiModerationBatchApproveTopics,
  apiModerationBatchRejectTopics,
  apiModerationBatchApproveComments,
  apiModerationBatchRejectComments,
  apiModerationStats,
  apiModerationResetStats,
  apiModerationResetCircuitBreaker,
  apiModerationHealth
} from '@/net/api/moderation';

const activeTab = ref('topics');
const topicsLoading = ref(false);
const commentsLoading = ref(false);
const topics = ref([]);
const comments = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const totalTopics = ref(0);
const totalComments = ref(0);
const selectedTopics = ref([]);
const selectedComments = ref([]);
const selectAllTopics = ref(false);
const selectAllComments = ref(false);

// 统计信息
const stats = ref({
  total: 0,
  allow: 0,
  block: 0,
  manual: 0,
  fallback: 0,
  allowRate: '0.00%',
  blockRate: '0.00%',
  manualRate: '0.00%',
  circuitBreakerOpen: false,
  consecutiveFailures: 0,
  aiServiceAvailable: false,
  timestamp: ''
});
const statsLoading = ref(false);

// 获取待审核的帖子
const fetchPendingTopics = () => {
  topicsLoading.value = true;
  apiModerationPendingTopics(currentPage.value, pageSize.value, (data) => {
    topics.value = data.list;
    totalTopics.value = data.total;
    topicsLoading.value = false;
    selectedTopics.value = [];
    selectAllTopics.value = false;
  });
};

// 获取待审核的评论
const fetchPendingComments = () => {
  commentsLoading.value = true;
  apiModerationPendingComments(currentPage.value, pageSize.value, (data) => {
    comments.value = data.list;
    totalComments.value = data.total;
    commentsLoading.value = false;
    selectedComments.value = [];
    selectAllComments.value = false;
  });
};

// 批准帖子
const approveTopic = (tid) => {
  ElMessageBox.confirm('确定要批准这个帖子吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    apiModerationApproveTopic(tid, () => {
      ElMessage.success('帖子批准成功！');
      fetchPendingTopics();
    });
  }).catch(() => {
    // 取消操作
  });
};

// 拒绝帖子
const rejectTopic = (tid) => {
  ElMessageBox.confirm('确定要拒绝这个帖子吗？拒绝后帖子将被隐藏。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    apiModerationRejectTopic(tid, () => {
      ElMessage.success('帖子拒绝成功！');
      fetchPendingTopics();
    });
  }).catch(() => {
    // 取消操作
  });
};

// 批准评论
const approveComment = (id) => {
  ElMessageBox.confirm('确定要批准这个评论吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    apiModerationApproveComment(id, () => {
      ElMessage.success('评论批准成功！');
      fetchPendingComments();
    });
  }).catch(() => {
    // 取消操作
  });
};

// 拒绝评论
const rejectComment = (id) => {
  ElMessageBox.confirm('确定要拒绝这个评论吗？拒绝后评论将被删除。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    apiModerationRejectComment(id, () => {
      ElMessage.success('评论拒绝成功！');
      fetchPendingComments();
    });
  }).catch(() => {
    // 取消操作
  });
};

// 切换标签页
const handleTabChange = (tab) => {
  activeTab.value = tab.props.name;
  currentPage.value = 1;
  if (tab.props.name === 'topics') {
    fetchPendingTopics();
  } else {
    fetchPendingComments();
  }
};

// 分页变化
const handlePageChange = (page) => {
  currentPage.value = page;
  if (activeTab.value === 'topics') {
    fetchPendingTopics();
  } else {
    fetchPendingComments();
  }
};

// 处理帖子全选
const handleSelectAllTopics = (val) => {
  if (val) {
    selectedTopics.value = topics.value.map(item => item.id);
  } else {
    selectedTopics.value = [];
  }
};

// 处理评论全选
const handleSelectAllComments = (val) => {
  if (val) {
    selectedComments.value = comments.value.map(item => item.id);
  } else {
    selectedComments.value = [];
  }
};

// 处理帖子选择变化
const handleTopicSelectionChange = (selection) => {
  selectedTopics.value = selection.map(item => item.id);
  selectAllTopics.value = selection.length === topics.value.length;
};

// 处理评论选择变化
const handleCommentSelectionChange = (selection) => {
  selectedComments.value = selection.map(item => item.id);
  selectAllComments.value = selection.length === comments.value.length;
};

// 批量批准帖子
const batchApproveTopics = () => {
  ElMessageBox.confirm(`确定要批准选中的${selectedTopics.value.length}个帖子吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    apiModerationBatchApproveTopics(selectedTopics.value, () => {
      ElMessage.success('批量批准成功！');
      fetchPendingTopics();
    });
  }).catch(() => {
    // 取消操作
  });
};

// 批量拒绝帖子
const batchRejectTopics = () => {
  ElMessageBox.confirm(`确定要拒绝选中的${selectedTopics.value.length}个帖子吗？拒绝后帖子将被隐藏。`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    apiModerationBatchRejectTopics(selectedTopics.value, () => {
      ElMessage.success('批量拒绝成功！');
      fetchPendingTopics();
    });
  }).catch(() => {
    // 取消操作
  });
};

// 批量批准评论
const batchApproveComments = () => {
  ElMessageBox.confirm(`确定要批准选中的${selectedComments.value.length}个评论吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    apiModerationBatchApproveComments(selectedComments.value, () => {
      ElMessage.success('批量批准成功！');
      fetchPendingComments();
    });
  }).catch(() => {
    // 取消操作
  });
};

// 批量拒绝评论
const batchRejectComments = () => {
  ElMessageBox.confirm(`确定要拒绝选中的${selectedComments.value.length}个评论吗？拒绝后评论将被删除。`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    apiModerationBatchRejectComments(selectedComments.value, () => {
      ElMessage.success('批量拒绝成功！');
      fetchPendingComments();
    });
  }).catch(() => {
    // 取消操作
  });
};

// 获取统计信息
const fetchStats = () => {
  statsLoading.value = true;
  apiModerationStats((data) => {
    stats.value = data;
    statsLoading.value = false;
  });
};

// 重置统计
const resetStats = () => {
  ElMessageBox.confirm('确定要重置审核统计吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    apiModerationResetStats(() => {
      ElMessage.success('统计已重置！');
      fetchStats();
    });
  }).catch(() => {});
};

// 重置熔断器
const resetCircuitBreaker = () => {
  apiModerationResetCircuitBreaker(() => {
    ElMessage.success('熔断器已重置！');
    fetchStats();
  });
};

// 初始化
onMounted(() => {
  fetchPendingTopics();
  fetchStats();
});

// 提取纯文本内容
const extractPlainText = (content) => {
  try {
    const obj = JSON.parse(content);
    if (obj.ops) {
      let text = '';
      for (const op of obj.ops) {
        if (op.insert && typeof op.insert === 'string') {
          text += op.insert;
        }
      }
      return text.length > 100 ? text.substring(0, 100) + '...' : text;
    }
    return content;
  } catch (e) {
    return content;
  }
};
</script>

<template>
  <div class="moderation-admin">
    <div class="moderation-admin-header">
      <div>
        <div class="title">
          <el-icon><CircleCheck/></el-icon>
          内容审核管理
        </div>
        <div class="desc">
          在这里管理论坛的所有待审核内容，包括帖子和评论
        </div>
      </div>
    </div>
    
    <!-- 统计面板 -->
    <el-card class="stats-card" v-loading="statsLoading">
      <template #header>
        <div class="stats-header">
          <span>审核统计</span>
          <div class="stats-actions">
            <el-button type="primary" size="small" @click="fetchStats">刷新</el-button>
            <el-button size="small" @click="resetStats">重置统计</el-button>
            <el-button 
              v-if="stats.circuitBreakerOpen" 
              type="warning" 
              size="small" 
              @click="resetCircuitBreaker"
            >
              重置熔断器
            </el-button>
          </div>
        </div>
      </template>
      <div class="stats-content">
        <div class="stats-item">
          <div class="stats-value">{{ stats.total }}</div>
          <div class="stats-label">总审核数</div>
        </div>
        <div class="stats-item success">
          <div class="stats-value">{{ stats.allow }}</div>
          <div class="stats-label">通过 ({{ stats.allowRate }})</div>
        </div>
        <div class="stats-item danger">
          <div class="stats-value">{{ stats.block }}</div>
          <div class="stats-label">拦截 ({{ stats.blockRate }})</div>
        </div>
        <div class="stats-item warning">
          <div class="stats-value">{{ stats.manual }}</div>
          <div class="stats-label">人工审核 ({{ stats.manualRate }})</div>
        </div>
        <div class="stats-item info">
          <div class="stats-value">{{ stats.fallback }}</div>
          <div class="stats-label">降级处理</div>
        </div>
        <div class="stats-item" :class="stats.aiServiceAvailable ? 'success' : 'danger'">
          <div class="stats-value">
            <el-tag :type="stats.aiServiceAvailable ? 'success' : 'danger'" size="small">
              {{ stats.aiServiceAvailable ? '正常' : '异常' }}
            </el-tag>
          </div>
          <div class="stats-label">AI服务状态</div>
        </div>
        <div class="stats-item" :class="stats.circuitBreakerOpen ? 'danger' : 'success'">
          <div class="stats-value">
            <el-tag :type="stats.circuitBreakerOpen ? 'danger' : 'success'" size="small">
              {{ stats.circuitBreakerOpen ? '开启' : '关闭' }}
            </el-tag>
          </div>
          <div class="stats-label">熔断器状态</div>
        </div>
        <div class="stats-item" v-if="stats.consecutiveFailures > 0">
          <div class="stats-value">{{ stats.consecutiveFailures }}</div>
          <div class="stats-label">连续失败次数</div>
        </div>
      </div>
      <div class="stats-time" v-if="stats.timestamp">
        更新时间: {{ stats.timestamp }}
      </div>
    </el-card>

    <el-tabs v-model="activeTab" @tab-click="handleTabChange">
      <el-tab-pane label="待审核帖子" name="topics">
        <div class="batch-actions">
          <el-button type="primary" :disabled="selectedTopics.length === 0" @click="batchApproveTopics">批量批准</el-button>
          <el-button type="danger" :disabled="selectedTopics.length === 0" @click="batchRejectTopics">批量拒绝</el-button>
        </div>
        <el-table 
          v-loading="topicsLoading" 
          :data="topics" 
          height="400"
          @selection-change="handleTopicSelectionChange"
        >
          <el-table-column type="selection" width="55"/>
          <el-table-column prop="id" label="帖子ID" width="80" align="center"/>
          <el-table-column prop="title" label="标题" width="300" show-overflow-tooltip/>
          <el-table-column label="内容" width="400" show-overflow-tooltip>
            <template #default="scope">
              {{ extractPlainText(scope.row.content) }}
            </template>
          </el-table-column>
          <el-table-column prop="uid" label="用户ID" width="100" align="center"/>
          <el-table-column prop="time" label="提交时间" width="180">
            <template #default="scope">
              {{ new Date(scope.row.time).toLocaleString() }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="approveTopic(scope.row.id)">批准</el-button>
              <el-button type="danger" size="small" @click="rejectTopic(scope.row.id)">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="totalTopics"
            @size-change="handlePageChange"
            @current-change="handlePageChange"
          />
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="待审核评论" name="comments">
        <div class="batch-actions">
          <el-button type="primary" :disabled="selectedComments.length === 0" @click="batchApproveComments">批量批准</el-button>
          <el-button type="danger" :disabled="selectedComments.length === 0" @click="batchRejectComments">批量拒绝</el-button>
        </div>
        <el-table 
          v-loading="commentsLoading" 
          :data="comments" 
          height="400"
          @selection-change="handleCommentSelectionChange"
        >
          <el-table-column type="selection" width="55"/>
          <el-table-column prop="id" label="评论ID" width="80" align="center"/>
          <el-table-column prop="tid" label="帖子ID" width="80" align="center"/>
          <el-table-column label="内容" width="400" show-overflow-tooltip>
            <template #default="scope">
              {{ extractPlainText(scope.row.content) }}
            </template>
          </el-table-column>
          <el-table-column prop="uid" label="用户ID" width="100" align="center"/>
          <el-table-column prop="time" label="提交时间" width="180">
            <template #default="scope">
              {{ new Date(scope.row.time).toLocaleString() }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="approveComment(scope.row.id)">批准</el-button>
              <el-button type="danger" size="small" @click="rejectComment(scope.row.id)">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="totalComments"
            @size-change="handlePageChange"
            @current-change="handlePageChange"
          />
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="违禁词管理" name="prohibited">
        <ProhibitedAdmin />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style lang="less" scoped>
.moderation-admin {
  padding: 20px;
  
  .moderation-admin-header {
    margin-bottom: 20px;
  }
  
  .title {
    font-weight: bold;
    margin-bottom: 5px;
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .desc {
    color: #bababa;
    font-size: 13px;
    margin-bottom: 20px;
  }
  
  .batch-actions {
    margin-bottom: 10px;
    display: flex;
    align-items: center;
    gap: 10px;
    justify-content: flex-end;
  }
  
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }

  .stats-card {
    margin-bottom: 20px;

    .stats-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .stats-actions {
        display: flex;
        gap: 10px;
      }
    }

    .stats-content {
      display: flex;
      flex-wrap: wrap;
      gap: 20px;

      .stats-item {
        flex: 1;
        min-width: 120px;
        text-align: center;
        padding: 15px;
        background: #f5f7fa;
        border-radius: 8px;

        .stats-value {
          font-size: 24px;
          font-weight: bold;
          color: #303133;
          margin-bottom: 5px;
        }

        .stats-label {
          font-size: 12px;
          color: #909399;
        }

        &.success {
          background: #f0f9eb;
          .stats-value { color: #67c23a; }
        }

        &.danger {
          background: #fef0f0;
          .stats-value { color: #f56c6c; }
        }

        &.warning {
          background: #fdf6ec;
          .stats-value { color: #e6a23c; }
        }

        &.info {
          background: #f4f4f5;
          .stats-value { color: #909399; }
        }
      }
    }

    .stats-time {
      margin-top: 10px;
      text-align: right;
      font-size: 12px;
      color: #909399;
    }
  }
}
</style>
