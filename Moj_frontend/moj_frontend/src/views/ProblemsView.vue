<template>
    <div class="all">
        <a-space direction="horizontal" align="start">
            <div class="card">
                <a-tabs>
                    <a-tab-pane key="1">
                        <template #title>
                            <icon-file />题目描述
                        </template>

                        <ProblemDescView :quesitons="questionData" />
                    </a-tab-pane>
                    <a-tab-pane key="2">
                        <template #title>
                            <icon-experiment />题解
                        </template>
                        暂无题解....
                    </a-tab-pane>
                    <a-tab-pane key="3">
                        <template #title>
                            <icon-message />评论区
                        </template>
                        <ProblemCommentsView :question-id="String(route.params.id)" />
                    </a-tab-pane>
                    <a-tab-pane key="4">
                        <template #title>
                            <icon-history />提交记录
                        </template>
                        <ProblemSubmissionView ref="submissionView" :question-id="String(route.params.id)"
                            :user-id="String(userStore.loginUser?.id)" />
                    </a-tab-pane>
                </a-tabs>
            </div>

            <div class="card" style="width: 65vw; margin-left: 1.5vw">
                <a-resize-box class="code-editor-resize-box" :directions="['bottom']"
                    style="width: 64vw; height: 80vh; max-height: 80vh; margin: 0 auto;"
                    v-model:height="resize_box_height" @moving="onResizeBoxMoving">
                    <template #resize-trigger>
                        <div :class="[
                            `resizebox-demo`,
                            `resizebox-demo-horizontal`
                        ]">
                            <div class="resizebox-demo-line" />
                        </div>
                    </template>
                    <a-space direction="vertical">
                        <div style="margin-top:1vh; width: 64vw; display: flex; justify-content: space-between">
                            <a-space direction="horizontal">
                                <a-button type="text" size="small"
                                    style="margin-top: 0px; margin-bottom: 0px; margin-left: 2vw">
                                    <span style="color: rgba(0, 0, 0, 0.64);">{{ currentLang }}</span>
                                    <icon-down style="color: rgba(0, 0, 0, 0.64);" size="large" />
                                </a-button>
                                <span style="color: rgba(0, 0, 0, 0.50); display: flex">
                                    <div class="contest_status"></div>未在比赛状态...
                                </span>

                                <div style="margin-left: 10vw">
                                    <a-button type="secondary" :disabled="true" @click="onSubmittingTest">

                                        <span v-if="submitState !== 'testing'"><icon-play-arrow /> 运行</span>
                                        <span v-else><icon-loading /> 运行中...</span>
                                    </a-button>
                                    <a-divider direction="vertical" />
                                    <a-button type="primary" status="success" :disabled="!submitEnabled()"
                                        @click="onSubmittingCode">
                                        <span v-if="submitState !== 'submitting'"><icon-upload :size="14" /> 提交</span>
                                        <span v-else><icon-loading :size="14" /> 提交运行中...</span>
                                    </a-button>
                                </div>
                            </a-space>
                            <a-space class="code-editor-right-top-button-group" direction="horizontal">
                                <a-tooltip content="你要不猜猜按下去会不会有提示呢~" position="right">
                                    <button><icon-bulb :size="18" /></button>
                                </a-tooltip>
                                <a-tooltip content="重置回代码模板"><button><icon-undo :size="18" /></button></a-tooltip>
                                <a-tooltip content="全屏"><button><icon-expand :size="18" /></button></a-tooltip>
                            </a-space>
                        </div>
                        <a-divider :margin="0"></a-divider>
                        <div v-if="!isLoggin()">
                            <a-alert style="width: 64vw;">你尚未登陆, 无法提交...</a-alert>
                        </div>
                        <Codemirror v-model:value="code" :options="cmOptions" style="height: 72vh; width: 64vw;" />
                    </a-space>
                </a-resize-box>
                <div style="
                        display: flex; 
                        justify-content: space-between; 
                    ">
                    <a-tabs type="card-gutter" :active-key="currentTerminal" @change="onTerminalChange">
                        <a-tab-pane key="1">
                            <template #title>
                                <icon-check-square size="large" style="color: green;" /> 测试用例
                            </template>
                            <div :style="{
                                width: '64vw',
                                height: current_status ? 0 : 'calc(' + max_height + 'px - ' + resize_box_height + 'px)',
                                overflow: 'hidden'
                            }">
                                <a-space direction="horizontal" align="start">
                                    <a-tabs type="line" :editable="true" show-add-button auto-switch animation
                                        @add="onAddingCases" v-model:active-key="active_key">
                                        <a-tab-pane v-for="(item, index) of testCases" :key="index" :closable="false">
                                            <template #title>
                                                <a-space direction="horizontal">
                                                    <span style="color: rgba(0,0,0,1);"
                                                        @mouseenter="onCaseTabHovering(index)"
                                                        @mouseleave="onCaseTabLeave()">
                                                        Case {{ index + 1 }}
                                                        <span v-if="currentCaseTabHovered === index">
                                                            <icon-close-circle-fill class="case-delete-hint"
                                                                @click="onDeleteCases(index)" :size="17" />
                                                        </span>
                                                    </span>
                                                </a-space>
                                            </template>
                                            <a-space direction="vertical" style="margin-left: 1vw;">
                                                <a-space direction="horizontal">
                                                    <a-tag color="green">输入样例</a-tag>
                                                    <a-textarea v-model:model-value="item.input" style="width: 55vw"
                                                        auto-size />
                                                </a-space>
                                                <a-space direction="horizontal" style="margin-top: 2vh;">
                                                    <a-tag color="gold">输出样例</a-tag>
                                                    <a-textarea v-model:model-value="item.output" style="width: 55vw"
                                                        auto-size />
                                                </a-space>
                                                <a-button type="outline" status="success"
                                                    @click="onLoadingTestCase(item)">
                                                    填入测试用例
                                                </a-button>
                                            </a-space>
                                        </a-tab-pane>
                                    </a-tabs>
                                </a-space>
                            </div>
                        </a-tab-pane>
                        <a-tab-pane key="2">
                            <template #title>
                                <icon-code-block size="large" style="color: green;" /> 测试结果
                            </template>
                            <div :style="{
                                width: '80vw',
                                height: current_status ? 0 : 'calc(' + max_height + 'px - ' + resize_box_height + 'px)',
                                overflow: 'hidden'
                            }">
                                <div v-if="['normal', 'success', 'fail'].includes(submitState)">
                                    <a-space direction="vertical" style="margin-left: 1vw;">
                                        <a-space direction="horizontal">
                                            <a-tag color="green">样例输入</a-tag>
                                            <a-textarea v-model:model-value="currentCase.input" style="width: 52vw"
                                                auto-size />
                                        </a-space>
                                        <a-space direction="horizontal" style="margin-top: 2vh;">
                                            <a-tag color="gold">样例输出</a-tag>
                                            <a-textarea v-model:model-value="currentCase.output" style="width: 52vw"
                                                auto-size />
                                        </a-space>
                                        <a-divider><icon-check-circle :size="18" /></a-divider>
                                        <div v-if="submitState === 'normal'">
                                            <a-space direction="horizontal" style="margin-top: 2vh;">
                                                <a-tag color="blue">实际输出</a-tag>
                                                <a-textarea v-model:model-value="current_answer" style="width: 52vw"
                                                    auto-size />
                                            </a-space>
                                        </div>
                                        <div v-else>
                                            <div v-if="submitState === 'success'">
                                                <h2 style="color: green">通过! Accepted</h2>
                                            </div>
                                            <div v-else>
                                                <h2 style="color: red">运行出错! /> {{ last_judge_message }}</h2>
                                                <span style="color: red">{{ last_judge_outcome }}</span>
                                            </div>
                                        </div>
                                    </a-space>
                                </div>
                                <div v-else>
                                    <a-skeleton :animation="true">
                                        <a-space direction="vertical" style="width: 64vw" size="large" align="start">
                                            <a-skeleton-line :rows="1" :widths="['60vw']" :line-height="25" />
                                            <a-skeleton-line :rows="1" :widths="['60vw']" :line-height="25" />
                                            <a-skeleton-line :rows="1" :widths="['60vw']" :line-height="25" />
                                            <a-skeleton-shape />
                                        </a-space>
                                    </a-skeleton>
                                    <a-space direction="horizontal" style="position: relative; top: -10vh; left: 24vw">
                                        <span style="color: green"><icon-loading :size="18" />
                                            {{ stateMessage }}ing...</span>
                                        <SubmitLoader />
                                    </a-space>
                                </div>
                            </div>
                        </a-tab-pane>
                    </a-tabs>

                    <button class="code-editor-reset-box-btn" @click="onStretch">
                        <icon-double-up class="resize-icon" :size="27" v-if="current_status" />
                        <icon-double-down class="resize-icon" :size="27" v-else />
                    </button>

                </div>
            </div>
        </a-space>
    </div>

