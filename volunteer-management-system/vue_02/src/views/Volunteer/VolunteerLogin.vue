<template>
  <div class="login-container">
    <div class="login-box">
      <div style="background-color: white; border-radius: 10px; padding: 30px; box-shadow: 0 0 10px rgb(90, 16, 6)">
        <div style="margin-bottom: 30px; color: #c32f1b; font-size: 20px; font-weight: bold; text-align: center">
          志愿者管理系统
        </div>
        <el-form ref="formRef" :rules="data.rules" :model="data.form" style="margin-right: 30px; margin-top: 10px">
          <el-form-item label="用户名" prop="username" label-width="80px">
            <el-input size="large" v-model="data.form.username" autocomplete="off" placeholder="请输入用户名" prefix-icon="User" />
          </el-form-item>
          <el-form-item label="密码" prop="password" label-width="80px">
            <el-input size="large" type="password" v-model="data.form.password" autocomplete="off" placeholder="请输入密码" prefix-icon="Lock" />
          </el-form-item>
        </el-form>
        <div>
          <el-button @click="login" size="large" type="danger" style="width: 100%; color: white">
            登录
          </el-button>
        </div>
        <div style="text-align: right; margin-top: 15px">
          没有账号？请 <a style="color: #c32f1b; text-decoration: none" href="/volunteerregister">注册</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { User, Lock } from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";
import {useUserStore} from "@/stores/userStore.js";

const data = reactive({
  form: {
    username: "",
    password: ""
  },
  rules: {
    username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
    password: [{ required: true, message: "请输入密码", trigger: "blur" }]
  }
});

const formRef = ref();
const userStore = useUserStore();
const login = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      // 去除用户名和密码的前后空格
      const cleanedForm = {
        username: data.form.username.trim(),
        password: data.form.password.trim()
      };

      request.post('/volunteer/login', cleanedForm).then((res) => {
        if (res.code === '200') {
          // localStorage.setItem('xm-pro-user', JSON.stringify(cleanedForm));
          const basicLoginInfo = { username: cleanedForm.username }; // **不要传递密码到 store 或 localStorage**
          userStore.loginSuccess(res.data, basicLoginInfo); // 调用 action
          ElMessage.success('登录成功');
          setTimeout(() => {
            location.href = '/volunteer';
          }, 500);
        } else {
          ElMessage.error(res.msg);
        }
      }).catch((err) => {
        ElMessage.error(err.response.data.msg || "登录失败");
      });
    }
  });
};
</script>

<style scoped>
.login-container {
  height: 100vh;
  overflow: hidden;
  background-image: url("@/assets/volunteerLogin.jpg");
  background-size: 110% 100%;
  background-position: -30px 0px;
}
.login-box {
  width: 50%;
  height: 100%;
  display: flex;
  right: -200px;
  align-items: center;
  position: absolute;
}
</style>