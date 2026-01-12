<template>
  <div class="forget-container">
    <div class="steps-wrapper">
      <el-steps
        :active="active"
        finish-status="success"
        align-center
        class="custom-steps"
      >
        <el-step title="验证电子邮件" class="step-item" />
        <el-step title="重新设定密码" class="step-item" />
      </el-steps>
    </div>

    <transition name="el-fade-in-linear" mode="out-in">
      <div v-if="active === 0" class="step-content">
        <div class="step-header">
          <h2 class="step-title">重置密码</h2>
          <p class="step-subtitle">请输入需要重置密码的电子邮件地址</p>
        </div>

        <div class="form-wrapper">
          <el-form
            :model="form"
            :rules="rules"
            @validate="onValidate"
            ref="formRef"
            class="forget-form"
          >
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
                    {{
                      coldTime > 0 ? "请稍后 " + coldTime + " 秒" : "获取验证码"
                    }}
                  </el-button>
                </el-col>
              </el-row>
            </el-form-item>
          </el-form>
        </div>

        <div class="button-wrapper">
          <el-button
            @click="confirmReset()"
            style="width: 100%"
            type="primary"
            class="step-button"
            size="large"
          >
            开始重置密码
          </el-button>
        </div>
      </div>
    </transition>

    <transition name="el-fade-in-linear" mode="out-in">
      <div v-if="active === 1" class="step-content">
        <div class="step-header">
          <h2 class="step-title">重置密码</h2>
          <p class="step-subtitle">请填写您的新密码，务必牢记，防止丢失</p>
        </div>

        <div class="form-wrapper">
          <el-form
            :model="form"
            :rules="rules"
            @validate="onValidate"
            ref="formRef"
            class="forget-form"
          >
            <el-form-item prop="password" class="form-item">
              <el-input
                v-model="form.password"
                :maxlength="16"
                type="password"
                placeholder="新密码"
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
                placeholder="重复新密码"
                class="custom-input"
              >
                <template #prefix>
                  <el-icon class="input-icon"><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-form>
        </div>

        <div class="button-wrapper">
          <el-button
            @click="doReset()"
            style="width: 100%"
            type="primary"
            class="step-button"
            size="large"
          >
            立即重置密码
          </el-button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { EditPen, Lock, Message } from "@element-plus/icons-vue";
import {
  apiAuthAskCode,
  apiAuthResetPassword,
  apiAuthRestConfirm,
} from "@/net/api/user";

const active = ref(0);

const form = reactive({
  email: "",
  code: "",
  password: "",
  password_repeat: "",
});

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
  email: [
    { required: true, message: "请输入邮件地址", trigger: "blur" },
    {
      type: "email",
      message: "请输入合法的电子邮件地址",
      trigger: ["blur", "change"],
    },
  ],
  code: [{ required: true, message: "请输入获取的验证码", trigger: "blur" }],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    {
      min: 6,
      max: 16,
      message: "密码的长度必须在6-16个字符之间",
      trigger: ["blur"],
    },
  ],
  password_repeat: [
    { validator: validatePassword, trigger: ["blur", "change"] },
  ],
};

const formRef = ref();
const isEmailValid = ref(false);
const coldTime = ref(0);

const onValidate = (prop, isValid) => {
  if (prop === "email") isEmailValid.value = isValid;
};

const validateEmail = () => apiAuthAskCode(form.email, coldTime, "reset");

const confirmReset = () => {
  formRef.value.validate((isValid) => {
    if (isValid) {
      apiAuthRestConfirm(
        {
          email: form.email,
          code: form.code,
        },
        active
      );
    }
  });
};

const doReset = () => {
  formRef.value.validate((isValid) => {
    if (isValid) {
      apiAuthResetPassword({
        email: form.email,
        code: form.code,
        password: form.password,
      });
    }
  });
};
</script>

<style scoped>
.forget-container {
  display: flex;
  flex-direction: column;
  padding: 40px;
  height: 100%;
  box-sizing: border-box;
  overflow-y: auto;
}

.steps-wrapper {
  margin-bottom: 32px;
}

.custom-steps {
  --el-step-line-color: #e8e8e8;
  --el-step-finish-line-color: #409eff;
  --el-step-finish-color: #409eff;
}

.step-item {
  font-size: 14px;
}

.step-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}

.step-header {
  text-align: center;
  margin-bottom: 32px;
  width: 100%;
}

.step-title {
  font-size: 28px;
  font-weight: 700;
  color: #1d2129;
  margin: 0 0 8px 0;
}

.step-subtitle {
  font-size: 14px;
  color: #86909c;
  margin: 0;
  line-height: 1.5;
}

.form-wrapper {
  width: 100%;
  margin-bottom: 24px;
}

.forget-form {
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

.button-wrapper {
  width: 100%;
  margin-bottom: 24px;
}

.step-button {
  width: 100%;
  height: 48px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  background-color: #409eff;
  border: none;
  transition: all 0.3s ease;
}

.step-button:hover {
  background-color: #66b1ff;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}
</style>
