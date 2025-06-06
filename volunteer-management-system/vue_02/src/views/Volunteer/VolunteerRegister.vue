<template>
  <div class="register-container">
    <!-- 注册框的外层容器 -->
    <div class="register-box">
      <!-- 背景白色、圆角、内边距、阴影 -->
      <div style="background-color: white; border-radius: 10px; padding: 30px; box-shadow: 0 0 10px rgb(90, 16, 6)">
        <!-- 标题 -->
        <div style="margin-bottom: 30px; color: #c32f1b; font-size: 20px; font-weight: bold; text-align: center">
          欢迎注册
        </div>
        <!-- 表单 -->
        <el-form ref="formRef" :rules="data.rules" :model="data.form" class="register-form">
          <!-- 左侧表单项 -->
          <div class="form-column">
            <!-- 用户名输入框 -->
            <el-form-item label="用户名" prop="username" label-width="80px">
              <el-input size="large" v-model="data.form.username" autocomplete="off" placeholder="请输入用户名" prefix-icon="User" />
            </el-form-item>
            <!-- 密码输入框 -->
            <el-form-item label="密码" prop="password" label-width="80px">
              <el-input size="large" type="password" v-model="data.form.password" autocomplete="off" placeholder="请输入密码" prefix-icon="Lock" />
            </el-form-item>
            <!-- 确认密码输入框 -->
            <el-form-item label="确认密码" prop="confirmPassword" label-width="80px">
              <el-input size="large" type="password" v-model="data.form.confirmPassword" autocomplete="off" placeholder="请确认密码" prefix-icon="Lock" />
            </el-form-item>
            <!-- 真实姓名输入框 -->
            <el-form-item label="真实姓名" prop="name" label-width="80px">
              <el-input size="large" v-model="data.form.name" autocomplete="off" placeholder="请输入真实姓名" prefix-icon="User" />
            </el-form-item>
            <!-- 性别选择 -->
            <el-form-item label="性别" prop="gender" label-width="80px">
              <el-radio-group v-model="data.form.gender">
                <el-radio label="男">男</el-radio>
                <el-radio label="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <!-- 手机号输入框 -->
            <el-form-item label="手机号" prop="phone" label-width="80px">
              <el-input size="large" v-model="data.form.phone" autocomplete="off" placeholder="请输入手机号" prefix-icon="Phone" />
            </el-form-item>
            <!-- 身份证号输入框 -->
            <el-form-item label="身份证号" prop="idCard" label-width="80px">
              <el-input size="large" v-model="data.form.idCard" autocomplete="off" placeholder="请输入身份证号" prefix-icon="User" />
            </el-form-item>
          </div>
          <!-- 右侧表单项 -->
          <div class="form-column">
            <!-- 国籍选择 -->
            <el-form-item label="国籍" prop="country" label-width="80px">
              <el-select v-model="data.form.country" placeholder="请选择国籍">
                <el-option label="中国" value="China"></el-option>
                <el-option label="其他国家" value="Other"></el-option>
              </el-select>
            </el-form-item>
            <!-- 民族选择 -->
            <el-form-item label="民族" prop="ethnicity" label-width="80px">
              <el-select v-model="data.form.ethnicity" placeholder="请选择民族">
                <el-option label="汉族" value="汉族"></el-option>
                <el-option label="蒙古族" value="蒙古族"></el-option>
                <el-option label="回族" value="回族"></el-option>
                <el-option label="藏族" value="藏族"></el-option>
                <el-option label="维吾尔族" value="维吾尔族"></el-option>
                <el-option label="苗族" value="苗族"></el-option>
                <el-option label="彝族" value="彝族"></el-option>
                <el-option label="壮族" value="壮族"></el-option>
                <el-option label="布依族" value="布依族"></el-option>
                <el-option label="朝鲜族" value="朝鲜族"></el-option>
                <el-option label="满族" value="满族"></el-option>
                <el-option label="侗族" value="侗族"></el-option>
                <el-option label="瑶族" value="瑶族"></el-option>
                <el-option label="白族" value="白族"></el-option>
                <el-option label="土家族" value="土家族"></el-option>
                <el-option label="哈尼族" value="哈尼族"></el-option>
                <el-option label="哈萨克族" value="哈萨克族"></el-option>
                <el-option label="傣族" value="傣族"></el-option>
                <el-option label="黎族" value="黎族"></el-option>
                <el-option label="傈僳族" value="傈僳族"></el-option>
                <el-option label="佤族" value="佤族"></el-option>
                <el-option label="畲族" value="畲族"></el-option>
                <el-option label="高山族" value="高山族"></el-option>
                <el-option label="拉祜族" value="拉祜族"></el-option>
                <el-option label="水族" value="水族"></el-option>
                <el-option label="东乡族" value="东乡族"></el-option>
                <el-option label="纳西族" value="纳西族"></el-option>
                <el-option label="景颇族" value="景颇族"></el-option>
                <el-option label="柯尔克孜族" value="柯尔克孜族"></el-option>
                <el-option label="土族" value="土族"></el-option>
                <el-option label="达斡尔族" value="达斡尔族"></el-option>
                <el-option label="仫佬族" value="仫佬族"></el-option>
                <el-option label="羌族" value="羌族"></el-option>
                <el-option label="布朗族" value="布朗族"></el-option>
                <el-option label="撒拉族" value="撒拉族"></el-option>
                <el-option label="毛南族" value="毛南族"></el-option>
                <el-option label="仡佬族" value="仡佬族"></el-option>
                <el-option label="锡伯族" value="锡伯族"></el-option>
                <el-option label="阿昌族" value="阿昌族"></el-option>
                <el-option label="普米族" value="普米族"></el-option>
                <el-option label="塔吉克族" value="塔吉克族"></el-option>
                <el-option label="怒族" value="怒族"></el-option>
                <el-option label="乌孜别克族" value="乌孜别克族"></el-option>
                <el-option label="俄罗斯族" value="俄罗斯族"></el-option>
                <el-option label="鄂温克族" value="鄂温克族"></el-option>
                <el-option label="德昂族" value="德昂族"></el-option>
                <el-option label="保安族" value="保安族"></el-option>
                <el-option label="裕固族" value="裕固族"></el-option>
                <el-option label="京族" value="京族"></el-option>
                <el-option label="塔塔尔族" value="塔塔尔族"></el-option>
                <el-option label="独龙族" value="独龙族"></el-option>
                <el-option label="鄂伦春族" value="鄂伦春族"></el-option>
                <el-option label="赫哲族" value="赫哲族"></el-option>
                <el-option label="门巴族" value="门巴族"></el-option>
                <el-option label="珞巴族" value="珞巴族"></el-option>
              </el-select>
            </el-form-item>
            <!-- 政治面貌选择 -->
            <el-form-item label="政治面貌" prop="politicalStatus" label-width="80px">
              <el-select v-model="data.form.politicalStatus" placeholder="请选择政治面貌">
                <el-option label="群众" value="群众"></el-option>
                <el-option label="中国共产党党员" value="中国共产党党员"></el-option>
                <el-option label="中国共产党预备党员" value="中国共产党预备党员"></el-option>
                <el-option label="中国共产主义青年团团员" value="中国共产主义青年团团员"></el-option>
                <el-option label="中国国民党革命委员会会员" value="中国国民党革命委员会会员"></el-option>
                <el-option label="中国民主同盟盟员" value="中国民主同盟盟员"></el-option>
                <el-option label="中国民主建国会会员" value="中国民主建国会会员"></el-option>
                <el-option label="中国民主促进会会员" value="中国民主促进会会员"></el-option>
                <el-option label="中国农工民主党党员" value="中国农工民主党党员"></el-option>
                <el-option label="中国致公党党员" value="中国致公党党员"></el-option>
                <el-option label="九三学社社员" value="九三学社社员"></el-option>
                <el-option label="台湾民主自治同盟盟员" value="台湾民主自治同盟盟员"></el-option>
                <el-option label="无党派民主人士" value="无党派民主人士"></el-option>
              </el-select>
            </el-form-item>
            <!-- 最高学历选择 -->
            <el-form-item label="最高学历" prop="educationLevel" label-width="80px">
              <el-select v-model="data.form.educationLevel" placeholder="请选择最高学历">
                <el-option label="博士研究生" value="博士研究生"></el-option>
                <el-option label="硕士研究生" value="硕士研究生"></el-option>
                <el-option label="大学本科" value="大学本科"></el-option>
                <el-option label="大学专科和专科学校" value="大学专科和专科学校"></el-option>
                <el-option label="技工学校" value="技工学校"></el-option>
                <el-option label="高中" value="高中"></el-option>
                <el-option label="初中" value="初中"></el-option>
                <el-option label="小学" value="小学"></el-option>
                <el-option label="幼儿园学龄前" value="幼儿园学龄前"></el-option>
                <el-option label="特殊教育" value="特殊教育"></el-option>
                <el-option label="文盲或半文盲" value="文盲或半文盲"></el-option>
                <el-option label="未说明情况" value="未说明情况"></el-option>
              </el-select>
            </el-form-item>
            <!-- 从业情况选择 -->
            <el-form-item label="从业情况" prop="occupation" label-width="80px">
              <el-select v-model="data.form.occupation" placeholder="请选择从业情况">
                <el-option label="国家公务员" value="国家公务员"></el-option>
                <el-option label="职员" value="职员"></el-option>
                <el-option label="企业管理人员" value="企业管理人员"></el-option>
                <el-option label="工人" value="工人"></el-option>
                <el-option label="学生" value="学生"></el-option>
                <el-option label="现役军人" value="现役军人"></el-option>
                <el-option label="自由职业" value="自由职业"></el-option>
                <el-option label="个体经营者" value="个体经营者"></el-option>
                <el-option label="无业人员" value="无业人员"></el-option>
                <el-option label="退(离)休人员" value="退(离)休人员"></el-option>
                <el-option label="医生" value="医生"></el-option>
                <el-option label="司机" value="司机"></el-option>
                <el-option label="律师" value="律师"></el-option>
                <el-option label="教师" value="教师"></el-option>
                <el-option label="农民" value="农民"></el-option>
                <el-option label="未说明情况" value="未说明情况"></el-option>
              </el-select>
            </el-form-item>
            <!-- 服务区域选择 -->
            <el-form-item label="服务区域" prop="serviceArea" label-width="80px">
              <el-select v-model="data.form.serviceArea" placeholder="请选择服务区域">
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
            <!-- 服务类别选择 -->
            <el-form-item label="服务类别" prop="serviceCategory" label-width="80px">
              <el-select v-model="data.form.serviceCategory" placeholder="请选择服务类别">
                <el-option label="助力复工复产志愿者" value="助力复工复产志愿者"></el-option>
                <el-option label="扶贫济困志愿者" value="扶贫济困志愿者"></el-option>
                <el-option label="社区志愿者" value="社区志愿者"></el-option>
                <el-option label="青年志愿者" value="青年志愿者"></el-option>
                <el-option label="文明志愿者" value="文明志愿者"></el-option>
                <el-option label="文化志愿者" value="文化志愿者"></el-option>
                <el-option label="医疗志愿者" value="医疗志愿者"></el-option>
                <el-option label="教育志愿者" value="教育志愿者"></el-option>
                <el-option label="助残志愿者" value="助残志愿者"></el-option>
                <el-option label="巾帼志愿者" value="巾帼志愿者"></el-option>
                <el-option label="消防志愿者" value="消防志愿者"></el-option>
                <el-option label="红十字志愿者" value="红十字志愿者"></el-option>
                <el-option label="税收志愿者" value="税收志愿者"></el-option>
                <el-option label="疫情防控志愿者" value="疫情防控志愿者"></el-option>
              </el-select>
            </el-form-item>
          </div>
        </el-form>
        <!-- 注册按钮 -->
        <div style="text-align: center; margin-top: 30px">
          <el-button @click="volunteerRegister" size="large" type="danger" style="width: 100%; color: white">
            注册
          </el-button>
        </div>
        <!-- 跳转到登录页面 -->
        <div style="text-align: center; margin-top: 15px">
          已有账号？请 <a style="color: #c32f1b; text-decoration: none" href="/volunteerlogin">登录</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { User, Lock, Phone } from "@element-plus/icons-vue";
