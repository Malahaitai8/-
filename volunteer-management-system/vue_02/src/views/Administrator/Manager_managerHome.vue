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
      <span class="welcome">欢迎您：{{ displayNameFromStore }}！</span>
    </header>
    <!-- 主体部分，用于展示主要功能按钮 -->
    <main class="main-content">
      <!-- 按钮容器，使用网格布局排列按钮 -->
      <div class="button-container">
        <!-- 循环渲染 router-link -->
        <router-link
            v-for="(item, index) in buttons"
            :key="index"
            :to="{ name: item.routeName }"
            class="custom-button"
        >
          {{ item.label }}
        </router-link>
      </div>
    </main>
  </div>
</template>

<script setup>
import {useAdminStore} from "@/stores/adminStore.js";
import {computed, onMounted} from "vue";

// 如果需要在 <script setup> 中显式定义组件名 (Vue 3.3+)
// defineOptions({ name: 'VolunteerHomeLayout' });

const adminStore = useAdminStore(); // 使用 Pinia store
adminStore.initializeStore();
const displayNameFromStore = computed(() => adminStore.displayName);
const isAuthenticated = computed(() => adminStore.isAuthenticated);
// 定义一个包含按钮信息的数组，添加路由名称
const buttons = [
  { label: '个人资料', routeName: 'personalData' },
  { label: '志愿者管理', routeName: 'manageVolunteer' },
  { label: '组织机构管理', routeName: 'manageGroup' },
  { label: '志愿活动管理', routeName: 'manageActivity' },
  { label: '投诉处理', routeName: 'complaint' },
  { label: '服务留言', routeName: 'message' }
];
onMounted(() => {
  // Pinia store 应该在其自己的 `initializeStore` action (通常在应用根组件 App.vue 或 main.js 调用一次)
  // 负责从 localStorage 初始化状态并触发获取最新数据。
  // VolunteerHome 组件通常不需要再显式调用 store 的 fetch 方法，
  // 除非您有特定的业务需求，比如每次进入该布局时都强制刷新数据。
  // console.log('VolunteerHome mounted. User Authenticated:', isAuthenticated.value);
  // console.log('Current detailed volunteer info from store:', userStore.detailedVolunteerInfo);

  // 如果 store 可能尚未初始化，或者你想确保数据是最新的（谨慎使用，避免不必要的 API 调用）
  if (isAuthenticated.value && !adminStore.detailedAdminInfo.adminId) { // 简单判断详细信息是否已加载
    // console.log('VolunteerHome: Detailed info might be missing, attempting to fetch from store action.');
    // userStore.fetchDetailedVolunteerInfo(); // store 内部应有逻辑防止 username 为空时调用
  }
});

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

/* 按钮容器样式，使用网格布局，将容器分为 2 列，设置按钮之间的间距 */
.button-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 40px;
}

/* 自定义按钮样式，设置背景颜色为红色，文字颜色为白色，加粗显示，添加内边距、圆角和鼠标指针样式 */

.custom-button {
  background-color: red;
  color: white;
  font-weight: bold;
  font-size: 20px;
  padding: 4px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  aspect-ratio: 10 / 1;
  /* 使用 flex 布局实现内容居中 */
  display: flex;
  justify-content: center;
  align-items: center;
  text-decoration: none;
}
</style>



