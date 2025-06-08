<template>
  <div class="projects-more-page">
    <div class="projects-more-title">参加更多项目</div>
    <div style="margin-bottom: 20px; display: flex; align-items: center;">
      <el-input v-model="searchText" placeholder="请输入项目名称" style="width: 240px; margin-right: 12px;" clearable />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>
    <el-row :gutter="24">
      <el-col v-for="item in pageData" :key="item.id" :span="6" class="project-card-col">
        <el-card class="project-card">
          <div class="project-img-wrap">
            <img :src="item.img" class="project-img" @click="goToDetail(item)" style="cursor:pointer;" />
            <span class="project-status" v-if="item.status === '进行中'">进行中</span>
          </div>
          <div class="project-name" @click="goToDetail(item)" style="cursor:pointer;">{{ item.name }}</div>
          <div class="project-info-row">
            <div class="recruit-label">招募人数 {{ item.target }}</div>
          </div>
          <div class="project-info-row">
            <div class="admitted-label">已录取人数 {{ item.signup }}</div>
          </div>
          <div class="project-action-row">
            <el-button type="primary" size="small" @click="openJoinDialog(item)">参与项目</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-pagination
      style="margin-top: 32px; text-align: center;"
      background
      layout="prev, pager, next, jumper"
      :total="projects.length"
      :page-size="pageSize"
      v-model:current-page="currentPage"
    />
    <el-dialog title="项目报名" v-model="joinDialogVisible" width="400px">
      <el-form :model="joinForm" label-width="80px">
        <el-form-item label="意向岗位">
          <el-select v-model="joinForm.position" placeholder="请选择意向岗位">
            <el-option
              v-for="item in joinPositions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="joinDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitJoin">提交</el-button>
      </template>
    </el-dialog>
    <el-dialog title="志愿活动详情" v-model="detailDialogVisible" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="志愿活动ID">{{ detailProject.id }}</el-descriptions-item>
        <el-descriptions-item label="活动名称">{{ detailProject.name }}</el-descriptions-item>
        <el-descriptions-item label="组织机构">{{ detailProject.orgName }}</el-descriptions-item>
        <el-descriptions-item label="负责人联系方式">{{ detailProject.contact }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ detailProject.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ detailProject.endTime }}</el-descriptions-item>
        <el-descriptions-item label="活动地点">{{ detailProject.location }}</el-descriptions-item>
        <el-descriptions-item label="总时长">{{ detailProject.duration }}</el-descriptions-item>
        <el-descriptions-item label="岗位设置">
          <el-tag v-for="pos in detailProject.positions || []" :key="pos" style="margin-right:4px;">{{ pos }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'ProjectsMore',
  setup() {
    const router = useRouter()
    // 模拟项目数据
    const projects = ref([
      {
        id: 1,
        img: require('@/static/image.jpeg'),
        name: '“小板凳”志愿服务项目',
        status: '进行中',
        target: 30,
        signup: 10,
        orgName: '福安志愿服务中心',
        contact: '张老师 13800000001',
        startTime: '2024-06-01 09:00',
        endTime: '2024-06-01 17:00',
        location: '福安市社区广场',
        duration: '8小时',
        positions: ['志愿者', '宣传员'],
      },
      {
        id: 2,
        img: require('@/static/image.jpeg'),
        name: '2025.6.5大志愿者"护航梦想路"',
        status: '进行中',
        target: 100,
        signup: 43,
        orgName: '梦想志愿者协会',
        contact: '李老师 13800000002',
        startTime: '2025-06-05 08:00',
        endTime: '2025-06-05 18:00',
        location: '市体育馆',
        duration: '10小时',
        positions: ['志愿者', '组织者'],
      },
      {
        id: 3,
        img: require('@/static/image.jpeg'),
        name: '福安志愿服务队',
        status: '进行中',
        target: 30,
        signup: 7,
        orgName: '福安志愿服务队',
        contact: '王老师 13800000003',
        startTime: '2024-07-01 09:00',
        endTime: '2024-07-01 17:00',
        location: '福安市志愿服务站',
        duration: '8小时',
        positions: ['志愿者'],
      },
      {
        id: 4,
        img: require('@/static/image.jpeg'),
        name: '"情暖端午·守护成长"五个一活动',
        status: '进行中',
        target: 100,
        signup: 20,
        orgName: '端午志愿服务组',
        contact: '赵老师 13800000004',
        startTime: '2024-06-10 09:00',
        endTime: '2024-06-10 17:00',
        location: '市青少年活动中心',
        duration: '8小时',
        positions: ['志愿者', '宣传员', '组织者'],
      },
      {
        id: 5,
        img: require('@/static/image.jpeg'),
        name: '"棕叶暖邻里 端午传真情"社区活动',
        status: '进行中',
        target: 2,
        signup: 0,
        orgName: '社区志愿服务站',
        contact: '钱老师 13800000005',
        startTime: '2024-06-08 14:00',
        endTime: '2024-06-08 18:00',
        location: '社区活动室',
        duration: '4小时',
        positions: ['志愿者'],
      },
      {
        id: 6,
        img: require('@/static/image.jpeg'),
        name: '多方携手植新绿，共筑生态新家园',
        status: '进行中',
        target: 50,
        signup: 6,
        orgName: '生态环保协会',
        contact: '孙老师 13800000006',
        startTime: '2024-06-15 09:00',
        endTime: '2024-06-15 17:00',
        location: '市郊林场',
        duration: '8小时',
        positions: ['志愿者', '宣传员'],
      },
      {
        id: 7,
        img: require('@/static/image.jpeg'),
        name: '"清捡垃圾 美化环境"志愿服务',
        status: '进行中',
        target: 50,
        signup: 6,
        orgName: '环保志愿者联盟',
        contact: '周老师 13800000007',
        startTime: '2024-06-20 09:00',
        endTime: '2024-06-20 17:00',
        location: '市区公园',
        duration: '8小时',
        positions: ['志愿者'],
      },
      {
        id: 8,
        img: require('@/static/image.jpeg'),
        name: '土右义工"文明小屋"项目',
        status: '进行中',
        target: 100,
        signup: 7,
        orgName: '土右义工协会',
        contact: '吴老师 13800000008',
        startTime: '2024-06-25 09:00',
        endTime: '2024-06-25 17:00',
        location: '土右社区',
        duration: '8小时',
        positions: ['志愿者', '宣传员'],
      }
    ])
    const pageSize = 8
    const currentPage = ref(1)
    const searchText = ref('')
    const filteredProjects = computed(() => {
      if (!searchText.value) return projects.value
      return projects.value.filter(item => item.name.includes(searchText.value))
    })
    const pageData = computed(() => {
      const start = (currentPage.value - 1) * pageSize
      return filteredProjects.value.slice(start, start + pageSize)
    })
    const joinDialogVisible = ref(false)
    const joinForm = ref({ position: '' })
    const joinPositions = ref(['志愿者', '宣传员'])
    const openJoinDialog = (item) => {
      joinDialogVisible.value = true
      joinForm.value = { position: '' }
      // 可根据item自定义岗位选项
      // joinPositions.value = item.positions || ['志愿者', '宣传员', '组织者']
    }
    const submitJoin = () => {
      joinDialogVisible.value = false
      window.$message ? window.$message.success('报名成功！') : alert('报名成功！')
    }
    const handleSearch = () => {
      currentPage.value = 1
    }
    // 详细信息弹窗相关
    const detailDialogVisible = ref(false)
    const detailProject = ref({})
    const openDetailDialog = (item) => {
      detailProject.value = item
      detailDialogVisible.value = true
    }
    // 跳转到详情页
    const goToDetail = (item) => {
      router.push({ name: 'ProjectDetail', params: { id: item.id } })
    }
    return {
      projects,
      pageSize,
      currentPage,
      pageData,
      searchText,
      handleSearch,
      joinDialogVisible,
      joinForm,
      joinPositions,
      openJoinDialog,
      submitJoin,
      detailDialogVisible,
      detailProject,
      openDetailDialog,
      goToDetail
    }
  }
}
</script>

