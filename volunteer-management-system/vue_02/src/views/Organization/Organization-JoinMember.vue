<template>
  <el-card>
    <div class="header">
      <h1>申请加入</h1>
      <el-input
          v-model="searchQuery"
          placeholder="搜索申请信息"
          style="width: 200px; margin-left: auto;"
          clearable
      ></el-input>
    </div>

    <el-button
        type="primary"
        @click="managePersonnel"
        style="margin-top: 100px"
    >正式成员</el-button>
    <el-button
        type="primary"
        @click="joinMember"
        style="margin-top: 100px"
    >申请加入</el-button>
    <el-button
        type="primary"
        @click="addPersonDialogVisible = true"
        style="margin-top: 100px"
    >申请添加系统外人员</el-button>

    <el-table :data="filteredVolunteers" style="width: 1500px; margin-top: 20px">
      <el-table-column prop="volunteerId" label="志愿者ID" width="200"></el-table-column>
      <el-table-column prop="name" label="姓名" width="200"></el-table-column>
      <el-table-column prop="phoneNumber" label="联系方式" width="200"></el-table-column>
      <el-table-column prop="totalVolunteerHours" label="志愿总时长" width="200"></el-table-column>
      <el-table-column label="操作" width="300" align="center">
        <template #default="scope">
          <el-button type="primary" size="small" @click="approve(scope.row)">允许加入</el-button>
          <el-button type="info" size="small" @click="showDetails(scope.row)">详细信息</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-button type="primary" @click="home" style="margin-top: 20px">返回主页</el-button>
<!--    添加系统外成员-->
    <el-dialog v-model="addPersonDialogVisible" title="添加系统外人员" width="70%">
      <el-form ref="formRef" :rules="formRules" :model="formData" class="register-form">
        <div class="form-column">
          <el-form-item label="用户名" prop="username" label-width="80px">
            <el-input size="large" v-model="formData.username" autocomplete="off" placeholder="请输入用户名" prefix-icon="User" />
          </el-form-item>
          <el-form-item label="密码" prop="password" label-width="80px">
            <el-input size="large" type="password" v-model="formData.password" autocomplete="off" placeholder="请输入密码" prefix-icon="Lock" />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword" label-width="80px">
            <el-input size="large" type="password" v-model="formData.confirmPassword" autocomplete="off" placeholder="请输入确认密码" prefix-icon="Lock" />
          </el-form-item>
          <el-form-item label="真实姓名" prop="name" label-width="80px">
            <el-input size="large" v-model="formData.name" autocomplete="off" placeholder="请输入真实姓名" prefix-icon="User" />
          </el-form-item>
          <el-form-item label="性别" prop="gender" label-width="80px">
            <el-select v-model="formData.gender" placeholder="请选择性别" style="width: 100%;">
              <el-option label="男" value="男"></el-option>
              <el-option label="女" value="女"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="手机号" prop="phone" label-width="80px">
            <el-input size="large" v-model="formData.phone" autocomplete="off" placeholder="请输入手机号" prefix-icon="Phone" />
          </el-form-item>
          <el-form-item label="身份证号" prop="idCard" label-width="80px">
            <el-input size="large" v-model="formData.idCard" autocomplete="off" placeholder="请输入身份证号" prefix-icon="User" />
          </el-form-item>
        </div>
        <div class="form-column">
          <el-form-item label="国籍" prop="country" label-width="80px">
            <el-input size="large" v-model="formData.country" autocomplete="off" placeholder="请输入国籍" prefix-icon="User" />
          </el-form-item>
          <el-form-item label="民族" prop="ethnicity" label-width="80px">
            <el-input size="large" v-model="formData.ethnicity" autocomplete="off" placeholder="请输入民族" prefix-icon="User" />
          </el-form-item>
          <el-form-item label="政治面貌" prop="politicalStatus" label-width="80px">
            <el-input size="large" v-model="formData.politicalStatus" autocomplete="off" placeholder="请输入政治面貌" prefix-icon="User" />
          </el-form-item>
          <el-form-item label="最高学历" prop="highestEducation" label-width="80px">
            <el-input size="large" v-model="formData.highestEducation" autocomplete="off" placeholder="请输入最高学历" prefix-icon="User" />
          </el-form-item>
          <el-form-item label="从业情况" prop="employmentStatus" label-width="80px">
            <el-input size="large" v-model="formData.employmentStatus" autocomplete="off" placeholder="请输入从业情况" prefix-icon="User" />
          </el-form-item>
          <el-form-item label="服务区域" prop="serviceArea" label-width="80px">
            <el-input size="large" v-model="formData.serviceArea" autocomplete="off" placeholder="请输入服务区域" prefix-icon="User" />
          </el-form-item>
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

