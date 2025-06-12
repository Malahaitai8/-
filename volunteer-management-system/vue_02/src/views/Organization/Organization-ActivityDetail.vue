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
          <el-button @click="fetchAllData" type="primary">重试</el-button>
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
              <div class="info-item">
                <label class="info-label">活动名称</label>
                <div v-if="!isEditing" class="info-value">{{ activity.activityName || '-' }}</div>
                <el-input v-else v-model="editForm.activityName" class="edit-input" />
              </div>
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
              <div class="info-item">
                <label class="info-label">活动地点</label>
                <div v-if="!isEditing" class="info-value">{{ activity.location || '-' }}</div>
                <el-input v-else v-model="editForm.location" class="edit-input" />
              </div>
              <div class="info-item">
                <label class="info-label">创建时间</label>
                <div class="info-value">{{ formatDate(activity.creationTime) || '-' }}</div>
              </div>
              <div class="info-item">
                <label class="info-label">招募人数</label>
                <div class="info-value">{{ activity.recruitmentCount || '-' }}</div>
              </div>
              <div class="info-item">
                <label class="info-label">录取人数</label>
                <div class="info-value">{{ activity.acceptedCount || '-' }}</div>
              </div>
              <div class="info-item">
                <label class="info-label">负责人联系电话</label>
                <div v-if="!isEditing" class="info-value">{{ activity.contactPersonPhone || '-' }}</div>
                <el-input v-else v-model="editForm.contactPersonPhone" class="edit-input" />
              </div>
              <div class="info-item">
                <label class="info-label">审核管理员ID</label>
                <div class="info-value">{{ activity.reviewerAdminId || '-' }}</div>
              </div>
              <div class="info-item">
                <label class="info-label">活动评分</label>
                 <div class="info-value">
                   <el-rate
                       v-if="activity.activityRating"
                       :model-value="activity.activityRating"
                       disabled
                       show-score
                       text-color="#ff9900"
                       score-template="{value} 分"
                   />
                   <span v-else>-</span>
                 </div>
              </div>
              <div class="info-item">
                <label class="info-label">活动时长</label>
                <div class="info-value">{{ activity.activityDurationHours || 0 }} 小时</div>
              </div>
              <div class="info-item">
                <label class="info-label">开始时间</label>
                <div class="info-value">{{ formatDateTime(activity.startTime) || '-' }}</div>
              </div>
              <div class="info-item">
                <label class="info-label">结束时间</label>
                <div class="info-value">{{ formatDateTime(activity.endTime) || '-' }}</div>
              </div>
            </div>
          </div>

          <!-- 时间安排 -->
          <div class="info-section">
            <div class="section-header">
              <h2>时间安排</h2>
              <div class="header-actions">
                <el-tag class="count-tag">共 {{ timeslots.length }} 个时段</el-tag>
                <el-button type="primary" size="small" @click="showAddTimeslotDialog = true" class="add-timeslot-btn">添加时段</el-button>
              </div>
            </div>
            <div v-if="isLoadingTimeslots" class="loading-section mini"><div class="loading-spinner small"></div><p>加载时段信息中...</p></div>
            <div v-else-if="timeslots.length === 0" class="empty-section"><p>暂无时段信息</p></div>
            <div v-else class="timeslots-container">
              <div v-for="(slot, index) in timeslots" :key="slot.timeslotId" class="timeslot-card">
                <div class="timeslot-number">{{ index + 1 }}</div>
                <div class="timeslot-content">
                  <div class="timeslot-time"><span class="start-time">{{ formatDateTime(slot.startTime) }}</span><span class="separator">至</span><span class="end-time">{{ formatDateTime(slot.endTime) }}</span></div>
                  <div class="timeslot-duration">持续: {{ calculateDuration(slot.startTime, slot.endTime) }}</div>
                </div>
                <div class="timeslot-actions"><el-button type="danger" size="small" @click="deleteTimeslot(slot.timeslotId)" icon="el-icon-delete" class="delete-timeslot-btn" title="删除时段" /></div>
              </div>
            </div>
          </div>

          <!-- 岗位信息 -->
          <div class="info-section">
            <div class="section-header">
              <h2>岗位信息</h2>
              <div class="header-right"><el-tag class="count-tag">共 {{ positions.length }} 个岗位</el-tag><el-button type="primary" size="small" @click="showAddPositionDialog = true" class="add-btn">新增岗位</el-button></div>
            </div>
            <div v-if="isLoadingPositions" class="loading-section mini"><div class="loading-spinner small"></div><p>加载岗位信息中...</p></div>
            <div v-else-if="positions.length === 0" class="empty-section"><p>暂无岗位信息</p></div>
            <div v-else class="positions-container">
              <div v-for="position in positions" :key="position.positionId" class="position-card">
                <div class="position-header"><h3 class="position-name">{{ position.positionName }}</h3><div class="position-stats"><span class="recruited">{{ position.recruitedVolunteers || 0 }}</span><span>/</span><span class="required">{{ position.requiredVolunteers || 0 }}</span></div></div>
                <div class="position-details"><div class="detail-row"><span class="detail-label">服务时长:</span><span class="detail-value">{{ position.positionServiceHours || 0 }} 小时</span></div><div class="detail-row"><span class="detail-label">招募进度:</span><div class="progress-wrapper"><el-progress :percentage="getRecruitmentProgress(position)" :color="getProgressColor(position)" :stroke-width="8" /></div></div></div>
                <div class="position-actions"><el-button type="danger" size="small" @click="deletePosition(position.positionId)" :disabled="position.recruitedVolunteers > 0">删除岗位</el-button></div>
              </div>
            </div>
          </div>

          <!-- 参与志愿者 -->
          <div class="info-section">
            <div class="section-header"><h2>参与志愿者</h2><div class="header-right"><el-tag class="count-tag">共 {{ participants.length }} 名志愿者</el-tag></div></div>
            <div v-if="isLoadingParticipants" class="loading-section mini"><div class="loading-spinner small"></div><p>加载参与者信息中...</p></div>
            <div v-else-if="participants.length === 0" class="empty-section"><p>暂无参与者信息</p></div>
            <div v-else class="participants-container">
              <div v-for="participant in participants" :key="participant.volunteerId" class="participant-card">
                <div class="participant-header"><div class="participant-info"><h3 class="participant-name">{{ participant.volunteerName }}</h3><span class="participant-position">{{ participant.positionName }}</span></div><div class="participant-status"><el-switch v-model="participant.isCheckedIn" active-value="是" inactive-value="否" @change="updateCheckInStatus(participant)" inline-prompt style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949" active-text="已签到" inactive-text="未签到"/></div></div>
                <div class="participant-details">
                  <div class="detail-row"><span class="detail-label">联系电话:</span><span class="detail-value">{{ participant.volunteerPhone || '-' }}</span></div>
                  <div class="detail-row"><span class="detail-label">邮箱:</span><span class="detail-value">{{ participant.volunteerEmail || '-' }}</span></div>
                  <div class="detail-row" v-if="participant.orgToVolunteerRating"><span class="detail-label">组织评分:</span><div class="rating-display"><el-rate :model-value="participant.orgToVolunteerRating" disabled show-score text-color="#ff9900" size="small"/></div></div>
                  <div class="detail-row" v-if="participant.volunteerToOrgRating"><span class="detail-label">志愿者评分:</span><div class="rating-display"><el-rate :model-value="participant.volunteerToOrgRating" disabled show-score text-color="#ff9900" size="small"/></div></div>
                </div>
                <div class="participant-actions"><el-button type="primary" size="small" class="rate-btn" :disabled="isRatingDisabled(participant)" :title="getRatingTooltip(participant)" @click="openRateDialog(participant)">{{ participant.orgToVolunteerRating ? '已评价' : '评价' }}</el-button></div>
              </div>
            </div>
          </div>

          <div class="button-group"><el-button @click="goBack" class="back-btn">返回</el-button></div>
        </div>
      </el-card>
    </div>

    <!-- 添加时段对话框 -->
    <el-dialog v-model="showAddTimeslotDialog" title="添加时段" width="500px" :before-close="handleTimeslotDialogClose">
      <el-form :model="timeslotForm" :rules="timeslotRules" ref="timeslotFormRef" label-width="80px">
        <el-form-item label="开始时间" prop="startTime"><el-date-picker v-model="timeslotForm.startTime" type="datetime" placeholder="选择开始时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%"/></el-form-item>
        <el-form-item label="结束时间" prop="endTime"><el-date-picker v-model="timeslotForm.endTime" type="datetime" placeholder="选择结束时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%"/></el-form-item>
      </el-form>
      <template #footer><span class="dialog-footer"><el-button @click="showAddTimeslotDialog = false">取消</el-button><el-button type="primary" @click="submitTimeslot" :loading="isAddingTimeslot">提交</el-button></span></template>
    </el-dialog>

    <!-- 添加岗位对话框 -->
    <el-dialog v-model="showAddPositionDialog" title="添加岗位" width="500px" :before-close="handlePositionDialogClose">
      <el-form :model="positionForm" :rules="positionRules" ref="positionFormRef" label-width="100px">
        <el-form-item label="岗位名称" prop="positionName"><el-input v-model="positionForm.positionName" placeholder="请输入岗位名称" maxlength="50" show-word-limit/></el-form-item>
        <el-form-item label="服务时长" prop="positionServiceHours"><el-input-number v-model="positionForm.positionServiceHours" :min="1" :max="999" placeholder="请输入服务时长" style="width: 100%"/><span style="margin-left: 8px; color: #909399;">小时</span></el-form-item>
        <el-form-item label="需求人数" prop="requiredVolunteers"><el-input-number v-model="positionForm.requiredVolunteers" :min="1" :max="9999" placeholder="请输入需求人数" style="width: 100%"/><span style="margin-left: 8px; color: #909399;">人</span></el-form-item>
      </el-form>
      <template #footer><span class="dialog-footer"><el-button @click="showAddPositionDialog = false">取消</el-button><el-button type="primary" @click="submitPosition" :loading="isAddingPosition">提交</el-button></span></template>
    </el-dialog>

    <!-- 评价志愿者对话框 -->
    <el-dialog v-model="rateDialog.visible" title="评价志愿者表现" width="400px" @close="resetRateDialog">
      <div class="rate-dialog-content"><p>正在为志愿者 <strong>{{ rateDialog.participantName }}</strong> 评分</p><el-rate v-model="rateDialog.score" :max="10" show-score score-template="{value} 分" size="large"/></div>
      <template #footer><span class="dialog-footer"><el-button @click="rateDialog.visible = false">取消</el-button><el-button type="primary" @click="submitRating" :loading="rateDialog.isSubmitting">提交评价</el-button></span></template>
    </el-dialog>
  </div>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus';
