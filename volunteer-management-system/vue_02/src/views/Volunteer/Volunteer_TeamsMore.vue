<template>
  <div class="teams-more-page">
    <div class="teams-more-title">参加更多队伍</div>
    <div style="margin-bottom: 20px; display: flex; align-items: center;">
      <el-input v-model="searchText" placeholder="请输入队伍名称" style="width: 240px; margin-right: 12px;" clearable @keyup.enter="handleSearch"/>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>
    <el-row :gutter="24" v-loading="isLoading" element-loading-text="加载中...">
      <el-col v-for="item in pageData" :key="item.orgId" :span="6" class="team-card-col">
        <el-card class="team-card">
          <!-- 移除了图片显示。如果需要图片，后端Organization实体和数据库需增加相应字段 -->
          <!-- <div class="team-img-wrap">
            <img :src="item.img" class="team-img" />
          </div> -->
          <div class="team-name">{{ item.orgName }}</div>
          <div class="team-info-row">
            <span>规模：{{ item.orgScale }} 人</span>
            <span>评分：{{ item.orgRating }} 分</span>
          </div>
          <div class="team-action-row">
            <el-button type="primary" size="small" @click="handleApply(item)">申请加入</el-button>
            <el-button type="primary" size="small" @click="showDetail(item)">查看详情</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <div v-if="!pageData.length && !isLoading" class="empty-box">
      <img src="https://img.alicdn.com/imgextra/i4/O1CN01v7Qw1B1QwQwQwQw_!!6000000002007-2-tps-200-200.png" alt="empty" />
      <div>暂无更多队伍信息</div>
    </div>
    <el-pagination
        v-if="total > pageSize"
        style="margin-top: 32px; text-align: center;"
        background
        layout="prev, pager, next, jumper"
        :total="total"
        :page-size="pageSize"
        v-model:current-page="currentPage"
        @current-change="handlePageChange"
    />
    <el-dialog v-model="detailDialogVisible" title="队伍详情" width="500px">
      <el-descriptions :column="1" border v-loading="isDetailLoading">
        <el-descriptions-item label="组织ID">{{ detailData.orgId }}</el-descriptions-item>
        <el-descriptions-item label="组织名称">{{ detailData.orgName }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ detailData.contactPersonPhone }}</el-descriptions-item>
        <el-descriptions-item label="服务区域">{{ detailData.serviceRegion }}</el-descriptions-item>
        <el-descriptions-item label="组织规模">{{ detailData.orgScale }} 人</el-descriptions-item>
        <el-descriptions-item label="组织评分">{{ detailData.orgRating }} 分</el-descriptions-item>
        <el-descriptions-item label="账户状态">{{ detailData.orgAccountStatus }}</el-descriptions-item>
        <el-descriptions-item label="总服务时长">{{ detailData.totalServiceHours }} 小时</el-descriptions-item>
        <el-descriptions-item label="活动举办次数">{{ detailData.activityCount }} 次</el-descriptions-item>
        <el-descriptions-item label="培训举办次数">{{ detailData.trainingCount }} 次</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '@/utils/request'; // 假设这是您的请求工具
import { useUserStore } from '@/stores/userStore'; // 假设您有Pinia的用户store

// 移除了静态图片导入，因为Organization实体中没有对应的图片字段
// import imageSrc from '@/assets/image.jpeg';

// --- 响应式数据 ---
const userStore = useUserStore();
const teams = ref([]); // 存储从后端获取的当前页的所有可加入组织
const total = ref(0); // 存储可加入组织的总数，用于分页
const pageSize = 8; // 每页显示的卡片数量
const currentPage = ref(1);
const searchText = ref(''); // 搜索框文本
const detailDialogVisible = ref(false); // 详情弹窗的显示状态
const detailData = ref({}); // 详情弹窗中显示的数据
const isLoading = ref(false); // 主列表的加载状态
const isDetailLoading = ref(false); // 详情弹窗的加载状态

// --- 计算属性 ---
// pageData 现在直接绑定到 teams.value，因为后端已经进行了分页
const pageData = computed(() => teams.value);

// --- 方法 ---

/**
 * 从后端获取可加入的队伍列表。
 * 根据当前用户的volunteerId、搜索关键词、当前页码和每页大小进行查询。
 */
