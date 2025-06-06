<template>
  <!-- 页面根容器 -->
  <div>
    <!-- 页眉部分，包含 logo、标题和欢迎信息 -->
    <header class="header">
      <img src="/logo.png" alt="Logo" class="logo">
      <h1 class="title">志愿管理系统</h1>
      <span class="welcome">欢迎您：管理员！</span>
    </header>
    <!-- 主体部分，用于展示信息卡片 -->
    <main class="main-content">
      <!-- 使用 ElForm 进行表单校验 -->
      <el-form ref="passwordFormRef" :model="passwordFormData" :rules="passwordRules" class="info-card" label-position="top">
        <div class="form-title">修改密码</div>
        <el-form-item label="请输入原密码" prop="oldPassword" class="input-group">
          <el-input type="password" v-model="passwordFormData.oldPassword" show-password placeholder="原密码"></el-input>
        </el-form-item>
        <el-form-item label="请输入新密码" prop="newPassword" class="input-group">
          <el-input type="password" v-model="passwordFormData.newPassword" show-password placeholder="新密码"></el-input>
        </el-form-item>
        <el-form-item label="请再次输入密码" prop="confirmPassword" class="input-group">
          <el-input type="password" v-model="passwordFormData.confirmPassword" show-password placeholder="确认新密码"></el-input>
        </el-form-item>
        <div class="button-group">
          <button type="button" @click="handleConfirm" class="action-button confirm">确认修改</button>
          <button type="button" @click="handleBack" class="action-button back">返回</button>
        </div>
      </el-form>
    </main>
  </div>
</template>

<script setup>
import {ref, reactive, computed, onMounted} from 'vue';
import {useRouter} from 'vue-router';
import {useAdminStore} from '@/stores/adminStore.js'; // 引入 adminStore
import {ElMessage, ElForm, ElFormItem, ElInput, ElButton} from 'element-plus'; // 引入 Element Plus 组件

const adminStore = useAdminStore();
const router = useRouter();

const passwordFormRef = ref(null); // 用于表单校验的引用
const passwordFormData = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 获取当前登录管理员的ID
const currentAdminId = computed(() => adminStore.detailedAdminInfo?.adminId || adminStore.loggedInAdmin?.adminId || '');

// 新密码与确认密码一致的校验规则
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'));
  } else if (value !== passwordFormData.newPassword) {
    callback(new Error('两次输入的新密码不一致!'));
  } else {
    callback();
  }
};

const passwordRules = reactive({
  oldPassword: [
    {required: true, message: '请输入原密码', trigger: 'blur'}
  ],
  newPassword: [
    {required: true, message: '请输入新密码', trigger: 'blur'},
    {min: 6, message: '新密码长度不能少于6位', trigger: 'blur'} // 与后端Service层校验一致
  ],
  confirmPassword: [
    {required: true, message: '请确认新密码', trigger: 'blur'},
    {validator: validateConfirmPassword, trigger: 'blur'}
  ]
});

onMounted(async () => {
  // 确保进入此页面时，store中已有管理员信息，特别是adminId
  if (!adminStore.isAuthenticated) {
    ElMessage.error('用户未登录，请先登录。');
    router.push('/login'); // 跳转到登录页，根据实际路由调整
    return;
  }
  if (!currentAdminId.value) {
    // 如果 adminId 不存在，尝试从 store 获取一次，或者提示错误
    // 这通常意味着 store 初始化或数据同步可能存在问题
    ElMessage.warn('无法获取管理员ID，请稍后重试或重新登录。');
    // 可以选择是否跳转回上一页或登录页
    // router.back();
  }
});


const handleConfirm = async () => {
  if (!passwordFormRef.value) return;

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      const adminId = currentAdminId.value;
      if (!adminId) {
        ElMessage.error('无法获取管理员ID，操作失败。');
        return;
      }

      try {
        const result = await adminStore.changeAdminPassword({
          adminId: adminId,
          oldPassword: passwordFormData.oldPassword,
          newPassword: passwordFormData.newPassword
        });

        if (result && result.code === "200") {
          ElMessage.success('密码修改成功！建议重新登录以使新密码生效。');
          passwordFormRef.value.resetFields(); // 清空表单
          // 考虑到安全性，修改密码后通常会引导用户重新登录
          //adminStore.logout(); // 调用store的登出方法
          router.push( '/administratorlogin'); // 跳转到管理员登录页，确保路由名称正确
        } else {
          ElMessage.error(result.msg || '密码修改失败，请检查输入或稍后再试。');
        }
      } catch (error) {
        console.error("修改密码过程中发生错误:", error);
        ElMessage.error(error.response?.data?.msg || error.message || '修改密码请求失败。');
      }
    } else {
      ElMessage.error('表单校验失败，请检查输入项。');
      return false;
    }
  });
};

const handleBack = () => {
  // router.push({ name: 'personalData' }); // 跳转回个人资料页，确保路由名称 'personalData' 正确
  router.back(); // 或者直接返回上一页
};
</script>

<style scoped>
/* 页眉样式 */
.header {
  background-color: #d32f2f; /* 深红色，与个人资料页一致 */
  color: white;
  display: flex;
  align-items: center;
  padding: 12px 25px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.15);
}

.logo {
  height: 45px;
  margin-right: 12px;
}

.title {
  font-size: 1.6em;
  font-weight: 600;
  margin: 0;
  flex-grow: 1;
}

.welcome {
  font-size: 0.9em;
  margin-left: auto;
}

/* 主体内容样式 */
.main-content {
  display: flex;
  justify-content: center;
  align-items: center; /* 垂直居中 */
  min-height: calc(100vh - 69px); /* 减去页眉高度，使其内容区域充满剩余空间 */
  padding: 20px;
  background-image: url('/bg.png'); /* 确保背景图路径正确 */
  background-size: cover;
  background-position: center;
}

/* 信息卡片样式 */
.info-card {
  background-color: rgba(255, 255, 255, 0.9); /* 轻微透明背景 */
  padding: 30px 40px; /* 增加内边距 */
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 450px; /* 卡片最大宽度 */
}

.form-title {
  text-align: center;
  font-size: 1.5em;
  font-weight: bold;
  color: #333;
  margin-bottom: 25px;
}

/* ElFormItem 默认会处理 label，所以自定义的 input-group 和 label 可以简化或移除 */
.input-group { /* el-form-item 已经有类似结构 */
  margin-bottom: 18px; /* Element Plus ElFormItem 默认有间距 */
}

/* 输入框样式，如果使用ElInput，很多样式会被组件覆盖 */
/* 保留一些通用调整 */
:deep(.el-input__inner) {
  padding: 10px 12px;
  border-radius: 6px;
}

/* 按钮组样式 */
.button-group {
  display: flex;
  justify-content: space-around; /* 让按钮平均分布空间 */
  margin-top: 25px;
}

/* 按钮样式 */
.action-button {
  padding: 10px 25px; /* 调整按钮大小 */
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1em;
  font-weight: 500;
  transition: background-color 0.2s ease, transform 0.1s ease;
  min-width: 120px; /* 按钮最小宽度 */
}

.action-button.confirm {
  background-color: #d32f2f; /* 红色 */
  color: white;
}

.action-button.confirm:hover {
  background-color: #b71c1c; /* 深红色 */
}

.action-button.back {
  background-color: #6c757d; /* 灰色 */
  color: white;
}

.action-button.back:hover {
  background-color: #5a6268; /* 深灰色 */
}
</style>
