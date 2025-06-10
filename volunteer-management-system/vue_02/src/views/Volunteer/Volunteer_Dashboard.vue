<template>

  <div class="home-container">
    <el-menu
      class="sidebar"
      :default-active="activeMenu"
      @select="handleSelect"
    >
      <el-menu-item index="personal">
        <el-icon><User /></el-icon>
        <span>个人中心</span>
      </el-menu-item>
      <el-menu-item index="projects">
        <el-icon><List /></el-icon>
        <span>志愿项目</span>
      </el-menu-item>
      <el-menu-item index="teams">
        <el-icon><UserFilled /></el-icon>
        <span>志愿队伍</span>
      </el-menu-item>
    </el-menu>

    <div class="content">
      <div class="system-title">
      <h1 class="art-title">志愿者管理系统</h1>
    </div>

      <div v-if="activeMenu === 'personal'" class="sub-menu">
        <el-menu mode="horizontal" :default-active="activeSubMenu" @select="handleSubSelectPersonal">
          <el-menu-item index="home">我的首页</el-menu-item>
          <el-menu-item index="profile">修改资料</el-menu-item>
          <el-menu-item index="reviews">我的评价</el-menu-item>
          <el-menu-item index="complaints">投诉举报</el-menu-item>
          <el-menu-item index="training">我的培训</el-menu-item>
        </el-menu>
      </div>

      <div v-if="activeMenu === 'projects'" class="sub-menu">
        <el-menu mode="horizontal" :default-active="activeProjectSubMenu" @select="handleSubSelectProjects" class="project-sub-menu">
          <el-menu-item index="list">志愿活动列表</el-menu-item>
          <el-menu-item index="apply">已报名列表</el-menu-item>
        </el-menu>
       <el-button
            type="danger"
            size="small"
            class="more-btn-in-menu"
            @click="navigateToMoreProjects"
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


  </div>
</template>

<script setup>
import {ref, computed, onMounted, watch} from 'vue';
import {useRouter, useRoute} from 'vue-router';
import {User, List, UserFilled, Plus} from '@element-plus/icons-vue';
import {useUserStore} from '@/stores/userStore'; // Using Pinia store from File 1

// --- Router and Store Initialization ---
const router = useRouter();
const currentRoute = useRoute();
const userStore = useUserStore();
userStore.initializeStore(); // Initialize store state from localStorage

// --- State from Pinia Store (Logic from File 1) ---
const displayNameFromStore = computed(() => userStore.displayName);
const isAuthenticated = computed(() => userStore.isAuthenticated);

// --- Local UI State for Menus (Concept from File 2) ---
const activeMenu = ref('personal'); // Main sidebar: 'personal', 'projects', 'teams'
const activeSubMenu = ref('home'); // Sub-menu for personal: 'home', 'profile', etc.
const activeProjectSubMenu = ref('list'); // Sub-menu for projects: 'list', 'apply'
// --- 【修改点】添加了页面跳转函数 ---
const navigateToMoreProjects = () => {
  // 假设所有可参加项目的列表页路由为 '/projects/more'
  // 请确保您在 router/index.js 中定义了此路由
  router.push('/volunteer/projects-more');
};
// --- Path Mapping for cleaner logic ---
const personalSubMenuMap = {
  '/volunteer': 'home',
  '/volunteer/profile': 'profile',
  '/volunteer/reviews': 'reviews',
  '/volunteer/complaints': 'complaints',
  '/volunteer/training': 'training',
};

const projectSubMenuMap = {
  '/volunteer/projects': 'list',
  '/volunteer/project-apply': 'apply',
};


// --- Menu Navigation Handlers (Combined Logic) ---

// Main sidebar selection
const handleSelect = (key) => {
  activeMenu.value = key;
  switch (key) {
    case 'personal':
      router.push('/volunteer');
      break;
    case 'projects':
      router.push('/volunteer/projects');
      break;
    case 'teams':
      router.push('/volunteer/teams');
      break;
  }
};

// Personal center sub-menu selection
const handleSubSelectPersonal = (key) => {
  activeSubMenu.value = key;
  // Find the path that corresponds to the selected key
  const path = Object.keys(personalSubMenuMap).find(p => personalSubMenuMap[p] === key);
  if (path && currentRoute.path !== path) {
    router.push(path);
  }
};

