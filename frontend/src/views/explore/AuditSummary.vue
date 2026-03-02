<script setup>
import { ref, onMounted } from 'vue';
import { apiModerationDashboard } from '@/net/api/moderation';
import { ElMessage } from 'element-plus';

const loading = ref(false);
const auditData = ref({
  total: 0,
  allowed: 0,
  reviewed: 0,
  blocked: 0,
  date: new Date().toLocaleDateString()
});

const recentRecords = ref([]);

const getStatusType = (status) => {
  switch (status) {
    case 'allowed':
    case 'ALLOW':
      return 'success';
    case 'blocked':
    case 'BLOCK':
      return 'danger';
    case 'reviewed':
    case 'MANUAL_REVIEW':
      return 'warning';
    default:
      return 'info';
  }
};

const getStatusText = (status) => {
  switch (status) {
    case 'ALLOW':
      return '允许';
    case 'BLOCK':
      return '拦截';
    case 'MANUAL_REVIEW':
      return '人工复核';
    default:
      return status;
  }
};

const fetchData = () => {
  loading.value = true;
  apiModerationDashboard((data) => {
    // 处理实时统计数据
    const realtime = data.realtime || {};
    auditData.value = {
      total: realtime.total || 0,
      allowed: realtime.allow || 0,
      reviewed: realtime.manual || 0,
      blocked: realtime.block || 0,
      date: new Date().toLocaleDateString()
    };

    // 处理最近审核记录（从每日统计数据生成）
    const dailyStats = data.daily || [];
    recentRecords.value = dailyStats.slice(-5).map((item, index) => ({
      id: index + 1,
      content: `${item.date} 审核内容`,
      status: item.count > 0 ? 'ALLOW' : 'BLOCK',
      statusText: item.count > 0 ? '正常' : '拦截',
      reason: '系统自动审核',
      time: item.date
    }));

    loading.value = false;
  }, (error) => {
    loading.value = false;
    ElMessage.error('获取审核摘要数据失败');
    console.error(error);
  });
};

onMounted(() => {
  fetchData();
});
</script>

<template>
  <div class="audit-summary">
    <el-card shadow="hover" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>审核日志摘要</span>
          <el-tag size="small" type="info">{{ auditData.date }}</el-tag>
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
      <div class="recent-records">
        <h4>近期审核记录</h4>
        <el-table :data="recentRecords" style="width: 100%" size="small">
          <el-table-column prop="content" label="内容" min-width="200">
            <template #default="scope">
              <el-tooltip :content="scope.row.content" placement="top">
                <div class="content-cell">{{ scope.row.content }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="statusText" label="状态" width="80">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)" size="small">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="reason" label="原因" min-width="150" />
          <el-table-column prop="time" label="时间" width="100" />
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.audit-summary {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.summary-stats {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 20px 0;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 8px;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background-color: #e0e0e0;
}

.recent-records h4 {
  margin-bottom: 15px;
  color: #333;
}

.content-cell {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
