<template>
    <div>
        <Codemirror :value="props.code" :options="cmOptions" aria-readonly="true" style="height: 72vh; width: 64vw;" />
    </div>
</template>


<script lang="ts" setup>
// code editor
import Codemirror from "codemirror-editor-vue3";
import "codemirror/theme/3024-day.css";
import 'codemirror/mode/clike/clike.js'
import 'codemirror/addon/edit/closebrackets.js'
import 'codemirror/addon/edit/matchbrackets.js'
import 'codemirror/keymap/sublime.js'
import { defineProps } from 'vue'

const props = defineProps({
    code: {
        type: String,
        required: true
    },
    currentLanguage: {
        type: String,
        required: true
    }
});

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



</script>