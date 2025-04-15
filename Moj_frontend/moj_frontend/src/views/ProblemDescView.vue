<template>
    <div>
        <div class="scroll-container" style="height: 77vh; overflow: auto; width: 32vw;">
            <a-card>
                <template #title>
                    <div class="question_title">
                        <h2>{{ props.quesitons.title }}</h2>
                    </div>
                </template>
                <div style="display: flex; justify-content: space-between; ">
                    <div>
                        <icon-schedule :size="16" />
                        <span style="color: rgba(0,0,0,0.60)">
                            时间限制: {{ props.quesitons.judgeConfig?.timeLimit }} ms
                        </span>
                    </div>
                    <div>
                        <icon-interaction :size="16" />
                        <span style="margin-right: 6vw; color: rgba(0,0,0,0.60)">
                            空间限制: {{ props.quesitons.judgeConfig?.memoryLimit }} KB
                        </span>
                    </div>
                </div>
                <h3>题目描述</h3> 
                <MdPreview :id="id" :modelValue="props.quesitons.content"></MdPreview>

                <h3>相关信息</h3>
                    <a-collapse>
                    <a-collapse-item header="相关标签" v-for="(item, index) of props.quesitons.tags" :key="index">
                        <template #expand-icon="{ active }">
                            <icon-tag v-if="!active" />
                            <icon-tag v-else style="color: blue" />
                        </template>
                        <a-tag :color="GetTagColor(item)" :bordered="true">
                            {{ item }}
                        </a-tag>
                    </a-collapse-item>
                </a-collapse>
            </a-card>

            <div style="height: 100vh"></div>
        </div>
    </div>
</template>


<script setup lang="ts">


import { defineProps, PropType } from 'vue'
import type { QuestionVO } from '../backend/question'
// Mark down editor 
import { MdPreview } from 'md-editor-v3';
import 'md-editor-v3/lib/preview.css';
import GetTagColor from '../config/ProblemTagsColorTable';

const id = 'preview-only';

const props = defineProps({
    quesitons: {
        type: Object as PropType<QuestionVO>,
        required: true
    }
});


</script>

<style scoped>
.scroll-container {
    height: 70vh;
    overflow: auto;
    /* 美化滚动条 */
    scrollbar-width: thin;
    scrollbar-color: var(--color-neutral-3) transparent;
}

/* 滚动条样式（可选） */
.scroll-container::-webkit-scrollbar {
    width: 6px;
}

.scroll-container::-webkit-scrollbar-thumb {
    background: var(--color-neutral-3);
    border-radius: 3px;
}

.question_title {
    color: rgba(37, 45, 86, 0.852);
}

#preview-only {
    margin-left: 2vw;
}
</style>