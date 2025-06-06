<template>
  <el-card>
    <div class="header">
      <h1>志愿培训记录</h1>
    </div>
    <el-table :data="activity" style="width: 100%; margin-top: 100px">
      <el-table-column prop="id" label="志愿培训ID" width="200">
      </el-table-column>
      <el-table-column prop="name" label="志愿培训名称" width="200">
      </el-table-column>
      <el-table-column prop="telephone" label="负责人联系方式" width="200">
      </el-table-column>
      <el-table-column prop="beginTime" label="开始时间" width="200">
      </el-table-column>
      <el-table-column prop="endTime" label="结束时间" width="200">
      </el-table-column>
      <el-table-column prop="duration" label="志愿培训时段" width="200">
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
      activity: [
        { id: '1', name: '急救', theme:'组织内部培训', telephone: "13738393933", beginTime: "2025-5-10" , endTime: "2025-5-30" ,duration:"2025-5-25 16:00-17:00"},
        { id: '2', name: '活动培训', theme:'志愿活动培训', telephone: "13738393933", beginTime: "2025-5-10" , endTime: "2025-5-30" ,duration:"2025-5-25 16:00-17:00"},
        { id: '3', name: '岗位培训', theme:'特殊岗位培训', telephone: "13738393933", beginTime: "2025-5-10" , endTime: "2025-5-30" ,duration:"2025-5-25 16:00-17:00"}
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
    viewDetails(){
      this.$router.push('/detailed-train-info');
    },
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