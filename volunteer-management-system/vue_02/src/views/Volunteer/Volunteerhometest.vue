<template>
  <div class="volunteer-dashboard">
    <h1>志愿者信息面板</h1>
      <table>
        <thead>
          <tr>
            <th>属性</th>
            <th>值</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <th>用户名</th>
            <td>{{ volunteerInfo.username || '未提供' }}</td>
          </tr>
          <tr>
            <th>志愿者ID</th>
            <td>{{ volunteerInfo.volunteerId || '未提供' }}</td>
          </tr>
          <tr>
            <th>真实姓名</th>
            <td>{{ volunteerInfo.name || '未提供' }}</td>
          </tr>
          <tr>
            <th>手机号</th>
            <td>{{ volunteerInfo.phone || '未提供' }}</td>
          </tr>
          <tr>
            <th>身份证号</th>
            <td>{{ volunteerInfo.idCard || '未提供' }}</td>
          </tr>
          <tr>
            <th>注册时间</th>
            <td>{{ volunteerInfo.registrationTime || '未提供' }}</td>
          </tr>
          <tr>
            <th>国籍</th>
            <td>{{ volunteerInfo.country || '未提供' }}</td>
          </tr>
          <tr>
            <th>性别</th>
            <td>{{ volunteerInfo.gender || '未提供' }}</td>
          </tr>
          <tr>
            <th>民族</th>
            <td>{{ volunteerInfo.ethnicity || '未提供' }}</td>
          </tr>
          <tr>
            <th>政治面貌</th>
            <td>{{ volunteerInfo.politicalStatus || '未提供' }}</td>
          </tr>
          <tr>
            <th>最高学历</th>
            <td>{{ volunteerInfo.educationLevel || '未提供' }}</td>
          </tr>
          <tr>
            <th>从业情况</th>
            <td>{{ volunteerInfo.occupation || '未提供' }}</td>
          </tr>
          <tr>
            <th>服务类别</th>
            <td>{{ volunteerInfo.serviceCategory || '未提供' }}</td>
          </tr>
          <tr>
            <th>服务区域</th>
            <td>{{ volunteerInfo.serviceArea || '未提供' }}</td>
          </tr>
          <tr>
            <th>志愿总时长</th>
            <td>{{ volunteerInfo.totalServiceHours || 0 }}</td>
          </tr>
          <tr>
            <th>志愿者综合评分</th>
            <td>{{ volunteerInfo.volunteerComprehensiveScore || 0 }}</td>
          </tr>
          <tr>
            <th>培训总时长</th>
            <td>{{ volunteerInfo.totalTrainingHours || 0 }}</td>
          </tr>
          <tr>
            <th>账户状态</th>
            <td>{{ volunteerInfo.accountStatus || '未提供' }}</td>
          </tr>
        </tbody>
      </table>
    </div>
</template>

<script setup>
import { reactive, toRefs, onMounted } from 'vue'
import request from '@/utils/request.js'

// 1. 读取本地用户信息
const userStr = localStorage.getItem('xm-pro-user')
const user = userStr ? JSON.parse(userStr) : { username: '' }

// 2. 响应式志愿者信息
const volunteerInfo = reactive({
  volunteerId: '',
  username: '',
  name: '',
  phone: '',
  idCard: '',
  password: '',
  registrationTime: '',
  country: '',
  gender: '',
  ethnicity: '',
  politicalStatus: '',
  educationLevel: '',
  occupation: '',
  serviceCategory: '',
  serviceArea: '',
  totalServiceHours: 0,
  volunteerComprehensiveScore: 0,
  totalTrainingHours: 0,
  accountStatus: ''
})

// 3. 获取志愿者信息
onMounted(() => {
  if (user.username) {
    request.get('/volunteer/selectByUsername', {
      params: {
        username: user.username.trim()
      }
    })
    .then(res => {
      if (res.data) {
        // 只更新属性，避免响应式失效
        Object.assign(volunteerInfo, res.data)
      }
    })
    .catch(() => {
      // 可以加个错误提示
    })
  }
})
</script>
