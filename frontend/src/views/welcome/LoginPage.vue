<template>
  <div class="login-container">
    <div class="login-header">
      <h2 class="login-title">登录</h2>
      <p class="login-subtitle">在进入系统之前请先输入用户名和密码进行登录</p>
    </div>

    <div class="login-form-wrapper">
      <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
        <el-form-item prop="username" class="form-item">
          <el-input
            v-model="form.username"
            maxlength="20"
            type="text"
            placeholder="用户名/邮箱"
            class="custom-input"
          >
            <template #prefix>
              <el-icon class="input-icon">
                <User />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password" class="form-item">
          <el-input
            v-model="form.password"
            type="password"
            maxlength="20"
            placeholder="密码"
            class="custom-input"
          >
            <template #prefix>
              <el-icon class="input-icon">
                <Lock />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <div class="form-actions">
          <el-form-item prop="remember" class="remember-me">
            <el-checkbox v-model="form.remember" label="记住我" />
          </el-form-item>

          <el-link @click="router.push('/forget')" class="forget-password">
            忘记密码？
          </el-link>
        </div>
      </el-form>
    </div>

    <div class="login-button-wrapper">
      <el-button
        @click="userLogin()"
        class="login-button"
        type="primary"
        size="large"
      >
        立即登录
      </el-button>
    </div>

    <div class="register-section">
      <el-divider class="custom-divider">
        <span class="divider-text">没有账号</span>
      </el-divider>
      <el-button
        @click="router.push('/register')"
        class="register-button"
        type="default"
        size="large">
        注册账号
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { User, Lock } from "@element-plus/icons-vue";
import router from "@/router";
import { inject, reactive, ref } from "vue";
import { login } from "@/net";
import { apiUserInfo } from "@/net/api/user";

const formRef = ref();
const form = reactive({
  username: "",
  password: "",
  remember: false,
});

const rules = {
  username: [{ required: true, message: "请输入用户名" }],
  password: [{ required: true, message: "请输入密码" }],
};

const loading = inject("userLoading");

function userLogin() {
  formRef.value.validate((isValid) => {
    if (isValid) {
      login(form.username, form.password, form.remember, () => {
        apiUserInfo(loading);
        router.push("/index");
      });
    }
  });
}
</script>

<style scoped>
.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 40px 40px;
  height: 100%;
  box-sizing: border-box;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  color: #1d2129;
  margin: 0 0 8px 0;
}

.login-subtitle {
  font-size: 14px;
  color: #86909c;
  margin: 0;
  line-height: 1.5;
}

.login-form-wrapper {
  width: 100%;
  margin-bottom: 24px;
}

.login-form {
  width: 100%;
}

.form-item {
  margin-bottom: 20px;
}

.custom-input {
  height: 48px;
  border-radius: 8px;
  border: 1px solid #d9d9d9;
  font-size: 14px;
  transition: all 0.3s ease;
}

.custom-input:focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.input-icon {
  color: #86909c;
  font-size: 16px;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.remember-me {
  margin: 0;
}

.remember-me .el-checkbox__label {
  font-size: 14px;
  color: #4e5969;
}

.forget-password {
  font-size: 14px;
  color: #409eff;
}

.forget-password:hover {
  color: #66b1ff;
}

.login-button-wrapper {
  width: 100%;
  margin-bottom: 32px;
}

.login-button {
  width: 100%;
  height: 48px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  background-color: #409eff;
  border: none;
  transition: all 0.3s ease;
}

.login-button:hover {
  background-color: #66b1ff;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.register-section {
  width: 100%;
}

.custom-divider {
  margin: 0 0 24px 0;
}

.divider-text {
  color: #86909c;
  font-size: 13px;
  padding: 0 16px;
}

.register-button {
  width: 100%;
  height: 48px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  border: 1px solid #d9d9d9;
  transition: all 0.3s ease;
}

.register-button:hover {
  border-color: #409eff;
  color: #409eff;
  background-color: rgba(64, 158, 255, 0.04);
}
</style>
