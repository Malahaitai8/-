<template>
  <div class="activity-detail-container">
    <!-- 页面头部 -->
    <div class="header">
      <h1>活动详情</h1>
    </div>

    <!-- 主要内容区域 -->
    <div class="content-wrapper">
      <el-card class="main-card">
        <!-- 加载状态 -->
        <div v-if="isLoading" class="loading-section">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="error" class="error-section">
          <div class="error-icon">⚠️</div>
          <p>{{ error }}</p>
          <el-button @click="fetchActivityDetail" type="primary">重试</el-button>
        </div>

        <!-- 活动详情内容 -->
        <div v-else-if="activity" class="activity-detail">
          <!-- 基本信息 -->
          <div class="info-section">
            <div class="section-header">
              <h2>基本信息</h2>
              <div class="header-controls">
                <el-tag :type="getStatusTagType(activity.activityStatus)" class="status-tag">
                  {{ activity.activityStatus }}
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
              <!-- 活动名称 -->
              <div class="info-item">
                <label class="info-label">活动名称</label>
                <div v-if="!isEditing" class="info-value">{{ activity.activityName || '-' }}</div>
                <el-input v-else v-model="editForm.activityName" class="edit-input" />
              </div>

              <!-- 活动状态 -->
              <div class="info-item">
                <label class="info-label">活动状态</label>
                <div v-if="!isEditing" class="info-value">{{ activity.activityStatus || '-' }}</div>
                <el-select v-else v-model="editForm.activityStatus" class="edit-input">
                  <el-option label="待审核" value="待审核" />
                  <el-option label="审核通过" value="审核通过" />
                  <el-option label="进行中" value="进行中" />
                  <el-option label="已结束" value="已结束" />
                </el-select>
              </div>

              <!-- 活动地点 -->
              <div class="info-item">
                <label class="info-label">活动地点</label>
                <div v-if="!isEditing" class="info-value">{{ activity.location || '-' }}</div>
                <el-input v-else v-model="editForm.location" class="edit-input" />
              </div>

              <!-- 创建时间 -->
              <div class="info-item">
                <label class="info-label">创建时间</label>
                <div class="info-value">{{ formatDate(activity.creationTime) || '-' }}</div>
              </div>

              <!-- 招募人数 (自动计算，不可编辑) -->
              <div class="info-item">
                <label class="info-label">招募人数</label>
                <div class="info-value">
                  {{ activity.recruitmentCount || '-' }}
                  <el-tooltip v-if="isEditing" content="此字段由系统根据岗位需求人数自动计算" placement="top">
                    <el-icon style="margin-left: 4px; color: #909399;"><InfoFilled /></el-icon>
                  </el-tooltip>
                </div>
              </div>

              <!-- 录取人数 -->
              <div class="info-item">
                <label class="info-label">录取人数</label>
                <div v-if="!isEditing" class="info-value">{{ activity.acceptedCount || '-' }}</div>
                <el-input-number v-else v-model="editForm.acceptedCount" :min="0" class="edit-input" />
              </div>

              <!-- 负责人联系电话 -->
              <div class="info-item">
                <label class="info-label">负责人联系电话</label>
                <div v-if="!isEditing" class="info-value">{{ activity.contactPersonPhone || '-' }}</div>
                <el-input v-else v-model="editForm.contactPersonPhone" class="edit-input" />
              </div>

              <!-- 审核管理员ID -->
              <div class="info-item">
                <label class="info-label">审核管理员ID</label>
                <div class="info-value">{{ activity.reviewerAdminId || '-' }}</div>
              </div>

              <!-- 活动评分 -->
              <div class="info-item">
                <label class="info-label">活动评分</label>
                <div v-if="!isEditing" class="info-value">{{ activity.activityRating || '-' }}</div>
                <el-input-number v-else v-model="editForm.activityRating" :min="0" :max="10" :precision="1" class="edit-input" />
              </div>

              <!-- 活动时长 (自动计算，不可编辑) -->
              <div class="info-item">
                <label class="info-label">活动时长</label>
                <div class="info-value">
                  {{ activity.activityDurationHours !== null && activity.activityDurationHours !== undefined ? activity.activityDurationHours : '-' }} 小时
                  <el-tooltip v-if="isEditing" content="此字段由系统根据时段安排自动计算" placement="top">
                    <el-icon style="margin-left: 4px; color: #909399;"><InfoFilled /></el-icon>
                  </el-tooltip>
                </div>
              </div>

              <!-- 开始时间 (自动同步，不可编辑) -->
              <div class="info-item">
                <label class="info-label">开始时间</label>
                <div class="info-value">
                  {{ formatDateTime(activity.startTime) || '-' }}
                  <el-tooltip v-if="isEditing" content="此字段由系统根据时段安排自动同步" placement="top">
                    <el-icon style="margin-left: 4px; color: #909399;"><InfoFilled /></el-icon>
                  </el-tooltip>
                </div>
              </div>

              <!-- 结束时间 (自动同步，不可编辑) -->
              <div class="info-item">
                <label class="info-label">结束时间</label>
                <div class="info-value">
                  {{ formatDateTime(activity.endTime) || '-' }}
                  <el-tooltip v-if="isEditing" content="此字段由系统根据时段安排自动同步" placement="top">
                    <el-icon style="margin-left: 4px; color: #909399;"><InfoFilled /></el-icon>
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

          <!-- 岗位信息 -->
          <div class="info-section">
            <div class="section-header">
              <h2>岗位信息</h2>
              <div class="header-right">
                <el-tag class="count-tag">共 {{ positions.length }} 个岗位</el-tag>
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="showAddPositionDialog = true"
                  class="add-btn"
                >
                  新增岗位
                </el-button>
              </div>
            </div>
            
            <div v-if="isLoadingPositions" class="loading-section mini">
              <div class="loading-spinner small"></div>
              <p>加载岗位信息中...</p>
            </div>
            <div v-else-if="positions.length === 0" class="empty-section">
              <p>暂无岗位信息</p>
            </div>
            <div v-else class="positions-container">
              <div v-for="position in positions" :key="position.positionId" class="position-card">
                <div class="position-header">
                  <h3 class="position-name">{{ position.positionName }}</h3>
                  <div class="position-stats">
                    <span class="recruited">{{ position.recruitedVolunteers || 0 }}</span>
                    <span>/</span>
                    <span class="required">{{ position.requiredVolunteers || 0 }}</span>
                  </div>
                </div>
                <div class="position-details">
                  <div class="detail-row">
                    <span class="detail-label">服务时长:</span>
                    <span class="detail-value">{{ position.positionServiceHours || 0 }} 小时</span>
                  </div>
                  <div class="detail-row">
                    <span class="detail-label">招募进度:</span>
                    <div class="progress-wrapper">
                      <el-progress 
                        :percentage="getRecruitmentProgress(position)" 
                        :color="getProgressColor(position)"
                        :stroke-width="8" />
                    </div>
                  </div>
                </div>
                <div class="position-actions">
                  <el-button 
                    type="danger" 
                    size="small" 
                    @click="deletePosition(position.positionId)"
                    :disabled="position.recruitedVolunteers > 0"
                  >
                    删除岗位
                  </el-button>
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
              <div v-for="participant in participants" :key="participant.volunteerId" class="participant-card">
                <div class="participant-header">
                  <div class="participant-info">
                    <h3 class="participant-name">{{ participant.volunteerName }}</h3>
                    <span class="participant-position">{{ participant.positionName }}</span>
                  </div>
                  <div class="participant-status">
                    <el-tag 
                      :type="participant.isCheckedIn === '是' ? 'success' : 'warning'"
                      size="small"
                    >
                      {{ participant.isCheckedIn === '是' ? '已签到' : '未签到' }}
                    </el-tag>
                  </div>
                </div>
                <div class="participant-details">
                  <div class="detail-row">
                    <span class="detail-label">联系电话:</span>
                    <span class="detail-value">{{ participant.volunteerPhone || '-' }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="detail-label">邮箱:</span>
                    <span class="detail-value">{{ participant.volunteerEmail || '-' }}</span>
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

          <!-- 操作按钮 -->
          <div class="button-group">
            <el-button @click="goBack" class="back-btn">返回</el-button>
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
        :model="timeslotForm" 
        :rules="timeslotRules" 
        ref="timeslotFormRef"
        label-width="80px"
      >
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="timeslotForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="timeslotForm.endTime"
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
            @click="submitTimeslot"
            :loading="isAddingTimeslot"
          >
            提交
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加岗位对话框 -->
    <el-dialog
      v-model="showAddPositionDialog"
      title="添加岗位"
      width="500px"
      :before-close="handlePositionDialogClose"
    >
      <el-form 
        :model="positionForm" 
        :rules="positionRules" 
        ref="positionFormRef"
        label-width="100px"
      >
        <el-form-item label="岗位名称" prop="positionName">
          <el-input
            v-model="positionForm.positionName"
            placeholder="请输入岗位名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="服务时长" prop="positionServiceHours">
          <el-input-number
            v-model="positionForm.positionServiceHours"
            :min="1"
            :max="999"
            placeholder="请输入服务时长"
            style="width: 100%"
          />
          <span style="margin-left: 8px; color: #909399;">小时</span>
        </el-form-item>

        <el-form-item label="需求人数" prop="requiredVolunteers">
          <el-input-number
            v-model="positionForm.requiredVolunteers"
            :min="1"
            :max="9999"
            placeholder="请输入需求人数"
            style="width: 100%"
          />
          <span style="margin-left: 8px; color: #909399;">人</span>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddPositionDialog = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="submitPosition"
            :loading="isAddingPosition"
          >
            提交
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'
import axios from 'axios'

export default {
  name: 'OrganizationActivityDetail',
  components: {
    InfoFilled
  },
  data() {
    return {
      activity: null,
      timeslots: [],
      positions: [],
      isLoading: false,
      isLoadingTimeslots: false,
      isLoadingPositions: false,
      
      // 参与志愿者相关
      participants: [],
      isLoadingParticipants: false,
      error: null,
      isEditing: false,
      isSaving: false,
      editForm: {},
      
      // 添加时段相关
      showAddTimeslotDialog: false,
      isAddingTimeslot: false,
      timeslotForm: {
        startTime: '',
        endTime: ''
      },
      timeslotRules: {
        startTime: [
          { required: true, message: '请选择开始时间', trigger: 'change' }
        ],
        endTime: [
          { required: true, message: '请选择结束时间', trigger: 'change' }
        ]
      },

      // 添加岗位相关
      showAddPositionDialog: false,
      isAddingPosition: false,
      positionForm: {
        positionName: '',
        positionServiceHours: null,
        requiredVolunteers: null
      },
      positionRules: {
        positionName: [
          { required: true, message: '请输入岗位名称', trigger: 'blur' },
          { min: 1, max: 50, message: '岗位名称长度在 1 到 50 个字符', trigger: 'blur' }
        ],
        positionServiceHours: [
          { required: true, message: '请输入服务时长', trigger: 'blur' },
          { type: 'number', min: 1, max: 999, message: '服务时长必须在 1 到 999 小时之间', trigger: 'blur' }
        ],
        requiredVolunteers: [
          { required: true, message: '请输入需求人数', trigger: 'blur' },
          { type: 'number', min: 1, max: 9999, message: '需求人数必须在 1 到 9999 人之间', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.fetchActivityDetail()
    this.fetchTimeslots()
    this.fetchPositions()
    this.fetchParticipants()
  },
  methods: {
    async fetchActivityDetail() {
      this.isLoading = true
      this.error = null
      try {
        const activityId = this.$route.params.id
        console.log('获取活动详情, ID:', activityId)
        
        const response = await axios.get(`http://localhost:8080/volunteerActivity/${activityId}`)
        console.log('活动详情响应:', response.data)
        
        if (response.data.code === '200') {
          this.activity = response.data.data
          console.log('活动详情设置成功:', this.activity)
        } else {
          this.error = response.data.msg || '获取活动详情失败'
        }
      } catch (error) {
        console.error('获取活动详情出错:', error)
        this.error = '获取活动详情失败: ' + error.message
      } finally {
        this.isLoading = false
      }
    },

    async fetchTimeslots() {
      this.isLoadingTimeslots = true
      try {
        const activityId = this.$route.params.id
        const response = await axios.get(`http://localhost:8080/volunteerActivity/${activityId}/timeslots`)
        
        if (response.data.code === '200') {
          this.timeslots = response.data.data || []
        }
      } catch (error) {
        console.error('获取时段信息出错:', error)
      } finally {
        this.isLoadingTimeslots = false
      }
    },

    async fetchPositions() {
      this.isLoadingPositions = true
      try {
        const activityId = this.$route.params.id
        const response = await axios.get(`http://localhost:8080/volunteerActivity/${activityId}/positions`)
        
        if (response.data.code === '200') {
          this.positions = response.data.data || []
        }
      } catch (error) {
        console.error('获取岗位信息出错:', error)
      } finally {
        this.isLoadingPositions = false
      }
    },

    async fetchParticipants() {
      this.isLoadingParticipants = true
      try {
        const activityId = this.$route.params.id
        const response = await axios.get(`http://localhost:8080/volunteerActivity/${activityId}/participants`)
        
        if (response.data.code === '200') {
          this.participants = response.data.data || []
        }
      } catch (error) {
        console.error('获取参与者信息出错:', error)
      } finally {
        this.isLoadingParticipants = false
      }
    },

    // 开始编辑
    startEdit() {
      this.isEditing = true
      // 复制当前活动数据到编辑表单
      this.editForm = {
        ...this.activity,
        startTime: this.activity.startTime ? new Date(this.activity.startTime).toISOString().slice(0, 19) : null,
        endTime: this.activity.endTime ? new Date(this.activity.endTime).toISOString().slice(0, 19) : null
      }
    },

    // 取消编辑
    cancelEdit() {
      this.isEditing = false
      this.editForm = {}
    },

    // 保存修改
    async saveChanges() {
      this.isSaving = true
      try {
        const activityId = this.$route.params.id
        
        // 准备提交的数据
        const updateData = {
          ...this.editForm,
          activityId: activityId
        }

        const response = await axios.put(`http://localhost:8080/volunteerActivity/${activityId}`, updateData)
        
        if (response.data.code === '200') {
          ElMessage.success('活动信息更新成功！')
          this.isEditing = false
          // 重新获取活动详情
          await this.fetchActivityDetail()
        } else {
          ElMessage.error(response.data.msg || '更新失败')
        }
      } catch (error) {
        console.error('更新活动信息出错:', error)
        ElMessage.error('更新失败: ' + error.message)
      } finally {
        this.isSaving = false
      }
    },

    formatDate(dateString) {
      if (!dateString) return '-'
      try {
        const date = new Date(dateString)
        return date.toLocaleDateString('zh-CN')
      } catch (error) {
        return dateString
      }
    },

    formatDateTime(dateString) {
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
    },

    calculateDuration(startTime, endTime) {
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
    },

    getRecruitmentProgress(position) {
      if (!position.requiredVolunteers || position.requiredVolunteers === 0) return 0
      const progress = Math.round((position.recruitedVolunteers || 0) / position.requiredVolunteers * 100)
      return Math.min(progress, 100)
    },

    getProgressColor(position) {
      const progress = this.getRecruitmentProgress(position)
      if (progress >= 100) return '#67c23a'
      if (progress >= 80) return '#e6a23c'
      if (progress >= 50) return '#409eff'
      return '#f56c6c'
    },

    getStatusTagType(status) {
      const statusMap = {
        '待审核': 'info',
        '审核通过': 'success',
        '进行中': '',
        '已结束': 'info',
        '审核不通过': 'warning',
        '已停用': 'danger'
      }
      return statusMap[status] || ''
    },

    // 时段对话框关闭前的处理
    handleTimeslotDialogClose() {
      this.showAddTimeslotDialog = false
      this.resetTimeslotForm()
    },

    // 重置时段表单
    resetTimeslotForm() {
      this.timeslotForm = {
        startTime: '',
        endTime: ''
      }
      // 清除表单验证
      if (this.$refs.timeslotFormRef) {
        this.$refs.timeslotFormRef.clearValidate()
      }
    },

    // 提交时段
    async submitTimeslot() {
      try {
        // 先验证表单
        const valid = await this.$refs.timeslotFormRef.validate()
        if (!valid) return

        // 检查时间逻辑
        const startTime = new Date(this.timeslotForm.startTime)
        const endTime = new Date(this.timeslotForm.endTime)
        
        if (startTime >= endTime) {
          ElMessage.error('开始时间必须早于结束时间')
          return
        }

        this.isAddingTimeslot = true
        const activityId = this.$route.params.id

        const response = await axios.post(
          `http://localhost:8080/volunteerActivity/${activityId}/timeslots`,
          {
            startTime: this.timeslotForm.startTime,
            endTime: this.timeslotForm.endTime
          }
        )

        if (response.data.code === '200') {
          ElMessage.success('时段添加成功！')
          this.showAddTimeslotDialog = false
          this.resetTimeslotForm()
          // 重新获取时段列表和活动详情（触发器会自动更新时长）
          await this.fetchTimeslots()
          await this.fetchActivityDetail()
        } else {
          ElMessage.error(response.data.msg || '添加时段失败')
        }
      } catch (error) {
        console.error('添加时段出错:', error)
        ElMessage.error('添加时段失败: ' + error.message)
      } finally {
        this.isAddingTimeslot = false
      }
    },

    // 删除时段
    async deleteTimeslot(timeslotId) {
      try {
        await ElMessageBox.confirm(
          '确定要删除这个时段吗？删除后无法恢复。',
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        )

        const activityId = this.$route.params.id
        const response = await axios.delete(
          `http://localhost:8080/volunteerActivity/${activityId}/timeslots/${timeslotId}`
        )

        if (response.data.code === '200') {
          ElMessage.success('时段删除成功！')
          // 重新获取时段列表和活动详情（触发器会自动更新时长）
          await this.fetchTimeslots()
          await this.fetchActivityDetail()
        } else {
          // 显示后端返回的具体错误信息
          const errorMsg = response.data.msg || '删除时段失败'
          ElMessage.error(errorMsg)
        }
      } catch (error) {
        if (error === 'cancel') {
          // 用户取消删除
          return
        }
        
        console.log('完整错误对象:', error)
        
        // 处理HTTP错误响应
        if (error.response) {
          console.log('错误响应状态:', error.response.status)
          console.log('错误响应数据:', error.response.data)
          
          if (error.response.data && error.response.data.msg) {
            ElMessage.error(error.response.data.msg)
          } else if (error.response.data && error.response.data.code) {
            ElMessage.error(`删除失败 (${error.response.data.code})`)
          } else {
            ElMessage.error('删除时段失败')
          }
        } else {
          console.error('删除时段出错:', error)
          ElMessage.error('删除时段失败: ' + error.message)
        }
      }
    },

    // 岗位对话框关闭前的处理
    handlePositionDialogClose() {
      this.showAddPositionDialog = false
      this.resetPositionForm()
    },

    // 重置岗位表单
    resetPositionForm() {
      this.positionForm = {
        positionName: '',
        positionServiceHours: null,
        requiredVolunteers: null
      }
      // 清除表单验证
      if (this.$refs.positionFormRef) {
        this.$refs.positionFormRef.clearValidate()
      }
    },

    // 提交岗位
    async submitPosition() {
      try {
        // 先验证表单
        const valid = await this.$refs.positionFormRef.validate()
        if (!valid) return

        this.isAddingPosition = true
        const activityId = this.$route.params.id

        const response = await axios.post(
          `http://localhost:8080/volunteerActivity/${activityId}/positions`,
          {
            positionName: this.positionForm.positionName,
            positionServiceHours: this.positionForm.positionServiceHours,
            requiredVolunteers: this.positionForm.requiredVolunteers
          }
        )

        if (response.data.code === '200') {
          ElMessage.success('岗位添加成功！')
          this.showAddPositionDialog = false
          this.resetPositionForm()
          // 重新获取岗位列表和活动详情（触发器会自动更新招募人数）
          await this.fetchPositions()
          await this.fetchActivityDetail()
        } else {
          ElMessage.error(response.data.msg || '添加岗位失败')
        }
      } catch (error) {
        console.error('添加岗位出错:', error)
        
        // 处理HTTP错误响应
        if (error.response) {
          console.log('错误响应状态:', error.response.status)
          console.log('错误响应数据:', error.response.data)
          
          if (error.response.data && error.response.data.msg) {
            ElMessage.error(error.response.data.msg)
          } else if (error.response.data && error.response.data.code) {
            ElMessage.error(`添加失败 (${error.response.data.code})`)
          } else {
            ElMessage.error('添加岗位失败')
          }
        } else {
          ElMessage.error('添加岗位失败: ' + error.message)
        }
      } finally {
        this.isAddingPosition = false
      }
    },

    // 删除岗位
    async deletePosition(positionId) {
      try {
        await ElMessageBox.confirm(
          '确定要删除这个岗位吗？删除后无法恢复。',
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        )

        const activityId = this.$route.params.id
        const response = await axios.delete(
          `http://localhost:8080/volunteerActivity/${activityId}/positions/${positionId}`
        )

        if (response.data.code === '200') {
          ElMessage.success('岗位删除成功！')
          // 重新获取岗位列表和活动详情（触发器会自动更新招募人数）
          await this.fetchPositions()
          await this.fetchActivityDetail()
        } else {
          // 显示后端返回的具体错误信息
          const errorMsg = response.data.msg || '删除岗位失败'
          ElMessage.error(errorMsg)
        }
      } catch (error) {
        if (error === 'cancel') {
          // 用户取消删除
          return
        }
        
        console.log('完整错误对象:', error)
        
        // 处理HTTP错误响应
        if (error.response) {
          console.log('错误响应状态:', error.response.status)
          console.log('错误响应数据:', error.response.data)
          
          if (error.response.data && error.response.data.msg) {
            ElMessage.error(error.response.data.msg)
          } else if (error.response.data && error.response.data.code) {
            ElMessage.error(`删除失败 (${error.response.data.code})`)
          } else {
            ElMessage.error('删除岗位失败')
          }
        } else {
          console.error('删除岗位出错:', error)
          ElMessage.error('删除岗位失败: ' + error.message)
        }
      }
    },

    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
/* 基础容器样式 */
.activity-detail-container {
  min-height: 100vh;
  background-image: url('@/../public/bg.png');
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center;
  padding: 0;
  position: relative;
}

.activity-detail-container::before {
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
  font-size: 14px;
  padding: 6px 12px;
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

.add-btn {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.4);
  color: white;
  border-radius: 6px;
  font-size: 12px;
  padding: 8px 16px;
}

.add-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.6);
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

.timeslot-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.timeslot-actions {
  margin-left: auto;
  padding-left: 15px;
}

.delete-timeslot-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ff4757;
  border: none;
  color: white;
  transition: all 0.3s ease;
}

.delete-timeslot-btn:hover {
  background: #ff3742 !important;
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(255, 71, 87, 0.4);
}

.timeslot-number {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #ff3333, #ff6666);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  margin-right: 20px;
  flex-shrink: 0;
}

.timeslot-content {
  flex: 1;
}

.timeslot-time {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 8px;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.separator {
  color: #999;
  font-weight: normal;
}

.timeslot-duration {
  color: #666;
  font-size: 14px;
}

/* 岗位容器 */
.positions-container {
  padding: 30px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(450px, 1fr));
  gap: 25px;
}

.position-card {
  padding: 25px;
  background: #f8f9fa;
  border-radius: 12px;
  border-left: 4px solid #ff3333;
  transition: all 0.3s ease;
}

.position-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.position-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.position-name {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.position-stats {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 16px;
  font-weight: 500;
}

.recruited {
  color: #ff3333;
  font-weight: 600;
}

.required {
  color: #666;
}

.position-details {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.detail-row {
  display: flex;
  align-items: center;
  gap: 15px;
}

.detail-label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  min-width: 80px;
}

.detail-value {
  color: #666;
  font-size: 14px;
}

.progress-wrapper {
  flex: 1;
}

.position-actions {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}

/* 参与志愿者容器 */
.participants-container {
  padding: 30px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(450px, 1fr));
  gap: 25px;
}

.participant-card {
  padding: 25px;
  background: #f8f9fa;
  border-radius: 12px;
  border-left: 4px solid #28a745;
  transition: all 0.3s ease;
}

.participant-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.participant-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.participant-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.participant-name {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.participant-position {
  background: #e9ecef;
  color: #6c757d;
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 500;
  align-self: flex-start;
}

.participant-status {
  display: flex;
  align-items: center;
}

.participant-details {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.rating-display {
  display: flex;
  align-items: center;
}

/* 空状态 */
.empty-section {
  text-align: center;
  padding: 3rem;
  color: #999;
  font-size: 16px;
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
  
  .main-card {
    max-width: 95%;
    min-height: auto;
  }
  
  .main-card :deep(.el-card__body) {
    padding: 20px;
    min-height: auto;
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
  
  .timeslots-container,
  .positions-container,
  .participants-container {
    grid-template-columns: 1fr;
    padding: 20px;
  }
  
  .timeslot-card {
    flex-direction: column;
    text-align: center;
    gap: 15px;
  }
  
  .timeslot-number {
    margin-right: 0;
  }
  
  .position-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .detail-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .detail-label {
    min-width: auto;
  }
  
  .participant-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .participant-info {
    align-items: flex-start;
  }
}
</style> 