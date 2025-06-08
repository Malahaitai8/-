<template>
  <div class="dashboard-content">

    <!-- 顶部用户信息区 -->
    <div class="user-header-section">
      <div class="user-info-display">
        <el-avatar :size="70" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
        <div class="user-greeting">
          <h3 class="username">欢迎回来，张三</h3>
          <div class="greeting-actions">
            <el-button type="primary" size="small" @click="goToProfile" class="edit-profile-btn">
              修改资料
            </el-button>
            <el-button
              :type="realNameStatus ? 'success' : 'warning'"
              size="small"
              class="realname-btn"
              @click="goToProfile"
            >
              {{ realNameStatus ? '已实名' : '未实名' }}
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据统计卡片 - 第一行 -->
    <div class="stat-cards-row">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="stat-card">
            <template #header>
              <div class="card-header">
                <el-icon><Timer /></el-icon>
                <span>总志愿时长</span>
              </div>
            </template>
            <div class="card-content">
              <span class="number">128</span>
              <span class="unit">小时</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="stat-card">
            <template #header>
              <div class="card-header">
                <el-icon><Star /></el-icon>
                <span>志愿者星级</span>
              </div>
            </template>
            <div class="card-content centered-content">
              <el-rate
                  v-model="starLevel"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value}"
                  :colors="['#ff9900', '#ff9900', '#ff9900']"
              />
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="stat-card">
            <template #header>
              <div class="card-header">
                <el-icon><Reading /></el-icon>
                <span>综合评分</span>
              </div>
            </template>
            <div class="card-content centered-content">
              <span class="score-number">{{ totalScore }}</span>
              <span class="score-unit">分</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Timer, Star, UserFilled, List, Reading } from '@element-plus/icons-vue'

export default {
  name: 'Dashboard',
  components: {
    Timer,
    Star,
    UserFilled,
    List,
    Reading
  },
  setup() {
    const router = useRouter()
    const starLevel = ref(4)
    const totalScore = ref(8.5)
    const realNameStatus = ref(true)

    const goToProfile = () => {
      router.push('/profile')
    }

    return {
      starLevel,
      totalScore,
      realNameStatus,
      goToProfile
    }
  }
}
</script>

<style scoped>
.dashboard-content {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
}

.user-header-section {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.user-info-display {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-greeting {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.username {
  margin: 0;
  color: #333;
  font-size: 22px;
  font-weight: bold;
}

.edit-profile-btn {
  background: #ff0000;
  border-color: #ff0000;
  color: #fff;
  border-radius: 4px;
  font-size: 12px;
}

.greeting-actions {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-top: 4px;
}

.realname-btn {
  font-size: 12px;
  border-radius: 4px;
  font-weight: bold;
  padding: 0 12px;
}

.stat-cards-row {
  margin-bottom: 20px;
}

.stat-card {
  height: 150px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  border-radius: 8px;
  overflow: hidden;
}
.stat-card :deep(.el-card__header) {
  padding: 12px 16px;
  background-color: #fff0f0;
  border-bottom: 1px solid #ffcccc;
  font-size: 14px;
  font-weight: bold;
  color: #ff0000;
}
.stat-card :deep(.el-card__body) {
  padding: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex-grow: 1;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
}
.card-header .el-icon {
  color: #ff0000;
}

.card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex-grow: 1;
}
.centered-content {
  justify-content: center;
}

.number {
  font-size: 36px;
  font-weight: bold;
  color: #ff0000;
  line-height: 1.2;
}

.unit {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

/* 图标样式 */
.el-icon {
  font-size: 18px;
}

.score-number {
  font-size: 36px;
  font-weight: bold;
  color: #ff9900;
  line-height: 1.2;
}
.score-unit {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}
</style> 