<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import 'echarts-wordcloud';

const chartRef = ref(null);
let chartInstance = null;

// Mock data for sensitive words (admin version with more details)
const wordData = [
  { name: '刷单', value: 80, category: '违规' },
  { name: '代课', value: 65, category: '违规' },
  { name: '太难了', value: 55, category: '情绪' },
  { name: '考试', value: 50, category: '学习' },
  { name: '作业', value: 45, category: '学习' },
  { name: '论文', value: 40, category: '学习' },
  { name: '挂科', value: 35, category: '学习' },
  { name: '兼职', value: 30, category: '其他' },
  { name: '实习', value: 25, category: '其他' },
  { name: '毕业', value: 20, category: '其他' },
  { name: '压力', value: 18, category: '情绪' },
  { name: '焦虑', value: 15, category: '情绪' },
  { name: '失眠', value: 12, category: '情绪' },
  { name: '抑郁', value: 10, category: '情绪' },
  { name: '请假', value: 8, category: '其他' }
];

const initChart = () => {
  if (chartRef.value) {
    chartInstance = echarts.init(chartRef.value);
    
    const option = {
      title: {
        text: '高频敏感词云',
        left: 'center'
      },
      tooltip: {
        trigger: 'item',
        formatter: function(params) {
          const data = wordData.find(item => item.name === params.name);
          return `${params.name}: ${params.value}次<br/>分类: ${data.category}`;
        }
      },
      series: [
        {
          type: 'wordCloud',
          shape: 'circle',
          left: 'center',
          top: 'center',
          width: '80%',
          height: '80%',
          right: null,
          bottom: null,
          sizeRange: [12, 60],
          rotationRange: [-45, 45],
          rotationStep: 45,
          gridSize: 8,
          drawOutOfBound: false,
          textStyle: {
            fontFamily: 'sans-serif',
            fontWeight: 'bold',
            color: function(params) {
              const data = wordData.find(item => item.name === params.name);
              if (data.category === '违规') return '#ff4d4f';
              if (data.category === '情绪') return '#fa8c16';
              if (data.category === '学习') return '#1890ff';
              return '#52c41a';
            }
          },
          emphasis: {
            focus: 'self',
            textStyle: {
              shadowBlur: 10,
              shadowColor: '#333'
            }
          },
          data: wordData
        }
      ]
    };
    
    chartInstance.setOption(option);
    
    window.addEventListener('resize', () => {
      chartInstance.resize();
    });
  }
};

onMounted(() => {
  initChart();
});
</script>

<template>
  <div class="word-cloud-admin">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>高频敏感词云管理</span>
          <el-select v-model="timeRange" size="small">
            <el-option label="今日" value="today" />
            <el-option label="本周" value="week" />
            <el-option label="本月" value="month" />
          </el-select>
        </div>
      </template>
      <div class="chart-container" ref="chartRef"></div>
      <div class="chart-desc">
        <p>显示近期评论中出现的高频风险词</p>
        <p>数据来源：从 db_topic_comment 中提取 + 分词统计</p>
      </div>
      <el-divider />
      <div class="word-list">
        <h4>敏感词列表</h4>
        <el-table :data="wordData" style="width: 100%" size="small">
          <el-table-column prop="name" label="敏感词" width="120" />
          <el-table-column prop="value" label="出现次数" width="100" />
          <el-table-column prop="category" label="分类" width="100" />
          <el-table-column label="操作" width="150">
            <template #default="scope">
              <el-button type="primary" size="small" link>编辑</el-button>
              <el-button type="danger" size="small" link>删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-divider />
      <div class="admin-actions">
        <el-button type="primary" size="small">导出数据</el-button>
        <el-button type="success" size="small">刷新数据</el-button>
        <el-button type="warning" size="small">添加敏感词</el-button>
      </div>
    </el-card>
  </div>
</template>

<style lang="less" scoped>
.word-cloud-admin {
  padding: 20px;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .chart-container {
    width: 100%;
    height: 400px;
    margin: 20px 0;
  }
  
  .chart-desc {
    margin-top: 20px;
    font-size: 14px;
    color: #666;
  }
  
  .word-list {
    margin-top: 20px;
  }
  
  .admin-actions {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }
}
</style>