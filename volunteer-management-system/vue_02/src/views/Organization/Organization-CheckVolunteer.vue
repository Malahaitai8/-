<template>
  <div class="activity-records-container">
    <div class="header">
      <h1>审核志愿活动报名</h1>
    </div>

    <div class="content-wrapper">
      <el-card class="main-card">
        <div class="search-filter-area">
          <el-input
              v-model="searchQuery"
              placeholder="搜索报名志愿者"
              clearable
              @clear="handleClear('volunteer')"
              class="search-input"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-input
              v-model="searchActivity"
              placeholder="搜索志愿活动"
              clearable
              @clear="handleClear('activity')"
              class="search-input"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="handleSearch" class="search-btn">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </div>

        <div class="table-container">
          <el-table
              :data="pendingActivity"
              v-loading="isLoading"
              class="activity-table"
              empty-text="暂无待审核的志愿者"
              table-layout="auto"
          >
            <el-table-column prop="id" label="志愿者ID" width="250" show-overflow-tooltip></el-table-column>
            <el-table-column prop="name" label="姓名" width="100" show-overflow-tooltip></el-table-column>
            <el-table-column prop="gender" label="性别" width="100" align="center"></el-table-column>
            <el-table-column prop="telephone" label="联系方式" width="180" align="center"></el-table-column>
            <el-table-column prop="activityName" label="申请的志愿活动" width="250" show-overflow-tooltip></el-table-column>
            <el-table-column prop="desiredPosition" label="意向岗位" width="150" align="center"></el-table-column>
            <el-table-column label="操作" width="260" align="center" fixed="right">
              <template #default="{ row }">
                <div class="action-buttons">
                  <el-button size="small" type="info" @click="viewDetails(row)" class="action-btn">
                    查看详情
                  </el-button>
                  <el-button size="small" type="success" @click="approve(row)" class="action-btn">
                    同意
                  </el-button>
                  <el-button size="small" type="danger" @click="reject(row)" class="action-btn">
                    拒绝
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div class="button-group">
          <el-button type="default" @click="home" size="large" class="back-btn">
            <el-icon><ArrowLeft /></el-icon>
            返回主页
          </el-button>
        </div>
      </el-card>
    </div>

    <!-- 对话框 -->
    <el-dialog v-model="dialogVisible" title="志愿者详细信息" width="800px">
      <el-form :model="selectedVolunteer" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="志愿者ID" class="bordered-item">
              <span>{{ selectedVolunteer.id }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" class="bordered-item">
              <span>{{ selectedVolunteer.name }}</span>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="性别" class="bordered-item">
              <span>{{ selectedVolunteer.gender }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="政治面貌" class="bordered-item">
              <span>{{ selectedVolunteer.zzmm }}</span>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="联系方式" class="bordered-item">
              <span>{{ selectedVolunteer.telephone }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最高学历" class="bordered-item">
              <span>{{ selectedVolunteer.study }}</span>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="志愿者综合评分" class="bordered-item">
              <span>{{ selectedVolunteer.rating }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请日期" class="bordered-item">
              <span>{{ selectedVolunteer.applicationDate }}</span>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="申请的志愿活动" class="bordered-item">
              <span>{{ selectedVolunteer.activityName }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请岗位" class="bordered-item">
              <span>{{ selectedVolunteer.desiredPosition }}</span>
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="申请活动时段" class="bordered-item">
              <span>{{ selectedVolunteer.activityPeriod }}</span>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-dialog>

  </div>
</template>

<script>
import { useOrgIdStore } from "@/stores/useOrgIdStore.js";
import { ElMessage } from "element-plus";
import request from "@/utils/request.js";
import { Search, ArrowLeft } from '@element-plus/icons-vue';

export default {
  components: {
    Search,
    ArrowLeft
  },
  data() {
    return {
      searchQuery: '', // "搜索报名志愿者" input
      searchActivity: '', // "搜索志愿活动" input
      pendingActivity: [], // Holds the filtered data for the table
      allPendingApplications: [], // Holds the original, unfiltered data from the server
      isLoading: false,
      dialogVisible: false,
      selectedVolunteer: {},
      orgId: null // Store orgId here
    };
  },
  mounted() {
    const orgIdStore = useOrgIdStore();
    this.orgId = orgIdStore.orgId; // Store orgId in component's data
    if (this.orgId) {
      this.fetchAndFilterApplications();
    } else {
      ElMessage.warning('组织ID未找到，请重新登录或刷新页面。');
    }
  },
  methods: {
    home() {
      this.$router.push('/organization-home');
    },
    viewDetails(row) {
      this.selectedVolunteer = row;
      this.dialogVisible = true;
    },
    approve(row) {
      row.applicationStatus = '已通过';
      this.updateApplicationStatus(row);
    },
    reject(row) {
      row.applicationStatus = '已拒绝';
      this.updateApplicationStatus(row);
    },

    fetchAndFilterApplications() {
      this.isLoading = true;
      request.get(`/api/application/pending-applications/${this.orgId}`)
          .then(res => {
            if (res.code === '200') {
              this.allPendingApplications = res.data || [];
              this.applyFilters();
            } else {
              ElMessage.error(res.msg || '获取数据失败');
              this.allPendingApplications = [];
              this.pendingActivity = [];
            }
          })
          .catch(error => {
            ElMessage.error('获取待审核列表时发生网络错误');
            console.error(error);
            this.allPendingApplications = [];
            this.pendingActivity = [];
          })
          .finally(() => {
            this.isLoading = false;
          });
    },

    applyFilters() {
      let filteredData = [...this.allPendingApplications];

      if (this.searchActivity && this.searchActivity.trim() !== '') {
        filteredData = filteredData.filter(item =>
            item.activityName && item.activityName.toLowerCase().includes(this.searchActivity.toLowerCase())
        );
      }

      if (this.searchQuery && this.searchQuery.trim() !== '') {
        filteredData = filteredData.filter(item =>
            item.name && item.name.toLowerCase().includes(this.searchQuery.toLowerCase())
        );
      }

      this.pendingActivity = filteredData;
    },

    handleSearch() {
      this.applyFilters();
    },

    // MODIFIED: This method no longer triggers a search
    handleClear(inputType) {
      if (inputType === 'activity') {
        this.searchActivity = '';
      }
      if (inputType === 'volunteer') {
        this.searchQuery = '';
      }
    },

    updateApplicationStatus(row) {
      request.put('/api/application/update-status', {
        applicationId: row.applicationId,
        applicationStatus: row.applicationStatus
      }).then(res => {
        if (res.code === '200') {
          ElMessage.success('操作成功');
          this.allPendingApplications = this.allPendingApplications.filter(item => item.applicationId !== row.applicationId);
          this.applyFilters();
        } else {
          ElMessage.error(res.msg || '状态更新失败');
        }
      }).catch(error => {
        ElMessage.error('状态更新时发生网络错误');
        console.error(error);
      });
    }
  }
};
</script>


<style scoped>
.activity-records-container {
  min-height: 100vh;
  background-image: url('@/../public/bg.png');
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center;
  padding: 0;
  position: relative;
}

.activity-records-container::before {
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
  max-width: 1600px;
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
  max-width: 300px;
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
}

.activity-table {
  width: 100%;
}

.activity-table :deep(.el-table__header) {
  background: linear-gradient(135deg, #ff3333, #ff6666);
}

.activity-table :deep(.el-table__header th) {
  background: transparent !important;
  color: white;
  font-weight: 600;
  font-size: 14px;
  padding: 15px 8px;
  border: none;
}

.activity-table :deep(.el-table__body tr) {
  transition: all 0.3s ease;
}

.activity-table :deep(.el-table__body tr:hover) {
  background: rgba(255, 51, 51, 0.05);
}

.activity-table :deep(.el-table__body td) {
  padding: 12px 8px;
  border-bottom: 1px solid #f0f0f0f0;
  font-size: 14px;
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
  border-top: 1px solid #f0f0f0f0;
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
.activity-table :deep(.el-loading-mask) {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
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

  .search-filter-area {
    flex-direction: column;
    gap: 10px;
  }

  .search-input {
    max-width: none;
    width: 100%;
  }

  .header h1 {
    font-size: 24px;
  }

  .activity-table :deep(.el-table__body td),
  .activity-table :deep(.el-table__header th) {
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
.activity-table :deep(.el-table__empty-block) {
  background: rgba(255, 51, 51, 0.05);
  color: #999;
  padding: 40px;
  border-radius: 8px;
  margin: 20px;
}
<style scoped>
   /* ... 您已有的其他样式 ... */

   /* 为弹窗内的表单项添加边框和内边距 */
 .main-card :deep(.el-dialog .bordered-item) {
   border: 1px solid #dcdfe6; /* 设置边框颜色 */
   border-radius: 6px;         /* 添加圆角 */
   padding: 0 15px;            /* 左右内边距 */
   margin-bottom: 20px;        /* 每个格子的下边距 */
   background-color: #f9fafc;  /* 设置淡淡的背景色，增强格子感 */
   display: flex;              /* 使用flex布局以更好地对齐 */
   align-items: center;        /* 垂直居中对齐标签和内容 */
   min-height: 40px;           /* 保证一个最小高度 */
 }

/* 调整标签的样式 */
.main-card :deep(.el-dialog .bordered-item .el-form-item__label) {
  /* color: #606266; */
  /* font-weight: 500; */
  padding-right: 12px;
  /* 确保标签在 flex 布局中不收缩 */
  flex-shrink: 0;
}

/* 调整内容区域的样式 */
.main-card :deep(.el-dialog .bordered-item .el-form-item__content) {
  /* 占据剩余空间 */
  flex-grow: 1;
  /* 移除 Element Plus 默认的 margin-left */
  margin-left: 0 !important;
  color: #303133;
  font-weight: 500;
}

/* 移除 el-form-item 默认的下边距，因为我们在 .bordered-item 上设置了 */
.main-card :deep(.el-dialog .el-form-item) {
  margin-bottom: 0;
}
</style>
