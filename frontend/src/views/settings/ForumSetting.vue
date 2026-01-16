<script setup>
import {
  apiForumUserTopic,
  apiForumUserTopicDelete,
  apiForumTypes,
} from "@/net/api/forum";
import { reactive, watchEffect } from "vue";
import Card from "@/components/Card.vue";
import TopicTag from "@/components/TopicTag.vue";
import { Clock, Delete, Hide, Lock } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { userStore } from "@/store";

const store = userStore();

const topicList = reactive({
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
            refreshList();
          });
        }
      },
    }
  );
};

const refreshList = () => {
  apiForumUserTopic(topicList.page, topicList.size, (data) => {
    topicList.list = data.list;
    topicList.total = data.total;
  });
};

watchEffect(() => refreshList());
</script>

<template>
  <div style="margin: auto; max-width: 700px">
    <div class="topic-list">
      <card v-for="topic in topicList.list">
        <div class="title">
          <el-tag
            size="small"
            effect="dark"
            type="info"
            disable-transitions
            style="margin-right: 10px"
            v-if="topic.locked"
          >
            <el-icon>
              <Hide />
            </el-icon>
            屏蔽
          </el-tag>
          <el-tag
            size="small"
            effect="dark"
            type="warning"
            disable-transitions
            style="margin-right: 10px"
            v-if="topic.locked"
          >
            <el-icon>
              <Lock />
            </el-icon>
            已锁定
          </el-tag>
          <topic-tag style="margin-right: 10px" :type="topic.type" />
          <el-link :href="`/index/topic-detail/${topic.id}`">{{
            topic.title
          }}</el-link>
        </div>
        <div class="content">
          <div style="font-size: 12px; color: gray">
            <el-icon>
              <Clock />
            </el-icon>
            <div style="margin-left: 2px; display: inline-block">
              {{ new Date(topic.time).toLocaleString() }}
            </div>
          </div>
          <el-link @click="deleteTopic(topic.id)" type="danger">
            <el-icon><Delete /></el-icon>&nbsp;
            <span>删除帖子</span>
          </el-link>
        </div>
      </card>
    </div>
    <div class="pagination">
      <el-pagination
        :total="topicList.total"
        v-model:current-page="topicList.page"
        v-model:page-size="topicList.size"
        layout="total, sizes, prev, pager, next, jumper"
      />
    </div>
  </div>
</template>

<style lang="less" scoped>
.topic-list {
  gap: 10px;
  display: flex;
  flex-direction: column;
  margin: 20px 0;

  .content {
    display: flex;
    justify-content: space-between;
    margin-top: 10px;
  }
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
