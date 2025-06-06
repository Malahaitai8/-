<template>
  <el-card>
    <div class="header-placeholder"></div>
    <div class="header">
      <h1>申请志愿活动</h1>
    </div>
    <el-form ref="activityFormRef" :model="activityFormData" :rules="activityFormRules" label-width="200px" style="margin-top: 20px;">
      <el-form-item label="志愿活动名称" prop="activityName">
        <el-input v-model="activityFormData.activityName" placeholder="请输入活动名称"></el-input>
      </el-form-item>
      <el-form-item label="志愿活动开始时间" prop="startTime">
        <el-date-picker
          v-model="activityFormData.startTime"
          type="datetime"
          placeholder="选择开始日期和时间"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
          style="width: 100%;"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="志愿活动结束时间" prop="endTime">
        <el-date-picker
          v-model="activityFormData.endTime"
          type="datetime"
          placeholder="选择结束日期和时间"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
          style="width: 100%;"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="活动地点" prop="location">
        <el-input v-model="activityFormData.location" placeholder="请输入活动地点"></el-input>
      </el-form-item>
      <el-form-item label="计划招募人数" prop="recruitmentCount">
        <el-input-number v-model="activityFormData.recruitmentCount" :min="1" placeholder="请输入招募人数" style="width: 100%;"></el-input-number>
      </el-form-item>
      <el-form-item label="负责人联系方式" prop="contactPersonPhone">
        <el-input v-model="activityFormData.contactPersonPhone" placeholder="请输入负责人手机号"></el-input>
      </el-form-item>
      <!-- 活动时段 activity.duration 字段暂时不直接映射到数据库的 ActivityDurationHours -->
      <!-- 后端可以根据 startTime 和 endTime 计算, 或者未来前端直接提供小时数 -->
      <el-form-item label="预计活动总时长 (小时)" prop="activityDurationHours">
        <el-input-number v-model="activityFormData.activityDurationHours" :min="0" placeholder="预计活动总小时数 (可选)" style="width: 100%;"></el-input-number>
      </el-form-item>
    </el-form>
    <div style="text-align: center; margin-top: 30px;">
      <el-button type="info" @click="goHome">返回主页</el-button>
      <el-button type="primary" @click="submitApplication" :loading="isSubmitting">提交申请</el-button>
      <el-button @click="resetForm">重置表单</el-button>
    </div>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'; // 增加了 watch
import { useRouter } from 'vue-router';
import { useOrganizationStore } from "@/stores/organizationStore.js";
import { useVolunteerActivityStore } from "@/stores/volunteerActivityStore.js";
import { ElMessage } from 'element-plus';

// defineOptions({ name: 'ActivityApplicationPage' });

const router = useRouter();
const organizationStore = useOrganizationStore();
const volunteerActivityStore = useVolunteerActivityStore();

const activityFormRef = ref(null);

const activityFormData = reactive({
  activityName: "",
  startTime: "",
  endTime: "",
  location: "",
  recruitmentCount: 1,
  contactPersonPhone: "",
  activityDurationHours: null,
});

// 新增：用于表示组织数据是否已加载完毕
const orgDataReady = ref(false);

// isSubmitting 现在从 volunteerActivityStore 获取
const isSubmitting = computed(() => volunteerActivityStore.isSubmittingApplication);

