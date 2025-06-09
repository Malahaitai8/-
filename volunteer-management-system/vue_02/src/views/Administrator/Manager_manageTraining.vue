<template>
  <div>
    <header class="header">
      <img src="/logo.png" alt="Logo" class="logo" />
      <h1 class="title">志愿者培训管理</h1>
      <span class="welcome">欢迎您：{{ displayNameFromStore }}！</span>
    </header>
    <main class="main-content">
      <div class="table-header">
        <h2>志愿培训审核与状态管理</h2>
        <div class="filter-buttons">
          <button @click="applyFilter('all')" :class="{ active: currentFilter === 'all' }">全部</button>
          <button @click="applyFilter(TRAINING_STATUS.PENDING)" :class="{ active: currentFilter === TRAINING_STATUS.PENDING }">待审核</button>
          <button @click="applyFilter(TRAINING_STATUS.APPROVED)" :class="{ active: currentFilter === TRAINING_STATUS.APPROVED }">审核通过</button>
          <button @click="applyFilter(TRAINING_STATUS.REJECTED)" :class="{ active: currentFilter === TRAINING_STATUS.REJECTED }">审核不通过</button>
          <button @click="applyFilter(TRAINING_STATUS.ONGOING)" :class="{ active: currentFilter === TRAINING_STATUS.ONGOING }">进行中</button>
          <button @click="applyFilter(TRAINING_STATUS.ENDED)" :class="{ active: currentFilter === TRAINING_STATUS.ENDED }">已结束</button>
          <button @click="applyFilter(TRAINING_STATUS.DISABLED)" :class="{ active: currentFilter === TRAINING_STATUS.DISABLED }">已停用</button>
        </div>
      </div>
      <div class="table-container">
        <table>
          <thead>
          <tr>
            <th>序号</th>
            <th>培训信息</th>
            <th>培训状态</th>
            <th>操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-if="isLoading && displayedTrainings.length === 0">
            <td colspan="4" style="text-align: center;">数据加载中...</td>
          </tr>
          <tr v-else-if="displayedTrainings.length === 0 && !isLoading">
            <td colspan="4" style="text-align: center;">暂无数据显示</td>
          </tr>
          <tr v-for="(training, index) in displayedTrainings" :key="training.trainingId">
            <td>{{ index + 1 }}</td>
            <td>
              <p><strong>培训ID：</strong>{{ training.trainingId }}</p>
              <p><strong>名称：</strong>{{ training.trainingName }}</p>
              <p><strong>组织ID：</strong>{{ training.orgId }} ({{ training.orgName || 'N/A' }})</p>
              <p><strong>主题：</strong>{{ training.theme || 'N/A' }}</p>
              <p><strong>地点：</strong>{{ training.location }}</p>
              <p><strong>时间：</strong>{{ formatDate(training.startTime) }} 至 {{ formatDate(training.endTime) }}</p>
              <p><strong>招募人数：</strong>{{ training.recruitmentCount }}</p>
              <p><strong>联系方式：</strong>{{ training.contactPersonPhone }}</p>
            </td>
            <td :class="getStatusClass(training.trainingStatus)">{{ training.trainingStatus }}</td>
            <td>
              <button @click="viewDetails(training)" class="action-btn view-btn">查看详情</button>
              <button
                  v-if="training.trainingStatus === TRAINING_STATUS.PENDING"
                  @click="handleApprove(training)"
                  class="action-btn approve-btn">
                通过审核
              </button>
              <button
                  v-if="training.trainingStatus === TRAINING_STATUS.APPROVED"
                  @click="handleReject(training)"
                  class="action-btn reject-btn">
                驳回审核
              </button>
              <button
                  v-if="training.trainingStatus === TRAINING_STATUS.ONGOING || training.trainingStatus === TRAINING_STATUS.APPROVED"
                  @click="handleDisable(training)"
                  class="action-btn disable-btn">
                停用培训
              </button>
              <button
                  v-if="training.trainingStatus === TRAINING_STATUS.DISABLED"
                  @click="handleEnable(training)"
                  class="action-btn enable-btn">
                重新启用
              </button>
              <button
                  v-if="training.trainingStatus === TRAINING_STATUS.REJECTED"
                  @click="handleReReview(training)"
                  class="action-btn rereview-btn">
                重新审核
              </button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </main>
  </div>
