<template>
  <div class="register-container">
    <div class="register-header">
      <h1 class="register-title">注册账号</h1>
      <p class="register-subtitle">创建您的校园论坛账号，开启学习之旅</p>
    </div>

    <div class="register-form">
      <el-form
        :model="form"
        :rules="rules"
        @validate="onValidate"
        ref="formRef"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            :maxlength="8"
            type="text"
            placeholder="用户名"
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
            :maxlength="16"
            type="password"
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

        <el-form-item prop="password_repeat">
          <el-input
            v-model="form.password_repeat"
            :maxlength="16"
            type="password"
            placeholder="重复密码"
            :class="['custom-input', { 'is-focused': isPasswordRepeatFocused }]"
            @focus="isPasswordRepeatFocused = true"
            @blur="isPasswordRepeatFocused = false"
          >
            <template #prefix>
              <el-icon class="input-icon"><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="email">
          <el-input
            v-model="form.email"
            type="email"
            placeholder="电子邮件地址"
            :class="['custom-input', { 'is-focused': isEmailFocused }]"
            @focus="isEmailFocused = true"
            @blur="isEmailFocused = false"
          >
            <template #prefix>
              <el-icon class="input-icon"><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="code">
          <div class="code-input-container">
            <el-input
              v-model="form.code"
              :maxlength="6"
              type="text"
              placeholder="请输入验证码"
              :class="[
                'custom-input',
                'code-input',
                { 'is-focused': isCodeFocused },
              ]"
              @focus="isCodeFocused = true"
              @blur="isCodeFocused = false"
            >
              <template #prefix>
                <el-icon class="input-icon"><EditPen /></el-icon>
              </template>
            </el-input>
            <el-button
              class="get-code-button"
              type="primary"
              :disabled="!isEmailValid || coldTime > 0"
              @click="validateEmail"
            >
              {{ coldTime > 0 ? "请稍后 " + coldTime + " 秒" : "获取验证码" }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>

    <div class="register-actions">
      <el-button @click="register" class="register-button" type="primary"
        >立即注册</el-button
      >
    </div>

    <div class="register-footer">
      <span class="login-text">已有账号? </span>
      <el-link class="login-link" @click="router.push('/')">立即登录</el-link>
    </div>
  </div>
</template>

<script setup>
import { EditPen, Lock, Message, User } from "@element-plus/icons-vue";
import router from "@/router";
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { get, post } from "@/net";

const form = reactive({
  username: "",
  password: "",
  password_repeat: "",
  email: "",
  code: "",
});

// 输入框焦点状态
const isUsernameFocused = ref(false);
const isPasswordFocused = ref(false);
const isPasswordRepeatFocused = ref(false);
const isEmailFocused = ref(false);
const isCodeFocused = ref(false);

const validateUsername = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("请输入用户名"));
  } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
    callback(new Error("用户名不能包含特殊字符，只能是中文/英文"));
  } else {
    callback();
  }
};

const validatePassword = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("请再次输入密码"));
  } else if (value !== form.password) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

const rules = {
  username: [
    { validator: validateUsername, trigger: ["blur", "change"] },
    {
      min: 2,
      max: 8,
      message: "用户名的长度必须在2-8个字符之间",
      trigger: ["blur", "change"],
    },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    {
      min: 6,
      max: 16,
      message: "密码的长度必须在6-16个字符之间",
      trigger: ["blur", "change"],
    },
  ],
  password_repeat: [
    { validator: validatePassword, trigger: ["blur", "change"] },
  ],
  email: [
    { required: true, message: "请输入邮件地址", trigger: "blur" },
    {
      type: "email",
      message: "请输入合法的电子邮件地址",
      trigger: ["blur", "change"],
    },
  ],
  code: [{ required: true, message: "请输入获取的验证码", trigger: "blur" }],
};

const formRef = ref();
const isEmailValid = ref(false);
const coldTime = ref(0);

const onValidate = (prop, isValid) => {
  if (prop === "email") isEmailValid.value = isValid;
};

const register = () => {
  formRef.value.validate((isValid) => {
    if (isValid) {
      post(
        "/api/auth/register",
        {
          username: form.username,
          password: form.password,
          email: form.email,
          code: form.code,
        },
        () => {
          ElMessage.success("注册成功，欢迎加入我们");
          router.push("/");
        }
      );
    } else {
      ElMessage.warning("请完整填写注册表单内容！");
    }
  });
};

const validateEmail = () => {
  coldTime.value = 60;
  get(
    `/api/auth/ask-code?email=${form.email}&type=register`,
    () => {
      ElMessage.success(`验证码已发送到邮箱: ${form.email}，请注意查收`);
      const handle = setInterval(() => {
        coldTime.value--;
        if (coldTime.value === 0) {
          clearInterval(handle);
        }
      }, 1000);
    },
    undefined,
    (message) => {
      ElMessage.warning(message);
      coldTime.value = 0;
    }
  );
};
</script>

<style scoped>
.register-container {
  width: 100%;
}

.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.register-title {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.register-subtitle {
  font-size: 16px;
  color: #666;
  margin: 0;
}

.register-form {
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

.code-input-container {
  display: flex;
  gap: 12px;
  align-items: center;
}

.code-input {
  flex: 1;
}

.get-code-button {
  min-width: 120px;
  height: 48px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

.get-code-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.get-code-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.register-actions {
  margin-bottom: 30px;
}

.register-button {
  width: 100%;
  height: 48px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

.register-footer {
  text-align: center;
}

.login-text {
  color: #666;
  font-size: 14px;
}

.login-link {
  color: #667eea;
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
}

.login-link:hover {
  color: #764ba2;
}
/* Responsive design */
@media (max-width: 768px) {
  .register-header {
    margin-bottom: 30px;
  }

  .register-title {
    font-size: 28px;
  }

  .register-subtitle {
    font-size: 14px;
  }

  .custom-input,
  .register-button,
  .get-code-button {
    height: 44px;
    font-size: 14px;
  }

  .code-input-container {
    flex-direction: column;
  }

  .code-input,
  .get-code-button {
    width: 100%;
  }
}
</style>
