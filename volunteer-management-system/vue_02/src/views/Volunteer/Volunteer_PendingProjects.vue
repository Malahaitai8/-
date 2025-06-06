<template>
  <div class="project-apply-page">
    <!-- 红色标题栏 -->
    <div class="project-apply-title">
      <span>待定项目</span>
    </div>
    <!-- 项目表格 -->
    <el-table
        v-if="pageData.length"
        :data="pageData"
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
    <!-- 空数据提示 -->
    <div v-else class="empty-box">
      <img src="https://img.alicdn.com/imgextra/i4/O1CN01v7Qw1B1QwQwQwQwQw_!!6000000002007-2-tps-200-200.png" alt="empty" />
      <div>暂无项目信息</div>
    </div>
    <!-- 分页器 -->
    <el-pagination
        v-if="projects.length > pageSize"
        style="margin-top: 24px; text-align: right;"
        background
        layout="prev, pager, next, jumper"
        :total="projects.length"
        :page-size="pageSize"
        v-model:current-page="currentPage"
    />
  </div>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'ProjectApply',
  setup() {
    // 模拟待定项目数据
    const projects = ref([
      {
        name: '“铸魂达尔罕”志愿服务项目',
        contact: '张坤 13800138001',
        joinDate: '2024-05-01',
        position: '志愿者',
        serviceTime: '—',
        status: '申请中'
      },
      {
        name: '社区防疫宣传',
        contact: '李明 13900139002',
        joinDate: '2024-04-15',
        position: '宣传员',
        serviceTime: '—',
        status: '申请中'
      },
      {
        name: '环保知识普及',
        contact: '王芳 13700137003',
        joinDate: '2024-03-20',
        position: '志愿者',
        serviceTime: '—',
        status: '申请中'
      }
    ])
    // 分页相关
    const pageSize = 5
    const currentPage = ref(1)
    const pageData = computed(() => {
      const start = (currentPage.value - 1) * pageSize
      return projects.value.slice(start, start + pageSize)
    })

    return {
      projects,
      pageData,
      pageSize,
      currentPage
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