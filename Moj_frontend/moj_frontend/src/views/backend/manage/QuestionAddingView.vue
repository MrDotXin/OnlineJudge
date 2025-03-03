<template>
    <a-layout class="layout-container">
        <!-- 头部优化 -->
        <a-layout-header class="header">
            <a-card :bordered="false" class="title-card">
                <a-space direction="horizontal">
                    <a-input placeholder="输入题目标题" class="title-input" allow-clear v-model="FormData.title"
                        style="width: 40vw; margin-left: 10vw" />
                </a-space>
            </a-card>
        </a-layout-header>

        <!-- 内容区优化 -->
        <a-layout-content class="content">
            <a-space direction="vertical" :size="24" fill >
                <!-- 题目描述区块 -->
                <a-card class="section-card">
                    <template #title>
                        <div class="card-title">
                            <icon-edit /> 题目描述
                        </div>
                    </template>
                    <MdEditor v-model="text" class="editor" />
                </a-card>

                <!-- 限制条件区块 -->
                <a-card class="section-card">
                    <template #title>
                        <div class="card-title">
                            <icon-settings /> 限制条件
                        </div>
                    </template>
                    <a-form :model="FormData" layout="inline" auto-label-width class="limit-form">
                        <!-- 使用栅格系统优化布局 -->
                        <a-row :gutter="24">
                            <a-col :span="12">
                                <a-form-item field="TimeLimit" label="时间限制(ms)" class="limit-item">
                                    <a-input-number v-model="FormData.TimeLimit" placeholder="1000"
                                        class="limit-input" />
                                </a-form-item>
                            </a-col>
                            <a-col :span="12">
                                <a-form-item field="MemoryLimit" label="空间限制(KB)" class="limit-item">
                                    <a-input-number v-model="FormData.MemoryLimit" placeholder="256000"
                                        class="limit-input" />
                                </a-form-item>
                            </a-col>
                        </a-row>
                    </a-form>
                </a-card>

                <!-- 标签区块 -->
                <a-card class="section-card">
                    <template #title>
                        <div class="card-title">
                            <icon-tags /> 题目标签
                        </div>
                    </template>
                    <a-form-item field="Tags" class="tag-form">
                        <a-input-tag v-model="FormData.tags" placeholder="输入标签" allow-clear class="tag-input" />
                    </a-form-item>
                </a-card>

                <!-- 测试用例区块 -->
                <a-card class="section-card">
                    <template #title>
                        <div class="card-title">
                            <icon-experiment /> 测试用例
                        </div>
                    </template>
                    <div class="test-case-container">
                        <div v-for="(judgeCase, index) in FormData.judgeCase" :key="index" class="test-case-item">
                            <a-space direction="horizontal">
                                <a-space direction="vertical">
                                    <a-textarea v-model="judgeCase.input" style="width: 40vw; height: 20vh" />
                                    <a-textarea v-model="judgeCase.output" style="width: 40vw; height: 20vh" />
                                    <a-divider />
                                </a-space>
                                <a-space direction="horizontal">
                                    <a-button status="success">
                                        <template #icon>
                                            <icon-editor />
                                        </template>
                                        <span>编辑用例{{ index + 1 }}</span>
                                    </a-button>
                                    <a-button status="danger" @click="() => FormData.judgeCase.splice(index, 1)">
                                        <template #icon>
                                            <icon-delete />
                                        </template>
                                    </a-button>
                                </a-space>
                            </a-space>
                        </div>
                        <a-button type="outline" class="add-case-btn" @click="addTestCase()">
                            <icon-plus /> 添加用例
                        </a-button>
                    </div>
                </a-card>
            </a-space>
        </a-layout-content>
        <a-button type="primary" style="margin: auto; width: 10vw; " @click="SubmitQuestion()" >
                <IconUpload /> 提交题目
        </a-button>
    </a-layout>
</template>


<script setup lang="ts">
import { ref } from 'vue';
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'

import { QuestionControllerService, Question, QuestionAddRequest } from '@/backend'

import { Message } from '@arco-design/web-vue';

const text = ref('# Hello Editor');

interface JudgeCase {
    input?: string;
    output?: string;
}

const FormData = ref({
    title: '',
    TimeLimit: 0,
    MemoryLimit: 0,
    tags: [] as string[],
    judgeCase: [] as JudgeCase[]
});

const addTestCase = () => {
    FormData.value.judgeCase.push({ input: '', output: '' });
}

const SubmitQuestion = async () => {
    console.log(FormData.value);
    let question = {
        answer: "",
        content: text.value,
        judgeCase: FormData.value.judgeCase,
        tags: FormData.value.tags,
        title: FormData.value.title,
        judgeConfig: {
            timeLimit: FormData.value.TimeLimit,
            memoryLimit: FormData.value.MemoryLimit
        }
    } as QuestionAddRequest;

    const res = await QuestionControllerService.addQuestionUsingPost(question);

    if (res.code === 0) {
        Message.success('提交成功');
    } else {
        Message.error('提交失败! ' + res.messages);
    }
}

</script>

<style scoped>
/* 统一间距系统 */
.layout-container {
    --spacing-lg: 24px;
    --spacing-md: 16px;
    --spacing-sm: 8px;
}

/* 头部优化 */
.header {
    background: #ffffffc7;
    padding: var(--spacing-md) 0;
}

.title-card {
    width: 80%;
    margin: 0 auto;
    background: transparent;
    box-shadow: none;
}

.title-input {
    width: 100%;
}

/* 内容区优化 */
.content {
    padding: var(--spacing-lg) 10%;
}

.section-card {
    margin-bottom: var(--spacing-md);
    border-radius: 8px;
}


.card-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    color: #1d2129;
}

/* 限制条件表单 */
.limit-form {
    width: 100%;
}

.limit-input {
    width: 200px;
}

/* 测试用例样式 */
.test-case-container {
    display: flex;
    flex-direction: column;
    gap: var(--spacing-md);
}

.test-case-item {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: var(--spacing-md);
}

.add-case-btn {
    align-self: flex-start;
    margin-top: var(--spacing-sm);
}

/* 统一输入组件样式 */
:deep(.arco-input),
:deep(.arco-input-number),
:deep(.arco-input-tag) {
    border-radius: 4px;
}

/* 响应式处理 */
@media (max-width: 768px) {
    .title-card {
        width: 95%;
    }

    .content {
        padding: var(--spacing-md);
    }

    .limit-input {
        width: 100%;
    }

    .test-case-item {
        grid-template-columns: 1fr;
    }
}
</style>
