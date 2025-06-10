<template>
  <el-card>
    <div class="header">
      <h1>正式成员</h1>
      <el-input
          v-model="searchQuery"
          placeholder="搜索人员"
          style="width: 200px; margin-left: auto;"
      ></el-input>
    </div>
    <el-button
        type="primary"
        @click="member"
        style="margin-top: 100px"
    >正式成员</el-button>
    <el-button
        type="primary"
        @click="join"
        style="margin-top: 100px"
    >申请加入</el-button>
    <el-button
        type="primary"
        @click="addPersonDialogVisible = true"
        style="margin-top: 100px"
    >申请添加系统外人员</el-button>
    <el-table :data="volunteers" style="width: 1500px; margin-top: 20px">
      <el-table-column prop="id" label="ID" width="200"></el-table-column>
      <el-table-column prop="name" label="姓名" width="200"></el-table-column>
      <el-table-column prop="telephone" label="联系方式" width="200"></el-table-column>
      <el-table-column prop="totalServiceHours" label="志愿总时长" width="200"></el-table-column>
      <el-table-column label="操作" width="300">
        <template v-slot="scope">
          <el-button type="primary" size="mini" @click="viewDetails(scope.row)">详细信息</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-button type="primary" @click="home" style="margin-top: 20px">返回主页</el-button>

    <!-- 添加系统外人员的弹窗 -->
    <el-dialog v-model="addPersonDialogVisible" title="添加系统外人员">
      <el-form ref="formRef" :rules="formRules" :model="formData" class="register-form">
        <!-- 左侧表单项 -->
        <div class="form-column">
          <!-- 用户名输入框 -->
          <el-form-item label="用户名" prop="username" label-width="80px">
            <el-input size="large" v-model="formData.username" autocomplete="off" placeholder="请输入用户名" prefix-icon="User" />
          </el-form-item>
          <!-- 密码输入框 -->
          <el-form-item label="密码" prop="password" label-width="80px">
            <el-input size="large" type="password" v-model="formData.password" autocomplete="off" placeholder="请输入密码" prefix-icon="Lock" />
          </el-form-item>
          <!-- 确认密码输入框 -->
          <el-form-item label="确认密码" prop="confirmPassword" label-width="80px">
            <el-input size="large" type="password" v-model="formData.confirmPassword" autocomplete="off" placeholder="请输入确认密码" prefix-icon="Lock" />
          </el-form-item>
          <!-- 真实姓名输入框 -->
          <el-form-item label="真实姓名" prop="name" label-width="80px">
            <el-input size="large" v-model="formData.name" autocomplete="off" placeholder="请输入真实姓名" prefix-icon="User" />
          </el-form-item>
          <!-- 性别输入框 -->
          <el-form-item label="性别" prop="gender" label-width="80px">
            <el-input size="large" v-model="formData.gender" autocomplete="off" placeholder="请输入性别" prefix-icon="User" />
          </el-form-item>
          <!-- 手机号输入框 -->
          <el-form-item label="手机号" prop="phone" label-width="80px">
            <el-input size="large" v-model="formData.phone" autocomplete="off" placeholder="请输入手机号" prefix-icon="Phone" />
          </el-form-item>
          <!-- 身份证号输入框 -->
          <el-form-item label="身份证号" prop="idCard" label-width="80px">
            <el-input size="large" v-model="formData.idCard" autocomplete="off" placeholder="请输入身份证号" prefix-icon="User" />
          </el-form-item>
        </div>
        <!-- 右侧表单项 -->
        <div class="form-column">
          <!-- 国籍输入框 -->
          <el-form-item label="国籍" prop="country" label-width="80px">
            <el-input size="large" v-model="formData.country" autocomplete="off" placeholder="请输入国籍" prefix-icon="User" />
          </el-form-item>
          <!-- 民族输入框 -->
          <el-form-item label="民族" prop="ethnicity" label-width="80px">
            <el-input size="large" v-model="formData.ethnicity" autocomplete="off" placeholder="请输入民族" prefix-icon="User" />
          </el-form-item>
          <!-- 政治面貌输入框 -->
          <el-form-item label="政治面貌" prop="politicalStatus" label-width="80px">
            <el-input size="large" v-model="formData.politicalStatus" autocomplete="off" placeholder="请输入政治面貌" prefix-icon="User" />
          </el-form-item>
          <!-- 最高学历输入框 -->
          <el-form-item label="最高学历" prop="highestEducation" label-width="80px">
            <el-input size="large" v-model="formData.highestEducation" autocomplete="off" placeholder="请输入最高学历" prefix-icon="User" />
          </el-form-item>
          <!-- 从业情况输入框 -->
          <el-form-item label="从业情况" prop="employmentStatus" label-width="80px">
            <el-input size="large" v-model="formData.employmentStatus" autocomplete="off" placeholder="请输入从业情况" prefix-icon="User" />
          </el-form-item>
          <!-- 服务区域输入框 -->
          <el-form-item label="服务区域" prop="serviceArea" label-width="80px">
            <el-input size="large" v-model="formData.serviceArea" autocomplete="off" placeholder="请输入服务区域" prefix-icon="User" />
          </el-form-item>
          <!-- 服务类别输入框 -->
          <el-form-item label="服务类别" prop="serviceCategory" label-width="80px">
            <el-input size="large" v-model="formData.serviceCategory" autocomplete="off" placeholder="请输入服务类别" prefix-icon="User" />
          </el-form-item>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addPersonDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">添加该成员</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import axios from "axios";
