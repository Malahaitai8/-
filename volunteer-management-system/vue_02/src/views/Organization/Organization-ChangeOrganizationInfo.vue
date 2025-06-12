<template>
  <div class="organization-edit-container">
    <div class="header">
      <h1>志愿组织信息修改</h1>
    </div>

    <div class="content-wrapper">
      <el-card class="main-card">
        <div v-if="!editableOrganization.orgId && !error" class="loading-section">
          <div class="loading-spinner"></div>
          <p>加载组织信息中...</p>
        </div>

        <div v-else-if="error" class="error-section">
          <div class="error-icon">⚠️</div>
          <p>{{ error }}</p>
          <el-button @click="initializeData" type="primary">重试</el-button>
        </div>

        <div v-else class="organization-edit">
          <el-form ref="editFormRef" :model="editableOrganization">
            <div class="info-section">
              <div class="section-header">
                <h2>修改基本信息</h2>
              </div>
              <div class="info-grid">
                <el-form-item prop="orgName" class="grid-form-item">
                  <label class="info-label">组织名称</label>
                  <el-input v-model="editableOrganization.orgName" class="edit-input" placeholder="请输入组织名称" />
                </el-form-item>

                <el-form-item prop="orgLoginUserName" class="grid-form-item">
                  <label class="info-label">组织登录用户名</label>
                  <el-input v-model="editableOrganization.orgLoginUserName" class="edit-input" placeholder="请输入登录用户名" />
                </el-form-item>

                <el-form-item prop="contactPersonPhone" class="grid-form-item">
                  <label class="info-label">负责人联系方式</label>
                  <el-input v-model="editableOrganization.contactPersonPhone" class="edit-input" placeholder="请输入联系方式" />
                </el-form-item>

                <el-form-item prop="serviceRegion" class="grid-form-item">
                  <label class="info-label">服务区域</label>
                  <el-select v-model="editableOrganization.serviceRegion" placeholder="请选择服务区域" class="edit-input">
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

                <el-form-item prop="orgScale" class="grid-form-item">
                  <label class="info-label">组织规模 (人数)</label>
                  <el-input-number v-model="editableOrganization.orgScale" :min="0" class="edit-input" />
                </el-form-item>

                <div class="info-item-disabled">
                  <label class="info-label">组织ID</label>
                  <div class="info-value-disabled">{{ editableOrganization.orgId }}</div>
                </div>

                <div class="info-item-disabled">
                  <label class="info-label">组织评分</label>
                  <div class="info-value-disabled" title="组织评分由系统或用户评定">{{ editableOrganization.orgRating }}</div>
                </div>
                <div class="info-item-disabled">
                  <label class="info-label">组织账户状态</label>
                  <div class="info-value-disabled" title="账户状态由管理员控制">{{ editableOrganization.orgAccountStatus }}</div>
                </div>
                <div class="info-item-disabled">
                  <label class="info-label">服务总时长 (小时)</label>
                  <div class="info-value-disabled" title="服务总时长由系统累积">{{ editableOrganization.totalServiceHours }}</div>
                </div>
                <div class="info-item-disabled">
                  <label class="info-label">活动举办次数</label>
                  <div class="info-value-disabled" title="活动次数由系统累积">{{ editableOrganization.activityCount }}</div>
                </div>
                <div class="info-item-disabled">
                  <label class="info-label">培训举办次数</label>
                  <div class="info-value-disabled" title="培训次数由系统累积">{{ editableOrganization.trainingCount }}</div>
                </div>
              </div>
            </div>
          </el-form>

          <div class="button-group">
            <el-button @click="handleSaveInfo" class="action-btn save-btn">确认修改</el-button>
            <el-button @click="resetForm" class="action-btn reset-btn">重置表单</el-button>
            <el-button @click="openChangePasswordDialog" class="action-btn password-btn">修改密码</el-button>
            <el-button @click="goBackToInfoPage" class="action-btn back-btn">返回信息页</el-button>
            <el-button @click="goHome" class="action-btn home-btn">返回主页</el-button>
          </div>
        </div>
      </el-card>
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
  </div>
</template>

<script setup>
import { ref, onMounted, watchEffect, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useOrganizationStore } from "@/stores/organizationStore.js";
import { ElMessage, ElMessageBox } from 'element-plus';