import { InfoFilled } from '@element-plus/icons-vue';
import request from '@/utils/request.js';

export default {
  name: 'OrganizationActivityDetail',
  components: { InfoFilled },
  data() {
    return {
      activity: null,
      timeslots: [],
      positions: [],
      participants: [],
      isLoading: true,
      isLoadingTimeslots: false,
      isLoadingPositions: false,
      isLoadingParticipants: false,
      error: null,
      isEditing: false,
      isSaving: false,
      editForm: {},
      rateDialog: {
        visible: false,
        isSubmitting: false,
        score: 0,
        participantId: null,
        participantName: '',
      },
      showAddTimeslotDialog: false,
      isAddingTimeslot: false,
      timeslotForm: { startTime: '', endTime: '' },
      timeslotRules: {
        startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
        endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
      },
      showAddPositionDialog: false,
      isAddingPosition: false,
      positionForm: {
        positionName: '',
        positionServiceHours: null,
        requiredVolunteers: null,
      },
      positionRules: {
        positionName: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }],
        positionServiceHours: [{ type: 'number', required: true, message: '服务时长必须是数字', trigger: 'blur' }],
        requiredVolunteers: [{ type: 'number', required: true, message: '需求人数必须是数字', trigger: 'blur' }],
      },
    };
  },
  created() {
    this.fetchAllData();
  },
  methods: {
    async fetchAllData() {
      this.isLoading = true;
      this.error = null;
      try {
        await Promise.all([
          this.fetchActivityDetail(),
          this.fetchTimeslots(),
          this.fetchPositions(),
          this.fetchParticipants(),
        ]);
      } catch (error) {
        this.error = '加载页面数据失败，请刷新重试。';
        console.error('Data fetching error:', error);
      } finally {
        this.isLoading = false;
      }
    },
    async fetchActivityDetail() {
      const activityId = this.$route.params.id;
      const response = await request.get(`/volunteerActivity/${activityId}`);
      if (response.code === '200') {
        this.activity = response.data;
      } else {
        throw new Error(response.msg || '获取活动详情失败');
      }
    },
    async fetchTimeslots() {
      this.isLoadingTimeslots = true;
      try {
        const activityId = this.$route.params.id;
        const response = await request.get(`/volunteerActivity/${activityId}/timeslots`);
        this.timeslots = response.data || [];
      } finally {
        this.isLoadingTimeslots = false;
      }
    },
    async fetchPositions() {
      this.isLoadingPositions = true;
      try {
        const activityId = this.$route.params.id;
        const response = await request.get(`/volunteerActivity/${activityId}/positions`);
        this.positions = response.data || [];
      } finally {
        this.isLoadingPositions = false;
      }
    },
    async fetchParticipants() {
      this.isLoadingParticipants = true;
      try {
        const activityId = this.$route.params.id;
        const response = await request.get(`/volunteerActivity/${activityId}/participants`);
        this.participants = (response.data || []).map(p => ({ ...p, isCheckedIn: p.isCheckedIn === '是' ? '是' : '否' }));
      } finally {
        this.isLoadingParticipants = false;
      }
    },
    startEdit() {
      this.isEditing = true;
      this.editForm = {
        activityName: this.activity.activityName,
        activityStatus: this.activity.activityStatus,
        location: this.activity.location,
        contactPersonPhone: this.activity.contactPersonPhone,
      };
    },
    cancelEdit() {
      this.isEditing = false;
      this.editForm = {};
    },
    async saveChanges() {
      this.isSaving = true;
      try {
        const payload = { ...this.activity, ...this.editForm };
        const response = await request.put(`/volunteerActivity/update`, payload);
        if (response.code === '200') {
          ElMessage.success('活动信息更新成功！');
          this.isEditing = false;
          await this.fetchActivityDetail();
        } else {
          ElMessage.error(response.msg || '更新失败');
        }
      } catch (error) {
        ElMessage.error('更新请求失败');
      } finally {
        this.isSaving = false;
      }
    },
    async updateCheckInStatus(participant) {
      const originalStatus = participant.isCheckedIn === '是' ? '否' : '是';
      try {
        await request.put('/volunteerActivity/participation/check-in', {
          activityId: this.activity.activityId,
          volunteerId: participant.volunteerId,
          isCheckedIn: participant.isCheckedIn,
        });
        ElMessage.success('签到状态更新成功！');
      } catch (err) {
        participant.isCheckedIn = originalStatus;
        ElMessage.error('更新签到状态失败');
      }
    },
    openRateDialog(participant) {
      this.rateDialog = {
        visible: true,
        isSubmitting: false,
        score: participant.orgToVolunteerRating || 0,
        participantId: participant.volunteerId,
        participantName: participant.volunteerName,
      };
    },
    resetRateDialog() {
      this.rateDialog.visible = false;
    },
    async submitRating() {
      if (this.rateDialog.score === 0) {
        ElMessage.warning('请选择评分');
        return;
      }
      this.rateDialog.isSubmitting = true;
      try {
        await request.put('/volunteerActivity/rate-participant', {
          activityId: this.activity.activityId,
          volunteerId: this.rateDialog.participantId,
          rating: this.rateDialog.score,
        });
        ElMessage.success('评价成功！');
        this.resetRateDialog();
        await this.fetchParticipants();
      } catch (err) {
        ElMessage.error('评价失败');
      } finally {
        this.rateDialog.isSubmitting = false;
      }
    },
    isRatingDisabled(participant) {
      if (!this.activity || this.activity.activityStatus !== '已结束') return true;
      if (participant.orgToVolunteerRating) return true;
      const endTime = new Date(this.activity.endTime);
      const now = new Date();
      const sevenDaysAfter = new Date(endTime.getTime() + 7 * 24 * 60 * 60 * 1000);
      return now < endTime || now > sevenDaysAfter;
    },
    getRatingTooltip(participant) {
      if (!this.activity) return '';
      if (this.activity.activityStatus !== '已结束') return '活动结束后方可评价';
      if (participant.orgToVolunteerRating) return `已评分为: ${participant.orgToVolunteerRating}分`;
      const endTime = new Date(this.activity.endTime);
      const now = new Date();
      const sevenDaysAfter = new Date(endTime.getTime() + 7 * 24 * 60 * 60 * 1000);
      if (now < endTime) return '活动结束后方可评价';
      if (now > sevenDaysAfter) return '已超过7天评价期限';
      return '评价该志愿者的表现';
    },
    handleTimeslotDialogClose() { this.showAddTimeslotDialog = false; this.resetTimeslotForm(); },
    resetTimeslotForm() { this.timeslotForm = { startTime: '', endTime: '' }; this.$refs.timeslotFormRef?.clearValidate(); },
    async submitTimeslot() {
      this.$refs.timeslotFormRef.validate(async (valid) => {
        if (!valid) return;
        this.isAddingTimeslot = true;
        try {
          await request.post(`/volunteerActivity/${this.activity.activityId}/timeslots`, this.timeslotForm);
          ElMessage.success('时段添加成功！');
          this.showAddTimeslotDialog = false;
          await this.fetchAllData();
        } catch (error) {
          ElMessage.error('添加失败');
        } finally {
          this.isAddingTimeslot = false;
        }
      });
    },
    async deleteTimeslot(timeslotId) {
        try {
            await ElMessageBox.confirm('确定要删除这个时段吗？', '确认删除', { type: 'warning' });
            await request.delete(`/volunteerActivity/${this.activity.activityId}/timeslots/${timeslotId}`);
            ElMessage.success('时段删除成功！');
            await this.fetchAllData();
        } catch(e) {
            if (e !== 'cancel') {
                ElMessage.error('删除失败');
            }
        }
    },
    handlePositionDialogClose() { this.showAddPositionDialog = false; this.resetPositionForm(); },
    resetPositionForm() { this.positionForm = { positionName: '', positionServiceHours: null, requiredVolunteers: null }; this.$refs.positionFormRef?.clearValidate(); },
    async submitPosition() {
      this.$refs.positionFormRef.validate(async (valid) => {
        if (!valid) return;
        this.isAddingPosition = true;
        try {
          await request.post(`/volunteerActivity/${this.activity.activityId}/positions`, this.positionForm);
          ElMessage.success('岗位添加成功！');
          this.showAddPositionDialog = false;
          await this.fetchAllData();
        } catch (error) {
          ElMessage.error('添加失败');
        } finally {
          this.isAddingPosition = false;
        }
      });
    },
    async deletePosition(positionId) {
        try {
            await ElMessageBox.confirm('确定要删除这个岗位吗？', '确认删除', { type: 'warning' });
            await request.delete(`/volunteerActivity/${this.activity.activityId}/positions/${positionId}`);
            ElMessage.success('岗位删除成功！');
            await this.fetchAllData();
        } catch(e) {
            if (e !== 'cancel') {
                ElMessage.error('删除失败');
            }
        }
    },
    formatDate: (d) => (d ? new Date(d).toLocaleDateString('zh-CN') : '-'),
    formatDateTime: (d) => (d ? new Date(d).toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' }) : '-'),
    calculateDuration: (s, e) => {
      if (!s || !e) return '-';
      const diff = new Date(e) - new Date(s);
      return `${Math.floor(diff/36e5)}小时${Math.floor((diff%36e5)/6e4)}分钟`;
    },
    getRecruitmentProgress: (p) => (p.requiredVolunteers ? Math.min(Math.round((p.recruitedVolunteers||0)/p.requiredVolunteers*100),100) : 0),
    getProgressColor(p){
      const prog = this.getRecruitmentProgress(p);
      if(prog>=100) return '#67c23a'; if(prog>=80) return '#e6a23c'; return '#409eff';
    },
    getStatusTagType: (s) => ({'待审核':'info','审核通过':'success','进行中':'','已结束':'info','审核不通过':'warning','已停用':'danger'}[s]||''),
    goBack() { this.$router.go(-1); },
  },
};
</script>

