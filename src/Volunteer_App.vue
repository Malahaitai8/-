<template>
  <div id="app">
    <div class="system-title">
      <h1 class="art-title">志愿者管理系统</h1>
    </div>
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
.system-title {
  text-align: center;
  margin: 0;
  padding: 20px 0 10px 0;
  background: linear-gradient(to right, #ff0000, #ff6666);
  border-radius: 0 0 16px 16px;
  box-shadow: 0 4px 12px rgba(255, 0, 0, 0.08);
  position: relative;
  z-index: 1000;
}

.art-title {
  margin: 0;
  font-size: 36px;
  font-weight: bold;
  color: #fff;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
  letter-spacing: 4px;
  font-family: "Microsoft YaHei", "微软雅黑", sans-serif;
  background: linear-gradient(to bottom, #fff, #ffe6e6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: titleGlow 2s ease-in-out infinite alternate;
}

@keyframes titleGlow {
  from {
    text-shadow: 0 0 10px rgba(255, 0, 0, 0.5);
  }
  to {
    text-shadow: 0 0 20px rgba(255, 0, 0, 0.8);
  }
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  height: 100vh;
  margin: 0;
  padding: 0;
  background: #f7f7f7;
}

/* 自定义主题样式 */
:root {
  --el-color-primary: #ff0000;
  --el-color-primary-light-3: #ff6666;
  --el-color-primary-light-5: #ff9999;
  --el-color-primary-light-7: #ffcccc;
  --el-color-primary-light-9: #ffe6e6;
  --el-color-primary-dark-2: #cc0000;
}

/* 菜单激活状态颜色 */
.el-menu-item.is-active {
  color: #ff0000 !important;
  border-right: 2px solid #ff0000;
}

/* 菜单hover状态颜色 */
.el-menu-item:hover {
  background-color: #ffe6e6 !important;
}

/* 按钮主题色 */
.el-button--primary {
  --el-button-bg-color: #ff0000;
  --el-button-border-color: #ff0000;
  --el-button-hover-bg-color: #cc0000;
  --el-button-hover-border-color: #cc0000;
}

/* 链接颜色 */
a {
  color: #ff0000;
}

a:hover {
  color: #cc0000;
}
</style> 