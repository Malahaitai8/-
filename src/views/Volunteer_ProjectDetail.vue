<template>
  <div class="project-detail-bg">
    <div class="project-detail-breadcrumb">
      <span class="crumb">当前位置：</span>
      <span class="crumb-link">首页</span>
      <span class="crumb-sep">&gt;</span>
      <span class="crumb-link">志愿项目</span>
      <span class="crumb-sep">&gt;</span>
      <span class="crumb-active">项目详情</span>
    </div>
    <div class="project-detail-main">
      <div class="project-detail-left">
        <img :src="project.img || require('@/static/image.jpeg')" class="project-detail-img" />
      </div>
      <div class="project-detail-right">
        <div class="project-title-row">
          <div class="project-title">{{ project.name }}</div>
          <div class="project-id">项目编号：<span>{{ project.id }}</span></div>
        </div>
        <div class="project-info-row">
          <span>组织机构：<span class="project-org">{{ project.orgName }}</span></span>
          <span>联系人：<span class="project-contact">{{ project.contact }}</span></span>
        </div>
        <div class="project-info-row">
          <span>活动时间：{{ project.startTime }} ~ {{ project.endTime }}</span>
          <span>活动地点：{{ project.location }}</span>
        </div>
        <div class="project-info-row">
          <span>招募人数：{{ project.target || '--' }}</span>
          <span>已录取人数：{{ project.signup || '--' }}</span>
        </div>
        <div class="project-info-row">
          <span>总时长：{{ project.duration }}</span>
          <span>岗位设置：
            <el-tag v-for="pos in project.positions || []" :key="pos" style="margin-right:4px;">{{ pos }}</el-tag>
          </span>
        </div>

      </div>
    </div>
    
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

export default {
  name: 'Volunteer_ProjectDetail',
  setup() {
    const route = useRoute()
    const router = useRouter()
    // 假设数据来源于本地（实际可从API获取）
    const allProjects = [
      {
        id: 1,
        img: require('@/static/image.jpeg'),
        name: '“小板凳”志愿服务项目',
        orgName: '福安志愿服务中心',
        contact: '张老师 13800000001',
        startTime: '2024-06-01 09:00',
        endTime: '2024-06-01 17:00',
        location: '福安市社区广场',
        duration: '8小时',
        positions: ['志愿者', '宣传员'],
        target: 30,
        signup: 10,
      },
      {
        id: 2,
        img: require('@/static/image.jpeg'),
        name: '2025.6.5大志愿者"护航梦想路"',
        orgName: '梦想志愿者协会',
        contact: '李老师 13800000002',
        startTime: '2025-06-05 08:00',
        endTime: '2025-06-05 18:00',
        location: '市体育馆',
        duration: '10小时',
        positions: ['志愿者', '组织者'],
        target: 100,
        signup: 43,
      },
      {
        id: 3,
        img: require('@/static/image.jpeg'),
        name: '福安志愿服务队',
        orgName: '福安志愿服务队',
        contact: '王老师 13800000003',
        startTime: '2024-07-01 09:00',
        endTime: '2024-07-01 17:00',
        location: '福安市志愿服务站',
        duration: '8小时',
        positions: ['志愿者'],
        target: 30,
        signup: 7,
      },
      {
        id: 4,
        img: require('@/static/image.jpeg'),
        name: '"情暖端午·守护成长"五个一活动',
        orgName: '端午志愿服务组',
        contact: '赵老师 13800000004',
        startTime: '2024-06-10 09:00',
        endTime: '2024-06-10 17:00',
        location: '市青少年活动中心',
        duration: '8小时',
        positions: ['志愿者', '宣传员', '组织者'],
        target: 100,
        signup: 20,
      },
      {
        id: 5,
        img: require('@/static/image.jpeg'),
        name: '"棕叶暖邻里 端午传真情"社区活动',
        orgName: '社区志愿服务站',
        contact: '钱老师 13800000005',
        startTime: '2024-06-08 14:00',
        endTime: '2024-06-08 18:00',
        location: '社区活动室',
        duration: '4小时',
        positions: ['志愿者'],
        target: 2,
        signup: 0,
      },
      {
        id: 6,
        img: require('@/static/image.jpeg'),
        name: '多方携手植新绿，共筑生态新家园',
        orgName: '生态环保协会',
        contact: '孙老师 13800000006',
        startTime: '2024-06-15 09:00',
        endTime: '2024-06-15 17:00',
        location: '市郊林场',
        duration: '8小时',
        positions: ['志愿者', '宣传员'],
        target: 50,
        signup: 6,
      },
      {
        id: 7,
        img: require('@/static/image.jpeg'),
        name: '"清捡垃圾 美化环境"志愿服务',
        orgName: '环保志愿者联盟',
        contact: '周老师 13800000007',
        startTime: '2024-06-20 09:00',
        endTime: '2024-06-20 17:00',
        location: '市区公园',
        duration: '8小时',
        positions: ['志愿者'],
        target: 50,
        signup: 6,
      },
      {
        id: 8,
        img: require('@/static/image.jpeg'),
        name: '土右义工"文明小屋"项目',
        orgName: '土右义工协会',
        contact: '吴老师 13800000008',
        startTime: '2024-06-25 09:00',
        endTime: '2024-06-25 17:00',
        location: '土右社区',
        duration: '8小时',
        positions: ['志愿者', '宣传员'],
        target: 100,
        signup: 7,
      }
    ]
    const project = ref({})
    const id = Number(route.params.id)
    project.value = allProjects.find(p => p.id === id) || {}
    const goBack = () => {
      router.back()
    }
    return {
      project,
      goBack
    }
  }
}
</script>

