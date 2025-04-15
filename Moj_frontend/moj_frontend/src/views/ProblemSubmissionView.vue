<template>
    <div class="submission-list-container">
        <a-card :bordered="false" class="submission-card">
            <template #title>
                <div class="card-title">
                    <span>提交记录</span>
                </div>
            </template>
            <a-space direction="vertical">
                <span>筛选</span>          
                <a-select :style="{width:'30vw'}" placeholder="语言类型" v-model="searchParams.language" allow-clear>
                    <a-option>{{ QuestionSubmitLanguageEnum.JAVA18 }}</a-option>
                    <a-option>{{ QuestionSubmitLanguageEnum.CPLUSPLUS }}</a-option>
                </a-select>
                
                <a-select :style="{width:'30vw'}" placeholder="判题状态" v-model="searchParams.status" allow-clear>
                    <a-option><span style="color: green">{{ JudgeInfoEnum.ACCEPTED }}</span></a-option>
                    <a-option><span style="color: red">{{ JudgeInfoEnum.COMPILER_ERROR }}</span></a-option>
                    <a-option><span style="color: red">{{ JudgeInfoEnum.WRONG_ANSWER }}</span></a-option>
                    <a-option><span style="color: red">{{ JudgeInfoEnum.TIME_LIMIT_EXCEEDED }}</span></a-option>
                    <a-option><span style="color: red">{{ JudgeInfoEnum.MEMORY_LIMIT_EXCEEDED }}</span></a-option>
                </a-select>

                <a-button type="primary" @click="onFilter" >筛选</a-button>
            </a-space>

            <a-list 
                :bordered="false" 
                :data="submissionData" 
                @page-change="handlePageChange"
                @reach-bottom="fetchData"
                :scrollbar="true"
                :max-height="'70vh'"
            >
                <template #scroll-loading>
                    <a-spin v-if="submissionData.length < page.total"></a-spin>
                </template>
                <template #item="{ item, index }">
                    <a-list-item class="submission-item">
                        <a-card :class="getStatusClass(item.status)" hoverable>
                            <div class="submission-content">
                                <!-- 提交基本信息 -->
                                <div class="submission-meta">
                                    <div class="meta-item">
                                        <icon-clock-circle />
                                        <span>{{ formatTime(item.createTime) }}</span>
                                    </div>
                                    <div class="meta-item">
                                        <icon-code />
                                        <span>{{ getLanguageName(item.language) }}</span>
                                    </div>
                                    <div :class="['status-badge', getStatusColor(item.status)]">
                                        {{ getStatusText(item.status) }}
                                    </div>
                                </div>

                                <!-- 判题结果 -->
                                <div class="judge-result" v-if="item.judgeInfo">
                                    <div class="result-item">
                                        <span v-if="item.status === 'Accepted'">                                            
                                            <span style="color: green"><icon-check-circle-fill />Accepted</span>
                                        </span>
                                        <span v-else-if="item.status === 'Waitting'">
                                            <span style="color: blue"><icon-check-circle-fill /> {{ item.status }}</span>
                                        </span>
                                        <span v-else>
                                            <span style="color: red"><icon-close-circle-fill /> {{ item.status }}</span>
                                        </span>
                                    </div>

                                    <div class="result-stats">
                                        <div class="stat-item">
                                            <icon-clock-circle />
                                            <span>时间: {{ item.judgeInfo.time || 0 }}ms</span>
                                        </div>
                                        <div class="stat-item">
                                            <icon-interaction />
                                            <span>内存: {{ item.judgeInfo.memory || 0 }}KB</span>
                                        </div>
                                    </div>
                                </div>

                                <!-- 代码预览 -->
                                <div class="code-preview">
                                    <a-collapse :bordered="false">
                                        <a-collapse-item header="查看代码" :key="index">
                                            <div style="height: 10vh; overflow: auto">
                                                <CodeDisplayer :code="item.code" :current-language="item.language" />
                                            </div>  
                                        </a-collapse-item>
                                    </a-collapse>
                                </div>

                                <!-- 输出预览 -->
                                <div class="code-preview">
                                    <a-collapse :bordered="false">
                                        <a-collapse-item header="查看详情" :key="index">
                                            <div style="height: 10vh; overflow: auto">
                                                <span style="color: red">
                                                    {{ item.judgeInfo.message }}
                                                </span>
                                            </div>  
                                        </a-collapse-item>
                                    </a-collapse>
                                </div>
                            </div>
                        </a-card>
                    </a-list-item>
                </template>
            </a-list>
        </a-card>
    </div>
</template>

<script setup lang="ts">
import { defineProps, defineExpose, ref } from 'vue'
import { QuestionSubmitControllerService, QuestionSubmitVO } from '../backend/question'
import JudgeInfoEnum from '@/enums/JudgeInfoEnum'
import QuestionSubmitLanguageEnum from '@/enums/QuestionSubmitLanguageEnum'
import CodeDisplayer from '@/components/CodeDisplayer.vue'

