<template>
  <div class="training-detail-container">
    <!-- 页面头部 -->
    <div class="header">
      <h1>培训详情</h1>
    </div>

    <!-- 主要内容区域 -->
    <div class="content-wrapper">
      <el-card class="main-card">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-section">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="error" class="error-section">
          <div class="error-icon">⚠️</div>
          <p>{{ error }}</p>
          <el-button @click="fetchTrainingDetail" type="primary">重试</el-button>
        </div>

        <!-- 培训详情内容 -->
        <div v-else-if="training" class="training-detail">
          <!-- 基本信息 -->
          <div class="info-section">
            <div class="section-header">
              <h2>基本信息</h2>
              <div class="header-controls">
                <el-tag :class="getStatusClass(training.trainingStatus)" class="status-tag">
                  {{ training.trainingStatus }}
                </el-tag>
                <el-button
                    v-if="!isEditing"
                    @click="startEdit"
                    type="primary"
                    class="edit-btn">
                  修改
                </el-button>
                <div v-else class="edit-controls">
                  <el-button @click="saveChanges" type="success" :loading="isSaving">保存</el-button>
                  <el-button @click="cancelEdit" type="info">取消</el-button>
                </div>
              </div>
            </div>

            <div class="info-grid">
              <!-- 培训名称 -->
              <div class="info-item">
                <label class="info-label">培训名称</label>
                <div v-if="!isEditing" class="info-value">{{ training.trainingName || '-' }}</div>
                <el-input v-else v-model="editForm.trainingName" class="edit-input"/>
              </div>

              <!-- 培训状态 -->
              <div class="info-item">
                <label class="info-label">培训状态</label>
                <div v-if="!isEditing" class="info-value">{{ training.trainingStatus || '-' }}</div>
                <el-select v-else v-model="editForm.trainingStatus" class="edit-input">
                  <el-option label="待审核" value="待审核"/>
                  <el-option label="进行中" value="进行中"/>
                  <el-option label="已结束" value="已结束"/>
                </el-select>
              </div>

              <!-- 培训地点 -->
              <div class="info-item">
                <label class="info-label">培训地点</label>
                <div v-if="!isEditing" class="info-value">{{ training.location || '-' }}</div>
                <el-input v-else v-model="editForm.location" class="edit-input"/>
              </div>

              <!-- 创建时间 -->
              <div class="info-item">
                <label class="info-label">创建时间</label>
                <div class="info-value">{{ formatDateTime(training.creationTime) || '-' }}</div>
              </div>

              <!-- 招募人数 -->
              <div class="info-item">
                <label class="info-label">招募人数</label>
                <div v-if="!isEditing" class="info-value">{{ training.recruitmentCount || '-' }}</div>
                <el-input-number v-else v-model="editForm.recruitmentCount" :min="0" class="edit-input"/>
              </div>

              <!-- 负责人联系电话 -->
              <div class="info-item">
                <label class="info-label">负责人联系电话</label>
                <div v-if="!isEditing" class="info-value">{{ training.contactPersonPhone || '-' }}</div>
                <el-input v-else v-model="editForm.contactPersonPhone" class="edit-input"/>
              </div>

              <!-- 审核管理员ID -->
              <div class="info-item">
                <label class="info-label">审核管理员ID</label>
                <div class="info-value">{{ training.reviewerAdminId || '-' }}</div>
              </div>

              <!-- 培训评分 -->
              <div class="info-item">
                <label class="info-label">培训评分</label>
                <div v-if="!isEditing" class="info-value">{{ training.trainingRating || '-' }}</div>
                <el-input-number v-else v-model="editForm.trainingRating" :min="0" :max="10" :precision="1" class="edit-input"/>
              </div>

              <!-- 开始时间 (自动同步，不可编辑) -->
              <div class="info-item">
                <label class="info-label">开始时间</label>
                <div class="info-value">
                  {{ formatDateTime(training.startTime) || '-' }}
                  <el-tooltip v-if="isEditing" content="此字段由系统根据时段安排自动同步" placement="top">
                    <el-icon style="margin-left: 4px; color: #909399;">
                      <InfoFilled/>
                    </el-icon>
                  </el-tooltip>
                </div>
              </div>

              <!-- 结束时间 (自动同步，不可编辑) -->
              <div class="info-item">
                <label class="info-label">结束时间</label>
                <div class="info-value">
                  {{ formatDateTime(training.endTime) || '-' }}
                  <el-tooltip v-if="isEditing" content="此字段由系统根据时段安排自动同步" placement="top">
                    <el-icon style="margin-left: 4px; color: #909399;">
                      <InfoFilled/>
                    </el-icon>
                  </el-tooltip>
                </div>
              </div>
            </div>
          </div>

          <!-- 时间安排 -->
          <div class="info-section">
            <div class="section-header">
              <h2>时间安排</h2>
              <div class="header-actions">
                <el-tag class="count-tag">共 {{ timeslots.length }} 个时段</el-tag>
                <el-button
                    type="primary"
                    size="small"
                    @click="showAddTimeslotDialog = true"
                    icon="el-icon-plus"
                    class="add-timeslot-btn"
                >
                  添加时段
                </el-button>
              </div>
            </div>

            <div v-if="isLoadingTimeslots" class="loading-section mini">
              <div class="loading-spinner small"></div>
              <p>加载时段信息中...</p>
            </div>
            <div v-else-if="timeslots.length === 0" class="empty-section">
              <p>暂无时段信息</p>
            </div>
            <div v-else class="timeslots-container">
              <div v-for="(slot, index) in timeslots" :key="slot.timeslotId" class="timeslot-card">
                <div class="timeslot-number">{{ index + 1 }}</div>
                <div class="timeslot-content">
                  <div class="timeslot-time">
                    <span class="start-time">{{ formatDateTime(slot.startTime) }}</span>
                    <span class="separator">至</span>
                    <span class="end-time">{{ formatDateTime(slot.endTime) }}</span>
                  </div>
                  <div class="timeslot-duration">
                    持续: {{ calculateDuration(slot.startTime, slot.endTime) }}
                  </div>
                </div>
                <div class="timeslot-actions">
                  <el-button
                      type="danger"
                      size="small"
                      @click="deleteTimeslot(slot.timeslotId)"
                      icon="el-icon-delete"
                      class="delete-timeslot-btn"
                      title="删除时段"
                  />
                </div>
              </div>
            </div>
          </div>

          <!-- 参与志愿者 -->
          <div class="info-section">
            <div class="section-header">
              <h2>参与志愿者</h2>
              <div class="header-right">
                <el-tag class="count-tag">共 {{ participants.length }} 名志愿者</el-tag>
              </div>
            </div>

            <div v-if="isLoadingParticipants" class="loading-section mini">
              <div class="loading-spinner small"></div>
              <p>加载参与者信息中...</p>
            </div>
            <div v-else-if="participants.length === 0" class="empty-section">
              <p>暂无参与者信息</p>
            </div>
            <div v-else class="participants-container">
              <!-- [MODIFIED] Participant card structure updated -->
              <div v-for="participant in participants" :key="participant.volunteerId" class="participant-card">
                <div class="participant-header">
                  <div class="participant-info">
                    <h3 class="participant-name">{{ participant.name }}</h3>
                  </div>
                  <div class="participant-status">
                    <!-- [MODIFIED] Switched from el-tag to el-switch -->
                    <el-switch
                        v-model="participant.isCheckedIn"
                        active-value="是"
                        inactive-value="否"
                        @change="updateCheckInStatus(participant)"
                        inline-prompt
                        style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
                        active-text="已签到"
                        inactive-text="未签到"
                    />
                  </div>
                </div>
                <div class="participant-details">
                  <div class="detail-row">
                    <span class="detail-label">用户ID:</span>
                    <span class="detail-value">{{ participant.volunteerId || '-' }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="detail-label">联系电话:</span>
                    <span class="detail-value">{{ participant.phone || '-' }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="detail-label">性别:</span>
                    <span class="detail-value">{{ participant.gender || '-' }}</span>
                  </div>
                   <div class="detail-row">
                    <span class="detail-label">政治面貌:</span>
                    <span class="detail-value">{{ participant.politicalStatus || '-' }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="detail-label">学历:</span>
                    <span class="detail-value">{{ participant.highestEducation || '-' }}</span>
                  </div>
                   <div class="detail-row">
                    <span class="detail-label">服务类别:</span>
                    <span class="detail-value">{{ participant.serviceCategory || '-' }}</span>
                  </div>
                  <div class="detail-row" v-if="participant.orgToVolunteerRating">
                    <span class="detail-label">组织评分:</span>
                    <div class="rating-display">
                      <el-rate
                          v-model="participant.orgToVolunteerRating"
                          disabled
                          show-score
                          text-color="#ff9900"
                          size="small"
                      />
                    </div>
                  </div>
                  <div class="detail-row" v-if="participant.volunteerToOrgRating">
                    <span class="detail-label">志愿者评分:</span>
                    <div class="rating-display">
                      <el-rate
                          v-model="participant.volunteerToOrgRating"
                          disabled
                          show-score
                          text-color="#ff9900"
                          size="small"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 添加时段对话框 -->
    <el-dialog
        v-model="showAddTimeslotDialog"
        title="添加时段"
        width="500px"
        :before-close="handleTimeslotDialogClose"
    >
      <el-form
          :model="addTimeslotForm"
          :rules="timeslotRules"
          ref="timeslotFormRef"
          label-width="80px"
      >
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
              v-model="addTimeslotForm.startTime"
              type="datetime"
              placeholder="选择开始时间"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
              v-model="addTimeslotForm.endTime"
              type="datetime"
              placeholder="选择结束时间"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddTimeslotDialog = false">取消</el-button>
          <el-button
              type="primary"
              @click="handleAddTimeslot"
              :loading="isAddingTimeslot"
          >
            提交
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 操作按钮 -->
    <div class="button-group">
      <el-button @click="goBack" class="back-btn">返回</el-button>
    </div>
  </div>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'
import request from '@/utils/request.js'
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

export default {
  name: 'OrganizationTrainingDetail',
  components: {
    InfoFilled
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const training = ref(null)
    const timeslots = ref([])
    const participants = ref([])
    const loading = ref(true)
    const isLoadingTimeslots = ref(false)
    const isLoadingParticipants = ref(false)
    const error = ref(null)
    const isEditing = ref(false)
    const isSaving = ref(false)
    const showAddTimeslotDialog = ref(false)
    const isAddingTimeslot = ref(false)
    const timeslotFormRef = ref(null)
    const editForm = ref({})

    const addTimeslotForm = reactive({
      startTime: '',
      endTime: '',
    })

    const timeslotRules = {
      startTime: [
        { required: true, message: '请选择开始时间', trigger: 'change' }
      ],
      endTime: [
        { required: true, message: '请选择结束时间', trigger: 'change' }
      ]
    }

    const fetchTrainingDetail = async () => {
      loading.value = true
      error.value = null
      try {
        const res = await request.get(`/volunteerTraining/get/${route.params.id}`)
        if (res.code === '200' && res.data) {
          training.value = res.data
          // [MODIFIED] Fetching participants now gets full volunteer details
          await Promise.all([
            fetchTimeslots(),
            fetchParticipants()
          ])
        } else {
          error.value = res.msg || '获取培训详情失败'
        }
      } catch (err) {
        console.error(err)
        error.value = '网络错误，无法加载培训详情'
      } finally {
        loading.value = false
      }
    }

    const fetchTimeslots = async () => {
      if (!training.value?.trainingId) return
      isLoadingTimeslots.value = true
      try {
        const res = await request.get(`/activityTimeslot/byEvent/${training.value.trainingId}`)
        if (res.code === '200') {
          timeslots.value = res.data || []
        } else {
          ElMessage.error(res.msg || '获取时段列表失败')
        }
      } catch (err) {
        console.error('获取时段列表失败:', err)
        ElMessage.error('网络错误，获取时段列表失败')
      } finally {
        isLoadingTimeslots.value = false
      }
    }

    const fetchParticipants = async () => {
      isLoadingParticipants.value = true
      try {
        // This endpoint now returns detailed volunteer info
        const res = await request.get(`/volunteerTraining/${route.params.id}/participants`)
        if (res.code === '200' && res.data) {
          // ensure every participant has an isCheckedIn property
          participants.value = res.data.map(p => ({
            ...p,
            isCheckedIn: p.isCheckedIn || '否' // Default to '否' if null/undefined
          }))
        } else {
           participants.value = []
           console.error(res.msg || '获取参与者列表失败')
        }
      } catch (err) {
        console.error('获取参与者信息出错:', err)
      } finally {
        isLoadingParticipants.value = false
      }
    }

    // [NEW] Method to update check-in status
    const updateCheckInStatus = async (participant) => {
      const originalStatus = participant.isCheckedIn === '是' ? '否' : '是'
      try {
        const payload = {
          trainingId: route.params.id,
          volunteerId: participant.volunteerId,
          isCheckedIn: participant.isCheckedIn,
        };
        const res = await request.put('/volunteerTraining/participation/status', payload);
        if (res.code === '200') {
          ElMessage.success('签到状态更新成功！');
        } else {
          // Revert on failure
          participant.isCheckedIn = originalStatus;
          ElMessage.error(res.msg || '更新失败');
        }
      } catch (err) {
        // Revert on failure
        participant.isCheckedIn = originalStatus;
        console.error('更新签到状态出错:', err);
        ElMessage.error('网络错误，更新签到状态失败');
      }
    };


    const handleAddTimeslot = async () => {
      if (!addTimeslotForm.startTime || !addTimeslotForm.endTime) {
        ElMessage.warning('请选择开始时间和结束时间')
        return
      }

      // 验证开始时间是否晚于结束时间
      const startTime = new Date(addTimeslotForm.startTime)
      const endTime = new Date(addTimeslotForm.endTime)
      if (startTime >= endTime) {
        ElMessage.warning('开始时间不能晚于或等于结束时间')
        return
      }

      isAddingTimeslot.value = true
      try {
        const payload = {
          eventId: training.value.trainingId,
          startTime: addTimeslotForm.startTime,
          endTime: addTimeslotForm.endTime,
        }

        const res = await request.post('/activityTimeslot', payload, {
          timeout: 10000 // 设置10秒超时
        })

        if (res.code === '200') {
          ElMessage.success('时段添加成功！')
          showAddTimeslotDialog.value = false
          addTimeslotForm.startTime = ''
          addTimeslotForm.endTime = ''
          await fetchTimeslots()
          await fetchTrainingDetail()
        } else {
          ElMessage.error(res.msg || '添加时段失败')
        }
      } catch (err) {
        console.error('添加时段出错:', err)
        if (err.code === 'ECONNABORTED') {
          ElMessage.error('请求超时，请检查网络连接或稍后重试')
        } else if (err.response) {
          ElMessage.error(err.response.data.msg || '添加时段失败')
        } else {
          ElMessage.error('网络错误，添加时段失败')
        }
      } finally {
        isAddingTimeslot.value = false
      }
    }

    const deleteTimeslot = async (timeslotId) => {
      try {
        await ElMessageBox.confirm('确定要删除这个时段吗？', '确认删除', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })

        const res = await request.delete(`/activityTimeslot/${timeslotId}/${training.value.trainingId}`)
        if (res.code === '200') {
          ElMessage.success('时段删除成功！')
          await fetchTimeslots()
          await fetchTrainingDetail()
        } else {
          ElMessage.error(res.msg || '删除时段失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除时段失败:', error)
          ElMessage.error('网络错误，删除时段失败')
        }
      }
    }

    const startEdit = () => {
      isEditing.value = true
      editForm.value = {
        ...training.value,
        startTime: training.value.startTime ? new Date(training.value.startTime).toISOString().slice(0, 19) : null,
        endTime: training.value.endTime ? new Date(training.value.endTime).toISOString().slice(0, 19) : null
      }
    }

    const cancelEdit = () => {
      isEditing.value = false
      editForm.value = {}
    }

    const saveChanges = async () => {
      isSaving.value = true
      try {
        const res = await request.put(`/volunteerTraining/update`, editForm.value)
        if (res.code === '200') {
          ElMessage.success('培训信息更新成功！')
          isEditing.value = false
          await fetchTrainingDetail()
        } else {
          ElMessage.error(res.msg || '更新失败')
        }
      } catch (error) {
        console.error('更新培训信息出错:', error)
        ElMessage.error('更新失败: ' + error.message)
      } finally {
        isSaving.value = false
      }
    }

    const formatDateTime = (dateString) => {
      if (!dateString) return '-'
      try {
        const date = new Date(dateString)
        return date.toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        })
      } catch (error) {
        return dateString
      }
    }

    const calculateDuration = (startTime, endTime) => {
      if (!startTime || !endTime) return '-'
      try {
        const start = new Date(startTime)
        const end = new Date(endTime)
        const duration = end - start
        const hours = Math.floor(duration / (1000 * 60 * 60))
        const minutes = Math.floor((duration % (1000 * 60 * 60)) / (1000 * 60))
        return `${hours}小时${minutes}分钟`
      } catch (error) {
        return '-'
      }
    }

    const getStatusClass = (status) => {
      if (!status) return ''
      const statusMap = {
        '待审核': 'status-pending',
        '审核通过': 'status-approved',
        '审核不通过': 'status-rejected',
        '已取消': 'status-cancelled',
        '已完成': 'status-completed',
        '进行中': 'status-in-progress'
      }
      return `status-tag ${statusMap[status] || 'status-unknown'}`
    }

    const handleTimeslotDialogClose = () => {
      showAddTimeslotDialog.value = false
      addTimeslotForm.startTime = ''
      addTimeslotForm.endTime = ''
    }

    const goBack = () => {
      router.back()
    }

    onMounted(() => {
      fetchTrainingDetail()
    })

    return {
      training,
      timeslots,
      participants,
      loading,
      isLoadingTimeslots,
      isLoadingParticipants,
      error,
      isEditing,
      isSaving,
      showAddTimeslotDialog,
      isAddingTimeslot,
      timeslotFormRef,
      editForm,
      addTimeslotForm,
      timeslotRules,
      formatDateTime,
      calculateDuration,
      getStatusClass,
      handleAddTimeslot,
      deleteTimeslot,
      startEdit,
      cancelEdit,
      saveChanges,
      handleTimeslotDialogClose,
      goBack,
      fetchTrainingDetail,
      updateCheckInStatus, // [NEW] Expose method
    }
  }
}
</script>