</template>

<script setup>
import {ref, computed, onMounted} from 'vue';
import {useAdminStore} from "@/stores/adminStore.js";
import {ElMessage, ElMessageBox} from 'element-plus';
import request from '@/utils/request';

const adminStore = useAdminStore();

const displayNameFromStore = computed(() => adminStore.displayName || '管理员');
const isAuthenticated = computed(() => adminStore.isAuthenticated);

const allTrainings = ref([]);
const displayedTrainings = ref([]);
const isLoading = ref(false);
const currentFilter = ref('all');

const TRAINING_STATUS = {
  PENDING: '待审核',
  APPROVED: '审核通过',
  REJECTED: '审核不通过',
  ONGOING: '进行中',
  ENDED: '已结束',
  DISABLED: '已停用'
};

// 简单日期格式化函数
const formatDate = (datetimeStr) => {
  if (!datetimeStr) return 'N/A';
  const date = new Date(datetimeStr);
  if (isNaN(date.getTime())) return '日期无效';
  try {
    return date.toLocaleString('zh-CN', {
      year: 'numeric', month: '2-digit', day: '2-digit',
      hour: '2-digit', minute: '2-digit',
      hour12: false
    }).replace(/\//g, '-');
  } catch (e) {
    console.error("Error formatting date:", datetimeStr, e);
    return datetimeStr;
  }
};

onMounted(async () => {
  console.log('Manager_manageTraining.vue: Component mounted.');
  if (!adminStore.loggedInAdmin && typeof adminStore.initializeStore === 'function') {
    await adminStore.initializeStore();
  }

  if (isAuthenticated.value) {
    console.log('Manager_manageTraining.vue: Admin is authenticated.');
    if ((!adminStore.detailedAdminInfo || !adminStore.detailedAdminInfo.adminId) && typeof adminStore.fetchDetailedAdminInfo === 'function') {
      try {
        await adminStore.fetchDetailedAdminInfo(adminStore.loggedInAdmin?.adminId);
      } catch (error) {
        console.error("Error fetching detailed admin info:", error);
      }
    }
    await fetchAllTrainings();
  } else {
    ElMessage.error('请先登录！');
  }
});

const fetchAllTrainings = async () => {
  isLoading.value = true;
  // let params = {}; // Removed, as filtering is now client-side
  // if (currentFilter.value !== 'all') { // Removed, as filtering is now client-side
  //   params.trainingStatus = currentFilter.value; // Removed, as filtering is now client-side
  // }

  console.log('Fetching trainings from /volunteerTraining/all');
  try {
    const response = await request.get('/volunteerTraining/all');
    console.log('API Response for /volunteerTraining/all:', response);

    if (response && response.code === "200" && Array.isArray(response.data)) {
      allTrainings.value = response.data.map(trn => ({
        trainingId: trn.trainingId,
        orgId: trn.orgId,
        trainingName: trn.trainingName,
        theme: trn.theme,
        startTime: trn.startTime,
        endTime: trn.endTime,
        location: trn.location,
        recruitmentCount: trn.recruitmentCount,
        trainingStatus: trn.trainingStatus || TRAINING_STATUS.PENDING,
        creationTime: trn.creationTime,
        reviewerAdminId: trn.reviewerAdminId,
        contactPersonPhone: trn.contactPersonPhone,
        trainingRating: trn.trainingRating,
        isRatingAggregated: trn.isRatingAggregated,
      }));
      filterTrainings(); // Call filter after fetching all data
      console.log('Fetched trainings from DB:', allTrainings.value);
    } else {
      ElMessage.error(response.msg || '获取志愿培训列表失败');
      console.error('Failed to fetch trainings, unexpected response structure or code:', response);
      allTrainings.value = [];
      filterTrainings(); // Ensure displayedTrainings is updated even on error
    }
  } catch (error) {
    console.error("Error fetching trainings API:", error);
    const errorMessage = error.response && error.response.data && error.response.data.msg
                         ? error.response.data.msg
                         : '请求志愿培训列表失败，请检查网络或服务器日志。';
    ElMessage.error(errorMessage);
    allTrainings.value = [];
    filterTrainings(); // Ensure displayedTrainings is updated even on error
  } finally {
    isLoading.value = false;
  }
};

const filterTrainings = () => {
  if (currentFilter.value === 'all') {
    displayedTrainings.value = [...allTrainings.value];
  } else {
    displayedTrainings.value = allTrainings.value.filter(trn => trn.trainingStatus === currentFilter.value);
  }
};

const applyFilter = (filterType) => {
  currentFilter.value = filterType;
  filterTrainings(); // Filter immediately after changing filter type
};

const getStatusClass = (status) => {
  switch (status) {
    case TRAINING_STATUS.PENDING: return 'status-pending';
    case TRAINING_STATUS.APPROVED: return 'status-approved';
    case TRAINING_STATUS.REJECTED: return 'status-rejected';
    case TRAINING_STATUS.ONGOING: return 'status-ongoing';
    case TRAINING_STATUS.ENDED: return 'status-ended';
    case TRAINING_STATUS.DISABLED: return 'status-disabled';
    default: return 'status-unknown';
  }
};

const viewDetails = (training) => {
  ElMessageBox.alert(`
    <div style="text-align: left; font-size: 14px; line-height: 1.6;">
      <p><strong>培训ID:</strong> ${training.trainingId || 'N/A'}</p>
      <p><strong>培训名称:</strong> ${training.trainingName || 'N/A'}</p>
      <p><strong>组织ID:</strong> ${training.orgId || 'N/A'} (${training.orgName || 'N/A'})</p>
      <p><strong>主题:</strong> ${training.theme || 'N/A'}</p>
      <p><strong>开始时间:</strong> ${formatDate(training.startTime)}</p>
      <p><strong>结束时间:</strong> ${formatDate(training.endTime)}</p>
      <p><strong>地点:</strong> ${training.location || 'N/A'}</p>
      <p><strong>招募人数:</strong> ${training.recruitmentCount === null || training.recruitmentCount === undefined ? 'N/A' : training.recruitmentCount}</p>
      <p><strong>培训状态:</strong> ${training.trainingStatus || 'N/A'}</p>
      <p><strong>创建时间:</strong> ${formatDate(training.creationTime)}</p>
      <p><strong>审核管理员ID:</strong> ${training.reviewerAdminId || 'N/A'}</p>
      <p><strong>联系方式：</strong>${training.contactPersonPhone || 'N/A'}</p>
      <p><strong>培训评分:</strong> ${training.trainingRating === null || training.trainingRating === undefined ? 'N/A' : training.trainingRating}</p>
      <p><strong>评分已汇总:</strong> ${training.isRatingAggregated || '否'}</p>
    </div>
  `, `培训 "${training.trainingName || training.trainingId}" 的详细信息`, {
    dangerouslyUseHTMLString: true,
    confirmButtonText: '关闭',
    customClass: 'detail-message-box'
  });
};

const updateTrainingStatusApi = async (trainingId, newStatus, operationName, remarks = null) => {
  isLoading.value = true;
  try {
    const payload = {
      trainingId: trainingId,
      newStatus: newStatus,
      reviewerAdminId: adminStore.detailedAdminInfo?.adminId || 'ADMIN_SYS_FALLBACK'
    };

    console.log(`Calling API PUT /volunteerTraining/review with payload:`, payload);

    const response = await request.put(`/volunteerTraining/review`, payload);

    if (response && response.code === "200") { // Changed from `response.code === 200` to `response.code === "200"` for string comparison
      ElMessage.success(`${operationName}成功！`);
      await fetchAllTrainings();
      return true;
    } else {
      ElMessage.error(response.msg || `${operationName}失败`);
      console.error('Failed to update training status, API response:', response);
      return false;
    }
  } catch (error) {
    console.error(`Error during ${operationName} for ${trainingId}:`, error);
    const errorMsg = error.response?.data?.msg || error.message || `请求${operationName}失败，请检查网络连接或服务器日志。`;
    ElMessage.error(errorMsg);
    return false;
  } finally {
    isLoading.value = false;
  }
};

const handleApprove = (training) => {
  ElMessageBox.confirm(`确定要通过培训 "${training.trainingName}" (${training.trainingId}) 的审核吗？`, `确认通过审核`, {
    confirmButtonText: '确定通过',
    cancelButtonText: '取消',
    type: 'success',
  }).then(async () => {
    await updateTrainingStatusApi(training.trainingId, TRAINING_STATUS.APPROVED, '通过审核');
  }).catch(() => {
    ElMessage.info('已取消操作');
  });
};

const handleReject = (training) => {
  ElMessageBox.prompt('请输入驳回审核的理由（可选）：', `确认驳回培训 "${training.trainingName}"`, {
    confirmButtonText: '确定驳回',
    cancelButtonText: '取消',
    type: 'warning',
    inputType: 'textarea',
    inputPlaceholder: '驳回理由（选填，200字以内）'
  }).then(async ({value: reason}) => {
    await updateTrainingStatusApi(training.trainingId, TRAINING_STATUS.REJECTED, '驳回审核', reason);
  }).catch(() => {
    ElMessage.info('已取消操作');
  });
};

const handleDisable = (training) => {
  ElMessageBox.confirm(`确定要停用培训 "${training.trainingName}" (${training.trainingId}) 吗？`, `确认停用培训`, {
    confirmButtonText: '确定停用',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    await updateTrainingStatusApi(training.trainingId, TRAINING_STATUS.DISABLED, '停用培训');
  }).catch(() => {
    ElMessage.info('已取消操作');
  });
};

const handleEnable = (training) => {
  ElMessageBox.confirm(`确定要重新启用培训 "${training.trainingName}" (${training.trainingId}) 吗？`, `确认启用培训`, {
    confirmButtonText: '确定启用',
    cancelButtonText: '取消',
    type: 'info',
  }).then(async () => {
    // Note: Re-enabling usually sets status to APPROVED or PENDING, depending on business logic.
    // Assuming APPROVED here as per your example.
    await updateTrainingStatusApi(training.trainingId, TRAINING_STATUS.APPROVED, '启用培训');
  }).catch(() => {
    ElMessage.info('已取消操作');
  });
};

const handleReReview = (training) => {
  ElMessageBox.confirm(
      `对培训 "${training.trainingName}" (${training.trainingId}) 的 "${TRAINING_STATUS.REJECTED}" 状态进行操作：`,
      '重新审核/修改状态',
      {
        distinguishCancelAndClose: true,
        confirmButtonText: '通过审核',
        cancelButtonText: '仍驳回/保持',
        showClose: false,
        callback: async (action) => {
          if (action === 'confirm') {
            await updateTrainingStatusApi(training.trainingId, TRAINING_STATUS.APPROVED, '通过审核(重新审核)');
          } else if (action === 'cancel') {
            ElMessage.info('操作已取消，状态保持不变。');
          }
        }
      }
  ).catch(() => {
    ElMessage.info('操作已取消。');
  });
};
</script>

<style scoped>
/* 页眉样式 */
.header {
  background-color: red;
  display: flex;
  align-items: center;
  padding: 10px 20px;
  color: white;
}

.logo {
  height: 50px;
  margin-right: 15px;
}

.title {
  color: white;
  font-weight: bold;
  margin: 0 30px;
  font-size: 1.7em;
}

.welcome {
  color: white;
  font-size: 14px;
  margin-left: auto;
}

/* 主体内容样式 */
.main-content {
  padding: 20px;
  background-image: url('/bg.png');
  background-size: cover;
  background-position: center;
  min-height: calc(100vh - 70px);
}

/* 表格标题和筛选按钮样式 */
.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background-color: rgba(255, 255, 255, 0.9);
  padding: 15px;
  border-radius: 8px;
}