<style scoped>
.project-detail-bg {
  min-height: 100vh;
  background: linear-gradient(180deg, #fff 60px, #f8f8f8 300px, #fff 100%);
  padding-bottom: 40px;
}
.project-detail-breadcrumb {
  padding: 24px 0 0 40px;
  font-size: 15px;
  color: #b22222;
}
.crumb {
  color: #b22222;
}
.crumb-link {
  color: #b22222;
  cursor: pointer;
}
.crumb-sep {
  margin: 0 6px;
  color: #b22222;
}
.crumb-active {
  color: #333;
}
.project-detail-main {
  display: flex;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.06);
  margin: 28px auto 0 auto;
  width: 1100px;
  min-height: 520px;
  padding: 48px 42px 42px 42px;
  align-items: flex-start;
}
.project-detail-left {
  width: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.project-detail-img {
  width: 260px;
  height: 260px;
  border-radius: 8px;
  object-fit: cover;
  background: #f8f8f8;
  border: 1px solid #eee;
}
.project-detail-right {
  flex: 1;
  padding-left: 56px;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}
.project-title-row {
  display: flex;
  align-items: flex-end;
  gap: 32px;
  margin-bottom: 36px;
}
.project-title {
  font-size: 28px;
  font-weight: bold;
  color: #222;
}
.project-id {
  font-size: 17px;
  color: #b22222;
}
.project-info-row {
  margin-top: 32px;
  font-size: 18px;
  color: #444;
  display: flex;
  gap: 60px;
  min-height: 32px;
}
.project-org {
  color: #b22222;
  font-weight: 500;
}
.project-contact {
  color: #228B22;
  font-weight: 500;
}
.project-share-row {
  margin-top: 18px;
  font-size: 15px;
  color: #888;
  display: flex;
  align-items: center;
  gap: 8px;
}
.iconfont {
  font-size: 20px;
  margin-right: 8px;
  color: #409EFF;
  cursor: pointer;
}
.project-detail-tabs {
  width: 1100px;
  margin: 24px auto 0 auto;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.06);
  min-height: 180px;
  padding: 0 32px 32px 32px;
}
.tab-bar {
  display: flex;
  border-bottom: 2px solid #f5c6cb;
  margin-bottom: 16px;
  padding-top: 24px;
}
.tab {
  font-size: 17px;
  color: #b22222;
  margin-right: 32px;
  padding-bottom: 8px;
  cursor: pointer;
}
.tab.active {
  border-bottom: 3px solid #b22222;
  font-weight: bold;
}
.tab-content {
  min-height: 80px;
  padding: 12px 0 0 0;
}
.project-desc {
  color: #666;
  font-size: 15px;
}
</style> 