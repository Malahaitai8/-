<template>
  <div class="home-container">
    <!-- 左侧边栏 -->
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

    <!-- 右侧内容区 -->
    <div class="content">
      <!-- 个人中心二级菜单 -->
      <div v-if="activeMenu === 'personal'" class="sub-menu">
        <el-menu mode="horizontal" :default-active="activeSubMenu" @select="handleSubSelect">
          <el-menu-item index="dashboard">我的首页</el-menu-item>
          <el-menu-item index="profile">修改资料</el-menu-item>
          <el-menu-item index="reviews">我的评价</el-menu-item>
          <el-menu-item index="complaints">投诉举报</el-menu-item>
          <el-menu-item index="training">我的培训</el-menu-item>
        </el-menu>
      </div>
      <!-- 志愿项目二级菜单 -->
      <div v-if="activeMenu === 'projects'" class="sub-menu">
        <el-menu mode="horizontal" :default-active="activeProjectSubMenu" @select="handleProjectSubSelect" class="project-sub-menu">
          <el-menu-item index="projects">项目列表</el-menu-item>
          <el-menu-item index="project-apply">待定项目</el-menu-item>
        </el-menu>
        <!-- 将按钮移到菜单外面，但仍在 sub-menu div 内 -->
        <el-button
            type="danger"
            size="small"
            class="more-btn-in-menu"
            @click="goToProjectsMore"
            round
        >
          <el-icon style="vertical-align: middle; margin-right: 4px;">
            <Plus />
          </el-icon>
          参加更多项目
        </el-button>
      </div>
      <!-- 其他菜单同理可加 -->

      <div class="main-content">
        <router-view></router-view>
      </div>
    </div>

  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, List, UserFilled, Plus } from '@element-plus/icons-vue'

export default {
  name: 'Home',
  components: {
    User,
    List,
    UserFilled,
    Plus
  },
  setup() {
    const router = useRouter()
    const activeMenu = ref('personal')
    const activeSubMenu = ref('dashboard')
    const activeProjectSubMenu = ref('projects')

    // 参加更多项目弹窗相关
    const showMoreDialog = ref(false)
    const searchProject = ref('')
    const handleSearch = () => {
      alert(`搜索项目：${searchProject.value}`)
      // 这里可以添加实际的搜索逻辑，比如跳转到搜索页面
      // router.push({ path: '/projects/search', query: { q: searchProject.value } })
      showMoreDialog.value = false
      searchProject.value = ''
    }

    // 一级菜单跳转
    const handleSelect = (key) => {
      activeMenu.value = key
      if (key === 'personal') {
        router.push('/')
        activeSubMenu.value = 'dashboard'
      } else if (key === 'projects') {
        router.push('/projects')
        activeProjectSubMenu.value = 'projects' // 默认激活"项目列表"
      } else if (key === 'teams') {
        router.push('/teams')
      }
    }

    // 个人中心二级菜单跳转
    const handleSubSelect = (key) => {
      activeSubMenu.value = key
      if (key === 'dashboard') {
        router.push('/')
      } else {
        router.push('/' + key)
      }
    }

    // 志愿项目二级菜单跳转
    const handleProjectSubSelect = (key) => {
      activeProjectSubMenu.value = key
      // 注意：点击二级菜单时，也需要跳转路由
      if (key === 'projects') {
        router.push('/projects')
      } else if (key === 'project-apply') {
        router.push('/project-apply')
      }
    }

    const goToProjectsMore = () => {
      router.push('/projects-more')
    }

    return {
      activeMenu,
      activeSubMenu,
      handleSelect,
      handleSubSelect,
      activeProjectSubMenu,
      handleProjectSubSelect,
      showMoreDialog,
      searchProject,
      handleSearch,
      goToProjectsMore
    }
  }
}
</script>

<style scoped>
.home-container {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 200px;
  height: 100%;
  border-right: solid 1px #e6e6e6;
  background-color: #fff;
}

.content {
  flex: 1;
  padding: 20px;
  background-color: #f5f5f5;
}

.sub-menu {
  margin-bottom: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  /* 让 sub-menu 使用 flex 布局 */
  display: flex;
  align-items: center; /* 垂直居中对齐 */
}

/* 调整二级菜单的样式 */
.sub-menu .el-menu {
  flex: 1; /* 让菜单占据剩余空间 */
  border-bottom: none; /* 移除底部边框，因为它在外层 sub-menu div 上 */
}

.more-btn-in-menu {
  margin-left: auto; /* 将按钮推到最右侧 */
  margin-right: 10px; /* 可选：调整右侧边距 */
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
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 添加红色主题样式 */
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
</style> 