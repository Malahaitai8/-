<template>
  <!-- 页面根容器 -->
  <div class="admin-profile-page">
    <!-- 页眉部分，包含 logo、标题和欢迎信息 -->
    <header class="header">
      <img src="/logo.png" alt="Logo" class="logo">
      <h1 class="title">志愿管理系统</h1>
      <span class="welcome">欢迎您：{{ formData.name }}{{formData.idCardNumber}}！</span>
    </header>

    <!-- 主体部分，用于展示个人信息输入项 -->
    <main class="main-content">
      <div class="form-container">
        <!-- 循环渲染表单项 -->
        <div v-for="item in formItems" :key="item.id" class="form-item">
          <label :for="item.id" class="form-label">
            <span v-if="!item.disabled" class="required">*</span> <!-- 必填项标记，可根据实际校验规则调整 -->
            {{ item.label }}:
          </label>
          <div class="form-input-area">
            <!-- 特殊处理：服务地区 (ServiceArea) 使用省市两级下拉框 -->
            <template v-if="item.id === 'serviceArea'">
              <input
                  :id="item.id"
                  type="text"
                  :placeholder="getPlaceholder(item.id)"
                  v-model="formData.serviceArea"
                  class="form-input"
                  :disabled="!isEditing"
              >
            </template>

            <!-- 特殊处理：性别 (Gender) 和权限等级 (PermissionLevel) 使用下拉选择 -->
            <template v-else-if="item.type === 'select' && item.options">
              <select :id="item.id" v-model="formData[item.id]" class="form-select"
                      :disabled="item.disabled || !isEditing">
                <option value="">{{ getPlaceholder(item.id) || '请选择' }}</option>
                <option v-for="option in item.options" :key="option" :value="option">{{ option }}</option>
              </select>
            </template>

            <!-- 其他类型的表单项 -->
            <template v-else>
              <input
                  :id="item.id"
                  :type="item.type"
                  :placeholder="getPlaceholder(item.id)"
                  v-model="formData[item.id]"
                  class="form-input"
                  :disabled="item.disabled || !isEditing"
              >
            </template>
          </div>
        </div>

        <!-- 操作按钮容器 -->
        <div class="button-container">
          <button @click="handleChangePassword" class="action-button">修改密码</button>
          <button v-if="!isEditing" @click="handleModifyInfo" class="action-button">修改个人信息</button>
          <button v-if="isEditing" @click="handleSaveInfo" class="action-button primary">保存个人信息</button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import {ref, onMounted, watch} from 'vue'; // 引入 watch
import {ElMessage} from 'element-plus';
import router from "@/router/index.js"; // 假设您的路由实例路径正确
import { useAdminStore } from '@/stores/adminStore.js'; // 引入 adminStore

const adminStore = useAdminStore();
// adminStore.initializeStore(); // initializeStore 将在 onMounted 中调用

const formData = ref({
  adminId: '',          // 对应 AdminID
  name: '',             // 对应 Name
  gender: '',           // 对应 Gender (例如：'男', '女')
  idCardNumber: '',     // 对应 IDCardNumber
  phoneNumber: '',      // 对应 PhoneNumber
  password: '',         // 对应 Password (通常不在个人资料表单直接编辑)
  serviceArea: '',      // 对应 ServiceArea (现在是单个文本输入)
  currentPosition: '',  // 对应 CurrentPosition
  permissionLevel: '',   // 对应 PermissionLevel
});

// 定义表单项目数组，每个对象代表一个输入项
const formItems = [
  {id: 'adminId', label: '管理员 ID', type: 'text', disabled: true},
  {id: 'name', label: '姓名', type: 'text'},
  {id: 'gender', label: '性别', type: 'select', options: ['男', '女', '未知']},
  {id: 'idCardNumber', label: '身份证号码', type: 'text'}, // 确保显示身份证号
  {id: 'phoneNumber', label: '手机号', type: 'tel'},     // 确保显示手机号
  {id: 'serviceArea', label: '服务地区', type: 'text'},   // serviceArea 作为文本输入
  {id: 'currentPosition', label: '当前职务', type: 'text'},
  {id: 'permissionLevel', label: '权限等级', type: 'select', options: ['高', '中', '低']}
];