<style scoped>
.activity-detail-container { min-height: 100vh; background-image: url('@/../public/bg.png'); background-repeat: no-repeat; background-size: cover; background-position: center; padding: 0; position: relative; }
.activity-detail-container::before { content: ''; position: absolute; top: 0; left: 0; right: 0; bottom: 0; background: rgba(255, 51, 51, 0.1); z-index: 1; }
.header { background-color: #ff3333; color: white; padding: 20px; text-align: center; box-shadow: 0 2px 10px rgba(255, 51, 51, 0.3); margin-bottom: 0; position: relative; z-index: 2; }
.header h1 { margin: 0; font-size: 28px; font-weight: 600; }
.content-wrapper { display: flex; justify-content: center; align-items: flex-start; padding: 30px 20px; min-height: calc(100vh - 80px); position: relative; z-index: 2; }
.main-card { width: 100%; max-width: 1800px; min-height: 700px; border-radius: 12px; box-shadow: 0 8px 25px rgba(255, 51, 51, 0.2); border: 1px solid rgba(255, 51, 51, 0.1); overflow: hidden; background: rgba(255, 255, 255, 0.95); backdrop-filter: blur(10px); }
.main-card :deep(.el-card__body) { padding: 40px; min-height: 600px; }
.loading-section, .error-section { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 4rem 2rem; text-align: center; }
.loading-section.mini { padding: 2rem; min-height: 120px; }
.loading-spinner { width: 40px; height: 40px; border: 4px solid #f3f3f3; border-top: 4px solid #ff3333; border-radius: 50%; animation: spin 1s linear infinite; margin-bottom: 1rem; }
.loading-spinner.small { width: 24px; height: 24px; border-width: 3px; }
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
.error-icon { font-size: 3rem; margin-bottom: 1rem; }
.info-section { background: white; border-radius: 12px; margin-bottom: 30px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1); overflow: hidden; }
.section-header { background: linear-gradient(135deg, #ff3333, #ff6666); color: white; padding: 20px 30px; display: flex; justify-content: space-between; align-items: center; }
.section-header h2 { margin: 0; font-size: 20px; font-weight: 600; }
.header-actions, .header-controls, .header-right { display: flex; align-items: center; gap: 15px; }
.add-timeslot-btn, .add-btn { background: rgba(255, 255, 255, 0.2); border: 1px solid rgba(255, 255, 255, 0.4); color: white; border-radius: 6px; font-size: 12px; padding: 8px 16px; }
.add-timeslot-btn:hover, .add-btn:hover { background: rgba(255, 255, 255, 0.3); border-color: rgba(255, 255, 255, 0.6); }
.status-tag { font-size: 14px; padding: 6px 12px; }
.count-tag { background: rgba(255, 255, 255, 0.2); color: white; border: none; font-size: 14px; padding: 6px 12px; }
.edit-btn { background: rgba(255, 255, 255, 0.2); border: 1px solid rgba(255, 255, 255, 0.4); color: white; }
.edit-btn:hover { background: rgba(255, 255, 255, 0.3); border-color: rgba(255, 255, 255, 0.6); }
.edit-controls { display: flex; gap: 10px; }
.info-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 25px; padding: 30px; }
.info-item { display: flex; flex-direction: column; gap: 8px; }
.info-label { font-size: 14px; font-weight: 600; color: #333; margin-bottom: 5px; }
.info-value { padding: 12px 15px; background: #f8f9fa; border-radius: 8px; border-left: 4px solid #ff3333; font-size: 14px; color: #666; min-height: 20px; }
.timeslots-container, .positions-container, .participants-container { padding: 30px; display: grid; grid-template-columns: repeat(auto-fill, minmax(450px, 1fr)); gap: 25px; }
.timeslot-card, .position-card, .participant-card { background: #f8f9fa; border-radius: 12px; transition: all 0.3s ease; }
.timeslot-card:hover, .position-card:hover, .participant-card:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1); }
.timeslot-card { display: flex; align-items: center; padding: 20px; border-left: 4px solid #ff3333; }
.timeslot-number { width: 50px; height: 50px; background: linear-gradient(135deg, #ff3333, #ff6666); color: white; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 600; margin-right: 20px; flex-shrink: 0; }
.timeslot-content { flex: 1; }
.timeslot-time { display: flex; align-items: center; gap: 15px; margin-bottom: 8px; font-size: 16px; font-weight: 500; }
.separator { color: #999; }
.timeslot-duration { color: #666; font-size: 14px; }
.timeslot-actions { margin-left: auto; padding-left: 15px; }
.delete-timeslot-btn { width: 36px; height: 36px; border-radius: 50%; padding: 0; display: flex; align-items: center; justify-content: center; background: #ff4757; border: none; color: white; transition: all 0.3s ease; }
.delete-timeslot-btn:hover { background: #ff3742 !important; transform: scale(1.1); }
.position-card { padding: 25px; border-left: 4px solid #ff3333; }
.position-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.position-name { margin: 0; font-size: 18px; font-weight: 600; }
.position-stats { display: flex; align-items: center; gap: 5px; font-size: 16px; font-weight: 500; }
.recruited { color: #ff3333; }
.position-details { display: flex; flex-direction: column; gap: 15px; }
.detail-row { display: flex; align-items: center; gap: 15px; }
.detail-label { font-size: 14px; font-weight: 600; color: #333; min-width: 80px; }
.detail-value { color: #666; font-size: 14px; }
.progress-wrapper { flex: 1; }
.position-actions { margin-top: 15px; display: flex; justify-content: flex-end; }
.participant-card { padding: 25px; border-left: 4px solid #28a745; display: flex; flex-direction: column; }
.participant-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.participant-info { display: flex; flex-direction: column; gap: 8px; }
.participant-name { margin: 0; font-size: 18px; font-weight: 600; }
.participant-position { background: #e9ecef; color: #6c757d; padding: 4px 12px; border-radius: 15px; font-size: 12px; font-weight: 500; align-self: flex-start; }
.participant-details { flex-grow: 1; }
.participant-actions { padding: 15px 0 0; border-top: 1px solid #eee; text-align: right; margin-top: 20px; }
.rate-btn:disabled { cursor: not-allowed; }
.rate-dialog-content { text-align: center; padding: 20px 0; }
.rate-dialog-content p { margin-bottom: 20px; font-size: 16px; }
.empty-section { text-align: center; padding: 3rem; color: #999; font-size: 16px; }
.button-group { display: flex; justify-content: center; padding-top: 20px; border-top: 1px solid #f0f0f0; }
.back-btn { background: #f8f9fa; border: 2px solid #dee2e6; color: #6c757d; padding: 12px 30px; border-radius: 25px; font-size: 16px; font-weight: 500; min-width: 120px; transition: all 0.3s ease; }
.back-btn:hover { background: #e9ecef; border-color: #adb5bd; color: #495057; }
</style>