import { createRouter, createWebHistory } from 'vue-router'
import VocabHome from '../views/VocabHome.vue'
import VocabLibrary from '../views/VocabLibrary.vue'
import AdminDashboard from '../views/AdminDashboard.vue'
import LoginView from '../views/LoginView.vue'

const routes = [
  {
    path: '/',
    redirect: () => {
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
  history: createWebHistory(),
  routes
})

export default router
