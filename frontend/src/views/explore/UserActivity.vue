<script setup>
import { ref, computed, onMounted } from 'vue';
import { 
  TrendCharts, 
  ChatDotSquare, 
  Star, 
  View,
  Timer,
  Trophy,
  Medal
} from '@element-plus/icons-vue';
import { userStore } from '@/store';

const store = userStore();

// 用户活动数据（模拟数据，实际应从后端获取）
const userActivity = ref({
  username: store.user?.username || '用户',
  joinDate: '2023-09-01',
  totalTopics: 23,
  totalComments: 156,
  totalLikes: 89,
  followers: 45,
  following: 32,
  level: 5,
  exp: 2350,
  nextLevelExp: 3000,
  rank: 128
});

// 活跃度趋势数据（最近7天）
const activityTrend = ref([
  { date: '周一', topics: 2, comments: 8, likes: 5 },
  { date: '周二', topics: 1, comments: 12, likes: 8 },
  { date: '周三', topics: 3, comments: 15, likes: 12 },
  { date: '周四', topics: 0, comments: 6, likes: 3 },
  { date: '周五', topics: 2, comments: 10, likes: 7 },
  { date: '周六', topics: 4, comments: 18, likes: 15 },
  { date: '周日', topics: 3, comments: 14, likes: 10 }
]);

// 成就徽章
const badges = ref([
  { id: 1, name: '初来乍到', icon: 'User', color: '#909399', earned: true, desc: '完成首次登录' },
  { id: 2, name: '话痨新手', icon: 'ChatDotSquare', color: '#67C23A', earned: true, desc: '发布10条评论' },
  { id: 3, name: '内容创作者', icon: 'Document', color: '#409EFF', earned: true, desc: '发布5个主题' },
  { id: 4, name: '受欢迎的人', icon: 'Star', color: '#E6A23C', earned: true, desc: '获得50个赞' },
  { id: 5, name: '论坛达人', icon: 'Trophy', color: '#F56C6C', earned: false, desc: '发布50个主题' },
  { id: 6, name: '意见领袖', icon: 'Medal', color: '#F56C6C', earned: false, desc: '获得500个赞' },
  { id: 7, name: '活跃分子', icon: 'Fire', color: '#F56C6C', earned: false, desc: '连续7天登录' },
  { id: 8, name: '社区守护者', icon: 'Shield', color: '#F56C6C', earned: false, desc: '举报10条违规内容' }
]);

// 最近动态
const recentActivities = ref([
  { id: 1, type: 'topic', content: '发布了主题《校园食堂美食推荐》', time: '2小时前', icon: 'Document' },
  { id: 2, type: 'comment', content: '评论了《期末考试复习经验分享》', time: '5小时前', icon: 'ChatDotSquare' },
  { id: 3, type: 'like', content: '点赞了《校园风景摄影》', time: '1天前', icon: 'ThumbUp' },
  { id: 4, type: 'topic', content: '发布了主题《图书馆座位预约攻略》', time: '2天前', icon: 'Document' },
  { id: 5, type: 'comment', content: '评论了《社团招新信息汇总》', time: '3天前', icon: 'ChatDotSquare' }
]);

// 等级进度百分比
const levelProgress = computed(() => {
  return Math.round((userActivity.value.exp / userActivity.value.nextLevelExp) * 100);
});

// 获取等级颜色
const getLevelColor = (level) => {
  if (level >= 10) return '#F56C6C';
  if (level >= 5) return '#E6A23C';
  if (level >= 3) return '#409EFF';
  return '#67C23A';
};

// 获取活动类型颜色
const getActivityTypeColor = (type) => {
  const colors = {
    topic: '#409EFF',
    comment: '#67C23A',
    like: '#F56C6C'
  };
  return colors[type] || '#909399';
};

onMounted(() => {
  // 这里可以调用API获取真实数据
});
</script>

