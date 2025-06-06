<template>
  <!-- 页面根容器 -->
  <div>
    <!-- 页眉部分，包含 logo、标题和欢迎信息 -->
    <header class="header">
      <img src="/logo.png" alt="Logo" class="logo">
      <h1 class="title">志愿者管理</h1>
      <span class="welcome">欢迎您：{{ displayNameFromStore }}！</span>
    </header>
    <!-- 主体部分，用于展示表格 -->
    <main class="main-content">
      <div class="table-header">
        <h2>志愿者账户与认证管理</h2>
        <div class="filter-buttons">
          <button @click="applyFilter('all')" :class="{ active: currentFilter === 'all' }">全部</button>
          <button @click="applyFilter(VOLUNTEER_STATUS.PENDING)" :class="{ active: currentFilter === VOLUNTEER_STATUS.PENDING }">待认证</button>
          <button @click="applyFilter(VOLUNTEER_STATUS.APPROVED)" :class="{ active: currentFilter === VOLUNTEER_STATUS.APPROVED }">已认证</button>
          <button @click="applyFilter(VOLUNTEER_STATUS.REJECTED)" :class="{ active: currentFilter === VOLUNTEER_STATUS.REJECTED }">认证未通过</button>
          <button @click="applyFilter(VOLUNTEER_STATUS.FROZEN)" :class="{ active: currentFilter === VOLUNTEER_STATUS.FROZEN }">已冻结</button>
        </div>
      </div>
      <!-- 可滚动的表格容器 -->
      <div class="table-container">
        <table>
          <thead>
          <tr>
            <th>序号</th>
            <th>信息</th>
            <th>账户状态</th>
            <th>操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-if="isLoading && displayedVolunteers.length === 0"> <!-- 仅在初次加载时显示整行loading -->
            <td colspan="4" style="text-align: center;">数据加载中...</td>
          </tr>
          <tr v-else-if="displayedVolunteers.length === 0 && !isLoading">
            <td colspan="4" style="text-align: center;">暂无数据显示</td>
          </tr>
          <tr v-for="(volunteer, index) in displayedVolunteers" :key="volunteer.volunteerId">
            <td>{{ index + 1 }}</td>
            <td>
              <p>ID：{{ volunteer.volunteerId }}</p>
              <p>用户名：{{ volunteer.username }}</p>
              <p>姓名：{{ volunteer.name }}</p>
              <p>性别：{{ volunteer.gender }}</p>
              <p>手机号：{{ volunteer.phone }}</p>
              <p>身份证：{{ volunteer.idCard }}</p>
              <p>服务区域：{{ volunteer.serviceArea }}</p>
            </td>
            <td :class="getStatusClass(volunteer.accountStatus)">{{ volunteer.accountStatus }}</td>
            <td>
              <button @click="viewDetails(volunteer)" class="action-btn view">查看</button>
              <button
                  v-if="volunteer.accountStatus === VOLUNTEER_STATUS.PENDING"
                  @click="handleApprove(volunteer)"
                  class="action-btn approve">
                通过认证
              </button>
              <button
                  v-if="volunteer.accountStatus === VOLUNTEER_STATUS.APPROVED"
                  @click="handleReject(volunteer)"
                  class="action-btn reject">
                驳回认证
              </button>
              <button
                  v-if="volunteer.accountStatus === VOLUNTEER_STATUS.APPROVED"
                  @click="handleFreeze(volunteer)"
                  class="action-btn freeze">
                冻结账户
              </button>
              <button
                  v-if="volunteer.accountStatus === VOLUNTEER_STATUS.FROZEN"
                  @click="handleUnfreeze(volunteer)"
                  class="action-btn unfreeze">
                解冻账户
              </button>
               <button
                  v-if="volunteer.accountStatus === VOLUNTEER_STATUS.REJECTED"
                  @click="viewDetails(volunteer)"
                  class="action-btn review-rejection">
                查看原因
              </button>
               <button
                  v-if="volunteer.accountStatus === VOLUNTEER_STATUS.REJECTED"
                  @click="handleReReview(volunteer)"
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
import { useAdminStore } from "@/stores/adminStore.js"; // 请确保路径正确
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '@/utils/request'; // 您的请求工具，请确保路径正确

