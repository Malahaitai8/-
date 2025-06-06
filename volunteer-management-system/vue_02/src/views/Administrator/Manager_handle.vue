<template>
  <!-- 页面根容器 -->
  <div>
    <!-- 页眉部分，包含 logo、标题和欢迎信息 -->
    <header class="header">
      <!-- 显示 logo 图片，图片路径为根目录下的 logo.png -->
      <img src="/logo.png" alt="Logo" class="logo">
      <!-- 显示网页标题，即志愿管理系统 -->
      <h1 class="title">志愿管理系统</h1>
      <!-- 显示欢迎信息，欢迎管理员 -->
      <span class="welcome">欢迎您：管理员！</span>
    </header>
    <!-- 主体部分，用于展示两个信息框 -->
    <main class="main-content">
      <!-- 第一个信息框：志愿者信息 -->
      <div class="info-box">
        <h2>投诉对象信息</h2>
        <div class="form-item">
          <label>相关者 ID</label>
          <input type="text" value="V001" readonly>
        </div>
        <div class="form-item">
          <label>处理结果</label>
          <select>
            <option value="freeze45">请选择</option>
            <option value="freeze45">冻结账号45天</option>
            <option value="freeze30">冻结账号30天</option>
            <option value="freeze20">冻结账号20天</option>
            <option value="freeze15">冻结账号15天</option>
            <option value="freeze10">冻结账号10天</option>
          </select>
<!--        </div><div class="form-item">
          <label>相关志愿者2 ID</label>
          <input type="text" value="V002" readonly>
        </div>
        <div class="form-item">
          <label>志愿者2处理结果</label>
          <select>
            <option value="freeze45">请选择</option>
            <option value="freeze45">冻结账号45天</option>
            <option value="freeze30">冻结账号30天</option>
            <option value="freeze20" >冻结账号20天</option>
            <option value="freeze15">冻结账号15天</option>
            <option value="freeze10">冻结账号10天</option>
          </select>-->
        </div>
      </div>-->
<!--      &lt;!&ndash; 第二个信息框：志愿组织机构信息 &ndash;&gt;
      <div class="info-box">
        <h2>志愿组织机构信息</h2>
        <div class="form-item">
          <label>相关志愿组织机构1 ID</label>
          <input type="text" value="O001" readonly>
        </div>
        <div class="form-item">
          <label>志愿组织机构1处理结果</label>
          <select>
            <option value="freeze45">请选择</option>
            <option value="freeze45" >冻结账号45天</option>
            <option value="freeze30">冻结账号30天</option>
            <option value="freeze20">冻结账号20天</option>
            <option value="freeze15">冻结账号15天</option>
            <option value="freeze10">冻结账号10天</option>
          </select>
        </div>
        <div class="form-item">
          <label>相关志愿组织机构2 ID</label>
          <input type="text" value="O002" readonly>
        </div>
        <div class="form-item">
          <label>志愿组织机构2处理结果</label>
          <select>
            <option value="freeze45">请选择</option>
            <option value="freeze45">冻结账号45天</option>
            <option value="freeze30">冻结账号30天</option>
            <option value="freeze20">冻结账号20天</option>
            <option value="freeze15" >冻结账号15天</option>
            <option value="freeze10">冻结账号10天</option>
          </select>
        </div>
      </div>
       底部按钮 -->
      <div class="button-container">
        <button @click="goBack">返回</button>
        <button @click="submitData">确认提交</button>
      </div>
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import {useAdminStore} from "@/stores/adminStore.js"; // 假设使用 Element Plus 提示框，需要安装

//管理员信息传递
const adminStore = useAdminStore(); // 使用 Pinia store
adminStore.initializeStore();
const displayNameFromStore = computed(() => adminStore.displayName);
const isAuthenticated = computed(() => adminStore.isAuthenticated);
onMounted(() => {
  if (isAuthenticated.value && !adminStore.detailedAdminInfo.adminId) { // 简单判断详细信息是否已加载
  }
});




const router = useRouter();

const goBack = () => {
  router.push({ name: 'managerOperation' });
};

const submitData = () => {
  ElMessage.success('提交成功');
};
</script>

<style scoped>
/* 页眉样式，设置背景颜色为红色，使用弹性布局使其内容垂直居中，添加内边距 */
.header {
  background-color: red;
  display: flex;
  align-items: center;
  padding: 10px 20px;
}

/* logo 图片样式，设置图片高度 */
.logo {
  height: 50px;
}

/* 标题样式，设置文字颜色为白色，加粗显示，并添加左右外边距 */
.title {
  color: white;
  font-weight: bold;
  margin: 0 30px;
}

/* 欢迎信息样式，设置文字颜色为白色，字体大小为 14px */
.welcome {
  color: white;
  font-size: 14px;
}

/* 主体内容样式，设置内边距并添加背景图 */
.main-content {
  padding: 50px;
  background-image: url('/bg.png');
  background-size: cover;
  background-position: center;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 信息框样式 */
.info-box {
  background-color: rgba(255, 255, 255, 0.8);
  padding: 20px;
  border-radius: 8px;
  width: 600px;
  margin-bottom: 350px;
}

/* 表单项样式 */
.form-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.form-item label {
  width: 200px;
  text-align: right;
  margin-right: 10px;
}

.form-item input,
.form-item select {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  flex-grow: 1;
}

/* 底部按钮容器样式 */
.button-container {
  display: flex;
  gap: 20px;
  justify-content: center;
  width: 100%;
  margin-top: auto;
}

.button-container button {
  padding: 8px 16px;
  background-color: red;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>