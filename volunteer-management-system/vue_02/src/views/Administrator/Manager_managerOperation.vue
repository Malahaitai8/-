<template>
  <!-- 页面根容器 -->
  <div>
    <!-- 页眉部分，包含 logo、标题和欢迎信息 -->
    <header class="header">
      <!-- 显示 logo 图片，图片路径为根目录下的 logo.png -->
      <img src="/logo.png" alt="Logo" class="logo">
      <!-- 显示网页标题，即志愿管理系统 -->
      <h1 class="title">志愿管理系统</h1>
      <!-- 显示欢迎信息，欢迎管理员 -->
      <span class="welcome">欢迎您：管理员！</span>
    </header>
    <!-- 主体部分，用于展示投诉信息框 -->
    <main class="main-content">
      <h2>投诉信息</h2>
      <div class="complaint-container">
        <div class="form-container">
          <div v-for="(item, index) in formItems" :key="index" class="form-item">
            <label :for="item.id">
              <!-- 根据条件决定是否显示红色 * -->
              <span v-if="item.required" class="required">*</span>
              {{ item.label }}
            </label>
            <input :id="item.id" :type="item.type" :value="item.defaultValue" v-model="formData[item.id]">
          </div>
        </div>
        <div class="action-container">
          <button class="action-button" @click="goToHandlePage">去处理</button>
          <button class="action-button" @click="confirmProcessing">确认处理</button>
          <button class="action-button" @click="rejectComplaint">驳回</button>
<!--          <textarea
              class="reason-textarea"
              placeholder="请管理员阐述处理结果或驳回理由"
              v-model="reasonText"
          ></textarea>-->
          <button class="confirm-button">保存</button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import {useAdminStore} from "@/stores/adminStore.js"; // 假设使用 Element Plus 提示框，需要安装
//管理员信息传递
const adminStore = useAdminStore(); // 使用 Pinia store
adminStore.initializeStore();
const displayNameFromStore = computed(() => adminStore.displayName);
const isAuthenticated = computed(() => adminStore.isAuthenticated);
onMounted(() => {
  if (isAuthenticated.value && !adminStore.detailedAdminInfo.adminId) { // 简单判断详细信息是否已加载
  }
});


const router = useRouter();

const formItems = [
  { id: 'complaintId', label: '投诉ID', type: 'text', defaultValue: 'C001', required: true },
  { id: 'initiatorId', label: '发起人ID', type: 'text', defaultValue: 'U1001', required: true },
  { id: 'targetId', label: '投诉对象ID', type: 'text', defaultValue: 'U2001', required: true },
  { id: 'targetId', label: '对应志愿活动组织机构', type: 'text', defaultValue: 'U2001', required: false },
  { id: 'complaintTime', label: '投诉时间', type: 'datetime-local', defaultValue: '2024-07-01T12:00', required: true },
  { id: 'complaintType', label: '投诉类型', type: 'text', defaultValue: '服务质量问题', required: true },
  { id: 'complaintContent', label: '投诉内容', type: 'text', defaultValue: '志愿者服务态度不佳', required: true },
  { id: 'evidenceLink', label: '证据链接', type: 'text', defaultValue: 'https://example.com/evidence', required: true },
  { id: 'processingStatus', label: '处理状态', type: 'text', defaultValue: '待处理', required: true },
  { id: 'processingResult', label: '处理结果', type: 'text', defaultValue: '', required: false },
  { id: 'latestProcessingTime', label: '最新处理时间', type: 'datetime-local', defaultValue: '', required: false },
  { id: 'processorId', label: '处理人ID', type: 'text', defaultValue: '', required: false },
  { id: 'followUpTime', label: '回访时间', type: 'datetime-local', defaultValue: '', required: false },
  { id: 'followUpResult', label: '回访结果', type: 'text', defaultValue: '', required: false },
  { id: 'arbitrationRound', label: '仲裁轮次', type: 'number', defaultValue: 1, required: false },
  { id: 'reviewAdmin', label: '复审管理员', type: 'text', defaultValue: '', required: false },
];

const formData = ref({});
const reasonText = ref('');

formItems.forEach(item => {
  formData.value[item.id] = item.defaultValue;
});

const goToHandlePage = () => {
  router.push({ name: 'handle' });
};

const confirmProcessing = () => {
  ElMessage.success('处理成功');
  router.push({ name: 'complaint' });
};

const rejectComplaint = () => {
  ElMessage.success('处理成功');
  router.push({ name: 'complaint' });
};
</script>

<style scoped>
/* 页眉样式，设置背景颜色为红色，使用弹性布局使其内容垂直居中，添加内边距 */
.header {
  background-color: red;
  display: flex;
  align-items: center;
  padding: 10px 20px;
}

/* logo 图片样式，设置图片高度 */
.logo {
  height: 50px;
}

/* 标题样式，设置文字颜色为白色，加粗显示，并添加左右外边距 */
.title {
  color: white;
  font-weight: bold;
  margin: 0 30px;
}

/* 欢迎信息样式，设置文字颜色为白色，字体大小为 14px */
.welcome {
  color: white;
  font-size: 14px;
}

/* 主体内容样式，设置内边距并添加背景图 */
.main-content {
  padding: 250px;
  background-image: url('/bg.png');
  background-size: cover;
  background-position: center;
}

/* 投诉信息容器样式 */
.complaint-container {
  display: flex;
  gap: 30px;
}

/* 表单容器样式 */
.form-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  flex: 1;
}

/* 表单项样式 */
.form-item {
  display: flex;
  align-items: center;
}

/* 标签样式 */
label {
  width: 150px;
  text-align: right;
  margin-right: 10px;
}

/* 必填项标记样式 */
.required {
  color: red;
  margin-right: 5px;
}

/* 输入框样式 */
input {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  flex-grow: 1;
}

/* 操作按钮容器样式 */
.action-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 300px;
}

/* 操作按钮样式 */
.action-button {
  padding: 8px 16px;
  background-color: red;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

/* 文本输入框样式 */
.reason-textarea {
  flex-grow: 1;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  resize: none;
}

/* 确认按钮样式 */
.confirm-button {
  padding: 8px 16px;
  background-color: red;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>