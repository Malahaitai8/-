<template>
  <div class="project-apply-page">
    <!-- 红色标题栏 -->
    <div class="project-apply-title">
      <span>已报名列表</span>
    </div>
    <!-- 筛选按钮 -->
    <div class="filter-buttons">
      <button @click="applyFilter('all')" :class="{ active: currentFilter === 'all' }">全部</button>
      <button @click="applyFilter(PROJECT_STATUS.PENDING)" :class="{ active: currentFilter === PROJECT_STATUS.PENDING }">申请中</button>
      <button @click="applyFilter(PROJECT_STATUS.APPROVED)" :class="{ active: currentFilter === PROJECT_STATUS.APPROVED }">审核通过</button>
      <button @click="applyFilter(PROJECT_STATUS.REJECTED)" :class="{ active: currentFilter === PROJECT_STATUS.REJECTED }">审核不通过</button>
    </div>
    <!-- 项目表格 -->
    <el-table
        v-if="pageData.length"
        :data="pageData"
        border
        style="width: 100%; margin-top: 20px;"
        header-cell-class-name="table-header"
    >
      <el-table-column prop="name" label="项目名称" align="center" />
      <el-table-column prop="position" label="意向岗位" align="center" />
      <el-table-column prop="joinDate" label="申请时间" align="center" />
      <el-table-column prop="status" label="申请状态" align="center" />
      <el-table-column label="操作" align="center" width="200">
        <template #default="scope">
          <el-button
            size="small"
            type="danger"
            @click="withdrawApplication(scope.row)"
            v-if="scope.row.status === PROJECT_STATUS.PENDING"
          >撤回申请</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 空数据提示 -->
    <div v-else class="empty-box">
      <img src="https://img.alicdn.com/imgextra/i4/O1CN01v7Qw1B1QwQwQwQwQw_!!6000000002007-2-tps-200-200.png" alt="empty" />
      <div>暂无项目信息</div>
    </div>
    <!-- 分页器 -->
    <el-pagination
        v-if="filteredProjects.length > pageSize"
        style="margin-top: 24px; text-align: right;"
        background
        layout="prev, pager, next, jumper"
        :total="filteredProjects.length"
        :page-size="pageSize"
        v-model:current-page="currentPage"
    />
  </div>
</template>

<script>
import { ref, computed, reactive } from 'vue'

const PROJECT_STATUS = {
  PENDING: '申请中',
  APPROVED: '审核通过',
  REJECTED: '审核不通过'
}

export default {
  name: 'ProjectApply',
  setup() {
    // 模拟待定项目数据
    const projects = ref([
      {
        name: '“铸魂达尔罕”志愿服务项目',
        joinDate: '2024-05-01',
        position: '志愿者',
        status: PROJECT_STATUS.PENDING
      },
      {
        name: '社区防疫宣传',
        joinDate: '2024-04-15',
        position: '宣传员',
        status: PROJECT_STATUS.APPROVED
      },
      {
        name: '环保知识普及',
        joinDate: '2024-03-20',
        position: '志愿者',
        status: PROJECT_STATUS.REJECTED
      }
    ])
    // 分页相关
    const pageSize = 5
    const currentPage = ref(1)
    const currentFilter = ref('all')
    const filteredProjects = ref([...projects.value])
    const pageData = computed(() => {
      const start = (currentPage.value - 1) * pageSize
      return filteredProjects.value.slice(start, start + pageSize)
    })

    // 筛选逻辑
    const applyFilter = (filterType) => {
      currentFilter.value = filterType
      currentPage.value = 1
      if (filterType === 'all') {
        filteredProjects.value = [...projects.value]
      } else {
        filteredProjects.value = projects.value.filter(p => p.status === filterType)
      }
    }

    // 撤回申请
    const withdrawApplication = (row) => {
      // 这里可以调用接口，演示用alert
      alert(`已撤回：${row.name}`)
    }

    return {
      projects,
      pageData,
      pageSize,
      currentPage,
      currentFilter,
      filteredProjects,
      applyFilter,
      withdrawApplication,
      PROJECT_STATUS
    }
  }
}
</script>

<style scoped>
.project-apply-page {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.06);
  padding: 0 0 32px 0;
  min-height: 400px;
}
.project-apply-title {
  background: #fff0f0;
  color: #ff0000;
  font-weight: bold;
  font-size: 20px;
  padding: 18px 32px 8px 32px;
  border-bottom: 3px solid #ff0000;
  margin-bottom: 0;
}
.filter-buttons {
  margin-top: 20px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
.filter-buttons button {
  background-color: #f0f0f0;
  color: #333;
  padding: 8px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s ease;
}
.filter-buttons button.active {
  background-color: #ff0000;
  color: white;
  border-color: #ff0000;
}
.filter-buttons button:hover:not(.active) {
  background-color: #e9e9e9;
  border-color: #c0c0c0;
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