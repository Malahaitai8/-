<template>
  <div class="projects-page">
    <!-- 红色标题栏 -->
    <div class="projects-title">
      <span>我的项目</span>
    </div>
    <!-- 我的项目表格 -->
    <el-table
      v-if="activeTab === 'myProjects' && myPageData.length"
      :data="myPageData"
      border
      style="width: 100%; margin-top: 0;"
      header-cell-class-name="table-header"
    >
      <el-table-column prop="name" label="项目名称" align="center" />
      <el-table-column prop="contact" label="联系方式" align="center" />
      <el-table-column prop="joinDate" label="加入时间" align="center" />
      <el-table-column prop="position" label="岗位" align="center" />
      <el-table-column prop="serviceTime" label="服务时长" align="center" />
      <el-table-column prop="status" label="状态" align="center" />
    </el-table>
    <!-- 待定项目表格 -->
    <el-table
      v-if="activeTab === 'pendingProjects' && pendingPageData.length"
      :data="pendingPageData"
      border
      style="width: 100%; margin-top: 0;"
      header-cell-class-name="table-header"
    >
      <el-table-column prop="name" label="项目名称" align="center">
        <template #default="scope">
          <span style="color:#d9001b;font-weight:bold;">{{ scope.row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="contact" label="联系方式" align="center" />
      <el-table-column prop="position" label="岗位" align="center" />
      <el-table-column prop="status" label="状态" align="center" />
      <el-table-column label="操作" align="center">
        <template #default="scope">
          <el-button type="text" style="color:#d9001b;" @click="handleApply(scope.row)">申请中</el-button>
          <el-button type="text" style="color:#d9001b;" @click="handleDelete(scope.row)">删除</el-button>
          <el-button type="text" style="color:#d9001b;" @click="handleChange(scope.row)">更换岗位</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 空数据提示 -->
    <div v-if="(activeTab === 'myProjects' && !myPageData.length) || (activeTab === 'pendingProjects' && !pendingPageData.length)" class="empty-box">
      <img src="https://img.alicdn.com/imgextra/i4/O1CN01v7Qw1B1QwQwQwQwQw_!!6000000002007-2-tps-200-200.png" alt="empty" />
      <div>暂无项目信息</div>
    </div>
    <!-- 分页器 -->
    <el-pagination
      v-if="activeTab === 'myProjects' && myProjects.length > pageSize"
      style="margin-top: 24px; text-align: right;"
      background
      layout="prev, pager, next, jumper"
      :total="myProjects.length"
      :page-size="pageSize"
      v-model:current-page="myCurrentPage"
    />
    <el-pagination
      v-if="activeTab === 'pendingProjects' && pendingProjects.length > pageSize"
      style="margin-top: 24px; text-align: right;"
      background
      layout="prev, pager, next, jumper"
      :total="pendingProjects.length"
      :page-size="pageSize"
      v-model:current-page="pendingCurrentPage"
    />

  </div>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'Projects',
  setup() {
    // 我的项目数据
    const myProjects = ref([
      {
        name: '社区卫生宣传',
        contact: '张老师 13800138000',
        joinDate: '2024-04-01',
        position: '宣传员',
        serviceTime: '12小时',
        status: '已完成'
      },
      {
        name: '无偿献血活动',
        contact: '李主任 13900139000',
        joinDate: '2024-03-15',
        position: '志愿者',
        serviceTime: '8小时',
        status: '进行中'
      }
      ])
    const pendingProjects = ref([
      {
        name: '“铸魂达尔罕”志愿服务项目',
        contact: '张坤',
        position: '志愿者',
        status: '申请中'
      },
      {
        name: '社区防疫宣传',
        contact: '李明',
        position: '宣传员',
        status: '申请中'
      }
    ])
    // 分页相关
    const pageSize = 5
    const myCurrentPage = ref(1)
    const pendingCurrentPage = ref(1)
    const myPageData = computed(() => {
      const start = (myCurrentPage.value - 1) * pageSize
      return myProjects.value.slice(start, start + pageSize)
    })
    const pendingPageData = computed(() => {
      const start = (pendingCurrentPage.value - 1) * pageSize
      return pendingProjects.value.slice(start, start + pageSize)
    })

    // 标签页切换
    const activeTab = ref('myProjects')
    const handleTabClick = (tab) => {
      // 切换分页时重置页码
      if (tab.paneName === 'myProjects') {
        myCurrentPage.value = 1
      } else if (tab.paneName === 'pendingProjects') {
        pendingCurrentPage.value = 1
      }
    }

    // 操作按钮事件
    const handleApply = (row) => {
      alert(`申请中：${row.name}`)
    }
    const handleDelete = (row) => {
      alert(`删除：${row.name}`)
    }
    const handleChange = (row) => {
      alert(`更换岗位：${row.name}`)
    }

    const showMoreDialog = ref(false)
    const searchProject = ref('')
    const handleSearch = () => {
      alert(`搜索项目：${searchProject.value}`)
    }

    return {
      myProjects,
      pendingProjects,
      pageSize,
      myCurrentPage,
      pendingCurrentPage,
      myPageData,
      pendingPageData,
      activeTab,
      handleTabClick,
      handleApply,
      handleDelete,
      handleChange,
      showMoreDialog,
      searchProject,
      handleSearch
    }
  }
}
</script>

<style scoped>
.projects-title {
  background: #fff0f0;
  color: #ff0000;
  font-weight: bold;
  font-size: 20px;
  padding: 18px 32px 8px 32px;
  border-bottom: 3px solid #ff0000;
  margin-bottom: 0;
}
.projects-page {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.06);
  padding: 0 0 32px 0;
  min-height: 400px;
}
.projects-tabs-bar {
  display: flex;
  align-items: center;
  background: #fff0f0;
  border-bottom: 3px solid #ff0000;
  margin-bottom: 0;
  padding: 0 32px;
}
.projects-tabs {
  flex: 1;
  background: transparent;
  border-bottom: none;
}
.more-btn {
  margin-left: 16px;
  background: #ff0000;
  color: #fff;
  border: none;
}
.projects-tabs :deep(.el-tabs__item.is-active) {
  color: #ff0000 !important;
  font-weight: bold;
  background: #fff0f0;
  border-bottom: 3px solid #ff0000 !important;
}
.projects-tabs :deep(.el-tabs__item) {
  font-size: 18px;
}
.table-header {
  background: #fff0f0 !important;
  color: #ff0000 !important;
  font-weight: bold;
}
.empty-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 60px 0 0 0;
  color: #aaa;
  font-size: 16px;
}
.empty-box img {
  width: 80px;
  margin-bottom: 12px;
  opacity: 0.6;
}
</style>