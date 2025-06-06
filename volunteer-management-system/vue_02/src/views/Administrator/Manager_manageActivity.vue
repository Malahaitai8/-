<template>
  <!-- 页面根容器 -->
  <div>
    <!-- 页眉部分，包含 logo、标题和欢迎信息 -->
    <header class="header">
      <img src="/logo.png" alt="Logo" class="logo">
      <h1 class="title">志愿活动管理</h1>
      <span class="welcome">欢迎您：{{ displayNameFromStore }}！</span>
    </header>
    <!-- 主体部分，用于展示表格 -->
    <main class="main-content">
      <div class="table-header">
        <h2>志愿活动审核与状态管理</h2>
        <div class="filter-buttons">
          <button @click="applyFilter('all')" :class="{ active: currentFilter === 'all' }">全部</button>
          <button @click="applyFilter(ACTIVITY_STATUS.PENDING)" :class="{ active: currentFilter === ACTIVITY_STATUS.PENDING }">待审核</button>
          <button @click="applyFilter(ACTIVITY_STATUS.APPROVED)" :class="{ active: currentFilter === ACTIVITY_STATUS.APPROVED }">审核通过</button>
          <button @click="applyFilter(ACTIVITY_STATUS.REJECTED)" :class="{ active: currentFilter === ACTIVITY_STATUS.REJECTED }">审核不通过</button>
          <button @click="applyFilter(ACTIVITY_STATUS.ONGOING)" :class="{ active: currentFilter === ACTIVITY_STATUS.ONGOING }">进行中</button>
          <button @click="applyFilter(ACTIVITY_STATUS.ENDED)" :class="{ active: currentFilter === ACTIVITY_STATUS.ENDED }">已结束</button>
          <button @click="applyFilter(ACTIVITY_STATUS.DISABLED)" :class="{ active: currentFilter === ACTIVITY_STATUS.DISABLED }">已停用</button>
        </div>
      </div>
      <!-- 可滚动的表格容器 -->
      <div class="table-container">
        <table>
          <thead>
          <tr>
            <th>序号</th>
            <th>活动信息</th>
            <th>活动状态</th>
            <th>操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-if="isLoading && displayedActivities.length === 0">
            <td colspan="4" style="text-align: center;">数据加载中...</td>
          </tr>
          <tr v-else-if="displayedActivities.length === 0 && !isLoading">
            <td colspan="4" style="text-align: center;">暂无数据显示</td>
          </tr>
          <tr v-for="(activity, index) in displayedActivities" :key="activity.activityId">
            <td>{{ index + 1 }}</td>
            <td>
              <p><strong>活动ID：</strong>{{ activity.activityId }}</p>
              <p><strong>名称：</strong>{{ activity.activityName }}</p>
              <p><strong>组织ID：</strong>{{ activity.orgId }}</p>
              <p><strong>地点：</strong>{{ activity.location }}</p>
              <p><strong>时间：</strong>{{ formatDate(activity.startTime) }} 至 {{ formatDate(activity.endTime) }}</p>
              <p><strong>招募/已录：</strong>{{ activity.recruitmentCount }} / {{ activity.acceptedCount }}</p>
              <p><strong>联系方式：</strong>{{ activity.contactPersonPhone }}</p>
            </td>
            <td :class="getStatusClass(activity.activityStatus)">{{ activity.activityStatus }}</td>
            <td>
              <button @click="viewDetails(activity)" class="action-btn view-btn">查看详情</button>
              <button
                  v-if="activity.activityStatus === ACTIVITY_STATUS.PENDING"
                  @click="handleApprove(activity)"
                  class="action-btn approve-btn">
                通过审核
              </button>
              <button
                  v-if="activity.activityStatus === ACTIVITY_STATUS.APPROVED"
                  @click="handleReject(activity)"
                  class="action-btn reject-btn">
                驳回审核
              </button>
              <button
                  v-if="activity.activityStatus === ACTIVITY_STATUS.ONGOING"
                  @click="handleDisable(activity)"
                  class="action-btn disable-btn">
                停用活动
              </button>
              <button
                  v-if="activity.activityStatus === ACTIVITY_STATUS.DISABLED"
                  @click="handleEnable(activity)"
                  class="action-btn enable-btn">
                重新启用
              </button>
              <button
                  v-if="activity.activityStatus === ACTIVITY_STATUS.REJECTED"
                  @click="handleReReview(activity)"
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
import {useAdminStore} from "@/stores/adminStore.js"; // 确保路径正确
import {ElMessage, ElMessageBox} from 'element-plus';
import request from '@/utils/request'; // 您的请求工具

