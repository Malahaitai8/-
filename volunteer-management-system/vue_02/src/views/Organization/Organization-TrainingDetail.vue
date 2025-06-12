<template>
  <div class="training-detail-container">
    <div class="detail-card">
      <div class="header-section">
        <button @click="goBack" class="back-button">
          ← 返回
        </button>
        <h1 class="page-title">培训详情</h1>
      </div>

      <div class="content-section">
        <div v-if="loading" class="loading-section">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>

        <div v-else-if="error" class="error-section">
          <p class="error-message">{{ error }}</p>
        </div>

        <div v-else-if="!training" class="error-section">
          <p class="error-message">未找到培训信息</p>
        </div>

        <div v-else>
          <!-- 基本信息 -->
          <div class="info-card">
            <h2 class="section-title">基本信息</h2>
            <div class="info-grid">
              <div class="info-item">
                <label class="info-label">培训名称</label>
                <div class="info-value">{{ training.trainingName || '--' }}</div>
              </div>
              <div class="info-item">
                <label class="info-label">培训主题</label>
                <div class="info-value">{{ training.theme || '--' }}</div>
              </div>
              <div class="info-item">
                <label class="info-label">培训状态</label>
                <div class="info-value">
                  <span :class="getStatusClass(training.trainingStatus)">{{ training.trainingStatus || '--' }}</span>
                </div>
              </div>
              <div class="info-item">
                <label class="info-label">培训ID</label>
                <div class="info-value">{{ training.trainingId || '--' }}</div>
              </div>
            </div>
          </div>

          <!-- 时间安排 -->
          <div class="info-card">
            <h2 class="section-title">时间安排</h2>
            <div class="info-grid">
              <div class="info-item">
                <label class="info-label">开始时间</label>
                <div class="info-value">{{ formatDateTime(training.startTime) }}</div>
              </div>
              <div class="info-item">
                <label class="info-label">结束时间</label>
                <div class="info-value">{{ formatDateTime(training.endTime) }}</div>
              </div>
              <div class="info-item">
                <label class="info-label">培训地点</label>
                <div class="info-value">{{ training.location || '--' }}</div>
              </div>
              <div class="info-item">
                <label class="info-label">创建时间</label>
                <div class="info-value">{{ formatDateTime(training.creationTime) }}</div>
              </div>
            </div>
          </div>

          <!-- 人员信息 -->
          <div class="info-card">
            <h2 class="section-title">人员信息</h2>
            <div class="info-grid">
              <div class="info-item">
                <label class="info-label">招募人数</label>
                <div class="info-value">{{ training.recruitmentCount || 0 }}</div>
              </div>
              <div class="info-item">
                <label class="info-label">联系电话</label>
                <div class="info-value">{{ training.contactPersonPhone || '--' }}</div>
              </div>
              <div class="info-item">
                <label class="info-label">审核管理员ID</label>
                <div class="info-value">{{ training.reviewerAdminId || '--' }}</div>
              </div>
              <div class="info-item">
                <label class="info-label">培训评分</label>
                <div class="info-value">{{ training.trainingRating || '--' }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/utils/request.js'

const route = useRoute()
const router = useRouter()
const training = ref(null)
const loading = ref(true)
const error = ref(null)

// 获取培训详情
const loadTrainingDetail = async () => {
  const trainingId = route.params.id
  console.log('获取培训详情，ID:', trainingId)
  
  if (!trainingId) {
    error.value = '缺少培训ID参数'
    loading.value = false
    return
  }

  try {
    loading.value = true
    error.value = null
    
    const res = await request.get(`/volunteerTraining/get/${trainingId}`)
    console.log('API响应:', res)
    
    if (res.code === '200' && res.data) {
      training.value = res.data
      console.log('培训数据:', training.value)
    } else {
      error.value = res.msg || '获取培训详情失败'
    }
  } catch (err) {
    console.error('加载培训详情失败:', err)
    error.value = '网络错误或服务器异常'
  } finally {
    loading.value = false
  }
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return '--'
  try {
    return new Date(datetime).toLocaleString('zh-CN')
  } catch (e) {
    return '--'
  }
}

// 根据培训状态返回不同的CSS类
const getStatusClass = (status) => {
  if (!status) return 'status-pending'
  
  switch (status) {
    case '待审核': return 'status-pending'
    case '审核通过': return 'status-approved'
    case '进行中': return 'status-approved'
    case '已结束': return 'status-cancelled'
    case '审核不通过': return 'status-rejected'
    case '已停用': return 'status-rejected'
    default: return 'status-pending'
  }
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}

onMounted(() => {
  loadTrainingDetail()
})
</script>

<style scoped>
.training-detail-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #ff6b6b 100%);
  padding: 20px;
  position: relative;
}

