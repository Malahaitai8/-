<template>
  <el-card>
    <div class="header">
      <h1>申请志愿活动</h1>
    </div>
    <el-form ref="form" label-width="200px" style="margin-top: 100px">
      <el-form-item label="志愿活动名称">
        <template #label>
          <span class="required">*</span> 志愿活动名称
        </template>
        <el-input v-model="activity.name"></el-input>
      </el-form-item>
      <el-form-item label="志愿活动时段">
        <template #label>
          <span class="required">*</span> 志愿活动时段
        </template>
        <el-input v-model="activity.duration"></el-input>
      </el-form-item>
      <el-form-item label="志愿活动开始时间">
        <template #label>
          <span class="required">*</span> 志愿活动开始时间
        </template>
        <el-input v-model="activity.beginTime"></el-input>
      </el-form-item>
      <el-form-item label="志愿活动结束时间">
        <template #label>
          <span class="required">*</span> 志愿活动结束时间
        </template>
        <el-input v-model="activity.endTime"></el-input>
      </el-form-item>
      <el-form-item label="活动地点">
        <template #label>
          <span class="required">*</span> 活动地点
        </template>
        <el-input v-model="activity.place"></el-input>
      </el-form-item>
      <el-form-item label="招募人数">
        <template #label>
          <span class="required">*</span> 招募人数
        </template>
        <el-input v-model="activity.numberOfRecruits"></el-input>
      </el-form-item>
      <el-form-item label="负责人联系方式">
        <template #label>
          <span class="required">*</span> 负责人联系方式
        </template>
        <el-input v-model="activity.telephone"></el-input>
      </el-form-item>
    </el-form>
    <el-button type="primary" @click="home">返回主页</el-button>
    <el-button type="primary" @click="showCreateDialog = true">创建岗位</el-button>
    <el-dialog v-model="showCreateDialog" title="创建岗位">
      <el-form>
        <div v-for="(position, index) in positions" :key="index">
          <el-form-item label="岗位名称">
            <el-input v-model="position.name"></el-input>
          </el-form-item>
          <el-form-item label="岗位时间段">
            <div v-for="(slot, slotIndex) in position.timeSlots" :key="slotIndex" style="margin-bottom: 10px;">
              <el-date-picker
                  v-model="position.timeSlots[slotIndex]"
                  type="datetime"
                  placeholder="选择日期时间">
              </el-date-picker>
            </div>
          </el-form-item>
          <el-button type="text" @click="removePosition(index)">删除岗位</el-button>
          <el-button type="text" @click="addTimeSlot(index)">新增时间段</el-button>
        </div>
        <el-button @click="addPosition">新增岗位</el-button>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCreateDialog = false">取消</el-button>
          <el-button type="primary" @click="applyAndCreatePositions">申请创建活动</el-button>
        </span>
      </template>
    </el-dialog>
  </el-card>
</template>

<script>
export default {
  data() {
    return {
      activity: {
        name: "XXX",
        work: "内场、外场、检票",
        beginTime: "2025-5-20",
        endTime: "2025-5-30",
        duration: "2025-5-25 16:00-17:00",
        place: "北京交通大学",
        numberOfRecruits: 100,
        telephone: 13828283929,
      },
      showCreateDialog: false,
      positions: [
        {
          name: "",
          timeSlots: [""],
        },
      ],
    };
  },
  methods: {
    home() {
      this.$router.push("/organization");
    },
    // 原 apply 方法已删除
    addPosition() {
      this.positions.push({
        name: "",
        timeSlots: [""],
      });
    },
    removePosition(index) {
      this.positions.splice(index, 1);
    },
    addTimeSlot(index) {
      this.positions[index].timeSlots.push("");
    },
    // 将原 savePositions 和 apply 的逻辑合并到此方法
    applyAndCreatePositions() {
      // 1. 保存和处理岗位数据
      this.positions = this.positions.map((position) => ({
        ...position,
        timeSlots: position.timeSlots.filter((slot) => slot !== ""),
      }));
      this.showCreateDialog = false;

      // 在这里可以添加将 activity 和 positions 数据发送到后端的逻辑

      // 2. 执行申请（页面跳转）
      this.$router.push("/apply-activity");
    },
  },
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

/* 添加必填项标记样式 */
.required {
  color: red;
  margin-right: 5px;
}
</style>