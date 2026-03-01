<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';

const chartRef = ref(null);
let chartInstance = null;

// Mock data for emotion distribution (admin version with more details)
const emotionData = [
  { name: '校园活动', positive: 65, neutral: 20, negative: 15, total: 120 },
  { name: '表白墙', positive: 75, neutral: 15, negative: 10, total: 150 },
  { name: '失物招领', positive: 80, neutral: 15, negative: 5, total: 80 },
  { name: '帖子广场', positive: 60, neutral: 25, negative: 15, total: 200 }
];

const initChart = () => {
  if (chartRef.value) {
    chartInstance = echarts.init(chartRef.value);
    
    const option = {
      title: {
        text: '情绪分布热力图',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        },
        formatter: function(params) {
          const dataIndex = params[0].dataIndex;
          const total = emotionData[dataIndex].total;
          let result = `${params[0].name}<br/>`;
          params.forEach(item => {
            result += `${item.marker}${item.seriesName}: ${item.value}% (${Math.round(item.value * total / 100)}条)<br/>`;
          });
          result += `总计: ${total}条`;
          return result;
        }
      },
      legend: {
        data: ['正面', '中性', '负面'],
        bottom: 10
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '15%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: emotionData.map(item => item.name)
      },
      yAxis: {
        type: 'value',
        name: '占比 (%)'
      },
      series: [
        {
          name: '正面',
          type: 'bar',
          stack: 'total',
          emphasis: {
            focus: 'series'
          },
          data: emotionData.map(item => item.positive),
          itemStyle: {
            color: '#52c41a'
          }
        },
        {
          name: '中性',
          type: 'bar',
          stack: 'total',
          emphasis: {
            focus: 'series'
          },
          data: emotionData.map(item => item.neutral),
          itemStyle: {
            color: '#1890ff'
          }
        },
        {
          name: '负面',
          type: 'bar',
          stack: 'total',
          emphasis: {
            focus: 'series'
          },
          data: emotionData.map(item => item.negative),
          itemStyle: {
            color: '#ff4d4f'
          }
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
  <div class="emotion-heatmap-admin">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>实时情绪热力图管理</span>
          <el-select v-model="timeRange" size="small">
            <el-option label="今日" value="today" />
            <el-option label="本周" value="week" />
            <el-option label="本月" value="month" />
          </el-select>
        </div>
      </template>
      <div class="chart-container" ref="chartRef"></div>
      <div class="chart-desc">
        <p>展示各板块的情感分布（正面/中性/负面占比）</p>
        <p>数据来源：后端定时调用 /moderate 统计</p>
      </div>
      <el-divider />
      <div class="admin-actions">
        <el-button type="primary" size="small">导出数据</el-button>
        <el-button type="success" size="small">刷新数据</el-button>
      </div>
    </el-card>
  </div>
</template>

<style lang="less" scoped>
.emotion-heatmap-admin {
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
  
  .admin-actions {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }
}
</style>