<template>
  <div class="apply-training-container">
    <div class="header">
      <h1>申请志愿培训</h1>
    </div>
    
    <div class="form-container">
      <el-card class="form-card">
        <el-form 
          ref="trainingForm" 
          :model="training" 
          :rules="rules"
          label-width="150px" 
          label-position="left"
          class="training-form"
        >
          <el-form-item label="培训名称" prop="name">
            <el-input 
              v-model="training.name" 
              placeholder="请输入志愿培训名称"
              clearable
            ></el-input>
          </el-form-item>
          
          <el-form-item label="培训类型" prop="type">
            <el-select 
              v-model="training.type" 
              placeholder="请选择志愿培训类型"
              style="width: 100%"
              clearable
            >
              <el-option
                v-for="type in trainingTypes"
                :key="type.value"
                :label="type.label"
                :value="type.value"
              ></el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="开始时间" prop="beginTime">
            <el-date-picker
              v-model="training.beginTime"
              type="datetime"
              placeholder="请选择培训开始时间"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 100%"
            ></el-date-picker>
          </el-form-item>
          
          <el-form-item label="结束时间" prop="endTime">
            <el-date-picker
              v-model="training.endTime"
              type="datetime"
              placeholder="请选择培训结束时间"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 100%"
            ></el-date-picker>
          </el-form-item>
          
          <el-form-item label="培训地点" prop="place">
            <el-input 
              v-model="training.place" 
              placeholder="请输入培训地点"
              clearable
            ></el-input>
          </el-form-item>
          
          <el-form-item label="参与人数" prop="numberOfParticipants">
            <el-input-number 
              v-model="training.numberOfParticipants" 
              :min="1"
              :max="9999"
              style="width: 100%"
              placeholder="请输入参与人数"
            ></el-input-number>
          </el-form-item>
          
          <el-form-item label="联系方式" prop="telephone">
            <el-input 
              v-model="training.telephone" 
              placeholder="请输入负责人联系方式"
              clearable
            ></el-input>
          </el-form-item>
        </el-form>
        
        <div class="button-group">
          <el-button type="default" @click="goBack" size="large">
            返回
          </el-button>
          <el-button type="primary" @click="submitApplication" size="large">
            提交申请
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useOrganizationStore } from '@/stores/organizationStore.js'

