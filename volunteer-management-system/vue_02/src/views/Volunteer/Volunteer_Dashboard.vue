<template>
  <div class="home-container">
    <el-menu
      class="sidebar"
      :default-active="activePath"
      @select="handleSelect"
    >
      <el-menu-item index="/volunteer">
        <el-icon><User /></el-icon>
        <span>个人中心</span>
      </el-menu-item>
      <el-menu-item index="/volunteer/projects">
        <el-icon><List /></el-icon>
        <span>志愿项目</span>
      </el-menu-item>
      <el-menu-item index="/volunteer/teams">
        <el-icon><UserFilled /></el-icon>
        <span>志愿队伍</span>
      </el-menu-item>
    </el-menu>

    <div class="content">
      <div v-if="isAuthenticated" style="margin-bottom: 20px; padding: 10px; background-color: #ecf5ff; border-radius: 4px;">
        <span style="color: #409eff; font-weight: bold;">欢迎您，{{ displayNameFromStore }}！</span>
      </div>

      <div v-if="currentRoute.path.startsWith('/volunteer') &&
                 !currentRoute.path.startsWith('/volunteer/projects') &&
                 !currentRoute.path.startsWith('/volunteer/teams')"
           class="sub-menu">
        <el-menu mode="horizontal" :default-active="currentRoute.path" @select="handleSubSelectPersonal">
          <el-menu-item index="/volunteer">我的首页</el-menu-item>
          <el-menu-item index="/volunteer/profile">修改资料</el-menu-item>
          <el-menu-item index="/volunteer/reviews">我的评价</el-menu-item>
          <el-menu-item index="/volunteer/complaints">投诉举报</el-menu-item>
          <el-menu-item index="/volunteer/training">我的培训</el-menu-item>
        </el-menu>
      </div>
      <div v-if="currentRoute.path.startsWith('/volunteer/projects')" class="sub-menu">
        <el-menu mode="horizontal" :default-active="currentRoute.path" @select="handleSubSelectProjects" class="project-sub-menu">
          <el-menu-item index="/volunteer/projects">项目列表</el-menu-item>
          <el-menu-item index="/volunteer/project-apply">待定项目</el-menu-item>
        </el-menu>
        <el-button
            type="danger"
            size="small"
            class="more-btn-in-menu"
            @click="showMoreDialog = true"
            round
        >
          <el-icon style="vertical-align: middle; margin-right: 4px;">
            <Plus />
          </el-icon>
          参加更多项目
        </el-button>
      </div>

      <div class="main-content">
        <router-view></router-view>
      </div>
    </div>

    <el-dialog title="参加更多项目" v-model="showMoreDialog" width="400px">
      <div style="text-align:center;">
        <el-input placeholder="请输入项目名称进行搜索" v-model="searchProjectName" style="margin-bottom: 20px;" />
        <el-button type="primary" @click="handleSearchProjectDialogAction">搜索</el-button>
        <div style="margin-top: 20px; color: #888;">（此处可展示更多可报名的项目列表）</div>
      </div>
      <template #footer>
        <el-button @click="showMoreDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'; // 移除了 onUnmounted 因为不再直接监听 localStorage
import { useRouter, useRoute } from 'vue-router';
import { User, List, UserFilled, Plus } from '@element-plus/icons-vue'; // 确保图标已导入
import { useUserStore } from '@/stores/userStore'; // 导入您的 Pinia 用户 store

// 如果需要在 <script setup> 中显式定义组件名 (Vue 3.3+)
// defineOptions({ name: 'VolunteerHomeLayout' });

const router = useRouter();
const userStore = useUserStore(); // 使用 Pinia store
userStore.initializeStore();
const currentRoute = useRoute();

// --- 从 Pinia Store 获取响应式数据 ---
// 这些 computed 属性会随着 store 中 state 的变化而自动更新
// 注意：在模板中使用时，对于 ref 和 computed 返回的 ref，需要 .value，但模板插值中会自动解包
// 对于 reactive 对象，直接 .属性名 即可。
// Pinia store 的 state 和 getters 返回的是 Proxy<Ref<T>> 或 Proxy<Reactive<T>>
// 在 <script setup> 内部访问 store 的 state 或 getter 时，直接 userStore.propertyName
// 在模板中，如果 store 的 state 是 ref，则 userStore.propertyName；如果是 reactive 对象，则是 userStore.objectName.propertyName
const displayNameFromStore = computed(() => userStore.displayName);
const isAuthenticated = computed(() => userStore.isAuthenticated);
// 如果 volunteerInfo 是 store 中的一个 reactive 对象：
// const volunteerInfoFromStore = computed(() => userStore.detailedVolunteerInfo); // 模板中用 volunteerInfoFromStore.username
// 或者直接在模板中使用 userStore.detailedVolunteerInfo.username