</template>

<script lang="ts" setup>
import { useRoute } from 'vue-router'
import { onMounted, ref } from 'vue'
import type { Ref } from 'vue'
import { UserStore } from '../store/user'// backend 


// 后端
import { QuestionSubmitControllerService } from '@/backend/question'
import { QuestionControllerService } from '@/backend/question'
import type { QuestionVO, JudgeCase } from '@/backend/question'


// code editor
import Codemirror from "codemirror-editor-vue3";
import "codemirror/theme/3024-day.css";
import 'codemirror/mode/clike/clike.js'
import 'codemirror/addon/edit/closebrackets.js'
import 'codemirror/addon/edit/matchbrackets.js'
import 'codemirror/keymap/sublime.js'

import SubmitLoader from '@/components/Loader/TallFish.vue'


// 先载入用户数据
const userStore = UserStore();

// 其它组件
// 题目评论
import ProblemCommentsView from '@/views/ProblemCommentsView.vue'

import ProblemDescView from '@/views/ProblemDescView.vue'

import ProblemSubmissionView from '@/views/ProblemSubmissionView.vue'

import JudgeInfoEnum from '@/enums/JudgeInfoEnum'

const submissionView = ref<{ reloadData: () => void } | null>(null);

const code = ref("for(int i = 0; i < n; i ++) {\n\n    i++; \n}");

