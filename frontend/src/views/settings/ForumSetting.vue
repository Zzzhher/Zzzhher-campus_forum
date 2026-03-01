<script setup>
import {
  apiForumUserTopic,
  apiForumUserTopicDelete,
  apiForumUserComment,
  apiForumCommentDelete,
} from "@/net/api/forum";
import { reactive, watchEffect, ref } from "vue";
import Card from "@/components/Card.vue";
import TopicTag from "@/components/TopicTag.vue";
import { Clock, Delete, Hide, Lock, ChatDotSquare, Document } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { userStore } from "@/store";

const store = userStore();
const activeTab = ref('topic');

const topicList = reactive({
  list: [],
  total: 0,
  page: 1,
  size: 10,
});

const commentList = reactive({
  list: [],
  total: 0,
  page: 1,
  size: 10,
});

const deleteTopic = (id) => {
  ElMessageBox.confirm(
    "删除后帖子将永远无法找回，您确定要这样做吗？",
    "删除帖子",
    {
      callback: (value) => {
        if (value === "confirm") {
          apiForumUserTopicDelete(id, () => {
            ElMessage.success("帖子删除成功");
            refreshTopicList();
          });
        }
      },
    }
  );
};

const deleteComment = (id) => {
  ElMessageBox.confirm(
    "删除后评论将永远无法找回，您确定要这样做吗？",
    "删除评论",
    {
      callback: (value) => {
        if (value === "confirm") {
          apiForumCommentDelete(id, () => {
            ElMessage.success("评论删除成功");
            refreshCommentList();
          });
        }
      },
    }
  );
};

const refreshTopicList = () => {
  apiForumUserTopic(topicList.page, topicList.size, (data) => {
    topicList.list = data.list;
    topicList.total = data.total;
  });
};

const refreshCommentList = () => {
  apiForumUserComment(commentList.page, commentList.size, (data) => {
    commentList.list = data.list;
    commentList.total = data.total;
  });
};

// 解析Quill编辑器内容为纯文本
const extractPlainText = (content) => {
  if (!content) return '';
  try {
    const contentObj = JSON.parse(content);
    if (contentObj.ops && Array.isArray(contentObj.ops)) {
      let text = '';
      contentObj.ops.forEach(op => {
        if (typeof op.insert === 'string') {
          text += op.insert;
        }
      });
      return text.trim();
    }
  } catch (e) {}
  return content;
};

watchEffect(() => {
  if (activeTab.value === 'topic') {
    refreshTopicList();
  } else {
    refreshCommentList();
  }
});
</script>

