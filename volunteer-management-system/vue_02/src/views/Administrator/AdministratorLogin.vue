<template>
  <div class="login-container">
    <div class="login-box">
      <div style="background-color: white; border-radius: 10px; padding: 30px; box-shadow: 0 0 10px rgb(90, 16, 6)">
        <div style="margin-bottom: 30px; color: #c32f1b; font-size: 20px; font-weight: bold; text-align: center">
          管理员登录
        </div>
        <el-form ref="formRef" :rules="data.rules" :model="data.form" style="margin-right: 30px; margin-top: 10px">
          <el-form-item label="账号" prop="identifier" label-width="80px">
            <el-input size="large" v-model="data.form.identifier" autocomplete="off" placeholder="请输入管理员ID或用户名" prefix-icon="User" />
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
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import request from "@/utils/request.js"; // 确保您的请求工具路径正确
import { useAdminStore } from '@/stores/adminStore.js'; // 确保路径指向您的 adminStore

const adminStore = useAdminStore();

const data = reactive({
  form: {
    identifier: "", // 用于输入 AdminID 或 Name
    password: ""
  },
  rules: {
    identifier: [{ required: true, message: "请输入管理员标识 (ID或名称)", trigger: "blur" }],
    password: [
        { required: true, message: "请输入密码", trigger: "blur" },
        // 根据需要可以添加密码复杂度或长度校验
        // { min: 6, message: "密码长度不能少于6位", trigger: "blur" }
    ]
  }
});

const formRef = ref();

const login = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      // 构建 payload 以匹配后端 Administrator 实体的字段名
      // 后端服务需要能够区分 identifier 是 adminId 还是 name，或者两者都尝试
      const payload = {
        adminId: data.form.identifier.trim(),  // 对应 Administrator.adminId
        password: data.form.password.trim()    // 对应 Administrator.password
      };

      request.post("/administrator/login", payload).then((res) => {
        console.log("管理员登录接口响应 (res):", JSON.stringify(res, null, 2));
        if (res.code === "200" && res.data) { // 确保 res.data 存在
          // 调用 adminStore 的 loginSuccess action
          // basicLoginInfoFromLogin 用于在 apiResponseData 不完整时提供基础信息
          // 这里我们传递 payload 中的 adminId 和 name 作为基础登录尝试信息
          const basicLoginInfo = {
            adminId: payload.adminId,
            password: payload.password
          };
          adminStore.loginSuccess(res.data, basicLoginInfo);

          ElMessage.success("登录成功");
          setTimeout(() => {
            location.href = "/manager"; // 管理员登录后的跳转路径，例如 /admin-dashboard 或 /manager
          }, 500);
        } else {
          console.error("管理员登录业务失败，Code:", res.code, "Msg:", res.msg);
          ElMessage.error(res.msg || `登录失败，代码: ${res.code || '未知'}`);
        }
      }).catch(err => {
        console.error("管理员登录请求失败:", err.response ? JSON.stringify(err.response.data, null, 2) : err.message);
        if (err.response && err.response.data && err.response.data.msg) {
          ElMessage.error(err.response.data.msg);
        } else if (err.message) {
          ElMessage.error("请求错误: " + err.message);
        } else {
          ElMessage.error("登录请求失败，请检查网络或联系管理员。");
        }
      });
    } else {
      ElMessage.warning("请检查表单信息是否完整且正确！");
      return false;
    }
  });
};
</script>


<style scoped>
.login-container {
  height: 100vh;
  overflow: hidden;
  background-image: url("@/assets/volunteerLogin.jpg"); /* 请确保此路径对于你的项目是正确的 */
  background-size: 110% 100%;
  background-position: -30px 0px;
}
.login-box {
  width: 50%;
  height: 100%;
  display: flex;
  /* 'right: -200px;' 根据父容器上下文，可能会使盒子偏移出屏幕。
     如果需要居中，可以考虑 'margin-left: auto;' 或其他居中技术。 */
  right: -200px;
  align-items: center;
  position: absolute;
}
</style>