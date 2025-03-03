<template>
    <a-layout style="height: 100vh;">
        <!-- <a-layout-header style="background-color: rgba(255,255,255);">JFCD</a-layout-header> -->
        <a-layout>
            <a-layout-sider 
                style="width: 49vw; background-color: aliceblue" >
                <span style="position: absolute; top: 35%; left: 40%; font-size: 30px;   font-weight: bold;">
                    MOJ Backend
                    <div style="margin-top: 10vh;">
                        <ParingCubers />
                    </div>  
                </span>    
            </a-layout-sider>
            <a-layout-content>
                <FormBackendLogin v-if="isLogin"
                    style="margin: auto; margin-top: 20vh; transform: scale(1.2);" 
                    @switchLogin="onSwitchLogin"
                    @login="onLogin"
                />
                    <FormBackendRegister v-else 
                    style="margin: auto; margin-top: 20vh; transform: scale(1.2);" 
                    @switchLogin="onSwitchLogin"
                    @register = "onRegister"
                />

            </a-layout-content>
        </a-layout>
    </a-layout>
</template>


<script setup lang="ts">
import { ref, onMounted } from 'vue'

import { UserStore } from '@/store/user'

import ParingCubers from '@/components/Loader/ParingCubers.vue'
import FormBackendLogin from '@/components/Form/FormBackendLogin.vue' 
import FormBackendRegister from '@/components/Form/FormBackendRegister.vue' 

import { Message } from '@arco-design/web-vue'

import { useRouter } from 'vue-router'

const isLogin = ref(true);

const backendStore = UserStore();

const router = useRouter();

const onSwitchLogin = () => {
    isLogin.value = !isLogin.value;
}

const onLogin = async (account : string, password : string) => {
    let res = await backendStore.userLogin({
        userPassword: password,
        userAccount: account
    });

    

    if (res?.code !== 0) {
        Message.error(res?.message as string);
    } else {
        router.push('/backend/center');
    }
}

const onRegister = async () => {
    console.log("register");
}

onMounted(async () => { 
    let res = await backendStore.checkLogin(); 
    if (res) {
        router.push('/backend/center');
    }
});

</script>

