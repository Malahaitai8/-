<template>
  <div class="dashboard-content">

    <!-- 顶部用户信息区 -->
    <div class="user-header-section">
      <div class="user-info-display">
        <el-avatar :size="70" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
        <div class="user-greeting">
          <h3 class="username">欢迎回来，{{ displayNameFromStore }}</h3>
          <el-button type="primary" size="small" @click="goToProfile" class="edit-profile-btn">
            修改资料
          </el-button>
        </div>
      </div>
    </div>

    <!-- 数据统计卡片 - 第一行 -->
    <div class="stat-cards-row">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="stat-card">
            <template #header>
              <div class="card-header">
                <el-icon><Timer /></el-icon>
                <span>总志愿时长</span>
              </div>
            </template>
            <div class="card-content">
              <span class="number">{{ userStore.serviceHours }}</span>
              <span class="unit">小时</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="stat-card">
            <template #header>
              <div class="card-header">
                <el-icon><Star /></el-icon>
                <span>志愿者星级</span>
              </div>
            </template>
            <div class="card-content centered-content">
              <el-rate
                  v-model="userStore.volunteerStarLevelFromView"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value}"
                  :colors="['#ff9900', '#ff9900', '#ff9900']"
              />
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="stat-card">
            <template #header>
              <div class="card-header">
                <el-icon><Reading /></el-icon>
                <span>你的综合评分</span>
              </div>
            </template>
            <div class="card-content">
              <span class="number">{{ userStore.detailedVolunteerInfo.volunteerComprehensiveScore }}</span>
              <span class="unit">分</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 数据统计卡片 - 第二行 -->
    <div class="stat-cards-row">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="stat-card">
            <template #header>
              <div class="card-header">
                <el-icon><UserFilled /></el-icon>
                <span>志愿团体</span>
              </div>
            </template>
            <div class="card-content">
              <span class="number">5</span>
              <span class="unit">个</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="stat-card">
            <template #header>
              <div class="card-header">
                <el-icon><List /></el-icon>
                <span>参与项目</span>
              </div>
            </template>
            <div class="card-content">
              <span class="number">12</span>
              <span class="unit">个</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

  </div>
</template>

<script setup>
import {ref, computed, onMounted} from 'vue';
import {useRouter, useRoute} from 'vue-router';
import {User, List, UserFilled, Plus} from '@element-plus/icons-vue';
import {useUserStore} from '@/stores/userStore'; // 导入您的 Pinia 用户 store

// 如果需要在 <script setup> 中显式定义组件名 (Vue 3.3+)
// defineOptions({ name: 'VolunteerHomeLayout' });

const router = useRouter();
const userStore = useUserStore(); // 使用 Pinia store
const currentRoute = useRoute(); // 获取当前路由信息，用于菜单激活和条件渲染