import { ElMessage } from "element-plus";

export default {
  setup() {
    const router = useRouter();
    const route = useRoute();
    const volunteers = ref([]);
    const searchQuery = ref('');
    const addPersonDialogVisible = ref(false);
    const activeButton = ref('member'); // 默认激活"正式成员"按钮

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

    const validatePass = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请再次确认密码"));
      } else if (value !== formData.value.password) {
        callback(new Error("两次输入的密码不一致"));
      } else {
        callback();
      }
    };

    const formRules = ref({
      username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
      password: [{ required: true, message: "请输入密码", trigger: "blur" }],
      confirmPassword: [
        { required: true, message: "请确认密码", trigger: "blur" },
        { validator: validatePass, trigger: "blur" }
      ],
      name: [{ required: true, message: "请输入真实姓名", trigger: "blur" }],
      gender: [{ required: true, message: "请输入性别", trigger: "blur" }],
      phone: [{ required: true, message: "请输入手机号", trigger: "blur" }],
      idCard: [{ required: true, message: "请输入身份证号", trigger: "blur" }],
      country: [{ required: true, message: "请输入国籍", trigger: "blur" }],
      ethnicity: [{ required: true, message: "请输入民族", trigger: "blur" }],
      politicalStatus: [{ required: true, message: "请输入政治面貌", trigger: "blur" }],
      highestEducation: [{ required: true, message: "请输入最高学历", trigger: "blur" }],
      employmentStatus:[{ required: true, message: "请输入从业情况", trigger: "blur" }],
      serviceArea: [{ required: true, message: "请输入服务区域", trigger: "blur" }],
      serviceCategory: [{ required: true, message: "请输入服务类别", trigger: "blur" }]
    });

    const fetchVolunteers = async () => {
      try {
        const response = await axios.get("/volunteerOrganizationJoin/active", { params: { orgId: '当前组织ID' } });
        volunteers.value = response.data;
      } catch (error) {
        console.error("获取正式成员数据失败：", error);
        ElMessage.error("获取正式成员数据失败，请稍后再试");
      }
    };

    const submitForm = () => {
      const formRef = ref(null);
      formRef.value.validate((valid) => {
        if (valid) {
          axios.post("/volunteerOrganizationJoin/addMember", formData.value)
              .then(response => {
                if (response.data.success) {
                  ElMessage.success("成功添加成员");
                  fetchVolunteers();
                  addPersonDialogVisible.value = false;
                } else {
                  ElMessage.error(response.data.message);
                }
              })
              .catch(error => {
                console.error("添加成员失败：", error);
                ElMessage.error("添加成员失败，请稍后再试");
              });
        } else {
          ElMessage.error("表单验证失败");
        }
      });
    };

    const home = () => {
      router.push('/organization-home');
    };

    const member = () => {
      activeButton.value = 'member';
      router.push('/manage-personnel');
    };

    const join = () => {
      activeButton.value = 'join';
      router.push('/join-member');
    };

    const viewDetails = (row) => {
      console.log('查看详细信息：', row);
      router.push('/detailed-volunteer-info-for-add');
    };

    onMounted(() => {
      if (route.path === '/join-member') {
        activeButton.value = 'join';
      } else {
        activeButton.value = 'member';
      }
    });

    fetchVolunteers();

    return {
      volunteers,
      searchQuery,
      addPersonDialogVisible,
      formData,
      formRules,
      home,
      viewDetails,
      submitForm,
      join,
      member,
      activeButton
    };
  }
};
</script>

<style scoped>
.header {
  background-color: #ff3333; /* 鲜红色背景 */
  color: white;
  padding: 10px 20px;
  margin-bottom: 100px;
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

.is-active {
  background-color: #ff0000; /* 高亮颜色 */
  color: white;
}

.el-button {
  margin-right: 10px; /* 添加按钮之间的间距 */
}

.register-form {
  display: flex;
  justify-content: space-between;
}
.form-column {
  flex: 1;
  margin-right: 30px;
}
.form-column:last-child {
  margin-right: 0;
}
</style>