<script setup>
import { ref, onMounted } from 'vue';
import { ElCard, ElButton, ElDivider, ElEmpty } from 'element-plus';
import { useRouter } from 'vue-router';
import { apiForumTopicList, apiForumTypes } from '@/net/api/forum';

const router = useRouter();
const topics = ref([]);
const loading = ref(false);
const lostFoundTypeId = ref(0);
const topicTypes = ref([]);

const loadLostFoundItems = () => {
  loading.value = true;
  // 首先获取所有帖子类型，找到失物招领对应的类型ID
  apiForumTypes((types) => {
    if (types) {
      topicTypes.value = types;
      const lostFoundType = types.find(type => type.name === '失物招领');
      if (lostFoundType) {
        lostFoundTypeId.value = lostFoundType.id;
      }
    }
    
    // 然后根据失物招领类型ID获取帖子列表
    apiForumTopicList(0, lostFoundTypeId.value, (data) => {
      topics.value = data || [];
      loading.value = false;
    });
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

const getTopicTypeName = (typeId) => {
  const type = topicTypes.value.find(t => t.id === typeId);
  return type ? type.name : '';
};

onMounted(() => {
  loadLostFoundItems();
});
</script>

<template>
  <div class="lost-found-view">
    <div class="lost-found-header">
      <h1>失物招领</h1>
      <el-button type="primary" @click="router.push('/index')">发布主题</el-button>
    </div>
    <el-divider />
    
    <div v-if="loading" class="loading">
      <el-skeleton :rows="5" animated />
    </div>
    
    <div v-else-if="topics.length === 0" class="empty">
      <el-empty description="暂无失物招领信息" />
    </div>
    
    <div v-else class="lost-found-list">
      <el-card
        v-for="topic in topics"
        :key="topic.id"
        class="lost-found-card"
        @click="goToTopicDetail(topic.id)"
      >
        <template #header>
          <div class="card-header">
            <h2>{{ topic.title }}</h2>
            <el-tag size="small" effect="plain">{{ getTopicTypeName(topic.type) }}</el-tag>
          </div>
        </template>
        
        <div class="card-content">
          <div v-if="topic.images && topic.images.length > 0" class="poster">
            <img :src="topic.images[0]" alt="失物图片" />
          </div>
          <div class="lost-found-info">
            <p class="description">{{ topic.text }}</p>
            <div class="info-item">
              <span class="label">发布时间：</span>
              <span>{{ formatDate(topic.time) }}</span>
            </div>
            <div class="info-item">
              <span class="label">发布者：</span>
              <span>{{ topic.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">互动：</span>
              <span>{{ topic.like }} 点赞 · {{ topic.collect }} 收藏 · {{ topic.comment }} 评论</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.lost-found-view {
  padding: 20px;
}

.lost-found-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.lost-found-header h1 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.loading {
  margin: 20px 0;
}

.empty {
  margin: 40px 0;
}

.lost-found-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.lost-found-card {
  cursor: pointer;
  transition: all 0.3s ease;
}

.lost-found-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.card-content {
  margin: 10px 0;
}

.poster {
  margin-bottom: 15px;
}

.poster img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
}

.lost-found-info {
  line-height: 1.6;
}

.description {
  margin-bottom: 15px;
  color: #666;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.info-item {
  margin-bottom: 8px;
  font-size: 14px;
}

.label {
  font-weight: 500;
  color: #333;
  margin-right: 8px;
}
</style>