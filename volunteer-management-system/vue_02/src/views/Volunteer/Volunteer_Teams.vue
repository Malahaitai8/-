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
        class="more-teams-btn"
        @click="showMoreDialog = true"
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
      <el-table-column prop="contact" label="联系方式" align="center" />
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

    <!-- 参加更多队伍弹窗 -->
    <el-dialog title="参加更多队伍" v-model="showMoreDialog" width="400px">
      <div style="text-align:center;">
        <el-input placeholder="请输入队伍名称进行搜索" v-model="searchTeam" style="margin-bottom: 20px;" />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <div style="margin-top: 20px; color: #888;">（此处可展示更多可加入的队伍列表）</div>
      </div>
      <template #footer>
        <el-button @click="showMoreDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'

export default {
  name: 'Teams',
  components: { Plus }, // 注册 Plus 图标
  setup() {
    // 模拟参与队伍数据
    const teams = ref([
      {
        name: '社区志愿服务队',
        contact: '李老师 13112345678',
        joinDate: '2023-08-10',
        status: '正常'
      },
      {
        name: '城市环保志愿团',
        contact: '王队长 13223456789',
        joinDate: '2023-11-20',
        status: '活跃'
      },
       {
        name: '红十字志愿服务队',
        contact: '赵主任 13334567890',
        joinDate: '2024-01-15',
        status: '正常'
      },
       {
        name: '青年志愿者协会',
        contact: '钱同学 13445678901',
        joinDate: '2024-03-01',
        status: '活跃'
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
    const handleViewDetail = (row) => {
       alert(`查看队伍详情：${row.name}`)
       // 这里可以实现跳转到队伍详情页面
       // router.push({ path: '/teams/detail', query: { teamId: row.id } }) // 假设队伍有id
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
      handleViewDetail
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