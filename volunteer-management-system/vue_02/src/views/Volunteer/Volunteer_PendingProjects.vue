<template>
  <div class="activities-page">
    <div class="page-title">
      <span>已报名活动</span>
      <div class="header-buttons">
        <el-button
            type="danger"
            size="small"
            class="more-btn-in-menu"
            @click="goToActivitiesMore"
            round
        >
          <el-icon style="vertical-align: middle; margin-right: 4px;"><Plus /></el-icon>
          发现更多活动
        </el-button>
        <el-button
            type="info"
            size="small"
            @click="fetchMyActivities"
            :loading="isLoading"
            round
            style="margin-left: 10px;"
        >
          <el-icon style="vertical-align: middle; margin-right: 4px;"><Refresh /></el-icon>
          刷新列表
        </el-button>
      </div>
    </div>

    <div class="filter-buttons">
      <button @click="applyFilter('全部')" :class="{ active: currentStatusFilter === '全部' }">全部</button>
      <button @click="applyFilter(STATUS.PENDING)" :class="{ active: currentStatusFilter === STATUS.PENDING }">待审核</button>
      <button @click="applyFilter(STATUS.APPROVED)" :class="{ active: currentStatusFilter === STATUS.APPROVED }">已通过</button>
      <button @click="applyFilter(STATUS.REJECTED)" :class="{ active: currentStatusFilter === STATUS.REJECTED }">已拒绝</button>
      <button @click="applyFilter(STATUS.CANCELLED)" :class="{ active: currentStatusFilter === STATUS.CANCELLED }">取消报名</button>
    </div>

    <el-table :data="paginatedActivities" v-loading="isLoading" style="width: 100%; margin-top: 20px;" header-cell-class-name="table-header" empty-text="暂无相关活动">
      <el-table-column prop="activityName" label="活动名称" align="center" width="220"></el-table-column>
      <el-table-column prop="applicationTime" label="报名时间" align="center" width="180">
        <template #default="scope">
          {{ formatDateTime(scope.row.applicationTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="intendedPositionName" label="意向岗位" align="center" width="180"></el-table-column>
      <el-table-column prop="applicationStatus" label="报名状态" align="center" width="120">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.applicationStatus)" size="small">
            {{ scope.row.applicationStatus }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" min-width="180">
        <template #default="scope">
          <el-button size="small" type="primary" @click="handleViewDetails(scope.row)">
            查看详情
          </el-button>
          <el-button size="small" type="danger" @click="handleWithdraw(scope.row)" :disabled="scope.row.applicationStatus !== '待审核'" style="margin-left: 10px;">
            撤回申请
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-if="!allActivities.length && !isLoading" class="empty-box">
      <img src="https://img.alicdn.com/imgextra/i4/O1CN01v7Qw1B1QwQwQw_!!6000000002007-2-tps-200-200.png" alt="empty"></img>
      <div>暂无活动信息</div>
    </div>

    <el-pagination
        v-if="filteredActivities.length > pageSize"
        style="margin-top: 24px; text-align: right;"
        background
        layout="prev, pager, next, jumper"
        :total="filteredActivities.length"
        :page-size="pageSize"
        v-model:current-page="currentPage"
    ></el-pagination>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useRouter } from 'vue-router';
import { Refresh, Plus } from '@element-plus/icons-vue';
import request from '@/utils/request';
import { useUserStore } from '@/stores/userStore';

const router = useRouter();
const userStore = useUserStore();

// --- 数据状态 ---
const allActivities = ref([]); // 存储从后端获取的【所有】活动
const isLoading = ref(false);
const currentStatusFilter = ref('全部');
const pageSize = 5;
const currentPage = ref(1);

// 定义报名状态常量
const STATUS = {
  PENDING: '待审核',
  APPROVED: '已通过',
  REJECTED: '已拒绝',
  CANCELLED: '取消报名',
};

// --- 【逻辑修正点 1】: 使用计算属性来处理筛选和分页 ---

// 计算属性1：根据当前筛选条件，从 allActivities 中筛选出结果
const filteredActivities = computed(() => {
  if (currentStatusFilter.value === '全部') {
    return allActivities.value;
  }
  return allActivities.value.filter(
    activity => activity.applicationStatus === currentStatusFilter.value
  );
});

// 计算属性2：对上面筛选出的结果，进行分页切割
const paginatedActivities = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  const end = start + pageSize;
  return filteredActivities.value.slice(start, end);
});