export default {
  name: 'OrganizationApplyTraining',
  setup() {
    const organizationStore = useOrganizationStore()
    return {
      organizationStore
    }
  },
  data() {
    return {
      training: {
        name: '',
        type: '',
        beginTime: '',
        endTime: '',
        place: '',
        numberOfParticipants: null,
        telephone: ''
      },
      trainingTypes: [
        { value: '组织内部培训', label: '组织内部培训' },
        { value: '特殊岗位培训', label: '特殊岗位培训' },
        { value: '志愿活动培训', label: '志愿活动培训' },
        { value: '技能培训', label: '技能培训' },
        { value: '安全培训', label: '安全培训' },
        { value: '其他培训', label: '其他培训' }
      ],
      rules: {
        name: [
          { required: true, message: '请输入培训名称', trigger: 'blur' },
          { min: 2, max: 50, message: '培训名称长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择培训类型', trigger: 'change' }
        ],
        beginTime: [
          { required: true, message: '请选择培训开始时间', trigger: 'change' }
        ],
        endTime: [
          { required: true, message: '请选择培训结束时间', trigger: 'change' }
        ],
        place: [
          { required: true, message: '请输入培训地点', trigger: 'blur' },
          { min: 2, max: 100, message: '培训地点长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        numberOfParticipants: [
          { required: true, message: '请输入参与人数', trigger: 'blur' },
          { type: 'number', min: 1, message: '参与人数必须大于0', trigger: 'blur' }
        ],
        telephone: [
          { required: true, message: '请输入联系方式', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    // 页面加载时重置表单
    this.resetForm()
  },
  methods: {
    // 重置表单数据
    resetForm() {
      this.training = {
        name: '',
        type: '',
        beginTime: '',
        endTime: '',
        place: '',
        numberOfParticipants: null,
        telephone: ''
      }
      // 清除表单验证
      if (this.$refs.trainingForm) {
        this.$refs.trainingForm.clearValidate()
      }
    },
    
    // 返回上一页
    goBack() {
      this.$router.go(-1)
    },
    
    // 提交申请
    async submitApplication() {
      try {
        // 表单验证
        const valid = await this.$refs.trainingForm.validate()
        if (!valid) {
          return
        }
        
        // 验证时间逻辑
        if (new Date(this.training.beginTime) >= new Date(this.training.endTime)) {
          ElMessage.error('开始时间不能晚于或等于结束时间')
          return
        }
        
        // 获取并验证组织ID
        const orgId = this.getOrgId()
        if (!orgId) {
          ElMessage.error('无法获取组织信息，请重新登录')
          return
        }
        
        // 构造请求数据
        const requestData = {
          trainingName: this.training.name,
          trainingType: this.training.type,
          startTime: this.training.beginTime,
          endTime: this.training.endTime,
          location: this.training.place,
          participantCount: this.training.numberOfParticipants,
          contactPersonPhone: this.training.telephone,
          orgId: orgId,
          trainingStatus: '待审核'
        }
        
        console.log('提交的培训申请数据:', requestData)
        
        // 发送申请请求
        const response = await axios.post('/api/volunteerTraining/apply', requestData)
        
        if (response.data.code === '200') {
          ElMessage.success('志愿培训申请提交成功，请等待审核！')
          // 重置表单
          this.resetForm()
          // 可选：跳转到组织首页
          setTimeout(() => {
            this.$router.push('/organization-home')
          }, 1500)
        } else {
          ElMessage.error(response.data.msg || '申请提交失败')
        }
      } catch (error) {
        console.error('提交培训申请时发生错误:', error)
        ElMessage.error('申请提交失败，请稍后重试')
      }
    },
    
    // 获取当前组织ID（从organizationStore获取）
    getOrgId() {
      const orgId = this.organizationStore.currentOrganizationId
      console.log('获取到的组织ID:', orgId)
      
      if (!orgId) {
        console.error('组织ID为空，尝试重新初始化store')
        this.organizationStore.initializeStore()
        return this.organizationStore.currentOrganizationId || null
      }
      
      return orgId
    }
  }
}
</script>

<style scoped>
.apply-training-container {
  min-height: 100vh;
  background-image: url('@/../public/bg.png');
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center;
  padding: 0;
  position: relative;
}

.apply-training-container::before {
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
  background-color: #ff3333; /* 鲜红色背景，与其他页面统一 */
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

.form-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 50px 20px;
  min-height: calc(100vh - 80px);
  position: relative;
  z-index: 2;
}

.form-card {
  width: 100%;
  max-width: 1000px; /* 进一步增大表单宽度 */
  min-height: 750px; /* 增加最小高度 */
  border-radius: 12px;
  box-shadow: 0 8px 25px rgba(255, 51, 51, 0.2);
  border: 1px solid rgba(255, 51, 51, 0.1);
  overflow: hidden;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

.form-card :deep(.el-card__body) {
  padding: 60px; /* 增加内边距 */
  min-height: 650px; /* 确保内容区域有足够高度 */
}

.training-form {
  margin-bottom: 40px; /* 增加表单底部间距 */
}

.training-form :deep(.el-form-item) {
  margin-bottom: 40px; /* 进一步增加表单项间距 */
}

.training-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #333;
  line-height: 1.6; /* 调整行高 */
  font-size: 16px; /* 增加标签字体大小 */
  margin-bottom: 8px; /* 减少标签与输入框的距离 */
}

.training-form :deep(.el-input) {
  height: 50px; /* 增加输入框高度 */
}

.training-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  border: 1px solid #dcdfe6; /* 只保留一层边框 */
  transition: all 0.3s ease;
  box-shadow: none; /* 移除默认阴影，避免重叠 */
  background-color: #fff;
}

.training-form :deep(.el-input__inner) {
  border: none; /* 移除内部输入框的边框，避免重叠 */
  border-radius: 0;
  height: 48px; /* 增加输入框高度 */
  line-height: 48px;
  font-size: 15px;
  box-shadow: none; /* 确保没有阴影 */
  background: transparent;
}

.training-form :deep(.el-input:hover .el-input__wrapper) {
  border-color: #ff6666; /* 悬停时边框颜色 */
}

.training-form :deep(.el-input.is-focus .el-input__wrapper) {
  border-color: #ff3333; /* 聚焦时显示红色边框 */
  box-shadow: 0 0 0 2px rgba(255, 51, 51, 0.2);
}

.training-form :deep(.el-select) {
  width: 100%;
  height: 50px;
}

.training-form :deep(.el-select .el-input__wrapper) {
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  box-shadow: none;
  background-color: #fff;
  height: 50px;
}

.training-form :deep(.el-select .el-input__inner) {
  border: none;
  height: 48px;
  line-height: 48px;
  background: transparent;
}

.training-form :deep(.el-select:hover .el-input__wrapper) {
  border-color: #ff6666;
}

.training-form :deep(.el-select.is-focus .el-input__wrapper) {
  border-color: #ff3333;
  box-shadow: 0 0 0 2px rgba(255, 51, 51, 0.2);
}

.training-form :deep(.el-date-editor) {
  width: 100%;
  height: 50px; /* 统一高度 */
}

.training-form :deep(.el-date-editor .el-input__wrapper) {
  border: 1px solid #dcdfe6; /* 统一边框样式 */
  border-radius: 8px;
  box-shadow: none;
  background-color: #fff;
}

.training-form :deep(.el-date-editor .el-input__inner) {
  border: none;
  height: 48px;
  line-height: 48px;
  background: transparent;
}

.training-form :deep(.el-date-editor:hover .el-input__wrapper) {
  border-color: #ff6666;
}

.training-form :deep(.el-date-editor.is-focus .el-input__wrapper) {
  border-color: #ff3333;
  box-shadow: 0 0 0 2px rgba(255, 51, 51, 0.2);
}

.training-form :deep(.el-input-number) {
  width: 100%;
  height: 50px; /* 统一高度 */
}

.training-form :deep(.el-input-number .el-input__wrapper) {
  border: 1px solid #dcdfe6; /* 统一边框样式 */
  border-radius: 8px;
  box-shadow: none;
  background-color: #fff;
}

.training-form :deep(.el-input-number .el-input__inner) {
  text-align: center; /* 数字居中显示 */
  border: none;
  height: 48px;
  line-height: 48px;
  font-size: 16px;
  font-weight: 500;
  background: transparent;
}

.training-form :deep(.el-input-number:hover .el-input__wrapper) {
  border-color: #ff6666;
}

.training-form :deep(.el-input-number.is-focus .el-input__wrapper) {
  border-color: #ff3333;
  box-shadow: 0 0 0 2px rgba(255, 51, 51, 0.2);
}

.training-form :deep(.el-input-number__increase),
.training-form :deep(.el-input-number__decrease) {
  height: 24px; /* 调整数字输入框按钮高度 */
  line-height: 24px;
  border: none;
  background: #f8f9fa;
}

.training-form :deep(.el-input-number__increase:hover),
.training-form :deep(.el-input-number__decrease:hover) {
  background: #ff3333;
  color: white;
}

.button-group {
  display: flex;
  justify-content: center;
  gap: 30px; /* 增加按钮间距 */
  margin-top: 40px; /* 增加顶部间距 */
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.button-group .el-button {
  padding: 15px 40px; /* 增大按钮尺寸 */
  border-radius: 25px;
  font-size: 16px;
  font-weight: 500;
  min-width: 140px; /* 增加最小宽度 */
  height: 50px; /* 固定按钮高度 */
  transition: all 0.3s ease;
}

.button-group .el-button--default {
  background: #f8f9fa;
  border: 2px solid #dee2e6;
  color: #6c757d;
}

.button-group .el-button--default:hover {
  background: #e9ecef;
  border-color: #adb5bd;
  color: #495057;
  transform: translateY(-2px);
}

.button-group .el-button--primary {
  background: #ff3333; /* 红色主题按钮 */
  border: 2px solid #ff3333;
  color: white;
  box-shadow: 0 4px 15px rgba(255, 51, 51, 0.3);
}

.button-group .el-button--primary:hover {
  background: #ff0000;
  border-color: #ff0000;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 51, 51, 0.4);
}

/* 表单项标题样式增强 */
.training-form :deep(.el-form-item__label)::before {
  content: '';
  display: inline-block;
  width: 3px;
  height: 16px;
  background: #ff3333;
  margin-right: 8px;
  vertical-align: middle;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .form-container {
    padding: 30px 15px;
  }
  
  .form-card {
    max-width: 95%;
    min-height: auto;
  }
  
  .form-card :deep(.el-card__body) {
    padding: 30px 20px;
    min-height: auto;
  }
  
  .header h1 {
    font-size: 24px;
  }
  
  .button-group {
    flex-direction: column;
    align-items: center;
    gap: 15px;
  }
  
  .button-group .el-button {
    width: 100%;
    max-width: 250px;
  }
  
  .training-form :deep(.el-form-item) {
    margin-bottom: 25px;
  }
}

/* 新增：表单验证错误样式 */
.training-form :deep(.el-form-item.is-error .el-input__wrapper) {
  border-color: #ff4757 !important;
  box-shadow: 0 0 0 2px rgba(255, 71, 87, 0.2) !important;
}

.training-form :deep(.el-form-item.is-error .el-select .el-input__wrapper) {
  border-color: #ff4757 !important;
  box-shadow: 0 0 0 2px rgba(255, 71, 87, 0.2) !important;
}

.training-form :deep(.el-form-item.is-error .el-date-editor .el-input__wrapper) {
  border-color: #ff4757 !important;
  box-shadow: 0 0 0 2px rgba(255, 71, 87, 0.2) !important;
}

.training-form :deep(.el-form-item.is-error .el-input-number .el-input__wrapper) {
  border-color: #ff4757 !important;
  box-shadow: 0 0 0 2px rgba(255, 71, 87, 0.2) !important;
}

.training-form :deep(.el-form-item__error) {
  color: #ff4757;
  font-size: 14px;
  margin-top: 5px;
}
</style>