<template>
  <div class="user-activity">
    <!-- 页面标题 -->
    <el-card shadow="hover" class="header-card">
      <div class="header-content">
        <el-icon :size="40" color="#409EFF"><TrendCharts /></el-icon>
        <div class="header-text">
          <h2>我的活跃度</h2>
          <p>查看你在论坛的互动数据和成长轨迹</p>
        </div>
      </div>
    </el-card>

    <!-- 用户概览 -->
    <el-row :gutter="20" class="overview-row">
      <el-col :span="8">
        <el-card shadow="hover" class="overview-card">
          <div class="user-level">
            <div class="level-badge" :style="{ backgroundColor: getLevelColor(userActivity.level) }">
              <span class="level-number">LV.{{ userActivity.level }}</span>
            </div>
            <div class="level-info">
              <h3>{{ userActivity.username }}</h3>
              <p>注册时间：{{ userActivity.joinDate }}</p>
            </div>
          </div>
          <div class="level-progress">
            <el-progress 
              :percentage="levelProgress" 
              :color="getLevelColor(userActivity.level)"
              :stroke-width="10"
            />
            <p class="exp-text">{{ userActivity.exp }} / {{ userActivity.nextLevelExp }} 经验值</p>
          </div>
          <div class="rank-info">
            <el-icon :size="20" color="#E6A23C"><Trophy /></el-icon>
            <span>全站排名：第 {{ userActivity.rank }} 名</span>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="16">
        <el-card shadow="hover" class="stats-card">
          <template #header>
            <span class="card-title">互动数据</span>
          </template>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="stat-item">
                <el-icon :size="30" color="#409EFF"><Document /></el-icon>
                <div class="stat-number">{{ userActivity.totalTopics }}</div>
                <div class="stat-label">发布主题</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <el-icon :size="30" color="#67C23A"><ChatDotSquare /></el-icon>
                <div class="stat-number">{{ userActivity.totalComments }}</div>
                <div class="stat-label">发表评论</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <el-icon :size="30" color="#F56C6C"><ThumbUp /></el-icon>
                <div class="stat-number">{{ userActivity.totalLikes }}</div>
                <div class="stat-label">获得点赞</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <!-- 活跃度趋势 -->
    <el-card shadow="hover" class="trend-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">
            <el-icon><TrendCharts /></el-icon>
            近7天活跃度趋势
          </span>
        </div>
      </template>
      <div class="trend-chart">
        <div 
          v-for="(day, index) in activityTrend" 
          :key="index"
          class="trend-day"
        >
          <div class="day-bars">
            <div 
              class="bar topic-bar" 
              :style="{ height: `${day.topics * 10}px` }"
              :title="`发布主题: ${day.topics}`"
            ></div>
            <div 
              class="bar comment-bar" 
              :style="{ height: `${day.comments * 3}px` }"
              :title="`发表评论: ${day.comments}`"
            ></div>
            <div 
              class="bar like-bar" 
              :style="{ height: `${day.likes * 5}px` }"
              :title="`获得点赞: ${day.likes}`"
            ></div>
          </div>
          <div class="day-label">{{ day.date }}</div>
        </div>
      </div>
      <div class="trend-legend">
        <div class="legend-item">
          <div class="legend-color topic-bar"></div>
          <span>发布主题</span>
        </div>
        <div class="legend-item">
          <div class="legend-color comment-bar"></div>
          <span>发表评论</span>
        </div>
        <div class="legend-item">
          <div class="legend-color like-bar"></div>
          <span>获得点赞</span>
        </div>
      </div>
    </el-card>

    <!-- 成就徽章和最近动态 -->
    <el-row :gutter="20" class="bottom-row">
      <el-col :span="12">
        <el-card shadow="hover" class="badges-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><Medal /></el-icon>
                成就徽章
              </span>
              <span class="badge-count">{{ badges.filter(b => b.earned).length }} / {{ badges.length }}</span>
            </div>
          </template>
          <div class="badges-grid">
            <div 
              v-for="badge in badges" 
              :key="badge.id"
              class="badge-item"
              :class="{ 'earned': badge.earned, 'locked': !badge.earned }"
            >
              <div 
                class="badge-icon"
                :style="{ backgroundColor: badge.earned ? badge.color : '#dcdfe6' }"
              >
                <el-icon :size="24" color="#fff"><component :is="badge.icon" /></el-icon>
              </div>
              <div class="badge-name">{{ badge.name }}</div>
              <el-tooltip :content="badge.desc" placement="top">
                <el-icon class="info-icon"><InfoFilled /></el-icon>
              </el-tooltip>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card shadow="hover" class="recent-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><Timer /></el-icon>
                最近动态
              </span>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="activity in recentActivities"
              :key="activity.id"
              :type="activity.type === 'topic' ? 'primary' : activity.type === 'comment' ? 'success' : 'warning'"
              :icon="activity.icon"
            >
              <div class="activity-content">
                <p>{{ activity.content }}</p>
                <span class="activity-time">{{ activity.time }}</span>
              </div>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style lang="less" scoped>
