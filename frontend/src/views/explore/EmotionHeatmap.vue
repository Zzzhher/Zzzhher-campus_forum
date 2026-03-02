<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import { apiModerationEmotionHeatmap } from '@/net/api/moderation';
import { ElMessage } from 'element-plus';

const chartRef = ref(null);
let chartInstance = null;
const loading = ref(false);
const emotionData = ref([]);

const initChart = () => {
  if (chartRef.value && emotionData.value.length > 0) {
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
        data: emotionData.value.map(item => item.name)
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
          data: emotionData.value.map(item => item.positive),
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
          data: emotionData.value.map(item => item.neutral),
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
          data: emotionData.value.map(item => item.negative),
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

const fetchData = () => {
  loading.value = true;
  apiModerationEmotionHeatmap((data) => {
    emotionData.value = data;
    loading.value = false;
    initChart();
  }, (error) => {
    loading.value = false;
    ElMessage.error('获取情感热力图数据失败');
    console.error(error);
  });
};

onMounted(() => {
  fetchData();
});
</script>

<template>
  <div class="emotion-heatmap">
    <el-card shadow="hover" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>今日情绪分布热力图</span>
          <el-tag size="small" type="info">实时数据</el-tag>
        </div>
      </template>
      <div ref="chartRef" style="width: 100%; height: 400px;"></div>
    </el-card>
  </div>
</template>

<style scoped>
.emotion-heatmap {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