.table-header h2 {
  margin: 0;
  color: #333;
}

/* 筛选按钮容器样式 */
.filter-buttons {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.filter-buttons button {
  background-color: red;
  color: white;
  padding: 8px 12px;
  border-radius: 5px;
  font-weight: 500;
  border: none;
  cursor: pointer;
  font-size: 0.9em;
}

.filter-buttons button.active {
  background-color: #b71c1c;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.2);
}

.filter-buttons button:hover:not(.active) {
  background-color: #e53935;
}

.table-container {
  max-height: calc(100vh - 200px);
  overflow-y: auto;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.12), 0 1px 2px rgba(0, 0, 0, 0.24);
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  border: 1px solid #ccc;
  padding: 10px 12px;
  text-align: left;
  vertical-align: middle;
}

th {
  background-color: #f2f2f2;
  font-weight: bold;
}

tr:hover {
  background-color: #f9f9f9;
}

td p {
  margin: 3px 0;
  line-height: 1.5;
}

td p strong {
  color: #333;
  margin-right: 5px;
}

/* 状态颜色类 */
.status-pending {
  color: #f57c00;
  font-weight: bold;
}

.status-approved {
  color: #38761d;
  font-weight: bold;
}

.status-rejected {
  color: #c9302c;
  font-weight: bold;
}

