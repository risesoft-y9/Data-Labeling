<template>
    <div class="article">
        <div class="content-fontsize content-btn">
            <div>
                <el-button class="global-btn-second" @click="handlerParticiple">重新分词</el-button>
                <el-button
                    type="primary"
                    plain
                    @click="handlerShowResult"
                    :class="{ 'focus-button': showButton, 'primary-button': !showButton }"
                    >显示标注结果</el-button
                >
                <el-popover
                    placement="top"
                    title=""
                    :width="247"
                    trigger="hover"
                    content="新保存的关键词会在2-3分钟后生效"
                >
                    <template #reference>
                        <el-button
                            type="primary"
                            plain
                            @click="handleShowParticiple"
                            :class="{ 'focus-button': IKButton, 'primary-button': !IKButton }"
                            >ik分词验证</el-button
                        >
                    </template>
                </el-popover>
            </div>
            <div>
                【字体：<span @click="handlerClick('large')" :class="{ red: fontsize == 'large' }">大</span>
                <span @click="handlerClick('middle')" :class="{ red: fontsize == 'middle' }">中</span>
                <span @click="handlerClick('small')" :class="{ red: fontsize == 'small' }">小</span>】
            </div>
        </div>
        <el-row class="content-top">
            <el-col :span="24">
                <y9-Form
                    class="y9-content-form"
                    :class="{
                        'content-small': fontsize == 'small',
                        'content-large': fontsize == 'large',
                        'content-middle': fontsize == 'middle',
                    }"
                    :config="y9FormConfig"
                >
                    <template #y9FormValue="{ item }">
                        <div @mouseenter="(e) => showToolTip(e, item)">
                            <el-tooltip v-if="item.visible" :content="item.content1" placement="top" effect="light">
                                <span>{{ item.content1 }}</span>
                            </el-tooltip>
                            <span v-else>{{ item.content1 }}</span>
                        </div>
                    </template>
                </y9-Form>
            </el-col>

            <el-col :span="24">
                <div
                    v-html="content"
                    @contextmenu="handlerClickMenuOpen"
                    :class="{
                        'content-small': fontsize == 'small',
                        'content-large': fontsize == 'large',
                        'content-middle': fontsize == 'middle',
                    }"
                >
                </div>
            </el-col>
        </el-row>
    </div>
    <div id="menu">
        <ul>
            <li>添加新关键词</li>
        </ul>
    </div>
    <el-button style="display: none" v-loading.fullscreen.lock="loading"></el-button>
</template>

