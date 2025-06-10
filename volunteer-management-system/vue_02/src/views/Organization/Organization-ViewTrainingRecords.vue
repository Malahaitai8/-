<template>
  <div class="training-records-container">
    <div class="header">
      <h1>我的志愿培训记录</h1>
    </div>
    
    <div class="content-wrapper">
      <el-card class="main-card">
        <div class="search-filter-area">
          <el-input 
            v-model="searchForm.trainingName" 
            placeholder="搜索培训名称" 
            clearable 
            @clear="loadTrainings"
            class="search-input"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-select 
            v-model="searchForm.trainingStatus" 
            placeholder="选择培训状态" 
            clearable 
            @clear="loadTrainings"
            class="search-select"
          >
            <el-option label="全部状态" value=""></el-option>
            <el-option label="待审核" value="待审核"></el-option>
            <el-option label="审核通过" value="审核通过"></el-option>
            <el-option label="进行中" value="进行中"></el-option>
            <el-option label="已结束" value="已结束"></el-option>
            <el-option label="审核不通过" value="审核不通过"></el-option>
            <el-option label="已停用" value="已停用"></el-option>
          </el-select>
          <el-select 
            v-model="searchForm.theme" 
            placeholder="选择培训类型" 
            clearable 
            @clear="loadTrainings"
            class="search-select"
          >
            <el-option label="全部类型" value=""></el-option>
            <el-option label="组织内部培训" value="组织内部培训"></el-option>
            <el-option label="特殊岗位培训" value="特殊岗位培训"></el-option>
            <el-option label="志愿活动培训" value="志愿活动培训"></el-option>
            <el-option label="技能培训" value="技能培训"></el-option>
            <el-option label="安全培训" value="安全培训"></el-option>
            <el-option label="其他培训" value="其他培训"></el-option>
          </el-select>
          <el-button type="primary" @click="loadTrainings" class="search-btn">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </div>

        <div class="table-container">
          <el-table 
            :data="trainings" 
            v-loading="isLoading" 
            class="training-table"
            empty-text="暂无培训记录"
            table-layout="auto"
          >
            <el-table-column prop="trainingId" label="培训ID" width="120" show-overflow-tooltip></el-table-column>
            <el-table-column prop="trainingName" label="培训名称" min-width="160" show-overflow-tooltip></el-table-column>
            <el-table-column prop="theme" label="培训类型" width="130" show-overflow-tooltip></el-table-column>
            <el-table-column prop="recruitmentCount" label="招募人数" width="100" align="center"></el-table-column>
            <el-table-column prop="trainingStatus" label="培训状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getStatusTagType(row.trainingStatus)" class="status-tag">
                  {{ row.trainingStatus }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="contactPersonPhone" label="联系方式" width="120" show-overflow-tooltip></el-table-column>
            <el-table-column prop="creationTime" label="创建时间" width="140" show-overflow-tooltip></el-table-column>
            <el-table-column label="操作" width="260" align="center" fixed="right">
              <template #default="{ row }">
                <div class="action-buttons">
                  <el-button size="small" type="info" @click="viewDetails(row)" class="action-btn">
                    查看详情
                  </el-button>
                  <el-button size="small" type="warning" @click="cancelTraining(row)" class="action-btn">
                    取消活动
                  </el-button>
                  <el-button size="small" type="danger" @click="deleteTraining(row)" class="action-btn">
                    删除记录
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div class="button-group">
          <el-button type="default" @click="goBack" size="large" class="back-btn">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import { useOrganizationStore } from '@/stores/organizationStore.js';
import request from '@/utils/request.js';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, ArrowLeft } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const organizationStore = useOrganizationStore();
const trainings = ref([]);
const isLoading = ref(false);

const searchForm = reactive({
  trainingName: '',
  trainingStatus: '',
  theme: '',
});

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});

// 加载培训列表
const loadTrainings = async () => {
  if (!organizationStore.currentOrganizationId) {
    ElMessage.warning('请先登录组织账户以查看培训记录。');
    return;
  }

  isLoading.value = true;
  try {
    const params = {};

    const res = await request.get(`/volunteerTraining/byOrg/${organizationStore.currentOrganizationId}`, { params });

    if (res.code === '200' && res.data) {
      let filteredTrainings = res.data;
      if (searchForm.trainingName) {
        filteredTrainings = filteredTrainings.filter(training =>
            training.trainingName.includes(searchForm.trainingName)
        );
      }
      if (searchForm.trainingStatus) {
        filteredTrainings = filteredTrainings.filter(training =>
            training.trainingStatus === searchForm.trainingStatus
        );
      }
      if (searchForm.theme) {
        filteredTrainings = filteredTrainings.filter(training =>
            training.theme === searchForm.theme
        );
      }
      trainings.value = filteredTrainings;
      pagination.total = filteredTrainings.length;
    } else {
      ElMessage.error(res.msg || '获取培训列表失败');
    }
  } catch (error) {
    console.error('加载培训列表失败:', error);
    ElMessage.error('网络错误或服务器异常');
  } finally {
    isLoading.value = false;
  }
};

// 返回上一页
const goBack = () => {
  router.go(-1);
};

// 查看培训详情
const viewDetails = (row) => {
  router.push({ 
    name: 'training-detail', 
    params: { id: row.trainingId } 
  });
};

