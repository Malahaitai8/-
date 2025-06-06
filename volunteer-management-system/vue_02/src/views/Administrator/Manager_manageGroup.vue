<template>
  <!-- 页面根容器 -->
  <div>
    <!-- 页眉部分，包含 logo、标题和欢迎信息 -->
    <header class="header">
      <img src="/logo.png" alt="Logo" class="logo">
      <h1 class="title">志愿组织管理</h1>
      <span class="welcome">欢迎您：{{ displayNameFromStore }}！</span>
    </header>
    <!-- 主体部分，用于展示表格 -->
    <main class="main-content">
      <div class="table-header">
        <h2>组织认证与状态管理</h2>
        <div class="filter-buttons">
          <button @click="applyFilter('all')" :class="{ active: currentFilter === 'all' }">全部</button>
          <button @click="applyFilter(ORGANIZATION_STATUS.PENDING)" :class="{ active: currentFilter === ORGANIZATION_STATUS.PENDING }">待认证</button>
          <button @click="applyFilter(ORGANIZATION_STATUS.APPROVED)" :class="{ active: currentFilter === ORGANIZATION_STATUS.APPROVED }">已认证</button>
          <button @click="applyFilter(ORGANIZATION_STATUS.REJECTED)" :class="{ active: currentFilter === ORGANIZATION_STATUS.REJECTED }">认证未通过</button>
          <button @click="applyFilter(ORGANIZATION_STATUS.FROZEN)" :class="{ active: currentFilter === ORGANIZATION_STATUS.FROZEN }">冻结</button>
        </div>
      </div>
      <!-- 可滚动的表格容器 -->
      <div class="table-container">
        <table>
          <thead>
          <tr>
            <th>序号</th>
            <th>组织信息</th>
            <th>认证状态</th>
            <th>操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-if="isLoading && displayedOrganizations.length === 0">
            <td colspan="4" style="text-align: center;">数据加载中...</td>
          </tr>
          <tr v-else-if="displayedOrganizations.length === 0 && !isLoading">
            <td colspan="4" style="text-align: center;">暂无数据显示</td>
          </tr>
          <tr v-for="(org, index) in displayedOrganizations" :key="org.organizationId">
            <td>{{ index + 1 }}</td>
            <td>
              <p><strong>组织ID：</strong>{{ org.organizationId }}</p>
              <p><strong>名称：</strong>{{ org.organizationName }}</p>
              <p><strong>登录名：</strong>{{ org.loginUsername }}</p>
              <p><strong>联系电话：</strong>{{ org.contactInfo }}</p>
              <p><strong>服务区域：</strong>{{ org.serviceArea }}</p>
            </td>
            <td :class="getStatusClass(org.accountStatus)">{{ org.accountStatus }}</td>
            <td>
              <button @click="viewDetails(org)" class="action-btn view">查看详情</button>
              <button
                  v-if="org.accountStatus === ORGANIZATION_STATUS.PENDING"
                  @click="handleApprove(org)"
                  class="action-btn approve">
                通过认证
              </button>
              <button
                  v-if="org.accountStatus === ORGANIZATION_STATUS.APPROVED"
                  @click="handleReject(org)"
                  class="action-btn reject">
                驳回认证
              </button>
              <button
                  v-if="org.accountStatus === ORGANIZATION_STATUS.APPROVED"
                  @click="handleFreeze(org)"
                  class="action-btn freeze">
                冻结账户
              </button>
              <button
                  v-if="org.accountStatus === ORGANIZATION_STATUS.FROZEN"
                  @click="handleUnfreeze(org)"
                  class="action-btn unfreeze">
                恢复账户
              </button>
               <button
                  v-if="org.accountStatus === ORGANIZATION_STATUS.REJECTED"
                  @click="handleReReview(org)"
                  class="action-btn re-review">
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
import { ref, computed, onMounted } from 'vue';
import { useAdminStore } from "@/stores/adminStore.js"; // 确保路径正确
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '@/utils/request'; // 您的请求工具

const adminStore = useAdminStore();

const displayNameFromStore = computed(() => adminStore.displayName || '管理员');
const isAuthenticated = computed(() => adminStore.isAuthenticated);

const allOrganizations = ref([]);
const displayedOrganizations = ref([]);
const isLoading = ref(false);
const currentFilter = ref('all');

// 对应 tbl_Organization 表中的 OrgAccountStatus
const ORGANIZATION_STATUS = {
  PENDING: '待认证',
  APPROVED: '已认证',
  REJECTED: '认证未通过',
  FROZEN: '冻结'
};

