<script setup>
import { useRoute } from "vue-router";
import { get } from "@/net";
import {computed, reactive} from "vue";
import axios from "axios";
import {ArrowLeft, CircleCheck, Female, Male, Star} from "@element-plus/icons-vue";
import { QuillDeltaToHtmlConverter } from 'quill-delta-to-html';
import Card from "@/components/Card.vue";
import router from "@/router";
import TopicTag from "@/components/TopicTag.vue";
import InteractButton from "@/components/InteractButton.vue";
import {ElMessage} from "element-plus";
const route = useRoute();
const tid = route.params.tid;

const topic = reactive({
  data: null,
  like: false,
  collect: false,
  comments: [],
});

get(`api/forum/topic?tid=${tid}`, (data) => {
  topic.data = data
  topic.like = data.interact.like
  topic.collect = data.interact.collect
})

const content = computed(() => {
    const ops = JSON.parse(topic.data.content).ops
    const converter = new QuillDeltaToHtmlConverter(ops, { inlineStyles: true });
    return converter.convert();
})

function interact(type, message) {
  get(`/api/forum/interact?tid=${tid}&type=${type}&state=${!topic[type]}`, () => {
    topic[type] = !topic[type]
    if (topic[type])
      ElMessage.success(`${message}成功!`)
    else
      ElMessage.success(`已取消${message}!`)
  })

}
</script>

<template>
  <div class="topic-page" v-if="topic.data">
    <div class="topic-main" style="position: sticky;top: 0;z-index: 10">
      <card style="display: flex; width: 100%;">
        <el-button :icon="ArrowLeft" type="primary" size="small" plain round @click="router.push('/index')">返回列表</el-button>
        <div style="text-align: center;flex: 1">
          <topic-tag :type="topic.data.type"/>
          <span style="font-weight: bold;margin-left: 5px">{{topic.data.title}}</span>
        </div>
      </card>
    </div>
    <div class="topic-main">
      <div class="topic-main-left">
        <el-avatar
          :src="axios.defaults.baseURL + '/images' + topic.data.user.avatar"
          :size="60"
        />
        <div>
          <div style="font-size: 18px; font-weight: bold">
            {{ topic.data.user.username }}
            <span style="color: hotpink" v-if="topic.data.user.gender === 1">
              <el-icon><Female /></el-icon>
            </span>
            <span style="color: dodgerblue" v-if="topic.data.user.gender === 0">
              <el-icon><Male /></el-icon>
            </span>
          </div>
          <div class="desc">{{ topic.data.user.email }}</div>
          <el-divider style="margin: 10px 0" />
          <div class="contact-info">
            <div class="contact-item">
              <span class="contact-label">微信号:</span>
              <span class="contact-value">{{
                topic.data.user.wx || "已隐藏或未填写"
              }}</span>
            </div>
            <div class="contact-item">
              <span class="contact-label">QQ号:</span>
              <span class="contact-value">{{
                topic.data.user.qq || "已隐藏或未填写"
              }}</span>
            </div>
            <div class="contact-item">
              <span class="contact-label">手机号:</span>
              <span class="contact-value">{{
                topic.data.user.phone || "已隐藏或未填写"
              }}</span>
            </div>
          </div>
        </div>
        <el-divider style="margin: 10px 0" />
        <div class="contact-info" style="margin: 0 5px;font-style: italic;font-size: 16px;color: #606266;
             font-family: 'Arial', sans-serif;
             line-height: 1.6;">{{ topic.data.user.desc }}</div>
      </div>
      <div class="topic-main-right">
        <div class="topic-content" v-html="content"></div>
        <el-divider/>
        <div style="font-size: 13px;color: grey; text-align: center">
          <div>发帖时间: {{new Date(topic.data.time).toLocaleString()}}</div>
        </div>
        <div style="text-align: right;margin-top: 30px">
          <interact-button name="点个赞吧" check-name="已点赞" color="pink" :check="topic.like"
                           @check="interact('like', '点赞')">
            <el-icon><CircleCheck/></el-icon>
          </interact-button>
          <interact-button name="点击收藏" check-name="已收藏" color="orange" :check="topic.collect"
                           @check="interact('collect', '收藏')"
                           style="margin-left: 20px">
            <el-icon><Star/></el-icon>
          </interact-button>
        </div>
      </div>
    </div>
    <div></div>
  </div>
</template>

<style scoped>
.topic-page {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 10px 0;
}
.topic-main {
  display: flex;
  border-radius: 7px;
  margin: 0 auto;
  background-color: var(--el-bg-color);
  width: 800px;

  .topic-main-left {
    width: 200px;
    padding: 10px;
    text-align: center;
    border-right: solid 1px var(--el-border-color);

    .desc {
      color: #606266;
      font-size: 14px;
      margin-top: 4px;
    }

    .contact-info {
      padding: 8px 12px;
      background-color: #e6f7ff;
      border-radius: 4px;
      text-align: left;
    }

    .contact-item {
      display: flex;
      align-items: center;
      padding: 4px 0;
      line-height: 1.5;
    }

    .contact-label {
      color: #909399;
      font-size: 14px;
      width: 60px;
      flex-shrink: 0;
    }

    .contact-value {
      color: #303133;
      font-size: 14px;
      flex: 1;
      word-break: break-all;
    }
  }
  .topic-main-right {
    width: 600px;
    padding: 10px 20px;

    .topic-content {
      font-size: 14px;
      line-height: 22px;
      opacity: 0.8;
    }
  }
}
</style>