// --- 菜单激活状态 和 导航逻辑 ---
const activePath = computed(() => {
  if (currentRoute.path.startsWith('/volunteer/projects')) return '/volunteer/projects';
  if (currentRoute.path.startsWith('/volunteer/teams')) return '/volunteer/teams';
  if (currentRoute.path.startsWith('/volunteer')) return '/volunteer';
  return '/volunteer'; // 默认
});

const handleSelect = (path) => {
  if (currentRoute.path !== path) { // 避免不必要的相同路径跳转
    router.push(path);
  }
};

const handleSubSelectPersonal = (path) => {
  if (currentRoute.path !== path) {
    router.push(path);
  }
};

const handleSubSelectProjects = (path) => {
  if (currentRoute.path !== path) {
    router.push(path);
  }
};

// --- “参加更多项目”弹窗逻辑 ---
const showMoreDialog = ref(false);
const searchProjectName = ref(''); // 修改变量名以更清晰区分

const handleSearchProjectDialogAction = () => { // 修改函数名以更清晰区分
  if (!searchProjectName.value.trim()) {
    // ElMessage.warning('请输入项目名称进行搜索'); // 假设您使用 Element Plus
    alert('请输入项目名称进行搜索');
    return;
  }
  alert(`正在搜索项目：${searchProjectName.value}`);
  // 实际搜索逻辑，例如跳转到包含搜索结果的页面或更新当前页面的列表
  // router.push({ path: '/all-available-projects', query: { search: searchProjectName.value.trim() } });
  showMoreDialog.value = false;
  // searchProjectName.value = ''; // 根据需求决定是否清空
};

// --- 组件生命周期钩子 ---
onMounted(() => {
  // Pinia store 应该在其自己的 `initializeStore` action (通常在应用根组件 App.vue 或 main.js 调用一次)
  // 负责从 localStorage 初始化状态并触发获取最新数据。
  // VolunteerHome 组件通常不需要再显式调用 store 的 fetch 方法，
  // 除非您有特定的业务需求，比如每次进入该布局时都强制刷新数据。
  // console.log('VolunteerHome mounted. User Authenticated:', isAuthenticated.value);
  // console.log('Current detailed volunteer info from store:', userStore.detailedVolunteerInfo);

  // 如果 store 可能尚未初始化，或者你想确保数据是最新的（谨慎使用，避免不必要的 API 调用）
  if (isAuthenticated.value && !userStore.detailedVolunteerInfo.volunteerId) { // 简单判断详细信息是否已加载
    // console.log('VolunteerHome: Detailed info might be missing, attempting to fetch from store action.');
    // userStore.fetchDetailedVolunteerInfo(); // store 内部应有逻辑防止 username 为空时调用
  }
});

</script>

<style scoped>
.home-container {
  display: flex;
  height: 100vh; /* 占满整个视窗高度 */
}

.sidebar {
  width: 200px; /* 固定宽度 */
  height: 100%; /* 占满父容器高度 */
  border-right: solid 1px #e6e6e6;
  background-color: #fff;
  flex-shrink: 0; /* 防止侧边栏在 flex 布局中被压缩 */
}

.content {
  flex: 1; /* 占据剩余空间 */
  padding: 20px;
  background-color: #f5f5f5;
  overflow-y: auto; /* 如果内容超出，允许垂直滚动 */
  display: flex;
  flex-direction: column; /* 使子元素垂直排列 */
}

.sub-menu {
  margin-bottom: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex; /* 用于内部 el-menu 和 el-button 的对齐 */
  align-items: center;
  flex-shrink: 0; /* 防止在内容很少时被压缩 */
}

.sub-menu .el-menu {
  flex-grow: 1; /* 让菜单占据尽可能多的空间 */
  border-bottom: none;
}

.more-btn-in-menu {
  margin-left: auto; /* 将按钮推到右侧 */
  margin-right: 10px;
  background: linear-gradient(90deg, #ff4d4f 0%, #ff0000 100%);
  color: #fff;
  border: none;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(255,0,0,0.10);
  letter-spacing: 1px;
  transition: background 0.3s;
}
.more-btn-in-menu:hover {
  background: linear-gradient(90deg, #ff7875 0%, #ff0000 100%);
  color: #fff;
}

.main-content {
  flex-grow: 1; /* 占据剩余的垂直空间 */
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  /* 如果希望 router-view 内部内容也能滚动，可能需要在这里或子组件设置 overflow */
}

/* Element Plus 菜单激活和悬停样式 (保持您的主题色) */
.el-menu-item.is-active {
  color: #ff0000 !important;
  /* background-color: #fff0f0 !important; 可选：激活时背景色 */
}

.el-menu-item:hover {
  background-color: #ffe6e6 !important;
}

/* 如果有 h2 标题 (根据您之前的 style 块) */
h2 {
  color: #ff0000;
  border-bottom: 2px solid #ff0000;
  padding-bottom: 10px;
  margin-bottom: 20px;
}
</style>