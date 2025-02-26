<template>
    <a-space direction="horizontal" style="display: flex; justify-content: space-between;">
        <div :style="{padding: '1px' }">
            <a-page-header
            :style="{ background: 'var(--color-bg-2)', height: '5px'}"
            subtitle="线上OJ平台"
            :show-back="false"
            >
                <template #title>
                    <a-space align="center">
                        <img src="@/assets/dog.png" width="20px" height="20px"/>
                        <span :style="{ marginLeft: '5px' }">MOJ</span>
                    </a-space>
                </template> 
            </a-page-header>
        </div>
        <div v-if="route.fullPath.startsWith('/home')">
            <TabLink></TabLink>
        </div>
        <div style="margin-top: 4vh; padding-right: 5vw;">
            <UserBrief />
        </div>
    </a-space>
    <a-divider></a-divider>
</template>

<script lang="ts" setup>
import UserBrief from '@/components/UserBrief.vue'
import TabLink from '@/components/TabLinks.vue'
import { useRoute } from 'vue-router'

const route = useRoute();



</script>