<script lang="ts" setup>
    import { ref, watch } from 'vue';
    import key from 'keymaster';
    import y9_storage from '@/utils/storage';
    import { $arrEqual, $deeploneObject } from '@/utils/object';
    import { getArticleAnalyse } from '@/api/home/index';
    import { ElNotification } from 'element-plus';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { topTitleData, currNo } from './data';

    const settingStore = useSettingStore();

    interface articleContentType {
        fieldName?: string;
        fieldContent?: string;
    }

    interface showButtonType {
        id: string;
        showButton: boolean;
        IKButton: boolean;
    }

    interface oldKeywordType {
        token: string;
        position: number;
        start_offset: number;
        end_offset: number;
        type: string;
    }

    const props = defineProps({
        // 文章信息内容
        contentInfo: {
            type: Object,
            default: () => {},
        },
        // 文章id
        currArticleId: {
            type: String,
            default: '',
        },
        // 自己选择的分词数组和接口返回的关键词
        selectwordList: {
            type: Array,
            default: () => [],
        },
        // // 关键词记录
        // recordList: {
        //     type: Array,
        //     default: () => [],
        // },
    });

    const emits = defineEmits(['getkey']);
    let labelWidthSize = {
        large: '110px',
        middle: '98px',
        small: '92px',
    };
    let labelWidthFontSize = {
        large: 18,
        middle: 16,
        small: 14,
    };

    //表单配置
    let y9FormConfig = ref({
        descriptionsFormConfig: {
            //描述表单配置
            column: settingStore.device === 'mobile' ? 1 : 2,
            labelAlign: 'center',
            labelWidth: '110px',
            contentWidth: '200px',
        },
        model: {},
        rules: {}, //表单验证规则
        itemList: [],
    });

    /***
     * 文本分析
     */

    // 文本分析的数组
    let oldKeywordsList = ref<Array<oldKeywordType>>([]);
    // loading
    let oldKeywordsLoading = ref(false);

    // 文本分析接口
    async function initArticleAnalyse(type?) {
        oldKeywordsLoading.value = true;
        let params = {
            id: props.currArticleId,
        };
        let result = await getArticleAnalyse(params);

        if (result.code == 0) {
            oldKeywordsList.value = JSON.parse(result.data).tokens;
        }

        if (IKButton.value) {
            handleShowParticiple(IKButton.value);
        }

        if (type == 'afresh') {
            if (showButton.value) {
                handlerShowResult(showButton.value);
            }
            // 提示信息
            ElNotification({
                title: result.code == 0 ? '成功' : '失败',
                message: result.code == 0 ? '重新分词成功' : result.msg,
                type: result.code == 0 ? 'success' : 'error',
                duration: 2000,
                offset: 80,
            });
        }

        oldKeywordsLoading.value = false;
    }

    // 文章信息
    let articleInfo = ref<Array<articleContentType>>([]);
    let content = ref('');
    // 接口的文章内容
    let articleContent = ref('');

    // 全局loading
    let loading = ref(false);

    // 对于文本分析 的数据进行下划线处
    watch(
        () => props.contentInfo,
        async (newVal, oldVal) => {
            if (newVal && Object.values(newVal).length) {
                // 筛选出文章内容
                await newVal.typefield?.map((item) => {
                    if (item.fieldName == '文章内容' || item.fieldName == '内容') {
                        articleContent.value = item.fieldContent;
                    }
                });
                // 筛选出除文章外的内容
                articleInfo.value = newVal?.typefield?.filter(
                    (item) => item.fieldName !== '文章内容' && item.fieldName !== '内容'
                );
                // 给表单赋值itemList
                let formItemList: any = [];
                await articleInfo.value?.map((item, index) => {
                    if (item.fieldName == '标题') {
                        formItemList.push({
                            type: 'slot',
                            label: item.fieldName,
                            key: index,
                            // 存放内容
                            content1: item.fieldContent,
                            // 控制tooltip是否出现
                            visible: false,
                            span: 2,
                            props: {
                                slotName: 'y9FormValue',
                            },
                        });
                        formItemList.push({
                            type: 'slot',
                            label: '序号',
                            key: index,
                            // 存放内容
                            content1: `${topTitleData.value.name}-${currNo.value}`,
                            // 控制tooltip是否出现
                            visible: false,
                            props: {
                                slotName: 'y9FormValue',
                            },
                        });
                        return;
                    }
                    formItemList.push({
                        type: 'slot',
                        label: item.fieldName,
                        key: index,
                        // 存放内容
                        content1: item.fieldContent,
                        // 控制tooltip是否出现
                        visible: false,
                        props: {
                            slotName: 'y9FormValue',
                        },
                    });
                });
                y9FormConfig.value.itemList = formItemList;
                // 暂时不需要 下划线 先直接赋值 再 需要判断是否需要进行标注显示结果的样式
                content.value = articleContent.value;
                // 每次进来时都判断 显示标注结果的数组中是否有该id 有就保持不变，没有就设置为false
                await handlerAllShowButton(newVal);
                initArticleAnalyse();
            }
        },
        {
            deep: true,
            immediate: true,
        }
    );

    watch(
        () => props.selectwordList,
        (newVal, oldVal) => {
            if (showButton.value && oldVal.length) {
                handlerShowResult(showButton.value);
            }
        },
        {
            deep: true,
        }
    );

    // 保存文章标题所有的标注结果的数组
    let allTitleShowButton = ref<Array<showButtonType>>([]);
    let showButton = ref(false);
    let IKButton = ref(false);
    // 显示标注结果,IK分词的逻辑处理
    async function handlerAllShowButton(articleInfo) {
        let buttonList = y9_storage.getObjectItem('titleShowButton');
        if (buttonList && buttonList?.length) {
            allTitleShowButton.value = buttonList;
        }
        if (allTitleShowButton.value.length) {
            let matchList = await allTitleShowButton.value?.filter((item) => item.id == articleInfo.id);
            if (!matchList?.length) {
                allTitleShowButton.value.push({ id: articleInfo.id, showButton: false, IKButton: false });
                showButton.value = false;
                IKButton.value = false;
            } else {
                showButton.value = matchList[0].showButton;
                IKButton.value = matchList[0].IKButton;
            }
        } else {
            allTitleShowButton.value.push({ id: articleInfo.id, showButton: false, IKButton: false });
        }
        // 模拟点击按钮
        handlerShowResult(showButton.value);
    }

    // 新关键词的列表
    let newKeyword = ref('');

    // 字体大小的变量
    let fontsize = ref('large');

    // 字体大小的切换 方法
    function handlerClick(type) {
        fontsize.value = type;
        y9FormConfig.value.descriptionsFormConfig.labelWidth = labelWidthSize[type];
    }

    // 鼠标悬浮 判断当前文字长度是否超出最大宽度，超出显示tooltip
    async function showToolTip(e, objInfo) {
        let toolTipShow = e.target.offsetWidth < objInfo.content1.length * labelWidthFontSize[fontsize.value];
        await y9FormConfig.value.itemList?.map((item: any) => {
            if (item.key == objInfo.key && toolTipShow) {
                item.visible = true;
            } else {
                item.visible = false;
            }
        });
    }

    // 右键自定义菜单的事件
    function handlerClickMenuOpen(ev) {
        let menu: any = document.getElementById('menu');
        let selectionValue = getSelectionText();
        if (selectionValue) {
            // 复制 选中文字
            newKeyword.value = selectionValue;
            var ev = ev || event;
            menu.style.display = 'block';
            menu.style.left = ev.clientX + 'px';
            //当滑动滚动条时也能准确获取菜单位置
            // 用clientY 减去menu菜单自身的高度
            menu.style.top = ev.clientY - 48 + 'px';
        }
        //阻止鼠标的默认事件
        ev.preventDefault(); //做一些兼容性的处理
        document.onclick = function () {
            menu.style.display = 'none';
        };
        menu.onclick = handlerClickLi;
    }

    // 点击自定义菜单选项
    function handlerClickLi(e) {
        if (e.target.innerText === '添加新关键词') {
            emits('getkey', newKeyword.value);
        }
    }

    // 获取到选中的文字内容
    function getSelectionText() {
        if (window.getSelection) {
            return window.getSelection()?.toString();
        } else if (document.selection && document.selection.createRange) {
            return document.selection.createRange().text;
        }
        return '';
    }

    // 点击重新分词按钮
    async function handlerParticiple() {
        loading.value = true;
        await initArticleAnalyse('afresh');
        setTimeout(() => {
            loading.value = false;
        }, 300);
    }

    // 点击IK分词验证
    async function handleShowParticiple(buttonStatus?) {
        // 改变数组中对应元素的值
        if (typeof buttonStatus !== 'boolean') {
            await allTitleShowButton.value?.map((item) => {
                if (item.id == props.contentInfo.id) {
                    item.IKButton = !item.IKButton;
                    IKButton.value = item.IKButton;
                    if (item.IKButton) {
                        showButton.value = false;
                        item.showButton = false;
                    }
                }
            });
            // 存储数据
            y9_storage.setObjectItem('titleShowButton', allTitleShowButton.value);
            if (IKButton.value) {
                await initArticleAnalyse();
            }
        }

        if (oldKeywordsList.value.length && IKButton.value) {
            // 定义一个下标位置 用来表示进度
            let indexOf = ref(0);
            content.value = '';
            await oldKeywordsList.value?.map((item, index) => {
                // 如果indexOf 等于元素的开始下标 证明与之前是联系起来的 且不是单字 直接进行下划线 样式设置
                if (item.start_offset == indexOf.value && item.end_offset - item.start_offset != 1) {
                    if ((item.position + 1) % 2 == 0) {
                        content.value += `<span style="text-decoration: underline wavy var(--el-color-primary);margin-right: 2px;">${item.token}</span>`;
                    } else {
                        content.value += `<span style="border-bottom: 3px solid var(--el-color-primary);margin-right: 2px;">${item.token}</span>`;
                    }
                    // 将下标位置更新到最新元素的结束下标
                    indexOf.value = item.end_offset;
                } else {
                    // 和上个元素没连接，先判断是否是单字  单字直接拼接

                    if (item.end_offset - item.start_offset == 1) {
                        content.value += articleContent.value.slice(indexOf.value, item.start_offset);
                        content.value += articleContent.value.slice(item.start_offset, item.end_offset);
                        // 将下标位置更新到最新元素的结束下标
                        indexOf.value = item.end_offset;
                    } else {
                        // 不是单字 先拼接未连接的内容，再将元素区域进行下划线样式设置
                        content.value += articleContent.value.slice(indexOf.value, item.start_offset);
                        if ((item.position + 1) % 2 == 0) {
                            content.value += `<span style="text-decoration: underline wavy var(--el-color-primary);margin-right: 2px;">${item.token}</span>`;
                        } else {
                            content.value += `<span style="border-bottom: 3px solid var(--el-color-primary);margin-right: 2px;">${item.token}</span>`;
                        }
                        // 将下标位置更新到最新元素的结束下标
                        indexOf.value = item.end_offset;
                    }
                }

                if (index == oldKeywordsList.value.length - 1) {
                    // 遍历完数组后 如果下标位置 不是字符串最后一个位置  就将之后的内容拼接
                    if (indexOf.value !== articleContent.value.length) {
                        content.value += articleContent.value.slice(indexOf.value);
                    }
                }
            });
        } else {
            // 不显示分词结果
            content.value = articleContent.value;
        }
    }

    // 显示标注结果
    async function handlerShowResult(buttonStatus?) {
        // 改变数组中对应元素的值
        if (typeof buttonStatus !== 'boolean') {
            await allTitleShowButton.value?.map((item) => {
                if (item.id == props.contentInfo.id) {
                    item.showButton = !item.showButton;
                    showButton.value = item.showButton;
                    if (showButton.value) {
                        item.IKButton = false;
                        IKButton.value = false;
                    }
                }
            });
            // 存储数据
            y9_storage.setObjectItem('titleShowButton', allTitleShowButton.value);
        }

        content.value = articleContent.value;

        if (props.selectwordList.length && showButton.value) {
            // 显示标注结果
            let wordsList = sortArray($deeploneObject(props.selectwordList));
            await wordsList?.map((item: any) => {
                const reg = new RegExp(`${item}`, 'g');
                if (content.value.includes(item)) {
                    content.value = content.value.replace(
                        reg,
                        `<b style="border: 1px solid red;
                border-radius: 4px;
                font-weight: 500;">${item}</b>`
                    );
                }
            });
        }
    }

    // 数组按字符串长度降序排序
    function sortArray(array) {
        array.sort((a, b) => b.length - a.length);
        return array;
    }

    // 组合键
    key('ctrl+k,shift+s,enter', function () {
        let selectionValue = getSelectionText();
        if (selectionValue) {
            emits('getkey', selectionValue);
        }
        // 清除选中，避免重复操作
        clearSelectionText();
        return false;
    });

    // 清除
    function clearSelectionText() {
        if (window.getSelection()) {
            let selection = window.getSelection();
            selection?.removeAllRanges();
        } else if (document.selection && document.selection.clear) {
            document.selection.clear();
        }
    }
