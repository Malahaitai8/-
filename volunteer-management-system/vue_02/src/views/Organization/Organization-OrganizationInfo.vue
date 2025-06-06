<template>
  <el-card>
    <div class="header">
      <h1>志愿组织信息</h1>
    </div>
    <el-form label-width="200px" style="margin-top: 100px" v-if="organizationStore.detailedOrganizationInfo && organizationStore.detailedOrganizationInfo.orgId">
      <el-form-item label="组织ID">
        <span>{{ organizationStore.detailedOrganizationInfo.orgId }}</span>
      </el-form-item>
      <el-form-item label="组织名称">
        <span>{{ organizationStore.detailedOrganizationInfo.orgName }}</span>
      </el-form-item>
      <el-form-item label="组织登录用户名">
        <span>{{ organizationStore.detailedOrganizationInfo.orgLoginUserName }}</span>
      </el-form-item>
      <el-form-item label="组织登录密码">
        <span>********</span> </el-form-item>
      <el-form-item label="负责人联系方式">
        <span>{{ organizationStore.detailedOrganizationInfo.contactPersonPhone }}</span>
      </el-form-item>
      <el-form-item label="服务区域">
        <span>{{ organizationStore.detailedOrganizationInfo.serviceRegion }}</span>
      </el-form-item>
      <el-form-item label="组织规模 (人数)">
        <span>{{ organizationStore.detailedOrganizationInfo.orgScale }}</span>
      </el-form-item>
      <el-form-item label="组织评分">
        <span>{{ organizationStore.detailedOrganizationInfo.orgRating }}</span>
      </el-form-item>
      <el-form-item label="组织账户状态">
        <span>{{ organizationStore.detailedOrganizationInfo.orgAccountStatus }}</span>
      </el-form-item>
      <el-form-item label="服务总时长 (小时)">
        <span>{{ organizationStore.detailedOrganizationInfo.totalServiceHours }}</span>
      </el-form-item>
      <el-form-item label="活动举办次数">
        <span>{{ organizationStore.detailedOrganizationInfo.activityCount }}</span>
      </el-form-item>
      <el-form-item label="培训举办次数">
        <span>{{ organizationStore.detailedOrganizationInfo.trainingCount }}</span>
      </el-form-item>
    </el-form>
    <div v-else-if="organizationStore.isLoading">
      <p>正在加载组织信息...</p>
    </div>
    <div v-else>
      <p>未能加载组织信息，或组织信息不完整。</p>
    </div>
    <el-button type="primary" @click="home" style="margin-top: 20px;">返回主页</el-button>
    <el-button type="primary" @click="changeInfo" style="margin-top: 20px;">修改信息</el-button>
  </el-card>
</template>

<script setup>
import { computed, onMounted } from 'vue'; // 移除了 ref 因为不再需要本地静态 organization
import { useRouter } from 'vue-router';
import { useOrganizationStore } from "@/stores/organizationStore.js"; // 确保路径正确

const organizationStore = useOrganizationStore();
const router = useRouter();

// defineOptions({ name: 'OrganizationDisplayPage' }); // 可选

onMounted(() => {
  // 确保 store 初始化逻辑被调用。
  // 理想情况下，initializeStore 应该在应用加载时（如 main.js）或 store 首次被注入时调用一次。
  // 这里的调用是作为一种确保机制，特别是如果用户直接导航到此页面。
  if (!organizationStore.detailedOrganizationInfo.orgId && organizationStore.isAuthenticated) {
    // 如果已认证但详细信息不完整，尝试获取
    console.log('Organization Info Page: Attempting to fetch detailed organization info.');
    organizationStore.fetchDetailedOrganizationInfo();
  } else if (!organizationStore.isAuthenticated && localStorage.getItem('xm-pro-organization')) {
    // 如果 localStorage 中有会话信息但 store 中未认证，尝试初始化
     organizationStore.initializeStore();
  }
});

// 不再需要本地的 maskedPassword，因为我们不应该从 store 中获取密码来掩码。
// 如果确实需要展示密码已设置的指示，可以在模板中硬编码掩码。

const home = () => {
  router.push('/organization-home'); // 确保这是正确的主页路由
};

const changeInfo = () => {
  // 导航到修改信息的页面，通常会传递组织ID或从 store 中读取
  //router.push({ name: 'OrganizationChangeInfo', params: { orgId: organizationStore.detailedOrganizationInfo.orgId } });
  router.push('/change-organization-info'); //如果路由不需要参数或参数通过 store 获取
};

// 现在模板将直接从 organizationStore.detailedOrganizationInfo 读取数据
</script>

<style scoped>
.header {
  text-align: center;
  margin-bottom: 20px; /* 与表单的 margin-top 配合 */
}
/* 您可以添加其他需要的样式 */
</style>

<style scoped>
.header {
  background-color: #ff3333; /* 鲜红色背景 */
  color: white;
  padding: 10px 20px;
  margin-bottom: 20px;
  text-align: left;
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  z-index: 1000;
}

</style>