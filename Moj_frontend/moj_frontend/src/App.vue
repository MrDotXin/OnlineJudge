<template>
  <div v-if="route.fullPath.startsWith('/backend')">
      <router-view />
  </div>
  <div v-else>
    <MainLayout></MainLayout>
  </div>
</template>

<script setup lang="ts">
  import MainLayout from '@/Layout/MainLayout.vue'
  import { useRoute } from 'vue-router'

  const route = useRoute();



</script>

