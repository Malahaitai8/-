<template>
  <div class="teams-more-page">
    <div class="teams-more-title">参加更多队伍</div>
    <div style="margin-bottom: 20px; display: flex; align-items: center;">
      <el-input v-model="searchText" placeholder="请输入队伍名称" style="width: 240px; margin-right: 12px;" clearable />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>
    <el-row :gutter="24">
      <el-col v-for="item in pageData" :key="item.id" :span="6" class="team-card-col">
        <el-card class="team-card">
          <div class="team-img-wrap">
            <img :src="item.img" class="team-img" />
          </div>
          <div class="team-name">{{ item.name }}</div>
          <div class="team-info-row">
            <span>队长：{{ item.leader }}</span>
            <span>人数：{{ item.count }}</span>
          </div>
          <div class="team-action-row">
            <el-button type="primary" size="small" @click="handleApply(item)">申请加入</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-pagination
      style="margin-top: 32px; text-align: center;"
      background
      layout="prev, pager, next, jumper"
      :total="teams.length"
      :page-size="pageSize"
      v-model:current-page="currentPage"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
// ✅ Correct way to import static assets like images
import imageSrc from '@/assets/image.jpeg';

// --- Reactive Data ---
const teams = ref([
  {
    id: 1,
    // ✅ Use the imported image variable here
    img: imageSrc,
    name: '社区志愿服务队',
    leader: '李老师',
    count: 32
  },
  {
    id: 2,
    img: imageSrc,
    name: '城市环保志愿团',
    leader: '王队长',
    count: 28
  },
  {
    id: 3,
    img: imageSrc,
    name: '红十字志愿服务队',
    leader: '赵主任',
    count: 40
  },
  {
    id: 4,
    img: imageSrc,
    name: '青年志愿者协会',
    leader: '钱同学',
    count: 22
  }
  // You can add more teams here for testing pagination
]);

const pageSize = 8;
const currentPage = ref(1);
const searchText = ref('');

// --- Computed Properties ---
const filteredTeams = computed(() => {
  if (!searchText.value) {
    return teams.value;
  }
  // Filter based on the team name including the search text
  return teams.value.filter(item => item.name.includes(searchText.value));
});

const pageData = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  const end = start + pageSize;
  return filteredTeams.value.slice(start, end);
});

// --- Methods ---
const handleSearch = () => {
  // When a new search is performed, reset to the first page
  currentPage.value = 1;
};

const handleApply = (item) => {
  // This approach is fine, it uses a global message if available, otherwise a standard alert.
  // In larger apps, you might inject a notification service instead.
  if (window.$message) {
    window.$message.success(`已申请加入“${item.name}”！`);
  } else {
    alert(`已申请加入“${item.name}”！`);
  }
};
</script>

<style scoped>
.teams-more-page {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.06);
  padding: 24px 16px 40px 16px;
  min-height: 600px;
}
.teams-more-title {
  font-size: 24px;
  font-weight: bold;
  color: #ff0000;
  margin-bottom: 24px;
  text-align: left;
}
.team-card-col {
  margin-bottom: 24px;
}
.team-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px 0 rgba(255,0,0,0.06);
  padding: 0;
  transition: box-shadow 0.2s;
}
.team-card:hover {
  box-shadow: 0 4px 16px 0 rgba(255,0,0,0.12);
}
.team-img-wrap {
  position: relative;
  width: 100%;
  height: 120px;
  overflow: hidden;
  background: #f8f8f8;
  display: flex;
  align-items: center;
  justify-content: center;
}
.team-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.team-status {
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
.team-status.inactive {
  background: #409EFF;
}
.team-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin: 16px 0 8px 0;
  text-align: center;
  min-height: 40px;
}
.team-info-row {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 13px;
  margin: 0 12px 8px 12px;
}
.team-action-row {
  display: flex;
  justify-content: center;
  margin: 12px 0 0 0;
}
</style>