<template>
  <div class="register-container">
    <div class="register-box">
      <div style="background-color: white; border-radius: 10px; padding: 30px; box-shadow: 0 0 10px rgb(90, 16, 6)">
        <div style="margin-bottom: 30px; color: #c32f1b; font-size: 20px; font-weight: bold; text-align: center">
          欢迎注册组织机构
        </div>
        <el-form ref="formRef" :rules="data.rules" :model="data.form" style="margin-right: 30px; margin-top: 20px">
          <!-- 组织名称 -->
          <el-form-item label="组织名称" prop="orgName" label-width="120px">
            <el-input size="large" v-model="data.form.orgName" autocomplete="off" placeholder="请输入组织名称" prefix-icon="OfficeBuilding" />
          </el-form-item>
          <!-- 登录用户名 -->
          <el-form-item label="登录用户名" prop="orgLoginUserName" label-width="120px">
            <el-input size="large" v-model="data.form.orgLoginUserName" autocomplete="off" placeholder="请输入登录用户名" prefix-icon="User" />
          </el-form-item>
          <!-- 登录密码 -->
          <el-form-item label="登录密码" prop="orgLoginPassword" label-width="120px">
            <el-input size="large" type="password" show-password v-model="data.form.orgLoginPassword" autocomplete="off" placeholder="请输入登录密码" prefix-icon="Lock" />
          </el-form-item>
          <!-- 确认密码 -->
          <el-form-item label="确认密码" prop="confirmPassword" label-width="120px">
            <el-input size="large" type="password" show-password v-model="data.form.confirmPassword" autocomplete="off" placeholder="请确认密码" prefix-icon="Lock" />
          </el-form-item>
          <!-- 负责人联系方式 -->
          <el-form-item label="负责人联系方式" prop="contactPersonPhone" label-width="120px">
            <el-input size="large" v-model="data.form.contactPersonPhone" autocomplete="off" placeholder="请输入负责人联系方式" prefix-icon="Phone" />
          </el-form-item>
          <!-- 服务区域 -->
          <el-form-item label="服务区域" prop="serviceRegion" label-width="120px">
            <el-select v-model="data.form.serviceRegion" placeholder="请选择服务区域" style="width: 100%;">
              <el-option label="北京" value="北京"></el-option>
              <el-option label="天津" value="天津"></el-option>
              <el-option label="上海" value="上海"></el-option>
              <el-option label="重庆" value="重庆"></el-option>
              <el-option label="河北" value="河北"></el-option>
              <el-option label="山西" value="山西"></el-option>
              <el-option label="辽宁" value="辽宁"></el-option>
              <el-option label="吉林" value="吉林"></el-option>
              <el-option label="黑龙江" value="黑龙江"></el-option>
              <el-option label="江苏" value="江苏"></el-option>
              <el-option label="浙江" value="浙江"></el-option>
              <el-option label="安徽" value="安徽"></el-option>
              <el-option label="福建" value="福建"></el-option>
              <el-option label="江西" value="江西"></el-option>
              <el-option label="山东" value="山东"></el-option>
              <el-option label="河南" value="河南"></el-option>
              <el-option label="湖北" value="湖北"></el-option>
              <el-option label="湖南" value="湖南"></el-option>
              <el-option label="广东" value="广东"></el-option>
              <el-option label="广西" value="广西"></el-option>
              <el-option label="海南" value="海南"></el-option>
              <el-option label="四川" value="四川"></el-option>
              <el-option label="贵州" value="贵州"></el-option>
              <el-option label="云南" value="云南"></el-option>
              <el-option label="西藏" value="西藏"></el-option>
              <el-option label="陕西" value="陕西"></el-option>
              <el-option label="甘肃" value="甘肃"></el-option>
              <el-option label="青海" value="青海"></el-option>
              <el-option label="台湾" value="台湾"></el-option>
              <el-option label="香港" value="香港"></el-option>
              <el-option label="澳门" value="澳门"></el-option>
            </el-select>
          </el-form-item>
          <!-- 组织规模 -->
          <el-form-item label="组织规模" prop="orgScale" label-width="120px">
            <el-input size="large" type="number" v-model.number="data.form.orgScale" autocomplete="off" placeholder="请输入组织人数规模" prefix-icon="UserGroup" />
          </el-form-item>
        </el-form>
        <div style="margin-top: 20px;">
          <el-button @click="organizationRegister" size="large" type="danger" style="width: 100%; color: white">
            注册
          </el-button>
        </div>
        <div style="text-align: right; margin-top: 15px">
          已有账号？请 <a style="color: #c32f1b; text-decoration: none" href="/organizationlogin">登录</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import request from "@/utils/request.js";

