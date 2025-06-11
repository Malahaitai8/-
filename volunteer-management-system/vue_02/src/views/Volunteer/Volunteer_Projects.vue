<template>
  <div class="activities-page">
    <div class="page-title">我的志愿活动</div>

    <div class="filter-buttons">
      <button @click="applyFilter('all')" :class="{ active: currentFilter === 'all' }">全部</button>
      <button @click="applyFilter(ACTIVITY_STATUS.ONGOING)" :class="{ active: currentFilter === ACTIVITY_STATUS.ONGOING }">进行中</button>
      <button @click="applyFilter(ACTIVITY_STATUS.ENDED)" :class="{ active: currentFilter === ACTIVITY_STATUS.ENDED }">已结束</button>
      <button @click="applyFilter('TO_BE_EVALUATED')" :class="{ active: currentFilter === 'TO_BE_EVALUATED' }">待评价</button>
    </div>

    <el-table
        v-if="pageData.length"
        :data="pageData"
        border
        style="width: 100%; margin-top: 20px;"
        header-cell-class-name="table-header"
        v-loading="isLoading"
        empty-text="暂无相关活动信息"
    >
      <el-table-column prop="activityName" label="活动名称" align="center" width="180"></el-table-column>

      <el-table-column prop="positionName" label="我的岗位" align="center" width="180"></el-table-column>

      <el-table-column prop="startTime" label="开始日期" align="center" width="160">
        <template #default="scope">{{ formatDate(scope.row.startTime) }}</template>
      </el-table-column>
      <el-table-column prop="endTime" label="结束日期" align="center" width="160">
        <template #default="scope">{{ formatDate(scope.row.endTime) }}</template>
      </el-table-column>
      <el-table-column prop="activityStatus" label="活动状态" align="center">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.activityStatus)">{{ scope.row.activityStatus }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isCheckedIn" label="是否签到" align="center">
         <template #default="scope">
          <el-tag :type="scope.row.isCheckedIn === '是' ? 'success' : 'danger'" size="small">{{ scope.row.isCheckedIn }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="我给组织方打分" align="center">
        <template #default="scope">
          <span v-if="scope.row.volunteerToOrgRating">{{ scope.row.volunteerToOrgRating }} 分</span>
          <el-tag v-else type="info">未评分</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="组织方对我的评分" align="center">
        <template #default="scope">
          <span v-if="scope.row.orgToVolunteerRating">{{ scope.row.orgToVolunteerRating }} 分</span>
          <el-tag v-else type="warning">待评分</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" width="240" fixed="right">
        <template #default="scope">
          <el-button
              size="small" type="primary" @click="openEvaluateDialog(scope.row)"
              :disabled="scope.row.activityStatus !== '已结束' || !!scope.row.volunteerToOrgRating || isAfterEvaluationWindow(scope.row.endTime)"
              :title="getEvaluationButtonTooltip(scope.row)">
            {{ scope.row.volunteerToOrgRating ? '已评价' : '评价' }}
          </el-button>
          <el-button size="small" type="danger" @click="openComplaintDialog(scope.row)" style="margin-left: 10px;">我要投诉</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-else class="empty-box" v-loading="isLoading">
      <img v-if="!isLoading" src="https://img.alicdn.com/imgextra/i4/O1CN01v7Qw1B1QwQwQw_!!6000000002007-2-tps-200-200.png" alt="empty">
      <div v-if="!isLoading">暂无相关活动信息</div>
    </div>

    <el-pagination
        v-if="filteredActivities.length > pageSize"
        style="margin-top: 24px; text-align: right;"
        background layout="prev, pager, next, jumper"
        :total="filteredActivities.length" :page-size="pageSize" v-model:current-page="currentPage"
    ></el-pagination>

    <el-dialog :title="evaluateDialog.isEdit ? '修改我的评分' : '为本次活动评分'" v-model="evaluateDialog.visible" width="400px" @close="resetEvaluateDialog">
      <div style="text-align: center;">
        <el-rate v-model="evaluateDialog.score" :max="10" show-score score-template="{value} 分" size="large"/>
      </div>
      <template #footer>
        <el-button @click="evaluateDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitEvaluate">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog title="我要投诉" v-model="complaintDialog.visible" width="500px" @close="resetComplaintDialog">
      <el-form :model="complaintDialog.form" ref="complaintFormRef" label-width="80px">
        <el-form-item label="投诉对象"><el-input :value="complaintDialog.targetName" disabled></el-input></el-form-item>
        <el-form-item label="投诉类型" prop="type" :rules="[{ required: true, message: '请选择投诉类型' }]">
          <el-select v-model="complaintDialog.form.type" placeholder="请选择投诉类型" style="width:100%;">
            <el-option v-for="type in complaintTypes" :key="type" :label="type" :value="type"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="投诉内容" prop="content" :rules="[{ required: true, message: '请填写投诉内容' }]">
          <el-input type="textarea" v-model="complaintDialog.form.content" :rows="4" placeholder="请详细描述您的问题..."></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="complaintDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitComplaint">提交投诉</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// Script部分完全不变，因为后端已经返回了positionName字段
import { ref, reactive, computed, watch, onMounted } from 'vue';
import { useUserStore } from '@/stores/userStore';
import request from '@/utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';

const userStore = useUserStore();
const allActivities = ref([]);
const filteredActivities = ref([]);
const isLoading = ref(false);
const pageSize = 5;
const currentPage = ref(1);
const currentFilter = ref('all');

const pageData = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return filteredActivities.value.slice(start, start + pageSize);
});

