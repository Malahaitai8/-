<template>
  <div class="training-page">
    <div class="training-title">我的培训</div>
    <div class="filter-buttons">
      <button @click="applyFilter('all')" :class="{ active: currentFilter === 'all' }">全部</button>
      <!-- 移除待审核和审核不通过按钮 -->
      <button @click="applyFilter(TRAINING_STATUS.ONGOING)" :class="{ active: currentFilter === TRAINING_STATUS.ONGOING }">进行中</button>
      <button @click="applyFilter(TRAINING_STATUS.ENDED)" :class="{ active: currentFilter === TRAINING_STATUS.ENDED }">已结束</button>
      <button @click="applyFilter(TRAINING_STATUS.APPROVED)" :class="{ active: currentFilter === TRAINING_STATUS.APPROVED }">审核通过</button>
      <button @click="applyFilter(TRAINING_STATUS.DISABLED)" :class="{ active: currentFilter === TRAINING_STATUS.DISABLED }">已停用</button>
      <button @click="applyFilter('TO_BE_EVALUATED')" :class="{ active: currentFilter === 'TO_BE_EVALUATED' }">待评价</button>
    </div>

    <el-table
        v-if="pageData.length"
        :data="pageData"
        border
        style="width: 100%; margin-top: 20px;"
        header-cell-class-name="table-header"
    >
      <el-table-column prop="trainingName" label="培训名称" align="center" width="180"></el-table-column>
      <el-table-column prop="startTime" label="开始日期" align="center" width="160"></el-table-column>
      <el-table-column prop="endTime" label="结束日期" align="center" width="160"></el-table-column>
      <el-table-column prop="trainingStatus" label="培训状态" align="center"></el-table-column>
      <el-table-column prop="isCheckedIn" label="是否签到" align="center"></el-table-column>

      <el-table-column label="我的评分" align="center">
        <template #default="scope">
          <span v-if="scope.row.volunteerToOrgRating">{{ scope.row.volunteerToOrgRating }} 分</span>
          <el-tag v-else type="info">未评分</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="组织方评分" align="center">
        <template #default="scope">
          <span v-if="scope.row.orgToVolunteerRating">{{ scope.row.orgToVolunteerRating }} 分</span>
          <el-tag v-else type="warning">待评分</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" width="240" fixed="right">
        <template #default="scope">
          <el-button
              size="small"
              type="primary"
              @click="openEvaluateDialog(scope.row)"
              :disabled="scope.row.trainingStatus !== '已结束' || !!scope.row.volunteerToOrgRating || isAfterEvaluationWindow(scope.row.endTime)"
          >
            {{ scope.row.volunteerToOrgRating ? '已评价' : '评价' }}
          </el-button>

          <el-button size="small" type="danger" @click="openComplaintDialog(scope.row)" style="margin-left: 10px;">我要投诉</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-else class="empty-box">
      <img src="https://img.alicdn.com/imgextra/i4/O1CN01v7Qw1B1QwQwQwQw_!!6000000002007-2-tps-200-200.png" alt="empty"></img>
      <div>暂无相关培训信息</div>
    </div>

    <el-pagination
        v-if="trainings.length > pageSize"
        style="margin-top: 24px; text-align: right;"
        background
        layout="prev, pager, next, jumper"
        :total="trainings.length"
        :page-size="pageSize"
        v-model:current-page="currentPage"
    ></el-pagination>

    <el-dialog :title="evaluateDialog.isEdit ? '修改我的评分' : '为本次培训评分'" v-model="evaluateDialog.visible" width="400px" @close="resetEvaluateDialog">
      <div style="text-align: center;">
        <el-rate
            v-model="evaluateDialog.score"
            :max="10"
            show-score
            score-template="{value} 分"
            size="large"
        ></el-rate>
      </div>
      <template #footer>
        <el-button @click="evaluateDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitEvaluate">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog title="我要投诉" v-model="complaintDialog.visible" width="500px" @close="resetComplaintDialog">
      <el-form :model="complaintDialog.form" ref="complaintFormRef" label-width="80px">
        <el-form-item label="投诉对象">
          <el-input :value="complaintDialog.targetName" disabled></el-input>
        </el-form-item>
        <el-form-item label="投诉类型" prop="type" :rules="[{ required: true, message: '请选择投诉类型' }]">
          <el-select v-model="complaintDialog.form.type" placeholder="请选择投诉类型">
            <el-option label="服务质量" value="服务质量"></el-option>
            <el-option label="行为不当" value="行为不当"></el-option>
            <el-option label="信息虚假" value="信息虚假"></el-option>
            <el-option label="活动违规" value="活动违规"></el-option>
            <el-option label="其他" value="其他"></el-option>
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
import { ref, reactive, computed, watch } from 'vue';
import { useUserStore } from '@/stores/userStore';
import request from '@/utils/request';
import { ElMessage } from 'element-plus';

const userStore = useUserStore();
const trainings = ref([]); // Holds all fetched trainings

const pageSize = 5;
const currentPage = ref(1);
// Computed property to display filtered and paginated data
const pageData = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return filteredTrainings.value.slice(start, start + pageSize); // Use filteredTrainings here
});

const evaluateDialog = reactive({
  visible: false,
  score: 0,
  trainingId: null,
  isEdit: false // 用于判断是新增评价还是修改
});

const currentFilter = ref('all'); // Tracks the active filter
const filteredTrainings = ref([]); // Holds trainings after filtering

