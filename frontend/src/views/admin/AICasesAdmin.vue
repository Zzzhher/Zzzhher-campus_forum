<script setup>
import { ref, onMounted } from 'vue';
import { get } from '@/net';

// 从后端获取的AI审核案例数据
const aiCases = ref([]);
const loading = ref(false);

// 搜索和筛选
const searchQuery = ref('');
const categoryFilter = ref('');
const decisionFilter = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 获取AI审核案例数据
const fetchAICases = () => {
  loading.value = true;
  const params = new URLSearchParams({
    page: currentPage.value,
    size: pageSize.value,
    search: searchQuery.value,
    category: categoryFilter.value,
    decision: decisionFilter.value
  });
  get(`/api/admin/moderation/cases?${params.toString()}`,
    (data) => {
      aiCases.value = data.list || [];
      total.value = data.total || 0;
      loading.value = false;
    },
    (message) => {
      console.error('获取AI审核案例失败:', message);
      aiCases.value = [];
      total.value = 0;
      loading.value = false;
    }
  );
};

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

const handleSearch = () => {
  currentPage.value = 1;
  fetchAICases();
};

const handlePageChange = () => {
  fetchAICases();
};

onMounted(() => {
  fetchAICases();
});
</script>

<template>
  <div class="ai-cases-admin">
    <el-card shadow="hover" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>AI审核案例库管理</span>
          <el-button type="primary" size="small" @click="fetchAICases">刷新数据</el-button>
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
        <el-button type="primary" size="small" style="margin-left: 10px" @click="handleSearch">搜索</el-button>
      </div>
      <div class="cases-table">
        <el-table :data="aiCases" style="width: 100%" size="small" empty-text="暂无数据">
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
              <el-button type="primary" size="small" link>查看</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination" v-if="total > 0">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @change="handlePageChange"
          />
        </div>
        <el-empty v-else description="暂无审核案例数据" />
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
