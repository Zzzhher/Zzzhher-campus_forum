<script setup>
import { ref, onMounted } from 'vue';
import { get } from '@/net';

const timeRange = ref('');
const searchQuery = ref('');
const statusFilter = ref('');
const auditData = ref({
  total: 0,
  allowed: 0,
  reviewed: 0,
  blocked: 0,
  date: new Date().toLocaleDateString()
});
const auditRecords = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);

const fetchAuditSummary = () => {
  get('/api/admin/moderation/stats/dashboard',
    (data) => {
      if (data.realtime) {
        auditData.value = {
          total: data.realtime.total || 0,
          allowed: data.realtime.allow || 0,
          reviewed: data.realtime.manual || 0,
          blocked: data.realtime.block || 0,
          date: new Date().toLocaleDateString()
        };
      }
    },
    (message) => {
      console.error('获取审核摘要数据失败:', message);
    }
  );
};

const fetchAuditRecords = () => {
  loading.value = true;
  const params = new URLSearchParams({
    page: currentPage.value,
    size: pageSize.value,
    search: searchQuery.value,
    status: statusFilter.value,
    timeRange: timeRange.value
  });
  get(`/api/admin/moderation/logs?${params.toString()}`,
    (data) => {
      auditRecords.value = data.list || [];
      total.value = data.total || 0;
      loading.value = false;
    },
    (message) => {
      console.error('获取审核记录失败:', message);
      auditRecords.value = [];
      total.value = 0;
      loading.value = false;
    }
  );
};

const getStatusType = (status) => {
  switch (status) {
    case 'allowed': return 'success';
    case 'blocked': return 'danger';
    case 'reviewed': return 'warning';
    default: return 'info';
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchAuditRecords();
};

const handlePageChange = () => {
  fetchAuditRecords();
};

const refreshData = async () => {
  await fetchAuditSummary();
  await fetchAuditRecords();
};

onMounted(() => {
  fetchAuditSummary();
  fetchAuditRecords();
});
</script>

<template>
  <div class="audit-summary-admin">
    <el-card shadow="hover" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>审核日志摘要管理</span>
          <el-select v-model="timeRange" size="small" @change="refreshData">
            <el-option label="全部" value="" />
            <el-option label="今日" value="today" />
            <el-option label="本周" value="week" />
            <el-option label="本月" value="month" />
          </el-select>
        </div>
      </template>
      <div class="summary-stats">
        <div class="stat-item">
          <div class="stat-number">{{ auditData.total }}</div>
          <div class="stat-label">总审核数</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-number">{{ auditData.allowed }}</div>
          <div class="stat-label">允许</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-number">{{ auditData.reviewed }}</div>
          <div class="stat-label">人工复核</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-number">{{ auditData.blocked }}</div>
          <div class="stat-label">拦截</div>
        </div>
      </div>
      <el-divider />
      <div class="search-filters">
        <el-input v-model="searchQuery" placeholder="搜索内容" style="width: 200px" size="small" />
        <el-select v-model="statusFilter" placeholder="状态" style="width: 120px; margin-left: 10px" size="small">
          <el-option label="全部" value="" />
          <el-option label="允许" value="allowed" />
          <el-option label="拦截" value="blocked" />
          <el-option label="人工复核" value="reviewed" />
        </el-select>
        <el-button type="primary" size="small" style="margin-left: 10px" @click="handleSearch">搜索</el-button>
      </div>
      <div class="audit-records">
        <el-table :data="auditRecords" style="width: 100%" size="small" empty-text="暂无数据">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="content" label="内容" min-width="200">
            <template #default="scope">
              <el-tooltip :content="scope.row.content" placement="top">
                <div class="content-cell">{{ scope.row.content }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="type" label="类型" width="80" />
          <el-table-column prop="user" label="用户" width="100" />
          <el-table-column prop="statusText" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.statusText }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="reason" label="原因" width="120" />
          <el-table-column prop="time" label="时间" width="100" />
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
        <el-empty v-else description="暂无审核记录" />
      </div>
      <el-divider />
      <div class="admin-actions">
        <el-button type="primary" size="small">导出数据</el-button>
        <el-button type="success" size="small" @click="refreshData">刷新数据</el-button>
      </div>
    </el-card>
  </div>
</template>

<style lang="less" scoped>
.audit-summary-admin {
  padding: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .summary-stats {
    display: flex;
    justify-content: space-around;
    align-items: center;
    margin: 30px 0;

    .stat-item {
      text-align: center;
      flex: 1;

      .stat-number {
        font-size: 32px;
        font-weight: bold;
        color: #1890ff;
      }

      .stat-label {
        font-size: 14px;
        color: #666;
        margin-top: 5px;
      }
    }

    .stat-divider {
      width: 1px;
      height: 60px;
      background-color: #eaeaea;
    }
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