const adminStore = useAdminStore();

const displayNameFromStore = computed(() => adminStore.displayName || '管理员');
const isAuthenticated = computed(() => adminStore.isAuthenticated);

const allActivities = ref([]);
const displayedActivities = ref([]);
const isLoading = ref(false);
const currentFilter = ref('all');

// 对应 tbl_VolunteerActivity 表中的 ActivityStatus
const ACTIVITY_STATUS = {
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
      hour: '2-digit', minute: '2-digit', // second: '2-digit', // 移除秒以简化
      hour12: false
    }).replace(/\//g, '-');
  } catch (e) {
    console.error("Error formatting date:", datetimeStr, e);
    return datetimeStr;
  }
};

onMounted(async () => {
  console.log('Manager_manageActivity.vue: Component mounted.');
  if (!adminStore.loggedInAdmin && typeof adminStore.initializeStore === 'function') {
    await adminStore.initializeStore();
  }

  if (isAuthenticated.value) {
    console.log('Manager_manageActivity.vue: Admin is authenticated.');
    if ((!adminStore.detailedAdminInfo || !adminStore.detailedAdminInfo.adminId) && typeof adminStore.fetchDetailedAdminInfo === 'function') {
      try {
        // 确保 adminStore.loggedInAdmin.adminId 是可用的，如果 fetchDetailedAdminInfo 需要它
        await adminStore.fetchDetailedAdminInfo(adminStore.loggedInAdmin?.adminId);
      } catch (error) {
        console.error("Error fetching detailed admin info:", error);
      }
    }
    await fetchAllActivities();
  } else {
    ElMessage.error('请先登录！');
    // 考虑跳转到登录页，例如：
    // import router from '@/router';
    // router.push({ name: 'AdministratorLogin' }); // 确保路由名称'AdministratorLogin'已定义
  }
});

const fetchAllActivities = async () => {
  isLoading.value = true;
  console.log('Fetching all activities from /volunteerActivity/selectAll');
  try {
    // ** 真实 API 调用点 **
    // 对应后端 VolunteerActivityController.java 中的 @GetMapping("/selectAll")
    const response = await request.get('/volunteerActivity/selectAll');
    console.log('API Response for /volunteerActivity/selectAll:', response);

    if (response && response.code === "200" && Array.isArray(response.data)) {
      allActivities.value = response.data.map(act => ({
        activityId: act.activityId,
        activityName: act.activityName,
        orgId: act.orgId,
        location: act.location,
        startTime: act.startTime,
        endTime: act.endTime,
        recruitmentCount: act.recruitmentCount,
        acceptedCount: act.acceptedCount,
        activityStatus: act.activityStatus || ACTIVITY_STATUS.PENDING,
        creationTime: act.creationTime,
        reviewerAdminId: act.reviewerAdminId,
        contactPersonPhone: act.contactPersonPhone,
        activityDurationHours: act.activityDurationHours,
        activityRating: act.activityRating,
        isRatingAggregated: act.isRatingAggregated
      }));
      filterActivities();
      console.log('Fetched activities from DB:', allActivities.value);
    } else {
      ElMessage.error(response.msg || '获取志愿活动列表失败');
      console.error('Failed to fetch activities, response:', response);
      allActivities.value = [];
      filterActivities();
    }
  } catch (error) {
    console.error("Error fetching activities API:", error);
    ElMessage.error('请求志愿活动列表失败，请检查网络或服务器日志。');
    allActivities.value = [];
    filterActivities();
  } finally {
    isLoading.value = false;
  }
};

