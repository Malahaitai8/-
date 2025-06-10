<template>
  <div class="activities-page">
    <div class="page-title">
      <span>已报名活动</span>
      <div class="header-buttons">
        <el-button type="danger" size="small" class="more-btn-in-menu" @click="goToActivitiesMore" round>
          <el-icon style="vertical-align: middle; margin-right: 4px;"><Plus /></el-icon>
          发现更多活动
        </el-button>
        <el-button type="info" size="small" @click="fetchMyActivities" :loading="isLoading" round style="margin-left: 10px;">
          <el-icon style="vertical-align: middle; margin-right: 4px;"><Refresh /></el-icon>
          刷新列表
        </el-button>
      </div>
    </div>

    <div class="filter-buttons">
      <button @click="applyFilter('全部')" :class="{ active: currentStatusFilter === '全部' }">全部</button>
      <button @click="applyFilter(STATUS.PENDING)" :class="{ active: currentStatusFilter === '待审核' }">待审核</button>
      <button @click="applyFilter(STATUS.APPROVED)" :class="{ active: currentStatusFilter === '已通过' }">已通过</button>
      <button @click="applyFilter(STATUS.REJECTED)" :class="{ active: currentStatusFilter === '已拒绝' }">已拒绝</button>
      <button @click="applyFilter(STATUS.CANCELLED)" :class="{ active: currentStatusFilter === '取消报名' }">取消报名</button>
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
      <img src="https://img.alicdn.com/imgextra/i4/O1CN01v7Qw1B1QwQwQwQw_!!6000000002007-2-tps-200-200.png" alt="empty"></img>
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

    <el-dialog v-model="detailDialogVisible" title="活动详细信息" width="600px">
      <el-descriptions v-if="detailData.activityId" :column="2" border>
        <el-descriptions-item label="活动名称">{{ detailData.activityName }}</el-descriptions-item>
        <el-descriptions-item label="活动ID">{{ detailData.activityId }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ formatDateTime(detailData.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ formatDateTime(detailData.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="活动地点" :span="2">{{ detailData.location }}</el-descriptions-item>
        <el-descriptions-item label="活动状态">
          <el-tag size="small">{{ detailData.activityStatus }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="招募人数">{{ detailData.recruitmentCount }} 人</el-descriptions-item>
        <el-descriptions-item label="已录取人数">{{ detailData.acceptedCount }} 人</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.contactPersonPhone }}</el-descriptions-item>
      </el-descriptions>
      <div v-else v-loading="isDetailLoading" style="min-height: 200px;"></div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

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
const allActivities = ref([]);
const isLoading = ref(false);
const currentStatusFilter = ref('全部');
const pageSize = 5;
const currentPage = ref(1);

// 【新增】为详情弹窗创建状态
const detailDialogVisible = ref(false);
const detailData = ref({});
const isDetailLoading = ref(false);

const STATUS = {
  PENDING: '待审核',
  APPROVED: '已通过',
  REJECTED: '已拒绝',
  CANCELLED: '取消报名',
};

// ... 计算属性和大部分方法保持不变 ...
const filteredActivities = computed(() => {
  if (currentStatusFilter.value === '全部') {
    return allActivities.value;
  }
  return allActivities.value.filter(
    activity => activity.applicationStatus === currentStatusFilter.value
  );
});

const paginatedActivities = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  const end = start + pageSize;
  return filteredActivities.value.slice(start, end);
});

const fetchMyActivities = async () => {
  const volunteerId = userStore.detailedVolunteerInfo.volunteerId;
  if (!volunteerId) return;

  isLoading.value = true;
  try {
    const res = await request.get(`/api/application/my-activities/${volunteerId}`);
    if (res.code === '200' && res.data) {
      allActivities.value = res.data;
    } else {
      ElMessage.error(res.msg || '获取活动列表失败');
    }
  } catch (error) {
    ElMessage.error('网络请求失败');
  } finally {
    isLoading.value = false;
  }
};

const applyFilter = (filterType) => {
  currentStatusFilter.value = filterType;
  currentPage.value = 1;
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
        fetchMyActivities();
      } else {
        ElMessage.error(res.msg || '撤回失败');
      }
    } catch (error) {
      ElMessage.error('网络请求失败');
    }
  }).catch(() => ElMessage.info('已取消操作'));
};


// 【最重要修改】重写 handleViewDetails 方法
const handleViewDetails = async (activity) => {
  detailData.value = {}; // 先清空旧数据
  detailDialogVisible.value = true;
  isDetailLoading.value = true;

  try {
    // 调用后端接口获取活动详情
    const res = await request.get(`/volunteerActivity/${activity.activityId}`);
    if (res.code === '200' && res.data) {
      detailData.value = res.data;
    } else {
      ElMessage.error(res.msg || '获取活动详情失败');
      detailDialogVisible.value = false; // 获取失败则关闭弹窗
    }
  } catch (error) {
    ElMessage.error('网络错误，无法获取活动详情');
    detailDialogVisible.value = false; // 出现网络错误也关闭弹窗
  } finally {
    isDetailLoading.value = false;
  }
};

// ... 其他辅助方法保持不变 ...
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
/* 样式保持不变 */
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
</style>