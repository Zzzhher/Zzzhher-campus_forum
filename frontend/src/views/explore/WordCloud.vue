<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import 'echarts-wordcloud';

const chartRef = ref(null);
let chartInstance = null;

// Mock data for sensitive words
const wordData = [
  { name: '刷单', value: 80 },
  { name: '代课', value: 65 },
  { name: '太难了', value: 55 },
  { name: '考试', value: 50 },
  { name: '作业', value: 45 },
  { name: '论文', value: 40 },
  { name: '挂科', value: 35 },
  { name: '兼职', value: 30 },
  { name: '实习', value: 25 },
  { name: '毕业', value: 20 },
  { name: '压力', value: 18 },
  { name: '焦虑', value: 15 },
  { name: '失眠', value: 12 },
  { name: '抑郁', value: 10 },
  { name: '请假', value: 8 }
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
  <div class="word-cloud">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>高频敏感词云</span>
          <el-tag size="small" type="warning">近期数据</el-tag>
        </div>
      </template>
      <div class="chart-container" ref="chartRef"></div>
      <div class="chart-desc">
        <p>显示近期评论中出现的高频风险词（如"刷单""代课""太难了"）</p>
        <p>数据来源：从 db_topic_comment 中提取 + 分词统计</p>
      </div>
    </el-card>
  </div>
</template>

<style lang="less" scoped>
.word-cloud {
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