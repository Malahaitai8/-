<template>
  <div>
<div class="card">
<!--查询-->
    <el-card>
      <el-input style="width: 240px" v-model="data.name" placeholder="请输入查找内容" prefix-icon="Search"></el-input>
      <el-button @click="load" style="margin-left: 20px" type="info">查询</el-button>
      <el-button style="margin-left: 20px" type="info">重置</el-button>
    </el-card>
<!--按钮-->
    <el-card>
      <el-button style="margin-left: 20px" type="info" @click="handleAdd">新增</el-button>
      <el-button style="margin-left: 20px" type="info" @click="delBatch">批量删除</el-button>
<!--      <el-button style="margin-left: 20px" type="info">导入</el-button>-->
<!--      <el-button style="margin-left: 20px" type="info">导出</el-button>-->
    </el-card>

<!--表格-->
  <el-card>
    <div style="margin: 20px">
        <el-table :data="data.tabledata" stripe  @selection-change="handleSlectionChange" style="width: 100%">
        <el-table-column type="selection" width="55"/>
        <el-table-column prop="username" label="用户名" width="180" />
        <el-table-column prop="name" label="人名" width="180" />
        <el-table-column prop="sex" label="性别" width="180" />
        <el-table-column prop="no" label="工号" width="180" />
        <el-table-column prop="age" label="年龄" width="180" />
        <el-table-column prop="description" label="个人简介" width="180" show-overflow-tooltip />
        <el-table-column prop="departmentId" label="部门" width="180" />
        <el-table-column label="操作栏" width="180">
  <!--scope:取一整行-->
            <template #default="scope">
              <el-button  @click="delrow(scope.row.id)" type="danger" :icon="Delete" circle></el-button>
              <el-button  @click="handleUpdate(scope.row)" type="primary" :icon="Edit" circle>
              </el-button>
            </template>
          </el-table-column>
    </el-table>
    </div>
<!--目录-->
    <div style="margin: 20px">
      <el-pagination
          @current-change="load"
          @size-change="load"
        v-model:current-page="data.pageNum"
        v-model:page-size="data.pageSize"
        :page-sizes="[5,10,15,20]"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="data.total"
      />
    </div>
    </el-card>
  </div>

    <el-dialog v-model="data.formVisible" title="基本信息" width="500" destroy-on-close>
    <el-form ref="formRef" :rules="data.rules" :model="data.form" style="padding-right: 10px">
      <el-form-item label="id" label-width="80px">
        <el-input v-model="data.form.id" autocomplete="off" />
      </el-form-item>
<!--      prop要和data.rules对应-->
      <el-form-item label="用户名" prop="username" label-width="80px">
        <el-input v-model="data.form.username" autocomplete="off" />
      </el-form-item>
      <el-form-item label="密码" prop="password" label-width="80px">
        <el-input type="password" v-model="data.form.password" autocomplete="off" />
      </el-form-item>
      <el-form-item label="角色" prop="role" label-width="80px">
        <el-input v-model="data.form.role" autocomplete="off" />
      </el-form-item>
      <el-form-item label="姓名" prop="name" label-width="80px">
        <el-input v-model="data.form.name" autocomplete="off" />
      </el-form-item>
      <el-form-item label="性别" prop="sex" label-width="80px">
        <el-radio-group v-model="data.form.sex">
          <el-radio label="男" value="男"></el-radio>
          <el-radio label="女" value="女"></el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="工号" prop="no" label-width="80px">
        <el-input v-model="data.form.no" autocomplete="off" />
      </el-form-item>
      <el-form-item label="年龄" prop="age" label-width="80px">
        <el-input-number v-model="data.form.age" autocomplete="off" />
      </el-form-item>
      <el-form-item label="个人简介" label-width="80px">
        <el-input :rows="3" type="textarea" v-model="data.form.description" autocomplete="off" />
      </el-form-item>
      <el-form-item label="部门id" label-width="80px">
        <el-input-number v-model="data.form.departmentId" autocomplete="off" />
      </el-form-item>


    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="data.formVisible = false">取消</el-button>
        <el-button type="primary" @click="save">
          保存
        </el-button>
      </div>
    </template>
  </el-dialog>


  </div>