// 省份列表 (保留数据，以备将来使用)
const provinces = ref([
  '北京市', '天津市', '上海市', '重庆市', '河北省', '山西省', '辽宁省', '吉林省', '黑龙江省', '江苏省', '浙江省', '安徽省', '福建省', '江西省', '山东省', '河南省', '湖北省', '湖南省', '广东省', '海南省', '四川省', '贵州省', '云南省', '陕西省', '甘肃省', '青海省', '台湾省', '内蒙古自治区', '广西壮族自治区', '西藏自治区', '宁夏回族自治区', '新疆维吾尔自治区', '香港特别行政区', '澳门特别行政区'
]);

// 省份对应的地区数据 (保留数据，以备将来使用)
const provinceDistricts = {
  '北京市': ['东城区', '西城区', '朝阳区', '丰台区', '石景山区', '海淀区', '门头沟区', '房山区', '通州区', '顺义区', '昌平区', '大兴区', '怀柔区', '平谷区', '密云区', '延庆区'],
  '天津市': ['和平区', '河东区', '河西区', '南开区', '河北区', '红桥区', '东丽区', '西青区', '津南区', '北辰区', '武清区', '宝坻区', '滨海新区', '宁河区', '静海区', '蓟州区'],
  // TODO: 其他省份地区需要补充完整
};

// 移除了 selectedProvince, selectedDistrict, districts ref
// 移除了 updateDistricts, handleDistrictChange, updateServiceAreaInFormData 函数

// 定义获取占位符的方法
const getPlaceholder = (id) => {
  const itemConfig = formItems.find(item => item.id === id);
  const label = itemConfig ? itemConfig.label : '';
  const placeholders = {
    'adminId': '管理员 ID (通常不可编辑)',
    'name': '请输入姓名',
    'gender': '请选择性别',
    'idCardNumber': '请输入身份证号码',
    'phoneNumber': '请输入手机号',
    'serviceArea': '请输入服务地区 (例如：北京市海淀区)', // 更新占位符
    'currentPosition': '请输入当前职务',
    'permissionLevel': '请选择权限等级'
  };
  return placeholders[id] || `请输入${label}`;
};

// --- 数据初始化逻辑 ---
// 监听 store 中 detailedAdminInfo 的变化，并更新 form
watch(() => adminStore.detailedAdminInfo, (newInfo) => {
  console.log("AdminStore detailedAdminInfo changed, updating form:", JSON.parse(JSON.stringify(newInfo)));
  if (newInfo && newInfo.adminId) {
    formData.value.adminId = newInfo.adminId;
    formData.value.name = newInfo.name || '';
    formData.value.gender = newInfo.gender || '';
    formData.value.idCardNumber = newInfo.idCardNumber || '';
    formData.value.phoneNumber = newInfo.phoneNumber || '';
    formData.value.currentPosition = newInfo.currentPosition || '';
    formData.value.permissionLevel = newInfo.permissionLevel || '';
    formData.value.serviceArea = newInfo.serviceArea || ''; // 直接赋值

    // formData.value.password = newInfo.password || ''; // 密码通常不在此处填充
  } else {
    // 如果 detailedAdminInfo 为空或无效，则清空表单
    Object.keys(formData.value).forEach(key => {
        if (key !== 'password') {
             formData.value[key] = '';
        }
    });
    console.log("AdminStore detailedAdminInfo is empty or invalid, form cleared.");
  }
}, { immediate: true, deep: true });

onMounted(() => {
  adminStore.initializeStore();

  if (adminStore.isAuthenticated && (!adminStore.detailedAdminInfo || !adminStore.detailedAdminInfo.adminId)) {
    console.log("Admin Profile mounted: Detailed admin info missing or incomplete, fetching now.");
    adminStore.fetchDetailedAdminInfo();
  } else if (adminStore.isAuthenticated && adminStore.detailedAdminInfo && adminStore.detailedAdminInfo.adminId) {
    console.log("Admin Profile mounted: Detailed admin info already in store. FormData should be populated by watcher.");
  }
  // 移除了 onMounted 中设置 selectedProvince 和调用 updateDistricts 的逻辑
});

const isEditing = ref(false);
const handleChangePassword = () => {
  router.push('/managerPassword'); // 确保路由名称正确
};

const handleModifyInfo = () => {
  isEditing.value = true;
  ElMessage.info('个人信息表单已可编辑');
};

