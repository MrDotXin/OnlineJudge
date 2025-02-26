<template>
    <div class="home_page">
        <a-space direction="horizontal" align="start">
            <a-col>
                <a-row class="problems_fliter_group">
                    <a-col :span="4">
                        <a-popover title="Title" trigger="click">
                            <button class="secondary_radius_button">筛选比赛 <icon-down/></button>
                            <template #content>
                                <p>暂未实现</p>
                            </template>
                        </a-popover>
                    </a-col>    
                    <a-col :span="4">  
                        <a-popover title="Title" trigger="click">
                            <button class="secondary_radius_button">标签 <icon-down/></button>
                            <template #content>
                                <p>暂未实现</p>
                            </template>
                        </a-popover>
                    </a-col>
                    <a-col :span="10">
                        <a-input :style="{width:'320px'}" placeholder="搜索题目, 题号, 标签" allow-clear>
                            <template #prefix>
                                <icon-search />
                            </template>
                        </a-input>
                    </a-col>
                </a-row>
                <div v-if="isLoadingData">
                    <a-skeleton :animation="true" style="width: 70vw; ">
                        <a-space direction="vertical" :style="{width: '100%'}" size="large">
                            <a-skeleton-line :rows="1" :line-height="40" :widths="['70vw']" />
                            <a-skeleton-line :rows="1" :line-height="40" :widths="['70vw']" />
                            <a-skeleton-line :rows="1" :line-height="40" :widths="['70vw']" />
                            <a-skeleton-line :rows="1" :line-height="40" :widths="['70vw']" />
                            <a-skeleton-line :rows="1" :line-height="40" :widths="['70vw']" />
                            <a-skeleton-line :rows="1" :line-height="40" :widths="['70vw']" />
                        </a-space>
                    </a-skeleton>
                </div> 
                <div v-else>
                    <a-table class="problem_table"
                        :columns="columns"
                        :data="dataSource"
                        :pagination="{
                            showTotal: true,
                            pageSize: page.pageSize,
                            current: page.current,  
                            total: pageTotal
                        }"
                        @page-change="onPageChange"

                        row-key="title"

                        stripe

                        :bordered="false"

                        style="width: 70vw;"

                        size="medium"
                    >
                        <template #th>
                            <th 
                                class="problem_th" 
                            />
                        </template>
                        <template #title="{ record }">
                            <span 
                                class="no_select problem_link"
                                style="display:" 
                                
                                @click="onToProblem(record.id)"
                            > 
                                {{ record.title }}
                            </span>
                        </template>

                        <template #acceptanceRate="{ record }">
                            <span style="display: block; text-align: left">
                                {{ record.submitNum === 0 ? 0 : record.acceptNum / record.submitNum }} %
                            </span>
                        </template>

                        <template #tags = "{ record }">  
                            <a-space wrap>
                                <a-tag v-for="(tag, index) of record.tags" :key="index" :color="getTagColor(tag)" bordered>
                                    {{ tag }}
                                </a-tag>
                            </a-space>
                        </template>
                    </a-table>
                </div>
            </a-col>
            <a-col style="margin-left: 7vw; margin-top: 2vh;">
                <a-space direction="vertical" >
                    <a-row>
                        <a-card style="width: 20vw; height: 40vh; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);" >
                            <template #title>
                                <span style="color: red">
                                    公告
                                </span>
                            </template>

                            <p>
                                纯绿色!!
                            </p>
                        </a-card>
                    </a-row>
                    <a-row>
                        <a-card style="width: 20vw; height: 40vh; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);" title="排名" >
                            暂无
                        </a-card>
                    </a-row>
                </a-space>
            </a-col>
        </a-space>
    </div>
</template>

<script setup lang="ts">
import { TableColumnData } from '@arco-design/web-vue/es/table';
import { ref, Ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { QuestionControllerService } from '@/backend'
import type { QuestionVO } from '@/backend'

const page = ref({
    current: 1,
    pageSize: 20
});

const pageTotal = ref(0);
const isLoadingData = ref(false);

const router = useRouter();

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
    width: 120,
},
];

// 表格数据
const dataSource: Ref<QuestionVO[]> = ref([]);

const loadData = async () => {
    isLoadingData.value = true;
    const res = await QuestionControllerService.listQuestionVoByPageUsingPost({
        pageSize: page.value.pageSize,
        current: page.value.current
    });
    
    dataSource.value = res.data.records;
    pageTotal.value = res.data.total;
    isLoadingData.value = false;
};

// 难度标签颜色映射
const getTagColor = (tag: string) => {
    if (tag === 'Easy') {
        return 'green';
    }

    return 'blue';
};


async function onPageChange(current : number) {
    page.value.current = current;
    await loadData();
}

const onToProblem = (id : string) => {
    console.log(id);
    router.push(`/problems/${id}`);
}
</script>

<style scoped>

.no_select {
    user-select: none;
}

.problem_link {
    color: rgb(79, 72, 72);
    &:hover {
        color: rgb(24, 94, 224);
        cursor: pointer;
    }

    &:active {
        color: rgb(24, 94, 224);
    }

}

.problem_th {
    background-color: transparent;
    color: rgba(101, 95, 95, 0.866);

    &:hover {
        background-color: transparent;
        color: black
    }
}

.secondary_radius_button {
    border: none;
    height: 5vh;
    width: 9vw;
    border-radius: 5px;
    margin-bottom: 2vw;
    margin-left: 10px;
    background-color: rgba(0, 0, 0, 0.08);
    color: rgba(0, 0, 0, 0.67);

    &:hover {
        color: rgb(0, 0, 0);
        background-color: rgba(0, 0, 0, 0.17);
        cursor: pointer;
    }
}

.problems_fliter_group {
    margin-bottom: 2vh;
    margin-top: 3vh;
}

</style>