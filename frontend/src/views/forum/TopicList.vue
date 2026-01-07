<script setup>
import LightCard from "@/components/LightCard.vue";
import {
  Calendar,
  CollectionTag,
  EditPen,
  Link,
} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {reactive, computed, ref} from "vue";
import { get } from "@/net";
import { ElMessage } from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";

const weather = reactive({
  location: {},
  now: [],
  hourly: [],
  success: false,
});

const editor = ref(false)

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
          <el-icon><EditPen /></el-icon> 点击发表主题...
        </div>
      </light-card>
      <light-card style="margin-top: 10px; height: 30px"> </light-card>
      <div
        style="
          margin-top: 10px;
          display: flex;
          flex-direction: column;
          gap: 10px;
        "
      >
        <light-card style="height: 150px" v-for="item in 10"> </light-card>
      </div>
    </div>
    <div style="width: 280px">
      <div style="position: sticky; top: 20px">
        <light-card>
          <div style="font-weight: bold">
            <el-icon><CollectionTag /></el-icon>
            论坛公告
          </div>
          <el-divider style="margin: 10px 0" />
          <div style="font-size: 14px; margin: 10px; color: grey">
            为认真学习宣传贯彻党的二十大精神，深入贯彻习近平强军思想，
            作为迎接办学70周年系列学术活动之一，国防科技大学将于2022年11月24日至26日在长沙举办"国防科技
          </div>
        </light-card>
        <light-card style="margin-top: 10px">
          <div style="font-weight: bold">
            <el-icon><Calendar /></el-icon>
            天气信息
          </div>
          <el-divider style="margin: 10px 0" />
          <weather :data="weather" />
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
          <el-icon><Link /></el-icon>
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
            <el-image
              style="height: 100%"
              src="https://element-plus.org/images/js-design-banner.jpg"
            />
          </div>
          <div class="friend-link">
            <el-image
              style="height: 100%"
              src="https://element-plus.org/images/vform-banner.png"
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
    <topic-editor :show="editor" @success="editor = false" @close="editor = false"/>
  </div>
</template>

<style lang="less" scoped>
.info-text {
  display: flex;
  justify-content: space-between;
  color: grey;
  font-size: 14px;
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
