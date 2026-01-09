<script setup>
import LightCard from "@/components/LightCard.vue";
import {
  ArrowRightBold,
  Calendar, CircleCheck, Clock,
  CollectionTag, Compass, Document, Edit,
  EditPen, FolderOpened,
  Link, Microphone, Picture, Star,
} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {reactive, computed, ref, watch} from "vue";
import {get} from "@/net";
import {ElMessage} from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";
import {userStore} from "@/store";
import axios from "axios";
import ColorDot from "@/components/ColorDot.vue";
import router from "@/router";
import TopicTag from "@/components/TopicTag.vue";
import TopicCollectList from "@/components/TopicCollectList.vue";

const store = userStore()

const weather = reactive({
  location: {},
  now: [],
  hourly: [],
  success: false,
});

const editor = ref(false)
const topics = reactive({
  list: [],
  type: 0,
  page: 0,
  end: false,
  top: []
})

const collects = ref(false)


watch( () => topics.type, () => resetList(), {immediate: true})


get('/api/forum/top-topic', data => topics.top = data)
function updateList() {
  if (topics.end) return
  get(`/api/forum/list-topic?page=${topics.page}&type=${topics.type}`, data => {
    if (data) {
      data.forEach(d => topics.list.push(d))
      topics.page++
    }
    if (!data || data.length < 10)
      topics.end = true
  })
}

function onTopicCreate() {
  editor.value = false
  resetList()
}

function resetList() {
  topics.page = 0
  topics.end = false
  topics.list = []
  updateList()
}

updateList()


const today = computed(() => {
  const date = new Date()
  return `${date.getFullYear()} 年 ${date.getMonth() + 1} 月 ${date.getDate()} 日`
})

// 检测浏览器是否支持地理定位
if (navigator.geolocation) {
  // 尝试获取用户位置，设置更长的超时时间以兼容浏览器
  navigator.geolocation.getCurrentPosition(
      (position) => {
        const longitude = position.coords.longitude;
        const latitude = position.coords.latitude;
        console.log("成功获取位置:", longitude, latitude);
        get(
            `/api/forum/weather?longitude=${longitude}&latitude=${latitude}`,
            (data) => {
              Object.assign(weather, data);
              weather.success = true;
            },
            (error) => {
              console.error("天气API请求失败:", error);
              ElMessage.error("获取天气信息失败，请稍后重试");
              // API失败时使用默认位置
              useDefaultLocation();
            }
        );
      },
      (error) => {
        console.warn("地理定位失败:", error.code, error.message);
        let message = "";
        switch (error.code) {
          case error.PERMISSION_DENIED:
            message = "您拒绝了位置权限，将使用默认位置显示天气";
            break;
          case error.POSITION_UNAVAILABLE:
            message = "无法获取位置信息，将使用默认位置显示天气";
            break;
          case error.TIMEOUT:
            message = "位置获取超时，将使用默认位置显示天气";
            break;
          default:
            message = "位置获取失败，将使用默认位置显示天气";
        }
        ElMessage.warning(message);
        // 使用默认位置（北京）
        useDefaultLocation();
      },
      {
        timeout: 10000, // 增加超时时间到10秒
        enableHighAccuracy: false, // 改为false以提高兼容性
        maximumAge: 300000, // 允许使用5分钟内的缓存位置
      }
  );
} else {
  console.warn("浏览器不支持地理定位");
  ElMessage.warning("您的浏览器不支持地理定位，将使用默认位置显示天气");
  useDefaultLocation();
}

// 使用默认位置获取天气
function useDefaultLocation() {
  get(
      `/api/forum/weather?longitude=116.41&latitude=39.92`,
      (data) => {
        Object.assign(weather, data);
        weather.success = true;
      },
      (error) => {
        console.error("默认位置天气API请求失败:", error);
        ElMessage.error("获取天气信息失败，请检查网络连接");
      }
  );
}
</script>

