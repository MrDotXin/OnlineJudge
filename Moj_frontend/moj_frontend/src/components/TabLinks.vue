<template>
    <a-space direction="horizontal">
        <a-col 
            v-for="(tab, index) of links_group" :key="index" 
            style="margin-left: 6vw; margin-top: 4vh; font-size: large;"
        >
            <span 
                class="no_select clickable text_hint" 
                v-if="index !== currentSelector"
                @click="TabTriggered(tab.path, index)"
                >
                {{ tab.title }}
            </span>
            <span class="no_select text_selected" v-else>{{ tab.title }}</span>
        </a-col>
    </a-space>
</template>


<script lang="ts" setup>
import { ref, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router'

const router = useRouter();
const route  = useRoute();

const links_group = [
    {
        title: "题库",
        path: '/home/problemset'
    },
    {
        title: "比赛",
        path: '/home/contests'
    },
    {
        title: "FAQ",
        path: '/home/FAQ'
    }
];

watch(
    () => route.fullPath,
    (newPath) => {
        resetSelector(newPath);
    }
)

const currentSelector = ref(0);

const resetSelector = (path : string) => {
    for (let [index, tab] of links_group.entries()) {
        if (path.endsWith(tab.path)) {
            currentSelector.value = index;
            break;
        }
    }
}

const TabTriggered = (link : string, index : number) => {
    if (currentSelector.value !== index) {
        currentSelector.value = index;
        router.push(link);
    } 
}

</script>


<style>

.no_select {
    user-select: none;
}

.clickable {
    &:hover {
        cursor: pointer;
    }

    &:active {
        cursor: arrow;
    }
}

.text_hint {
    color: rgba(0, 0, 0, 0.5);
    font-size: 15px;
    &:hover {
        cursor: pointer;
        color: rgba(0, 0, 0, 0.87);
    }

    &:active {
        cursor: arrow;
        color: rgba(0, 0, 0, 0.5);
    }
}

.text_selected {
    color: black;
}

</style>