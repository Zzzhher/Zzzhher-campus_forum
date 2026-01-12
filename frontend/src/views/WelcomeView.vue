<template>
  <div class="welcome-container">
    <div class="left-image">
      <el-image
        style="width: 100%; height: 100%"
        fit="cover"
        src="https://pic.netbian.com/uploads/allimg/251229/221744-176701786486a8.jpg"
      />
    </div>
    <div class="welcome-title">
      <div style="font-size: 30px; font-weight: bold">
        欢迎来到我们的校园论坛交流社区
      </div>
      <div style="margin-top: 10px">
        在这里你可以畅聊校园新鲜事，分享学习干货，结识同频的小伙伴～
      </div>
      <div style="margin-top: 5px">
        在这里你可以提问解惑、发布闲置、组队活动、吐槽日常，解锁校园生活更多可能！
      </div>
    </div>
    <div class="right-card">
      <router-view v-slot="{ Component }">
        <transition name="el-fade-in-linear" mode="out-in">
          <component :is="Component" style="height: 100%" />
        </transition>
      </router-view>
    </div>
  </div>
</template>

<script setup></script>

<style scoped>
.welcome-container {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  display: flex;
  background-color: #f5f7fa;
}

.left-image {
  flex: 1;
  position: relative;
}

.welcome-title {
  position: absolute;
  bottom: 30px;
  left: 30px;
  color: white;
  text-shadow: 0 0 15px rgba(0, 0, 0, 0.8);
  max-width: 600px;
  line-height: 1.6;
}

.right-card {
  width: 450px;
  z-index: 1;
  background-color: white;
  box-shadow: -10px 0 30px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}
</style>
