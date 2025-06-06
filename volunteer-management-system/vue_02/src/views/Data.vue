<template>

  <div class="card">
<!--查询-->
    <el-card>
      <el-input style="width: 240px" v-model:type="data.name" placeholder="请输入查找内容" prefix-icon="Search"></el-input>
      <el-button style="margin-left: 20px" type="info">查询</el-button>
      <el-button style="margin-left: 20px" type="info">重置</el-button>
    </el-card>
<!--按钮-->
    <el-card>
      <el-button style="margin-left: 20px" type="info">新增</el-button>
      <el-button style="margin-left: 20px" type="info">批量删除</el-button>
      <el-button style="margin-left: 20px" type="info">导入</el-button>
      <el-button style="margin-left: 20px" type="info">导出</el-button>
    </el-card>

<!--表格-->
  <el-card>
    <div style="margin: 20px">
        <el-table :data="data.tabledata" stripe style="width: 100%">
        <el-table-column prop="name" label="人名" width="180" />
        <el-table-column prop="sex" label="性别" width="180" />
        <el-table-column prop="country" label="国家" />
          <el-table-column label="操作栏">
  <!--scope:取一整行-->
            <template #default="scope">
              <el-button type="danger" @click="del(scope.row.id)">删除</el-button>
              <el-button type="primary">
                <el-icon  @click="log(scope.row)"><Edit/></el-icon>
              </el-button>
            </template>
          </el-table-column>
    </el-table>
    </div>
<!--目录-->
    <div style="margin: 20px">
      <el-pagination
        v-model:current-page="data.currentPage"
        v-model:page-size="data.pageSize"
        :page-sizes="[5,10,15,20]"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="data.total"
      />
    </div>
    </el-card>
  </div>
</template>

<script setup>
import {reactive} from "vue";
import {Edit, Search} from "@element-plus/icons-vue";


const data=reactive({
  name:null,
  tabledata:[
    {id:1,name:'于晓璘',sex:'男',country:'中国'},
    {id:2,name:'文太裕',sex:'男',country:'韩国'},
    {id:3,name:'刘演锡',sex:'男',country:'韩国'},
    {id:4,name:'杨颖',sex:'女',country:'中国'}
  ],
  currentPage:1,
  pageSize:4,
  dialogVisible:false,
  total:47,
  row:null
})
const del=(id) => {
alert("删除id="+id+"的数据")
}

const log=(row)=>{
  data.row = row
  data.dialogVisible=true
}
</script>