const adminStore = useAdminStore();

const displayNameFromStore = computed(() => adminStore.displayName || '管理员');
const isAuthenticated = computed(() => adminStore.isAuthenticated);

const allVolunteers = ref([]);
const displayedVolunteers = ref([]);
const isLoading = ref(false);
const currentFilter = ref('all');

const VOLUNTEER_STATUS = {
  PENDING: '未实名认证',
  APPROVED: '已实名认证',
  REJECTED: '认证未通过',
  FROZEN: '已冻结'
};

onMounted(async () => {
  console.log('Manager_manageVolunteer.vue: Component mounted.');
  if (!adminStore.loggedInAdmin && typeof adminStore.initializeStore === 'function') {
      await adminStore.initializeStore();
  }

  if (isAuthenticated.value) {
    console.log('Manager_manageVolunteer.vue: Admin is authenticated.');
    if ((!adminStore.detailedAdminInfo || !adminStore.detailedAdminInfo.adminId) && typeof adminStore.fetchDetailedAdminInfo === 'function') {
      console.log('Manager_manageVolunteer.vue: Detailed admin info missing, fetching now.');
      try {
        await adminStore.fetchDetailedAdminInfo(adminStore.loggedInAdmin?.adminId);
      } catch (error) {
        console.error("Manager_manageVolunteer.vue: Error fetching detailed admin info:", error);
      }
    }
    await fetchAllVolunteers();
  } else {
    console.warn('Manager_manageVolunteer.vue: Admin not authenticated.');
    ElMessage.error('请先登录！');
    // import router from '@/router'; router.push({ name: 'AdministratorLogin' });
  }
});

const fetchAllVolunteers = async () => {
  isLoading.value = true;
  try {
    const response = await request.get('/volunteer/selectAll');
    if (response && response.code === "200" && Array.isArray(response.data)) {
      allVolunteers.value = response.data.map(v => ({
        ...v,
        accountStatus: v.accountStatus || VOLUNTEER_STATUS.PENDING
      }));
      filterVolunteers();
      console.log('Fetched volunteers from DB:', allVolunteers.value);
    } else {
      ElMessage.error(response.msg || '获取志愿者列表失败');
      allVolunteers.value = [];
      filterVolunteers();
    }
  } catch (error) {
    console.error("Error fetching volunteers:", error);
    ElMessage.error('请求志愿者列表失败，请检查网络或联系管理员。');
    allVolunteers.value = [];
    filterVolunteers();
  } finally {
    isLoading.value = false;
  }
};

const filterVolunteers = () => {
  if (currentFilter.value === 'all') {
    displayedVolunteers.value = [...allVolunteers.value];
  } else {
    displayedVolunteers.value = allVolunteers.value.filter(v => v.accountStatus === currentFilter.value);
  }
};

const applyFilter = (filterType) => {
  currentFilter.value = filterType;
  filterVolunteers();
};

const getStatusClass = (status) => {
  switch (status) {
    case VOLUNTEER_STATUS.PENDING: return 'status-pending';
    case VOLUNTEER_STATUS.APPROVED: return 'status-approved';
    case VOLUNTEER_STATUS.REJECTED: return 'status-rejected';
    case VOLUNTEER_STATUS.FROZEN: return 'status-frozen';
    default: return 'status-unknown';
  }
};

