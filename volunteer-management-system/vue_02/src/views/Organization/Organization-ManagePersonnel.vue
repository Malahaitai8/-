<template>
  <el-card>
    <div class="header">
      <h1>人员管理</h1>
      <el-input
          v-model="searchQuery"
          placeholder="搜索人员"
          style="width: 200px; margin-left: auto;"
      ></el-input>
    </div>
    <el-button
        type="primary"
        :class="{ 'is-active': activeRoute === '/manage-personnel' }"
        @click="member"
        style="margin-top: 100px"
    >正式成员</el-button>
    <el-button
        type="primary"
        :class="{ 'is-active': activeRoute === '/join-member' }"
        @click="join"
        style="margin-top: 100px"
    >申请加入</el-button>
    <el-table :data="volunteers" style="width: 80%; margin-top: 20px">
      <el-table-column prop="id" label="ID" width="200">
      </el-table-column>
      <el-table-column prop="name" label="姓名" width="200">
      </el-table-column>
      <el-table-column prop="telephone" label="联系方式" width="200">
      </el-table-column>
      <el-table-column prop="totalServiceHours" label="志愿总时长" width="200">
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template v-slot="scope">
          <el-button type="primary" size="mini" @click="viewDetails(scope.row)">详细信息</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-button type="primary" @click="home" style="margin-top: 20px">返回主页</el-button>
  </el-card>
</template>

<script>
export default {
  data() {
    return {
      volunteers: [
        { id: '1', name: '张三', telephone: "13738393933", totalServiceHours: '200小时' },
        { id: '2', name: '李四', telephone: "13738393933", totalServiceHours: '150小时' },
        { id: '3', name: '王五', telephone: "13738393933", totalServiceHours: '100小时' }
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
      this.$router.push('/organization-home');
    },
    member() {
      this.$router.push('/manage-personnel');
    },
    join() {
      this.$router.push('/join-member');
    },
    viewDetails(row) {
      console.log('查看详细信息：', row);
      this.$router.push('/detailed-volunteer-info');
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
  position: absolute;
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