import dayjs from 'dayjs'
import {
    IconCode,
    IconClockCircle,
    IconCheckCircleFill,
    IconCloseCircleFill,
    IconInteraction
} from '@arco-design/web-vue/es/icon'

const page = ref({
    pageSize: 10,
    current: 0,
    total: 0
})

const searchParams = ref({
    language: "",
    status: ""
});

const props = defineProps({
    questionId: {
        type: String,
        required: true
    },
    userId: {
        type: String,
        required: true
    }
})

const submissionData = ref<QuestionSubmitVO[]>([])
const reloadData = async () => {
    submissionData.value = []
    page.value.current = 0

    await fetchData();
}

defineExpose({
    reloadData: reloadData
})

const onFilter = async () => {
    await reloadData();
}

const fetchData = async () => {
    if (page.value.current == 0 || submissionData.value.length < page.value.total) {
        page.value.current += 1;
        await loadData();
    }
}   

const loadData = async () => {
    if (props.userId !== 'undefined' && props.questionId !== 'undefined' ) {
        const response = await QuestionSubmitControllerService.listQuestionSubmitByPageUsingPost({
            pageSize: String(page.value.pageSize),
            current: String(page.value.current),
            userId: String(props.userId),
            questionId: String(props.questionId),
            sortField: 'createTime',
            sortOrder: 'descend',
            ...searchParams.value
        })

        if (response.data) {
            submissionData.value = submissionData.value.concat(response.data.records || []);
            page.value.total = response.data.total || 0
        }
    } else {
        console.log("未登录, 无法获取提交列表");
    }
}

const handlePageChange = (newPage: number) => {
    page.value.current = newPage
    loadData()
}

const formatTime = (time?: string) => {
    return time ? dayjs(time).format('YYYY-MM-DD hh:mm:ss'  ) : '--'
}

const getLanguageName = (lang?: string) => {
    const languages: Record<string, string> = {
        'java': 'Java',
        'cpp': 'C++',
        'python': 'Python',
        'javascript': 'JavaScript',
        'go': 'Go'
    }
    return lang ? languages[lang] || lang : '未知语言'
}

const getStatusText = (status?: number) => {
    const statusMap: Record<number, string> = {
        0: '等待中',
        1: '判题中',
        2: '已完成',
        3: '失败'
    }
    return status !== undefined ? statusMap[status] : '未知状态'
}

const getStatusColor = (status?: number) => {
    const colors: Record<number, string> = {
        0: 'status-waiting',
        1: 'status-judging',
        2: 'status-success',
        3: 'status-error'
    }
    return status !== undefined ? colors[status] : 'status-unknown'
}

const getStatusClass = (status?: number) => {
    return `submission-status-${status || 0}`
}

</script>

<style scoped>
.submission-list-container {
    margin: 16px 0;
    height: 70vh;
    overflow: auto;
}

.card-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 500;
}

.submission-item {
    margin-bottom: 16px;
}

.submission-content {
    padding: 12px;
}

.submission-meta {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 12px;
    font-size: 14px;
    color: var(--color-text-2);
}

.meta-item {
    display: flex;
    align-items: center;
    gap: 4px;
}

.status-badge {
    padding: 2px 8px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 500;
}

.status-waiting {
    background-color: var(--color-warning-light-1);
    color: var(--color-warning-dark-2);
}

.status-judging {
    background-color: var(--color-primary-light-1);
    color: var(--color-primary-dark-2);
}

.status-success {
    background-color: var(--color-success-light-1);
    color: var(--color-success-dark-2);
}

.status-error {
    background-color: var(--color-danger-light-1);
    color: var(--color-danger-dark-2);
}

.judge-result {
    margin: 12px 0;
    padding: 12px;
    background-color: var(--color-fill-1);
    border-radius: 4px;
}

.result-item {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 14px;
    font-weight: 500;
    margin-bottom: 8px;
}

.result-stats {
    display: flex;
    gap: 16px;
}

.stat-item {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 13px;
    color: var(--color-text-2);
}

.code-preview {
    margin-top: 12px;
}

.code-block {
    font-family: 'JetBrains Mono', monospace;
    background-color: var(--color-fill-2);
    border-radius: 4px;
    padding: 8px;
}

/* 状态卡片样式 */
.submission-status-0 {
    border-left: 4px solid var(--color-warning-light-2);
}

.submission-status-1 {
    border-left: 4px solid var(--color-primary-light-2);
}

.submission-status-2 {
    border-left: 4px solid var(--color-success-light-2);
}

.submission-status-3 {
    border-left: 4px solid var(--color-danger-light-2);
}
</style>