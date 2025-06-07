<template>
  <div id="app">
    <div class="header">
      <img src="@\..\public\orgalogo.png" alt="Logo" class="logo">
      <h1>志愿管理系统</h1>
      <span class="welcome-message">欢迎您，{{displayNameFromStore}}</span>
    </div>
    <div class="content-container">
      <div class="button-container">
        <el-button class="big-button" @click="organizationInfo">志愿组织信息</el-button>
        <el-button class="big-button" @click="applyActivity">申请志愿活动</el-button>
        <el-button class="big-button" @click="applyTraining">申请志愿培训</el-button>
      </div>

      <div class="button-container">
        <el-button class="big-button" @click="viewActivityRecords">查看志愿活动记录</el-button>
        <el-button class="big-button" @click="viewTrainingRecords">查看志愿培训记录</el-button>
        <el-button class="big-button" @click="managePersonnel">人员管理</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import{useOrganizationStore} from "@/stores/organizationStore.js";
import { useAdminStore } from '@/stores/adminStore.js';
import {computed, onMounted} from "vue";

const organizationStore = useOrganizationStore();
const adminStore = useAdminStore();
organizationStore.initializeStore();
const displayNameFromStore = computed(() => organizationStore.displayName);
const isAuthenticated = computed(() => adminStore.isAuthenticated);
// 可选：如果需要在开发工具中明确显示组件名或用于递归组件 (Vue 3.3+)
// defineOptions({ name: 'App' });

const router = useRouter();

const organizationInfo = () => {
  router.push('/organization-info');
};

const applyActivity = () => {
  router.push('/apply-activity');
};

const applyTraining = () => {
  router.push('/apply-training');
};

const viewActivityRecords = () => {
  router.push('/view-activity-records');
};

const viewTrainingRecords = () => {
  router.push('/view-training-records');
};

const managePersonnel = () => {
  router.push('/manage-personnel');
};
onMounted(() => {
  // Pinia store 应该在其自己的 `initializeStore` action (通常在应用根组件 App.vue 或 main.js 调用一次)
  // 负责从 localStorage 初始化状态并触发获取最新数据。
  // VolunteerHome 组件通常不需要再显式调用 store 的 fetch 方法，
  // 除非您有特定的业务需求，比如每次进入该布局时都强制刷新数据。
  // console.log('VolunteerHome mounted. User Authenticated:', isAuthenticated.value);
  // console.log('Current detailed volunteer info from store:', userStore.detailedVolunteerInfo);

  // 如果 store 可能尚未初始化，或者你想确保数据是最新的（谨慎使用，避免不必要的 API 调用）
  if (isAuthenticated.value && !organizationStore.detailedOrganizationInfo.orgId) { // 简单判断详细信息是否已加载
    // console.log('VolunteerHome: Detailed info might be missing, attempting to fetch from store action.');
    // userStore.fetchDetailedVolunteerInfo(); // store 内部应有逻辑防止 username 为空时调用
  }
});

// 这些函数现在可以直接在 <template> 中使用，例如：
// <button @click="organizationInfo">组织信息</button>
</script>

<style scoped>
body {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  margin: 0;
}

.header {
  background-color: #ff3333; /* 鲜红色背景 */
  color: white;
  padding: 10px 20px;
  margin-bottom: 20px;
  text-align: left;
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  z-index: 1000;
}

.logo {
  height: 30px;
  margin-right: 10px;
}

.welcome-message {
  font-size: 14px;
  margin-left: 10px;
}

.content-container {
  background-image: url('@/../public/bg.png');
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center;
  min-height: calc(100vh - 50px);
  padding-top: 50px;
}

.button-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 20px;
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  margin-top: 150px;
}

.big-button {
  background-color: #ff3333;
  color: white;
  font-size: 18px;
  padding: 15px 30px;
  flex: 1 1 200px;
  min-width: 200px;
  min-height: 100px;
  box-sizing: border-box;
  border: none;
  border-radius: 4px;
}

.big-button:hover {
  background-color: #ff0000;
}
</style>