<template>
  <div class="forum-setting-container">
    <div class="page-header">
      <h2 class="page-title">
        <el-icon><Document /></el-icon>
        我的内容管理
      </h2>
      <p class="page-subtitle">管理您发布的帖子和评论</p>
    </div>

    <el-tabs v-model="activeTab" class="custom-tabs" type="card">
      <el-tab-pane name="topic">
        <template #label>
          <span class="tab-label">
            <el-icon><Document /></el-icon>
            我的帖子
            <el-tag v-if="topicList.total > 0" size="small" type="info" class="count-tag">{{ topicList.total }}</el-tag>
          </span>
        </template>

        <div v-if="topicList.list.length === 0" class="empty-state">
          <el-empty description="暂无帖子" />
        </div>

        <div v-else class="content-list">
          <card v-for="topic in topicList.list" :key="topic.id" class="content-card topic-card">
            <div class="card-header">
              <div class="topic-tags">
                <el-tag v-if="topic.locked" size="small" effect="dark" type="warning" class="status-tag">
                  <el-icon><Lock /></el-icon>已锁定
                </el-tag>
                <topic-tag :type="topic.type" />
              </div>
              <el-button 
                @click="deleteTopic(topic.id)" 
                type="danger" 
                link 
                size="small"
                class="delete-btn"
              >
                <el-icon><Delete /></el-icon>删除
              </el-button>
            </div>

            <div class="topic-title">
              <el-link :href="`/index/topic-detail/${topic.id}`" class="title-link">
                {{ topic.title }}
              </el-link>
            </div>

            <div class="card-footer">
              <div class="time-info">
                <el-icon><Clock /></el-icon>
                <span>{{ new Date(topic.time).toLocaleString() }}</span>
              </div>
            </div>
          </card>
        </div>

        <div class="pagination-wrapper">
          <el-pagination
            :total="topicList.total"
            v-model:current-page="topicList.page"
            v-model:page-size="topicList.size"
            layout="total, sizes, prev, pager, next, jumper"
            :page-sizes="[10, 20, 50]"
          />
        </div>
      </el-tab-pane>

      <el-tab-pane name="comment">
        <template #label>
          <span class="tab-label">
            <el-icon><ChatDotSquare /></el-icon>
            我的评论
            <el-tag v-if="commentList.total > 0" size="small" type="info" class="count-tag">{{ commentList.total }}</el-tag>
          </span>
        </template>

        <div v-if="commentList.list.length === 0" class="empty-state">
          <el-empty description="暂无评论" />
        </div>

        <div v-else class="content-list">
          <card v-for="comment in commentList.list" :key="comment.id" class="content-card comment-card">
            <div class="card-header">
              <el-link :href="`/index/topic-detail/${comment.tid}`" class="post-link">
                <el-icon><ChatDotSquare /></el-icon>
                评论于帖子 #{{ comment.tid }}
              </el-link>
              <el-button 
                @click="deleteComment(comment.id)" 
                type="danger" 
                link 
                size="small"
                class="delete-btn"
              >
                <el-icon><Delete /></el-icon>删除
              </el-button>
            </div>

            <div class="comment-body">
              {{ extractPlainText(comment.content) }}
            </div>

            <div class="card-footer">
              <div class="time-info">
                <el-icon><Clock /></el-icon>
                <span>{{ new Date(comment.time).toLocaleString() }}</span>
              </div>
            </div>
          </card>
        </div>

        <div class="pagination-wrapper">
          <el-pagination
            :total="commentList.total"
            v-model:current-page="commentList.page"
            v-model:page-size="commentList.size"
            layout="total, sizes, prev, pager, next, jumper"
            :page-sizes="[10, 20, 50]"
          />
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style lang="less" scoped>
.forum-setting-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);

  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: var(--el-text-color-primary);
    margin: 0 0 8px 0;
    display: flex;
    align-items: center;
    gap: 10px;

    .el-icon {
      font-size: 28px;
      color: var(--el-color-primary);
    }
  }

  .page-subtitle {
    font-size: 14px;
    color: var(--el-text-color-secondary);
    margin: 0;
  }
}

.custom-tabs {
  :deep(.el-tabs__header) {
    margin-bottom: 20px;
  }

  :deep(.el-tabs__item) {
    padding: 0 24px;
    height: 44px;
    font-size: 15px;

    &.is-active {
      font-weight: 600;
    }
  }
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 6px;

  .el-icon {
    font-size: 16px;
  }

  .count-tag {
    margin-left: 4px;
  }
}

.empty-state {
  padding: 60px 0;
}

.content-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.content-card {
  transition: all 0.3s ease;
  border-radius: 8px;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }
}

.topic-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .topic-tags {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .status-tag {
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }

  .topic-title {
    margin-bottom: 12px;

    .title-link {
      font-size: 16px;
      font-weight: 500;
      color: var(--el-text-color-primary);
      line-height: 1.5;

      &:hover {
        color: var(--el-color-primary);
      }
    }
  }
}

.comment-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .post-link {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 13px;
      color: var(--el-color-primary);

      .el-icon {
        font-size: 14px;
      }
    }
  }

  .comment-body {
    padding: 12px 16px;
    background-color: var(--el-fill-color-light);
    border-radius: 6px;
    font-size: 14px;
    line-height: 1.6;
    color: var(--el-text-color-regular);
    margin-bottom: 12px;
    word-break: break-all;
  }
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .time-info {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;
    color: var(--el-text-color-secondary);

    .el-icon {
      font-size: 14px;
    }
  }
}

.delete-btn {
  opacity: 0.7;
  transition: opacity 0.2s;

  &:hover {
    opacity: 1;
  }
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
  padding-top: 16px;
  border-top: 1px solid var(--el-border-color-lighter);
}

// 深色模式适配
.dark {
  .comment-body {
    background-color: var(--el-fill-color-dark);
  }
}
</style>