<template>
  <div style="display: flex; margin: 20px auto; gap: 20px; max-width: 900px">
    <div style="flex: 1">
      <light-card>
        <div class="create-topic" @click="editor = true">
          <el-icon><EditPen/></el-icon>点击发表主题...
        </div>
        <div style="margin-top: 10px;display: flex;gap: 13px;font-size: 18px;color: grey">
          <el-icon><Edit /></el-icon>
          <el-icon><Document /></el-icon>
          <el-icon><Compass /></el-icon>
          <el-icon><Picture /></el-icon>
          <el-icon><Microphone /></el-icon>
        </div>
      </light-card>

      <light-card style="margin-top: 10px; display: flex; flex-direction: column; gap: 10px">
        <div v-for="item in topics.top" class="top-topic" @click="router.push(`/index/topic-detail/${item.id}`)">
          <el-tag type="info" size="small">置顶</el-tag>
          <div>{{item.title}}</div>
          <div>{{new Date(item.time).toLocaleString()}}</div>
        </div>
      </light-card>

      <light-card style="margin-top: 10px;display: flex;gap: 7px">
        <div :class="`type-select-card ${topics.type === item.id ? 'active' : ''}`" v-for="item in store.forum.types" @click="topics.type = item.id">
          <color-dot :color="item.color"/>
          <span style="margin-left: 5px">{{item.name}}</span>
        </div>
      </light-card>
      <transition name="el-fade-in" mode="out-in">
        <div v-if="topics.list.length">
          <div v-if="store.forum.types" style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px;" v-infinite-scroll="updateList">
            <light-card v-for="item in topics.list" class="topic-card" @click="router.push('/index/topic-detail/'+item.id)">
              <div style="display: flex">
                <div><el-avatar :size="30" :src="`${axios.defaults.baseURL}/images${item.avatar}`"/></div>
                <div style="margin-left: 7px; transform: translateY(-2px)">
                  <div style="font-size: 13px;font-weight: bold">{{ item.username }}</div>
                  <div style="font-size: 12px;color: grey"><el-icon><Clock/></el-icon>
                    <div style="margin-left: 2px;display: inline-block;transform: translateY(-2px)">
                      {{ new Date(item.time).toLocaleString() }}</div>
                  </div>
                </div>
              </div>
              <div style="margin-top: 5px">
                <topic-tag :type="item.type"/>
                <span style="font-weight: bold;margin-left: 7px">{{ item.title }}</span>
              </div>
              <div class="topic-content">{{ item.text }}</div>
              <div style="display: grid;grid-template-columns: repeat(3, 1fr);grid-gap: 10px">
                <el-image v-for="img in item.images" :src="img" class="topic-image" fit="cover"></el-image>
              </div>
              <div style="display: flex;gap: 20px;font-size: 13px;margin-top: 10px;opacity: 0.8">
                <div>
                  <el-icon style="vertical-align: middle"><CircleCheck/></el-icon> {{item.like}}点赞
                </div>
                <div>
                  <el-icon style="vertical-align: middle"><Star/></el-icon> {{item.collect}}收藏
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
          <div class="collect-list-button" @click="collects = true"><span><el-icon><FolderOpened/></el-icon>查看我的收藏</span>
            <el-icon style="transform: translateY(3px)"><ArrowRightBold/></el-icon>
          </div>
        </light-card>

        <light-card>
          <div style="font-weight: bold">
            <el-icon><CollectionTag/></el-icon>论坛公告
          </div>
          <el-divider style="margin: 10px 0"/>
          <div style="font-size: 14px; margin: 10px; color: grey">
            为认真学习宣传贯彻党的二十大精神，深入贯彻习近平强军思想，
            作为迎接办学70周年系列学术活动之一，国防科技大学将于2022年11月24日至26日在长沙举办"国防科技
          </div>
        </light-card>
        <light-card style="margin-top: 10px">
          <div style="font-weight: bold">
            <el-icon>
              <Calendar/>
            </el-icon>
            天气信息
          </div>
          <el-divider style="margin: 10px 0"/>
          <weather :data="weather"/>
        </light-card>
        <light-card style="margin-top: 10px">
          <div class="info-text">
            <div>当前日期</div>
            <div>{{ today }}</div>
          </div>
          <div class="info-text">
            <div>当前IP地址</div>
            <div>127.0.0.1</div>
          </div>
        </light-card>
        <div style="font-size: 14px; margin-top: 10px; color: grey">
          <el-icon>
            <Link/>
          </el-icon>
          友情链接
        </div>
        <el-divider style="margin: 10px 0"/>
        <div
            style="
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            grid-gap: 10px;
            margin-top: 10px;
          "
        >
          <div class="friend-link">
            <el-image
                src="https://element-plus.org/images/js-design-banner.jpg"
                style="height: 100%"
            />
          </div>
          <div class="friend-link">
            <el-image
                src="https://element-plus.org/images/vform-banner.png"
                style="height: 100%"
            />
          </div>
          <!--          <div class="friend-link">-->
          <!--            <el-image-->
          <!--              style="height: 100%"-->
          <!--              src="https://element-plus.org/images/sponsors/jnpfsoft.jpg"-->
          <!--            />-->
          <!--          </div>-->
        </div>
      </div>
    </div>
    <topic-editor :show="editor" @close="editor = false" @success="onTopicCreate"/>
    <topic-collect-list :show="collects" @close="collects = false"/>
  </div>
</template>

<style lang="less" scoped>


.collect-list-button {
  font-size: 14px;
  display: flex;
  justify-content: space-between;
  transition: .3s;

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
  transition: background-color .3s;

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
    transition: color .3s;

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
  transition: scale .3s;

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
