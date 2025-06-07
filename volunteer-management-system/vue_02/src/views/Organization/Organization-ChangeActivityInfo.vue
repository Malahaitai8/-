<template>
  <el-card>
    <div class="header">
      <h1>志愿活动信息修改</h1>
    </div>

    <el-form ref="form" label-width="200px" style="margin-top: 100px">
      <el-form-item label="志愿活动ID">
        <el-input v-model="activity.id"></el-input>
      </el-form-item>
      <el-form-item label="志愿活动名称">
        <el-input v-model="activity.name"></el-input>
      </el-form-item>
      <el-form-item label="志愿活动开始时间">
        <el-input v-model="activity.beginTime"></el-input>
      </el-form-item>
      <el-form-item label="志愿活动结束时间">
        <el-input v-model="activity.endTime"></el-input>
      </el-form-item>
      <el-form-item label="志愿活动地点">
        <el-input v-model="activity.place"></el-input>
      </el-form-item>
      <el-form-item label="招募人数">
        <el-input v-model="activity.NumberOfRecruits"></el-input>
      </el-form-item>
      <el-form-item label="录取人数">
        <el-input v-model="activity.NumberOfAdmission"></el-input>
      </el-form-item>
      <el-form-item label="志愿活动状态">
        <el-input v-model="activity.condition"></el-input>
      </el-form-item>
      <el-form-item label="志愿活动创建时间">
        <el-input v-model="activity.createTime"></el-input>
      </el-form-item>
      <el-form-item label="审核管理员ID">
        <el-input v-model="activity.auditID"></el-input>
      </el-form-item>
      <el-form-item label="负责人联系方式">
        <el-input v-model="activity.telephone"></el-input>
      </el-form-item>
      <el-form-item label="志愿活动总时长">
        <el-input v-model="activity.totalTime"></el-input>
      </el-form-item>
      <el-form-item label="活动评分">
        <el-input v-model="activity.score"></el-input>
      </el-form-item>
    </el-form>
    <el-button type="primary" @click="home" style="margin-top: 20px">返回主页</el-button>
    <el-button type="primary" @click="Info" style="margin-top: 20px">返回详细信息页</el-button>
    <el-button type="primary" @click="success" style="margin-top: 20px">确定修改</el-button>
  </el-card>
</template>

<script>
export default {
  data() {
    return {
      activity:
          {
            id: 1,
            name: '同心小学',
            beginTime:"2025-5-10",
            endTime:"2025-5-30",
            place:"北京交通大学",
            NumberOfRecruits:"100",
            NumberOfAdmission:"10",
            condition:"运行中",
            createTime:"2025-5-5",
            auditID: 1,
            telephone: "13738393933",
            totalTime: 20,
            score: 5,
          },

    };
  },
  methods: {
    home() {
      this.$router.push('/organization-home');
    },
    Info() {
      this.$router.push('/detailed-activity-info');
    },
    success() {
      this.$message.success('活动信息修改成功！');
    },
  }
};
</script>

<style scoped>
.header {
  background-color: #ff3333; /* 鲜红色背景 */
  color: white;
  padding: 10px 20px;
  margin-bottom: 20px;
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

.el-button {
  margin-right: 10px; /* 添加按钮之间的间距 */
}
</style>