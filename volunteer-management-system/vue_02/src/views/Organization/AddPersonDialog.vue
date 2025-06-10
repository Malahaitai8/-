<template>
<!--  <el-dialog v-model="addPersonDialogVisible" title="添加系统外人员">-->
<!--    <el-form ref="formRef" :rules="formRules" :model="formData" class="register-form">-->
<!--      &lt;!&ndash; 左侧表单项 &ndash;&gt;-->
<!--      <div class="form-column">-->
<!--        &lt;!&ndash; 用户名输入框 &ndash;&gt;-->
<!--        <el-form-item label="用户名" prop="username" label-width="80px">-->
<!--          <el-input size="large" v-model="formData.username" autocomplete="off" placeholder="请输入用户名" prefix-icon="User" />-->
<!--        </el-form-item>-->
<!--        &lt;!&ndash; 密码输入框 &ndash;&gt;-->
<!--        <el-form-item label="密码" prop="password" label-width="80px">-->
<!--          <el-input size="large" type="password" v-model="formData.password" autocomplete="off" placeholder="请输入密码" prefix-icon="Lock" />-->
<!--        </el-form-item>-->
<!--        &lt;!&ndash; 确认密码输入框 &ndash;&gt;-->
<!--        <el-form-item label="确认密码" prop="confirmPassword" label-width="80px">-->
<!--          <el-input size="large" type="password" v-model="formData.confirmPassword" autocomplete="off" placeholder="请输入确认密码" prefix-icon="Lock" />-->
<!--        </el-form-item>-->
<!--        &lt;!&ndash; 真实姓名输入框 &ndash;&gt;-->
<!--        <el-form-item label="真实姓名" prop="name" label-width="80px">-->
<!--          <el-input size="large" v-model="formData.name" autocomplete="off" placeholder="请输入真实姓名" prefix-icon="User" />-->
<!--        </el-form-item>-->
<!--        &lt;!&ndash; 性别输入框 &ndash;&gt;-->
<!--        <el-form-item label="性别" prop="gender" label-width="80px">-->
<!--          <el-input size="large" v-model="formData.gender" autocomplete="off" placeholder="请输入性别" prefix-icon="User" />-->
<!--        </el-form-item>-->
<!--        &lt;!&ndash; 手机号输入框 &ndash;&gt;-->
<!--        <el-form-item label="手机号" prop="phone" label-width="80px">-->
<!--          <el-input size="large" v-model="formData.phone" autocomplete="off" placeholder="请输入手机号" prefix-icon="Phone" />-->
<!--        </el-form-item>-->
<!--        &lt;!&ndash; 身份证号输入框 &ndash;&gt;-->
<!--        <el-form-item label="身份证号" prop="idCard" label-width="80px">-->
<!--          <el-input size="large" v-model="formData.idCard" autocomplete="off" placeholder="请输入身份证号" prefix-icon="User" />-->
<!--        </el-form-item>-->
<!--      </div>-->
<!--      &lt;!&ndash; 右侧表单项 &ndash;&gt;-->
<!--      <div class="form-column">-->
<!--        &lt;!&ndash; 国籍输入框 &ndash;&gt;-->
<!--        <el-form-item label="国籍" prop="country" label-width="80px">-->
<!--          <el-input size="large" v-model="formData.country" autocomplete="off" placeholder="请输入国籍" prefix-icon="User" />-->
<!--        </el-form-item>-->
<!--        &lt;!&ndash; 民族输入框 &ndash;&gt;-->
<!--        <el-form-item label="民族" prop="ethnicity" label-width="80px">-->
<!--          <el-input size="large" v-model="formData.ethnicity" autocomplete="off" placeholder="请输入民族" prefix-icon="User" />-->
<!--        </el-form-item>-->
<!--        &lt;!&ndash; 政治面貌输入框 &ndash;&gt;-->
<!--        <el-form-item label="政治面貌" prop="politicalStatus" label-width="80px">-->
<!--          <el-input size="large" v-model="formData.politicalStatus" autocomplete="off" placeholder="请输入政治面貌" prefix-icon="User" />-->
<!--        </el-form-item>-->
<!--        &lt;!&ndash; 最高学历输入框 &ndash;&gt;-->
<!--        <el-form-item label="最高学历" prop="highestEducation" label-width="80px">-->
<!--          <el-input size="large" v-model="formData.highestEducation" autocomplete="off" placeholder="请输入最高学历" prefix-icon="User" />-->
<!--        </el-form-item>-->
<!--        &lt;!&ndash; 从业情况输入框 &ndash;&gt;-->
<!--        <el-form-item label="从业情况" prop="employmentStatus" label-width="80px">-->
<!--          <el-input size="large" v-model="formData.employmentStatus" autocomplete="off" placeholder="请输入从业情况" prefix-icon="User" />-->
<!--        </el-form-item>-->
<!--        &lt;!&ndash; 服务区域输入框 &ndash;&gt;-->
<!--        <el-form-item label="服务区域" prop="serviceArea" label-width="80px">-->
<!--          <el-input size="large" v-model="formData.serviceArea" autocomplete="off" placeholder="请输入服务区域" prefix-icon="User" />-->
<!--        </el-form-item>-->
<!--        &lt;!&ndash; 服务类别输入框 &ndash;&gt;-->
<!--        <el-form-item label="服务类别" prop="serviceCategory" label-width="80px">-->
<!--          <el-input size="large" v-model="formData.serviceCategory" autocomplete="off" placeholder="请输入服务类别" prefix-icon="User" />-->
<!--        </el-form-item>-->
<!--      </div>-->
<!--    </el-form>-->
<!--    <div slot="footer" class="dialog-footer">-->
<!--      <el-button @click="addPersonDialogVisible = false">取消</el-button>-->
<!--      <el-button type="primary" @click="submitForm">添加该成员</el-button>-->
<!--    </div>-->
<!--  </el-dialog>-->
</template>

