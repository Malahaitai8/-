<template>
  <div class="training-page">
    <!-- 红色标题栏 -->
    <!-- 培训表格 -->
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

      <!-- 新增：我的评分 列 -->
      <el-table-column label="我的评分" align="center">
        <template #default="scope">
          <!-- 修改点：使用更有弹性的 "truthy" 判断 -->
          <span v-if="scope.row.volunteerToOrgRating">{{ scope.row.volunteerToOrgRating }} 分</span>
          <el-tag v-else type="info">未评分</el-tag>
        </template>
      </el-table-column>

      <!-- 新增：组织方评分 列 -->
      <el-table-column label="组织方评分" align="center">
        <template #default="scope">
          <span v-if="scope.row.orgToVolunteerRating">{{ scope.row.orgToVolunteerRating }} 分</span>
          <el-tag v-else type="warning">待评分</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" width="240" fixed="right">
        <template #default="scope">
          <!--
            【核心修改点】
            按钮逻辑现在使用更健壮的 "truthiness" 检查。
            在JavaScript中，null、0、undefined 都会被视为 false。
            这样，无论后端返回 null 还是 0，都能正确判断为“未评分”。
          -->
          <el-button
              size="small"
              type="primary"
              @click="openEvaluateDialog(scope.row)"
              :disabled="scope.row.trainingStatus !== '已结束' || !!scope.row.volunteerToOrgRating"
          >
            {{ scope.row.volunteerToOrgRating ? '已评价' : '评价' }}
          </el-button>

          <el-button size="small" type="danger" @click="openComplaintDialog(scope.row)" style="margin-left: 10px;">我要投诉</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 空数据提示 -->
    <div v-else class="empty-box">
      <img src="https://img.alicdn.com/imgextra/i4/O1CN01v7Qw1B1QwQwQwQw_!!6000000002007-2-tps-200-200.png" alt="empty"></img>
      <div>暂无相关培训信息</div>
    </div>

    <!-- 分页器 -->
    <el-pagination
        v-if="trainings.length > pageSize"
        style="margin-top: 24px; text-align: right;"
        background
        layout="prev, pager, next, jumper"
        :total="trainings.length"
        :page-size="pageSize"
        v-model:current-page="currentPage"
    ></el-pagination>

    <!-- 评价弹窗 -->
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

    <!-- 投诉弹窗 (代码保持不变) -->
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
const trainings = ref([]);

const pageSize = 5;
const currentPage = ref(1);
const pageData = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return trainings.value.slice(start, start + pageSize);
});

const evaluateDialog = reactive({
  visible: false,
  score: 0,
  trainingId: null,
  isEdit: false // 用于判断是新增评价还是修改
});

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
    } else {
      trainings.value = [];
      ElMessage.error(res.msg || '获取培训列表失败');
    }
  } catch (error) {
    trainings.value = [];
    ElMessage.error('网络错误，无法获取培训列表');
  }
};

watch(() => userStore.detailedVolunteerInfo?.volunteerId, (newId) => {
  if (newId) {
    fetchMyTrainings(newId);
  } else {
    trainings.value = [];
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
