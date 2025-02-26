import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { LoginUserVO } from '@/backend'
import { UserControllerService } from '@/backend'

interface LoginParams {
  userAccount: string
  userPassword: string
}

export const UserStore = defineStore('user', () => {

  const loginUser = ref<LoginUserVO | null>(null)
  const isLoggedIn = ref(false)

  const fetchCurrentUser = async () => {
    try {
      const res = await UserControllerService.getLoginUserUsingGet()
      if (res.code === 0) {
        isLoggedIn.value = true;
        loginUser.value = res.data as LoginUserVO
      } else {
        isLoggedIn.value = false;
        loginUser.value = null;
      }
    } catch (error) {
      clearUserData()
    }
  }

  const userLogin = async (params: LoginParams) => {
    try {
      const res = await UserControllerService.userLoginUsingPost(params);
      
      if (res.code === 0) {
        loginUser.value = res.data as LoginUserVO;
        isLoggedIn.value = true;
      } 

      return {
        code : res.code,
        message  : res.message
      };

    } catch (error) {
      clearUserData();
    }
  }

  const userLogout = async () => {
    try {
      await UserControllerService.userLogoutUsingPost()
    } finally {
      clearUserData();
    }
  }

  const clearUserData = () => {
    loginUser.value = null
    isLoggedIn.value = false
  }

  if (!isLoggedIn.value) {
    fetchCurrentUser()
  }

  return {
    loginUser,
    isLoggedIn,
    userLogin,
    userLogout,
    fetchCurrentUser
  }
})