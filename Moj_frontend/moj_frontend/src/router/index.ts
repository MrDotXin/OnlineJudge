import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/home/problemset'
  },
  {
    path: '/home/problemset',
    name: 'home',
    component: HomeView
  },
  {
    path: '/home/contests',
    name: 'contests',
    component: () => import ('../views/ContestsView.vue')
  },
  {
    path: '/problems/:id',
    name: 'problems',
    component: () => import ('../views/ProblemsView.vue')
  },
  {
    path: '/login',
    name: 'userLogin',
    component: () => import ('../views/UserLoginView.vue')
  },
  {
    path: '/register',
    name: 'userRegister',
    component: () => import ('../views/UserRegisterView.vue')
  },
  
  {
    path: '/noAuthView',
    name: 'noAuthView',
    component: () => import ('../views/NoAuthView.vue')
  },
  
  {
    path: '/backend',
    redirect: '/backend/entry',
  },
  {
    path: '/backend/entry',
    name: 'backendEntry',
    component: () => import ('../views/backend/BackEndEntry.vue')
  },
  {
    path: '/backend/center',
    name: 'backendManage',
    component: () => import ('../views/backend/BackendManageView.vue'),
    meta: {
      'target': 'admin'
    }
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
