<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import 'echarts-wordcloud';
import { apiModerationSensitiveWords } from '@/net/api/moderation';
import { ElMessage } from 'element-plus';

const chartRef = ref(null);
let chartInstance = null;
const loading = ref(false);
const wordData = ref([]);

const initChart = () => {
  if (chartRef.value && wordData.value.length > 0) {
    chartInstance = echarts.init(chartRef.value);

    const option = {
      title: {
        text: '高频敏感词云',
        left: 'center'
      },
      tooltip: {
        trigger: 'item',
        formatter: function(params) {
          return `${params.name}: ${params.value}次`;
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
            color: function() {
              return 'rgb(' + [
                Math.round(Math.random() * 160),
                Math.round(Math.random() * 160),
                Math.round(Math.random() * 255)
              ].join(',') + ')';
            }
          },
          emphasis: {
            focus: 'self',
            textStyle: {
              shadowBlur: 10,
              shadowColor: '#333'
            }
          },
          data: wordData.value
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
  apiModerationSensitiveWords((data) => {
    wordData.value = data;
    loading.value = false;
    initChart();
  }, (error) => {
    loading.value = false;
    ElMessage.error('获取敏感词数据失败');
    console.error(error);
  });
};

onMounted(() => {
  fetchData();
});
</script>

<template>
  <div class="word-cloud">
    <el-card shadow="hover" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>高频敏感词云</span>
          <el-tag size="small" type="warning">近7天数据</el-tag>
        </div>
      </template>
      <div ref="chartRef" style="width: 100%; height: 400px;"></div>
    </el-card>
  </div>
</template>

<style scoped>
.word-cloud {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
