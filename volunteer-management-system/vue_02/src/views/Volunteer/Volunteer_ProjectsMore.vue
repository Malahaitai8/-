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
            <img :src="item.img" class="project-img" alt="Project Image"/>
            <span class="project-status" v-if="item.status === '进行中'">进行中</span>
          </div>
          <div class="project-name">{{ item.name }}</div>
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
      style="margin-top: 32px; justify-content: center;"
      background
      layout="prev, pager, next, jumper"
      :total="filteredProjects.length"
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
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
// ✅ Correct way to import static assets like images.
import imageSrc from '@/assets/image.jpeg';

// --- Reactive Data ---
const projects = ref([
    {
        id: 1,
        img: imageSrc,
        name: '“小板凳”志愿服务项目',
        status: '进行中',
        target: 30,
        signup: 10,
    },
    {
        id: 2,
        img: imageSrc,
        name: '2025.6.5大志愿者"护航梦想路"',
        status: '进行中',
        target: 100,
        signup: 43,
    },
    {
        id: 3,
        img: imageSrc,
        name: '福安志愿服务队',
        status: '进行中',
        target: 30,
        signup: 7,
    },
    {
        id: 4,
        img: imageSrc,
        name: '"情暖端午·守护成长"五个一活动',
        status: '进行中',
        target: 100,
        signup: 20,
    },
    {
        id: 5,
        img: imageSrc,
        name: '"棕叶暖邻里 端午传真情"社区活动',
        status: '进行中',
        target: 2,
        signup: 0,
    },
    {
        id: 6,
        img: imageSrc,
        name: '多方携手植新绿，共筑生态新家园',
        status: '进行中',
        target: 50,
        signup: 6,
    },
    {
        id: 7,
        img: imageSrc,
        name: '"清捡垃圾 美化环境"志愿服务',
        status: '进行中',
        target: 50,
        signup: 6,
    },
    {
        id: 8,
        img: imageSrc,
        name: '土右义工"文明小屋"项目',
        status: '进行中',
        target: 100,
        signup: 7,
    }
]);

const pageSize = 8;
const currentPage = ref(1);
const searchText = ref('');
const joinDialogVisible = ref(false);
const joinForm = ref({ position: '' });
const joinPositions = ref(['志愿者', '宣传员']);

// --- Computed Properties ---
const filteredProjects = computed(() => {
    if (!searchText.value) {
        return projects.value;
    }
    return projects.value.filter(item => item.name.includes(searchText.value));
});

const pageData = computed(() => {
    const start = (currentPage.value - 1) * pageSize;
    const end = start + pageSize;
    return filteredProjects.value.slice(start, end);
});

// --- Methods ---
const handleSearch = () => {
    currentPage.value = 1;
};

const openJoinDialog = (item) => {
    joinDialogVisible.value = true;
    joinForm.value = { position: '' };
    // You can customize positions based on the item if needed
    // For example: joinPositions.value = item.positions || ['志愿者', '宣传员', '组织者'];
};

const submitJoin = () => {
    joinDialogVisible.value = false;
    if (window.$message) {
        window.$message.success('报名成功！');
    } else {
        alert('报名成功！');
    }
};
</script>

<style scoped>
.projects-more-page {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.06);
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
  box-shadow: 0 2px 8px 0 rgba(255,0,0,0.06);
  padding: 0;
  transition: all 0.3s ease-in-out;
}
.project-card:hover {
  box-shadow: 0 4px 16px 0 rgba(255,0,0,0.12);
  transform: translateY(-5px);
}
.project-img-wrap {
  position: relative;
  width: 100%;
  padding-top: 56.25%; /* 16:9 Aspect Ratio */
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
:deep(.el-pagination) {
    justify-content: center;
}
</style>
