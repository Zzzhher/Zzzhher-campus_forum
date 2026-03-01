<script setup>

import "@vueup/vue-quill/dist/vue-quill.snow.css";
import { Delta, Quill, QuillEditor } from "@vueup/vue-quill";

import ImageResize from "quill-image-resize-vue";
import { ImageExtend, QuillWatch } from "quill-image-super-solution-module";

import { accessHeader } from "@/net";
import axios from "axios";
import ColorDot from "@/components/ColorDot.vue";
import { userStore } from "@/store";
import { apiForumTopicCreate } from "@/net/api/forum";
import { ElMessage } from "element-plus";
import { Check, Document, Edit } from "@element-plus/icons-vue";
import {computed, reactive, ref} from "vue";
import { ElMessageBox } from "element-plus";

const props = defineProps({
  show: Boolean,
  defaultTitle: {
    default: "",
    type: String,
  },
  defaultText: {
    default: "",
    type: String,
  },
  defaultType: {
    default: null,
    type: Number,
  },
  submitButton: {
    default: "立即发布",
    type: String,
  },
  submit: {
    default: (editor, success) => {
      apiForumTopicCreate(
        {
          type: editor.type.id,
          title: editor.title,
          content: editor.text,
        },
        (response) => {
          // 后端返回null表示成功，返回字符串表示有提示信息
          if (response === null) {
            ElMessage.success("帖子发表成功！");
          } else if (response.includes("等待审核")) {
            ElMessage.warning(response);
          } else {
            ElMessage.error(response);
            return; // 失败时不关闭编辑框
          }
          success(); // 无论是否需要审核，都关闭编辑框
        }
      );
    },
    type: Function,
  },
});
const store = userStore();
const emit = defineEmits(["close", "success"]);

const refEditor = ref();
const editor = reactive({
  type: null,
  title: "",
  text: new Delta(),
  loading: false,
});

function initEditor() {
    if (props.defaultText) editor.text = new Delta(JSON.parse(props.defaultText));
    else refEditor.value.setContents(new Delta(), "user");
    editor.title = props.defaultTitle;
    editor.type = findTypeById(props.defaultType);
    // 检查是否有草稿
    if (!props.defaultText && !props.defaultTitle && !props.defaultType) {
      loadDraft();
    }
}

function findTypeById(id) {
  for (let type of store.forum.types) {
    if (type.id === id) return type;
  }
}

function deltaToText(delta) {
  if (!delta || !Array.isArray(delta.ops)) return "";
  let str = "";
  for (let op of delta.ops) {
    if (op.insert && typeof op.insert === "string") {
      str += op.insert;
    }
  }
  // 移除 Quill 自动添加的末尾换行符
  if (str.endsWith("\n")) {
    str = str.slice(0, -1);
  }
  return str.replace(/\s/g, "");
}

const contentLength = computed(() => deltaToText(editor.text).length);

function hasContent() {
  return editor.title.trim() || deltaToText(editor.text).length > 0 || editor.type;
}

function saveDraft() {
  const draft = {
    title: editor.title,
    text: JSON.stringify(editor.text),
    typeId: editor.type?.id || null,
    savedAt: new Date().toISOString()
  };
  localStorage.setItem("topicDraft", JSON.stringify(draft));
  ElMessage.success("草稿已保存");
}

function loadDraft() {
  const draftStr = localStorage.getItem("topicDraft");
  if (draftStr) {
    try {
      const draft = JSON.parse(draftStr);
      if (draft.title || draft.text || draft.typeId) {
        ElMessageBox.confirm(
          "检测到未完成的草稿，是否恢复？",
          "恢复草稿",
          {
            confirmButtonText: "恢复",
            cancelButtonText: "放弃",
            type: "info",
          }
        )
          .then(() => {
            if (draft.text) editor.text = new Delta(JSON.parse(draft.text));
            editor.title = draft.title || "";
            if (draft.typeId) editor.type = findTypeById(draft.typeId);
            ElMessage.success("草稿已恢复");
          })
          .catch(() => {
            // 放弃草稿
            localStorage.removeItem("topicDraft");
          });
      }
    } catch (e) {
      console.error("解析草稿失败:", e);
      localStorage.removeItem("topicDraft");
    }
  }
}

function handleClose() {
  if (hasContent()) {
    ElMessageBox.confirm(
      "是否保存草稿？",
      "确认操作",
      {
        confirmButtonText: "保存草稿",
        cancelButtonText: "直接关闭",
        type: "warning",
      }
    )
      .then(() => {
        saveDraft();
        emit("close");
      })
      .catch(() => {
        emit("close");
      });
  } else {
    emit("close");
  }
}