const currentLang = ref("Java 1.8");
const cmOptions = {
    mode: "text/x-c++src", // Language mode

    theme: "3024-day",        // Theme
    line: true, // 显示行数
    lineNumbers: true, // 显示行号
    lineWrapping: true, // 软换行
    tabSize: 4, // tab宽度
    smartIndent: true, // 智能缩进
    autofocus: true, // 自动聚焦
    autoCloseBrackets: true, // 自动补全括号
    matchbrackets: true, //自动匹配括号
    keymap: "sublime" // 按键映射
}

const route = useRoute();
const questionData: Ref<QuestionVO> = ref({});

/**
 * 输出输入用例相关
 */
const testCases = ref<JudgeCase[]>([
    {
        input: "1",
        output: "2"
    },
    {
        input: "3",
        output: "4"
    },
]);

const currentCase = ref<JudgeCase>(
    {
        input: "1",
        output: "2"
    }
)

const currentTerminal = ref("1");
const onTerminalChange = (newKey: string | number) => {
    currentTerminal.value = newKey as string;
}

const onLoadingTestCase = (item: JudgeCase) => {
    currentCase.value.input = item.input;
    currentCase.value.output = item.output;
    currentTerminal.value = "2";
}

// 当前输出用例
const current_answer = ref("");

const active_key = ref(0);

// 判题状态
const submitState = ref<"normal" | "submitting" | "testing" | "success" | "fail" | "watting">("normal");

// 判题信息
const stateMessage = ref<string>("");

// 消息。
const last_judge_message = ref("");

// 详细信息。
const last_judge_outcome = ref("");

const onDeleteCases = (index: number) => {
    if (testCases.value.length > 1) {
        testCases.value.splice(index, 1);
        if (active_key.value === index) {
            active_key.value -= 1;
        }
    }
};

const onAddingCases = () => {
    testCases.value.push(testCases.value[testCases.value.length - 1]);
};

// 悬浮在测试用例上的删除按钮逻辑
const currentCaseTabHovered = ref(-1);

const onCaseTabHovering = (index: number) => {
    currentCaseTabHovered.value = index;
}

const onCaseTabLeave = () => {
    currentCaseTabHovered.value = -1;
}

const onSubmittingTest = () => {
    submitState.value = "testing";
}

// 提交判题

let waittingJudgeInterval = 0;

const submitEnabled = () => {
    return submitState.value !== 'submitting' && userStore.isLoggedIn;
}

const isLoggin = () => {
    return !!userStore.isLoggedIn;
}

const onSubmittingCode = async () => {
    submitState.value = "submitting";
    stateMessage.value = "提交中";

    currentTerminal.value = "2";
    stretchTo(0);

    let response = await QuestionSubmitControllerService.doQuestionSubmitUsingPost({
        code: code.value,
        language: currentLang.value,
        questionId: route.params.id as string
    });

    submissionView.value?.reloadData();

    let submissionId = response.data;

    waittingJudgeInterval = setInterval(async () => {

        let response = await QuestionSubmitControllerService.getQuestionSubmitByIdUsingGet1(
            submissionId
        );

        let status = response.data?.status;
        if (status !== JudgeInfoEnum.WAITING) {
            if (status === JudgeInfoEnum.RUNNING) {
                stateMessage.value = '运行中';
            } else {
                submitState.value = ((status === JudgeInfoEnum.ACCEPTED) ? 'success' : 'fail');

                last_judge_message.value = status || '';
                last_judge_outcome.value = response.data?.judgeInfo?.message || '';
                clearInterval(waittingJudgeInterval);
            }
        }
    }, 500);

}

