<template>
    <div>
        11
    </div>
</template>


<script setup lang="ts">

import { defineProps, onMounted } from 'vue';
const props = defineProps({
    questionId: {
        type: String,
        required: true
    }
});

onMounted(()=>{
    props;
});

</script>