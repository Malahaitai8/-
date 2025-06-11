<template>
  <div class="projects-more-page">
    <div class="projects-more-title">参加更多项目</div>
    <div style="margin-bottom: 20px; display: flex; align-items: center;">
      <el-input v-model="searchText" placeholder="请输入项目名称" style="width: 240px; margin-right: 12px;" clearable @keyup.enter="handleSearch" />
      <el-button type="primary" @click="handleSearch" :loading="isLoading">搜索</el-button>
    </div>

    <el-row :gutter="24" v-loading="isLoading">
      <el-col v-for="item in pageData" :key="item.activityId" :span="6" class="project-card-col">
        <el-card class="project-card">
          <div class="project-img-wrap">
            <img :src="imageSrc" class="project-img" alt="Project Image"/>
            <span class="project-status" v-if="item.activityStatus === '进行中'">进行中</span>
          </div>
          <div class="project-name">{{ item.activityName }}</div>
          <div class="project-info-row">
            <div class="recruit-label">招募人数 {{ item.recruitmentCount }}</div>
          </div>
          <div class="project-info-row">
            <div class="admitted-label">已录取人数 {{ item.acceptedCount }}</div>
          </div>
          <div class="project-action-row">
            <el-button type="primary" plain size="small" @click="openDetailDialog(item.activityId)">查看详情</el-button>
            <el-button type="primary" size="small" @click="openJoinDialog(item)">参与项目</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <div v-if="!activities.length && !isLoading" class="empty-state">
        <p>暂无开放报名的活动</p>
    </div>

    <el-pagination
      v-if="activities.length > pageSize"
      style="margin-top: 32px; justify-content: center;"
      background layout="prev, pager, next, jumper"
      :total="activities.length" :page-size="pageSize" v-model:current-page="currentPage"
    />

    <el-dialog title="项目报名" v-model="joinDialog.visible" width="400px" @close="resetJoinDialog">
      <el-form label-width="80px">
        <el-form-item label="报名活动"><el-input :value="joinDialog.activityName" disabled /></el-form-item>
        <el-form-item label="意向岗位" required>
          <el-select v-model="joinDialog.intendedPositionId" placeholder="请选择意向岗位" style="width: 100%;" v-loading="joinDialog.isLoadingPositions">
            <el-option v-for="pos in joinDialog.positions" :key="pos.positionId" :label="pos.positionName" :value="pos.positionId"/>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="joinDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitJoin" :loading="joinDialog.isSubmitting">提交报名</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialog.visible" title="活动详细信息" width="600px">
      <el-descriptions v-if="detailDialog.data" :column="2" border>
        <el-descriptions-item label="活动名称">{{ detailDialog.data.activityName }}</el-descriptions-item>
        <el-descriptions-item label="活动状态"><el-tag size="small">{{ detailDialog.data.activityStatus }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ detailDialog.data.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ detailDialog.data.endTime }}</el-descriptions-item>
        <el-descriptions-item label="活动地点" :span="2">{{ detailDialog.data.location }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailDialog.data.contactPersonPhone }}</el-descriptions-item>
      </el-descriptions>
      <div v-else v-loading="detailDialog.isLoading" style="min-height: 150px;"></div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/stores/userStore';
import request from '@/utils/request';
import imageSrc from '@/assets/image.jpeg';

const userStore = useUserStore();
const activities = ref([]);
const isLoading = ref(false);
const pageSize = 8;
const currentPage = ref(1);
const searchText = ref('');

const joinDialog = reactive({ visible: false, isLoadingPositions: false, isSubmitting: false, activityId: null, activityName: '', intendedPositionId: '', positions: [] });
const detailDialog = reactive({ visible: false, isLoading: false, data: null });

const pageData = computed(() => {
    const start = (currentPage.value - 1) * pageSize;
    return activities.value.slice(start, start + pageSize);
});

const fetchActivities = async () => {
    const volunteerId = userStore.detailedVolunteerInfo.volunteerId;
    if (!volunteerId) { return; }
    isLoading.value = true;
    try {
        const params = { activityName: searchText.value, volunteerId: volunteerId };
        const res = await request.get('/api/volunteer-activity/available-for-volunteer', { params });
        if (res.code === '200' && res.data) {
            activities.value = res.data;
        } else {
            ElMessage.error(res.msg || '获取活动列表失败');
        }
    } catch (error) { ElMessage.error('网络错误'); } finally { isLoading.value = false; }
};

