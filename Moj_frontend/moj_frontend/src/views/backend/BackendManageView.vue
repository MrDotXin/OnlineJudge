<template>
    <a-layout-header class="header-nav">
        <div class="nav-container">
            <!-- 品牌标识 -->
            <div class="brand">
                <img src="@/assets/dog.png" class="logo" />
                <h1>MOJ后台管理</h1>
            </div>
            <div class="nav-actions">
                <a-input-search placeholder="搜索设备/成员" class="search-box" />

                <a-dropdown position="br">
                    <a-badge :count="3" dot>
                        <a-button shape="circle" type="text">
                            <icon-notification />
                        </a-button>
                    </a-badge>
                    <template #content>
                        <a-doption>新设备接入申请</a-doption>
                        <a-doption>系统更新通知</a-doption>
                    </template>
                </a-dropdown>

                <a-avatar :size="32" :style="{ backgroundColor: '#3370ff' }">
                    <icon-user />
                </a-avatar>
            </div>
        </div>
    </a-layout-header>

    <a-layout class="admin-container">
        <a-layout-sider breakpoint="lg" @collapse="handleSiderCollapse" :collapsible="true" :trigger="null">
            <div class="logo">管理项</div>
            <a-menu :default-selected-keys="['Info']" :style="{ border: 'none' }" auto-open
                @MenuItemClick="onClickMenuItem">
                <a-menu-item key="Info">
                    <template #icon><icon-home /></template>
                    首页
                </a-menu-item>
                <a-sub-menu key="2" title="功能管理">
                    <template #icon>
                        <icon-apps />
                    </template>
                    <a-sub-menu key="question_manage" title="题目管理">
                        <template #icon>
                            <icon-storage />
                        </template>
                        <a-menu-item key="Question">题库管理</a-menu-item>
                        <a-menu-item key="QuestionAdd">创建题目</a-menu-item>
                    </a-sub-menu>
                    <a-menu-item key="2-2">角色管理</a-menu-item>
                    <a-menu-item key="2-3">通讯录设置</a-menu-item>
                </a-sub-menu>
            </a-menu>
        </a-layout-sider>

        <a-layout-content>
            <component :is="tabViews[currentTabView as keyof typeof tabViews]" />
        </a-layout-content>
    </a-layout>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import InfoView from '@/views/backend/manage/InfoView.vue'
import QuestionManageView from '@/views/backend/manage/QuestionManageView.vue'
import QuestionAddingView from '@/views/backend/manage/QuestionAddingView.vue'

const isSiderCollapsed = ref(false);
const handleSiderCollapse = (collapsed: boolean) => {
    isSiderCollapsed.value = collapsed;
};

const tabViews = {
    'Info': InfoView,
    'Question': QuestionManageView,
    "QuestionAdd": QuestionAddingView
}

let currentTabView = ref('Info');

function onClickMenuItem(key: keyof typeof tabViews) {
    currentTabView.value = key;
}



</script>

<style scoped>
.header-nav {
    height: 64px;
    padding: 0 24px;
    background: var(--color-bg-2);
    border-bottom: 1px solid var(--color-border);
}

.nav-container {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 100%;
    gap: 24px;
}

.brand {
    display: flex;
    align-items: center;
    gap: 12px;

    h1 {
        margin: 0;
        font-size: 18px;
        color: var(--color-text-1);
    }

    .logo {
        width: 32px;
        height: 32px;
    }
}

.nav-actions {
    display: flex;
    align-items: center;
    gap: 16px;

    .search-box {
        width: 240px;
    }
}

.arco-menu-horizontal {
    :deep(.arco-menu-overflow-wrap) {
        padding: 0 12px;
    }

    :deep(.arco-menu-item) {
        padding: 0 16px;
    }
}

.admin-container {
    height: 100vh;
}

.logo {
    height: 64px;
    line-height: 64px;
    font-size: 18px;
    font-weight: 500;
    padding-left: 24px;
    color: var(--color-text-1);
}

.header {
    display: flex;
    align-items: center;
    padding: 0 24px;
    background: var(--color-bg-2);
    border-bottom: 1px solid var(--color-border);
}

.enterprise-info {
    display: flex;
    align-items: center;
    gap: 12px;
}

.content {
    padding: 24px;
    background: var(--color-fill-2);
}

.mt-16 {
    margin-top: 16px;
}

.entry-item {
    padding: 8px;
    text-align: center;
}
</style>