.training-detail-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 20% 70%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
              radial-gradient(circle at 80% 30%, rgba(118, 75, 162, 0.2) 0%, transparent 40%);
  pointer-events: none;
}

.detail-card {
  max-width: 1400px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 24px;
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.15), 
              0 10px 20px rgba(0, 0, 0, 0.1),
              inset 0 1px 0 rgba(255, 255, 255, 0.4);
  overflow: hidden;
  position: relative;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.header-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #ff6b6b 100%);
  color: white;
  padding: 40px 50px;
  position: relative;
  overflow: hidden;
}

.header-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 30% 70%, rgba(255, 255, 255, 0.2) 0%, transparent 50%),
              radial-gradient(circle at 70% 30%, rgba(255, 255, 255, 0.15) 0%, transparent 50%);
  pointer-events: none;
}

.back-button {
  background: rgba(255, 255, 255, 0.25);
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.4);
  padding: 14px 28px;
  border-radius: 30px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(15px);
  position: relative;
  z-index: 10;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.back-button:hover {
  background: rgba(255, 255, 255, 0.35);
  border-color: rgba(255, 255, 255, 0.6);
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.page-title {
  margin: 25px 0 0 0;
  font-size: 36px;
  font-weight: 800;
  text-align: center;
  position: relative;
  z-index: 10;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  letter-spacing: 1px;
}

.content-section {
  padding: 40px;
}

.loading-section,
.error-section {
  text-align: center;
  padding: 80px 40px;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #ff3333;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  color: #ff3333;
  font-size: 18px;
  font-weight: 500;
}

.info-card {
  background: linear-gradient(145deg, #ffffff 0%, #fafbfc 100%);
  border: 1px solid rgba(102, 126, 234, 0.1);
  border-radius: 20px;
  padding: 36px;
  margin-bottom: 28px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.06),
              0 2px 8px rgba(102, 126, 234, 0.1);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.info-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 50%, #ff6b6b 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.info-card:hover {
  transform: translateY(-4px) scale(1.01);
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.12),
              0 8px 16px rgba(102, 126, 234, 0.15);
}

.info-card:hover::before {
  opacity: 1;
}

.section-title {
  color: #667eea;
  font-size: 26px;
  font-weight: 800;
  margin: 0 0 28px 0;
  padding-bottom: 16px;
  border-bottom: 3px solid transparent;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%) bottom / 100% 3px no-repeat;
  display: inline-block;
  position: relative;
  letter-spacing: 0.5px;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-label {
  font-size: 16px;
  font-weight: 600;
  color: #666;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.info-value {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  padding: 18px 24px;
  border-radius: 14px;
  border-left: 4px solid #667eea;
  min-height: 20px;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.info-value:hover {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-left-color: #764ba2;
  transform: translateX(3px);
}

.status-pending {
  background: #fff3cd;
  color: #856404;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 600;
  display: inline-block;
}

.status-approved {
  background: #d4edda;
  color: #155724;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 600;
  display: inline-block;
}

.status-rejected {
  background: #f8d7da;
  color: #721c24;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 600;
  display: inline-block;
}

.status-cancelled {
  background: #e2e3e5;
  color: #383d41;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 600;
  display: inline-block;
}

@media (max-width: 768px) {
  .training-detail-container {
    padding: 10px;
  }
  
  .header-section,
  .content-section {
    padding: 20px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .info-card {
    padding: 20px;
  }
  
  .section-title {
    font-size: 20px;
  }
}
</style> 