// --- 从 Pinia Store 获取响应式数据 ---
const displayNameFromStore = computed(() => userStore.displayName);
const isAuthenticated = computed(() => userStore.isAuthenticated);
// 示例：获取头像URL，如果store中有的话，否则使用默认
const userAvatar = computed(() => userStore.detailedVolunteerInfo?.avatarUrl || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png');
const starLevelFromStore = computed(() => userStore.starLevel); // 直接使用 store 中的 starLevel getter

// --- 菜单激活状态 ---
// 主菜单激活路径
const activePath = computed(() => {
  if (currentRoute.path.startsWith('/volunteer/projects')) return '/volunteer/projects';
  if (currentRoute.path.startsWith('/volunteer/teams')) return '/volunteer/teams';
  // 对于个人中心下的所有子路由，主菜单都应该激活 "/volunteer"
  if (currentRoute.path.startsWith('/volunteer')) return '/volunteer';
  return '/volunteer'; // 默认或回退值
});

// 判断当前是否在“个人中心”相关的二级菜单区域
const isPersonalSectionActive = computed(() => {
  return currentRoute.path.startsWith('/volunteer') &&
      !currentRoute.path.startsWith('/volunteer/projects') &&
      !currentRoute.path.startsWith('/volunteer/teams');
});

// 判断当前是否在“志愿项目”相关的二级菜单区域
const isProjectsSectionActive = computed(() => {
  return currentRoute.path.startsWith('/volunteer/projects');
});

// --- 导航方法 ---
const handleMenuSelect = (path) => {
  if (currentRoute.path !== path) {
    router.push(path);
  }
};

const handleSubMenuSelect = (path) => {
  if (currentRoute.path !== path) {
    router.push(path);
  }
};

// --- “参加更多项目”弹窗逻辑 ---
const showJoinProjectDialog = ref(false);
const searchProjectNameInput = ref('');

const handleProjectSearchAction = () => {
  if (!searchProjectNameInput.value.trim()) {
    alert('请输入项目名称进行搜索'); // 实际项目中请使用 ElMessage
    return;
  }
  alert(`正在搜索项目：${searchProjectNameInput.value}`);
  // 示例：跳转到项目搜索结果页或执行其他搜索逻辑
  // router.push({ path: '/projects/all', query: { search: searchProjectNameInput.value.trim() } });
  showJoinProjectDialog.value = false;
  // searchProjectNameInput.value = ''; // 根据需求决定是否清空
};

// --- 组件生命周期钩子 ---
onMounted(() => {
  // Pinia store 应该在其 `initializeStore` action 中（通常在应用根组件 App.vue 或 main.js 调用一次）
  // 负责从 localStorage 初始化状态并触发获取最新数据。
  // VolunteerHome 组件通常不需要再显式调用 store 的 fetch 方法，
  // 除非有特定的业务需求，比如每次进入该布局时都强制刷新数据。
  // console.log('VolunteerHomeLayout mounted. User Authenticated from store:', isAuthenticated.value);

  // 如果 store 的初始化是异步的，并且你想在这里确保数据已加载完成 (或至少已尝试加载)
  // 或者如果用户可能直接进入这个页面而 App.vue 的 onMounted 可能尚未完成 store 初始化
  if (!userStore.loggedInUser && localStorage.getItem(userStore.USER_SESSION_KEY)) {
    // 如果 store 中没有登录用户，但 localStorage 中有，则尝试初始化 store
    // 这是一种保险措施，理想情况下 App.vue 的初始化应该先于此
    // console.log('VolunteerHomeLayout: Store not initialized with user, attempting to initialize.');
    // userStore.initializeStore(); // 确保 initializeStore 幂等或有加载状态
  } else if (isAuthenticated.value && !userStore.detailedVolunteerInfo.volunteerId) {
    // 如果已认证但详细信息不完整，可以考虑触发一次获取
    // console.log('VolunteerHomeLayout: Detailed info might be missing, store action might be needed.');
    // userStore.fetchDetailedVolunteerInfo(); // 同样，store 内部应有逻辑防止不必要的重复调用
  }
});

</script>

<style scoped>
.dashboard-content {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
}

.user-header-section {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.user-info-display {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-greeting {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.username {
  margin: 0;
  color: #333;
  font-size: 22px;
  font-weight: bold;
}

.edit-profile-btn {
  background: #ff0000;
  border-color: #ff0000;
  color: #fff;
  border-radius: 4px;
  font-size: 12px;
}

.stat-cards-row {
  margin-bottom: 20px;
}

.stat-card {
  height: 150px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  border-radius: 8px;
  overflow: hidden;
}
.stat-card :deep(.el-card__header) {
  padding: 12px 16px;
  background-color: #fff0f0;
  border-bottom: 1px solid #ffcccc;
  font-size: 14px;
  font-weight: bold;
  color: #ff0000;
}
.stat-card :deep(.el-card__body) {
  padding: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex-grow: 1;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
}
.card-header .el-icon {
  color: #ff0000;
}

.card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex-grow: 1;
}
.centered-content {
  justify-content: center;
}

.number {
  font-size: 36px;
  font-weight: bold;
  color: #ff0000;
  line-height: 1.2;
}

.unit {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

/* 图标样式 */
.el-icon {
  font-size: 18px;
}
</style>