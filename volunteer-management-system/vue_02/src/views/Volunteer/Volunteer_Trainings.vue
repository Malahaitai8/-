<template>
  <div class="training-page">
    <!-- 红色标题栏 -->
    <div class="training-title">
      <span>我的培训</span>
    </div>
    <!-- 培训表格 -->
    <el-table
        v-if="pageData.length"
        :data="pageData"
        border
        style="width: 100%; margin-top: 20px;"
        header-cell-class-name="table-header"
    >
      <el-table-column prop="topic" label="培训主题" align="center"/>
      <el-table-column prop="team" label="队伍名称" align="center"/>
      <el-table-column prop="startDate" label="培训开始日期" align="center"/>
      <el-table-column prop="endDate" label="培训结束日期" align="center"/>
      <el-table-column prop="duration" label="培训时长" align="center"/>
      <el-table-column prop="count" label="培训人数" align="center"/>
    </el-table>
    <!-- 空数据提示 -->
    <div v-else class="empty-box">
      <img src="https://img.alicdn.com/imgextra/i4/O1CN01v7Qw1B1QwQwQwQwQw_!!6000000002007-2-tps-200-200.png"
           alt="empty"/>
      <div>暂无培训信息</div>
    </div>
    <!-- 分页器 -->
    <el-pagination
        v-if="trainings.length > pageSize"
        style="margin-top: 24px; text-align: right;"
        background
        layout="prev, pager, next, jumper"
        :total="trainings.length"
        :page-size="pageSize"
        v-model:current-page="currentPage"
    />
  </div>
</template>

<script>
import {ref, computed} from 'vue'

export default {
  name: 'Training',
  setup() {
    // 模拟培训数据
    const trainings = ref([
      {
        topic: '消防安全知识培训',
        team: '社区志愿服务队',
        startDate: '2024-04-01',
        endDate: '2024-04-01',
        duration: '2小时',
        count: 30
      },
      {
        topic: '急救技能培训',
        team: '红十字志愿队',
        startDate: '2024-03-15',
        endDate: '2024-03-15',
        duration: '3小时',
        count: 25
      },
      {
        topic: '防疫知识讲座',
        team: '青年志愿者协会',
        startDate: '2024-02-20',
        endDate: '2024-02-20',
        duration: '1.5小时',
        count: 40
      },
      {
        topic: '心理健康辅导',
        team: '心理志愿服务队',
        startDate: '2024-01-10',
        endDate: '2024-01-10',
        duration: '2小时',
        count: 18
      },
      {
        topic: '环保知识普及',
        team: '环保志愿团',
        startDate: '2023-12-05',
        endDate: '2023-12-05',
        duration: '2小时',
        count: 22
      },
      {
        topic: '助残服务培训',
        team: '助残志愿队',
        startDate: '2023-11-18',
        endDate: '2023-11-18',
        duration: '2小时',
        count: 15
      }
    ])
    // 分页相关
    const pageSize = 5
    const currentPage = ref(1)
    const pageData = computed(() => {
      const start = (currentPage.value - 1) * pageSize
      return trainings.value.slice(start, start + pageSize)
    })

    return {
      trainings,
      pageData,
      pageSize,
      currentPage
    }
  }
}
</script>

<style scoped>
.training-page {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.06);
  padding: 0 0 32px 0;
  min-height: 400px;
}

.training-title {
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