<template>
  <div class="activity-records-container">
    <h2>我的志愿活动记录</h2>

    <div class="search-filter-area">
      <el-input v-model="searchForm.activityName" placeholder="活动名称" clearable @clear="loadActivities"></el-input>
      <el-select v-model="searchForm.activityStatus" placeholder="活动状态" clearable @clear="loadActivities">
        <el-option label="全部" value=""></el-option>
        <el-option label="待审核" value="待审核"></el-option>
        <el-option label="审核通过" value="审核通过"></el-option>
        <el-option label="进行中" value="进行中"></el-option>
        <el-option label="已结束" value="已结束"></el-option>
        <el-option label="审核不通过" value="审核不通过"></el-option>
        <el-option label="已停用" value="已停用"></el-option>
      </el-select>
      <el-button type="primary" @click="loadActivities">搜索</el-button>
    </div>

    <el-table :data="activities" v-loading="isLoading" style="width: 100%; margin-top: 20px;">
      <el-table-column prop="activityId" label="活动ID" width="150"></el-table-column>
      <el-table-column prop="activityName" label="活动名称" width="180"></el-table-column>
      <el-table-column prop="location" label="地点" width="120"></el-table-column>
      <el-table-column prop="startTime" label="开始时间" width="180"></el-table-column>
      <el-table-column prop="endTime" label="结束时间" width="180"></el-table-column>
      <el-table-column prop="recruitmentCount" label="招募" width="80"></el-table-column>
      <el-table-column prop="acceptedCount" label="录取" width="80"></el-table-column>
      <el-table-column prop="activityStatus" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusTagType(row.activityStatus)">{{ row.activityStatus }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="creationTime" label="创建时间" width="180"></el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" @click="viewDetails(row)">详情</el-button>
          <el-button size="small" type="danger" @click="cancelActivity(row)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>

  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import { useOrganizationStore } from '@/stores/organizationStore.js'; //
import request from '@/utils/request.js'; //
import { ElMessage, ElMessageBox } from 'element-plus';

const organizationStore = useOrganizationStore();
const activities = ref([]); // 存储活动列表
const isLoading = ref(false); // 加载状态

const searchForm = reactive({
  activityName: '',
  activityStatus: '',
});

// 这里我们假设后端 `/byOrg/{orgId}` 接口暂时不支持分页，只返回全部列表。
// 如果后端支持分页，需要修改 loadActivities 方法并启用 el-pagination。
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});

// 加载活动列表
const loadActivities = async () => {
  if (!organizationStore.currentOrganizationId) {
    ElMessage.warning('请先登录组织账户以查看活动记录。');
    return;
  }

  isLoading.value = true;
  try {
    // 构建查询参数
    const params = {
      // 如果后端 /byOrg/{orgId} 接口支持 activityName 和 activityStatus 过滤，可以在这里添加
      // activityName: searchForm.activityName,
      // activityStatus: searchForm.activityStatus,
      // 由于目前 byOrg/{orgId} 只按ID查询，我们可能需要在前端进行筛选，或者后端扩展接口
    };

    // 直接调用后端接口，传入组织ID
    const res = await request.get(`/volunteerActivity/byOrg/${organizationStore.currentOrganizationId}`, { params });

    if (res.code === '200' && res.data) {
      // 如果后端返回的是完整列表，前端进行筛选
      let filteredActivities = res.data;
      if (searchForm.activityName) {
        filteredActivities = filteredActivities.filter(activity =>
            activity.activityName.includes(searchForm.activityName)
        );
      }
      if (searchForm.activityStatus) {
        filteredActivities = filteredActivities.filter(activity =>
            activity.activityStatus === searchForm.activityStatus
        );
      }
      activities.value = filteredActivities;
      pagination.total = filteredActivities.length; // 假设是前端分页的总数
    } else {
      ElMessage.error(res.msg || '获取活动列表失败');
    }
  } catch (error) {
    console.error('加载活动列表失败:', error);
    ElMessage.error('网络错误或服务器异常');
  } finally {
    isLoading.value = false;
  }
};

// 查看活动详情 (这里只是一个占位符，您可能需要跳转到详情页面)
const viewDetails = (row) => {
  ElMessage.info(`查看活动详情: ${row.activityName} (ID: ${row.activityId})`);
  // 实际应用中，这里会使用 router.push 跳转到活动详情页，并传入活动ID
  // router.push({ name: 'OrganizationDetailedActivityInfo', query: { activityId: row.activityId } });
};

// 取消活动
const cancelActivity = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要取消活动 "${row.activityName}" 吗？取消后活动状态将变为“已停用”，且已通过/待审核的报名都将取消。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    // 调用后端取消活动的接口
    // 假设有一个接口 PUT /volunteerActivity/cancel，或者调用 sp_OrgCancelEvent 对应的接口
    // 这里我们使用 Controller 中 sp_OrgCancelEvent 对应的接口：POST /organization/cancelEvent
    // 注意：sp_OrgCancelEvent 期望 EventType 和 EventID，以及 OperatingOrgID
    const payload = {
      operatingOrgId: organizationStore.currentOrganizationId, // 传递当前组织ID
      eventType: 'Activity', // 固定为 Activity
      eventID: row.activityId, // 活动ID
    };

    const res = await request.post('/organization/cancelEvent', payload); // 假设后端接口是 POST /organization/cancelEvent

    if (res.code === '200') {
      ElMessage.success('活动已成功取消！');
      loadActivities(); // 刷新列表
    } else {
      ElMessage.error(res.msg || '取消活动失败。');
    }
  } catch (error) {
    if (error !== 'cancel') { // 阻止用户取消弹窗时报错
      console.error('取消活动请求失败:', error);
      ElMessage.error('网络错误或系统异常，取消活动失败。');
    }
  }
};

// 根据活动状态返回不同的Tag样式
const getStatusTagType = (status) => {
  switch (status) {
    case '待审核': return 'info';
    case '审核通过': return 'success';
    case '进行中': return ''; // 默认颜色
    case '已结束': return 'info';
    case '审核不通过': return 'warning';
    case '已停用': return 'danger';
    default: return '';
  }
};

// 组件挂载时加载活动列表
onMounted(() => {
  loadActivities();
});

// 如果后端分页功能在 /byOrg/{orgId} 接口上，需要启用以下方法和 el-pagination
// const handleSizeChange = (newSize) => {
//   pagination.pageSize = newSize;
//   loadActivities();
// };
// const handleCurrentChange = (newPage) => {
//   pagination.pageNum = newPage;
//   loadActivities();
// };
</script>

<style scoped>
.activity-records-container {
  padding: 20px;
}

.search-filter-area {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.el-input, .el-select {
  flex: 1; /* 让输入框和选择框在 flex 布局中自动伸缩 */
  max-width: 200px; /* 控制最大宽度 */
}
</style>