onMounted(async () => {
  console.log('Manager_manageOrganization.vue: Component mounted.');
  if (!adminStore.loggedInAdmin && typeof adminStore.initializeStore === 'function') {
    await adminStore.initializeStore();
  }

  if (isAuthenticated.value) {
    console.log('Manager_manageOrganization.vue: Admin is authenticated.');
    if ((!adminStore.detailedAdminInfo || !adminStore.detailedAdminInfo.adminId) && typeof adminStore.fetchDetailedAdminInfo === 'function') {
      console.log('Manager_manageOrganization.vue: Detailed admin info missing, fetching now.');
      try {
        await adminStore.fetchDetailedAdminInfo(adminStore.loggedInAdmin?.adminId);
      } catch (error) {
        console.error("Manager_manageOrganization.vue: Error fetching detailed admin info:", error);
      }
    }
    await fetchAllOrganizations();
  } else {
    console.warn('Manager_manageOrganization.vue: Admin not authenticated.');
    ElMessage.error('请先登录！');
    // import router from '@/router'; router.push({ name: 'AdministratorLogin' });
  }
});

const fetchAllOrganizations = async () => {
  isLoading.value = true;
  console.log('Fetching all organizations from /organization/selectAll');
  try {
    const response = await request.get('/organization/selectAll');
    console.log('API Response for /organization/selectAll:', response);

    if (response && response.code === "200" && Array.isArray(response.data)) {
      allOrganizations.value = response.data.map(org => ({
        organizationId: org.orgId,
        organizationName: org.orgName,
        loginUsername: org.orgLoginUserName,
        contactInfo: org.contactPersonPhone,
        serviceArea: org.serviceRegion,
        organizationSize: org.orgScale,
        organizationRating: org.orgRating,
        accountStatus: org.orgAccountStatus || ORGANIZATION_STATUS.PENDING,
        totalServiceHours: org.totalServiceHours,
        eventCount: org.activityCount,
        trainingCount: org.trainingCount
      }));
      filterOrganizations();
      console.log('Fetched and mapped organizations:', allOrganizations.value);
    } else {
      ElMessage.error(response.msg || '获取组织列表失败');
      console.error('Failed to fetch organizations, response:', response);
      allOrganizations.value = [];
      filterOrganizations();
    }
  } catch (error) {
    console.error("Error fetching organizations API:", error);
    ElMessage.error('请求组织列表失败，请检查网络或服务器日志。');
    allOrganizations.value = [];
    filterOrganizations();
  } finally {
    isLoading.value = false;
  }
};

const filterOrganizations = () => {
  if (currentFilter.value === 'all') {
    displayedOrganizations.value = [...allOrganizations.value];
  } else {
    displayedOrganizations.value = allOrganizations.value.filter(org => org.accountStatus === currentFilter.value);
  }
};

const applyFilter = (filterType) => {
  currentFilter.value = filterType;
  filterOrganizations();
};

const getStatusClass = (status) => {
  switch (status) {
    case ORGANIZATION_STATUS.PENDING: return 'status-pending';
    case ORGANIZATION_STATUS.APPROVED: return 'status-approved';
    case ORGANIZATION_STATUS.REJECTED: return 'status-rejected';
    case ORGANIZATION_STATUS.FROZEN: return 'status-frozen';
    default: return 'status-unknown';
  }
};

const viewDetails = (org) => {
  console.log('查看组织详细信息:', org);
  ElMessageBox.alert(`
    <div style="text-align: left; font-size: 14px; line-height: 1.6;">
      <p><strong>组织ID:</strong> ${org.organizationId || 'N/A'}</p>
      <p><strong>组织名称:</strong> ${org.organizationName || 'N/A'}</p>
      <p><strong>登录用户名:</strong> ${org.loginUsername || 'N/A'}</p>
      <p><strong>联系方式:</strong> ${org.contactInfo || 'N/A'}</p>
      <p><strong>服务区域:</strong> ${org.serviceArea || 'N/A'}</p>
      <p><strong>组织规模:</strong> ${org.organizationSize === null || org.organizationSize === undefined ? 'N/A' : org.organizationSize}</p>
      <p><strong>组织评分:</strong> ${(org.organizationRating === null || org.organizationRating === undefined) ? 'N/A' : parseFloat(org.organizationRating).toFixed(1)}</p>
      <p><strong>账户状态:</strong> ${org.accountStatus || 'N/A'}</p>
      <p><strong>总服务时长:</strong> ${org.totalServiceHours === null || org.totalServiceHours === undefined ? '0' : org.totalServiceHours} 小时</p>
      <p><strong>活动举办次数:</strong> ${org.eventCount === null || org.eventCount === undefined ? '0' : org.eventCount}</p>
      <p><strong>培训举办次数:</strong> ${org.trainingCount === null || org.trainingCount === undefined ? '0' : org.trainingCount}</p>
    </div>
  `, `组织 "${org.organizationName || org.organizationId}" 的详细信息`, {
    dangerouslyUseHTMLString: true,
    confirmButtonText: '关闭',
    customClass: 'detail-message-box'
  });
};

