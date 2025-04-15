<template>
    <div>
        <UserRegisterView class="FormStyle" @submit="onUserRegister"  />
    </div>
</template>


<script lang="ts" setup>
import UserRegisterView from '../components/Form/UserRegisterForm.vue'
import { useRouter } from 'vue-router'
import { Message } from '@arco-design/web-vue'
import { UserStore } from '../store/user'

const router = useRouter();
const userStore = UserStore();

async function onUserRegister(account : string, password : string) {
    const response = await userStore.userRegister({ userAccount: account, userPassword: password, checkPassword: password });
    if (response?.code === 0) {
        Message.success({content: '注册成功!', duration: 500});
        
        router.back();   
    } else {
        Message.error({content: '注册失败! ' + response?.message, duration: 1000});
    }
}
</script>


<style scoped>

.FormStyle {
    position: absolute; 
    top: 8%; 
    left: 30%;
}

</style>