</template>

<script setup>
import {reactive,ref} from "vue";
import {Delete, Edit, Search} from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";


const data=reactive({
  name:null,
  tabledata:[],
  currentPage:1,
  pageSize:10,
  pageNum:1,
  dialogVisible:false,
  total:0,
  row:null,
  formVisible:false,
  form:{},
  isEditing:false,
  ids:[],
  rules:{
    username:[
      {required:true,message:'请输入账号',trigger:'blur'},
    ],
    password:[
      {required:true,message:'请输入密码',trigger:'blur'},
    ],
    role:[
      {required:true,message:'请输入角色',trigger:'blur'},
    ],
    name:[
      {required:true,message:'请输入姓名',trigger:'blur'},
    ],
    sex:[
      {required:true,message:'请输入性别',trigger:'blur'},
    ],
    no:[
      {required:true,message:'请输入工号',trigger:'blur'},
    ],
    age:[
      {required:true,message:'请输入年龄',trigger:'blur'},
    ]

  }
})

const load = () =>{
  request.get('/employee/selectPage',{
    params:{
      pageNum:data.pageNum,
      pageSize:data.pageSize,
      name:data.name
    }
  }).then(res=>{
      data.tabledata=res.data.list
    data.total=res.data.total
    })//?pageNum=1&pageSize=10
}
load()


const del=(id) => {
alert("删除id="+id+"的数据")
}

const log=(row)=>{
  data.row = row
  data.dialogVisible=true
}

const handleAdd=()=>{
  data.isEditing=false
  data.formVisible=true
  data.form={}
}

const handleUpdate=(row)=>{
  data.isEditing=true
  data.form= JSON.parse(JSON.stringify(row))
  data.formVisible=true
}

const save=()=>{//在一个保存方法里两个操作一个新增，一个编辑
  formRef.value.validate((valid)=>{
    if(valid){
      if (data.isEditing) {
        // 如果 isEditing 为 true，执行编辑操作
        update();
      } else {
        // 如果 isEditing 为 false，执行新增操作
        insert();
      }
    }
  })

}
const insert=()=>{
  request.post('/employee/insert',data.form).then(res=>{
    if(res.code === '200'){
      data.formVisible=false
      ElMessage.success('操作成功')
      load()//一定要重新加载数据
    }else{
      ElMessage.error(res.msg)
    }
  })
}
const update=()=>{
  request.put('/employee/updateById',data.form).then(res=>{
    if(res.code === '200'){
      data.formVisible=false
      ElMessage.success('操作成功')
      load()//一定要重新加载数据
    }else{
      ElMessage.error(res.msg)
    }
  })
}

const delrow =(id)=>{
  ElMessageBox.confirm('删除数据无法修改，确认删除吗？','确认删除',{type:'warning'}).then(()=>{
  request.delete('/employee/deleteById/' +id).then(res=>{
    if(res.code === '200'){
      ElMessage.success('操作成功')
      load()//一定要重新加载数据
    }else{
      ElMessage.error(res.msg)
    }})
  }).catch()
}

const handleSlectionChange=(rows)=>{
//从选中的行数据取出所有id组成一个新的数组
  data.ids=rows.map(row=>row.id)
}
const delBatch=()=>{
  if(data.ids.length===0){
    ElMessage.warning('请选择')
    return
  }
    ElMessageBox.confirm('删除数据无法修改，确认删除吗？','确认删除',{type:'warning'}).then(()=>{
  request.delete('/employee/deleteBatch',{ data:data.ids }).then(res=>{
     if(res.code === '200'){
      ElMessage.success('操作成功')
      load()//一定要重新加载数据
    }else{
      ElMessage.error(res.msg)
    }})
  })
}

const formRef = ref()


</script>