.user-activity {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;

  .header-card {
    margin-bottom: 20px;

    .header-content {
      display: flex;
      align-items: center;
      gap: 20px;

      .header-text {
        h2 {
          margin: 0 0 8px 0;
          color: #303133;
        }

        p {
          margin: 0;
          color: #909399;
          font-size: 14px;
        }
      }
    }
  }

  .overview-row {
    margin-bottom: 20px;

    .overview-card {
      height: 100%;

      .user-level {
        display: flex;
        align-items: center;
        gap: 15px;
        margin-bottom: 20px;

        .level-badge {
          width: 60px;
          height: 60px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;

          .level-number {
            color: #fff;
            font-size: 18px;
            font-weight: bold;
          }
        }

        .level-info {
          h3 {
            margin: 0 0 5px 0;
            color: #303133;
          }

          p {
            margin: 0;
            color: #909399;
            font-size: 13px;
          }
        }
      }

      .level-progress {
        margin-bottom: 15px;

        .exp-text {
          text-align: center;
          margin: 10px 0 0 0;
          font-size: 13px;
          color: #606266;
        }
      }

      .rank-info {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 10px;
        background-color: #fdf6ec;
        border-radius: 8px;
        color: #e6a23c;
        font-size: 14px;
      }
    }

    .stats-card {
      height: 100%;

      .card-title {
        font-weight: bold;
        color: #303133;
      }

      .stat-item {
        text-align: center;
        padding: 20px 0;

        .stat-number {
          font-size: 28px;
          font-weight: bold;
          color: #303133;
          margin: 10px 0;
        }

        .stat-label {
          font-size: 14px;
          color: #909399;
        }
      }
    }
  }

  .trend-card {
    margin-bottom: 20px;

    .card-header {
      .card-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-weight: bold;
        color: #303133;
      }
    }

    .trend-chart {
      display: flex;
      justify-content: space-around;
      align-items: flex-end;
      height: 200px;
      padding: 20px 0;

      .trend-day {
        display: flex;
        flex-direction: column;
        align-items: center;
        flex: 1;

        .day-bars {
          display: flex;
          gap: 3px;
          align-items: flex-end;
          height: 150px;

          .bar {
            width: 20px;
            border-radius: 3px 3px 0 0;
            transition: height 0.3s ease;

            &.topic-bar {
              background-color: #409EFF;
            }

            &.comment-bar {
              background-color: #67C23A;
            }

            &.like-bar {
              background-color: #F56C6C;
            }
          }
        }

        .day-label {
          margin-top: 10px;
          font-size: 12px;
          color: #909399;
        }
      }
    }

    .trend-legend {
      display: flex;
      justify-content: center;
      gap: 30px;
      padding-top: 15px;
      border-top: 1px solid #ebeef5;

      .legend-item {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 13px;
        color: #606266;

        .legend-color {
          width: 16px;
          height: 16px;
          border-radius: 3px;

          &.topic-bar {
            background-color: #409EFF;
          }

          &.comment-bar {
            background-color: #67C23A;
          }

          &.like-bar {
            background-color: #F56C6C;
          }
        }
      }
    }
  }

  .bottom-row {
    .badges-card {
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .card-title {
          display: flex;
          align-items: center;
          gap: 8px;
          font-weight: bold;
          color: #303133;
        }

        .badge-count {
          font-size: 14px;
          color: #909399;
        }
      }

      .badges-grid {
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: 15px;

        .badge-item {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 8px;
          padding: 15px 10px;
          border-radius: 8px;
          transition: all 0.3s ease;

          &.earned {
            background-color: #f0f9ff;
          }

          &.locked {
            background-color: #f5f7fa;
            opacity: 0.6;
          }

          .badge-icon {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
          }

          .badge-name {
            font-size: 12px;
            color: #606266;
            text-align: center;
          }

          .info-icon {
            font-size: 12px;
            color: #c0c4cc;
            cursor: pointer;
          }
        }
      }
    }

    .recent-card {
      .card-header {
        .card-title {
          display: flex;
          align-items: center;
          gap: 8px;
          font-weight: bold;
          color: #303133;
        }
      }

      .activity-content {
        p {
          margin: 0 0 5px 0;
          color: #303133;
          font-size: 14px;
        }

        .activity-time {
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }
}
</style>
