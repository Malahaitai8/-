<template>
  <el-card>
    <div class="header">
      <h1>添加培训成员</h1>
    </div>
    <el-table :data="volunteers" style="width: 100%; margin-top: 100px">
      <el-table-column prop="id" label="ID" width="200">
      </el-table-column>
      <el-table-column prop="name" label="姓名" width="200">
      </el-table-column>
      <el-table-column prop="telephone" label="联系方式" width="200">
      </el-table-column>
      <el-table-column prop="totalServiceHours" label="志愿总时长" width="200">
      </el-table-column>
      <el-table-column prop="condition" label="培训状态" width="200">
      </el-table-column>
      <el-table-column prop="time" label="是否空闲" width="200">
      </el-table-column>
      <el-table-column label="操作" width="400">
        <template v-slot="scope">
          <el-button type="primary" size="mini" @click="viewDetails(scope.row)">详细信息</el-button>
          <el-button type="primary" size="mini" :disabled="scope.row.condition === '在该培训中' || scope.row.time === '该时段已有活动或培训'" @click="join(scope.row)">邀请加入</el-button>        </template>
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
        { id: '1', name: '张三', telephone: "13738393933", totalServiceHours: '200小时', condition:"在该培训中" , time: "该时段没有活动或培训"},
        { id: '2', name: '李四', telephone: "13738393933", totalServiceHours: '150小时', condition:"不在该培训中" , time: "该时段没有活动或培训"},
        { id: '3', name: '王五', telephone: "13738393933", totalServiceHours: '100小时', condition:"不在该培训中" , time: "该时段已有活动或培训"}
      ]
    };
  },
  methods: {
    home() {
      this.$router.push('/');
    },
    viewDetails(row) {
      console.log('查看详细信息：', row);
      this.$router.push('/detailed-volunteer-info-for-add-train');
    },
    join(row) {
      console.log('加入培训：', row);
      this.$message.success('成功将该成员加入到当前培训中！');
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

.el-button {
  margin-right: 10px; /* 添加按钮之间的间距 */
}
</style>