// 删除培训
const deleteTraining = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除培训 "${row.trainingName}" 吗？删除后将无法恢复！`, 
      '确认删除', 
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error',
      }
    );

    const res = await request.delete(`/volunteerTraining/delete/${row.trainingId}`);

    if (res.code === '200') {
      ElMessage.success('培训已成功删除！');
      loadTrainings();
    } else {
      ElMessage.error(res.msg || '删除培训失败。');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除培训请求失败:', error);
      ElMessage.error('网络错误或系统异常，删除培训失败。');
    }
  }
};

// 取消培训
const cancelTraining = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消培训 "${row.trainingName}" 吗？取消后培训状态将变为"已停用"，且已通过/待审核的报名都将取消。`, 
      '确认取消', 
      {
        confirmButtonText: '确定取消',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );

    const payload = {
      operatingOrgId: organizationStore.currentOrganizationId,
      eventType: 'Training',
      eventID: row.trainingId,
    };

    const res = await request.post('/organization/cancelEvent', payload);

    if (res.code === '200') {
      ElMessage.success('培训已成功取消！');
      loadTrainings();
    } else {
      ElMessage.error(res.msg || '取消培训失败。');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消培训请求失败:', error);
      ElMessage.error('网络错误或系统异常，取消培训失败。');
    }
  }
};

// 根据培训状态返回不同的Tag样式
const getStatusTagType = (status) => {
  switch (status) {
    case '待审核': return 'info';
    case '审核通过': return 'success';
    case '进行中': return '';
    case '已结束': return 'info';
    case '审核不通过': return 'warning';
    case '已停用': return 'danger';
    default: return '';
  }
};

onMounted(() => {
  loadTrainings();
});
</script>

<style scoped>
.training-records-container {
  min-height: 100vh;
  background-image: url('@/../public/bg.png');
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center;
  padding: 0;
  position: relative;
  overflow-x: hidden;
  width: 100%;
}

.training-records-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 51, 51, 0.1);
  z-index: 1;
}

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

.search-filter-area {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  flex-wrap: wrap;
  align-items: center;
}

.search-input {
  flex: 1;
  min-width: 200px;
  max-width: 280px;
}

.search-select {
  flex: 1;
  min-width: 160px;
  max-width: 220px;
}

.search-input :deep(.el-input__wrapper),
.search-select :deep(.el-input__wrapper) {
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  transition: all 0.3s ease;
  height: 42px;
}

.search-input :deep(.el-input__wrapper:hover),
.search-select :deep(.el-input__wrapper:hover) {
  border-color: #ff6666;
}

.search-input :deep(.el-input.is-focus .el-input__wrapper),
.search-select :deep(.el-select.is-focus .el-input__wrapper) {
  border-color: #ff3333;
  box-shadow: 0 0 0 2px rgba(255, 51, 51, 0.2);
}

.search-btn {
  background: #ff3333;
  border: 2px solid #ff3333;
  color: white;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
  white-space: nowrap;
  flex-shrink: 0;
}

.search-btn:hover {
  background: #ff0000;
  border-color: #ff0000;
  transform: translateY(-1px);
  box-shadow: 0 4px 15px rgba(255, 51, 51, 0.3);
}

.table-container {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
  width: 100%;
}

.training-table {
  width: 100%;
  overflow-x: hidden;
}

.training-table :deep(.el-table__header) {
  background: linear-gradient(135deg, #ff3333, #ff6666);
}

.training-table :deep(.el-table__header th) {
  background: transparent !important;
  color: white;
  font-weight: 600;
  font-size: 14px;
  padding: 15px 8px;
  border: none;
}

.training-table :deep(.el-table__body tr) {
  transition: all 0.3s ease;
}

.training-table :deep(.el-table__body tr:hover) {
  background: rgba(255, 51, 51, 0.05);
}

.training-table :deep(.el-table__body td) {
  padding: 12px 8px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
}

.status-tag {
  border-radius: 20px;
  padding: 4px 12px;
  font-size: 12px;
  font-weight: 500;
}

.action-buttons {
  display: flex;
  gap: 6px;
  justify-content: center;
  flex-wrap: nowrap;
  align-items: center;
}

.action-btn {
  border-radius: 6px;
  padding: 5px 8px;
  font-size: 12px;
  transition: all 0.3s ease;
  min-width: 72px;
  white-space: nowrap;
  flex-shrink: 0;
}

.action-btn:hover {
  transform: translateY(-1px);
}

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

/* 加载状态样式 */
.training-table :deep(.el-loading-mask) {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .main-card {
    max-width: 98%;
  }
  
  .training-table :deep(.el-table__body td),
  .training-table :deep(.el-table__header th) {
    padding: 10px 6px;
    font-size: 13px;
  }
}

@media (max-width: 1200px) {
  .content-wrapper {
    padding: 20px 10px;
  }
  
  .main-card {
    max-width: 99%;
  }
  
  .main-card :deep(.el-card__body) {
    padding: 30px 20px;
  }
}

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
  
  .search-filter-area {
    flex-direction: column;
    gap: 10px;
  }
  
  .search-input,
  .search-select {
    max-width: none;
    width: 100%;
  }
  
  .header h1 {
    font-size: 24px;
  }
  
  .training-table :deep(.el-table__body td),
  .training-table :deep(.el-table__header th) {
    padding: 8px 4px;
    font-size: 12px;
  }
  
  .action-buttons {
    gap: 4px;
  }
  
  .action-btn {
    min-width: 60px;
    padding: 4px 6px;
    font-size: 11px;
  }
}

/* 空状态样式 */
.training-table :deep(.el-table__empty-block) {
  background: rgba(255, 51, 51, 0.05);
  color: #999;
  padding: 40px;
  border-radius: 8px;
  margin: 20px;
}
</style>