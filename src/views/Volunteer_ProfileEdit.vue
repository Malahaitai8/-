<template>
  <div class="profile-edit-bg">
    <el-card class="profile-edit-card">
      <el-form :model="form" label-width="120px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.realname" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="form.idCard" />
        </el-form-item>
        <el-form-item label="国家">
          <el-input v-model="form.country" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="服务区域">
          <el-select v-model="form.region" placeholder="请选择">
            <el-option
              v-for="item in regions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="民族">
          <el-select v-model="form.nation" placeholder="请选择">
            <el-option
              v-for="item in nations"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="政治面貌">
          <el-select v-model="form.politics" placeholder="请选择">
            <el-option
              v-for="item in politics"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="最高学历">
          <el-select v-model="form.education" placeholder="请选择">
            <el-option
              v-for="item in educations"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="从业情况">
          <el-select v-model="form.occupation" placeholder="请选择">
            <el-option
              v-for="item in occupations"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="服务类别">
          <el-select v-model="form.serviceType" placeholder="请选择" clearable>
            <el-option
              v-for="item in serviceTypes"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveInfo">保存个人信息</el-button>
          <el-button type="danger" @click="showPwdDialog = true">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 修改密码弹窗 -->
    <el-dialog title="修改密码" v-model="showPwdDialog" width="400px">
      <el-form :model="pwdForm" label-width="100px">
        <el-form-item label="原密码">
          <el-input v-model="pwdForm.oldPwd" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="pwdForm.newPwd" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认新密码">
          <el-input v-model="pwdForm.confirmPwd" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPwdDialog = false">取 消</el-button>
        <el-button type="primary" @click="changePwd">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref } from 'vue'

export default {
  name: 'Profile',
  setup() {
    const regions = [
      '北京', '天津', '上海', '重庆', '河北', '山西', '辽宁', '吉林', '黑龙江',
      '江苏', '浙江', '安徽', '福建', '江西', '山东', '河南', '湖北', '湖南',
      '广东', '广西', '海南', '四川', '贵州', '云南', '西藏', '陕西', '甘肃',
      '青海', '台湾', '香港', '澳门'
    ]
    const nations = [
      '汉族', '蒙古族', '回族', '藏族', '维吾尔族', '苗族', '彝族', '壮族', '布依族', '朝鲜族', '满族', '侗族',
      '瑶族', '白族', '土家族', '哈尼族', '哈萨克族', '傣族', '黎族', '傈僳族', '佤族', '畲族', '高山族', '拉祜族',
      '水族', '东乡族', '纳西族', '景颇族', '柯尔克孜族', '土族', '达斡尔族', '仫佬族', '羌族', '布朗族', '撒拉族',
      '毛南族', '仡佬族', '锡伯族', '阿昌族', '普米族', '塔吉克族', '怒族', '乌孜别克族', '俄罗斯族', '鄂温克族',
      '德昂族', '保安族', '裕固族', '京族', '塔塔尔族', '独龙族', '鄂伦春族', '赫哲族', '门巴族', '珞巴族'
    ]
    const politics = [
      '中国共产党党员','中国共产党预备党员','中国共产主义青年团团员','中国国民党革命委员会会员',
      '中国民主同盟盟员','中国民主建国会会员','中国民主促进会会员','中国农工民主党党员',
      '中国致公党党员','九三学社社员','台湾民主自治同盟盟员','无党派民主人士','群众'
    ]
    const educations = [
      '博士研究生', '硕士研究生', '大学本科', '大学专科和专科学校', '技工学校', '高中', '初中', '小学',
      '幼儿园学龄前', '特殊教育', '文盲或半文盲', '未说明情况'
    ]
    const occupations = [
      '国家公务员', '职员', '企业管理人员', '工人', '学生', '现役军人', '自由职业', '个体经营者', '无业人员',
      '退(离)休人员', '医生', '司机', '律师', '教师', '农民', '未说明情况'
    ]
    const serviceTypes = [
      '助力复工复产志愿者', '扶贫济困志愿者', '社区志愿者', '青年志愿者', '文明志愿者', '文化志愿者',
      '医疗志愿者', '教育志愿者', '助残志愿者', '巾帼志愿者', '消防志愿者', '红十字志愿者', '税收志愿者', '疫情防控志愿者'
    ]

    const form = ref({
      username: '',
      realname: '',
      phone: '',
      idCard: '',
      country: '中国',
      gender: '',
      region: '',
      nation: '',
      politics: '',
      education: '',
      occupation: '',
      serviceType: ''
    })

    // 密码弹窗相关
    const showPwdDialog = ref(false)
    const pwdForm = ref({
      oldPwd: '',
      newPwd: '',
      confirmPwd: ''
    })

    const saveInfo = () => {
      // 这里可以提交表单
      alert('保存成功！')
    }

    const changePwd = () => {
      if (!pwdForm.value.oldPwd || !pwdForm.value.newPwd || !pwdForm.value.confirmPwd) {
        alert('请填写完整密码信息')
        return
      }
      if (pwdForm.value.newPwd !== pwdForm.value.confirmPwd) {
        alert('两次输入的新密码不一致')
        return
      }
      // 这里可以提交修改密码请求
      alert('密码修改成功！')
      showPwdDialog.value = false
      pwdForm.value.oldPwd = ''
      pwdForm.value.newPwd = ''
      pwdForm.value.confirmPwd = ''
    }

    return {
      form,
      regions,
      nations,
      politics,
      educations,
      occupations,
      serviceTypes,
      saveInfo,
      showPwdDialog,
      pwdForm,
      changePwd
    }
  }
}
</script>

<style scoped>
.profile-edit-bg {
  min-height: 600px;
  background: url('https://img.zcool.cn/community/01b1e95d5e7e6fa8012193a3e2e6e2.jpg@1280w_1l_2o_100sh.jpg') no-repeat right bottom;
  background-size: 400px auto;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 40px;
}
.profile-edit-card {
  width: 700px;
  background: rgba(255,255,255,0.95);
  box-shadow: 0 2px 12px rgba(255,0,0,0.08);
  border-radius: 12px;
}
.el-form-item {
  margin-bottom: 18px;
}
.el-button--primary,
.el-button--danger {
  margin-right: 10px;
}
</style> 