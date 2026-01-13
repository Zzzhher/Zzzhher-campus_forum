<template>
  <div class="register-container">
    <div class="register-header">
      <h2 class="register-title">注册新用户</h2>
      <p class="register-subtitle">
        欢迎注册我们的交流社区，请在下方填写相关信息
      </p>
    </div>

    <div class="register-form-wrapper">
      <el-form
        :model="form"
        :rules="rules"
        @validate="onValidate"
        ref="formRef"
        class="register-form"
      >
        <el-form-item prop="username" class="form-item">
          <el-input
            v-model="form.username"
            :maxlength="8"
            type="text"
            placeholder="用户名"
            class="custom-input"
          >
            <template #prefix>
              <el-icon class="input-icon"><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password" class="form-item">
          <el-input
            v-model="form.password"
            :maxlength="16"
            type="password"
            placeholder="密码"
            class="custom-input"
          >
            <template #prefix>
              <el-icon class="input-icon"><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password_repeat" class="form-item">
          <el-input
            v-model="form.password_repeat"
            :maxlength="16"
            type="password"
            placeholder="重复密码"
            class="custom-input"
          >
            <template #prefix>
              <el-icon class="input-icon"><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="email" class="form-item">
          <el-input
            v-model="form.email"
            type="email"
            placeholder="电子邮件地址"
            class="custom-input"
          >
            <template #prefix>
              <el-icon class="input-icon"><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="code" class="form-item">
              <el-row :gutter="12" style="width: 100%">
                <el-col :span="14">
                  <el-input
                    v-model="form.code"
                    :maxlength="6"
                    type="text"
                    placeholder="请输入验证码"
                    class="custom-input"
                  >
                    <template #prefix>
                      <el-icon class="input-icon"><EditPen /></el-icon>
                    </template>
                  </el-input>
                </el-col>
                <el-col :span="8">
                  <el-button
                    type="success"
                    @click="validateEmail"
                    :disabled="!isEmailValid || coldTime > 0"
                    class="code-button"
                    size="medium"
                  >
                    {{ coldTime > 0 ? "请稍后 " + coldTime + " 秒" : "获取验证码" }}
                  </el-button>
                </el-col>
              </el-row>
            </el-form-item>
      </el-form>
    </div>

    <div class="register-button-wrapper">
      <el-button
        style="width: 100%"
        type="primary"
        @click="register"
        plain
        class="register-button"
        size="large"
      >
        立即注册
      </el-button>
    </div>

    <div class="login-section">
      <span style="font-size: 14px; line-height: 1.5; color: #86909c"
        >已有账号?
      </span>
      <el-link type="primary" @click="router.push('/')" class="login-link">
        立即登录
      </el-link>
    </div>
  </div>
</template>

<script setup>
import { EditPen, Lock, Message, User } from "@element-plus/icons-vue";
import router from "@/router";
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { apiAuthAskCode, apiAuthRegister } from "@/net/api/user";

const form = reactive({
  username: "",
  password: "",
  password_repeat: "",
  email: "",
  code: "",
});

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
      apiAuthRegister({
        username: form.username,
        password: form.password,
        email: form.email,
        code: form.code,
      });
    } else {
      ElMessage.warning("请完整填写注册表单内容！");
    }
  })
}

const validateEmail = () => apiAuthAskCode(form.email, coldTime);
</script>

<style scoped>
.register-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px;
  height: 100%;
  box-sizing: border-box;
  overflow-y: auto;
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.register-title {
  font-size: 28px;
  font-weight: 700;
  color: #1d2129;
  margin: 0 0 8px 0;
}

.register-subtitle {
  font-size: 14px;
  color: #86909c;
  margin: 0;
  line-height: 1.5;
}

.register-form-wrapper {
  width: 100%;
  margin-bottom: 24px;
}

.register-form {
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

.code-button {
  height: 48px;
  width: 100%;
  font-size: 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.code-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.register-button-wrapper {
  width: 100%;
  margin-bottom: 24px;
}

.register-button {
  width: 100%;
  height: 48px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  background-color: #409eff;
  border: none;
  transition: all 0.3s ease;
}

.register-button:hover {
  background-color: #66b1ff;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.login-section {
  text-align: center;
  margin-top: 16px;
}

.login-link {
  font-size: 14px;
  color: #409eff;
  margin-left: 4px;
}

.login-link:hover {
  color: #66b1ff;
}
</style>
