import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/authStore'

const app = createApp(App)

app.use(createPinia())

const authStore = useAuthStore()
await authStore.init()
app.provide('authStore', authStore)

app.use(router)

app.mount('#app')
