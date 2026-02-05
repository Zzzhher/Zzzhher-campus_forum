<script setup>
import LightCard from "@/components/LightCard.vue";
import {
  ArrowRightBold,
  Calendar,
  CircleCheck,
  Clock,
  CollectionTag,
  Compass,
  Document,
  Edit,
  EditPen,
  FolderOpened,
  Link,
  Lock,
  ChatDotRound,
  Microphone,
  Picture,
  Star,
} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import { reactive, computed, ref, watch, onMounted } from "vue";
import { ElMessage } from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";
import { userStore } from "@/store";
import ColorDot from "@/components/ColorDot.vue";
import router from "@/router";
import TopicTag from "@/components/TopicTag.vue";
import TopicCollectList from "@/components/TopicCollectList.vue";
import {
  apiForumTopicList,
  apiForumTopTopics,
  apiForumWeather,
  apiForumTypes,
} from "@/net/api/forum";

const store = userStore();

const weather = reactive({
  location: {},
  now: [],
  hourly: [],
  success: false,
});

const editor = ref(false);
const topics = reactive({
  list: [],
  type: 0,
  page: 0,
  end: false,
  top: [],
});

const collects = ref(false);

watch(
  () => topics.type,
  () => resetList(),
  { immediate: true }
);

function updateList() {
  if (topics.end) return;
  apiForumTopicList(topics.page, topics.type, (data) => {
    if (data) {
      data.forEach((d) => topics.list.push(d));
      topics.page++;
    }
    if (!data || data.length < 10) topics.end = true;
  });
}

function onTopicCreate() {
  editor.value = false;
  resetList();
}

function resetList() {
  topics.page = 0;
  topics.end = false;
  topics.list = [];
  updateList();
}

updateList();

const today = computed(() => {
  const date = new Date();
  return `${date.getFullYear()} 年 ${
    date.getMonth() + 1
  } 月 ${date.getDate()} 日`;
});

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
    ElMessage.warning("位置信息获取超时，请检测网络设置");
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

onMounted(() => {
  apiForumTopTopics((data) => (topics.top = data));
});
</script>

