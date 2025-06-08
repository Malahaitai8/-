<template>
  <div class="reviews-page">
<!--    &lt;!&ndash; 红色标题栏 &ndash;&gt;-->
<!--    <div class="reviews-title">-->
<!--      <span>我的评价</span>-->
<!--    </div>-->

    <!-- 选项卡 -->
    <el-tabs v-model="activeTab" class="reviews-tabs" @tab-click="handleTabClick">
      <!-- 志愿活动评价 -->
      <el-tab-pane label="志愿活动评价" name="activity">
        <div v-if="activityReviews.length > 0">
          <el-table :data="paginatedActivityReviews" border style="width: 100%" header-cell-class-name="table-header">
            <el-table-column prop="orgName" label="组织名称" align="center" />
            <el-table-column prop="projectName" label="活动名称" align="center" />
            <el-table-column prop="rating" label="评价分数" align="center">
              <template #default="scope">
                <el-rate v-model="scope.row.rating" disabled show-score text-color="#ff9900" score-template="{value} 分" />
              </template>
            </el-table-column>
            <el-table-column prop="reviewTime" label="评价时间" align="center" />
          </el-table>
          <el-pagination
              v-if="activityReviews.length > activityPagination.pageSize"
              style="margin-top: 24px; text-align: right;"
              background
              layout="prev, pager, next, jumper"
              :total="activityReviews.length"
              :page-size="activityPagination.pageSize"
              v-model:current-page="activityPagination.currentPage"
          />
        </div>
        <div v-else class="empty-box">
          <img src="https://img.alicdn.com/imgextra/i4/O1CN01v7Qw1B1QwQwQwQwQw_!!6000000002007-2-tps-200-200.png" alt="empty" />
          <div>暂无活动评价</div>
        </div>
      </el-tab-pane>

      <!-- 培训评价 -->
      <el-tab-pane label="培训评价" name="training">
        <div v-if="trainingReviews.length > 0">
          <el-table :data="paginatedTrainingReviews" border style="width: 100%" header-cell-class-name="table-header">
            <el-table-column prop="orgName" label="组织名称" align="center" />
            <el-table-column prop="projectName" label="培训名称" align="center" />
            <el-table-column prop="rating" label="评价分数" align="center">
              <template #default="scope">
                <el-rate v-model="scope.row.rating" disabled show-score text-color="#ff9900" score-template="{value} 分" />
              </template>
            </el-table-column>
            <el-table-column prop="reviewTime" label="评价时间" align="center" />
          </el-table>
          <el-pagination
              v-if="trainingReviews.length > trainingPagination.pageSize"
              style="margin-top: 24px; text-align: right;"
              background
              layout="prev, pager, next, jumper"
              :total="trainingReviews.length"
              :page-size="trainingPagination.pageSize"
              v-model:current-page="trainingPagination.currentPage"
          />
        </div>
        <div v-else class="empty-box">
          <img src="https://img.alicdn.com/imgextra/i4/O1CN01v7Qw1B1QwQwQwQwQw_!!6000000002007-2-tps-200-200.png" alt="empty" />
          <div>暂无培训评价</div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useUserStore } from '@/stores/userStore'; // 确保路径正确
import request from '@/utils/request';
import { ElMessage } from 'element-plus';

const userStore = useUserStore();
const activeTab = ref('activity');

// 志愿活动评价数据
const activityReviews = ref([]);
const activityPagination = reactive({
  currentPage: 1,
  pageSize: 5
});

// 培训评价数据
const trainingReviews = ref([]);
const trainingPagination = reactive({
  currentPage: 1,
  pageSize: 5
});

// 计算当前页的活动评价数据
const paginatedActivityReviews = computed(() => {
  const start = (activityPagination.currentPage - 1) * activityPagination.pageSize;
  return activityReviews.value.slice(start, start + activityPagination.pageSize);
});

// 计算当前页的培训评价数据
const paginatedTrainingReviews = computed(() => {
  const start = (trainingPagination.currentPage - 1) * trainingPagination.pageSize;
  return trainingReviews.value.slice(start, start + trainingPagination.pageSize);
});

// 获取志愿活动评价
const fetchActivityReviews = async (volunteerId) => {
  try {
    // 调用新的后端接口
    const res = await request.get(`/volunteer/${volunteerId}/reviews/activities`);
    if (res.code === '200' && Array.isArray(res.data)) {
      activityReviews.value = res.data;
    } else {
      ElMessage.error(res.msg || '获取活动评价失败');
    }
  } catch (error) {
    console.error("获取活动评价失败:", error);
    ElMessage.error('网络错误，无法获取活动评价');
  }
};

// 获取培训评价
const fetchTrainingReviews = async (volunteerId) => {
  try {
    // 调用新的后端接口
    const res = await request.get(`/volunteer/${volunteerId}/reviews/trainings`);
    if (res.code === '200' && Array.isArray(res.data)) {
      trainingReviews.value = res.data;
    } else {
      ElMessage.error(res.msg || '获取培训评价失败');
    }
  } catch (error) {
    console.error("获取培训评价失败:", error);
    ElMessage.error('网络错误，无法获取培训评价');
  }
};

// 切换选项卡时的处理
const handleTabClick = (tab) => {
  // 当前无需特殊处理，但保留此函数以便未来扩展
};

// 组件加载时执行
onMounted(() => {
  const volunteerId = userStore.detailedVolunteerInfo?.volunteerId;
  if (volunteerId) {
    fetchActivityReviews(volunteerId);
    fetchTrainingReviews(volunteerId);
  } else {
    // 这里可以加一个监听器，以防 userStore 数据加载慢
    // 如果用户信息是异步获取的，这个 onMounted 可能会先执行
    const unwatch = userStore.$subscribe((mutation, state) => {
        const newVolunteerId = state.detailedVolunteerInfo?.volunteerId;
        if(newVolunteerId) {
            fetchActivityReviews(newVolunteerId);
            fetchTrainingReviews(newVolunteerId);
            unwatch(); // 获取到ID后注销监听，避免重复调用
        }
    });
    console.warn("未立即找到志愿者ID，已设置监听器。");
  }
});
</script>

<style scoped>
.reviews-page {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.06);
  padding: 0 32px 32px; /* 调整内边距 */
  min-height: 400px;
}

.reviews-title {
  background: #fff0f0;
  color: #c32f1b; /* 统一主题红色 */
  font-weight: bold;
  font-size: 20px;
  padding: 18px 32px;
  /* 调整标题栏样式，使其不与Tabs重叠 */
  margin: 0 -32px 20px -32px;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
  border-bottom: 2px solid #fde2e2;
}

.reviews-tabs {
  width: 100%;
}

.table-header {
  background: #f8f8f9 !important;
  color: #515a6e !important;
  font-weight: bold;
}

.empty-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-top: 60px;
  color: #aaa;
  font-size: 16px;
  min-height: 200px;
}

.empty-box img {
  width: 80px;
  margin-bottom: 12px;
  opacity: 0.6;
}

/* 针对el-tabs的样式微调 */
:deep(.el-tabs__header) {
  margin-bottom: 20px;
}

:deep(.el-tabs__item.is-active) {
  color: #c32f1b;
}

:deep(.el-tabs__active-bar) {
  background-color: #c32f1b;
}
</style>
