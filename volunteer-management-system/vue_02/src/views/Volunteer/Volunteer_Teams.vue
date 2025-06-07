<template>
  <div class="teams-page">
    <!-- 顶部标题栏和按钮 -->
    <div class="teams-header">
      <div class="teams-title">
        <span>我的队伍</span>
      </div>
      <el-button
          type="danger"
          size="small"
          class="more-btn-in-menu"
          @click="goToTeamsMore"
          round
      >
        <el-icon style="vertical-align: middle; margin-right: 4px;">
          <Plus />
        </el-icon>
        参加更多队伍
      </el-button>
    </div>
    <!-- 队伍表格 -->
    <el-table
      v-if="pageData.length"
      :data="pageData"
      border
      style="width: 100%; margin-top: 20px;"
      header-cell-class-name="table-header"
    >
      <el-table-column prop="name" label="队伍名称" align="center" />
      <el-table-column prop="joinDate" label="加入时间" align="center" />
      <el-table-column prop="status" label="状态" align="center" />
      <el-table-column label="操作" align="center">
        <template #default="scope">
          <el-button type="text" style="color:#d9001b;" @click="handleLeave(scope.row)">退出队伍</el-button>
          <el-button type="text" style="color:#d9001b;" @click="handleViewDetail(scope.row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 空数据提示 -->
    <div v-else class="empty-box">
      <img src="https://img.alicdn.com/imgextra/i4/O1CN01v7Qw1B1QwQwQwQwQw_!!6000000002007-2-tps-200-200.png" alt="empty" />
      <div>暂无队伍信息</div>
    </div>
    <!-- 分页器 -->
    <el-pagination
      v-if="teams.length > pageSize"
      style="margin-top: 24px; text-align: right;"
      background
      layout="prev, pager, next, jumper"
      :total="teams.length"
      :page-size="pageSize"
      v-model:current-page="currentPage"
    />
    <!-- 队伍详情弹窗 -->
    <el-dialog title="队伍详细信息" v-model="detailDialogVisible" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="组织ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="组织名称">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ detailData.contact }}</el-descriptions-item>
        <el-descriptions-item label="服务区域">{{ detailData.area }}</el-descriptions-item>
        <el-descriptions-item label="组织规模">{{ detailData.size }}</el-descriptions-item>
        <el-descriptions-item label="组织评分">{{ detailData.score }}</el-descriptions-item>
        <el-descriptions-item label="账户状态">{{ detailData.status }}</el-descriptions-item>
        <el-descriptions-item label="总服务时长">{{ detailData.totalHours }}</el-descriptions-item>
        <el-descriptions-item label="活动举办次数">{{ detailData.activityCount }}</el-descriptions-item>
        <el-descriptions-item label="培训举办次数">{{ detailData.trainingCount }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

export default {
  name: 'Teams',
  components: { Plus }, // 注册 Plus 图标
  setup() {
    const router = useRouter()
    // 模拟参与队伍数据
    const teams = ref([
      {
        id: 'T001',
        name: '社区志愿服务队',
        joinDate: '2023-08-10',
        status: '正常',
        contact: '13800000001',
        area: '北京',
        size: '50人',
        score: 9.2,
        totalHours: '1200小时',
        activityCount: 15,
        trainingCount: 6
      },
      {
        id: 'T002',
        name: '城市环保志愿团',
        joinDate: '2023-11-20',
        status: '活跃',
        contact: '13800000002',
        area: '上海',
        size: '80人',
        score: 8.8,
        totalHours: '2000小时',
        activityCount: 22,
        trainingCount: 10
      },
      {
        id: 'T003',
        name: '红十字志愿服务队',
        joinDate: '2024-01-15',
        status: '正常',
        contact: '13800000003',
        area: '广州',
        size: '40人',
        score: 9.5,
        totalHours: '900小时',
        activityCount: 8,
        trainingCount: 3
      },
      {
        id: 'T004',
        name: '青年志愿者协会',
        joinDate: '2024-03-01',
        status: '活跃',
        contact: '13800000004',
        area: '深圳',
        size: '120人',
        score: 9.0,
        totalHours: '3500小时',
        activityCount: 30,
        trainingCount: 15
      }
    ])
    // 分页相关
    const pageSize = 5
    const currentPage = ref(1)
    const pageData = computed(() => {
      const start = (currentPage.value - 1) * pageSize
      return teams.value.slice(start, start + pageSize)
    })

    // 参加更多队伍弹窗相关
    const showMoreDialog = ref(false)
    const searchTeam = ref('')
    const handleSearch = () => {
      alert(`搜索队伍：${searchTeam.value}`)
      // 这里可以添加实际的搜索逻辑
      showMoreDialog.value = false // 搜索后关闭弹窗
      searchTeam.value = '' // 清空搜索框
    }

    // 操作按钮事件
    const handleLeave = (row) => {
      if (confirm(`确定要退出队伍 "${row.name}" 吗？`)) {
        // 实现退出队伍的逻辑
        alert(`已申请退出队伍：${row.name}`)
      }
    }
    // 队伍详情弹窗相关
    const detailDialogVisible = ref(false)
    const detailData = ref({})
    const handleViewDetail = (row) => {
      detailData.value = { ...row }
      detailDialogVisible.value = true
    }

    const goToTeamsMore = () => {
      router.push('/volunteer/teams-more')
    }

    return {
      teams,
      pageData,
      pageSize,
      currentPage,
      showMoreDialog,
      searchTeam,
      handleSearch,
      handleLeave,
      handleViewDetail,
      goToTeamsMore,
      detailDialogVisible,
      detailData
    }
  }
}
</script>

<style scoped>
.teams-page {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.06);
  padding: 0 0 32px 0;
  min-height: 400px;
}
.teams-header {
  display: flex;
  align-items: center;
  background: #fff0f0;
  border-bottom: 3px solid #ff0000;
  margin-bottom: 0;
  padding: 18px 32px 8px 32px;
}
.teams-title {
  flex: 1; /* 让标题占据剩余空间 */
  color: #ff0000;
  font-weight: bold;
  font-size: 20px;
}
.more-teams-btn {
   background: linear-gradient(90deg, #ff4d4f 0%, #ff0000 100%);
  color: #fff;
  border: none;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(255,0,0,0.10);
  letter-spacing: 1px;
  transition: background 0.3s;
}
.more-teams-btn:hover {
  background: linear-gradient(90deg, #ff7875 0%, #ff0000 100%);
  color: #fff;
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