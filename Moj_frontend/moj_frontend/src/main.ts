import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import '@arco-design/web-vue/dist/arco.css'
import ArcoVue from '@arco-design/web-vue'
import ArcoIcon from '@arco-design/web-vue/es/icon'
import { createPinia } from 'pinia'

import '@/Authority'

createApp(App)
    .use(router)
    .use(ArcoVue)
    .use(ArcoIcon)
    .use(createPinia())
.mount('#app')