const updateOrganizationStatusApi = async (orgId, newStatus, operationName, remarks = null) => {
  isLoading.value = true;
  try {
    const payload = {
      orgId: orgId,
      orgAccountStatus: newStatus, // 对应后端 Organization 实体的 orgAccountStatus
      // reviewerAdminId: adminStore.detailedAdminInfo?.adminId || null, // 可选：如果后端需要记录操作的管理员ID
    };
    if (remarks && operationName.includes('驳回')) {
      // 如果您的 Organization 实体和数据库表中有字段用于存储驳回理由（例如 rejectionReason 或 remarks）
      // payload.rejectionReason = remarks;
      console.log(`驳回理由 (如果后端支持将会发送): ${remarks}`);
    }

    console.log(`Calling API to update organization ${orgId} to status ${newStatus}`, payload);

    // API端点与您的 OrganizationController.java 中的 updateByOrgId 对应
    // 后端 updateByOrgId 方法由于是动态SQL，只会更新传入的字段（orgAccountStatus 和 orgId）
    //  ***** 这是唯一需要修改的地方  *****
    // 将API端点从 /updateByOrgId 改为 /status
    const response = await request.put(`/organization/status`, payload);
    //  ************************************


    if (response && response.code === "200") {
      ElMessage.success(`${operationName}成功！`);
      await fetchAllOrganizations(); // 重新获取列表以同步数据
      return true;
    } else {
      ElMessage.error(response.msg || `${operationName}失败`);
      console.error('Failed to update organization status, response:', response);
      return false;
    }
  } catch (error) {
    console.error(`Error during ${operationName} for ${orgId}:`, error);
    const errorMsg = error.response?.data?.msg || error.message || `请求${operationName}失败`;
    ElMessage.error(errorMsg);
    return false;
  } finally {
    isLoading.value = false;
  }
};

const handleApprove = (org) => {
  ElMessageBox.confirm(`确定要将组织 "${org.organizationName}" (${org.organizationId}) 的状态更改为 "${ORGANIZATION_STATUS.APPROVED}" 吗？`, `确认通过认证`, {
    confirmButtonText: '确定通过',
    cancelButtonText: '取消',
    type: 'success',
  }).then(async () => {
    await updateOrganizationStatusApi(org.organizationId, ORGANIZATION_STATUS.APPROVED, '通过认证');
  }).catch(() => {
    ElMessage.info('已取消操作');
  });
};

const handleReject = (org) => {
  ElMessageBox.prompt('请输入驳回认证的理由（可选）：', `确认驳回组织 "${org.organizationName}"`, {
    confirmButtonText: '确定驳回',
    cancelButtonText: '取消',
    type: 'warning',
    inputType: 'textarea',
    inputPlaceholder: '驳回理由（选填，200字以内）'
  }).then(async ({ value: reason }) => {
    console.log(`驳回组织 ${org.organizationId} 的理由: ${reason || '无'}`);
    await updateOrganizationStatusApi(org.organizationId, ORGANIZATION_STATUS.REJECTED, '驳回认证', reason);
  }).catch(() => {
    ElMessage.info('已取消操作');
  });
};

const handleFreeze = (org) => {
   ElMessageBox.confirm(`确定要将组织 "${org.organizationName}" (${org.organizationId}) 的账户状态更改为 "${ORGANIZATION_STATUS.FROZEN}" 吗？`, `确认冻结账户`, {
     confirmButtonText: '确定冻结',
     cancelButtonText: '取消',
     type: 'warning',
   }).then(async () => {
     await updateOrganizationStatusApi(org.organizationId, ORGANIZATION_STATUS.FROZEN, '冻结账户');
   }).catch(() => {
     ElMessage.info('已取消操作');
   });
};

