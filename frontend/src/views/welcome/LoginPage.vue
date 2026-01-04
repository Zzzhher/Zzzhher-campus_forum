<template>
  <div class="login-container">
    <div class="login-header">
      <h1 class="login-title">登录</h1>
      <p class="login-subtitle">欢迎回来，请登录您的账号</p>
    </div>

    <div class="login-form">
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            maxlength="20"
            type="text"
            placeholder="用户名/邮箱"
            :class="['custom-input', { 'is-focused': isUsernameFocused }]"
            @focus="isUsernameFocused = true"
            @blur="isUsernameFocused = false"
          >
            <template #prefix>
              <el-icon class="input-icon"><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            maxlength="20"
            placeholder="密码"
            :class="['custom-input', { 'is-focused': isPasswordFocused }]"
            @focus="isPasswordFocused = true"
            @blur="isPasswordFocused = false"
          >
            <template #prefix>
              <el-icon class="input-icon"><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <div class="login-options">
          <el-form-item prop="remember" class="remember-me">
            <el-checkbox v-model="form.remember" label="记住我" />
          </el-form-item>
          <el-link class="forgot-password" @click="router.push('/forget')"
            >忘记密码？</el-link
          >
        </div>
      </el-form>
    </div>

    <div class="login-actions">
      <el-button @click="userLogin()" class="login-button" type="primary"
        >立即登录</el-button
      >
    </div>

    <div class="login-footer">
      <span class="register-text">没有账号？</span>
      <el-link class="register-link" @click="router.push('/register')"
        >注册账号</el-link
      >
    </div>
  </div>
</template>

<script setup>
import { User, Lock } from "@element-plus/icons-vue";
import router from "@/router";
import { reactive, ref } from "vue";
import { login } from "@/net";

const formRef = ref();
const form = reactive({
  username: "",
  password: "",
  remember: false,
});

// 输入框焦点状态
const isUsernameFocused = ref(false);
const isPasswordFocused = ref(false);

const rules = {
  username: [{ required: true, message: "请输入用户名" }],
  password: [{ required: true, message: "请输入密码" }],
};

function userLogin() {
  formRef.value.validate((isValid) => {
    if (isValid) {
      login(form.username, form.password, form.remember, () =>
        router.push("/index")
      );
    }
  });
}
</script>

<style scoped>
.login-container {
  width: 100%;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-title {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.login-subtitle {
  font-size: 16px;
  color: #666;
  margin: 0;
}

.login-form {
  margin-bottom: 30px;
}

.custom-input {
  width: 100%;
  height: 48px;
  border-radius: 8px;
  padding: 0 16px;
  font-size: 16px;
  transition: all 0.3s ease;
  background-color: #fafafa;
}

.custom-input:hover {
  border-color: #667eea;
  background-color: white;
}

.custom-input.is-focused {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
  background-color: white;
}

.input-icon {
  color: #999;
  font-size: 18px;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.remember-me {
  margin: 0;
}

.forgot-password {
  color: #667eea;
  font-size: 14px;
  text-decoration: none;
}

.forgot-password:hover {
  color: #764ba2;
}

.login-actions {
  margin-bottom: 30px;
}

.login-button {
  width: 100%;
  height: 48px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

.login-footer {
  text-align: center;
}

.register-text {
  color: #666;
  font-size: 14px;
}

.register-link {
  color: #667eea;
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
}

.register-link:hover {
  color: #764ba2;
}
/* Responsive design */
@media (max-width: 768px) {
  .login-header {
    margin-bottom: 30px;
  }

  .login-title {
    font-size: 28px;
  }

  .login-subtitle {
    font-size: 14px;
  }

  .custom-input,
  .login-button {
    height: 44px;
    font-size: 14px;
  }
}
</style>
