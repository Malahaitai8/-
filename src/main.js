import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './Volunteer_App.vue'
import router from './router'

const app = createApp(App)
app.use(ElementPlus, {
  theme: {
    primary: '#ff0000'
  }
})
app.use(router)
app.mount('#app') 