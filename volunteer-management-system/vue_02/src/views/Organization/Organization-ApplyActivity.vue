<template>
  <div class="apply-activity-container">
    <h2>申请志愿活动</h2>
    <el-form :model="form" :rules="rules" ref="activityForm" label-width="120px" v-loading="volunteerActivityStore.isSubmittingApplication">
      <el-form-item label="组织ID" prop="orgId">
        <el-input v-model="form.orgId" disabled placeholder="将自动获取"></el-input>
      </el-form-item>
      <el-form-item label="活动名称" prop="activityName">
        <el-input v-model="form.activityName" placeholder="请输入活动名称"></el-input>
      </el-form-item>
      <el-form-item label="活动地点" prop="location">
        <el-input v-model="form.location" placeholder="请输入活动地点"></el-input>
      </el-form-item>
      <el-form-item label="开始时间" prop="startTime">
        <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="选择活动开始时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%;"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="选择活动结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%;"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="招募人数" prop="recruitmentCount">
        <el-input-number v-model="form.recruitmentCount" :min="1" controls-position="right"></el-input-number>
      </el-form-item>
      <el-form-item label="联系人电话" prop="contactPersonPhone">
        <el-input v-model="form.contactPersonPhone" placeholder="请输入负责人联系电话"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitApplication">提交申请</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import { useOrganizationStore } from '@/stores/organizationStore.js'; //
import { useVolunteerActivityStore } from '@/stores/volunteerActivityStore.js'; //
import { ElMessage } from 'element-plus';

const organizationStore = useOrganizationStore();
const volunteerActivityStore = useVolunteerActivityStore();
const activityForm = ref(null);

// 表单数据模型
const form = reactive({
  orgId: '', // 将自动从登录组织信息中获取
  activityName: '',
  location: '',
  startTime: '',
  endTime: '',
  recruitmentCount: 1,
  contactPersonPhone: '', // 初始为空
});

// 表单验证规则 (保持不变)
const rules = reactive({
  activityName: [
    { required: true, message: '请输入活动名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' },
  ],
  location: [
    { required: true, message: '请输入活动地点', trigger: 'blur' },
  ],
  startTime: [
    { required: true, message: '请选择活动开始时间', trigger: 'change' },
  ],
  endTime: [
    { required: true, message: '请选择活动结束时间', trigger: 'change' },
    {
      validator: (rule, value, callback) => {
        if (value && form.startTime && new Date(value) <= new Date(form.startTime)) {
          callback(new Error('结束时间必须晚于开始时间'));
        } else {
          callback();
        }
      },
      trigger: 'change',
    },
  ],
  recruitmentCount: [
    { required: true, message: '请输入招募人数', trigger: 'change' },
    { type: 'number', message: '招募人数必须为数字' },
    { validator: (rule, value, callback) => {
        if (value <= 0) {
          callback(new Error('招募人数必须大于0'));
        } else {
          callback();
        }
      }, trigger: 'change' },
  ],
  contactPersonPhone: [
    { required: true, message: '请输入负责人联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码', trigger: 'blur' },
  ],
});


// 组件挂载时自动填充 orgId （不再填充 contactPersonPhone）
onMounted(() => {
  if (organizationStore.currentOrganizationId) {
    form.orgId = organizationStore.currentOrganizationId;
    // 移除自动填充联系人电话的逻辑，让用户手动填写
    // if (!form.contactPersonPhone && organizationStore.detailedOrganizationInfo.contactPersonPhone) {
    //     form.contactPersonPhone = organizationStore.detailedOrganizationInfo.contactPersonPhone;
    // }
  } else {
    ElMessage.warning('未能获取当前登录组织的ID，请检查登录状态。');
    // 如果需要，这里可以添加路由跳转，例如：router.push('/organizationlogin');
  }
});

// 提交申请方法 (保持不变)
const submitApplication = async () => {
  if (!activityForm.value) return;
  await activityForm.value.validate(async (valid) => {
    if (valid) {
      if (!form.orgId) {
        ElMessage.error('组织ID缺失，无法提交申请。');
        return;
      }
      const response = await volunteerActivityStore.applyForActivity(form);
      if (response && response.code === '200') {
        resetForm(); // 提交成功后清空表单
      }
    } else {
      ElMessage.error('表单验证失败，请检查输入');
      return false;
    }
  });
};

// 重置表单方法
const resetForm = () => {
  if (activityForm.value) {
    activityForm.value.resetFields(); // 清空所有字段到初始状态 (空字符串或默认值)

    // 重新填充 orgId (总是需要保留)
    form.orgId = organizationStore.currentOrganizationId;

    // 明确清空联系人电话，不依赖任何条件
    form.contactPersonPhone = '';
  }
};
</script>

<style scoped>
.apply-activity-container {
  max-width: 800px;
  margin: 50px auto;
  padding: 30px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
}
</style>