<template>
  <el-card>
    <div class="header">
      <h1>申请培训</h1>
    </div>
    <el-form ref="form" label-width="200px" style="margin-top: 100px">
      <el-form-item label="志愿培训名称">
        <el-input v-model="train.name"></el-input>
      </el-form-item>
      <el-form-item label="志愿培训类型">
        <el-select v-model="train.theme" placeholder="请选择主题">
          <el-option
              v-for="theme in themes"
              :key="theme"
              :value="theme">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="志愿培训开始时间">
        <el-input v-model="train.beginTime"></el-input>
      </el-form-item>
      <el-form-item label="志愿培训结束时间">
        <el-input v-model="train.endTime"></el-input>
      </el-form-item>
      <el-form-item label="志愿培训时段">
        <el-input v-model="train.duration"></el-input>
      </el-form-item>
      <el-form-item label="培训地点">
        <el-input v-model="train.place"></el-input>
      </el-form-item>
      <el-form-item label="招募人数">
        <el-input v-model="train.numberOfRecruits"></el-input>
      </el-form-item>
      <el-form-item label="负责人联系方式">
        <el-input v-model="train.telephone"></el-input>
      </el-form-item>
    </el-form>
    <el-button type="primary" @click="home">返回主页</el-button>
    <el-button type="primary" @click="add">添加培训成员</el-button>
    <el-button type="primary" @click="apply">申请志愿培训</el-button>
  </el-card>
</template>

<script>
export default {
  data() {
    return {
      train: {
        name: "XXX",
        theme: '', // 默认值为空字符串，而不是数组
        beginTime: "2025-5-20",
        endTime: "2025-5-30",
        duration:"2025-5-25 16:00-17:00",
        place: "北京交通大学",
        numberOfRecruits: 100,
        telephone: "13828283929"
      },
      themes: ['组织内部培训', '特殊岗位培训', '志愿活动培训'] // 预定义的主题选项
    };
  },
  methods: {
    home() {
      this.$router.push('/organization-home');
    },
    add() {
      this.$router.push('/train-add-member');
    },
    apply() {
      this.$message.success('志愿培训申请成功！');
    }
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
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  z-index: 1000;
}
.el-input {
  width: 100%; /* 使输入框宽度为100% */
}
</style>