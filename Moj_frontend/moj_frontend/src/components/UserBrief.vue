<template>
    <div v-if="!userStore.checkLogin()">
        <span class="text_hint no_select" @click="onToLogin()">登录</span>
        <span style="margin-left: 10px; margin-right: 10px; font-size: 15px">或</span>
        <span class="text_hint no_select" @click="onToRegister()">注册</span>
    </div>
    <div v-else>
        <a-popover title="" trigger="click" style="width: 25vw;">
            <span class="clickable no-select">{{ userStore.loginUser?.userName }}</span>
            <template #content>
                <a-space direction="vertical">
                    <a-button type="text" style="width: 20vw; color: rgba(0, 0, 0, 0.5);">
                        <template #icon>
                            <icon-idcard size="large" style="margin-top: 0.5vh;" />
                        </template>
                        <span style="font-size: 15px">用户设置</span>
                    </a-button>
                    <a-button type="text" style="width: 20vw; color: rgba(0, 0, 0, 0.5);">
                        <template #icon>
                            <icon-user size="large" style="margin-top: 0.5vh;" />
                        </template>
                        <span style="font-size: 15px" @click="onToUserSpace">个人空间</span>
                    </a-button>
                    <a-button type="text" style="width: 20vw; color: rgba(0, 0, 0, 0.5);">
                        <template #icon>
                            <icon-user size="large" style="margin-top: 0.5vh;" />
                        </template>
                        <span style="font-size: 15px" @click="onToUserSpace">我的申请</span>
                    </a-button  >
                    <a-button type="text" style="width: 20vw; color: rgba(0, 0, 0, 0.5);" @click="onUserLogout()">
                        <template #icon>
                            <icon-poweroff size="large" style="margin-top: 0.5vh;" />
                        </template>
                        <span style="font-size: 15px">用户登出</span>
                    </a-button>
                </a-space> 
            </template>
        </a-popover>
    </div>
</template>

<script setup lang="ts">
import { UserStore } from '../store/user'
import { useRouter } from 'vue-router'

const router = useRouter();

const userStore = UserStore();
console.log(userStore);


async function onToLogin() {
    router.push('/login');
}

async function onToRegister() {
    router.push('/register');
}

async function onToUserSpace() {
    router.push(`/user/space`);
}

async function onUserLogout() {
    userStore.userLogout();
}

</script>

<style scoped>

.text_hint {
    color: rgba(0, 0, 0, 0.5);
    font-size: 15px;
    &:hover {
        cursor: pointer;
        color: rgba(0, 0, 0, 0.87);
    }

    &:active {
        cursor: arrow;
        color: rgba(0, 0, 0, 0.5);
    }
}

.no_select {
    user-select: none;
}

.clickable {
    &:hover {
        cursor: pointer;
    }

    &:active {
        cursor: arrow;
    }
}

</style>