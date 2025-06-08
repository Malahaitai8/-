<template>
  <div class="training-page">
    <!-- 红色标题栏 -->
    <div class="training-title">
      <span>我的培训</span>
    </div>
    <!-- 培训表格 -->
    <el-table
      v-if="pageData.length"
      :data="pageData"
      border
      style="width: 100%; margin-top: 20px;"
      header-cell-class-name="table-header"
    >
      <el-table-column prop="name" label="培训名称" align="center" />
      <el-table-column prop="startDate" label="培训开始日期" align="center" />
      <el-table-column prop="endDate" label="培训结束日期" align="center" />
      <el-table-column prop="state" label="培训状态" align="center" />
      <el-table-column prop="signIn" label="是否签到" align="center" />
      <el-table-column label="评价" align="center">
        <template #default="scope">
          <el-button size="small" type="primary" @click="openEvaluateDialog(scope.row)">评价</el-button>
          <el-button size="small" type="danger" @click="openComplaintDialog(scope.row)">我要投诉</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 空数据提示 -->
    <div v-else class="empty-box">
      <img src="https://img.alicdn.com/imgextra/i4/O1CN01v7Qw1B1QwQwQwQwQw_!!6000000002007-2-tps-200-200.png" alt="empty" />
      <div>暂无培训信息</div>
    </div>
    <!-- 分页器 -->
    <el-pagination
      v-if="trainings.length > pageSize"
      style="margin-top: 24px; text-align: right;"
      background
      layout="prev, pager, next, jumper"
      :total="trainings.length"
      :page-size="pageSize"
      v-model:current-page="currentPage"
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
  name: 'Training',
  setup() {
    // 模拟培训数据
    const trainings = ref([
      {
        name: '消防安全知识培训',
        startDate: '2024-04-01',
        endDate: '2024-04-01',
        state: '已完成',
        signIn: '是'
      },
      {
        name: '急救技能培训',
        startDate: '2024-03-15',
        endDate: '2024-03-15',
        state: '已完成',
        signIn: '是'
      },
      {
        name: '防疫知识讲座',
        startDate: '2024-02-20',
        endDate: '2024-02-20',
        state: '已完成',
        signIn: '是'
      },
      {
        name: '心理健康辅导',
        startDate: '2024-01-10',
        endDate: '2024-01-10',
        state: '已完成',
        signIn: '是'
      },
      {
        name: '环保知识普及',
        startDate: '2023-12-05',
        endDate: '2023-12-05',
        state: '已完成',
        signIn: '是'
      },
      {
        name: '助残服务培训',
        startDate: '2023-11-18',
        endDate: '2023-11-18',
        state: '已完成',
        signIn: '是'
      }
    ])
    // 分页相关
    const pageSize = 5
    const currentPage = ref(1)
    const pageData = computed(() => {
      const start = (currentPage.value - 1) * pageSize
      return trainings.value.slice(start, start + pageSize)
    })
    const evaluateDialogVisible = ref(false)
    const evaluateScore = ref(0)
    const openEvaluateDialog = (row) => {
      evaluateDialogVisible.value = true
      evaluateScore.value = row.score || 0
    }
    const submitEvaluate = () => {
      evaluateDialogVisible.value = false
      window.$message ? window.$message.success(`评价提交成功！分数：${evaluateScore.value}`) : alert(`评价提交成功！分数：${evaluateScore.value}`)
    }
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
      trainings,
      pageData,
      pageSize,
      currentPage,
      evaluateDialogVisible,
      evaluateScore,
      openEvaluateDialog,
      submitEvaluate,
      openComplaintDialog,
      complaintDialogVisible,
      complaintForm,
      complaintFormRef,
      handleEvidenceChange,
      submitComplaint,
      cancelComplaint
    }
  }
}
</script>

<style scoped>
.training-page {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.06);
  padding: 0 0 32px 0;
  min-height: 400px;
}
.training-title {
  background: #fff0f0;
  color: #ff0000;
  font-weight: bold;
  font-size: 20px;
  padding: 18px 32px 8px 32px;
  border-bottom: 3px solid #ff0000;
  margin-bottom: 0;
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