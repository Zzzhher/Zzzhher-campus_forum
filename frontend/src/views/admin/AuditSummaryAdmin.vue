<script setup>
import { ref, onMounted } from 'vue';

// Mock data for audit summary (admin version with more details)
const auditData = ref({
  total: 127,
  allowed: 110,
  reviewed: 15,
  blocked: 2,
  date: new Date().toLocaleDateString()
});

// Mock data for audit records with pagination
const auditRecords = ref([
  {
    id: 1,
    content: '课程太难了',
    status: 'allowed',
    statusText: '允许',
    reason: '合理吐槽',
    time: '10:30',
    user: 'user',
    type: '帖子'
  },
  {
    id: 2,
    content: '有人要刷单吗？',
    status: 'blocked',
    statusText: '拦截',
    reason: '违规内容',
    time: '10:25',
    user: 'user456',
    type: '评论'
  },
  {
    id: 3,
    content: '寻找代课，价格面议',
    status: 'blocked',
    statusText: '拦截',
    reason: '违规内容',
    time: '10:20',
    user: 'user789',
    type: '帖子'
  },
  {
    id: 4,
    content: '考试要挂科了，怎么办？',
    status: 'allowed',
    statusText: '允许',
    reason: '合理求助',
    time: '10:15',
    user: 'user234',
    type: '评论'
  },
  {
    id: 5,
    content: '兼职：日结，有意者联系',
    status: 'reviewed',
    statusText: '人工复核',
    reason: '需要审核',
    time: '10:10',
    user: 'user567',
    type: '帖子'
  }
]);

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(auditRecords.value.length);

const getStatusType = (status) => {
  switch (status) {
    case 'allowed': return 'success';
    case 'blocked': return 'danger';
    case 'reviewed': return 'warning';
    default: return 'info';
  }
};
</script>

<template>
  <div class="audit-summary-admin">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>审核日志摘要管理</span>
          <el-select v-model="timeRange" size="small">
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
        <el-button type="primary" size="small" style="margin-left: 10px">搜索</el-button>
      </div>
      <div class="audit-records">
        <el-table :data="auditRecords" style="width: 100%" size="small">
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
        <el-button type="warning" size="small">清空日志</el-button>
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