# 组织ID问题调试指南

## 问题现象
提交申请志愿活动时显示"发布活动的组织不存在"，但数据库中明明存在该组织。

## 可能的原因分析

### 1. 前端组织ID获取问题
前端可能没有正确获取到组织ID，或获取到的ID与数据库中的ID不匹配。

**调试方法**：
1. 打开浏览器开发者工具（F12）
2. 进入申请志愿活动页面
3. 查看Console控制台输出的日志：
   - `获取到的组织ID: xxxxx`
   - `提交的请求数据: {...}`

### 2. 数据库中组织ID格式问题
数据库中的组织ID可能与前端获取的格式不一致。

**检查方法**：
```sql
-- 查看数据库中的组织数据
SELECT OrgID, OrgName, OrgLoginUserName, OrgAccountStatus 
FROM tbl_Organization 
ORDER BY OrgID;
```

### 3. 组织状态问题
组织可能存在但状态不是"已认证"。

## 解决步骤

### 步骤1：检查前端组织ID
1. 登录组织账号
2. 进入申请志愿活动页面
3. 打开开发者工具查看Console输出
4. 记录显示的组织ID

### 步骤2：检查组织Store状态
在浏览器Console中执行：
```javascript
// 检查localStorage中的组织信息
console.log('localStorage组织信息:', localStorage.getItem('xm-pro-organization'));

// 检查组织store状态（如果使用Vue DevTools）
// 查看Pinia store中的organizationStore状态
```

### 步骤3：检查数据库数据
连接数据库执行以下查询：
```sql
-- 查看所有组织
SELECT OrgID, OrgName, OrgLoginUserName, OrgAccountStatus 
FROM tbl_Organization;

-- 查看特定组织（替换为实际的组织ID）
SELECT * FROM tbl_Organization WHERE OrgID = '你的组织ID';

-- 查看组织登录用户名对应的组织ID
SELECT OrgID, OrgLoginUserName FROM tbl_Organization WHERE OrgLoginUserName = '你的登录用户名';
```

### 步骤4：检查后端日志
启动后端服务后，提交申请时查看控制台输出：
```
正在验证组织ID: xxxxx
查询到的组织信息: xxxxx (状态: xxxxx) 或 null
```

## 常见解决方案

### 方案1：修复组织ID获取
如果前端获取的组织ID为空或错误：

1. 确保组织已正确登录
2. 检查组织store是否正确初始化
3. 验证`detailedOrganizationInfo.orgId`是否有值

### 方案2：更新组织状态
如果组织存在但状态不是"已认证"：

```sql
-- 更新组织状态为已认证（替换为实际的组织ID）
UPDATE tbl_Organization 
SET OrgAccountStatus = '已认证' 
WHERE OrgID = '你的组织ID';
```

### 方案3：创建测试组织
如果数据库中没有合适的组织数据：

```sql
-- 插入测试组织数据
INSERT INTO tbl_Organization (
    OrgID, OrgName, OrgLoginUserName, OrgLoginPassword, 
    ContactPersonPhone, ServiceRegion, OrgScale, OrgAccountStatus
) VALUES (
    'ORG000000000001', '测试志愿组织', 'testorg', 'password123',
    '13800138000', '北京', 100, '已认证'
);
```

## 临时解决方案

如果需要快速测试，可以临时修改前端代码：

```javascript
// 在 Organization-ApplyActivity.vue 中临时硬编码组织ID
getOrgId() {
  // 临时使用已知存在的组织ID
  return 'ORG000000000001'; // 替换为数据库中实际存在的组织ID
}
```

## 验证修复
修复后，提交申请时应该：
1. 前端Console显示正确的组织ID
2. 后端日志显示找到组织信息
3. 申请成功提交，显示成功消息

## 注意事项
- 确保数据库服务正常运行
- 检查网络连接和API代理配置
- 确认组织登录状态有效
- 验证数据库表中的字段名和数据类型正确 