<template>
  <div
    style="
      display: flex;
      margin: 20px auto;
      gap: 20px;
      max-width: 900px;
      padding: 0 20px;
    "
  >
    <div style="flex: 1">
      <light-card>
        <div class="create-topic" @click="editor = true">
          <el-icon><EditPen /></el-icon>点击发表主题...
        </div>
        <div
          style="
            margin-top: 10px;
            display: flex;
            gap: 13px;
            font-size: 18px;
            color: grey;
          "
        >
          <el-icon><Edit /></el-icon>
          <el-icon><Document /></el-icon>
          <el-icon><Compass /></el-icon>
          <el-icon><Picture /></el-icon>
          <el-icon><Microphone /></el-icon>
        </div>
      </light-card>

      <light-card
        style="
          margin-top: 10px;
          display: flex;
          flex-direction: column;
          gap: 10px;
        "
      >
        <div
          v-for="item in topics.top"
          class="top-topic"
          @click="router.push(`/index/topic-detail/${item.id}`)"
        >
          <el-tag type="info" size="small">置顶</el-tag>
          <div>{{ item.title }}</div>
          <div>{{ new Date(item.time).toLocaleString() }}</div>
        </div>
      </light-card>

      <light-card style="margin-top: 10px; display: flex; gap: 7px">
        <div
          :class="`type-select-card ${topics.type === item.id ? 'active' : ''}`"
          v-for="(item, index) in store.forum.types.slice(0, 6)"
          @click="topics.type = item.id"
        >
          <color-dot :color="item.color" />
          <span style="margin-left: 5px">{{ item.name }}</span>
        </div>
      </light-card>
      <transition name="el-fade-in" mode="out-in">
        <div v-if="topics.list.length">
          <div
            v-if="store.forum.types"
            style="
              margin-top: 10px;
              display: flex;
              flex-direction: column;
              gap: 10px;
            "
            v-infinite-scroll="updateList"
          >
            <light-card
              v-for="item in topics.list"
              class="topic-card"
              @click="router.push('/index/topic-detail/' + item.id)"
            >
              <div style="display: flex">
                <div>
                  <el-avatar
                    :size="30"
                    :src="store.avatarUserUrl(item.avatar)"
                  />
                </div>
                <div style="margin-left: 7px; transform: translateY(-2px)">
                  <div style="font-size: 13px; font-weight: bold">
                    {{ item.username }}
                  </div>
                  <div style="font-size: 12px; color: grey">
                    <el-icon><Clock /></el-icon>
                    <div
                      style="
                        margin-left: 2px;
                        display: inline-block;
                        transform: translateY(-2px);
                      "
                    >
                      {{ new Date(item.time).toLocaleString() }}
                    </div>
                  </div>
                </div>
              </div>
              <div style="margin-top: 5px">
                <el-tag
                  size="small"
                  effect="dark"
                  type="warning"
                  disable-transitions
                  style="margin-right: 10px"
                  v-if="item.locked"
                >
                  <el-icon>
                    <Lock />
                  </el-icon>
                  已锁定
                </el-tag>
                <topic-tag :type="item.type" />
                <span style="font-weight: bold; margin-left: 7px">{{
                  item.title
                }}</span>
              </div>
              <div class="topic-content">{{ item.text }}</div>
              <div
                style="
                  display: grid;
                  grid-template-columns: repeat(3, 1fr);
                  grid-gap: 10px;
                "
              >
                <el-image
                  v-for="img in item.images"
                  :src="img"
                  class="topic-image"
                  fit="cover"
                ></el-image>
              </div>
              <div
                style="
                  display: flex;
                  gap: 20px;
                  font-size: 13px;
                  margin-top: 10px;
                  opacity: 0.8;
                "
              >
                <div>
                  <el-icon style="vertical-align: middle"
                    ><CircleCheck
                  /></el-icon>
                  {{ item.like }}点赞
                </div>
                <div>
                  <el-icon style="vertical-align: middle"><Star /></el-icon>
                  {{ item.collect }}收藏
                </div>
                <div>
                  <el-icon style="vertical-align: middle"><ChatDotRound /></el-icon>
                  {{ item.comment }}评论
                </div>
              </div>
            </light-card>
          </div>
        </div>
      </transition>
    </div>
    <div style="width: 280px">
      <div style="position: sticky; top: 20px">
        <light-card>
          <div class="collect-list-button" @click="collects = true">
            <span
              ><el-icon><FolderOpened /></el-icon>查看我的收藏</span
            >
            <el-icon style="transform: translateY(3px)"
              ><ArrowRightBold
            /></el-icon>
          </div>
        </light-card>

        <light-card>
          <div style="font-weight: bold">
            <el-icon><CollectionTag /></el-icon>论坛公告
          </div>
          <el-divider style="margin: 10px 0" />
          <div style="font-size: 14px; margin: 10px; color: grey">
            欢迎来到校园论坛！为营造良好的交流氛围，
            请遵守论坛规则，文明发言，共同维护和谐的网络环境。祝大家学习进步，生活愉快！
          </div>
        </light-card>
        <light-card>
          <div style="font-weight: bold">
            <el-icon>
              <Calendar />
            </el-icon>
            天气信息
          </div>
          <el-divider style="margin: 10px 0" />
          <weather :data="weather" />
        </light-card>
        <light-card>
          <div class="info-text">
            <div>当前日期</div>
            <div>{{ today }}</div>
          </div>
          <div class="info-text">
            <div>当前IP地址</div>
            <div>127.0.0.1</div>
          </div>
        </light-card>
        <light-card>
          <div style="font-size: 14px; color: grey">
            <el-icon>
              <Link />
            </el-icon>
            友情链接
          </div>
          <el-divider style="margin: 10px 0" />
          <div
            style="
              display: grid;
              grid-template-columns: repeat(2, 1fr);
              grid-gap: 10px;
              margin-top: 10px;
            "
          >
            <div class="friend-link">
              <a
                href="https://javascript.info/"
                target="_blank"
                rel="noopener noreferrer"
              >
                <el-image
                  src="https://element-plus.org/images/js-design-banner.jpg"
                  style="height: 100%"
                />
              </a>
            </div>
            <div class="friend-link">
              <a
                href="https://www.vform666.com/"
                target="_blank"
                rel="noopener noreferrer"
              >
                <el-image
                  src="https://element-plus.org/images/vform-banner.png"
                  style="height: 100%"
                />
              </a>
            </div>
            <!--          <div class="friend-link">-->
            <!--            <el-image-->
            <!--              style="height: 100%"-->
            <!--              src="https://element-plus.org/images/sponsors/jnpfsoft.jpg"-->
            <!--            />-->
            <!--          </div>-->
          </div>
        </light-card>
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

<style lang="less" scoped>
.collect-list-button {
  font-size: 14px;
  display: flex;
  justify-content: space-between;
  transition: 0.3s;

  &:hover {
    cursor: pointer;
    opacity: 0.6;
  }
}

.info-text {
  display: flex;
  justify-content: space-between;
  color: grey;
  font-size: 14px;
}

.type-select-card {
  background-color: #f5f5f5;
  padding: 2px 7px;
  font-size: 14px;
  border-radius: 3px;
  box-sizing: border-box;
  transition: background-color 0.3s;

  &.active {
    border: solid 1px #ead4c4;
  }
  &:hover {
    cursor: pointer;
    background-color: #dadada;
  }
}

.top-topic {
  display: flex;

  div:first-of-type {
    font-size: 14px;
    margin-left: 10px;
    font-weight: bold;
    opacity: 0.8;
    transition: color 0.3s;

    &:hover {
      color: grey;
    }
  }

  div:nth-of-type(2) {
    flex: 1;
    color: grey;
    font-size: 13px;
    text-align: right;
  }

  &:hover {
    cursor: pointer;
  }
}

.topic-card {
  padding: 15px;
  transition: scale 0.3s;

  &:hover {
    scale: 1.015;
    cursor: pointer;
  }

  .topic-content {
    font-size: 13px;
    color: grey;
    margin: 5px 0;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 3;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .topic-image {
    width: 100%;
    height: 100%;
    max-height: 110px;
    border-radius: 5px;
  }
}

.friend-link {
  border-radius: 5px;
  overflow: hidden;
}

.create-topic {
  background-color: #efefef;
  border-radius: 5px;
  height: 40px;
  color: grey;
  font-size: 14px;
  line-height: 40px;
  padding: 0 10px;

  &:hover {
    cursor: pointer;
  }
}

.dark {
  .create-topic {
    background-color: #232323;
  }

  .type-select-card {
    background-color: #282828;

    &.active {
      border: solid 1px #64594b;
    }

    &:hover {
      background-color: #5e5e5e;
    }
  }
}
</style>