const activityFormRules = reactive({
  activityName: [{ required: true, message: '请输入志愿活动名称', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择活动开始时间', trigger: 'change' }],
  endTime: [
    { required: true, message: '请选择活动结束时间', trigger: 'change' },
    { validator: (rule, value, callback) => {
        if (activityFormData.startTime && value && new Date(value) <= new Date(activityFormData.startTime)) {
          callback(new Error('活动结束时间必须晚于开始时间'));
        } else {
          callback();
        }
      }, trigger: 'change'
    }
  ],
  location: [{ required: true, message: '请输入活动地点', trigger: 'blur' }],
  recruitmentCount: [
    { required: true, message: '请输入招募人数', trigger: 'change' },
    { type: 'number', min: 1, message: '招募人数至少为1', trigger: 'change'}
  ],
  contactPersonPhone: [
    { required: true, message: '请输入负责人联系方式', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的11位手机号码', trigger: 'blur'}
  ],
  activityDurationHours: [
    { type: 'number', min: 0, message: '活动时长必须为非负数', trigger: 'change', required: false }
  ]
});

onMounted(async () => {
  console.log('ActivityApplication.vue: onMounted triggered.');
  // 确保 organizationStore 已初始化并且详细信息已加载
  // 理想情况下，initializeStore 应该在应用加载时（如main.js）调用一次
  if (!organizationStore.isAuthenticated && localStorage.getItem('xm-pro-organization')) {
      console.log('ActivityApplication.vue: Organization store not authenticated but session exists, initializing store.');
      await organizationStore.initializeStore(); // initializeStore 应该返回 Promise 或能被 await
  } else if (organizationStore.isAuthenticated && !organizationStore.currentOrganizationId) {
      console.log('ActivityApplication.vue: Organization authenticated but orgId missing, fetching detailed info.');
      await organizationStore.fetchDetailedOrganizationInfo();
  }

  // 检查 orgId 是否已加载
  if (organizationStore.currentOrganizationId) {
    orgDataReady.value = true;
    console.log('ActivityApplication.vue: Org data is ready. Org ID:', organizationStore.currentOrganizationId);
  } else if (organizationStore.isAuthenticated) {
    // 如果已认证但仍未获取到 orgId，可能是异步获取还未完成，可以监听
    console.warn('ActivityApplication.vue: Authenticated, but Org ID not immediately available. Waiting for store update.');
  } else {
    ElMessage.error('组织未登录或会话无效，无法申请活动。请先登录。');
    // router.push('/login'); // 或者跳转到登录页
    orgDataReady.value = false; // 明确标记数据未准备好
  }
});

// 使用 watch 监听 organizationStore.currentOrganizationId 的变化
// 当 orgId 从空变为有效值时，更新 orgDataReady
watch(() => organizationStore.currentOrganizationId, (newOrgId) => {
  if (newOrgId) {
    orgDataReady.value = true;
    console.log('ActivityApplication.vue: Org ID became available through watch:', newOrgId);
  }
});


const goHome = () => {
  router.push('/organization-home');
};

const submitApplication = async () => {
  if (!activityFormRef.value) return;

  // 在提交前再次确认 orgId 是否存在
  const currentOrgId = organizationStore.currentOrganizationId;
  if (!currentOrgId) {
    ElMessage.error('无法获取当前组织ID，操作无法继续。请尝试刷新页面或重新登录。');
    orgDataReady.value = false; // 可能需要重新校验或提示用户
    return;
  }
  // 确保表单是可见且数据准备好的情况下才进行验证
  if (!orgDataReady.value) {
      ElMessage.warning('组织数据尚未准备好，请稍候...');
      return;
  }

  await activityFormRef.value.validate(async (valid) => {
    if (valid) {
      const applicationData = {
        orgId: currentOrgId,
        activityName: activityFormData.activityName,
        startTime: activityFormData.startTime,
        endTime: activityFormData.endTime,
        location: activityFormData.location,
        recruitmentCount: activityFormData.recruitmentCount,
        contactPersonPhone: activityFormData.contactPersonPhone,
        activityDurationHours: activityFormData.activityDurationHours === null || activityFormData.activityDurationHours === '' ? null : Number(activityFormData.activityDurationHours),
      };

      try {
        const response = await volunteerActivityStore.applyForActivity(applicationData);
        if (response && response.code === '200') {
          ElMessage.success(response.msg || '志愿活动申请已提交，请等待审核！');
          resetForm();
        } else {
          ElMessage.error(response?.msg || '申请提交失败，请稍后再试。');
        }
      } catch (error) {
        console.error('提交活动申请时发生错误:', error);
        ElMessage.error('提交申请过程中发生网络或系统错误。');
      }
    } else {
      ElMessage.error('请检查表单信息是否完整且正确！');
      return false;
    }
  });
};

const resetForm = () => {
  if (activityFormRef.value) {
    activityFormRef.value.resetFields();
  }
  activityFormData.activityName = "";
  activityFormData.startTime = "";
  activityFormData.endTime = "";
  activityFormData.location = "";
  activityFormData.recruitmentCount = 1;
  activityFormData.contactPersonPhone = "";
  activityFormData.activityDurationHours = null;
};

</script>

<style scoped>
.header-placeholder {
  height: 70px; /* 根据实际header高度调整，确保内容不被fixed header遮挡 */
}
.header {
  background-color: #ff3333; /* 鲜红色背景 */
  color: white;
  padding: 10px 20px;
  text-align: center; /* 标题居中 */
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 1000;
  box-sizing: border-box;
}
.el-input, .el-select, .el-date-picker, .el-input-number {
  width: 100%; /* 使表单控件宽度占满 */
}
</style>