const fetchAvailableTeams = async () => {
  const volunteerId = userStore.detailedVolunteerInfo?.volunteerId;
  if (!volunteerId) {
    ElMessage.warning('请先登录以查看更多队伍。');
    teams.value = [];
    total.value = 0;
    return;
  }

  isLoading.value = true;
  try {
    const res = await request.get('/organization/availableForVolunteer', {
      params: {
        volunteerId: volunteerId,
        orgName: searchText.value,
        pageNum: currentPage.value,
        pageSize: pageSize,
      },
    });

    if (res.code === '200' && res.data) {
      teams.value = res.data.list; // 后端PageInfo返回的数据结构是{ list: [...], total: ... }
      total.value = res.data.total;
    } else {
      ElMessage.error(res.msg || '获取可加入队伍列表失败');
      teams.value = [];
      total.value = 0;
    }
  } catch (error) {
    ElMessage.error('网络错误，无法获取可加入队伍列表');
    console.error('Error fetching available teams:', error);
    teams.value = [];
    total.value = 0;
  } finally {
    isLoading.value = false;
  }
};

/**
 * 处理搜索操作。
 * 搜索时重置到第一页，并重新获取数据。
 */
const handleSearch = () => {
  currentPage.value = 1; // 搜索时重置到第一页
  fetchAvailableTeams();
};

/**
 * 处理分页页码变化。
 * 更新当前页码，并重新获取数据。
 * @param {number} newPage 新的页码
 */
const handlePageChange = (newPage) => {
  currentPage.value = newPage;
  fetchAvailableTeams();
};

/**
 * 处理申请加入队伍操作。
 * 向后端发送申请请求。
 * @param {object} item 申请加入的队伍（组织）对象
 */
const handleApply = async (item) => {
  try {
    await ElMessageBox.confirm(
      `确定要申请加入队伍 “${item.orgName}” 吗？`,
      '确认申请',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info',
      }
    );

    const payload = {
      volunteerId: userStore.detailedVolunteerInfo.volunteerId,
      orgId: item.orgId,
    };
    const res = await request.post('/volunteerOrganizationJoin/applyToJoin', payload);

    if (res.code === '200') {
      ElMessage.success('申请已提交，请等待队伍审核！');
      // 成功申请后，重新获取列表，已申请的队伍将不再显示
      fetchAvailableTeams();
    } else {
      ElMessage.error(res.msg || '申请加入失败');
    }
  } catch (error) {
    if (error !== 'cancel') { // 用户取消操作或网络错误
      ElMessage.error('操作取消或网络错误');
      console.error('Error applying to join team:', error);
    }
  }
};

/**
 * 显示队伍详情弹窗。
 * 从后端获取完整的组织详情。
 * @param {object} item 要查看详情的队伍（组织）对象
 */
const showDetail = async (item) => {
  isDetailLoading.value = true;
  detailDialogVisible.value = true;
  detailData.value = {}; // 清空之前的数据
  try {
    // API端点: GET /organization/selectByOrgId/{orgId}
    const res = await request.get(`/organization/selectByOrgId/${item.orgId}`);
    if (res.code === '200' && res.data) {
      detailData.value = res.data;
    } else {
      ElMessage.error(res.msg || '获取队伍详情失败');
      detailDialogVisible.value = false;
    }
  } catch (error) {
    ElMessage.error('网络错误，无法获取队伍详情');
    console.error('Error fetching organization details:', error);
    detailDialogVisible.value = false;
  } finally {
    isDetailLoading.value = false;
  }
};

// --- 生命周期钩子 & 监听器 ---
onMounted(() => {
  // 组件挂载时立即获取数据
  if (userStore.detailedVolunteerInfo?.volunteerId) {
    fetchAvailableTeams();
  }
});

// 监听 volunteerId 的变化（例如，用户登录/登出）
watch(() => userStore.detailedVolunteerInfo?.volunteerId, (newId, oldId) => {
  if (newId && newId !== oldId) {
    currentPage.value = 1; // 用户变化时重置页码
    fetchAvailableTeams();
  } else if (!newId && oldId) {
    // 用户登出，清空数据
    teams.value = [];
    total.value = 0;
  }
});

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
  height: 100%; /* 确保卡片高度一致 */
  display: flex;
  flex-direction: column;
}
.team-card:hover {
  box-shadow: 0 4px 16px 0 rgba(255,0,0,0.12);
}
/* 移除了图片相关的样式，因为Organization实体中没有对应的图片字段 */
/* .team-img-wrap {} */
/* .team-img {} */
.team-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin: 16px; /* 调整外边距 */
  text-align: center;
  min-height: 40px;
  flex-grow: 1; /* 允许名称占据可用空间 */
}
.team-info-row {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 13px;
  margin: 0 16px 8px 16px; /* 调整外边距 */
}
.team-action-row {
  display: flex;
  justify-content: center;
  padding-bottom: 16px; /* 按钮底部内边距 */
}
</style>
