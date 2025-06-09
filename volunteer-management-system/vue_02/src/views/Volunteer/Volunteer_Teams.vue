<template>
  <div class="teams-page">
    <div class="teams-title">
      <span>我的队伍</span>
      <div class="header-buttons">
        <el-button
            type="danger"
            size="small"
            class="more-btn-in-menu"
            @click="goToTeamsMore"
            round
        >
          <el-icon style="vertical-align: middle; margin-right: 4px;">
            <Plus />
          </el-icon>
          参加更多队伍
        </el-button>
        <el-button
            type="info"
            size="small"
            @click="refreshTeams"
            round
            style="margin-left: 10px;"
        >
          <el-icon style="vertical-align: middle; margin-right: 4px;">
            <Refresh />
          </el-icon>
          刷新列表
        </el-button>
      </div>
    </div>


    <!-- 队伍状态筛选按钮 -->
    <div class="filter-buttons">
      <button @click="applyFilter('all')" :class="{ active: currentFilter === 'all' }">全部</button>
      <button @click="applyFilter(MEMBER_STATUS.PENDING)" :class="{ active: currentFilter === MEMBER_STATUS.PENDING }">申请中</button>
      <button @click="applyFilter(MEMBER_STATUS.JOINED)" :class="{ active: currentFilter === MEMBER_STATUS.JOINED }">已加入</button>
      <button @click="applyFilter(MEMBER_STATUS.EXITED)" :class="{ active: currentFilter === MEMBER_STATUS.EXITED }">已退出</button>
    </div>

    <!-- 队伍表格 -->
    <el-table
        v-if="pageData.length"
        :data="pageData"
        border
        style="width: 100%; margin-top: 20px;"
        header-cell-class-name="table-header"
        v-loading="isLoading"
        element-loading-text="加载中..."
    >
      <el-table-column prop="orgName" label="队伍名称" align="center" width="180"></el-table-column>
      <el-table-column prop="joinTime" label="加入时间" align="center" width="160">
        <template #default="scope">
          {{ formatDateTime(scope.row.joinTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="memberStatus" label="状态" align="center"></el-table-column>

      <el-table-column label="操作" align="center" width="240" fixed="right">
        <template #default="scope">
          <el-button
              size="small"
              type="danger"
              @click="handleLeave(scope.row)"
              :disabled="scope.row.memberStatus === MEMBER_STATUS.EXITED"
          >
            {{ scope.row.memberStatus === MEMBER_STATUS.EXITED ? '已退出' : '退出队伍' }}
          </el-button>

          <el-button
              size="small"
              type="primary"
              @click="handleViewDetail(scope.row)"
              style="margin-left: 10px;"
          >
            查看详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-else class="empty-box">
      <img src="https://img.alicdn.com/imgextra/i4/O1CN01v7Qw1B1QwQwQwQw_!!6000000002007-2-tps-200-200.png" alt="empty"></img>
      <div>暂无队伍信息</div>
    </div>

    <el-pagination
        v-if="filteredTeams.length > pageSize"
        style="margin-top: 24px; text-align: right;"
        background
        layout="prev, pager, next, jumper"
        :total="filteredTeams.length"
        :page-size="pageSize"
        v-model:current-page="currentPage"
    ></el-pagination>

    <el-dialog title="队伍详细信息" v-model="detailDialogVisible" width="500px">
      <el-descriptions :column="1" border v-loading="isDetailLoading">
        <el-descriptions-item label="组织ID">{{ detailData.orgId }}</el-descriptions-item>
        <el-descriptions-item label="组织名称">{{ detailData.orgName }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ detailData.contactPersonPhone }}</el-descriptions-item>
        <el-descriptions-item label="服务区域">{{ detailData.serviceRegion }}</el-descriptions-item>
        <el-descriptions-item label="组织规模">{{ detailData.orgScale }} 人</el-descriptions-item>
        <el-descriptions-item label="组织评分">{{ detailData.orgRating }} 分</el-descriptions-item>
        <el-descriptions-item label="账户状态">{{ detailData.orgAccountStatus }}</el-descriptions-item>
        <el-descriptions-item label="总服务时长">{{ detailData.totalServiceHours }} 小时</el-descriptions-item>
        <el-descriptions-item label="活动举办次数">{{ detailData.activityCount }} 次</el-descriptions-item>
        <el-descriptions-item label="培训举办次数">{{ detailData.trainingCount }} 次</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'; // Import onMounted
import { Plus, Refresh } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '@/utils/request';
import { useUserStore } from '@/stores/userStore';
import router from "@/router/index.js";

const userStore = useUserStore();

const allTeams = ref([]); // Holds all fetched teams
const filteredTeams = ref([]); // Holds teams after filtering
const pageSize = 5;
const currentPage = ref(1);
const isLoading = ref(false); // Loading state for the table
const isDetailLoading = ref(false); // Loading state for the detail dialog

const detailDialogVisible = ref(false);
const detailData = ref({});
const currentFilter = ref('all'); // Tracks the active filter

// Define member status constants based on tbl_VolunteerOrganizationJoin
const MEMBER_STATUS = {
  PENDING: '申请中',
  JOINED: '已加入',
  EXITED: '已退出',
};

// Computed property to display filtered and paginated data
const pageData = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return filteredTeams.value.slice(start, start + pageSize);
});