onMounted(fetchActivities);

const handleSearch = () => {
    currentPage.value = 1;
    fetchActivities();
};

const resetJoinDialog = () => { Object.assign(joinDialog, { visible: false, isLoadingPositions: false, isSubmitting: false, activityId: null, activityName: '', intendedPositionId: '', positions: [] }); };

const openJoinDialog = async (activity) => {
    resetJoinDialog();
    joinDialog.visible = true;
    joinDialog.isLoadingPositions = true;
    joinDialog.activityId = activity.activityId;
    joinDialog.activityName = activity.activityName;
  try {
    // 【已修正】这里的API地址之前写错了，现在修正为获取岗位的正确地址
    const res = await request.get(`/api/volunteer-activity/${activity.activityId}/positions`);
    if (res.code === '200' && res.data) {
      joinDialog.positions = res.data;
    } else {
      ElMessage.error(res.msg || '获取岗位列表失败');
      joinDialog.visible = false;
    }
  } catch (error) {
    ElMessage.error('网络错误，无法获取岗位列表');
    joinDialog.visible = false;
  } finally {
    joinDialog.isLoadingPositions = false;
  }
};

const submitJoin = async () => {
  const volunteerId = userStore.detailedVolunteerInfo.volunteerId;
  if (!volunteerId) {
    return ElMessage.warning('请先登录再报名！');
  }
  if (!joinDialog.intendedPositionId) {
    return ElMessage.warning('请选择意向岗位');
  }

  joinDialog.isSubmitting = true;
  try {
    const payload = {volunteerId, activityId: joinDialog.activityId, intendedPositionId: joinDialog.intendedPositionId};
    const res = await request.post('/api/application/apply', payload);
    if (res.code === '200') {
      ElMessage.success('报名成功！');
      joinDialog.visible = false;
      fetchActivities();
    } else {
      ElMessage.error(res.msg || '报名失败');
    }
  } catch (error) {
    ElMessage.error('报名时发生错误');
  } finally {
    joinDialog.isSubmitting = false;
  }
};

const openDetailDialog = async (activityId) => {
  detailDialog.visible = true;
  detailDialog.isLoading = true;
  detailDialog.data = null;
  try {
    // 【已修正】这里的API地址也统一为/api前缀
    const res = await request.get(`/api/volunteer-activity/${activityId}`);
    if (res.code === '200' && res.data) {
      detailDialog.data = res.data;
    } else {
      ElMessage.error(res.msg || '获取详情失败');
      detailDialog.visible = false;
    }
  } catch (error) {
    ElMessage.error('网络错误');
    detailDialog.visible = false;
  } finally {
    detailDialog.isLoading = false;
  }
}
</script>

<style scoped>
/* 样式保持不变 */
.projects-more-page {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.06);
  padding: 24px;
  min-height: 600px;
}

.projects-more-title {
  font-size: 24px;
  font-weight: bold;
  color: #ff0000;
  margin-bottom: 24px;
  text-align: left;
}

.project-card-col {
  margin-bottom: 24px;
}

.project-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px 0 rgba(255, 0, 0, 0.06);
  padding: 0;
  transition: all 0.3s ease-in-out;
}

.project-card:hover {
  box-shadow: 0 4px 16px 0 rgba(255, 0, 0, 0.12);
  transform: translateY(-5px);
}

.project-img-wrap {
  position: relative;
  width: 100%;
  padding-top: 56.25%;
  overflow: hidden;
  background: #f0f2f5;
}

.project-img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.project-status {
  position: absolute;
  left: 12px;
  top: 12px;
  background: #13ce66;
  color: #fff;
  font-size: 14px;
  border-radius: 12px;
  padding: 2px 12px;
  font-weight: bold;
}

.project-name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  padding: 16px;
  margin: 0;
  text-align: center;
  min-height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.project-info-row {
  display: flex;
  justify-content: center;
  color: #606266;
  font-size: 13px;
  padding: 0 16px 4px 16px;
}

.recruit-label, .admitted-label {
  text-align: center;
  width: 100%;
}

.project-action-row {
  display: flex;
  justify-content: center;
  padding: 8px 16px 16px 16px;
  border-top: 1px solid #f0f2f5;
  margin-top: 8px;
}

.empty-state {
  text-align: center;
  color: #909399;
  padding: 40px 0;
}
</style>