const handleSaveInfo = async () => {
  // serviceArea 现在直接从 formData.value.serviceArea 获取，无需特殊组合
  console.log('准备保存的管理员信息:', JSON.parse(JSON.stringify(formData.value)));
  try {
    const dataToSave = {...formData.value};
    delete dataToSave.password;

    const result = await adminStore.updateAdminInfo(dataToSave);
    if (result && result.code === "200") {
      ElMessage.success('个人信息保存成功！');
      isEditing.value = false;
    } else {
      ElMessage.error(result.msg || '保存失败，请稍后再试。');
    }
  } catch (error) {
    console.error("保存管理员信息失败:", error);
    ElMessage.error('保存过程中发生错误。');
  }
};
</script>


<style scoped>
.admin-profile-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  font-family: 'Arial', sans-serif;
}

.header {
  background-color: #c0392b; /* 深红色 */
  color: white;
  display: flex;
  align-items: center;
  padding: 15px 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.logo {
  height: 50px;
  margin-right: 15px;
}

.title {
  font-size: 1.8em;
  font-weight: bold;
  margin: 0;
  flex-grow: 1; /* 让标题占据更多空间，将欢迎语推到右边 */
}

.welcome {
  font-size: 0.9em;
  margin-left: auto; /* 将欢迎信息推到最右边 */
}

.main-content {
  flex-grow: 1;
  padding: 30px;
  background-color: #f4f6f8; /* 淡灰色背景 */
  display: flex;
  justify-content: center; /* 水平居中表单容器 */
  align-items: flex-start; /* 垂直方向顶部对齐 */
  overflow-y: auto; /* 内容过多时允许滚动 */
}

.form-container {
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  width: 100%;
  max-width: 700px; /* 限制表单最大宽度 */
  display: flex;
  flex-direction: column; /* 使表单项垂直排列 */
  gap: 20px; /* 表单项之间的间距 */
}

.form-item {
  display: flex;
  flex-direction: column; /* 标签和输入框垂直排列 */
  gap: 8px; /* 标签和输入区域之间的间距 */
}

.form-label {
  font-weight: bold;
  color: #333;
  font-size: 0.95em;
  display: flex; /* 用于对齐星号和文本 */
  align-items: center;
}

.required {
  color: #e74c3c; /* 红色星号 */
  margin-right: 4px;
  font-weight: bold;
}

.form-input-area {
  display: flex; /* 用于并排显示省市下拉框 */
  width: 100%;
}

.form-input,
.form-select,
.select-province,
.select-district {
  width: 100%; /* 输入框和下拉框占满可用宽度 */
  padding: 10px 12px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 1em;
  box-sizing: border-box; /* 确保 padding 和 border 不会增加元素的总宽度 */
}

.form-input:disabled,
.form-select:disabled,
.select-province:disabled,
.select-district:disabled {
  background-color: #f0f0f0;
  cursor: not-allowed;
  color: #777;
}


.service-area-selects {
  display: flex;
  gap: 10px; /* 省份和市区下拉框之间的间距 */
  width: 100%;
}

.select-province,
.select-district {
  flex: 1; /* 让两个下拉框平分宽度 */
}


.button-container {
  display: flex;
  gap: 15px;
  justify-content: center; /* 按钮居中 */
  margin-top: 25px;
  padding-top: 20px;
  border-top: 1px solid #eee; /* 分隔线 */
}

.action-button {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 1em;
  font-weight: bold;
  transition: background-color 0.2s ease, transform 0.1s ease;
}

.action-button:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.action-button.primary {
  background-color: #27ae60; /* 绿色 */
  color: white;
}

.action-button.primary:hover {
  background-color: #229954;
}

/* 默认按钮样式 (修改密码, 修改个人信息) */
.action-button:not(.primary) {
  background-color: #e74c3c; /* 红色 */
  color: white;
}

.action-button:not(.primary):hover {
  background-color: #c0392b;
}


/* 响应式调整 */
@media (max-width: 600px) {
  .header {
    flex-direction: column;
    align-items: flex-start;
    padding: 15px;
  }

  .title {
    margin-top: 10px;
    margin-left: 0;
  }

  .welcome {
    margin-top: 8px;
    margin-left: 0;
    align-self: flex-start;
  }

  .main-content {
    padding: 15px;
  }

  .form-container {
    padding: 20px;
  }

  .service-area-selects {
    flex-direction: column; /* 在小屏幕上，省市下拉框垂直排列 */
  }

  .button-container {
    flex-direction: column;
  }

  .action-button {
    width: 100%;
  }
}
</style>
