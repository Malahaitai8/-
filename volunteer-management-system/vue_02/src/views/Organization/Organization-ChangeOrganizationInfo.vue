<template>
  <el-card>
    <div class="header-placeholder"></div>
    <div class="header">
      <h1>志愿组织信息修改</h1>
    </div>
    <el-form ref="editFormRef" :model="editableOrganization" label-width="200px" style="margin-top: 20px;">
      <el-form-item label="组织ID">
        <el-input v-model="editableOrganization.orgId" disabled></el-input>
      </el-form-item>
      <el-form-item label="组织名称" prop="orgName">
        <el-input v-model="editableOrganization.orgName"></el-input>
      </el-form-item>
      <el-form-item label="组织登录用户名" prop="orgLoginUserName">
        <el-input v-model="editableOrganization.orgLoginUserName"></el-input>
      </el-form-item>
      <el-form-item label="负责人联系方式" prop="contactPersonPhone">
        <el-input v-model="editableOrganization.contactPersonPhone"></el-input>
      </el-form-item>
      <el-form-item label="服务区域" prop="serviceRegion">
        <el-select v-model="editableOrganization.serviceRegion" placeholder="请选择服务区域" style="width: 100%;">
          <el-option label="北京" value="北京"></el-option>
          <el-option label="天津" value="天津"></el-option>
          <el-option label="上海" value="上海"></el-option>
          <el-option label="重庆" value="重庆"></el-option>
          <el-option label="河北" value="河北"></el-option>
          <el-option label="山西" value="山西"></el-option>
          <el-option label="辽宁" value="辽宁"></el-option>
          <el-option label="吉林" value="吉林"></el-option>
          <el-option label="黑龙江" value="黑龙江"></el-option>
          <el-option label="江苏" value="江苏"></el-option>
          <el-option label="浙江" value="浙江"></el-option>
          <el-option label="安徽" value="安徽"></el-option>
          <el-option label="福建" value="福建"></el-option>
          <el-option label="江西" value="江西"></el-option>
          <el-option label="山东" value="山东"></el-option>
          <el-option label="河南" value="河南"></el-option>
          <el-option label="湖北" value="湖北"></el-option>
          <el-option label="湖南" value="湖南"></el-option>
          <el-option label="广东" value="广东"></el-option>
          <el-option label="广西" value="广西"></el-option>
          <el-option label="海南" value="海南"></el-option>
          <el-option label="四川" value="四川"></el-option>
          <el-option label="贵州" value="贵州"></el-option>
          <el-option label="云南" value="云南"></el-option>
          <el-option label="西藏" value="西藏"></el-option>
          <el-option label="陕西" value="陕西"></el-option>
          <el-option label="甘肃" value="甘肃"></el-option>
          <el-option label="青海" value="青海"></el-option>
          <el-option label="台湾" value="台湾"></el-option>
          <el-option label="香港" value="香港"></el-option>
          <el-option label="澳门" value="澳门"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="组织规模 (人数)" prop="orgScale">
        <el-input-number v-model="editableOrganization.orgScale" :min="0" style="width: 100%;"></el-input-number>
      </el-form-item>
      <el-form-item label="组织评分" prop="orgRating">
        <el-input v-model="editableOrganization.orgRating" disabled title="组织评分由系统或用户评定"></el-input>
      </el-form-item>
      <el-form-item label="组织账户状态" prop="orgAccountStatus">
        <el-input v-model="editableOrganization.orgAccountStatus" disabled title="账户状态由管理员控制"></el-input>
      </el-form-item>
      <el-form-item label="服务总时长 (小时)" prop="totalServiceHours">
         <el-input-number v-model="editableOrganization.totalServiceHours" :min="0" disabled title="服务总时长由系统累积" style="width: 100%;"></el-input-number>
      </el-form-item>
      <el-form-item label="活动举办次数" prop="activityCount">
        <el-input-number v-model="editableOrganization.activityCount" :min="0" disabled title="活动次数由系统累积" style="width: 100%;"></el-input-number>
      </el-form-item>
      <el-form-item label="培训举办次数" prop="trainingCount">
        <el-input-number v-model="editableOrganization.trainingCount" :min="0" disabled title="培训次数由系统累积" style="width: 100%;"></el-input-number>
      </el-form-item>
    </el-form>
    <div style="text-align: center; margin-top: 20px;">
      <el-button type="info" @click="goBackToInfoPage">返回信息页</el-button>
      <el-button type="primary" @click="handleSaveInfo">确认修改</el-button>
      <el-button @click="resetForm">重置表单</el-button>
      <el-button type="warning" @click="openChangePasswordDialog">修改密码</el-button>
      <el-button type="danger" @click="goHome">返回主页</el-button>
    </div>

    <el-dialog title="修改密码" v-model="changePasswordDialogVisible" width="450px" :close-on-click-modal="false">
      <el-form :model="passwordForm" ref="passwordFormRef" label-width="100px" :rules="passwordRules">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password autocomplete="off" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password autocomplete="off" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password autocomplete="off" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="changePasswordDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="handleChangePassword">确 定修 改</el-button>
        </div>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted, watchEffect, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useOrganizationStore } from "@/stores/organizationStore.js";
import { ElMessage, ElMessageBox } from 'element-plus';

const organizationStore = useOrganizationStore();
const router = useRouter();

const editableOrganization = ref({
  orgId: '',
  orgName: '',
  orgLoginUserName: '',
  contactPersonPhone: '',
  serviceRegion: '',
  orgScale: 0,
  orgRating: 0.0,
  orgAccountStatus: '',
  totalServiceHours: 0,
  activityCount: 0,
  trainingCount: 0
});