const organizationStore = useOrganizationStore();
const router = useRouter();

const editableOrganization = ref({ orgId: '' }); // Initialize with a key to prevent initial error render
const error = ref(null);

const editFormRef = ref(null);
const passwordFormRef = ref(null);
const changePasswordDialogVisible = ref(false);

const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' });

// Password validation rules (no changes needed)
const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入新密码'));
  } else if (value.length < 6) {
    callback(new Error('密码长度不能少于6位'));
  } else {
    if (passwordForm.confirmPassword !== '') {
      passwordFormRef.value?.validateField('confirmPassword');
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

onMounted(initializeData);

watchEffect(() => {
  if (organizationStore.detailedOrganizationInfo?.orgId) {
    populateFormWithStoreData();
  }
});

async function initializeData() {
  error.value = null;
  try {
    if (!organizationStore.detailedOrganizationInfo?.orgId) {
      // Prioritize fetching if data is not in the store
      await organizationStore.fetchDetailedOrganizationInfo();
    }
    populateFormWithStoreData();
    if (!editableOrganization.value.orgId) {
      throw new Error("Failed to load organization data.");
    }
  } catch (err) {
    error.value = "加载组织信息失败，请检查网络或稍后重试。";
    console.error(err);
  }
}

function populateFormWithStoreData() {
  const storeData = organizationStore.detailedOrganizationInfo;
  if (!storeData || !storeData.orgId) return;

  editableOrganization.value = {
    orgId: storeData.orgId,
    orgName: storeData.orgName || '',
    orgLoginUserName: storeData.orgLoginUserName || '',
    contactPersonPhone: storeData.contactPersonPhone || '',
    serviceRegion: storeData.serviceRegion || '',
    orgScale: Number(storeData.orgScale) || 0,
    orgRating: Number(storeData.orgRating) || 0.0,
    orgAccountStatus: storeData.orgAccountStatus || '未知',
    totalServiceHours: Number(storeData.totalServiceHours) || 0,
    activityCount: Number(storeData.activityCount) || 0,
    trainingCount: Number(storeData.trainingCount) || 0
  };
}

const goHome = () => router.push('/organization-home');
const goBackToInfoPage = () => router.push('/organization-info');

const handleSaveInfo = async () => {
  // Validation and save logic remains the same
  const dataToUpdate = {
    orgId: editableOrganization.value.orgId,
    orgName: editableOrganization.value.orgName,
    orgLoginUserName: editableOrganization.value.orgLoginUserName,
    contactPersonPhone: editableOrganization.value.contactPersonPhone,
    serviceRegion: editableOrganization.value.serviceRegion,
    orgScale: Number(editableOrganization.value.orgScale)
  };
  // ... (rest of the save logic is unchanged)
  if (!dataToUpdate.orgName || !dataToUpdate.orgLoginUserName || !dataToUpdate.contactPersonPhone || !dataToUpdate.serviceRegion) {
    ElMessage.error('组织名称、登录用户名、联系方式和服务区域不能为空！');
    return;
  }
  try {
    const response = await organizationStore.updateOrganizationInfo(dataToUpdate);
    if (response && response.code === '200') {
      ElMessage.success('组织信息更新成功！');
      router.push('/organization-info');
    } else {
      ElMessage.error(response?.msg || '信息更新失败。');
    }
  } catch (error) {
    ElMessage.error('信息更新过程中发生错误。');
  }
};

const resetForm = () => {
  // Reset logic remains the same
  ElMessageBox.confirm('确定要重置表单吗？未保存的更改将会丢失。', '警告', {
    confirmButtonText: '确定重置', cancelButtonText: '取消', type: 'warning'
  }).then(() => {
    populateFormWithStoreData();
    ElMessage.info('表单已重置。');
  }).catch(() => {});
};

const openChangePasswordDialog = () => {
  // Dialog open logic remains the same
  passwordForm.oldPassword = '';
  passwordForm.newPassword = '';
  passwordForm.confirmPassword = '';
  passwordFormRef.value?.resetFields();
  changePasswordDialogVisible.value = true;
};

const handleChangePassword = async () => {
  // Password change logic remains the same
  if (!passwordFormRef.value) return;
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await organizationStore.changeOrganizationPassword({
          orgId: editableOrganization.value.orgId,
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword,
        });
        if (response && response.code === '200') {
          ElMessage.success('密码修改成功！请重新登录。');
          changePasswordDialogVisible.value = false;
          router.push('/organizationlogin');
        } else {
          ElMessage.error(response?.msg || '密码修改失败。');
        }
      } catch (error) {
        ElMessage.error('修改密码过程中发生错误。');
      }
    }
  });
};
</script>