// --- 方法 ---

const fetchMyActivities = async () => {
  const volunteerId = userStore.detailedVolunteerInfo.volunteerId;
  if (!volunteerId) return;

  isLoading.value = true;
  try {
    const res = await request.get(`/api/application/my-activities/${volunteerId}`);
    if (res.code === '200' && res.data) {
      allActivities.value = res.data;
      // 数据获取后，计算属性会自动更新，无需手动调用 applyFilter
    } else {
      ElMessage.error(res.msg || '获取活动列表失败');
    }
  } catch (error) {
    ElMessage.error('网络请求失败');
  } finally {
    isLoading.value = false;
  }
};

// 【逻辑修正点 2】: applyFilter 现在只负责修改筛选条件和重置页码
const applyFilter = (filterType) => {
  currentStatusFilter.value = filterType;
  currentPage.value = 1; // 切换筛选时，自动回到第一页
};

const handleWithdraw = (activity) => {
  ElMessageBox.confirm(
    `确定要撤回对活动《${activity.activityName}》的报名申请吗？`, '确认撤回',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    try {
      const payload = { applicationId: activity.applicationId };
      const res = await request.put('/api/application/withdraw', payload);
      if (res.code === '200') {
        ElMessage.success('撤回成功');
        fetchMyActivities(); // 成功后刷新列表
      } else {
        ElMessage.error(res.msg || '撤回失败');
      }
    } catch (error) {
      ElMessage.error('网络请求失败');
    }
  }).catch(() => ElMessage.info('已取消操作'));
};

// 其他方法保持不变
const handleViewDetails = (activity) => {
  ElMessageBox.alert(`...`); // 省略内容
};
const goToActivitiesMore = () => {
  router.push('/volunteer/activities-more');
};
const formatDateTime = (time) => {
  if (!time) return '';
  return new Date(time).toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' });
};
const getStatusTagType = (status) => {
  switch (status) {
    case '已通过': return 'success';
    case '待审核': return 'primary';
    case '已拒绝': return 'danger';
    case '取消报名': return 'info';
    default: return 'info';
  }
};

onMounted(() => {
  if (userStore.detailedVolunteerInfo && userStore.detailedVolunteerInfo.volunteerId) {
    fetchMyActivities();
  } else {
    userStore.fetchDetailedVolunteerInfo().then(() => {
      if (userStore.detailedVolunteerInfo && userStore.detailedVolunteerInfo.volunteerId) {
        fetchMyActivities();
      } else {
        ElMessage.warning('无法获取您的用户信息，请尝试重新登录');
      }
    }).catch(error => {
        ElMessage.error('获取用户信息时出错，请刷新页面或重新登录');
    });
  }
});
</script>

<style scoped>
/* 所有样式保持不变，这里只写几个关键的做示例 */
.activities-page {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.06);
  padding: 0 32px 32px;
  min-height: 400px;
}

.page-title {
  background: #fff0f0;
  color: #ff0000;
  font-weight: bold;
  font-size: 20px;
  padding: 18px 32px;
  margin: 0 -32px 20px -32px;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
  border-bottom: 2px solid #fde2e2;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-buttons {
  display: flex;
  gap: 10px;
  align-items: center;
}

.more-btn-in-menu {
   background: linear-gradient(90deg, #ff4d4f 0%, #ff0000 100%);
  color: #fff;
  border: none;
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
/* ... 其他样式省略 ... */
</style>