<template>
  <div class="organization-detail-container">
    <div class="header">
      <h1>志愿组织信息</h1>
    </div>

    <div class="content-wrapper">
      <el-card class="main-card">
        <div v-if="organizationStore.isLoading" class="loading-section">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>

        <div v-else-if="organizationStore.error" class="error-section">
          <div class="error-icon">⚠️</div>
          <p>{{ organizationStore.error }}</p>
          <el-button @click="retryFetch" type="primary">重试</el-button>
        </div>

        <div v-else-if="organizationStore.detailedOrganizationInfo && organizationStore.detailedOrganizationInfo.orgId" class="organization-detail">
          <div class="info-section">
            <div class="section-header">
              <h2>基本信息</h2>
              <div class="header-controls">
                <el-tag :type="getStatusTagType(organizationStore.detailedOrganizationInfo.orgAccountStatus)" class="status-tag">
                  {{ organizationStore.detailedOrganizationInfo.orgAccountStatus }}
                </el-tag>
                <el-button
                    @click="changeInfo"
                    type="primary"
                    class="edit-btn">
                  修改信息
                </el-button>
              </div>
            </div>

            <div class="info-grid">
              <div class="info-item">
                <label class="info-label">组织ID</label>
                <div class="info-value">{{ organizationStore.detailedOrganizationInfo.orgId || '-' }}</div>
              </div>

              <div class="info-item">
                <label class="info-label">组织名称</label>
                <div class="info-value">{{ organizationStore.detailedOrganizationInfo.orgName || '-' }}</div>
              </div>

              <div class="info-item">
                <label class="info-label">组织登录用户名</label>
                <div class="info-value">{{ organizationStore.detailedOrganizationInfo.orgLoginUserName || '-' }}</div>
              </div>

              <div class="info-item">
                <label class="info-label">组织登录密码</label>
                <div class="info-value">********</div>
              </div>

              <div class="info-item">
                <label class="info-label">负责人联系方式</label>
                <div class="info-value">{{ organizationStore.detailedOrganizationInfo.contactPersonPhone || '-' }}</div>
              </div>

              <div class="info-item">
                <label class="info-label">服务区域</label>
                <div class="info-value">{{ organizationStore.detailedOrganizationInfo.serviceRegion || '-' }}</div>
              </div>

              <div class="info-item">
                <label class="info-label">组织规模 (人数)</label>
                <div class="info-value">{{ organizationStore.detailedOrganizationInfo.orgScale || '-' }}</div>
              </div>

              <div class="info-item">
                <label class="info-label">组织评分</label>
                <div class="info-value">{{ organizationStore.detailedOrganizationInfo.orgRating || '-' }}</div>
              </div>

              <div class="info-item">
                <label class="info-label">服务总时长 (小时)</label>
                <div class="info-value">{{ organizationStore.detailedOrganizationInfo.totalServiceHours || '0' }}</div>
              </div>

              <div class="info-item">
                <label class="info-label">活动举办次数</label>
                <div class="info-value">{{ organizationStore.detailedOrganizationInfo.activityCount || '0' }}</div>
              </div>

              <div class="info-item">
                <label class="info-label">培训举办次数</label>
                <div class="info-value">{{ organizationStore.detailedOrganizationInfo.trainingCount || '0' }}</div>
              </div>
            </div>
          </div>

          <div class="button-group">
            <el-button @click="home" class="back-btn">返回主页</el-button>
          </div>
        </div>

        <div v-else class="error-section">
          <div class="error-icon">ℹ️</div>
          <p>未能加载组织信息，或组织信息不完整。</p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useOrganizationStore } from "@/stores/organizationStore.js";

const organizationStore = useOrganizationStore();
const router = useRouter();

onMounted(() => {
  // The existing logic to fetch data is preserved.
  if (!organizationStore.detailedOrganizationInfo.orgId && organizationStore.isAuthenticated) {
    organizationStore.fetchDetailedOrganizationInfo();
  } else if (!organizationStore.isAuthenticated && localStorage.getItem('xm-pro-organization')) {
    organizationStore.initializeStore();
  }
});

const home = () => {
  router.push('/organization-home');
};

const changeInfo = () => {
  router.push('/change-organization-info');
};

// A helper function to retry fetching data, assuming the store has this method.
const retryFetch = () => {
  organizationStore.fetchDetailedOrganizationInfo();
}

// Helper function to determine tag type based on status, copied from the reference style.
const getStatusTagType = (status) => {
  const statusMap = {
    '正常': 'success',
    '待审核': 'warning',
    '已冻结': 'danger',
    '审核不通过': 'info',
  };
  return statusMap[status] || 'primary';
};
</script>

<style scoped>
/* Copied and adapted styles from the reference component */

/* 基础容器样式 */
.organization-detail-container {
  min-height: 100vh;
  background-image: url('@/../public/bg.png');
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center;
  padding: 0;
  position: relative;
}

.organization-detail-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 51, 51, 0.1);
  z-index: 1;
}

/* 页面头部 */
.header {
  background-color: #ff3333;
  color: white;
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 10px rgba(255, 51, 51, 0.3);
  margin-bottom: 0;
  position: relative;
  z-index: 2;
}

.header h1 {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
}

/* 内容区域 */
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
  min-height: 700px;
  border-radius: 12px;
  box-shadow: 0 8px 25px rgba(255, 51, 51, 0.2);
  border: 1px solid rgba(255, 51, 51, 0.1);
  overflow: hidden;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

.main-card :deep(.el-card__body) {
  padding: 40px;
  min-height: 600px;
}

/* 加载和错误状态 */
.loading-section, .error-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
  min-height: 500px;
  color: #6c757d;
}

.loading-spinner {
  width: 40px;
  height: 40px;
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
  font-size: 3rem;
  margin-bottom: 1rem;
}

/* 信息区域 */
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

.header-controls {
  display: flex;
  align-items: center;
  gap: 15px;
}

.status-tag {
  font-size: 14px;
  padding: 6px 12px;
  height: auto;
  line-height: normal;
}

.edit-btn {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.4);
  color: white;
}

.edit-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.6);
}

/* 基本信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 25px;
  padding: 30px;
}

.info-item {
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

.info-value {
  padding: 12px 15px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #ff3333;
  font-size: 14px;
  color: #666;
  min-height: 44px; /* Ensure consistent height */
  display: flex;
  align-items: center;
  word-break: break-all;
}

/* 操作按钮 */
.button-group {
  display: flex;
  justify-content: center;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.back-btn {
  background: #f8f9fa;
  border: 2px solid #dee2e6;
  color: #6c757d;
  padding: 12px 30px;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 500;
  min-width: 120px;
  height: 45px;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: #e9ecef;
  border-color: #adb5bd;
  color: #495057;
  transform: translateY(-2px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content-wrapper {
    padding: 20px 15px;
  }

  .main-card :deep(.el-card__body) {
    padding: 20px;
  }

  .info-grid {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 20px;
  }

  .section-header {
    padding: 15px 20px;
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }

  .header-controls {
    width: 100%;
    justify-content: center;
  }
}
</style>