const viewDetails = (volunteer) => {
  console.log('查看详细信息:', volunteer);
  // 注意：确保volunteer对象中的所有字段都存在，或者提供回退值
  ElMessageBox.alert(`
    <div style="text-align: left; font-size: 14px; line-height: 1.6;">
      <p><strong>ID:</strong> ${volunteer.volunteerId || 'N/A'}</p>
      <p><strong>用户名:</strong> ${volunteer.username || 'N/A'}</p>
      <p><strong>姓名:</strong> ${volunteer.name || 'N/A'}</p>
      <p><strong>性别:</strong> ${volunteer.gender || 'N/A'}</p>
      <p><strong>手机号:</strong> ${volunteer.phone || 'N/A'}</p>
      <p><strong>身份证号:</strong> ${volunteer.idCard || 'N/A'}</p>
      <p><strong>服务区域:</strong> ${volunteer.serviceArea || 'N/A'}</p>
      <p><strong>账户状态:</strong> ${volunteer.accountStatus || 'N/A'}</p>
      <p><strong>国籍:</strong> ${volunteer.country || '未填写'}</p>
      <p><strong>民族:</strong> ${volunteer.ethnicity || '未填写'}</p>
      <p><strong>政治面貌:</strong> ${volunteer.politicalStatus || '未填写'}</p>
      <p><strong>最高学历:</strong> ${volunteer.highestEducation || '未填写'}</p>
      <p><strong>从业情况:</strong> ${volunteer.employmentStatus || '未填写'}</p>
      <p><strong>服务类别:</strong> ${volunteer.serviceCategory || '未填写'}</p>
      <p><strong>总服务时长:</strong> ${(volunteer.totalServiceHours === null || volunteer.totalServiceHours === undefined) ? '0.00' : parseFloat(volunteer.totalServiceHours).toFixed(2)} 小时</p>
      <p><strong>综合评分:</strong> ${(volunteer.volunteerComprehensiveScore === null || volunteer.volunteerComprehensiveScore === undefined) ? '0.00' : parseFloat(volunteer.volunteerComprehensiveScore).toFixed(2)}</p>
    </div>
  `, `志愿者 ${volunteer.name || volunteer.volunteerId} 的详细信息`, {
    dangerouslyUseHTMLString: true,
    confirmButtonText: '关闭',
    customClass: 'detail-message-box'
  });
};

// 核心函数：调用API更新志愿者状态
const updateVolunteerStatusApi = async (volunteerId, newStatus, operationName, remarks = null) => {
  isLoading.value = true;
  try {
    const payload = {
      accountStatus: newStatus
    };
    // 根据后端 VolunteerController，remarks 应该作为 rejectionReason 传递
    if (remarks && (operationName === '驳回认证' || operationName.includes('驳回'))) {
      payload.rejectionReason = remarks;
    }

    console.log(`Calling API to update volunteer ${volunteerId} to status ${newStatus}`, payload);

    // ** 真实 API 调用点 **
    // 对应 VolunteerController.java 中的 @PutMapping("/updateStatus/{volunteerId}")
    const response = await request.put(`/volunteer/updateStatus/${volunteerId}`, payload);

    if (response && response.code === "200") {
      ElMessage.success(`${operationName}成功！`);
      await fetchAllVolunteers(); // 重新获取列表以同步数据
      return true;
    } else {
      ElMessage.error(response.msg || `${operationName}失败`);
      return false;
    }
  } catch (error) {
    console.error(`Error during ${operationName} for ${volunteerId}:`, error);
    const errorMsg = error.response?.data?.msg || error.message || `请求${operationName}失败`;
    ElMessage.error(errorMsg);
    return false;
  } finally {
    isLoading.value = false;
  }
};

const handleApprove = (volunteer) => {
  ElMessageBox.confirm(`确定要将志愿者 ${volunteer.name} (${volunteer.volunteerId}) 的认证状态更改为 "${VOLUNTEER_STATUS.APPROVED}" 吗？`, `确认通过认证`, {
    confirmButtonText: '确定通过',
    cancelButtonText: '取消',
    type: 'success',
  }).then(async () => {
    await updateVolunteerStatusApi(volunteer.volunteerId, VOLUNTEER_STATUS.APPROVED, '通过认证');
  }).catch(() => {
    ElMessage.info('已取消操作');
  });
};