const filterActivities = () => {
  if (currentFilter.value === 'all') {
    displayedActivities.value = [...allActivities.value];
  } else {
    displayedActivities.value = allActivities.value.filter(act => act.activityStatus === currentFilter.value);
  }
};

const applyFilter = (filterType) => {
  currentFilter.value = filterType;
  filterActivities();
};

const getStatusClass = (status) => {
  switch (status) {
    case ACTIVITY_STATUS.PENDING:
      return 'status-pending';
    case ACTIVITY_STATUS.APPROVED:
      return 'status-approved';
    case ACTIVITY_STATUS.REJECTED:
      return 'status-rejected';
    case ACTIVITY_STATUS.ONGOING:
      return 'status-ongoing';
    case ACTIVITY_STATUS.ENDED:
      return 'status-ended';
    case ACTIVITY_STATUS.DISABLED:
      return 'status-disabled';
    default:
      return 'status-unknown';
  }
};

const viewDetails = (activity) => {
  ElMessageBox.alert(`
    <div style="text-align: left; font-size: 14px; line-height: 1.6;">
      <p><strong>活动ID:</strong> ${activity.activityId || 'N/A'}</p>
      <p><strong>活动名称:</strong> ${activity.activityName || 'N/A'}</p>
      <p><strong>组织ID:</strong> ${activity.orgId || 'N/A'}</p>
      <p><strong>开始时间:</strong> ${formatDate(activity.startTime)}</p>
      <p><strong>结束时间:</strong> ${formatDate(activity.endTime)}</p>
      <p><strong>地点:</strong> ${activity.location || 'N/A'}</p>
      <p><strong>招募人数:</strong> ${activity.recruitmentCount === null || activity.recruitmentCount === undefined ? 'N/A' : activity.recruitmentCount}</p>
      <p><strong>已录取人数:</strong> ${activity.acceptedCount === null || activity.acceptedCount === undefined ? 'N/A' : activity.acceptedCount}</p>
      <p><strong>活动状态:</strong> ${activity.activityStatus || 'N/A'}</p>
      <p><strong>创建时间:</strong> ${formatDate(activity.creationTime)}</p>
      <p><strong>审核管理员ID:</strong> ${activity.reviewerAdminId || 'N/A'}</p>
      <p><strong>联系电话:</strong> ${activity.contactPersonPhone || 'N/A'}</p>
      <p><strong>活动时长(小时):</strong> ${activity.activityDurationHours === null || activity.activityDurationHours === undefined ? 'N/A' : activity.activityDurationHours}</p>
      <p><strong>活动评分:</strong> ${activity.activityRating === null || activity.activityRating === undefined ? 'N/A' : activity.activityRating}</p>
      <p><strong>评分已汇总:</strong> ${activity.isRatingAggregated || '否'}</p>
    </div>
  `, `活动 "${activity.activityName || activity.activityId}" 的详细信息`, {
    dangerouslyUseHTMLString: true,
    confirmButtonText: '关闭',
    customClass: 'detail-message-box'
  });
};

