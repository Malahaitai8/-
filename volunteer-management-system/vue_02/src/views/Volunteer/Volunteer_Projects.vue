<template>
  <div class="projects-page">
    <!-- 红色标题栏 -->
    <div class="projects-title">
      <span>我的项目</span>
    </div>
    <!-- 我的项目表格 -->
    <el-table
      v-if="activeTab === 'myProjects' && myPageData.length"
      :data="myPageData"
      border
      style="width: 100%; margin-top: 0;"
      header-cell-class-name="table-header"
    >
      <el-table-column prop="name" label="项目名称" align="center" />
      <el-table-column prop="position" label="岗位" align="center" />
      <el-table-column prop="signIn" label="是否签到" align="center" />
      <el-table-column label="我的评分" align="center">
        <template #default="scope">
          <span v-if="scope.row.score">{{ scope.row.score }} 分</span>
          <el-tag v-else type="info">未评分</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="组织方评分" align="center">
        <template #default="scope">
          <span v-if="scope.row.organisationScore">{{ scope.row.organisationScore }} 分</span>
          <el-tag v-else type="warning">待评分</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template #default="scope">
          <el-button size="small" type="primary" @click="openEvaluateDialog(scope.row)" :disabled="!!scope.row.score">评价</el-button>
          <el-button size="small" type="danger" @click="openComplaintDialog(scope.row)">我要投诉</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 待定项目表格 -->
    <el-table
      v-if="activeTab === 'pendingProjects' && pendingPageData.length"
      :data="pendingPageData"
      border
      style="width: 100%; margin-top: 0;"
      header-cell-class-name="table-header"
    >
      <el-table-column prop="name" label="项目名称" align="center">
        <template #default="scope">
          <span style="color:#d9001b;font-weight:bold;">{{ scope.row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="position" label="岗位" align="center" />
      <el-table-column prop="status" label="状态" align="center" />
      <el-table-column label="评价" align="center">
        <template #default="scope">
          <el-button type="text" style="color:#d9001b;" @click="handleApply(scope.row)">申请中</el-button>
          <el-button type="text" style="color:#d9001b;" @click="handleDelete(scope.row)">删除</el-button>
          <el-button type="text" style="color:#d9001b;" @click="handleChange(scope.row)">更换岗位</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 空数据提示 -->
    <div v-if="(activeTab === 'myProjects' && !myPageData.length) || (activeTab === 'pendingProjects' && !pendingPageData.length)" class="empty-box">
      <img src="https://img.alicdn.com/imgextra/i4/O1CN01v7Qw1B1QwQwQwQwQw_!!6000000002007-2-tps-200-200.png" alt="empty" />
      <div>暂无项目信息</div>
    </div>
    <!-- 分页器 -->
    <el-pagination
      v-if="activeTab === 'myProjects' && myProjects.length > pageSize"
      style="margin-top: 24px; text-align: right;"
      background
      layout="prev, pager, next, jumper"
      :total="myProjects.length"
      :page-size="pageSize"
      v-model:current-page="myCurrentPage"
    />
    <!-- 评价弹窗 -->
    <el-dialog title="填写评价" v-model="evaluateDialogVisible" width="400px">
      <el-rate
        v-model="evaluateScore"
        :max="10"
        show-score
        score-template="{value} 分"
        style="margin-bottom: 16px;"
      />
      <template #footer>
        <el-button @click="evaluateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEvaluate">提交</el-button>
      </template>
    </el-dialog>
    <el-pagination
      v-if="activeTab === 'pendingProjects' && pendingProjects.length > pageSize"
      style="margin-top: 24px; text-align: right;"
      background
      layout="prev, pager, next, jumper"
      :total="pendingProjects.length"
      :page-size="pageSize"
      v-model:current-page="pendingCurrentPage"
    />
    <!-- 投诉弹窗 -->
    <el-dialog title="我要投诉" v-model="complaintDialogVisible" width="500px">
      <el-form :model="complaintForm" ref="complaintFormRef">
        <el-form-item label="投诉类型" :required="true">
          <el-select v-model="complaintForm.type" placeholder="请选择投诉类型">
            <el-option label="服务质量" value="服务质量" />
            <el-option label="行为不当" value="行为不当" />
            <el-option label="信息虚假" value="信息虚假" />
            <el-option label="活动违规" value="活动违规" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="投诉内容" :required="true">
          <el-input type="textarea" v-model="complaintForm.content" :rows="4" placeholder="请填写投诉内容" />
        </el-form-item>
        <el-form-item label="证据文件">
          <el-upload
            class="upload-demo"
            action=""
            :auto-upload="false"
            :show-file-list="true"
            :on-change="handleEvidenceChange"
            :file-list="complaintForm.evidenceList"
            accept=".jpg,.jpeg,.png,.txt,.pdf"
          >
            <el-button size="small">选择文件</el-button>
            <template #tip>
              <div style="color: #aaa;">支持图片或文本文件，非必填</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancelComplaint">取消</el-button>
        <el-button type="primary" @click="submitComplaint">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'Projects',
  setup() {
    // 我的项目数据
    const myProjects = ref([
      {
        name: '社区卫生宣传',
        position: '宣传员',
        signIn: '是',
      },
      {
        name: '无偿献血活动',
        position: '志愿者',
        signIn: '是',
      },
      ])
    const pendingProjects = ref([
      {
        name: '“铸魂达尔罕”志愿服务项目',
        position: '志愿者',
        signIn: '是',
      },
      {
        name: '社区防疫宣传',
        position: '宣传员',
        signIn: '是',
      }
    ])
    // 分页相关
    const pageSize = 5
    const myCurrentPage = ref(1)
    const pendingCurrentPage = ref(1)
    const myPageData = computed(() => {
      const start = (myCurrentPage.value - 1) * pageSize
      return myProjects.value.slice(start, start + pageSize)
    })
    const pendingPageData = computed(() => {
      const start = (pendingCurrentPage.value - 1) * pageSize
      return pendingProjects.value.slice(start, start + pageSize)
    })

    // 标签页切换
    const activeTab = ref('myProjects')
    const handleTabClick = (tab) => {
      // 切换分页时重置页码
      if (tab.paneName === 'myProjects') {
        myCurrentPage.value = 1
      } else if (tab.paneName === 'pendingProjects') {
        pendingCurrentPage.value = 1
      }
    }

    // 操作按钮事件
    const handleApply = (row) => {
      alert(`申请中：${row.name}`)
    }
    const handleDelete = (row) => {
      alert(`删除：${row.name}`)
    }
    const handleChange = (row) => {
      alert(`更换岗位：${row.name}`)
    }

    const showMoreDialog = ref(false)
    const searchProject = ref('')
    const handleSearch = () => {
      alert(`搜索项目：${searchProject.value}`)
    }

    // 评价弹窗相关
    const evaluateDialogVisible = ref(false)
    const evaluateScore = ref(0)
    const openEvaluateDialog = (row) => {
      evaluateDialogVisible.value = true
      evaluateScore.value = row.score || 0
      currentEvaluateRow.value = row
    }
    const submitEvaluate = () => {
      evaluateDialogVisible.value = false
      if (currentEvaluateRow.value) {
        currentEvaluateRow.value.score = evaluateScore.value
      }
      window.$message ? window.$message.success(`评价提交成功！分数：${evaluateScore.value}`) : alert(`评价提交成功！分数：${evaluateScore.value}`)
    }
    const currentEvaluateRow = ref(null)

    // 投诉相关
    const complaintDialogVisible = ref(false)
    const complaintForm = ref({
      type: '',
      content: '',
      evidenceList: []
    })
    const complaintFormRef = ref(null)
    const openComplaintDialog = (row) => {
      complaintDialogVisible.value = true
      complaintForm.value = { type: '', content: '', evidenceList: [] }
    }
    const handleEvidenceChange = (file, fileList) => {
      complaintForm.value.evidenceList = fileList
    }
    const submitComplaint = () => {
      if (!complaintForm.value.type) {
        window.$message ? window.$message.error('请选择投诉类型！') : alert('请选择投诉类型！')
        return
      }
      if (!complaintForm.value.content) {
        window.$message ? window.$message.error('请填写投诉内容！') : alert('请填写投诉内容！')
        return
      }
      complaintDialogVisible.value = false
      window.$message ? window.$message.success('投诉成功！') : alert('投诉成功！')
    }
    const cancelComplaint = () => {
      complaintDialogVisible.value = false
      window.$message ? window.$message.info('投诉已取消') : alert('投诉已取消')
    }

    return {
      myProjects,
      pendingProjects,
      pageSize,
      myCurrentPage,
      pendingCurrentPage,
      myPageData,
      pendingPageData,
      activeTab,
      handleTabClick,
      handleApply,
      handleDelete,
      handleChange,
      showMoreDialog,
      searchProject,
      handleSearch,
      evaluateDialogVisible,
      evaluateScore,
      openEvaluateDialog,
      submitEvaluate,
      currentEvaluateRow,
      complaintDialogVisible,
      complaintForm,
      complaintFormRef,
      openComplaintDialog,
      handleEvidenceChange,
      submitComplaint,
      cancelComplaint
    }
  }
}
</script>

<style scoped>
.projects-title {
  background: #fff0f0;
  color: #ff0000;
  font-weight: bold;
  font-size: 20px;
  padding: 18px 32px 8px 32px;
  border-bottom: 3px solid #ff0000;
  margin-bottom: 0;
}
.projects-page {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.06);
  padding: 0 0 32px 0;
  min-height: 400px;
}
.projects-tabs-bar {
  display: flex;
  align-items: center;
  background: #fff0f0;
  border-bottom: 3px solid #ff0000;
  margin-bottom: 0;
  padding: 0 32px;
}
.projects-tabs {
  flex: 1;
  background: transparent;
  border-bottom: none;
}
.more-btn {
  margin-left: 16px;
  background: #ff0000;
  color: #fff;
  border: none;
}
.projects-tabs :deep(.el-tabs__item.is-active) {
  color: #ff0000 !important;
  font-weight: bold;
  background: #fff0f0;
  border-bottom: 3px solid #ff0000 !important;
}
.projects-tabs :deep(.el-tabs__item) {
  font-size: 18px;
}
.table-header {
  background: #fff0f0 !important;
  color: #ff0000 !important;
  font-weight: bold;
}
.empty-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 60px 0 0 0;
  color: #aaa;
  font-size: 16px;
}
.empty-box img {
  width: 80px;
  margin-bottom: 12px;
  opacity: 0.6;
}
</style>