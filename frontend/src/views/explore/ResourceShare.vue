<script setup>
import { ref, computed } from 'vue';
import { Document, Download, Star, View, Search, Upload, Folder, Link } from '@element-plus/icons-vue';
import { userStore } from '@/store';

const store = userStore();

// 资源分类
const categories = ref([
  { id: 'all', name: '全部', icon: Document },
  { id: 'study', name: '学习资料', icon: Document },
  { id: 'software', name: '软件工具', icon: Folder },
  { id: 'entertainment', name: '娱乐资源', icon: Star },
  { id: 'life', name: '生活实用', icon: Link }
]);

const currentCategory = ref('all');
const searchQuery = ref('');

// 资源数据
const resources = ref([
  {
    id: 1,
    title: '高等数学复习资料合集',
    description: '包含高数上下册重点知识点总结、历年真题及答案解析',
    category: 'study',
    type: 'pdf',
    size: '25.6 MB',
    downloads: 1250,
    views: 3420,
    rating: 4.8,
    uploader: '学霸小王',
    uploadTime: '2024-01-15',
    tags: ['数学', '考试', '复习'],
    isHot: true
  },
  {
    id: 2,
    title: 'Python编程入门教程',
    description: '从零开始学习Python，包含视频教程和源码',
    category: 'study',
    type: 'video',
    size: '1.2 GB',
    downloads: 890,
    views: 2150,
    rating: 4.6,
    uploader: '编程达人',
    uploadTime: '2024-01-12',
    tags: ['Python', '编程', '教程'],
    isHot: true
  },
  {
    id: 3,
    title: '校园地图高清版',
    description: '最新校园地图，包含所有教学楼、食堂、宿舍位置',
    category: 'life',
    type: 'image',
    size: '8.5 MB',
    downloads: 2340,
    views: 5670,
    rating: 4.9,
    uploader: '校园通',
    uploadTime: '2024-01-10',
    tags: ['地图', '校园', '导航'],
    isHot: true
  },
  {
    id: 4,
    title: 'IDEA开发工具及插件推荐',
    description: 'IDEA安装包及常用插件配置，提高开发效率',
    category: 'software',
    type: 'zip',
    size: '456 MB',
    downloads: 567,
    views: 1230,
    rating: 4.7,
    uploader: '技术大牛',
    uploadTime: '2024-01-08',
    tags: ['IDEA', '开发工具', '插件'],
    isHot: false
  },
  {
    id: 5,
    title: '四六级英语词汇表',
    description: '大学英语四六级核心词汇，带音标和例句',
    category: 'study',
    type: 'pdf',
    size: '5.2 MB',
    downloads: 1890,
    views: 4230,
    rating: 4.5,
    uploader: '英语达人',
    uploadTime: '2024-01-05',
    tags: ['英语', '四六级', '词汇'],
    isHot: true
  },
  {
    id: 6,
    title: '校园周边美食攻略',
    description: '校园周边美食推荐，包含地址、价格、评分',
    category: 'life',
    type: 'pdf',
    size: '12.3 MB',
    downloads: 1560,
    views: 3890,
    rating: 4.8,
    uploader: '吃货小王',
    uploadTime: '2024-01-03',
    tags: ['美食', '攻略', '生活'],
    isHot: true
  },
  {
    id: 7,
    title: '考研资料大礼包',
    description: '包含各科考研真题、复习资料、经验分享',
    category: 'study',
    type: 'zip',
    size: '856 MB',
    downloads: 2100,
    views: 4560,
    rating: 4.9,
    uploader: '考研上岸',
    uploadTime: '2024-01-01',
    tags: ['考研', '资料', '真题'],
    isHot: true
  },
  {
    id: 8,
    title: '电影资源分享',
    description: '经典电影合集，包含中外优秀影片',
    category: 'entertainment',
    type: 'link',
    size: '-',
    downloads: 3400,
    views: 8900,
    rating: 4.7,
    uploader: '电影迷',
    uploadTime: '2023-12-28',
    tags: ['电影', '娱乐', '资源'],
    isHot: true
  }
]);