<script>
import { ref } from "vue";
import axios from "axios";
import { ElMessage } from "element-plus";

export default {
  props: {
    addPersonDialogVisible: Boolean
  },
  emits: ['update:addPersonDialogVisible'],
  setup(props, { emit }) {
    const formData = ref({
      username: "",
      password: "",
      confirmPassword: "",
      name: "",
      gender: "",
      phone: "",
      idCard: "",
      country: "",
      ethnicity: "",
      politicalStatus: "",
      highestEducation: "",
      employmentStatus: "",
      serviceArea: "",
      serviceCategory: ""
    });

    const formRules = ref({
      username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
      password: [{ required: true, message: "请输入密码", trigger: "blur" }],
      confirmPassword: [
        { required: true, message: "请确认密码", trigger: "blur" },
        (rule, value, callback) => {
          if (!value) {
            callback(new Error("请再次确认密码"));
          } else if (value !== formData.value.password) {
            callback(new Error("两次输入的密码不一致"));
          } else {
            callback();
          }
        }
      ],
      name: [{ required: true, message: "请输入真实姓名", trigger: "blur" }],
      gender: [{ required: true, message: "请输入性别", trigger: "blur" }],
      phone: [{ required: true, message: "请输入手机号", trigger: "blur" }],
      idCard: [{ required: true, message: "请输入身份证号", trigger: "blur" }],
      country: [{ required: true, message: "请输入国籍", trigger: "blur" }],
      ethnicity: [{ required: true, message: "请输入民族", trigger: "blur" }],
      politicalStatus: [{ required: true, message: "请输入政治面貌", trigger: "blur" }],
      highestEducation: [{ required: true, message: "请输入最高学历", trigger: "blur" }],
      employmentStatus: [{ required: true, message: "请输入从业情况", trigger: "blur" }],
      serviceArea: [{ required: true, message: "请输入服务区域", trigger: "blur" }],
      serviceCategory: [{ required: true, message: "请输入服务类别", trigger: "blur" }]
    });

    const submitForm = () => {
      const formRef = ref(null);
      formRef.value.validate(async (valid) => {
        if (valid) {
          try {
            const response = await axios.post("/volunteerOrganizationJoin/addMember", formData.value);
            if (response.data.success) {
              ElMessage.success("成功添加成员");
              emit('update:addPersonDialogVisible', false);
            } else {
              ElMessage.error(response.data.message);
            }
          } catch (error) {
            console.error("添加成员失败：", error);
            ElMessage.error("添加成员失败，请稍后再试");
          }
        } else {
          ElMessage.error("表单验证失败");
        }
      });
    };

    return {
      formData,
      formRules,
      submitForm
    };
  }
};
</script>