const handleReject = (volunteer) => {
  ElMessageBox.prompt('请输入驳回认证的理由（可选）：', `确认驳回志愿者 "${volunteer.name}"`, {
    confirmButtonText: '确定驳回',
    cancelButtonText: '取消',
    type: 'warning',
    inputType: 'textarea',
    inputPlaceholder: '驳回理由（选填，200字以内）'
  }).then(async ({ value: reason }) => {
    console.log(`驳回志愿者 ${volunteer.volunteerId} 的理由: ${reason || '无'}`);
    await updateVolunteerStatusApi(volunteer.volunteerId, VOLUNTEER_STATUS.REJECTED, '驳回认证', reason);
  }).catch(() => {
    ElMessage.info('已取消操作');
  });
};

const handleFreeze = (volunteer) => {
   ElMessageBox.confirm(`确定要将志愿者 ${volunteer.name} (${volunteer.volunteerId}) 的账户状态更改为 "${VOLUNTEER_STATUS.FROZEN}" 吗？`, `确认冻结账户`, {
    confirmButtonText: '确定冻结',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    await updateVolunteerStatusApi(volunteer.volunteerId, VOLUNTEER_STATUS.FROZEN, '冻结账户');
  }).catch(() => {
    ElMessage.info('已取消操作');
  });
};

const handleUnfreeze = (volunteer) => {
  ElMessageBox.confirm(`确定要将志愿者 ${volunteer.name} (${volunteer.volunteerId}) 的账户状态恢复为 "${VOLUNTEER_STATUS.APPROVED}" 吗？`, `确认解冻账户`, {
    confirmButtonText: '确定解冻',
    cancelButtonText: '取消',
    type: 'info',
  }).then(async () => {
    await updateVolunteerStatusApi(volunteer.volunteerId, VOLUNTEER_STATUS.APPROVED, '解冻账户');
  }).catch(() => {
    ElMessage.info('已取消操作');
  });
};

const handleReReview = (volunteer) => {
  ElMessageBox.confirm(
    `对志愿者 ${volunteer.name} (${volunteer.volunteerId}) 的 "认证未通过" 状态进行操作：`,
    '重新审核/修改状态',
    {
      distinguishCancelAndClose: true,
      confirmButtonText: '通过认证',
      cancelButtonText: '仍驳回/保持', // 或 '重置为待认证'
      showClose: false,
      callback: async (action) => {
        if (action === 'confirm') {
          await updateVolunteerStatusApi(volunteer.volunteerId, VOLUNTEER_STATUS.APPROVED, '通过认证(重新审核)');
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
/* 页眉样式，设置背景颜色为红色，使用弹性布局使其内容垂直居中，添加内边距 */
.header {
  background-color: red;
  display: flex;
  align-items: center;
  padding: 10px 20px;
}

/* logo 图片样式，设置图片高度 */
.logo {
  height: 50px;
}

/* 标题样式，设置文字颜色为白色，加粗显示，并添加左右外边距 */
.title {
  color: white;
  font-weight: bold;
  margin: 0 30px;
}

/* 欢迎信息样式，设置文字颜色为白色，字体大小为 14px */
.welcome {
  color: white;
  font-size: 14px;
}

/* 主体内容样式，设置内边距并添加背景图 */
.main-content {
  padding: 20px;
  background-image: url('/bg.png');
  background-size: cover;
  background-position: center;
}

/* 表格标题和筛选按钮样式 */
.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

/* 筛选按钮容器样式 */
.filter-buttons {
  display: flex;
  gap: 10px;
}

/* 可滚动的表格容器样式 */
.table-container {
  max-height: 600px;
  overflow-y: auto;
}

/* 表格样式，设置背景为白色 */
table {
  width: 100%;
  border-collapse: collapse;
  background-color: white;
}

th, td {
  border: 1px solid #ccc;
  padding: 8px;
  text-align: left;
}

th {
  background-color: #f2f2f2;
}

/* 操作按钮样式 */
button {
  margin: 2px;
  padding: 4px 8px;
  background-color: red;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>