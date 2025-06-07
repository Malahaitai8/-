<template>
  <div class="reviews-page">
    <!-- 红色标题栏 -->
    <div class="reviews-title">
      <span>我的评价</span>
    </div>
    <!-- 评价表格 -->
    <el-table
      v-if="pageData.length"
      :data="pageData"
      border
      style="width: 100%; margin-top: 20px;"
      header-cell-class-name="table-header"
    >
      <el-table-column prop="type" label="评价类型" align="center" />
      <el-table-column prop="group" label="团体名称" align="center" />
      <el-table-column prop="score" label="评价分数" align="center">
        <template #default="scope">
          <span v-if="scope.row.score">{{ scope.row.score }} 分</span>
          <span v-else>未评分</span>
        </template>
      </el-table-column>
      <el-table-column prop="project" label="项目名称" align="center" />
      <el-table-column prop="date" label="评价时间" align="center" />
    </el-table>
    <!-- 空数据提示 -->
    <div v-else class="empty-box">
      <img src="https://img.alicdn.com/imgextra/i4/O1CN01v7Qw1B1QwQwQwQwQw_!!6000000002007-2-tps-200-200.png" alt="empty" />
      <div>暂无评价</div>
    </div>
    <!-- 分页器 -->
    <el-pagination
      v-if="reviews.length > pageSize"
      style="margin-top: 24px; text-align: right;"
      background
      layout="prev, pager, next, jumper"
      :total="reviews.length"
      :page-size="pageSize"
      v-model:current-page="currentPage"
    />
  </div>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'Reviews',
  setup() {
    // 模拟评价数据
    const reviews = ref([
      {
        type: '志愿活动',
        group: '青年志愿者协会',
        content: '服务态度非常好，积极参与各项活动。',
        project: '社区卫生宣传',
        date: '2024-05-01 10:23',
        score: 9
      },
      {
        type: '志愿活动',
        group: '红十字会',
        content: '工作认真负责，团队协作能力强。',
        project: '无偿献血活动',
        date: '2024-04-15 14:10',
        score: 10
      },
      {
        type: '志愿活动',
        group: '环保志愿团',
        content: '热心公益，表现优秀。',
        project: '城市清洁行动',
        date: '2024-03-20 09:00',
        score: 8
      },
      {
        type: '志愿活动',
        group: '助残志愿队',
        content: '关爱弱势群体，服务细致周到。',
        project: '助残日活动',
        date: '2024-02-28 16:45',
        score: 9
      },
      {
        type: '培训',
        group: '社区志愿服务中心',
        content: '积极参与社区服务，获得一致好评。',
        project: '社区防疫宣传',
        date: '2024-01-18 11:30',
        score: 10
      },
      {
        type: '培训',
        group: '社区志愿服务中心',
        content: '积极参与社区服务，获得一致好评。',
        project: '社区防疫宣传',
        date: '2024-01-18 11:30',
        score: 7
      },
      {
        type: '培训',
        group: '社区志愿服务中心',
        content: '积极参与社区服务，获得一致好评。',
        project: '社区防疫宣传',
        date: '2024-01-18 11:30',
        score: 8
      }
    ])
    // 分页相关
    const pageSize = 5
    const currentPage = ref(1)
    const pageData = computed(() => {
      const start = (currentPage.value - 1) * pageSize
      return reviews.value.slice(start, start + pageSize)
    })

    return {
      reviews,
      pageData,
      pageSize,
      currentPage
    }
  }
}
</script>

<style scoped>
.reviews-page {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.06);
  padding: 0 0 32px 0;
  min-height: 400px;
}
.reviews-title {
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