<style scoped>
.projects-more-page {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.06);
  padding: 24px 16px 40px 16px;
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
  box-shadow: 0 2px 8px 0 rgba(255,0,0,0.06);
  padding: 0;
  transition: box-shadow 0.2s;
}
.project-card:hover {
  box-shadow: 0 4px 16px 0 rgba(255,0,0,0.12);
}
.project-img-wrap {
  position: relative;
  width: 100%;
  height: 120px;
  overflow: hidden;
  background: #f8f8f8;
  display: flex;
  align-items: center;
  justify-content: center;
}
.project-img {
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
  color: #333;
  margin: 16px 0 8px 0;
  text-align: center;
  min-height: 40px;
}
.project-info-row {
  display: flex;
  justify-content: center;
  align-items: center;
  color: #666;
  font-size: 13px;
  margin: 0 0 4px 0;
}
.recruit-label, .admitted-label {
  text-align: center;
  width: 100%;
}
.project-progress-row {
  display: flex;
  justify-content: center;
  align-items: center;
  color: #666;
  font-size: 13px;
  margin: 0 0 4px 0;
}
.project-days-row {
  display: flex;
  justify-content: center;
  align-items: center;
  color: #666;
  font-size: 13px;
  margin: 0 0 8px 0;
}
.progress-label {
  text-align: center;
  width: 100%;
}
.days-label {
  text-align: center;
  width: 100%;
}
.progress-num {
  color: #ff0000;
  font-weight: bold;
}
.project-days {
  color: #ff0000;
  font-weight: bold;
}
.days {
  color: #ff0000;
}
.project-action-row {
  display: flex;
  justify-content: center;
  margin: 12px 0 0 0;
}
</style> 