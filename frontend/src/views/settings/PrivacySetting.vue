<script setup>
import Card from "@/components/Card.vue";
import { Setting } from "@element-plus/icons-vue";
</script>

<template>
  <div style="margin: auto;max-width: 600px">
    <div style="margin-top: 20px">
      <card :icon="Setting" title="隐私设置" desc="在这里设置哪些内容可以被其他人看到，请各位小伙伴注重自己的隐私~">
        <div class="checkbox-list">
          <el-checkbox>公开展示我的手机号</el-checkbox>
          <el-checkbox>公开展示我的电子邮件地址</el-checkbox>
          <el-checkbox>公开展示我的微信号</el-checkbox>
          <el-checkbox>公开展示我的QQ号</el-checkbox>
          <el-checkbox>公开展示我的性别</el-checkbox>
        </div>
      </card>
      <card style="margin: 20px 0">

      </card>
    </div>
  </div>
</template>

<style scoped>
.checkbox-list {
  margin: 0 0;
}
</style>