const editFormRef = ref(null);
const passwordFormRef = ref(null);
const changePasswordDialogVisible = ref(false);

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
});

// Password validation rules
const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入新密码'));
  } else if (value.length < 6) {
    callback(new Error('密码长度不能少于6位'));
  } else {
    if (passwordForm.confirmPassword !== '') {
      passwordFormRef.value.validateField('confirmPassword');
    }
    callback();
  }
};
const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'));
  } else if (value !== passwordForm.newPassword) {
    callback(new Error("两次输入的新密码不一致!"));
  } else {
    callback();
  }
};

const passwordRules = reactive({
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, validator: validatePass, trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validatePass2, trigger: 'blur' }],
});

onMounted(() => {
  initializeData();
});

watchEffect(() => {
  if (organizationStore.detailedOrganizationInfo && organizationStore.detailedOrganizationInfo.orgId) {
    populateFormWithStoreData();
  }
});

function initializeData() {
  if (!organizationStore.currentOrganizationId && organizationStore.isAuthenticated) {
    organizationStore.fetchDetailedOrganizationInfo().then(populateFormWithStoreData);
  } else if (organizationStore.currentOrganizationId) {
    populateFormWithStoreData();
  } else if (!organizationStore.isAuthenticated && localStorage.getItem('xm-pro-organization')) {
    organizationStore.initializeStore().then(() => {
      if (organizationStore.detailedOrganizationInfo && organizationStore.detailedOrganizationInfo.orgId) {
        populateFormWithStoreData();
      }
    });
  }
}

function populateFormWithStoreData() {
  const storeData = organizationStore.detailedOrganizationInfo;
  editableOrganization.value = {
    orgId: storeData.orgId || '',
    orgName: storeData.orgName || '',
    orgLoginUserName: storeData.orgLoginUserName || '',
    contactPersonPhone: storeData.contactPersonPhone || '',
    serviceRegion: storeData.serviceRegion || '',
    orgScale: Number(storeData.orgScale) || 0,
    orgRating: Number(storeData.orgRating) || 0.0,
    orgAccountStatus: storeData.orgAccountStatus || '',
    totalServiceHours: Number(storeData.totalServiceHours) || 0,
    activityCount: Number(storeData.activityCount) || 0,
    trainingCount: Number(storeData.trainingCount) || 0
  };
}

const goHome = () => router.push('/organization-home');
const goBackToInfoPage = () => router.push('/organization-info');

const handleSaveInfo = async () => {
  const dataToUpdate = {
    orgId: editableOrganization.value.orgId,
    orgName: editableOrganization.value.orgName,
    orgLoginUserName: editableOrganization.value.orgLoginUserName,
    contactPersonPhone: editableOrganization.value.contactPersonPhone,
    serviceRegion: editableOrganization.value.serviceRegion,
    orgScale: Number(editableOrganization.value.orgScale)
  };

  if (!dataToUpdate.orgName || !dataToUpdate.orgLoginUserName || !dataToUpdate.contactPersonPhone || !dataToUpdate.serviceRegion) {
    ElMessage.error('组织名称、登录用户名、联系方式和服务区域不能为空！');
    return;
  }
  if (dataToUpdate.orgScale === null || dataToUpdate.orgScale < 0) {
    ElMessage.error('组织规模必须是有效的数字！');
    return;
  }

  try {
    ElMessage.info('正在保存信息...');
    const response = await organizationStore.updateOrganizationInfo(dataToUpdate);
    if (response && response.code === '200') {
      ElMessage.success('组织信息更新成功！');
      router.push('/organization-info');
    } else {
      ElMessage.error(response?.msg || '信息更新失败，请稍后再试。');
    }
  } catch (error) {
    console.error('保存组织信息时发生错误:', error);
    ElMessage.error('信息更新过程中发生网络或系统错误。');
  }
};

const resetForm = () => {
  ElMessageBox.confirm('确定要重置表单内容吗？未保存的更改将会丢失。', '警告', {
    confirmButtonText: '确定重置',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    populateFormWithStoreData();
    ElMessage.info('表单已重置为当前已保存的状态。');
  }).catch(() => {});
};

const openChangePasswordDialog = () => {
  passwordForm.oldPassword = '';
  passwordForm.newPassword = '';
  passwordForm.confirmPassword = '';
  if (passwordFormRef.value) {
    passwordFormRef.value.resetFields();
  }
  changePasswordDialogVisible.value = true;
};

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return;
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      if (!organizationStore.currentOrganizationId) {
        ElMessage.error('无法获取组织ID，无法修改密码。');
        return;
      }
      try {
        ElMessage.info('正在修改密码...');
        const response = await organizationStore.changeOrganizationPassword({
          orgId: organizationStore.currentOrganizationId,
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword,
        });
        if (response && response.code === '200') {
          ElMessage.success('密码修改成功！');
          router.push('/organizationlogin');
          changePasswordDialogVisible.value = false;
        } else {
          ElMessage.error(response?.msg || '密码修改失败，请稍后再试。');
        }
      } catch (error) {
        console.error('修改密码时发生错误:', error);
        ElMessage.error('修改密码过程中发生网络或系统错误。');
      }
    } else {
      console.log('密码表单校验失败!');
      return false;
    }
  });
};
</script>

<style scoped>
.header-placeholder {
  height: 60px;
}
.header {
  background-color: #ff3333;
  color: white;
  padding: 10px 20px;
  text-align: center;
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 1000;
  box-sizing: border-box;
}
.el-input, .el-input-number, .el-select {
  width: 100%;
}
</style>