<script setup>
import {
  Bell,
  ChatDotSquare,
  Collection, DataLine,
  Document,
  Files,
  Location,
  Lock, Message, Monitor,
  Notification,
  Operation,
  Position,
  School,
  Umbrella,
  User,
} from "@element-plus/icons-vue";
import UserInfo from "@/components/UserInfo.vue";
import {get} from "@/net";
import {userStore} from "@/store";
import {inject, ref} from "vue";
const adminMenu = [
  {
    title: '校园论坛管理', icon: Location, sub: [
      { title: '用户管理', icon: User, index: '/admin/user' },
      { title: '邮件管理', icon: Message, index: '/admin/email' },
      { title: '帖子广场管理', icon: ChatDotSquare, index: '/admin/forum' },
      { title: '校园活动管理', icon: Notification },
      { title: '失物招领管理', icon: Bell },
      { title: '表白墙管理', icon: Umbrella },
      { title: '合作机构管理', icon: School }
    ]
  }, {
    title: '探索与发现管理', icon: Position, sub: [
      { title: '成绩管理', icon: Document },
      { title: '课程表管理', icon: Files },
      { title: '在线图书馆管理', icon: Collection },
      { title: '合作机构管理', icon: School }
    ]
  }
]

const loading = inject('userLoading')
</script>
<template>
  <div class="admin-content" v-loading="loading" element-loading-text="正在进入，请耐心等待...">
    <el-container style="height: 100%">
      <el-aside width="230px" class="admin-content-aside">
        <div class="logo-box">
          <el-image
            class="logo"
            src="https://element-plus.org/images/element-plus-logo.svg"
          />
        </div>
        <el-scrollbar style="min-height: calc(100vh - 57px)">
          <el-menu
            router
            :default-active="$route.path"
            :default-openeds="['1', '2']"
            style="min-height: calc(100vh - 57px);border: none"
          >
            <el-sub-menu :index="(index + 1).toString()"
                         v-for="(menu, index) in adminMenu">
              <template #title>
                <el-icon>
                  <component :is="menu.icon"/>
                </el-icon>
                <span><b>{{ menu.title }}</b></span>
              </template>
              <el-menu-item :index="subMenu.index" v-for="subMenu in menu.sub">
                <template #title>
                  <el-icon>
                    <component :is="subMenu.icon"/>
                  </el-icon>
                  {{ subMenu.title }}
                </template>
              </el-menu-item>
            </el-sub-menu>
          </el-menu>
        </el-scrollbar>
      </el-aside>
      <el-container>
        <el-header class="admin-content-header">
          <div style="flex: 1"></div>
          <user-info/>
        </el-header>
        <el-main>Main</el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style scoped>
.admin-content {
  height: 100vh;
  width: 100vw;
  .admin-content-aside {
    border-right: solid 1px var(--el-border-color);

    .logo-box {
      text-align: center;
      padding: 15px 0 10px;
      height: 32px;

      .logo {
        height: 32px;
      }
    }
  }
  .admin-content-header {
    border-bottom: solid 1px var(--el-border-color);
    height: 55px;
    display: flex;
    align-items: center;
    box-sizing: border-box;

    :deep(.el-tabs__header) {
      height: 32px;
      margin-bottom: 0;
      border-bottom: none;
    }

    :deep(.el-tabs__nav) {
      gap: 10px;
      border: none;
    }

    :deep(.el-tabs__item) {
      height: 32px;
      padding: 0 15px;
      border-radius: 6px;
      border: solid 1px var(--el-border-color);
    }
  }
}
</style>
