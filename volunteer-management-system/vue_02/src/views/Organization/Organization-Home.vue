<template>
  <div class="organization-home-container">
    <div class="header">
      <div class="header-content">
        <img src="@/../public/orgalogo.png" alt="Logo" class="logo">
        <h1>志愿管理系统</h1>
      </div>
      <span class="welcome-message">欢迎您，{{ displayNameFromStore }}</span>
    </div>

    <div class="content-wrapper">
      <el-card class="main-card">
        <div class="button-grid">
          <el-button class="grid-button" @click="organizationInfo">
            <el-icon><OfficeBuilding /></el-icon>
            <span>志愿组织信息</span>
          </el-button>
          <el-button class="grid-button" @click="applyActivity">
            <el-icon><Promotion /></el-icon>
            <span>申请志愿活动</span>
          </el-button>
          <el-button class="grid-button" @click="applyTraining">
            <el-icon><DataAnalysis /></el-icon>
            <span>申请志愿培训</span>
          </el-button>
          <el-button class="grid-button" @click="viewActivityRecords">
            <el-icon><Tickets /></el-icon>
            <span>查看活动记录</span>
          </el-button>
          <el-button class="grid-button" @click="viewCheckVolunteer">
            <el-icon><User /></el-icon>
            <span>审核活动报名</span>
          </el-button>
          <el-button class="grid-button" @click="viewTrainingRecords">
            <el-icon><Reading /></el-icon>
            <span>查看培训记录</span>
          </el-button>
          <el-button class="grid-button" @click="managePersonnel">
            <el-icon><Files /></el-icon>
            <span>人员管理</span>
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { useOrganizationStore } from "@/stores/organizationStore.js";
import { computed, onMounted } from "vue";
// Import icons from element-plus
import {
  OfficeBuilding,
  Promotion,
  DataAnalysis,
  Tickets,
  User,
  Reading,
  Files,
} from '@element-plus/icons-vue';

const organizationStore = useOrganizationStore();
organizationStore.initializeStore();
const displayNameFromStore = computed(() => organizationStore.displayName);

const router = useRouter();

const organizationInfo = () => router.push('/organization-info');
const applyActivity = () => router.push('/apply-activity');
const applyTraining = () => router.push('/apply-training');
const viewActivityRecords = () => router.push('/view-activity-records');
const viewCheckVolunteer = () => router.push('/OrganizationCheckVolunteer');
const viewTrainingRecords = () => router.push('/view-training-records');
const managePersonnel = () => router.push('/manage-personnel');

onMounted(() => {
  if (organizationStore.isAuthenticated && !organizationStore.detailedOrganizationInfo.orgId) {
    // The store will handle fetching data if necessary upon initialization.
    // No explicit fetch call is needed here unless you want to force a refresh.
  }
});
</script>

<style scoped>
/* Base container styling */
.organization-home-container {
  min-height: 100vh;
  background-image: url('@/../public/bg.png');
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center;
  position: relative;
  overflow: hidden;
}

.organization-home-container::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(255, 51, 51, 0.08); /* Lighter overlay for a softer look */
  z-index: 1;
}

/* Header styling */
.header {
  background: rgba(255, 51, 51, 0.9);
  color: white;
  padding: 15px 30px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 4px 12px rgba(255, 51, 51, 0.3);
  position: relative;
  z-index: 2;
}

.header-content {
  display: flex;
  align-items: center;
}

.logo {
  height: 40px;
  margin-right: 15px;
  filter: drop-shadow(0 2px 3px rgba(0,0,0,0.2));
}

.header h1 {
  margin: 0;
  font-size: 26px;
  font-weight: 600;
}

.welcome-message {
  font-size: 16px;
  font-weight: 500;
}

/* Content area styling */
.content-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 72px); /* Adjust based on header height */
  padding: 40px;
  position: relative;
  z-index: 2;
}

.main-card {
  width: 100%;
  max-width: 1200px;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
}

.main-card :deep(.el-card__body) {
  padding: 50px;
}

/* Button grid styling */
.button-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 30px;
}

.grid-button {
  height: 120px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border-radius: 12px;
  font-size: 18px;
  font-weight: 500;
  color: white;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #ff5f6d, #ff3333);
  box-shadow: 0 4px 15px rgba(255, 51, 51, 0.3);
}

.grid-button .el-icon {
  font-size: 36px;
  margin-bottom: 12px;
}

.grid-button:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(255, 51, 51, 0.4);
  background: linear-gradient(135deg, #ff6b7a, #ff4d4d);
}

.grid-button:active {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(255, 51, 51, 0.3);
}

/* Responsive design */
@media (max-width: 768px) {
  .header {
    flex-direction: column;
    padding: 15px;
  }
  .header-content {
    margin-bottom: 10px;
  }
  .content-wrapper {
    padding: 20px;
    align-items: flex-start;
    padding-top: 40px;
  }
  .main-card :deep(.el-card__body) {
    padding: 30px;
  }
  .button-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  .grid-button {
    height: 100px;
  }
}
</style>