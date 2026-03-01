<script setup>
import { ref, onMounted } from 'vue';

// Mock data for AI audit cases (admin version with more details)
const aiCases = ref([
  {
    id: 1,
    content: '课程太难了',
    decision: 'allowed',
    decisionText: '允许',
    reason: '合理吐槽',
    difficulty: 'low',
    category: '学习',
    createdAt: '2023-10-01'
  },
  {
    id: 2,
    content: '拉人进群刷单',
    decision: 'blocked',
    decisionText: '拦截',
    reason: '高危',
    difficulty: 'high',
    category: '违规',
    createdAt: '2023-10-02'
  },
  {
    id: 3,
    content: '有人要代课吗？',
    decision: 'blocked',
    decisionText: '拦截',
    reason: '违规',
    difficulty: 'medium',
    category: '违规',
    createdAt: '2023-10-03'
  },
  {
    id: 4,
    content: '考试要挂科了，怎么办？',
    decision: 'allowed',
    decisionText: '允许',
    reason: '合理求助',
    difficulty: 'low',
    category: '学习',
    createdAt: '2023-10-04'
  },
  {
    id: 5,
    content: '兼职：日结，有意者联系',
    decision: 'reviewed',
    decisionText: '人工复核',
    reason: '需要审核',
    difficulty: 'medium',
    category: '其他',
    createdAt: '2023-10-05'
  }
]);

const getDecisionType = (decision) => {
  switch (decision) {
    case 'allowed': return 'success';
    case 'blocked': return 'danger';
    case 'reviewed': return 'warning';
    default: return 'info';
  }
};

const getDifficultyType = (difficulty) => {
  switch (difficulty) {
    case 'high': return 'danger';
    case 'medium': return 'warning';
    case 'low': return 'success';
    default: return 'info';
  }
};

const getDifficultyText = (difficulty) => {
  switch (difficulty) {
    case 'high': return '高';
    case 'medium': return '中';
    case 'low': return '低';
    default: return '未知';
  }
};
</script>

<template>
  <div class="ai-cases-admin">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>AI审核案例库管理</span>
          <el-button type="primary" size="small">添加案例</el-button>
        </div>
      </template>
      <div class="search-filters">
        <el-input v-model="searchQuery" placeholder="搜索内容" style="width: 200px" size="small" />
        <el-select v-model="categoryFilter" placeholder="分类" style="width: 120px; margin-left: 10px" size="small">
          <el-option label="全部" value="" />
          <el-option label="学习" value="学习" />
          <el-option label="违规" value="违规" />
          <el-option label="其他" value="其他" />
        </el-select>
        <el-select v-model="decisionFilter" placeholder="审核结果" style="width: 120px; margin-left: 10px" size="small">
          <el-option label="全部" value="" />
          <el-option label="允许" value="allowed" />
          <el-option label="拦截" value="blocked" />
          <el-option label="人工复核" value="reviewed" />
        </el-select>
        <el-button type="primary" size="small" style="margin-left: 10px">搜索</el-button>
      </div>
      <div class="cases-table">
        <el-table :data="aiCases" style="width: 100%" size="small">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="content" label="内容" min-width="200">
            <template #default="scope">
              <el-tooltip :content="scope.row.content" placement="top">
                <div class="content-cell">{{ scope.row.content }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="category" label="分类" width="100" />
          <el-table-column prop="decisionText" label="审核结果" width="100">
            <template #default="scope">
              <el-tag :type="getDecisionType(scope.row.decision)">{{ scope.row.decisionText }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="reason" label="原因" width="120" />
          <el-table-column label="难度" width="80">
            <template #default="scope">
              <el-tag :type="getDifficultyType(scope.row.difficulty)" size="small">
                {{ getDifficultyText(scope.row.difficulty) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="120" />
          <el-table-column label="操作" width="150">
            <template #default="scope">
              <el-button type="primary" size="small" link>编辑</el-button>
              <el-button type="danger" size="small" link>删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
          />
        </div>
      </div>
      <el-divider />
      <div class="admin-actions">
        <el-button type="primary" size="small">导出数据</el-button>
        <el-button type="success" size="small">刷新数据</el-button>
        <el-button type="warning" size="small">批量操作</el-button>
      </div>
    </el-card>
  </div>
</template>

<style lang="less" scoped>
.ai-cases-admin {
  padding: 20px;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .search-filters {
    margin: 20px 0;
  }
  
  .content-cell {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 300px;
  }
  
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
  
  .admin-actions {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }
}
</style>