const handleUnfreeze = (org) => {
  ElMessageBox.confirm(`确定要将组织 "${org.organizationName}" (${org.organizationId}) 的账户状态恢复为 "${ORGANIZATION_STATUS.APPROVED}" 吗？`, `确认恢复账户`, {
    confirmButtonText: '确定恢复',
    cancelButtonText: '取消',
    type: 'info',
  }).then(async () => {
    await updateOrganizationStatusApi(org.organizationId, ORGANIZATION_STATUS.APPROVED, '恢复账户');
  }).catch(() => {
    ElMessage.info('已取消操作');
  });
};

const handleReReview = (org) => {
  ElMessageBox.confirm(
      `对组织 "${org.organizationName}" (${org.organizationId}) 的 "${ORGANIZATION_STATUS.REJECTED}" 状态进行操作：`,
      '重新审核/修改状态',
      {
        distinguishCancelAndClose: true,
        confirmButtonText: '通过认证',
        cancelButtonText: '置为待认证',
        showClose: false,
        callback: async (action) => {
          if (action === 'confirm') {
            await updateOrganizationStatusApi(org.organizationId, ORGANIZATION_STATUS.APPROVED, '通过认证(重新审核)');
          } else if (action === 'cancel') {
            await updateOrganizationStatusApi(org.organizationId, ORGANIZATION_STATUS.PENDING, '置为待认证(重新审核)');
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
  background-color: #d32f2f;
  display: flex;
  align-items: center;
  padding: 10px 25px;
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.logo {
  height: 45px;
  margin-right: 15px;
}

.title {
  font-size: 1.7em;
  font-weight: 600;
  margin: 0;
}

.welcome {
  font-size: 0.9em;
  margin-left: auto;
}

/* 主体内容样式 */
.main-content {
  padding: 25px;
  background-color: #f4f6f8;
  min-height: calc(100vh - 65px);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.table-header h2 {
  margin: 0;
  color: #333;
  font-size: 1.4em;
}

.filter-buttons {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.filter-buttons button {
  background-color: #4a90e2;
  color: white;
  padding: 8px 12px;
  border-radius: 5px;
  font-weight: 500;
  border: none;
  cursor: pointer;
  font-size: 0.9em;
}

.filter-buttons button.active {
  background-color: #357abd;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.2);
}

.filter-buttons button:hover:not(.active) {
  background-color: #6a9fd2;
}

.table-container {
  max-height: calc(100vh - 250px);
  overflow-y: auto;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  border-bottom: 1px solid #e0e0e0;
  padding: 10px 12px;
  text-align: left;
  vertical-align: middle;
  font-size: 0.9em;
}

th {
  background-color: #f9fafb;
  font-weight: 600;
  color: #4a5568;
  border-top: 1px solid #e0e0e0;
}

tr:hover {
  background-color: #f5f5f5;
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

.status-frozen {
  color: #6c757d;
  font-weight: bold;
}

.status-unknown {
  color: #777;
  font-style: italic;
}


.action-btn {
  margin: 2px;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85em;
  transition: background-color 0.2s ease, opacity 0.2s ease;
  border: 1px solid transparent;
  color: white;
  font-weight: 500;
}

.action-btn:disabled {
  background-color: #e0e0e0 !important;
  color: #9e9e9e !important;
  cursor: not-allowed;
  opacity: 0.7;
}

.action-btn.view {
  background-color: #17a2b8;
}

.action-btn.view:hover:not(:disabled) {
  background-color: #138496;
}

.action-btn.approve {
  background-color: #28a745;
}

.action-btn.approve:hover:not(:disabled) {
  background-color: #218838;
}

.action-btn.reject {
  background-color: #dc3545;
}

.action-btn.reject:hover:not(:disabled) {
  background-color: #c82333;
}

.action-btn.freeze {
  background-color: #ffc107;
  color: #212529;
  border-color: #ffc107;
}

.action-btn.freeze:hover:not(:disabled) {
  background-color: #e0a800;
  border-color: #d39e00;
}

.action-btn.unfreeze {
  background-color: #007bff;
}

.action-btn.unfreeze:hover:not(:disabled) {
  background-color: #0069d9;
}

.action-btn.review-rejection, .action-btn.re-review {
  background-color: #6c757d;
}

.action-btn.review-rejection:hover:not(:disabled), .action-btn.re-review:hover:not(:disabled) {
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
