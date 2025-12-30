<template>
  <div class="welcome-container">
    <div class="welcome-left">
      <div class="welcome-content">
        <div class="welcome-logo">Campus Forum</div>
        <div class="welcome-slogan">连接你的学习世界</div>
      </div>
    </div>
    <div class="welcome-right">
      <div class="card-container">
        <router-view v-slot="{ Component }">
          <transition name="el-fade-in-linear" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup></script>

<style scoped>
.welcome-container {
  display: flex;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background-color: #f8f9fa;
}

.welcome-left {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 40px;
}

.welcome-content {
  max-width: 500px;
}

.welcome-logo {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 20px;
  letter-spacing: -1px;
}

.welcome-slogan {
  font-size: 24px;
  font-weight: 300;
  line-height: 1.6;
  opacity: 0.9;
}

.welcome-right {
  width: 450px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background-color: white;
}

.card-container {
  width: 100%;
  max-width: 360px;
}

@media (max-width: 768px) {
  .welcome-container {
    flex-direction: column;
  }

  .welcome-left {
    width: 100%;
    height: 300px;
  }

  .welcome-right {
    width: 100%;
    box-shadow: none;
  }
}
</style>