function submitTopic() {
  const text = deltaToText(editor.text);
  if (text.length > 20000) {
    ElMessage.warning("字数超出限制，无法发布主题！");
    return;
  }
  if (!editor.title) {
    ElMessage.warning("请填写标题！");
    return;
  }
  if (!editor.type) {
    ElMessage.warning("请选择一个合适的帖子类型！");
    return;
  }
  props.submit(editor, () => {
    // 发布成功后清除草稿
    localStorage.removeItem("topicDraft");
    emit("success");
  });
}
Quill.register("modules/imageResize", ImageResize);
Quill.register("modules/ImageExtend", ImageExtend);

const editorOption = {
  modules: {
    toolbar: {
      container: [
        "bold",
        "italic",
        "underline",
        "strike",
        "clean",
        { color: [] },
        { background: [] },
        { size: ["small", false, "large", "huge"] },
        { header: [1, 2, 3, 4, 5, 6, false] },
        { list: "ordered" },
        { list: "bullet" },
        { align: [] },
        "blockquote",
        "code-block",
        "link",
        "image",
        { indent: "-1" },
        { indent: "+1" },
      ],
      handlers: {
        image: function () {
          QuillWatch.emit(this.quill.id);
        },
      },
    },
    imageResize: {
      modules: ["Resize", "DisplaySize"],
    },
    ImageExtend: {
      action: axios.defaults.baseURL + "/api/image/cache",
      name: "file",
      size: 5,
      loading: true,
      accept: "image/png, image/jpeg",
      response: (resp) => {
        if (resp.data) {
          return axios.defaults.baseURL + "/images" + resp.data;
        } else {
          return null;
        }
      },
      methods: "POST",
      headers: (xhr) => {
        xhr.setRequestHeader("Authorization", accessHeader().Authorization);
      },
      start: () => (editor.uploading = true),
      success: () => {
        ElMessage.success("图片上传成功!");
        editor.uploading = false;
      },
      error: () => {
        ElMessage.warning("图片上传失败，请联系管理员!");
        editor.uploading = false;
      },
    },
  },
};
</script>
<template>
  <div class="topic-editor-wrapper">
    <el-drawer
      :model-value="show"
      direction="btt"
      @open="initEditor"
      :close-on-click-modal="false"
      :size="650"
      @close="handleClose"
    >
      <template #header>
        <div>
          <div style="font-weight: bold">发表新帖子</div>
          <div style="font-size: 13px">
            发表内容之前，请遵守相关法律法规，不要出现不文明行为。
          </div>
        </div>
      </template>
      <div style="display: flex; gap: 10px">
        <div style="width: 150px">
          <el-select
            placeholder="选择主题类型..."
            value-key="id"
            v-model="editor.type"
            :disabled="!store.forum.types.length"
          >
            <el-option
              v-for="item in store.forum.types.filter((type) => type.id > 0)"
              :value="item"
              :label="item.name"
            >
              <div>
                <color-dot :color="item.color" />
                <span style="margin-left: 10px">{{ item.name }}</span>
              </div>
            </el-option>
          </el-select>
        </div>
        <div style="flex: 1">
          <el-input
            v-model="editor.title"
            placeholder="请输入帖子标题..."
            :prefix-icon="Document"
            style="height: 100%"
            maxlength="30"
          />
        </div>
      </div>
      <div style="margin-top: 5px; font-size: 13px; color: grey">
        <color-dot :color="editor.type ? editor.type.color : '#dedede'" />
        <span style="margin-left: 5px">{{
          editor.type ? editor.type.desc : "请在上方选择一个帖子类型"
        }}</span>
      </div>
      <div
        style="
          margin-top: 10px;
          height: 440px;
          overflow: hidden;
          border-radius: 5px;
        "
        v-loading="editor.uploading"
        element-loading-text="正在上传图片，请耐心等待..."
      >
        <quill-editor
          v-model:content="editor.text"
          style="height: calc(100% - 45px)"
          content-type="delta"
          ref="refEditor"
          placeholder="分享你此刻的想法..."
          :options="editorOption"
        />
      </div>
      <div
        style="display: flex; justify-content: space-between; margin-top: 5px"
      >
        <div style="color: grey; font-size: 13px">
          当前字数 {{ contentLength }}（最大支持20000字）
        </div>
          <el-button type="success" :icon="Check" @click="submitTopic" plain>{{
            submitButton
          }}</el-button>
      </div>
    </el-drawer>
  </div>
</template>

<style scoped>
.topic-editor-wrapper :deep(.el-drawer) {
  width: 800px;
  margin: auto;
  border-radius: 10px 10px 10px 10px;
  text-align: left;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-10px);
}

.topic-editor-wrapper :deep(.el-drawer__header) {
  margin: 0;
}
</style>
