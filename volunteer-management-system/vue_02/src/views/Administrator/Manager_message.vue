<template>
  <!-- 页面根容器 -->
  <div>
    <!-- 页眉部分，包含 logo、标题和欢迎信息 -->
    <header class="header">
      <!-- 显示 logo 图片，图片路径为根目录下的 logo.png -->
      <img src="/logo.png" alt="Logo" class="logo">
      <!-- 显示网页标题为志愿者管理 -->
      <h1 class="title">服务留言</h1>
      <!-- 显示欢迎信息，欢迎管理员 -->
      <span class="welcome">欢迎您：管理员！</span>
    </header>
    <!-- 主体部分，用于展示表格 -->
    <main class="main-content">
      <!-- 表格标题和筛选按钮 -->
      <div class="table-header">
        <h2>服务留言信息</h2>
        <div class="filter-buttons">
          <button @click="filterHandled">好评</button>
          <button @click="filterUnhandled">差评</button>
        </div>
      </div>
      <!-- 可滚动的表格容器 -->
      <div class="table-container">
        <table>
          <thead>
          <tr>
            <th>序号</th>
            <th>留言</th>
            <th>操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(item, index) in volunteerApplications" :key="index">
            <td>{{ index + 1 }}</td>
            <td>
              <p>匿名用户{{ item.volunteerId }}</p>
              <p>留言内容：{{ item.contact }}</p>
              <p>留言时间：{{ item.applicationTime }}</p>
            </td>

            <td>
              <button @click="viewDetails(item)">查看详细信息</button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import {useAdminStore} from "@/stores/adminStore.js";
//管理员信息传递
const adminStore = useAdminStore(); // 使用 Pinia store
adminStore.initializeStore();
const displayNameFromStore = computed(() => adminStore.displayName);
const isAuthenticated = computed(() => adminStore.isAuthenticated);
onMounted(() => {
  if (isAuthenticated.value && !adminStore.detailedAdminInfo.adminId) { // 简单判断详细信息是否已加载
  }
});


// 模拟更多志愿者申请数据
const volunteerApplications = ref([
  {
    volunteerId: '123',
    contact: '管理员处理事件及时到位，服务质量很高',
    applicationTime: '2024-01-01 12:00:00',

  },
  {
    volunteerId: '456',
    contact: '管理员太马虎了，太差劲',
    applicationTime: '2024-01-02 13:00:00',

  },
  {
    volunteerId: '789',
    contact: '管理员太棒了为你打call给你加鸡腿',
    applicationTime: '2024-01-03 14:00:00',

  },
  {
    volunteerId: '666',
    contact: '管理员处理事件及时到位，服务质量很高',
    applicationTime: '2024-01-02 13:00:00',

  },
  {
    volunteerId: '777',
    contact: '投诉处理结果太让我满意了。这波操作必须给五星好评！',
    applicationTime: '2024-01-02 13:00:00',

  },
  {
    volunteerId: '888',
    contact: '四楼说得对',
    applicationTime: '2024-01-02 13:00:00',

  }
]);

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
  padding: 20px;
  background-image: url('/bg.png');
  background-size: cover;
  background-position: center;
}

/* 表格标题和筛选按钮样式 */
.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

/* 筛选按钮容器样式 */
.filter-buttons {
  display: flex;
  gap: 10px;
}

/* 可滚动的表格容器样式 */
.table-container {
  max-height: 600px;
  overflow-y: auto;
}

/* 表格样式，设置背景为白色 */
table {
  width: 100%;
  border-collapse: collapse;
  background-color: white;
}

th, td {
  border: 1px solid #ccc;
  padding: 8px;
  text-align: left;
}

th {
  background-color: #f2f2f2;
}

/* 操作按钮样式 */
button {
  margin: 2px;
  padding: 4px 8px;
  background-color: red;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>