const validatePass = (rule, value, callback) => {
  if (!value) {
    callback(new Error("请再次确认密码"));
  } else if (value !== data.form.orgLoginPassword) { // 对应修改
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

const data = reactive({
  form: {
    orgName: "",            // 对应数据库 OrgName
    orgLoginUserName: "",   // 对应数据库 OrgLoginUserName
    orgLoginPassword: "",   // 对应数据库 OrgLoginPassword
    confirmPassword: "",    // 仅前端校验
    contactPersonPhone: "", // 对应数据库 ContactPersonPhone
    serviceRegion: "北京",  // 对应数据库 ServiceRegion
    orgScale: null,         // 对应数据库 OrgScale
  },
  rules: {
    orgName: [
      { required: true, message: "请输入组织名称", trigger: "blur" }
    ],
    orgLoginUserName: [
      { required: true, message: "请输入登录用户名", trigger: "blur" }
    ],
    orgLoginPassword: [
      { required: true, message: "请输入登录密码", trigger: "blur" },
      { min: 6, message: "密码长度不能少于6位", trigger: "blur" }
    ],
    confirmPassword: [
      { required: true, message: "请确认密码", trigger: "blur" },
      { validator: validatePass, trigger: "blur" }
    ],
    contactPersonPhone: [
      { required: true, message: "请输入负责人联系方式", trigger: "blur" },
      { pattern: /^1[3-9]\d{9}$/, message: "请输入有效的11位手机号码", trigger: "blur" }
    ],
    serviceRegion: [
      { required: true, message: "请选择服务区域", trigger: "change" }
    ],
    orgScale: [
      { required: true, message: "请输入组织规模", trigger: "blur" },
      { type: 'number', min: 1, message: '组织规模必须为大于0的整数', trigger: 'blur' }
    ]
  }
});

const formRef = ref();

const organizationRegister = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      const payload = { ...data.form };
      delete payload.confirmPassword; // 不发送确认密码字段

      // 确保发送给后端的字段名与后端实体类完全一致
      // 在当前 data.form 中，字段名已经与后端驼峰式一致了

      request.post("/organization/register", payload).then((res) => { // API端点
        if (res.code === "200") {
          ElMessage.success("注册申请已提交，请等待审核！");
          setTimeout(() => {
            location.href = "/organizationlogin";
          }, 1500);
        } else {
          ElMessage.error(res.msg || "注册失败，请稍后再试");
        }
      }).catch(err => {
        console.error("注册请求失败:", err);
        ElMessage.error("注册请求发生错误，请检查网络或联系管理员");
      });
    } else {
      ElMessage.warning("请检查表单信息是否完整且正确！");
      return false;
    }
  });
};
</script>

<style scoped>
.register-container {
  height: 100vh;
  overflow: hidden;
  background-image: url("@/assets/volunteerLogin.jpg");
  background-size: cover;
  background-position: center center;
  display: flex;
  justify-content: center;
  align-items: center;
}

.register-box {
  width: 90%;
  max-width: 500px;
}

@media (max-width: 768px) {
  .register-box {
    width: 95%;
    padding: 15px;
  }

  .el-form-item {
    margin-bottom: 15px;
  }

  .el-button {
    font-size: 14px;
  }
}

.el-select {
  width: 100%;
}
</style>
