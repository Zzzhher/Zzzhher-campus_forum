<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';

const chartRef = ref(null);
let chartInstance = null;

// Mock data for emotion distribution
const emotionData = [
  { name: '校园活动', positive: 65, neutral: 20, negative: 15 },
  { name: '表白墙', positive: 75, neutral: 15, negative: 10 },
  { name: '失物招领', positive: 80, neutral: 15, negative: 5 },
  { name: '帖子广场', positive: 60, neutral: 25, negative: 15 }
];

const initChart = () => {
  if (chartRef.value) {
    chartInstance = echarts.init(chartRef.value);
    
    const option = {
      title: {
        text: '今日情绪分布热力图',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
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
  <div class="emotion-heatmap">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>实时情绪热力图</span>
          <el-tag size="small" type="info">今日数据</el-tag>
        </div>
      </template>
      <div class="chart-container" ref="chartRef"></div>
      <div class="chart-desc">
        <p>展示今日各板块（校园活动、表白墙等）的情感分布（正面/中性/负面占比）</p>
      </div>
    </el-card>
  </div>
</template>

<style lang="less" scoped>
.emotion-heatmap {
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
}
</style>