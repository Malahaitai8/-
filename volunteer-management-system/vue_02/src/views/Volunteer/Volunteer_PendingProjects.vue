<template>
  <div class="registered-activities-page">
    <div class="page-title">
      <span>已报名活动</span>
      <el-button @click="fetchMyActivities" :loading="isLoading" icon="el-icon-refresh" circle title="刷新"></el-button>
    </div>

    <div class="filter-buttons">
      <el-radio-group v-model="currentStatusFilter" size="small" @change="filterActivities">
        <el-radio-button value="全部">全部</el-radio-button>
        <el-radio-button value="待审核">待审核</el-radio-button>
        <el-radio-button value="已通过">已通过</el-radio-button>
        <el-radio-button value="已拒绝">已拒绝</el-radio-button>
        <el-radio-button value="取消报名">取消报名</el-radio-button>
      </el-radio-group>
    </div>

    <el-table :data="filteredActivities" v-loading="isLoading" style="width: 100%" class="activity-table" empty-text="暂无相关活动">
      <el-table-column prop="activityName" label="活动名称" width="220"></el-table-column>
      <el-table-column prop="applicationTime" label="报名时间" width="180">
        <template #default="scope">
          {{ formatDateTime(scope.row.applicationTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="intendedPositionName" label="意向岗位" width="180"></el-table-column>
      <el-table-column prop="applicationStatus" label="报名状态" width="120" align="center">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.applicationStatus)" size="small">
            {{ scope.row.applicationStatus }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" min-width="150">
        <template #default="scope">
          <el-button size="mini" @click="handleViewDetails(scope.row)">查看详情</el-button>
          <el-button
            size="mini"
            type="warning"
            @click="handleWithdraw(scope.row)"
            :disabled="scope.row.applicationStatus !== '待审核'"
          >
            撤回申请
          </el-button>
        </template>
      </el-table-column>
    </el-table>

  </div>
</template>

<script setup>
import {ref, onMounted, computed} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import request from '@/utils/request'; // 你的请求工具
import {useUserStore} from '@/stores/userStore'; // 你的用户状态管理

// --- 响应式数据 ---
const userStore = useUserStore();
const allActivities = ref([]); // 存储所有从后端获取的活动
const filteredActivities = ref([]); // 存储筛选后的活动
const isLoading = ref(false);
const currentStatusFilter = ref('全部');

// --- 方法 ---

const fetchMyActivities = async () => {
  // ✅ 正确：在这里使用 detailedVolunteerInfo
  const volunteerId = userStore.detailedVolunteerInfo.volunteerId;
  if (!volunteerId) {
    // 这个警告现在主要由 onMounted 处理，但保留以防万一
    ElMessage.warning('无法获取您的信息，请尝试重新登录');
    return;
  }

  isLoading.value = true;
  try {
    const res = await request.get(`/api/application/my-activities/${volunteerId}`);
    if (res.code === '200' && res.data) {
      allActivities.value = res.data;
      filterActivities(); // 获取数据后立即执行一次筛选
    } else {
      ElMessage.error(res.msg || '获取活动列表失败');
    }
  } catch (error) {
    console.error("获取已报名活动时出错:", error);
    ElMessage.error('网络请求失败');
  } finally {
    isLoading.value = false;
  }
};

const filterActivities = () => {
  if (currentStatusFilter.value === '全部') {
    filteredActivities.value = allActivities.value;
  } else {
    filteredActivities.value = allActivities.value.filter(
        activity => activity.applicationStatus === currentStatusFilter.value
    );
  }
};

const handleWithdraw = (activity) => {
  ElMessageBox.confirm(
      `确定要撤回对活动《${activity.activityName}》的报名申请吗？`,
      '确认撤回',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  ).then(async () => {
    try {
      const payload = {applicationId: activity.applicationId};
      const res = await request.put('/api/application/withdraw', payload);
      if (res.code === '200') {
        ElMessage.success('撤回成功');
        fetchMyActivities(); // 刷新整个列表
      } else {
        ElMessage.error(res.msg || '撤回失败');
      }
    } catch (error) {
      ElMessage.error('网络请求失败');
    }
  }).catch(() => {
    ElMessage.info('已取消操作');
  });
};

const handleViewDetails = (activity) => {
  ElMessageBox.alert(
      `
      <div><strong>活动名称:</strong> ${activity.activityName}</div>
      <div><strong>活动地点:</strong> ${activity.location}</div>
      <div><strong>意向岗位:</strong> ${activity.intendedPositionName || '未指定'}</div>
      <div><strong>报名状态:</strong> ${activity.applicationStatus}</div>
    `,
      '报名详情',
      {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '关闭'
      }
  );
};

const formatDateTime = (time) => {
  if (!time) return '';
  return new Date(time).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const getStatusTagType = (status) => {
  switch (status) {
    case '已通过':
      return 'success';
    case '待审核':
      return 'primary';
    case '已拒绝':
      return 'danger';
    case '取消报名':
      return 'info';
    default:
      return 'info';
  }
};

// --- 生命周期钩子 ---
onMounted(() => {
  // ✅ 这是最关键的修改：确保能稳定获取到用户信息
  // 检查store中是否已有ID
  if (userStore.detailedVolunteerInfo && userStore.detailedVolunteerInfo.volunteerId) {
    fetchMyActivities();
  } else {
    // 如果没有，可能是因为异步获取还没完成，或者用户直接访问此页面
    // 尝试调用 userStore 中的 action 来获取一次用户信息
    userStore.fetchDetailedVolunteerInfo().then(() => {
      // 再次检查ID是否存在
      if (userStore.detailedVolunteerInfo && userStore.detailedVolunteerInfo.volunteerId) {
        fetchMyActivities();
      } else {
        // 如果还没有，则提示用户
        ElMessage.warning('无法获取您的用户信息，请尝试重新登录');
      }
    }).catch(error => {
      // 处理 fetchDetailedVolunteerInfo 可能发生的错误
      console.error("在 onMounted 中获取用户信息失败:", error);
      ElMessage.error('获取用户信息时出错，请刷新页面或重新登录');
    });
  }
});
</script>

<style scoped>
.registered-activities-page {
  padding: 24px;
  background-color: #fff;
}

.page-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  font-size: 22px;
}

.filter-buttons {
  margin-bottom: 20px;
}

.activity-table {
  border-radius: 4px;
}
</style>