// Utility to format datetime
const formatDateTime = (dateTimeString) => {
  if (!dateTimeString) return '';
  const date = new Date(dateTimeString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}`;
};

// --- API Calls ---

// Fetch currently logged-in volunteer's joined organizations
const fetchMyTeams = async (volunteerId) => {
  if (!volunteerId) {
    allTeams.value = [];
    filteredTeams.value = [];
    return;
  }
  isLoading.value = true;
  try {
    const res = await request.get(`/volunteerOrganizationJoin/myJoinedOrganizations/${volunteerId}`);
    if (res.code === '200' && Array.isArray(res.data)) {
      allTeams.value = res.data;
      filterTeams(); // Apply current filter after fetching all data
    } else {
      ElMessage.error(res.msg || '获取我的队伍信息失败');
      allTeams.value = [];
      filteredTeams.value = [];
    }
  } catch (error) {
    ElMessage.error('网络错误，无法获取队伍信息');
    console.error('Error fetching my teams:', error);
    allTeams.value = [];
    filteredTeams.value = [];
  } finally {
    isLoading.value = false;
  }
};

// Apply filter logic
const filterTeams = () => {
  currentPage.value = 1; // Reset page to 1 when filter changes
  if (currentFilter.value === 'all') {
    filteredTeams.value = [...allTeams.value];
  } else {
    filteredTeams.value = allTeams.value.filter(team =>
        team.memberStatus === currentFilter.value
    );
  }
};

// Handle filter button click
const applyFilter = (filterType) => {
  currentFilter.value = filterType;
  filterTeams(); // Apply new filter
};

// Handle leaving a team
const handleLeave = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要退出队伍 "${row.orgName}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );

    const payload = {
      volunteerId: userStore.detailedVolunteerInfo.volunteerId,
      orgId: row.orgId,
      memberStatus: MEMBER_STATUS.EXITED // Explicitly setting status to '已退出'
    };
    const res = await request.put('/volunteerOrganizationJoin/leaveTeam', payload);

    if (res.code === '200') {
      ElMessage.success('已成功退出队伍');
      // Update the status locally in allTeams and then re-filter
      const index = allTeams.value.findIndex(t => t.orgId === row.orgId && t.volunteerId === userStore.detailedVolunteerInfo.volunteerId);
      if (index !== -1) {
        allTeams.value[index].memberStatus = MEMBER_STATUS.EXITED;
      }
      filterTeams(); // Re-apply filter to update table
    } else {
      ElMessage.error(res.msg || '退出队伍失败');
    }
  } catch (error) {
    if (error !== 'cancel') { // User clicked cancel
      ElMessage.error('网络错误或操作取消');
      console.error('Error leaving team:', error);
    }
  }
};

// Handle viewing team details
const handleViewDetail = async (row) => {
  isDetailLoading.value = true;
  detailDialogVisible.value = true;
  detailData.value = {}; // Clear previous data
  try {
    // API endpoint: GET /organization/selectByOrgId/{orgId}
    const res = await request.get(`/organization/selectByOrgId/${row.orgId}`);
    if (res.code === '200' && res.data) {
      detailData.value = res.data;
    } else {
      ElMessage.error(res.msg || '获取队伍详情失败');
      detailDialogVisible.value = false;
    }
  } catch (error) {
    ElMessage.error('网络错误，无法获取队伍详情');
    console.error('Error fetching organization details:', error);
    detailDialogVisible.value = false;
  } finally {
    isDetailLoading.value = false;
  }
};

// Navigate to "参加更多队伍" page
const goToTeamsMore = () => {
  router.push('/volunteer/teams-more');
};

// Refresh teams data
const refreshTeams = () => {
  fetchMyTeams(userStore.detailedVolunteerInfo.volunteerId);
  ElMessage.info('队伍列表已刷新');
};

// --- Lifecycle Hook ---
// Fetch teams on component mount
onMounted(() => {
  if (userStore.detailedVolunteerInfo?.volunteerId) {
    fetchMyTeams(userStore.detailedVolunteerInfo.volunteerId);
  }
});

// --- Watchers ---
// Fetch teams when volunteerId changes (e.g., after login/logout if store state changes dynamically)
watch(() => userStore.detailedVolunteerInfo?.volunteerId, (newId) => {
  if (newId) {
    fetchMyTeams(newId);
  } else {
    allTeams.value = [];
    filteredTeams.value = [];
  }
}); // Removed immediate: true as onMounted handles initial load if ID is available

</script>

<style scoped>
.teams-page {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.06);
  padding: 0 32px 32px; /* Adjusted padding to match training page */
  min-height: 400px;
}

/* Updated .teams-title to accommodate buttons inside */
.teams-title {
  background: #fff0f0;
  color: #ff0000; /* Used red from your example */
  font-weight: bold;
  font-size: 20px;
  padding: 18px 32px;
  margin: 0 -32px 20px -32px; /* Negative margins to extend background */
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
  border-bottom: 2px solid #fde2e2;

  display: flex; /* Make it a flex container */
  justify-content: space-between; /* Space out title text and buttons */
  align-items: center; /* Vertically center items */
}

.teams-title span {
  /* No specific styles needed here unless you want to override */
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
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(255,0,0,0.10);
  letter-spacing: 1px;
  transition: background 0.3s;
}

.more-btn-in-menu:hover {
  background: linear-gradient(90deg, #ff7875 0%, #ff0000 100%);
  color: #fff;
}

.filter-buttons {
  margin-top: 20px;
  padding: 0 0px; /* Aligned with table, removed side padding */
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-start; /* Align buttons to start */
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
  background-color: #007bff; /* Use blue for active state for general consistency */
  color: white;
  border-color: #007bff;
}

.filter-buttons button:hover:not(.active) {
  background-color: #e9e9e9;
  border-color: #c0c0c0;
}

.table-header {
  background: #f8f8f9 !important; /* Lighter background for table header */
  color: #515a6e !important; /* Darker text for table header */
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
</style>