// 伸缩框
let max_height = 0;
const resize_box_height = ref(Number.MAX_VALUE);
const current_status = ref(1);
const onResizeBoxMoving = () => {
    if (max_height === 0) {
        max_height = resize_box_height.value;
    }

    if ((max_height - resize_box_height.value) <= 3) {
        current_status.value = 1;
    } else {
        current_status.value = 0;
    }
}

const onStretch = () => {
    if (current_status.value === 1) {
        resize_box_height.value = max_height / 2;
        current_status.value = 0;
    } else {
        resize_box_height.value = max_height;
        current_status.value = 1;
    }
}

const stretchTo = (toward: number) => {
    current_status.value = toward;
    if (toward === 0) {
        resize_box_height.value = max_height / 2;
    } else {
        resize_box_height.value = max_height;
    }
}

const loadData = async () => {
    const id = route.params.id;
    const response = await QuestionControllerService.getQuestionVoByIdUsingGet(id as string);
    console.log(response);
    if (response.code === 0) {
        questionData.value = response.data as QuestionVO;
    }
}


onMounted(async () => {

    let element = document.getElementsByClassName("code-editor-resize-box");
    let style = window.getComputedStyle(element[0]);
    let tmp = style.maxHeight;

    max_height = Number(style.maxHeight.slice(0, tmp.length - 2));

    loadData();
});
</script>


<style scoped>
:deep(#preview-only) {
    zoom: 0.7
}



.all {
    background-color: rgba(242, 240, 240, 0.57);
}

.editor-top {
    width: 65vw;
    height: 5vh;
    border-top-left-radius: 30px;
    border-top-right-radius: 30px;
    background-color: rgba(0, 0, 0, 0.03);
}

.card {
    margin-top: 1vh;
    width: 32vw;
    height: 89vh;
    border-radius: 30px;
    background: white;
    box-shadow: 15px 15px 30px #bebebe,
        -15px -15px 30px #ffffff;
}

.btn_coder {
    margin-left: 0.5vw;
    margin-top: 0.2vh;

    height: 4.2vh;
    border-radius: 6px;

    border: none;
    background-color: rgba(0, 0, 0, 0.02);

    &:hover {
        background-color: rgba(0, 0, 0, 0.10);
        cursor: pointer;
    }

    &:active {
        background-color: rgba(0, 0, 0, 0.02);
    }
}


.contest_status {
    margin-top: 3.5px;
    margin-right: 4px;
    width: 8px;
    height: 8px;
    background: #e85f04;
    border-radius: 50%;
}

.code-editor-right-top-button-group button {
    border: none;
    background-color: transparent;

    &:first-child {
        color: rgba(0, 0, 0, 0.27);
    }

    &:hover {
        cursor: pointer;

        &:first-child {
            color: black;
        }
    }

    &:active {
        &:first-child {
            color: rgba(0, 0, 0, 0.27);
        }
    }
}

.resizebox-demo {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    background-color: var(--color-bg-2);
}

.resizebox-demo::before,
.resizebox-demo::after {
    width: 6px;
    height: 6px;
    border: 1px solid rgb(var(--arcoblue-6));
    content: '';
}

.resizebox-demo-line {
    flex: 1;
    background-color: rgb(var(--arcoblue-6));
}

.resizebox-demo-vertical {
    flex-direction: column;
}

.resizebox-demo-vertical .resizebox-demo-line {
    width: 1px;
    height: 100%;
}

.resizebox-demo-horizontal .resizebox-demo-line {
    height: 1px;
}

.resize-icon {
    color: rgba(0, 0, 0, 0.64);

    &:hover {
        color: black;
    }
}

.code-editor-reset-box-btn {

    width: 3vw;
    height: 4.5vh;
    border-top-left-radius: 5px;
    border-top-right-radius: 5px;

    position: relative;
    right: 32vw;

    border: none;
    background-color: rgb(255, 255, 255);

    &:hover {
        border: 2px solid rgba(0, 0, 0, 0.21);
        cursor: pointer;
    }

    &:active {
        border: none;
    }
}

.case-delete-hint {
    color: rgba(0, 0, 0, 0.37);

    &:hover {
        color: rgba(0, 0, 0, 0.57)
    }

    &:active {
        color: rgba(0, 0, 0, 0.37)
    }
}
</style>