// 核心函数：调用API更新活动状态
const updateActivityStatusApi = async (activityId, newStatus, operationName, remarks = null) => {
  isLoading.value = true; // 也可以针对单个操作设置更细致的loading状态
  try {
    const payload = {
      activityId: activityId,       // 后端 reviewActivity 方法从 payload 获取 activityId
      activityStatus: newStatus,    // 后端 reviewActivity 方法从 payload 获取 activityStatus
      reviewerAdminId: adminStore.detailedAdminInfo?.adminId || 'ADMIN_SYS_FALLBACK' // 当前操作的管理员ID
    };
    if (remarks && operationName.includes('驳回')) {
      payload.rejectionReason = remarks; // 后端 reviewActivity 方法从 payload 获取 rejectionReason
    }

    console.log(`Calling API PUT /volunteerActivity/review with payload:`, payload);

    // 对应后端 VolunteerActivityController.java 中的 @PutMapping("/review")
    const response = await request.put(`/volunteerActivity/review`, payload);

    if (response && response.code === "200") {
      ElMessage.success(`${operationName}成功！`);
      await fetchAllActivities(); // 操作成功后，重新获取列表以保证数据一致性
      return true;
    } else {
      ElMessage.error(response.msg || `${operationName}失败`);
      console.error('Failed to update activity status, API response:', response);
      return false;
    }
  } catch (error) {
    console.error(`Error during ${operationName} for ${activityId}:`, error);
    const errorMsg = error.response?.data?.msg || error.message || `请求${operationName}失败，请检查网络连接或服务器日志。`;
    ElMessage.error(errorMsg);
    return false;
  } finally {
    isLoading.value = false;
  }
};

const handleApprove = (activity) => {
  ElMessageBox.confirm(`确定要通过活动 "${activity.activityName}" (${activity.activityId}) 的审核吗？`, `确认通过审核`, {
    confirmButtonText: '确定通过',
    cancelButtonText: '取消',
    type: 'success',
  }).then(async () => {
    await updateActivityStatusApi(activity.activityId, ACTIVITY_STATUS.APPROVED, '通过审核');
  }).catch(() => {
    ElMessage.info('已取消操作');
  });
};

const handleReject = (activity) => {
  ElMessageBox.prompt('请输入驳回审核的理由（可选）：', `确认驳回活动 "${activity.activityName}"`, {
    confirmButtonText: '确定驳回',
    cancelButtonText: '取消',
    type: 'warning',
    inputType: 'textarea',
    inputPlaceholder: '驳回理由（选填，200字以内）'
  }).then(async ({value: reason}) => {
    await updateActivityStatusApi(activity.activityId, ACTIVITY_STATUS.REJECTED, '驳回审核', reason);
  }).catch(() => {
    ElMessage.info('已取消操作');
  });
};

const handleDisable = (activity) => {
  ElMessageBox.confirm(`确定要停用活动 "${activity.activityName}" (${activity.activityId}) 吗？`, `确认停用活动`, {
    confirmButtonText: '确定停用',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    await updateActivityStatusApi(activity.activityId, ACTIVITY_STATUS.DISABLED, '停用活动');
  }).catch(() => {
    ElMessage.info('已取消操作');
  });
};

const handleEnable = (activity) => {
  ElMessageBox.confirm(`确定要重新启用活动 "${activity.activityName}" (${activity.activityId}) 吗？`, `确认启用活动`, {
    confirmButtonText: '确定启用',
    cancelButtonText: '取消',
    type: 'info',
  }).then(async () => {
    await updateActivityStatusApi(activity.activityId, ACTIVITY_STATUS.APPROVED, '启用活动');
  }).catch(() => {
    ElMessage.info('已取消操作');
  });
};

const handleReReview = (activity) => {
  ElMessageBox.confirm(
      `对活动 "${activity.activityName}" (${activity.activityId}) 的 "${ACTIVITY_STATUS.REJECTED}" 状态进行操作：`,
      '重新审核/修改状态',
      {
        distinguishCancelAndClose: true,
        confirmButtonText: '通过审核',
        cancelButtonText: '仍驳回/保持', // 或者 '置为待审核'，如果业务允许
        showClose: false,
        callback: async (action) => {
          if (action === 'confirm') {
            await updateActivityStatusApi(activity.activityId, ACTIVITY_STATUS.APPROVED, '通过审核(重新审核)');
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
/* 页眉样式，与您提供的简单模板一致 */
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

/* 主体内容样式，与您提供的简单模板一致 */
.main-content {
  padding: 20px;
  background-image: url('/bg.png');
  background-size: cover;
  background-position: center;
  min-height: calc(100vh - 70px);
}

/* 表格标题和筛选按钮样式，与您提供的简单模板一致 */
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

/* 筛选按钮容器样式，与您提供的简单模板一致 */
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
