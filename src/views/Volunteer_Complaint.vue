<template>
  <div class="complaints-page">
    <!-- 红色标题栏 -->
    <div class="complaints-title">
      <span>投诉举报</span>
    </div>
    <!-- 投诉表格 -->
    <el-table
      v-if="pageData.length"
      :data="pageData"
      border
      style="width: 100%; margin-top: 20px;"
      header-cell-class-name="table-header"
    >
      <el-table-column prop="complaintTime" label="投诉时间" align="center" />
      <el-table-column prop="target" label="投诉对象" align="center" />
      <el-table-column prop="type" label="投诉类型" align="center" />
      <el-table-column prop="content" label="投诉内容" align="center" />
      <el-table-column prop="state" label="处理状态" align="center" />
      <el-table-column prop="result" label="处理结果" align="center" />
      <el-table-column label="回访内容" align="center">
        <template #default="scope">
          <el-button size="small" type="success" @click="handleSatisfy(scope.row)">满意</el-button>
          <el-button size="small" type="danger" @click="handleUnsatisfy(scope.row)">不满意</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 空数据提示 -->
    <div v-else class="empty-box">
      <img src="https://img.alicdn.com/imgextra/i4/O1CN01v7Qw1B1QwQwQwQwQw_!!6000000002007-2-tps-200-200.png" alt="empty" />
      <div>暂无投诉信息</div>
    </div>
    <!-- 分页器 -->
    <el-pagination
      v-if="complaints.length > pageSize"
      style="margin-top: 24px; text-align: right;"
      background
      layout="prev, pager, next, jumper"
      :total="complaints.length"
      :page-size="pageSize"
      v-model:current-page="currentPage"
    />
  </div>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'Complaints',
  setup() {
    // 模拟投诉数据
    const complaints = ref([
      {
        complaintTime: '2024-05-10 09:30',
        target: '志愿服务队A',
        type: '活动安排',
        content: '志愿活动组织不合理，时间安排冲突。',
        state: '已处理',
        result: '调整活动时间',
        reply: '已优化活动时间安排，感谢反馈。'
      },
      {
        complaintTime: '2024-04-22 14:15',
        target: '物资管理员',
        type: '物资分配',
        content: '活动物资分配不均。',
        state: '已处理',
        result: '重新分配物资',
        reply: '已重新分配物资，确保公平。'
      },
      {
        complaintTime: '2024-03-18 16:40',
        target: '审核员',
        type: '报名审核',
        content: '报名信息未及时审核。',
        state: '已处理',
        result: '加快审核进度',
        reply: '已加快审核进度，感谢理解。'
      },
      {
        complaintTime: '2024-02-05 11:00',
        target: '志愿服务中心',
        type: '证书发放',
        content: '志愿者服务证书未发放。',
        state: '已处理',
        result: '补发证书',
        reply: '证书已补发，请查收。'
      },
      {
        complaintTime: '2024-01-12 15:25',
        target: '活动负责人',
        type: '现场管理',
        content: '活动现场秩序混乱。',
        state: '已处理',
        result: '加强管理',
        reply: '已加强现场管理，感谢建议。'
      },
      {
        complaintTime: '2023-12-20 10:00',
        target: '系统管理员',
        type: '系统故障',
        content: '报名系统偶尔无法登录。',
        state: '已处理',
        result: '修复系统',
        reply: '系统已修复，感谢反馈。'
      }
    ])
    // 分页相关
    const pageSize = 5
    const currentPage = ref(1)
    const pageData = computed(() => {
      const start = (currentPage.value - 1) * pageSize
      return complaints.value.slice(start, start + pageSize)
    })

    // 回访按钮事件
    const handleSatisfy = (row) => {
      window.$message ? window.$message.success('感谢您的满意反馈！') : alert('感谢您的满意反馈！')
    }
    const handleUnsatisfy = (row) => {
      window.$message ? window.$message.warning('我们会继续改进，感谢您的反馈！') : alert('我们会继续改进，感谢您的反馈！')
    }

    return {
      complaints,
      pageData,
      pageSize,
      currentPage,
      handleSatisfy,
      handleUnsatisfy
    }
  }
}
</script>

<style scoped>
.complaints-page {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.06);
  padding: 0 0 32px 0;
  min-height: 400px;
}
.complaints-title {
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