// 过滤后的资源
const filteredResources = computed(() => {
  let result = resources.value;
  
  // 按分类过滤
  if (currentCategory.value !== 'all') {
    result = result.filter(r => r.category === currentCategory.value);
  }
  
  // 按搜索词过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(r => 
      r.title.toLowerCase().includes(query) ||
      r.description.toLowerCase().includes(query) ||
      r.tags.some(tag => tag.toLowerCase().includes(query))
    );
  }
  
  return result;
});

// 获取文件类型图标
const getFileIcon = (type) => {
  const iconMap = {
    'pdf': 'Document',
    'video': 'VideoPlay',
    'image': 'Picture',
    'zip': 'Folder',
    'link': 'Link'
  };
  return iconMap[type] || 'Document';
};

// 获取分类名称
const getCategoryName = (categoryId) => {
  const category = categories.value.find(c => c.id === categoryId);
  return category ? category.name : '其他';
};

// 下载资源
const downloadResource = (resource) => {
  console.log('下载资源:', resource.title);
  // 这里可以添加实际的下载逻辑
};

// 上传资源对话框
const uploadDialogVisible = ref(false);
const uploadForm = ref({
  title: '',
  description: '',
  category: 'study',
  file: null
});

const showUploadDialog = () => {
  uploadDialogVisible.value = true;
};

const submitUpload = () => {
  console.log('提交上传:', uploadForm.value);
  uploadDialogVisible.value = false;
  // 这里可以添加上传逻辑
};
</script>