const ACTIVITY_STATUS = { ONGOING: '进行中', ENDED: '已结束', APPROVED: '审核通过' };

const isAfterEvaluationWindow = (endTimeString) => {
  if (!endTimeString) return true;
  const sevenDaysAfterEnd = new Date(new Date(endTimeString).getTime() + 7 * 24 * 60 * 60 * 1000);
  return new Date() > sevenDaysAfterEnd;
};

const getEvaluationButtonTooltip = (row) => {
  if (row.activityStatus !== '已结束') return '活动尚未结束，无法评价';
  if (!!row.volunteerToOrgRating) return '您已经评价过此活动';
  if (isAfterEvaluationWindow(row.endTime)) return '已超过7天评价期';
  return '';
};

const evaluateDialog = reactive({ visible: false, score: 0, participation: null, isEdit: false });
const openEvaluateDialog = (row) => {
  evaluateDialog.participation = row;
  evaluateDialog.isEdit = !!row.volunteerToOrgRating;
  evaluateDialog.score = row.volunteerToOrgRating || 0;
  evaluateDialog.visible = true;
};
const resetEvaluateDialog = () => { Object.assign(evaluateDialog, { visible: false, score: 0, participation: null, isEdit: false }); };
const submitEvaluate = async () => {
  if (evaluateDialog.score === 0) return ElMessage.warning('请选择评分');
  try {
    const payload = {
      volunteerId: evaluateDialog.participation.volunteerId,
      actualPositionId: evaluateDialog.participation.actualPositionId,
      rating: evaluateDialog.score,
    };
    const res = await request.put('/api/participation/rate', payload);
    if (res.code === '200') {
      ElMessage.success('评价成功！');
      resetEvaluateDialog();
      fetchMyActivities();
    } else { ElMessage.error(res.msg || '评价提交失败'); }
  } catch (error) { ElMessage.error('网络错误，评价失败'); }
};

