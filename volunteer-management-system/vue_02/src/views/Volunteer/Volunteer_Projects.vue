<template>
  <div class="projects-page">
    <div class="projects-title">
      <span>我的志愿旅程</span>
    </div>

    <div class="projects-tabs-bar">
      <el-tabs v-model="activeTab" class="projects-tabs" @tab-click="handleTabClick">
        <el-tab-pane label="我的志愿活动" name="myActivities"></el-tab-pane>
        <el-tab-pane label="已报名活动" name="pendingActivities"></el-tab-pane>
      </el-tabs>
      <el-button class="more-btn" @click="goToActivitiesMore" :icon="Plus" round>发现更多</el-button>
    </div>

    <el-table
      v-if="activeTab === 'myActivities'"
      :data="myActivitiesPageData"
      border style="width: 100%;" header-cell-class-name="table-header"
      v-loading="isLoading" empty-text="您还没有已参与的活动"
    >
      <el-table-column prop="name" label="活动名称" align="center" />
      <el-table-column prop="position" label="我的岗位" align="center" />
      <el-table-column prop="signIn" label="是否签到" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.signIn === '是' ? 'success' : 'info'">{{ scope.row.signIn }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="我的评分" align="center">
        <template #default="scope">
          <span v-if="scope.row.score">{{ scope.row.score }} 分</span>
          <el-tag v-else type="info">未评分</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="组织方评分" align="center">
        <template #default="scope">
          <span v-if="scope.row.organisationScore">{{ scope.row.organisationScore }} 分</span>
          <el-tag v-else type="warning">待评分</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200">
        <template #default="scope">
          <el-button size="small" type="primary" @click="openEvaluateDialog(scope.row)" :disabled="!!scope.row.score">评价</el-button>
          <el-button size="small" type="danger" @click="openComplaintDialog(scope.row)">投诉</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-table
      v-if="activeTab === 'pendingActivities'"
      :data="pendingActivitiesPageData"
      border style="width: 100%;" header-cell-class-name="table-header"
      v-loading="isLoading" empty-text="您还没有已报名的活动"
    >
      <el-table-column prop="name" label="活动名称" align="center">
        <template #default="scope">
          <span style="color:#d9001b;font-weight:bold;">{{ scope.row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="position" label="意向岗位" align="center" />
      <el-table-column prop="applicationStatus" label="报名状态" align="center" />
      <el-table-column label="操作" align="center" width="200">
        <template #default="scope">
          <el-button type="danger" size="small" @click="handleWithdraw(scope.row)" :disabled="scope.row.applicationStatus !== '待审核'">撤回</el-button>
          <el-button type="primary" plain size="small" @click="handleChangePosition(scope.row)" :disabled="scope.row.applicationStatus !== '待审核'">更换岗位</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="activeTab === 'myActivities' && myActivities.length > pageSize"
      style="margin-top: 24px; text-align: right;"
      background layout="prev, pager, next"
      :total="myActivities.length" :page-size="pageSize" v-model:current-page="myCurrentPage"
    />
    <el-pagination
      v-if="activeTab === 'pendingActivities' && pendingActivities.length > pageSize"
      style="margin-top: 24px; text-align: right;"
      background layout="prev, pager, next"
      :total="pendingActivities.length" :page-size="pageSize" v-model:current-page="pendingCurrentPage"
    />

    <el-dialog title="为活动组织方评分" v-model="evaluateDialogVisible" width="400px">
      <div style="text-align: center;">
        <el-rate v-model="evaluateScore" :max="10" show-score score-template="{value} 分" size="large"/>
      </div>
      <template #footer>
        <el-button @click="evaluateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEvaluate">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog title="填写投诉信息" v-model="complaintDialogVisible" width="500px">
      <el-form :model="complaintForm" ref="complaintFormRef" label-width="80px">
        <el-form-item label="投诉类型" prop="type" :required="true">
          <el-select v-model="complaintForm.type" placeholder="请选择投诉类型" style="width:100%">
            <el-option v-for="item in complaintTypes" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="投诉内容" prop="content" :required="true">
          <el-input type="textarea" v-model="complaintForm.content" :rows="4" placeholder="请详细描述您的问题..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="complaintDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitComplaint">提交投诉</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import { useUserStore } from '@/stores/userStore';
import request from '@/utils/request';

const router = useRouter();
const userStore = useUserStore();
const isLoading = ref(false);

// --- 核心数据 ---
const myActivities = ref([]); // 已参与
const pendingActivities = ref([]); // 已报名
const activeTab = ref('myActivities'); // 默认标签

// --- 分页 ---
const pageSize = 5;
const myCurrentPage = ref(1);
const pendingCurrentPage = ref(1);
const myActivitiesPageData = computed(() => {
  const start = (myCurrentPage.value - 1) * pageSize;
  return myActivities.value.slice(start, start + pageSize);
});
const pendingActivitiesPageData = computed(() => {
  const start = (pendingCurrentPage.value - 1) * pageSize;
  return pendingActivities.value.slice(start, start + pageSize);
});

// --- 数据获取 ---
const fetchAllData = () => {
  const volunteerId = userStore.detailedVolunteerInfo.volunteerId;
  if (!volunteerId) {
    ElMessage.warning('请先登录');
    return;
  }
  isLoading.value = true;
  Promise.all([
    request.get(`/api/participation/my-activities/${volunteerId}`),
    request.get(`/api/application/my-pending/${volunteerId}`)
  ]).then(([participationRes, applicationRes]) => {
    if (participationRes.code === '200') myActivities.value = participationRes.data;
    if (applicationRes.code === '200') pendingActivities.value = applicationRes.data;
  }).catch(err => {
    ElMessage.error('数据加载失败');
    console.error(err);
  }).finally(() => {
    isLoading.value = false;
  });
};

onMounted(fetchAllData);

// --- 标签页切换 ---
const handleTabClick = (tab) => {
  activeTab.value = tab.paneName;
};

// --- “已报名活动” 操作 ---
const handleWithdraw = async (row) => {
  await ElMessageBox.confirm('确定要撤回此报名申请吗?', '提示', { type: 'warning' });
  await request.put('/api/application/withdraw', { applicationId: row.applicationId });
  ElMessage.success('撤回成功');
  fetchAllData(); // 重新加载数据
};
const handleChangePosition = (row) => {
  ElMessage.info('更换岗位功能待实现');
};
const goToActivitiesMore = () => router.push('/activities-list'); // 假设的路径

// --- “我的志愿活动” 操作 ---
// 评价
const evaluateDialogVisible = ref(false);
const evaluateScore = ref(0);
const currentEvaluateRow = ref(null);
const openEvaluateDialog = (row) => {
  currentEvaluateRow.value = row;
  evaluateScore.value = row.score || 0;
  evaluateDialogVisible.value = true;
};
const submitEvaluate = async () => {
  await request.put('/api/participation/rate', {
    volunteerId: currentEvaluateRow.value.volunteerId,
    actualPositionId: currentEvaluateRow.value.actualPositionId,
    rating: evaluateScore.value
  });
  ElMessage.success('评价成功');
  evaluateDialogVisible.value = false;
  fetchAllData();
};

// 投诉
const complaintDialogVisible = ref(false);
const complaintForm = ref({});
const complaintTypes = ['服务质量', '行为不当', '信息虚假', '活动违规', '其他'];
const currentComplaintRow = ref(null);

const openComplaintDialog = (row) => {
  currentComplaintRow.value = row;
  complaintForm.value = { type: '', content: '' };
  complaintDialogVisible.value = true;
};
const submitComplaint = async () => {
    if (!complaintForm.value.type || !complaintForm.value.content) {
        return ElMessage.warning('请填写完整的投诉信息');
    }
    const complaintData = {
        complainantId: userStore.detailedVolunteerInfo.volunteerId,
        complaintTargetId: currentComplaintRow.value.activityId, // 或组织ID
        complaintType: complaintForm.value.type,
        complaintContent: complaintForm.value.content,
    };
    await request.post('/api/participation/complaint', complaintData);
    ElMessage.success('投诉已提交');
    complaintDialogVisible.value = false;
};
</script>

<style scoped>
/* 模仿您提供的样式 */
.projects-page { background: #fff; border-radius: 8px; box-shadow: 0 2px 12px 0 rgba(0,0,0,0.06); padding: 0 0 32px 0; min-height: 400px; }
.projects-title { background: #fff0f0; color: #ff0000; font-weight: bold; font-size: 20px; padding: 18px 32px 8px 32px; border-bottom: 3px solid #ff0000; margin-bottom: 0; }
.projects-tabs-bar { display: flex; align-items: center; background: #fff0f0; padding: 0 32px; }
.projects-tabs { flex: 1; background: transparent; border-bottom: none; }
.more-btn { margin-left: 16px; background: #ff0000; color: #fff; border: none; }
.projects-tabs :deep(.el-tabs__item.is-active) { color: #ff0000 !important; font-weight: bold; }
.projects-tabs :deep(.el-tabs__active-bar) { background-color: #ff0000 !important; }
.projects-tabs :deep(.el-tabs__item) { font-size: 16px; }
.table-header { background: #fff0f0 !important; color: #ff0000 !important; font-weight: bold; }
.empty-box { display: flex; flex-direction: column; align-items: center; margin: 60px 0 0 0; color: #aaa; font-size: 16px; }
.empty-box img { width: 80px; margin-bottom: 12px; opacity: 0.6; }
</style>