import { createRouter, createWebHistory } from 'vue-router'
import VocabHome from '../views/VocabHome.vue'
import VocabLibrary from '../views/VocabLibrary.vue'
import AdminDashboard from '../views/AdminDashboard.vue'
import LoginView from '../views/LoginView.vue'
import { STANDALONE } from '../config/mode.js'

const routes = [
  {
    path: '/',
    redirect: () => {
      // 单机版无需登录, 直接进学习页
      if (STANDALONE) return '/home'
      return localStorage.getItem('current_user') ? '/home' : '/login'
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginView
  },
  {
    path: '/home',
    name: 'Home',
    component: VocabHome
  },
  {
    path: '/library',
    name: 'Library',
    component: VocabLibrary
  },
  {
    path: '/admin',
    name: 'Admin',
    component: AdminDashboard
  }
]

const router = createRouter({
  // 单机版可能部署在子路径 (GitHub Pages) 或本地 file:// 打开, base 跟随构建配置
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
