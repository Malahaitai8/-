<template>
  <!-- 页面根容器 -->
  <div>
    <!-- 页眉部分，包含 logo、标题和欢迎信息 -->
    <header class="header">
      <!-- 显示 logo 图片，图片路径为根目录下的 logo.png -->
      <img src="/logo.png" alt="Logo" class="logo">
      <!-- 显示网页标题为志愿者管理 -->
      <h1 class="title">投诉处理</h1>
      <!-- 显示欢迎信息，欢迎管理员 -->
      <span class="welcome">欢迎您：管理员！</span>
    </header>
    <!-- 主体部分，用于展示表格 -->
    <main class="main-content">
      <!-- 表格标题和筛选按钮 -->
      <div class="table-header">
        <h2>投诉处理信息</h2>
        <div class="filter-buttons">
          <button @click="filterHandled">已处理</button>
          <button @click="filterUnhandled">未处理</button>
        </div>
      </div>
      <!-- 可滚动的表格容器 -->
      <div class="table-container">
        <table>
          <thead>
          <tr>
            <th>序号</th>
            <th>信息</th>
            <th>处理状态</th>
            <th>操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(item, index) in volunteerApplications" :key="index">
            <td>{{ index + 1 }}</td>
            <td>
              <p>投诉ID：{{ item.volunteerId }}</p>
              <p>投诉类型：{{ item.idNumber }}</p>
              <p>投诉内容：{{ item.contact }}</p>
              <p>投诉时间：{{ item.applicationTime }}</p>
            </td>
            <td>{{ item.status }}</td>
            <td>
              <button @click="viewDetails(item)">查看详情</button>
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
import { useRouter } from 'vue-router';
import {useAdminStore} from "@/stores/adminStore.js";

const router = useRouter();
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
    idNumber: '活动安全',
    contact: '植树志愿活动当天气候恶劣，主办方仍然进行',
    applicationTime: '2024-01-01 12:00:00',
    status: '未处理'
  },
  {
    volunteerId: '456',
    idNumber: '信息虚假',
    contact: '交通指挥志愿活动的奖品信息是虚假的',
    applicationTime: '2024-01-02 13:00:00',
    status: '未处理'
  },
  {
    volunteerId: '789',
    idNumber: '行为不当',
    contact: '管理员拒绝通过我的志愿者认证',
    applicationTime: '2024-01-03 14:00:00',
    status: '未处理'
  },
  {
    volunteerId: '666',
    idNumber: '管理员对投诉事件处理马虎不到位',
    contact: '13900139000',
    applicationTime: '2024-01-02 13:00:00',
    status: '未处理'
  },
  {
    volunteerId: '777',
    idNumber: '活动安全',
    contact: '景区检票人流众多可能危及人身安全',
    applicationTime: '2024-01-02 13:00:00',
    status: '未处理'
  },
  {
    volunteerId: '888',
    idNumber: '行为不当',
    contact: '志愿组织机构培训过程中产生不公平对待',
    applicationTime: '2024-01-02 13:00:00',
    status: '未处理'
  }
]);

// 筛选已处理的申请
const filterHandled = () => {
  // 这里可以添加筛选逻辑
};

// 筛选未处理的申请
const filterUnhandled = () => {
  // 这里可以添加筛选逻辑
};

// 查看详细信息
const viewDetails = (item) => {
  router.push({ name: 'managerOperation' });
};

// 通过认证
const approveApplication = (item) => {
  // 这里可以添加通过认证的逻辑
};

// 驳回申请
const rejectApplication = (item) => {
  // 这里可以添加驳回申请的逻辑
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