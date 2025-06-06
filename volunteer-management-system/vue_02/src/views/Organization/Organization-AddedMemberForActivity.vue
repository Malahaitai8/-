<template>
  <el-card>
    <div class="header">
      <h1>志愿活动人员管理</h1>
    </div>
    <el-button
        type="primary"
        :class="{ 'is-active': activeRoute === '/add-member-for-activity' }"
        @click="add"
        style="margin-top: 100px"
    >已报名志愿者</el-button>
    <el-button
        type="primary"
        :class="{ 'is-active': activeRoute === '/added-member-for-activity' }"
        @click="added"
        style="margin-top: 100px"
    >已招募志愿者</el-button>
    <el-table :data="volunteers" style="width: 1500px; margin-top: 20px">
      <el-table-column prop="id" label="ID" width="200">
      </el-table-column>
      <el-table-column prop="name" label="姓名" width="200">
      </el-table-column>
      <el-table-column prop="telephone" label="联系方式" width="200">
      </el-table-column>
      <el-table-column prop="totalServiceHours" label="志愿总时长" width="200">
      </el-table-column>
      <el-table-column label="操作" width="300">
        <template v-slot="scope">
          <el-button type="primary" size="mini" @click="viewDetails(scope.row)">详细信息</el-button>
          <el-button type="primary" size="mini" style="margin-left: 10px;" @click="approve(scope.row)">剔除该成员</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-button type="primary" @click="home" style="margin-top: 20px">返回主页</el-button>
    <el-button type="primary" @click="record" style="margin-top: 20px">返回记录页</el-button>
  </el-card>
</template>

<script>
export default {
  data() {
    return {
      volunteers: [
        { id: '4', name: '王一', telephone: "13738393933", totalServiceHours: '200小时' },
        { id: '5', name: '王二', telephone: "13738393933", totalServiceHours: '150小时' },
        { id: '6', name: '王三', telephone: "13738393933", totalServiceHours: '100小时' }
      ]
    };
  },
  computed: {
    activeRoute() {
      return this.$route.path;
    }
  },
  methods: {
    home() {
      this.$router.push('/');
    },
    add() {
      this.$router.push('/add-member-for-activity');
    },
    added() {
      this.$router.push('/added-member-for-activity');
    },
    record() {
      this.$router.push('/view-activity-records');
    },
    viewDetails(row) {
      console.log('查看详细信息：', row);
      this.$router.push('/detailed-info-for-added-activity');
    },
    approve(row) {
      console.log('允许加入：', row);
      this.$message.success('该成员已剔除！');
    }
  }
};
</script>

<style scoped>
.header {
  background-color: #ff3333; /* 鲜红色背景 */
  color: white;
  padding: 10px 20px;
  margin-bottom: 100px;
  text-align: left;
  position: absolute; /* 修改为相对定位 */
  top: 0;
  left: 0;
  width: 97.65%;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  z-index: 1000;
}

.is-active {
  background-color: #ff0000; /* 高亮颜色 */
  color: white;
}

.el-button {
  margin-right: 10px; /* 添加按钮之间的间距 */
}
</style>