<!--    详细信息-->
    <el-dialog v-model="detailsDialogVisible" title="志愿者详细信息" width="50%">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">{{ selectedVolunteer.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ selectedVolunteer.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ selectedVolunteer.gender }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ selectedVolunteer.phoneNumber }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ selectedVolunteer.idCardNumber }}</el-descriptions-item>
        <el-descriptions-item label="国籍">{{ selectedVolunteer.country }}</el-descriptions-item>
        <el-descriptions-item label="民族">{{ selectedVolunteer.ethnicity }}</el-descriptions-item>
        <el-descriptions-item label="政治面貌">{{ selectedVolunteer.politicalStatus }}</el-descriptions-item>
        <el-descriptions-item label="最高学历">{{ selectedVolunteer.highestEducation }}</el-descriptions-item>
        <el-descriptions-item label="从业情况">{{ selectedVolunteer.employmentStatus }}</el-descriptions-item>
        <el-descriptions-item label="服务区域">{{ selectedVolunteer.serviceArea }}</el-descriptions-item>
        <el-descriptions-item label="服务类别">{{ selectedVolunteer.serviceCategory }}</el-descriptions-item>
        <el-descriptions-item label="志愿总时长">{{ selectedVolunteer.totalVolunteerHours }}</el-descriptions-item>
        <el-descriptions-item label="志愿者综合评分">{{ selectedVolunteer.volunteerRating }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </el-card>
</template>

<script setup>
// 【已修正】这部分是能正常工作的脚本
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useOrgIdStore } from '@/stores/useOrgIdStore';
import request from '@/utils/request'; // 统一使用 request 工具

// --- setup 顶层作用域 ---
const router = useRouter();
const volunteers = ref([]);
const searchQuery = ref('');
const addPersonDialogVisible = ref(false);
const orgIdStore = useOrgIdStore();
const formRef = ref(null); // ✅ formRef 在顶层定义
const detailsDialogVisible = ref(false); // 控制详细信息弹窗的显示
const selectedVolunteer = ref({}); // 用于存储选中的志愿者信息

const showDetails = (row) => {
  selectedVolunteer.value = row; // 将选中的志愿者信息赋值
  detailsDialogVisible.value = true; // 打开弹窗
};

const formData = ref({
  username: "", password: "", confirmPassword: "", name: "", gender: "", phone: "",
  idCard: "", country: "", ethnicity: "", politicalStatus: "",
  highestEducation: "", employmentStatus: "", serviceArea: "", serviceCategory: ""
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
  confirmPassword: [{ required: true, validator: validatePass, trigger: "blur" }],
  name: [{ required: true, message: "请输入真实姓名", trigger: "blur" }],
  gender: [{ required: true, message: "请选择性别", trigger: "change" }],
  phone: [{ required: true, message: "请输入手机号", trigger: "blur" }],
  idCard: [{ required: true, message: "请输入身份证号", trigger: "blur" }],
});

const filteredVolunteers = computed(() => {
    if (!searchQuery.value) {
        return volunteers.value;
    }
    return volunteers.value.filter(v =>
        (v.name && v.name.includes(searchQuery.value)) ||
        (v.volunteerId && v.volunteerId.includes(searchQuery.value)) ||
        (v.phoneNumber && v.phoneNumber.includes(searchQuery.value))
    );
});

const fetchPendingVolunteers = async () => {
  try {
    const orgId = orgIdStore.orgId;
    if (!orgId) {
      ElMessage.warning('组织ID未加载，请刷新页面或重新登录。');
      return;
    }
    const res = await request.get("/volunteerOrganizationJoin/pending", {
      params: { orgId }
    });
    // ✅ 正确解析数据
    if (res.code === '200' && res.data) {
        volunteers.value = res.data;
    } else {
        ElMessage.error(res.msg || "获取申请列表失败");
    }
  } catch (error) {
    ElMessage.error("获取申请列表时发生网络错误");
  }
};

const approve = async (row) => {
  try {
    const orgId = orgIdStore.orgId;
    const res = await request.post("/volunteerOrganizationJoin/approveJoinRequest", {
      volunteerId: row.volunteerId,
      orgId: orgId
    });
    // ✅ 正确判断成功条件
    if (res.code === '200') {
      ElMessage.success("成功批准加入");
      fetchPendingVolunteers(); // 刷新申请列表
    } else {
      ElMessage.error(res.msg || '批准加入失败');
    }
  } catch (error) {
      ElMessage.error("批准加入时发生网络错误");
  }
};

const submitForm = async () => {
    if (!formRef.value) return; // 防御式编程
    // ✅ 修正后的表单提交逻辑
    try {
        await formRef.value.validate();
        const res = await request.post("/volunteerOrganizationJoin/addMember", formData.value);
        if (res.code === '200') {
            ElMessage.success("成功添加成员");
            fetchPendingVolunteers();
            addPersonDialogVisible.value = false;
        } else {
            ElMessage.error(res.msg || '添加成员失败');
        }
    } catch (validationError) {
        // validate 失败会 reject promise，在这里可以捕获，但通常 ElMessage 会自动提示
        console.log('表单验证失败', validationError);
    }
};

const home = () => router.push('/organization-home');
const managePersonnel = () => router.push('/manage-personnel');
const joinMember = () => router.push('/join-member');

onMounted(() => {
  fetchPendingVolunteers();
});
</script>

<style scoped>
/* 保留您原始的样式 */
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