<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { CircleCheck } from '@element-plus/icons-vue';
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
  apiModerationBatchRejectComments
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

// 初始化
onMounted(() => {
  fetchPendingTopics();
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
}
</style>