// Project sub-menu selection
const handleSubSelectProjects = (key) => {
  activeProjectSubMenu.value = key;
  const path = Object.keys(projectSubMenuMap).find(p => projectSubMenuMap[p] === key);
  if (path && currentRoute.path !== path) {
    router.push(path);
  }
};

// --- Dialog Logic (from File 1) ---
const showMoreDialog = ref(false);
const searchProjectName = ref('');

const handleSearchProjectDialogAction = () => {
  if (!searchProjectName.value.trim()) {
    alert('请输入项目名称进行搜索');
    return;
  }
  alert(`正在搜索项目：${searchProjectName.value}`);
  // Example action: navigate to a search results page
  // router.push({ path: '/all-projects', query: { search: searchProjectName.value.trim() } });
  showMoreDialog.value = false;
};

// --- Sync UI with Route on Load and Navigation ---
const syncMenuStateWithRoute = (route) => {
  const path = route.path;
  if (path.startsWith('/volunteer/projects') || path.startsWith('/volunteer/project-apply')) {
    activeMenu.value = 'projects';
    activeProjectSubMenu.value = projectSubMenuMap[path] || 'list';
  } else if (path.startsWith('/volunteer/teams')) {
    activeMenu.value = 'teams';
  } else if (path.startsWith('/volunteer')) {
    activeMenu.value = 'personal';
    activeSubMenu.value = personalSubMenuMap[path] || 'home';
  }
};

// Watch for route changes to keep menus in sync
watch(currentRoute, (newRoute) => {
  syncMenuStateWithRoute(newRoute);
});

// Set initial menu state when component mounts
onMounted(() => {
  syncMenuStateWithRoute(currentRoute);
});

</script>

<style scoped>
/* Using the more detailed and polished CSS from File 1 */
.home-container {
  display: flex;
  height: 100vh; /* Occupy full viewport height */
}

.sidebar {
  width: 200px; /* Fixed width */
  height: 100%; /* Occupy full parent height */
  border-right: solid 1px #e6e6e6;
  background-color: #fff;
  flex-shrink: 0; /* Prevent sidebar from shrinking in flex layout */
}

.content {
  flex: 1; /* Occupy remaining space */
  padding: 20px;
  background-color: #f5f5f5;
  overflow-y: auto; /* Allow vertical scrolling if content overflows */
  display: flex;
  flex-direction: column; /* Arrange children vertically */
}

.sub-menu {
  margin-bottom: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex; /* Used for alignment of internal el-menu and el-button */
  align-items: center;
  flex-shrink: 0; /* Prevent shrinking when content is sparse */
}

.sub-menu .el-menu {
  flex-grow: 1; /* Allow the menu to take up as much space as possible */
  border-bottom: none;
}

.more-btn-in-menu {
  margin-left: auto; /* Push the button to the right */
  margin-right: 10px;
  background: linear-gradient(90deg, #ff4d4f 0%, #ff0000 100%);
  color: #fff;
  border: none;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(255, 0, 0, 0.10);
  letter-spacing: 1px;
  transition: background 0.3s;
}

.more-btn-in-menu:hover {
  background: linear-gradient(90deg, #ff7875 0%, #ff0000 100%);
  color: #fff;
}

.main-content {
  flex-grow: 1; /* Occupy the remaining vertical space */
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* Red theme for active and hover states */
.el-menu-item.is-active {
  color: #ff0000 !important;
}

.el-menu-item:hover {
  background-color: #ffe6e6 !important;
}

h2 {
  color: #ff0000;
  border-bottom: 2px solid #ff0000;
  padding-bottom: 10px;
  margin-bottom: 20px;
}
.system-title {
  text-align: center;
  margin: 0;
  padding: 20px 0 10px 0;
  background: linear-gradient(to right, #ff0000, #ff6666);
  border-radius: 0 0 16px 16px;
  box-shadow: 0 4px 12px rgba(255, 0, 0, 0.08);
  position: relative;
  z-index: 1000;
}

.art-title {
  margin: 0;
  font-size: 36px;
  font-weight: bold;
  color: #fff;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
  letter-spacing: 4px;
  font-family: "Microsoft YaHei", "微软雅黑", sans-serif;
  background: linear-gradient(to bottom, #fff, #ffe6e6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: titleGlow 2s ease-in-out infinite alternate;
}
@keyframes titleGlow {
  from {
    text-shadow: 0 0 10px rgba(255, 0, 0, 0.5);
  }
  to {
    text-shadow: 0 0 20px rgba(255, 0, 0, 0.8);
  }
}
</style>