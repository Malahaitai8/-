<template>
  <el-card>
    <div class="header">
      <h1>人员管理</h1>
    </div>

    <el-form ref="form" label-width="200px" style="margin-top: 100px">
      <el-form-item label="志愿者ID">
        <el-input v-model="volunteers.id" disabled></el-input>
      </el-form-item>
      <el-form-item label="志愿者姓名">
        <el-input v-model="volunteers.name" disabled></el-input>
      </el-form-item>
      <el-form-item label="志愿者联系方式">
        <el-input v-model="volunteers.telephone" disabled></el-input>
      </el-form-item>
      <el-form-item label="志愿总时长">
        <el-input v-model="volunteers.totalServiceHours" disabled></el-input>
      </el-form-item>
      <el-form-item label="国家">
        <el-input v-model="volunteers.country" disabled></el-input>
      </el-form-item>
      <el-form-item label="民族">
        <el-input v-model="volunteers.ethnicity" disabled></el-input>
      </el-form-item>
      <el-form-item label="政治面貌">
        <el-input v-model="volunteers.politicalStatus" disabled></el-input>
      </el-form-item>
      <el-form-item label="最高学历">
        <el-input v-model="volunteers.education" disabled></el-input>
      </el-form-item>
      <el-form-item label="服务地区">
        <el-input v-model="volunteers.serviceArea" disabled></el-input>
      </el-form-item>
      <el-form-item label="从业情况">
        <el-input v-model="volunteers.occupation" disabled></el-input>
      </el-form-item>
      <el-form-item label="服务类型">
        <el-input v-model="volunteers.serviceType" disabled></el-input>
      </el-form-item>
      <el-form-item label="志愿者综合积分">
        <el-input v-model="volunteers.rating" disabled></el-input>
      </el-form-item>
      <el-form-item label="账户认证状态">
        <el-input v-model="volunteers.accountStatus" disabled></el-input>
      </el-form-item>
    </el-form>
    <el-button type="primary" @click="home" style="margin-top: 20px">返回主页</el-button>
    <el-button type="primary" @click="add" style="margin-top: 20px">返回已报名页</el-button>
  </el-card>
</template>

<script>
export default {
  data() {
    return {
      volunteers:
          { id: '1',
            name: '张三丰',
            telephone: "13738393933",
            totalServiceHours: '200小时',
            country: '中国',
            ethnicity: '汉族',
            politicalStatus: '党员',
            education: '本科',
            serviceArea: '北京',
            occupation: '学生',
            serviceType: '教育',
            rating: 4.5,
            accountStatus: '已认证' },

    };
  },
  methods: {
    home() {
      this.$router.push('/');
    },
    add() {
      this.$router.push('/add-member-for-activity');
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