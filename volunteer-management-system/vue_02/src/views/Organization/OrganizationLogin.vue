<template>
  <div class="login-container">
    <div class="login-box">
      <div style="background-color: white; border-radius: 10px; padding: 30px; box-shadow: 0 0 10px rgb(90, 16, 6)">
        <div style="margin-bottom: 30px; color: #c32f1b; font-size: 20px; font-weight: bold; text-align: center">
          组织机构登录
        </div>
        <!--
          注意: el-form-item 的 prop 属性用于关联 rules 中的校验键名,
          而 v-model 绑定的 data.form 中的属性名才真正参与数据提交。
          为了与后端 Organization 实体类的 orgLoginUserName 和 orgLoginPassword 对应，
          我们在提交时会使用这些名称。
        -->
        <el-form ref="formRef" :rules="data.rules" :model="data.form" style="margin-right: 30px; margin-top: 10px">
          <el-form-item label="用户名" prop="orgLoginUserName" label-width="80px">
            <el-input size="large" v-model="data.form.orgLoginUserName" autocomplete="off" placeholder="请输入登录用户名" prefix-icon="User" />
          </el-form-item>
          <el-form-item label="密码" prop="orgLoginPassword" label-width="80px">
            <el-input size="large" type="password" show-password v-model="data.form.orgLoginPassword" autocomplete="off" placeholder="请输入登录密码" prefix-icon="Lock" />
          </el-form-item>
        </el-form>
        <div style="margin-top: 20px;">
          <el-button @click="login" size="large" type="danger" style="width: 100%; color: white">
            登录
          </el-button>
        </div>
        <div style="text-align: right; margin-top: 15px">
          没有账号？请 <a style="color: #c32f1b; text-decoration: none" href="/organizationregister">注册</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import request from "@/utils/request.js"; // 确保请求工具路径正确
import { useOrganizationStore } from '@/stores/organizationStore.js';

const organizationStore = useOrganizationStore();

const data = reactive({
  form: {
    orgLoginUserName: "", // 与后端实体类字段名对应
    orgLoginPassword: ""  // 与后端实体类字段名对应
  },
  rules: {
    // rules 的键名与 prop 对应
    orgLoginUserName: [{ required: true, message: "请输入登录用户名", trigger: "blur" }],
    orgLoginPassword: [
        { required: true, message: "请输入登录密码", trigger: "blur" },
        { min: 6, message: "密码长度不能少于6位", trigger: "blur"}
    ]
  }
});

const formRef = ref();

const login = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      // 确保提交给后端的对象字段名与后端期望的一致
      const payload = {
        orgLoginUserName: data.form.orgLoginUserName.trim(),
        orgLoginPassword: data.form.orgLoginPassword.trim() // 密码通常也需要trim，但取决于具体业务
      };
      request.post("/organization/login", payload).then((res) => { // API端点
        console.log("后端登录接口返回的完整响应 (res):", JSON.stringify(res, null, 2));
        if (res.code === "200" && res.data) { // 确保 res.data 存在
          // 传递给 store 的 basicLoginInfo 可以只包含关键信息，如登录名
          // store 内部的 loginSuccess 会处理后端返回的完整 data
          const basicLoginInfo = { orgLoginUserName: payload.orgLoginUserName };
          organizationStore.loginSuccess(res.data, basicLoginInfo); // res.data 应该是后端返回的组织完整信息
          ElMessage.success("登录成功");
          setTimeout(() => {
            location.href = "/organization-home"; // 确认跳转路径正确
          }, 500);
        } else {
          console.error("登录业务失败，Code:", res.code, "Msg:", res.msg);
          ElMessage.error(res.msg || `登录失败，代码: ${res.code || '未知'}`);
        }
      }).catch((err) => {
        console.error("登录请求网络或代码错误 (err):", err.response ? JSON.stringify(err.response.data, null, 2) : err.message);
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
  background-image: url("@/assets/volunteerLogin.jpg"); /* 确认图片路径 */
  background-size: cover; /* cover 或 contain 可能比固定百分比更好 */
  background-position: center center;
  display: flex;
  justify-content: center;
  align-items: center;
}
.login-box {
  width: 90%;
  max-width: 400px; /* 调整合适的登录框最大宽度 */
 /* 移除了之前的 right 和 position: absolute，因为父容器已经是flex居中 */
}
/* 可以添加更多响应式样式 */
</style>