const complaintDialog = reactive({ visible: false, targetId: null, targetName: '', form: {type: '', content: ''}});
const complaintFormRef = ref(null);
const complaintTypes = ['服务质量', '行为不当', '信息虚假', '活动违规', '其他'];
const openComplaintDialog = (row) => {
  complaintDialog.targetId = row.orgId;
  complaintDialog.targetName = row.orgName;
  complaintDialog.visible = true;
};
const resetComplaintDialog = () => {
  if (complaintFormRef.value) complaintFormRef.value.resetFields();
  Object.assign(complaintDialog, { visible: false, targetId: null, targetName: '', form: {type: '', content: ''}});
};
const submitComplaint = async () => {
  if (!complaintFormRef.value) return;
  await complaintFormRef.value.validate(async (valid) => {
    if (valid) {
      const payload = {
        complainantId: userStore.detailedVolunteerInfo.volunteerId,
        complaintTargetId: complaintDialog.targetId, ...complaintDialog.form
      };
      try {
        const res = await request.post('/api/complaint/submit', payload);
        if (res.code === '200') {
          ElMessage.success('投诉已成功提交');
          resetComplaintDialog();
        } else { ElMessage.error(res.msg || '投诉提交失败'); }
      } catch (error) { ElMessage.error('网络错误，投诉失败'); }
    }
  });
};

const fetchMyActivities = async () => {
  const volunteerId = userStore.detailedVolunteerInfo.volunteerId;
  if (!volunteerId) return;
  isLoading.value = true;
  try {
    const res = await request.get(`/api/participation/my-activities/${volunteerId}`);
    if (res.code === '200' && Array.isArray(res.data)) {
      allActivities.value = res.data;
      applyFilter('all');
    } else { ElMessage.error(res.msg || '获取活动列表失败'); }
  } catch (error) { ElMessage.error('网络错误，无法获取活动列表'); } finally { isLoading.value = false; }
};

const filterActivities = () => {
  if (currentFilter.value === 'all') {
    filteredActivities.value = [...allActivities.value];
  } else if (currentFilter.value === 'TO_BE_EVALUATED') {
    filteredActivities.value = allActivities.value.filter(act =>
        act.activityStatus === '已结束' &&
        !act.volunteerToOrgRating &&
        !isAfterEvaluationWindow(act.endTime)
    );
  } else {
    filteredActivities.value = allActivities.value.filter(act => act.activityStatus === currentFilter.value);
  }
};

const applyFilter = (filterType) => {
  currentPage.value = 1;
  currentFilter.value = filterType;
  filterActivities();
};

const formatDate = (time) => {
    if (!time) return 'N/A';
    return new Date(time).toLocaleDateString();
};
const getStatusTagType = (status) => {
    switch (status) {
        case '进行中': return 'success';
        case '已结束': return 'info';
        case '审核通过': return 'primary';
        default: return 'warning';
    }
};

onMounted(() => {
  if (userStore.detailedVolunteerInfo.volunteerId) {
    fetchMyActivities();
  }
});

watch(() => userStore.detailedVolunteerInfo.volunteerId, (newId) => {
  if (newId) fetchMyActivities();
  else {
    allActivities.value = [];
    filteredActivities.value = [];
  }
});
</script>

<style scoped>
/* 样式保持不变 */
.activities-page { background: #fff; border-radius: 8px; box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.06); padding: 0 32px 32px; min-height: 400px; }
.page-title { background: #fff0f0; color: #c32f1b; font-weight: bold; font-size: 20px; padding: 18px 32px; margin: 0 -32px 20px -32px; border-top-left-radius: 8px; border-top-right-radius: 8px; border-bottom: 2px solid #fde2e2; }
.filter-buttons { margin-top: 20px; display: flex; gap: 10px; flex-wrap: wrap; }
.filter-buttons button { background-color: #f0f0f0; color: #333; padding: 8px 15px; border: 1px solid #ddd; border-radius: 4px; cursor: pointer; transition: all 0.2s ease; }
.filter-buttons button.active { background-color: #c32f1b; color: white; border-color: #c32f1b; }
.filter-buttons button:hover:not(.active) { background-color: #e9e9e9; border-color: #c0c0c0; }
.table-header { background: #f8f8f9 !important; color: #515a6e !important; font-weight: bold; }
.empty-box { display: flex; flex-direction: column; align-items: center; justify-content: center; margin-top: 60px; color: #aaa; font-size: 16px; min-height: 200px; }
.empty-box img { width: 80px; margin-bottom: 12px; opacity: 0.6; }
</style>