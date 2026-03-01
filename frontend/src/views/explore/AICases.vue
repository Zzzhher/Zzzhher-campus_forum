<script setup>
import { ref, onMounted } from 'vue';

// Mock data for AI audit cases
const aiCases = ref([
  {
    id: 1,
    content: '课程太难了',
    decision: 'allowed',
    decisionText: '允许',
    reason: '合理吐槽',
    difficulty: 'low',
    category: '学习'
  },
  {
    id: 2,
    content: '拉人进群刷单',
    decision: 'blocked',
    decisionText: '拦截',
    reason: '高危',
    difficulty: 'high',
    category: '违规'
  },
  {
    id: 3,
    content: '有人要代课吗？',
    decision: 'blocked',
    decisionText: '拦截',
    reason: '违规',
    difficulty: 'medium',
    category: '违规'
  },
  {
    id: 4,
    content: '考试要挂科了，怎么办？',
    decision: 'allowed',
    decisionText: '允许',
    reason: '合理求助',
    difficulty: 'low',
    category: '学习'
  },
  {
    id: 5,
    content: '兼职：日结，有意者联系',
    decision: 'reviewed',
    decisionText: '人工复核',
    reason: '需要审核',
    difficulty: 'medium',
    category: '其他'
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
  <div class="ai-cases">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>AI审核案例库</span>
          <el-tag size="small" type="info">典型案例</el-tag>
        </div>
      </template>
      <div class="cases-intro">
        <p>展示AI审核系统的典型案例，帮助了解审核规则和标准。</p>
      </div>
      <div class="cases-list">
        <el-card
          v-for="caseItem in aiCases"
          :key="caseItem.id"
          shadow="hover"
          class="case-item"
        >
          <template #header>
            <div class="case-header">
              <span class="case-category">{{ caseItem.category }}</span>
              <el-tag :type="getDecisionType(caseItem.decision)">{{ caseItem.decisionText }}</el-tag>
            </div>
          </template>
          <div class="case-content">
            <p class="case-text">{{ caseItem.content }}</p>
            <div class="case-reason">
              <span class="reason-label">原因：</span>
              <span class="reason-text">{{ caseItem.reason }}</span>
            </div>
            <div class="case-meta">
              <el-tag :type="getDifficultyType(caseItem.difficulty)" size="small">
                难度：{{ getDifficultyText(caseItem.difficulty) }}
              </el-tag>
            </div>
          </div>
        </el-card>
      </div>
      <div class="cases-desc">
        <p>数据来源：静态 JSON 或 mock 数据</p>
      </div>
    </el-card>
  </div>
</template>

<style lang="less" scoped>
.ai-cases {
  padding: 20px;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .cases-intro {
    margin-bottom: 20px;
    font-size: 14px;
    color: #666;
  }
  
  .cases-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
  }
  
  .case-item {
    transition: transform 0.3s ease;
    
    &:hover {
      transform: translateY(-5px);
    }
    
    .case-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .case-category {
      font-weight: bold;
      color: #1890ff;
    }
    
    .case-content {
      .case-text {
        font-size: 16px;
        margin: 10px 0;
        padding: 10px;
        background-color: #f7f8fa;
        border-radius: 4px;
      }
      
      .case-reason {
        margin: 10px 0;
        
        .reason-label {
          font-weight: bold;
          color: #666;
        }
        
        .reason-text {
          color: #333;
        }
      }
      
      .case-meta {
        margin-top: 10px;
      }
    }
  }
  
  .cases-desc {
    margin-top: 20px;
    font-size: 14px;
    color: #666;
  }
}
</style>