// Method to check if the current date is more than 7 days past the end time
const isAfterEvaluationWindow = (endTimeString) => {
  if (!endTimeString) return false;
  // Create a Date object from the end time string
  const endDate = new Date(endTimeString);
  // Add 7 days to the end date
  const sevenDaysAfterEnd = new Date(endDate);
  sevenDaysAfterEnd.setDate(endDate.getDate() + 7);
  // Get the current date
  const now = new Date();
  // Return true if the current date is after the 7-day evaluation window
  return now > sevenDaysAfterEnd;
};

// Define training statuses and the new 'TO_BE_EVALUATED' type
const TRAINING_STATUS = {
  // 移除 PENDING 和 REJECTED
  APPROVED: '审核通过',
  ONGOING: '进行中',
  ENDED: '已结束',
  DISABLED: '已停用',
};

const openEvaluateDialog = (row) => {
  evaluateDialog.trainingId = row.trainingId;
  evaluateDialog.isEdit = !!row.volunteerToOrgRating; // 使用 !! 转换为布尔值
  evaluateDialog.score = row.volunteerToOrgRating || 0;
  evaluateDialog.visible = true;
};

const resetEvaluateDialog = () => {
    evaluateDialog.visible = false;
    evaluateDialog.score = 0;
    evaluateDialog.trainingId = null;
    evaluateDialog.isEdit = false;
}

const submitEvaluate = async () => {
  if (evaluateDialog.score === 0) {
    ElMessage.warning('请选择评分');
    return;
  }
  try {
    const payload = {
      volunteerId: userStore.detailedVolunteerInfo.volunteerId,
      trainingId: evaluateDialog.trainingId,
      rating: evaluateDialog.score,
    };

    const res = await request.post('/volunteerTraining/rate', payload);
    if (res.code === '200') {
      ElMessage.success('评价成功！');
      resetEvaluateDialog();
      fetchMyTrainings(userStore.detailedVolunteerInfo.volunteerId); // 重新加载数据以更新状态
    } else {
      ElMessage.error(res.msg || '评价提交失败');
    }
  } catch (error) {
    ElMessage.error('网络错误，评价失败');
  }
};

const complaintDialog = reactive({
  visible: false,
  targetId: null,
  targetName: '',
  form: {type: '', content: ''}
});
const complaintFormRef = ref(null);

const openComplaintDialog = (row) => {
  complaintDialog.targetId = row.orgId;
  complaintDialog.targetName = row.orgName;
  complaintDialog.visible = true;
};

const resetComplaintDialog = () => {
  complaintDialog.visible = false;
  if (complaintFormRef.value) {
    complaintFormRef.value.resetFields();
  }
  complaintDialog.form = {type: '', content: ''};
}

const submitComplaint = async () => {
  if (!complaintFormRef.value) return;
  complaintFormRef.value.validate(async (valid) => {
    if (valid) {
      const payload = {
        complainantId: userStore.detailedVolunteerInfo.volunteerId,
        complaintTargetId: complaintDialog.targetId,
        complaintType: complaintDialog.form.type,
        complaintContent: complaintDialog.form.content,
      };
      try {
        const res = await request.post('/complaint/add', payload);
        if (res.code === '200') {
          ElMessage.success('投诉已成功提交');
          resetComplaintDialog();
        } else {
          ElMessage.error(res.msg || '投诉提交失败');
        }
      } catch (error) {
        ElMessage.error('网络错误，投诉失败');
      }
    }
  });
};

const fetchMyTrainings = async (volunteerId) => {
  if (!volunteerId) return;
  try {
    const res = await request.get(`/volunteerTraining/my-participations/${volunteerId}`);
    if (res.code === '200' && Array.isArray(res.data)) {
      trainings.value = res.data;
      filterTrainings(); // Apply filter after fetching
    } else {
      trainings.value = [];
      filterTrainings(); // Clear filtered list on error
      ElMessage.error(res.msg || '获取培训列表失败');
    }
  } catch (error) {
    trainings.value = [];
    filterTrainings(); // Clear filtered list on error
    ElMessage.error('网络错误，无法获取培训列表');
  }
};

// New function to filter trainings based on currentFilter
const filterTrainings = () => {
  currentPage.value = 1; // Reset page to 1 when filter changes
  if (currentFilter.value === 'all') {
    filteredTrainings.value = [...trainings.value];
  } else if (currentFilter.value === 'TO_BE_EVALUATED') {
    filteredTrainings.value = trainings.value.filter(training =>
        training.trainingStatus === TRAINING_STATUS.ENDED && // Must be ended
        !training.volunteerToOrgRating && // Must not have been rated by volunteer
        !isAfterEvaluationWindow(training.endTime) // Must be within the evaluation window
    );
  } else {
    filteredTrainings.value = trainings.value.filter(training =>
        training.trainingStatus === currentFilter.value
    );
  }
};

const applyFilter = (filterType) => {
  currentFilter.value = filterType;
  filterTrainings(); // Apply new filter
};


watch(() => userStore.detailedVolunteerInfo?.volunteerId, (newId) => {
  if (newId) {
    fetchMyTrainings(newId);
  } else {
    trainings.value = [];
    filteredTrainings.value = [];
  }
}, {immediate: true});

</script>

<style scoped>
.training-page {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.06);
  padding: 0 32px 32px;
  min-height: 400px;
}

.training-title {
  background: #fff0f0;
  color: #c32f1b;
  font-weight: bold;
  font-size: 20px;
  padding: 18px 32px;
  margin: 0 -32px 20px -32px;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
  border-bottom: 2px solid #fde2e2;
}

.filter-buttons {
  margin-top: 20px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
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
  background-color: #007bff;
  color: white;
  border-color: #007bff;
}

.filter-buttons button:hover:not(.active) {
  background-color: #e9e9e9;
  border-color: #c0c0c0;
}

.table-header {
  background: #f8f8f9 !important;
  color: #515a6e !important;
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