<style scoped>
.training-detail-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7eb 100%);
  padding: 20px;
}

.header {
  text-align: center;
  margin-bottom: 30px;
}

.header h1 {
  font-size: 28px;
  color: #333;
  margin: 0;
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
}

.loading-section.mini {
  padding: 2rem;
  min-height: 120px;
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

.loading-spinner.small {
  width: 24px;
  height: 24px;
  border-width: 3px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
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

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.add-timeslot-btn {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.4);
  color: white;
  border-radius: 6px;
  font-size: 12px;
  padding: 8px 16px;
}

.add-timeslot-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.6);
}

.header-controls {
  display: flex;
  align-items: center;
  gap: 15px;
}

.status-tag {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
}

.status-pending {
  background-color: #e6a23c;
  color: white;
}

.status-approved {
  background-color: #67c23a;
  color: white;
}

.status-rejected {
  background-color: #f56c6c;
  color: white;
}

.status-cancelled {
  background-color: #909399;
  color: white;
}

.status-completed {
  background-color: #409eff;
  color: white;
}

.status-in-progress {
  background-color: #67c23a;
  color: white;
}

.status-unknown {
  background-color: #909399;
  color: white;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.count-tag {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  font-size: 14px;
  padding: 6px 12px;
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

.edit-controls {
  display: flex;
  gap: 10px;
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
  min-height: 20px;
}

.edit-input {
  width: 100%;
}

.edit-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  transition: all 0.3s ease;
}

.edit-input :deep(.el-input__wrapper:hover) {
  border-color: #ff6666;
}

.edit-input :deep(.el-input.is-focus .el-input__wrapper) {
  border-color: #ff3333;
  box-shadow: 0 0 0 2px rgba(255, 51, 51, 0.2);
}

/* 时段容器 */
.timeslots-container {
  padding: 30px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.timeslot-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  border-left: 4px solid #ff3333;
  transition: all 0.3s ease;
  position: relative;
}

.timeslot-number {
  width: 24px;
  height: 24px;
  background: #ff3333;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  margin-right: 15px;
}

.timeslot-content {
  flex: 1;
}

.timeslot-time {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
}

.separator {
  margin: 0 8px;
  color: #999;
}

.timeslot-duration {
  font-size: 12px;
  color: #666;
}

.timeslot-actions {
  margin-left: 15px;
}

/* 参与者容器 */
.participants-container {
  padding: 30px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr)); /* Adjusted minmax */
  gap: 20px;
}

