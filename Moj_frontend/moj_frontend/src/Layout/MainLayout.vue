<template>
    <a-layout style="height: 120vh;">
        <a-layout-header style="height: 10vh;" ><GlobalHeader/></a-layout-header>
        <a-layout-content>
            <router-view />
        </a-layout-content>
        <a-layout-footer><GlobalFooter /></a-layout-footer>  
    </a-layout>
</template>


<script lang="ts" setup>
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalFooter from '@/components/GlobalFooter.vue'

</script>