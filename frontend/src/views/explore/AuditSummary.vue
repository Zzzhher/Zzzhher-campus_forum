<script setup>
import { ref, onMounted } from 'vue';

// Mock data for audit summary
const auditData = ref({
  total: 127,
  allowed: 110,
  reviewed: 15,
  blocked: 2,
  date: new Date().toLocaleDateString()
});

// Mock data for recent audit records
const recentRecords = ref([
  {
    id: 1,
    content: '课程太难了',
    status: 'allowed',
    statusText: '允许',
    reason: '合理吐槽',
    time: '10:30'
  },
  {
    id: 2,
    content: '有人要刷单吗？',
    status: 'blocked',
    statusText: '拦截',
    reason: '违规内容',
    time: '10:25'
  },
  {
    id: 3,
    content: '寻找代课，价格面议',
    status: 'blocked',
    statusText: '拦截',
    reason: '违规内容',
    time: '10:20'
  },
  {
    id: 4,
    content: '考试要挂科了，怎么办？',
    status: 'allowed',
    statusText: '允许',
    reason: '合理求助',
    time: '10:15'
  }
]);

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
  <div class="audit-summary">
    <el-card shadow="hover">
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
              <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.statusText }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="reason" label="原因" width="120" />
          <el-table-column prop="time" label="时间" width="80" />
        </el-table>
      </div>
      <div class="audit-desc">
        <p>数据来源：查询 comment.status 字段</p>
      </div>
    </el-card>
  </div>
</template>

<style lang="less" scoped>
.audit-summary {
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
  
  .recent-records {
    margin-top: 20px;
  }
  
  .content-cell {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 300px;
  }
  
  .audit-desc {
    margin-top: 20px;
    font-size: 14px;
    color: #666;
  }
}
</style>