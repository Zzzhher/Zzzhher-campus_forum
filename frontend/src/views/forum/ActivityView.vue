<script setup>
import { ref, onMounted, computed, reactive } from 'vue';
import { ElButton, ElDivider, ElTag, ElEmpty, ElSkeleton, ElAvatar, ElIcon, ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { apiForumTopicList, apiForumTypes, apiForumTopTopics, apiForumWeather } from '@/net/api/forum';
import LightCard from '@/components/LightCard.vue';
import Weather from '@/components/Weather.vue';
import TopicEditor from '@/components/TopicEditor.vue';
import TopicCollectList from '@/components/TopicCollectList.vue';
import { userStore } from '@/store';
import ColorDot from '@/components/ColorDot.vue';
import TopicTag from '@/components/TopicTag.vue';
import { 
  EditPen, Edit, Document, Compass, Picture, Microphone, 
  Clock, CircleCheck, Star, ChatDotRound, Calendar, CollectionTag,
  Link, ArrowRightBold, FolderOpened, Location, Timer
} from '@element-plus/icons-vue';

const router = useRouter();
const store = userStore();
const topics = ref([]);
const loading = ref(false);
const activityTypeId = ref(0);
const topicTypes = ref([]);
const editor = ref(false);
const topTopics = ref([]);
const collects = ref(false);

const weather = reactive({
  location: {},
  now: [],
  hourly: [],
  success: false,
});

const today = computed(() => {
  const date = new Date();
  return `${date.getFullYear()} 年 ${date.getMonth() + 1} 月 ${date.getDate()} 日`;
});

const loadActivities = () => {
  loading.value = true;
  
  // 先获取帖子类型
  apiForumTypes((types) => {
    if (types) {
      topicTypes.value = types;
      const activityType = types.find(type => type.name === '校园活动');
      if (activityType) {
        activityTypeId.value = activityType.id;
        
        // 获取到类型ID后再请求帖子列表
        apiForumTopicList(0, activityTypeId.value, (data) => {
          const filteredTopics = (data || []).filter(topic => topic.type === activityTypeId.value);
          topics.value = filteredTopics;
          loading.value = false;
        });
      } else {
        // 未找到校园活动类型
        topics.value = [];
        loading.value = false;
      }
    } else {
      loading.value = false;
    }
  });
};

const loadTopTopics = () => {
  apiForumTopTopics((data) => {
    topTopics.value = (data || []).filter(topic => topic.type === activityTypeId.value);
  });
};

const goToActivityDetail = (id) => {
  router.push(`/index/topic-detail/${id}`);
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};



const onTopicCreate = () => {
  editor.value = false;
  loadActivities();
};

onMounted(() => {
  loadActivities();
  loadTopTopics();
  
  navigator.geolocation.getCurrentPosition(
    (position) => {
      const longitude = position.coords.longitude;
      const latitude = position.coords.latitude;
      apiForumWeather(longitude, latitude, (data) => {
        Object.assign(weather, data);
        weather.success = true;
      });
    },
    (error) => {
      console.info(error);
      ElMessage.warning('位置信息获取超时，请检测网络设置');
      apiForumWeather(116.40529, 39.90499, (data) => {
        Object.assign(weather, data);
        weather.success = true;
      });
    },
    {
      timeout: 10000,
      enableHighAccuracy: true,
    }
  );
});
</script>

<template>
  <div class="activity-view">
    <div class="main-container">
      <!-- 左侧内容区 -->
      <div class="content-area">
        <!-- 快速发帖入口 -->
        <light-card>
          <div class="create-topic" @click="editor = true">
            <el-icon><EditPen /></el-icon>点击发布校园活动...
          </div>
          <div class="quick-actions">
            <el-icon><Edit /></el-icon>
            <el-icon><Document /></el-icon>
            <el-icon><Compass /></el-icon>
            <el-icon><Picture /></el-icon>
            <el-icon><Microphone /></el-icon>
          </div>
        </light-card>

        <!-- 置顶活动 -->
        <light-card v-if="topTopics.length > 0" class="top-section">
          <div v-for="item in topTopics" :key="item.id" class="top-topic" @click="goToActivityDetail(item.id)">
            <el-tag type="danger" size="small">置顶</el-tag>
            <div class="top-title">{{ item.title }}</div>
            <div class="top-time">{{ new Date(item.time).toLocaleString() }}</div>
          </div>
        </light-card>

        <!-- 活动列表 -->
        <div v-if="loading" class="loading">
          <el-skeleton :rows="5" animated />
        </div>
        
        <div v-else-if="topics.length === 0" class="empty">
          <el-empty description="暂无活动" />
        </div>
        
        <div v-else class="activity-list">
          <light-card
            v-for="topic in topics"
            :key="topic.id"
            class="activity-card"
            @click="goToActivityDetail(topic.id)"
          >
            <div class="card-header">
              <div class="user-info">
                <el-avatar :size="40" :src="store.avatarUserUrl(topic.avatar)" />
                <div class="user-meta">
                  <div class="username">{{ topic.username }}</div>
                  <div class="publish-time">
                    <el-icon><Clock /></el-icon>
                    {{ formatDate(topic.time) }}
                  </div>
                </div>
              </div>
            </div>
            
            <div class="card-body">
              <div class="activity-title">
                <topic-tag :type="topic.type" />
                <span class="title-text">{{ topic.title }}</span>
              </div>
              
              <div class="activity-content">{{ topic.text }}</div>
              
              <div v-if="topic.images && topic.images.length > 0" class="image-grid">
                <el-image
                  v-for="(img, index) in topic.images.slice(0, 3)"
                  :key="index"
                  :src="img"
                  fit="cover"
                  class="activity-image"
                />
              </div>
            </div>
            
            <div class="card-footer">
              <div class="interaction-stats">
                <span><el-icon><CircleCheck /></el-icon> {{ topic.like }} 点赞</span>
                <span><el-icon><Star /></el-icon> {{ topic.collect }} 收藏</span>
                <span><el-icon><ChatDotRound /></el-icon> {{ topic.comment }} 评论</span>
              </div>
            </div>
          </light-card>
        </div>
      </div>

      <!-- 右侧边栏 -->
      <div class="sidebar">
        <div class="sticky-container">
          <!-- 我的收藏 -->
          <light-card>
            <div class="collect-button" @click="collects = true">
              <span><el-icon><FolderOpened /></el-icon>查看我的收藏</span>
              <el-icon><ArrowRightBold /></el-icon>
            </div>
          </light-card>

          <!-- 活动公告 -->
          <light-card>
            <div class="section-title">
              <el-icon><CollectionTag /></el-icon>活动公告
            </div>
            <el-divider style="margin: 10px 0" />
            <div class="notice-content">
              欢迎参与校园活动！这里有丰富多彩的社团活动、学术讲座、文体比赛等。快来发现并参与你感兴趣的活动吧！
            </div>
          </light-card>

          <!-- 天气信息 -->
          <light-card>
            <div class="section-title">
              <el-icon><Calendar /></el-icon>天气信息
            </div>
            <el-divider style="margin: 10px 0" />
            <weather :data="weather" />
          </light-card>

          <!-- 今日信息 -->
          <light-card>
            <div class="info-row">
              <span>当前日期</span>
              <span>{{ today }}</span>
            </div>
            <div class="info-row">
              <span>当前IP地址</span>
              <span>127.0.0.1</span>
            </div>
          </light-card>

          <!-- 友情链接 -->
          <light-card>
            <div class="section-title" style="color: grey">
              <el-icon><Link /></el-icon>友情链接
            </div>
            <el-divider style="margin: 10px 0" />
            <div class="friend-links">
              <a href="https://javascript.info/" target="_blank" rel="noopener noreferrer">
                <el-image src="https://element-plus.org/images/js-design-banner.jpg" />
              </a>
              <a href="https://www.vform666.com/" target="_blank" rel="noopener noreferrer">
                <el-image src="https://element-plus.org/images/vform-banner.png" />
              </a>
            </div>
          </light-card>
        </div>
      </div>
    </div>

    <topic-editor
      :show="editor"
      @close="editor = false"
      @success="onTopicCreate"
    />
    <topic-collect-list :show="collects" @close="collects = false" />
  </div>
</template>

<style scoped lang="less">
.activity-view {
  padding: 20px;
}

.main-container {
  display: flex;
  margin: 0 auto;
  gap: 20px;
  max-width: 1000px;
}

.content-area {
  flex: 1;
}

.sidebar {
  width: 280px;
}

.sticky-container {
  position: sticky;
  top: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.create-topic {
  background-color: #efefef;
  border-radius: 5px;
  height: 40px;
  color: grey;
  font-size: 14px;
  line-height: 40px;
  padding: 0 10px;
  display: flex;
  align-items: center;
  gap: 8px;

  &:hover {
    cursor: pointer;
  }
}

.quick-actions {
  margin-top: 10px;
  display: flex;
  gap: 13px;
  font-size: 18px;
  color: grey;
}

.top-section {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.top-topic {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;

  .top-title {
    flex: 1;
    font-size: 14px;
    font-weight: bold;
    opacity: 0.8;
    transition: color 0.3s;

    &:hover {
      color: grey;
    }
  }

  .top-time {
    color: grey;
    font-size: 13px;
  }
}

.loading {
  margin: 20px 0;
}

.empty {
  margin: 40px 0;
}

.activity-list {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.activity-card {
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 15px;

  &:hover {
    transform: scale(1.01);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-meta {
  .username {
    font-size: 14px;
    font-weight: bold;
    color: #333;
  }

  .publish-time {
    font-size: 12px;
    color: grey;
    display: flex;
    align-items: center;
    gap: 4px;
    margin-top: 2px;
  }
}

.card-body {
  .activity-title {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;

    .title-text {
      font-size: 16px;
      font-weight: bold;
      color: #333;
    }
  }

  .activity-content {
    font-size: 14px;
    color: #666;
    line-height: 1.6;
    margin-bottom: 12px;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .image-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 10px;
    margin-bottom: 12px;

    .activity-image {
      width: 100%;
      height: 120px;
      border-radius: 8px;
      object-fit: cover;
    }
  }
}

.card-footer {
  .interaction-stats {
    display: flex;
    gap: 20px;
    font-size: 13px;
    color: #666;

    span {
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }
}

.collect-button {
  font-size: 14px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: 0.3s;
  cursor: pointer;

  &:hover {
    opacity: 0.6;
  }

  span {
    display: flex;
    align-items: center;
    gap: 6px;
  }
}

.section-title {
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 6px;
}

.notice-content {
  font-size: 14px;
  color: grey;
  line-height: 1.6;
}

.info-row {
  display: flex;
  justify-content: space-between;
  color: grey;
  font-size: 14px;
  margin-bottom: 8px;

  &:last-child {
    margin-bottom: 0;
  }
}

.friend-links {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;

  a {
    border-radius: 5px;
    overflow: hidden;

    .el-image {
      width: 100%;
      height: 60px;
    }
  }
}
</style>