import request from "@/utils/request.js";

// 验证确认密码
const validatePass = (rule, value, callback) => {
  if (!value) {
    callback(new Error("请再次确认密码"));
  } else if (value !== data.form.password) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

// 表单数据和验证规则
const data = reactive({
  form: {
    username: "",
    password: "",
    confirmPassword: "",
    name: "",
    gender: "",
    phone: "",
    idCard: "",
    country: "China",
    ethnicity: "汉族",
    politicalStatus: "群众",
    educationLevel: "未说明情况",
    occupation: "未说明情况",
    serviceArea: "北京",
    serviceCategory: ""
  },
  rules: {
    username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
    password: [{ required: true, message: "请输入密码", trigger: "blur" }],
    confirmPassword: [
      { required: true, message: "请确认密码", trigger: "blur" },
      { validator: validatePass, trigger: "blur" }
    ],
    name: [{ required: true, message: "请输入真实姓名", trigger: "blur" }],
    gender: [{ required: true, message: "请选择性别", trigger: "blur" }],
    phone: [{ required: true, message: "请输入手机号", trigger: "blur" }],
    idCard: [{ required: true, message: "请输入身份证号", trigger: "blur" }],
    country: [{ required: true, message: "请选择国籍", trigger: "blur" }],
    ethnicity: [{ required: true, message: "请选择民族", trigger: "blur" }],
    politicalStatus: [{ required: true, message: "请选择政治面貌", trigger: "blur" }],
    educationLevel: [{ required: true, message: "请选择最高学历", trigger: "blur" }],
    occupation: [{ required: true, message: "请选择从业情况", trigger: "blur" }],
    serviceArea: [{ required: true, message: "请选择服务区域", trigger: "blur" }],
    serviceCategory: [{ required: true, message: "请选择服务类别", trigger: "blur" }]
  }
});

// 表单引用
const formRef = ref();

// 注册逻辑
const volunteerRegister = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      request.post("/volunteer/register", data.form).then((res) => {
        if (res.code === "200") {
          ElMessage.success("注册成功");
          setTimeout(() => {
            location.href = "/volunteerlogin";
          }, 500);
        } else {
          ElMessage.error(res.msg);
        }
      });
    }
  });
};
</script>

<style scoped>
.register-container {
  height: 100vh;
  overflow: hidden;
  background-image: url("@/assets/volunteerLogin.jpg");
  background-size: 110% 100%;
  background-position: -30px 0px;
}
.register-box {
  width: 100%;
  max-width: 800px;
  height: 100%;
  display: flex;
  right: -200px;
  align-items: center;
  position: absolute;
}
.register-form {
  display: flex;
  justify-content: space-between;
  margin-left: 10px; /* 向左调整整体布局 */
}
.form-column {
  flex: 1;
  margin-right: 30px;
}
.form-column:last-child {
  margin-right: 0;
}
</style>