</script>

<style lang="scss" scoped>
    @import '@/theme/global.scss';
    .article {
        height: 100%;
        .content-top {
            .top-row {
                margin-bottom: 15px;
                box-sizing: border-box;
                > span:nth-child(1) {
                    display: table-cell;
                    white-space: nowrap;
                    color: var(--el-color-primary);
                    opacity: 0.8;
                    text-align: right;
                }
                > span:nth-child(2) {
                    display: table-cell;
                    color: #333;
                    opacity: 0.9;
                }
            }
            .y9-content-form {
                width: 100%;
                margin-bottom: 8px;
                :deep(.el-descriptions__table) {
                    .el-descriptions__content {
                        div {
                            @include textEllipsis;
                        }
                    }
                    .el-descriptions__label {
                        div {
                            span {
                                display: inline-block;
                                width: 100%;
                                text-align-last: justify;
                            }
                        }
                    }
                    .el-descriptions__cell {
                        padding: 8px 16px;
                    }
                }
            }
            .content-middle {
                :deep(.el-descriptions__table) {
                    .el-descriptions__cell {
                        div {
                            span {
                                font-size: 16px;
                            }
                        }
                    }
                }
            }
            .content-small {
                :deep(.el-descriptions__table) {
                    .el-descriptions__cell {
                        div {
                            span {
                                font-size: 14px;
                            }
                        }
                    }
                }
            }
            .content-large {
                :deep(.el-descriptions__table) {
                    .el-descriptions__cell {
                        div {
                            span {
                                font-size: 18px;
                            }
                        }
                    }
                }
            }
        }
        .content-middle {
            font-size: 16px;
            line-height: 28px;
        }
        .content-small {
            font-size: 14px;
            line-height: 24px;
        }
        .content-large {
            font-size: 18px;
            line-height: 30px;
        }
        .content-zhengwen {
            color: var(--el-color-primary);
            opacity: 0.8;
            text-align: right;
            display: inline-block;
        }
        .content-fontsize {
            font-size: 14px;
            span {
                cursor: pointer;
            }
            span:nth-child(2) {
                margin: 0 10px;
            }
            .focus-button {
                color: var(--el-button-hover-text-color);
                border-color: var(--el-button-hover-border-color);
                background-color: var(--el-button-hover-bg-color);
                outline: 0;
            }

            .primary-button:hover {
                color: var(--el-button-hover-bg-color);
                border-color: var(--el-button-border-color);
                background-color: var(--el-button-bg-color);
                outline: 0;
            }

            .primary-button {
                color: var(--el-color-primary);
                border-color: var(--el-color-primary);
                background-color: var(--el-color-white);
                outline: 0;
            }

            .is-disabled {
                color: var(--el-color-primary-light-3);
            }
        }
        .content-size {
            margin: 20px 0;
            height: 16px;
            width: 100%;
            text-align: right;
        }
        .content-btn {
            margin: 15px 0;
            display: flex;
            justify-content: space-between;
            align-items: center;
            :deep(.el-button) {
                line-height: 26px;
                height: 26px;
            }
        }
        .content-main {
            color: #333;
            opacity: 0.9;
            padding-bottom: 20px;
            box-sizing: border-box;
            display: inline;
        }
        .red {
            color: red;
        }
    }

    pre {
        white-space: pre-wrap;
        white-space: -moz-pre-wrap;
        white-space: -pre-wrap;
        white-space: -o-pre-wrap;
        *word-wrap: break-word;
        *white-space: normal;
        line-height: 20px;
    }

    #menu {
        width: 170px;
        background: #fff;
        box-shadow: $boxShadow;
        position: absolute;
        display: none;
        border-radius: 4px;
        box-sizing: border-box;
        border: 1px solid #efefef;
        z-index: 2;
        ul {
            list-style: none;
            font-size: 16px;
            color: #333;
            padding-left: 0;
            margin: 10px 0;
        }
        li {
            padding: 5px 0 5px 40px;
        }
        li:hover {
            background-color: #efefef;
            cursor: pointer;
        }
    }
</style>
