<template>
    <a-layout class="layout-container">
        <a-layout-header class="header">
            <a-card class="header-card" :bordered="false">
                <div class="header-content">
                    <h1 class="title">题目管理</h1>
                    <a-button type="primary" shape="round" class="add-button">
                        <template #icon><icon-plus /></template>
                        添加题目
                    </a-button>
                </div>
            </a-card>
        </a-layout-header>

        <a-layout-content class="content">
            <a-card class="content-card" :bordered="false">
                <a-table class="problem-table" :columns="columns" :data="dataSource" :pagination="page"
                    :loading="isLoadingData" @page-change="onPageChange" row-key="id" bordered>
                    <template #th>
                        <th class="problem_th" />
                    </template>
                    <template #title="{ record }">
                        <span class="no_select problem_link" style="display:" @click="onToProblem(record.id)">
                            {{ record.title }}
                        </span>
                    </template>

                    <template #acceptanceRate="{ record }">
                        <span style="display: block; text-align: left">
                            {{ record.submitNum === 0 ? 0 : record.acceptNum / record.submitNum }} %
                        </span>
                    </template>

                    <template #tags="{ record }">
                        <a-space wrap>
                            <a-tag v-for="(tag, index) of JSON.parse(record.tags)" :key="index"
                                :color="getTagColor(tag)">
                                {{ tag }}
                            </a-tag>
                        </a-space>
                    </template>

                    <template #operation = "{ record }">
                        <a-space :size="16">
                            <a-button type="secondary" class="editor-button">
                                <template #icon>
                                    <icon-edit />
                                </template>
                            </a-button>
                            <a-button type="secondary" class="delete-button" @click="onDeleteQuestion(record.id)">
                                <template #icon>
                                    <icon-delete />
                                </template>
                            </a-button>
                        </a-space>
                    </template>
                </a-table>
            </a-card>
        </a-layout-content>
    </a-layout>
</template>

<script setup lang="ts">
import { TableColumnData } from '@arco-design/web-vue/es/table';
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

import ColorTable from '@/config/ProblemTagsColorTable'
import { QuestionControllerService, Question } from '@/backend'

import { Message } from '@arco-design/web-vue'

const router = useRouter();

const page = ref({
    current: 1,
    pageSize: 20,
    total: 0
});

const pageTotal = ref(0);
const isLoadingData = ref(false);

const dataSource = ref<Question[]>([]);

const loadData = async () => {
    isLoadingData.value = true;
    const res = await QuestionControllerService.listQuestionByPageUsingPost({
        pageSize: page.value.pageSize,
        current: page.value.current
    });
    console.log(res);
    dataSource.value = res.data.records;
    pageTotal.value = res.data.total;
    page.value.total = res.data.total;
    isLoadingData.value = false;
};

onMounted(async () => {
    await loadData();
});

// 表格列配置
const columns: TableColumnData[] = [
    {
        title: '题目',
        dataIndex: 'title',
        slotName: 'title',
        width: 100,
        sortable: {
            sortDirections: ['ascend', 'descend']
        }
    },
    {
        title: '通过率',
        dataIndex: 'acceptanceRate',
        slotName: 'acceptanceRate',
        width: 40,
        sortable: {
            sortDirections: ['ascend', 'descend']
        }
    },
    {
        title: '点赞数',
        dataIndex: 'favourNum',
        width: 40,
        sortable: {
            sortDirections: ['ascend', 'descend']
        }
    },
    {
        title: '标签',
        dataIndex: 'tags',
        slotName: 'tags',
        width: 80,
    },
    {
        title: '操作',
        slotName: 'operation',
        width: 60
    }
];

async function onPageChange(current : number) {
    page.value.current = current;
    await loadData();
}

const onToProblem = (id : string) => {
    console.log(id);
    router.push(`/problems/${id}`);
}

const getTagColor = (item : string) => {
    console.log(item);
    return ColorTable[item as keyof typeof ColorTable];
}

const onDeleteQuestion = async (id : number) => {
    const res = await QuestionControllerService.deleteQuestionUsingPost({id : id});
    if (res.code === 0) {
        await loadData();

        Message.success('删除成功');
    } else {    
        Message.error('无法删除! ' + res.message);
    }

}
</script>


<style scoped>
.layout-container {
    background: #f5f7f9;
    min-height: 100vh;
}

.header {
    background: linear-gradient(135deg, #6b73ff 0%, #000dff 100%);
    height: auto;
    padding: 20px 0;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.header-card {
    background: transparent;
    border-radius: 0;
}

.header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 24px;
}

.title {
    color: white;
    margin: 0;
    font-size: 1.8em;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.add-button {
    background: #fff;
    color: #2d5aff;
    border-color: #fff;
    font-weight: 500;
    transition: all 0.3s;

    &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
}

.content {
    padding: 24px;
    margin-top: -20px;
}

.content-card {
    border-radius: 12px;
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    background: white;
}

.problem-table {
    --arco-table-border-radius: 8px;

    :deep(.arco-table-th) {
        background: #f8f9fa;
        font-weight: 500;
        color: #4a5568;
    }

    :deep(.arco-table-tr) {
        transition: background 0.2s;

        &:nth-child(even) {
            background: #f8fafc;
        }

        &:hover {
            background: #f1f5f9;
        }
    }
}

.problem_link {
    font-weight: 500;
    color: #3c4b64;

    &:hover {
        cursor: pointer;
        color: #2d5aff;
        text-decoration: underline;
    }
}

.editor-button {
    border-radius: 6px;
    transition: all 0.2s;

    &:hover {
        transform: scale(1.05);
        color: blue;
        background-color: rgba(0, 68, 255, 0.17);
    }
}

.delete-button {
    border-radius: 6px;
    transition: all 0.2s;

    &:hover {
        transform: scale(1.05);
        color: red;
        background-color: rgba(255, 0, 0, 0.17);
    }
}

/* 标签颜色优化 */
:deep(.arco-tag) {
    border-radius: 4px;
    font-weight: 500;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}
</style>