<style scoped>
/* Base container styles, adapted from the reference */
.organization-edit-container {
  min-height: 100vh;
  background-image: url('@/../public/bg.png');
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center;
  padding: 0;
  position: relative;
}

.organization-edit-container::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(255, 51, 51, 0.1);
  z-index: 1;
}

.header {
  background-color: #ff3333;
  color: white;
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 10px rgba(255, 51, 51, 0.3);
  position: relative;
  z-index: 2;
}

.header h1 {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
}

.content-wrapper {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 30px 20px;
  min-height: calc(100vh - 80px);
  position: relative;
  z-index: 2;
}

.main-card {
  width: 100%;
  max-width: 1800px;
  border-radius: 12px;
  box-shadow: 0 8px 25px rgba(255, 51, 51, 0.2);
  border: 1px solid rgba(255, 51, 51, 0.1);
  overflow: hidden;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

.main-card :deep(.el-card__body) {
  padding: 40px;
}

/* Loading and Error States */
.loading-section, .error-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
  min-height: 500px;
}

.loading-spinner {
  width: 40px; height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #ff3333;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-icon {
  font-size: 3rem; margin-bottom: 1rem;
}

/* Information Section Styling */
.info-section {
  background: white;
  border-radius: 12px;
  margin-bottom: 30px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.section-header {
  background: linear-gradient(135deg, #ff3333, #ff6666);
  color: white;
  padding: 20px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 25px;
  padding: 30px;
}

/* Custom Form Item Styling to match the reference */
.grid-form-item, .info-item-disabled {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.grid-form-item :deep(.el-form-item__content) {
  margin-left: 0 !important;
}

.edit-input {
  width: 100%;
}

.edit-input :deep(.el-input__wrapper),
.edit-input :deep(.el-input-number) {
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  transition: all 0.3s ease;
  width: 100%;
}

.edit-input :deep(.el-input__wrapper:hover),
.edit-input :deep(.el-input-number:hover) {
  border-color: #ff6666;
}

.edit-input :deep(.el-input.is-focus .el-input__wrapper),
.edit-input :deep(.el-select.is-focus .el-input__wrapper) {
  border-color: #ff3333;
  box-shadow: 0 0 0 2px rgba(255, 51, 51, 0.2);
}

.info-value-disabled {
  padding: 12px 15px;
  background: #f1f2f6;
  border-radius: 8px;
  border-left: 4px solid #b0bec5;
  font-size: 14px;
  color: #666;
  min-height: 44px;
  display: flex;
  align-items: center;
  word-break: break-all;
  cursor: not-allowed;
}

/* Button Group Styling */
.button-group {
  display: flex;
  justify-content: center;
  gap: 15px;
  flex-wrap: wrap;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.action-btn {
  padding: 12px 30px;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 500;
  min-width: 150px;
  height: 45px;
  border: none;
  transition: all 0.3s ease;
}
.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0,0,0,0.15);
}

.save-btn {
  background-color: #ff3333; color: white;
}
.save-btn:hover {
  background-color: #e62e2e;
}

.reset-btn {
  background-color: #f0f2f5; color: #606266;
}
.reset-btn:hover {
  background-color: #e4e7ed;
}

.password-btn {
  background-color: #e6a23c; color: white;
}
.password-btn:hover {
  background-color: #cf9236;
}

.back-btn {
  background-color: #409eff; color: white;
}
.back-btn:hover {
  background-color: #3a8ee6;
}

.home-btn {
  background-color: #909399; color: white;
}
.home-btn:hover {
  background-color: #82848a;
}

/* Responsive Design */
@media (max-width: 768px) {
  .content-wrapper { padding: 20px 15px; }
  .main-card :deep(.el-card__body) { padding: 20px; }
  .info-grid {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 20px;
  }
}
</style>
