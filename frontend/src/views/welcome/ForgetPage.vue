<template>
  <div class="forget-container">
    <div class="forget-steps">
      <el-steps :active="active" finish-status="success" align-center>
        <el-step title="验证电子邮件" />
        <el-step title="重新设定密码" />
      </el-steps>
    </div>

    <!-- Step 1: Verify Email -->
    <transition name="el-fade-in-linear" mode="out-in">
      <div class="forget-step" v-if="active === 0">
        <div class="forget-header">
          <div class="forget-title">重置密码</div>
          <div class="forget-subtitle">请输入需要重置密码的电子邮件地址</div>
        </div>

        <el-form
          :model="form"
          :rules="rules"
          @validate="onValidate"
          ref="formRef"
        >
          <el-form-item prop="email">
            <el-input
              v-model="form.email"
              type="email"
              placeholder="电子邮件地址"
              :class="{ 'custom-input': true, 'is-focused': isEmailFocused }"
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

        <div class="forget-actions">
          <el-button @click="confirmReset()" class="primary-button">
            开始重置密码
          </el-button>
        </div>
      </div>
    </transition>

    <!-- Step 2: Reset Password -->
    <transition name="el-fade-in-linear" mode="out-in">
      <div class="forget-step" v-if="active === 1">
        <div class="forget-header">
          <div class="forget-title">重置密码</div>
          <div class="forget-subtitle">
            请填写您的新密码，务必牢记，防止丢失
          </div>
        </div>

        <el-form
          :model="form"
          :rules="rules"
          @validate="onValidate"
          ref="formRef"
        >
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              :maxlength="16"
              type="password"
              placeholder="新密码"
              :class="{ 'custom-input': true, 'is-focused': isPasswordFocused }"
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
              placeholder="重复新密码"
              :class="{
                'custom-input': true,
                'is-focused': isPasswordRepeatFocused,
              }"
              @focus="isPasswordRepeatFocused = true"
              @blur="isPasswordRepeatFocused = false"
            >
              <template #prefix>
                <el-icon class="input-icon"><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
        </el-form>

        <div class="forget-actions">
          <el-button @click="doReset()" class="primary-button">
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
import { get, post } from "@/net";
import { ElMessage } from "element-plus";
import router from "@/router";

const active = ref(0);

const form = reactive({
  email: "",
  code: "",
  password: "",
  password_repeat: "",
});

// Added input focus state variables
const isEmailFocused = ref(false);
const isCodeFocused = ref(false);
const isPasswordFocused = ref(false);
const isPasswordRepeatFocused = ref(false);

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

const validateEmail = () => {
  coldTime.value = 60;
  get(
    `/api/auth/ask-code?email=${form.email}&type=reset`,
    () => {
      ElMessage.success(`验证码已发送到邮箱: ${form.email}，请注意查收`);
      const handle = setInterval(() => {
        coldTime.value--;
        if (coldTime.value === 0) {
          clearInterval(handle);
        }
      }, 1000);
    },
    (message) => {
      ElMessage.warning(message);
      coldTime.value = 0;
    }
  );
};

const confirmReset = () => {
  formRef.value.validate((isValid) => {
    if (isValid) {
      post(
        "/api/auth/reset-confirm",
        {
          email: form.email,
          code: form.code,
        },
        () => active.value++
      );
    }
  });
};

const doReset = () => {
  formRef.value.validate((isValid) => {
    if (isValid) {
      post(
        "/api/auth/reset-password",
        {
          email: form.email,
          code: form.code,
          password: form.password,
        },
        () => {
          ElMessage.success("密码重置成功，请重新登录");
          router.push("/");
        }
      );
    }
  });
};
</script>

<style scoped>
.forget-container {
  width: 100%;
  padding: 20px;
}

.forget-steps {
  margin: 30px 0;
}

.forget-step {
  text-align: center;
}

.forget-header {
  margin-bottom: 40px;
}

.forget-title {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin-bottom: 10px;
}

.forget-subtitle {
  font-size: 16px;
  color: #666;
}

/* Input styles */
.custom-input {
  height: 48px;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.3s ease;
  padding: 0 16px;
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
}

/* Code input container */
.code-input-container {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 20px;
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

/* Primary button */
.primary-button {
  width: 100%;
  height: 48px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  transition: all 0.3s ease;
  margin-top: 20px;
}

.primary-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* Responsive design */
@media (max-width: 768px) {
  .forget-container {
    padding: 15px;
  }

  .forget-title {
    font-size: 28px;
  }

  .forget-subtitle {
    font-size: 14px;
  }

  .custom-input,
  .get-code-button,
  .primary-button {
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