<template>
  <div class="resource-share">
    <!-- 页面标题 -->
    <el-card shadow="hover" class="header-card">
      <div class="header-content">
        <div class="header-text">
          <h2>资源共享</h2>
          <p>学习资料、软件工具、生活实用资源分享平台</p>
        </div>
        <el-button type="primary" @click="showUploadDialog">
          <el-icon><Upload /></el-icon>
          分享资源
        </el-button>
      </div>
    </el-card>

    <!-- 搜索和分类 -->
    <el-card shadow="hover" class="filter-card">
      <div class="filter-content">
        <!-- 搜索框 -->
        <el-input
          v-model="searchQuery"
          placeholder="搜索资源..."
          clearable
          class="search-input"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        
        <!-- 分类标签 -->
        <div class="category-tags">
          <el-tag
            v-for="category in categories"
            :key="category.id"
            :type="currentCategory === category.id ? 'primary' : 'info'"
            :effect="currentCategory === category.id ? 'dark' : 'plain'"
            class="category-tag"
            @click="currentCategory = category.id"
          >
            <el-icon><component :is="category.icon" /></el-icon>
            {{ category.name }}
          </el-tag>
        </div>
      </div>
    </el-card>

    <!-- 资源列表 -->
    <el-card shadow="hover" class="resources-card">
      <template #header>
        <div class="card-header">
          <span>资源列表 ({{ filteredResources.length }})</span>
        </div>
      </template>
      
      <div class="resources-list">
        <el-card
          v-for="resource in filteredResources"
          :key="resource.id"
          shadow="hover"
          class="resource-item"
        >
          <div class="resource-header">
            <div class="resource-title-section">
              <div class="resource-icon">
                <el-icon :size="24"><component :is="getFileIcon(resource.type)" /></el-icon>
              </div>
              <div class="resource-info">
                <h4 class="resource-title">
                  {{ resource.title }}
                  <el-tag v-if="resource.isHot" size="small" type="danger" effect="dark">热门</el-tag>
                </h4>
                <p class="resource-desc">{{ resource.description }}</p>
                <div class="resource-tags">
                  <el-tag
                    v-for="tag in resource.tags"
                    :key="tag"
                    size="small"
                    effect="plain"
                  >
                    {{ tag }}
                  </el-tag>
                  <el-tag size="small" type="info">{{ getCategoryName(resource.category) }}</el-tag>
                </div>
              </div>
            </div>
            <div class="resource-actions">
              <el-button type="primary" @click="downloadResource(resource)">
                <el-icon><Download /></el-icon>
                下载
              </el-button>
            </div>
          </div>
          
          <el-divider />
          
          <div class="resource-footer">
            <div class="resource-stats">
              <span class="stat-item">
                <el-icon><View /></el-icon>
                {{ resource.views }} 浏览
              </span>
              <span class="stat-item">
                <el-icon><Download /></el-icon>
                {{ resource.downloads }} 下载
              </span>
              <span class="stat-item">
                <el-icon><Star /></el-icon>
                {{ resource.rating }} 分
              </span>
              <span class="stat-item">大小: {{ resource.size }}</span>
            </div>
            <div class="resource-meta">
              <span>上传者: {{ resource.uploader }}</span>
              <span>上传时间: {{ resource.uploadTime }}</span>
            </div>
          </div>
        </el-card>
      </div>
      
      <!-- 空状态 -->
      <el-empty v-if="filteredResources.length === 0" description="暂无相关资源" />
    </el-card>

    <!-- 上传资源对话框 -->
    <el-dialog
      v-model="uploadDialogVisible"
      title="分享资源"
      width="500px"
    >
      <el-form :model="uploadForm" label-width="80px">
        <el-form-item label="资源标题">
          <el-input v-model="uploadForm.title" placeholder="请输入资源标题" />
        </el-form-item>
        <el-form-item label="资源描述">
          <el-input
            v-model="uploadForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入资源描述"
          />
        </el-form-item>
        <el-form-item label="资源分类">
          <el-select v-model="uploadForm.category" placeholder="请选择分类">
            <el-option
              v-for="cat in categories.filter(c => c.id !== 'all')"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="上传文件">
          <el-upload
            drag
            action="#"
            :auto-upload="false"
          >
            <el-icon :size="50"><Upload /></el-icon>
            <div class="el-upload__text">
              拖拽文件到此处或 <em>点击上传</em>
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUpload">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="less" scoped>
.resource-share {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;

  .header-card {
    margin-bottom: 20px;

    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;

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

  .filter-card {
    margin-bottom: 20px;

    .filter-content {
      .search-input {
        margin-bottom: 15px;
      }

      .category-tags {
        display: flex;
        gap: 10px;
        flex-wrap: wrap;

        .category-tag {
          cursor: pointer;
          display: flex;
          align-items: center;
          gap: 5px;

          &:hover {
            opacity: 0.8;
          }
        }
      }
    }
  }

  .resources-card {
    .card-header {
      font-weight: bold;
      color: #303133;
    }

    .resources-list {
      display: flex;
      flex-direction: column;
      gap: 15px;

      .resource-item {
        .resource-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;

          .resource-title-section {
            display: flex;
            gap: 15px;
            flex: 1;

            .resource-icon {
              width: 50px;
              height: 50px;
              background-color: #f0f9ff;
              border-radius: 8px;
              display: flex;
              align-items: center;
              justify-content: center;
              color: #409EFF;
            }

            .resource-info {
              flex: 1;

              .resource-title {
                margin: 0 0 8px 0;
                color: #303133;
                display: flex;
                align-items: center;
                gap: 8px;
              }

              .resource-desc {
                margin: 0 0 10px 0;
                color: #606266;
                font-size: 14px;
                line-height: 1.5;
              }

              .resource-tags {
                display: flex;
                gap: 8px;
                flex-wrap: wrap;
              }
            }
          }
        }

        .resource-footer {
          .resource-stats {
            display: flex;
            gap: 20px;
            margin-bottom: 10px;
            flex-wrap: wrap;

            .stat-item {
              display: flex;
              align-items: center;
              gap: 5px;
              font-size: 13px;
              color: #909399;
            }
          }

          .resource-meta {
            display: flex;
            gap: 20px;
            font-size: 12px;
            color: #c0c4cc;
          }
        }
      }
    }
  }
}
</style>
