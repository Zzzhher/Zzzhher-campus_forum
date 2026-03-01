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
import TopicTag from '@/components/TopicTag.vue';
import { 
  EditPen, Edit, Document, Compass, Picture, Microphone,
  Clock, CircleCheck, Star, ChatDotRound, Calendar, CollectionTag,
  Link, ArrowRightBold, FolderOpened, TrendCharts
} from '@element-plus/icons-vue';

const router = useRouter();
const store = userStore();
const topics = ref([]);
const loading = ref(false);
const confessionTypeId = ref(0);
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

const sortedTopics = computed(() => {
  // 按类型ID过滤，确保只显示表白墙类型的帖子
  return topics.value.filter(t => t.type === confessionTypeId.value);
});

const loadConfessions = () => {
  loading.value = true;
  
  // 先获取帖子类型
  apiForumTypes((types) => {
    if (types) {
      topicTypes.value = types;
      const confessionType = types.find(type => type.name === '表白墙');
      if (confessionType) {
        confessionTypeId.value = confessionType.id;
        
        // 获取到类型ID后再请求帖子列表
        apiForumTopicList(0, confessionTypeId.value, (data) => {
          topics.value = data || [];
          loading.value = false;
        });
      } else {
        // 未找到表白墙类型
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
    topTopics.value = (data || []).filter(topic => topic.type === confessionTypeId.value);
  });
};

const goToTopicDetail = (id) => {
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

const isAnonymous = (title, text) => {
  const content = (title + text).toLowerCase();
  return content.includes('匿名') || content.includes('偷偷') || content.includes('不敢');
};

const onTopicCreate = () => {
  editor.value = false;
  loadConfessions();
};

onMounted(() => {
  loadConfessions();
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
  <div class="confession-view">
    <div class="main-container">
      <!-- 左侧内容区 -->
      <div class="content-area">
        <!-- 快速发帖入口 -->
        <light-card class="confession-input-card">
          <div class="create-topic confession-theme" @click="editor = true">
            <el-icon><EditPen /></el-icon>
            <span>点击发表主题...</span>
          </div>
          <div class="quick-actions">
            <el-icon><Edit /></el-icon>
            <el-icon><Document /></el-icon>
            <el-icon><Compass /></el-icon>
            <el-icon><Picture /></el-icon>
            <el-icon><Microphone /></el-icon>
          </div>
        </light-card>

        <!-- 置顶表白 -->
        <light-card v-if="topTopics.length > 0" class="top-section">
          <div v-for="item in topTopics" :key="item.id" class="top-topic" @click="goToTopicDetail(item.id)">
            <el-tag type="danger" size="small" effect="dark">
              <el-icon><TrendCharts /></el-icon>置顶
            </el-tag>
            <div class="top-title">{{ item.title }}</div>
            <div class="top-time">{{ new Date(item.time).toLocaleString() }}</div>
          </div>
        </light-card>

        <!-- 表白列表 -->
        <div v-if="loading" class="loading">
          <el-skeleton :rows="5" animated />
        </div>
        
        <div v-else-if="sortedTopics.length === 0" class="empty">
          <el-empty description="暂无帖子" />
        </div>
        
        <div v-else class="confession-list">
          <light-card
            v-for="topic in sortedTopics"
            :key="topic.id"
            class="confession-card"
            @click="goToTopicDetail(topic.id)"
          >
            <div class="card-header">
              <div class="user-info">
                <div class="avatar-wrapper" :class="{ anonymous: isAnonymous(topic.title, topic.text) }">
                  <el-avatar 
                    :size="45" 
                    :src="isAnonymous(topic.title, topic.text) ? '' : store.avatarUserUrl(topic.avatar)"
                  >
                    <el-icon v-if="isAnonymous(topic.title, topic.text)"><Hide /></el-icon>
                    <el-icon v-else><User /></el-icon>
                  </el-avatar>
                </div>
                <div class="user-meta">
                  <div class="username">
                    {{ isAnonymous(topic.title, topic.text) ? '匿名用户' : topic.username }}
                    <el-tag v-if="isAnonymous(topic.title, topic.text)" size="small" type="info" effect="plain" class="anonymous-tag">
                      <el-icon><Hide /></el-icon>匿名
                    </el-tag>
                  </div>
                  <div class="publish-time">
                    <el-icon><Clock /></el-icon>
                    {{ formatDate(topic.time) }}
                  </div>
                </div>
              </div>
              <div class="heart-icon">
                <el-icon :size="24" color="#F56C6C"><Heart /></el-icon>
              </div>
            </div>
            
            <div class="card-body">
              <div class="confession-title">
                <topic-tag :type="topic.type" />
                <span class="title-text">{{ topic.title }}</span>
              </div>
              
              <div class="confession-content">{{ topic.text }}</div>
              
              <div v-if="topic.images && topic.images.length > 0" class="image-grid">
                <el-image
                  v-for="(img, index) in topic.images.slice(0, 3)"
                  :key="index"
                  :src="img"
                  fit="cover"
                  class="confession-image"
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

          <!-- 表白墙公告 -->
          <light-card class="notice-card">
            <div class="section-title confession-title-section">
              <el-icon color="#F56C6C"><Heart /></el-icon>表白墙公告
            </div>
            <el-divider style="margin: 10px 0" />
            <div class="notice-content">
              <p>💕 在这里，你可以勇敢表达心意</p>
              <p>🎀 支持匿名表白，保护你的隐私</p>
              <p>✨ 真诚待人，文明发言</p>
              <p>🌟 愿每一份心意都被温柔以待</p>
            </div>
          </light-card>

          <!-- 今日统计 -->
          <light-card class="stats-card">
            <div class="section-title">
              <el-icon><TrendCharts /></el-icon>今日统计
            </div>
            <el-divider style="margin: 10px 0" />
            <div class="stats-content">
              <div class="stat-row">
                <span>今日帖子</span>
                <span class="stat-value">{{ topics.filter(t => t.type === confessionTypeId && new Date(t.time).toDateString() === new Date().toDateString()).length }}</span>
              </div>
              <div class="stat-row">
                <span>累计点赞</span>
                <span class="stat-value">{{ topics.filter(t => t.type === confessionTypeId).reduce((sum, t) => sum + t.like, 0) }}</span>
              </div>
              <div class="stat-row">
                <span>累计评论</span>
                <span class="stat-value">{{ topics.filter(t => t.type === confessionTypeId).reduce((sum, t) => sum + t.comment, 0) }}</span>
              </div>
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
.confession-view {
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

.confession-input-card {
  background: linear-gradient(135deg, #fff5f5 0%, #ffffff 100%);
  border: 1px solid #ffe4e4;
}

.create-topic {
  background: linear-gradient(135deg, #ffeded 0%, #fff0f0 100%);
  border-radius: 25px;
  height: 45px;
  color: #666;
  font-size: 14px;
  line-height: 45px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  transition: all 0.3s;

  &.confession-theme {
    border: 1px solid #ffd4d4;

    &:hover {
      background: linear-gradient(135deg, #ffe0e0 0%, #fff5f5 100%);
      box-shadow: 0 2px 8px rgba(245, 108, 108, 0.15);
    }
  }

  &:hover {
    cursor: pointer;
  }

  .el-icon {
    color: #F56C6C;
    font-size: 18px;
  }
}

.quick-actions {
  margin-top: 12px;
  display: flex;
  gap: 15px;
  font-size: 18px;
  color: #d4a5a5;
  padding-left: 10px;
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
      color: #F56C6C;
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

.confession-list {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.confession-card {
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 18px;
  border: 1px solid transparent;

  &:hover {
    transform: scale(1.01);
    box-shadow: 0 4px 15px rgba(245, 108, 108, 0.12);
    border-color: #ffe4e4;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar-wrapper {
  position: relative;

  &.anonymous {
    .el-avatar {
      background: linear-gradient(135deg, #d3d3d3 0%, #a9a9a9 100%);
    }
  }
}

.user-meta {
  .username {
    font-size: 15px;
    font-weight: bold;
    color: #333;
    display: flex;
    align-items: center;
    gap: 8px;

    .anonymous-tag {
      font-size: 11px;
    }
  }

  .publish-time {
    font-size: 12px;
    color: #999;
    display: flex;
    align-items: center;
    gap: 4px;
    margin-top: 4px;
  }
}

.heart-icon {
  animation: heartbeat 2s ease-in-out infinite;
}

@keyframes heartbeat {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.card-body {
  .confession-title {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 10px;

    .title-text {
      font-size: 16px;
      font-weight: bold;
      color: #333;
    }
  }

  .confession-content {
    font-size: 14px;
    color: #555;
    line-height: 1.7;
    margin-bottom: 15px;
    display: -webkit-box;
    -webkit-line-clamp: 4;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .image-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 10px;
    margin-bottom: 15px;

    .confession-image {
      width: 100%;
      height: 130px;
      border-radius: 10px;
      object-fit: cover;
    }
  }
}

.card-footer {
  .interaction-stats {
    display: flex;
    gap: 25px;
    font-size: 13px;
    color: #666;

    span {
      display: flex;
      align-items: center;
      gap: 5px;
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

  &.confession-title-section {
    color: #F56C6C;
  }
}

.notice-card {
  background: linear-gradient(135deg, #fffafa 0%, #ffffff 100%);
  border: 1px solid #ffe8e8;
}

.notice-content {
  font-size: 13px;
  color: #666;
  line-height: 2;

  p {
    margin: 0;
  }
}

.stats-card {
  .stats-content {
    .stat-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 10px 0;
      border-bottom: 1px dashed #eee;
      font-size: 14px;
      color: #666;

      &:last-child {
        border-bottom: none;
      }

      .stat-value {
        font-weight: bold;
        color: #F56C6C;
        font-size: 16px;
      }
    }
  }
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