.participant-card {
  background: #f8f9fa;
  border-radius: 12px;
  border-left: 4px solid #ff3333;
  overflow: hidden;
  display: flex; /* Added */
  flex-direction: column; /* Added */
}

.participant-header {
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  border-bottom: 1px solid #eee;
}

.participant-info {
  flex: 1;
}

.participant-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.participant-details {
  padding: 15px 20px;
  flex-grow: 1; /* Added */
}

.detail-row {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  font-size: 14px; /* Standardized font size */
}

.detail-row:last-child {
  margin-bottom: 0;
}

.detail-label {
  width: 80px;
  color: #666;
  flex-shrink: 0; /* Prevent label from shrinking */
}

.detail-value {
  flex: 1;
  color: #333;
  word-break: break-all; /* Prevent long values from overflowing */
}

.rating-display {
  flex: 1;
}

/* 空状态 */
.empty-section {
  padding: 40px;
  text-align: center;
  color: #909399;
}

/* 按钮组 */
.button-group {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-bottom: 30px;
}

.back-btn {
  min-width: 120px;
}

/* 对话框样式 */
:deep(.el-dialog) {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #ff3333, #ff6666);
  color: white;
  padding: 20px;
  margin: 0;
}

:deep(.el-dialog__title) {
  color: white;
  font-size: 18px;
  font-weight: 600;
}

:deep(.el-dialog__headerbtn .el-dialog__close) {
  color: white;
}

:deep(.el-dialog__body) {
  padding: 30px;
}

:deep(.el-dialog__footer) {
  padding: 20px;
  border-top: 1px solid #eee;
}
</style>