.status-ongoing {
  color: #1976d2;
  font-weight: bold;
}

.status-ended {
  color: #616161;
  font-weight: bold;
}

.status-disabled {
  color: #9e9e9e;
  font-weight: bold;
  font-style: italic;
}

.status-unknown {
  color: #777;
  font-style: italic;
}

/* 操作按钮样式 */
.action-btn {
  margin: 2px;
  padding: 5px 10px;
  background-color: red;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9em;
  transition: background-color 0.2s ease;
}

.action-btn:hover:not(:disabled) {
  background-color: #b71c1c;
}

.action-btn:disabled {
  background-color: #ccc !important;
  cursor: not-allowed;
  opacity: 0.7;
}

/* 按钮特定颜色 */
.action-btn.view-btn {
  background-color: #17a2b8;
}
.action-btn.view-btn:hover:not(:disabled) {
  background-color: #138496;
}

.action-btn.approve-btn {
  background-color: #28a745;
}
.action-btn.approve-btn:hover:not(:disabled) {
  background-color: #218838;
}

.action-btn.reject-btn {
  background-color: #dc3545;
}
.action-btn.reject-btn:hover:not(:disabled) {
  background-color: #c82333;
}

.action-btn.disable-btn {
  background-color: #ffc107;
  color: #212529;
}
.action-btn.disable-btn:hover:not(:disabled) {
  background-color: #e0a800;
}

.action-btn.enable-btn {
  background-color: #007bff;
}
.action-btn.enable-btn:hover:not(:disabled) {
  background-color: #0069d9;
}

.action-btn.rereview-btn {
  background-color: #6c757d;
}
.action-btn.rereview-btn:hover:not(:disabled) {
  background-color: #5a6268;
}


:global(.detail-message-box) {
  max-width: 650px;
  width: auto;
}

:global(.detail-message-box .el-message-box__content) {
  max-height: 500px;
  overflow-y: auto;
}
</style>