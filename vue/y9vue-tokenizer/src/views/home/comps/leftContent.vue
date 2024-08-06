<template>
    <div class="content content-r content-title" :class="{ 'content-icon': menuShrink }">
        <i v-if="!menuShrink" class="ri-menu-fold-line" @click="handleChangeMenuShrink(true)"></i>
        <i v-else class="ri-menu-unfold-line" @click="handleChangeMenuShrink(false)"></i>
        <div class="title-btn">
            <div v-if="!menuShrink">
                <div style="display: flex; margin-top: 10px">
                    <i class="ri-menu-line" style="margin: 0 5px 0 10px" @click.stop="isSelectData = true"></i>
                    {{ topTitleData.name }}（{{ topTitleData.num }}）
                </div>
                <div class="data-select">
                    <i class="ri-edit-line" @click="selectDialogConfig.show = true"></i>
                    当前数据：{{ showRangeData.minNum }} - {{ showRangeData.maxNum }}
                </div>
                <div v-if="isSelectData && selectDataList.length > 1" class="select-data" id="select-data">
                    <div
                        v-for="(item, index) in selectDataList"
                        :key="index"
                        class="select-data-item"
                        :class="{ 'selected-item': topTitleData.name == item.name }"
                        @click.stop="handleClickSelectData(item)"
                    >
                        <span>{{ item.name }}（{{ item.num }}）</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="title-btns" v-if="!menuShrink">
            <div class="btns">
                <span
                    v-for="(item, index) in typeList"
                    :key="index"
                    @click="handlerClickType(item.key)"
                    :class="{ 'title-btn-active': titleListType == item.key }"
                >
                    <i :class="item.icon"></i>{{ item.name }}</span
                >
            </div>
        </div>
        <div class="title-list" v-if="!menuShrink" v-loading="titleLoading">
            <div
                v-for="(item, index) in articleTitleList"
                :key="index"
                class="title-item"
                :class="{
                    'title-active': currNo == item.index,
                }"
            >
                <span
                    @click="
                        handlerClickTitle(
                            item.id,
                            (y9PaginationSquareBgConfig.currentPage - 1) * 10 + index,
                            item.index
                        )
                    "
                    >{{ item.index }}. {{ item.title }}</span
                >
                <i
                    :class="{
                        'ri-bookmark-3-line': item.mark == '已标注',
                        'ri-bookmark-line': item.mark == '未标注',
                        'ri-bookmark-fill': item.mark == '标注中',
                    }"
                ></i>
            </div>
            <div v-if="!articleTitleList.length" class="empty-text">暂无数据</div>
        </div>
        <y9Pagination
            :config="y9PaginationSquareBgConfig"
            v-if="!menuShrink && articleTitleList.length"
            @current-change="onCurrentPageChange"
        ></y9Pagination>
        <y9Dialog v-model:config="selectDialogConfig">
            <div class="dialog-content">
                <span class="content-tip">*当前类型数据总量为{{ topTitleData.num }}</span>
                <el-form
                    ref="ruleFormRef"
                    class="content-box"
                    :model="listDataSelect"
                    :rules="rules"
                    label-width="auto"
                >
                    <div style="display: flex; align-items: flex-start">
                        <el-form-item label="列表数据选择" prop="minNum">
                            <el-input v-model="listDataSelect.minNum" style="width: 100px" />
                        </el-form-item>
                        <div style="height: 32px">
                            <span class="box-line"></span>
                        </div>
                        <el-form-item label="" prop="maxNum">
                            <el-input v-model="listDataSelect.maxNum" style="width: 100px" />
                        </el-form-item>
                    </div>
                </el-form>
                <div class="content-option">
                    <el-button type="primary" style="margin-right: 28px" @click="handleClickSubmit(ruleFormRef)"
                        >确认</el-button
                    >
                    <el-button @click="selectDialogConfig.show = false">取消</el-button>
                </div>
            </div>
        </y9Dialog>
        <el-button style="display: none" v-loading.fullscreen.lock="loading"></el-button>
    </div>
</template>

<script lang="ts" setup>
    import { onMounted, ref, watch, reactive, onBeforeUnmount } from 'vue';
    import type { FormInstance, FormRules } from 'element-plus';
    import { topTitleData, currNo } from './data';
    import { getArticleTitle, getArticleTypeList } from '@/api/home/index';
    import { $deeploneObject } from '@/utils/object';
    interface titleType {
        title?: string;
        mark?: string;
        id?: string;
    }

    interface dataSelectType {
        minNum?: string | number;
        maxNum?: string | number;
    }

    interface topTitleType {
        name?: string;
        num?: number;
    }

    let initnite = ref(true);

    let loading = ref(false);

    let emits = defineEmits(['change-article-id', 'change-shrink', 'auto-save', 'update-addNewArticle']);
    let props = defineProps({
        goOrBack: {
            type: String,
            default: '',
        },
        // 标注状态
        markStatus: {
            type: String,
            default: '',
        },
        // 是否新增文章
        addNewArticle: {
            type: Boolean,
            default: false,
        },
        // 点击上篇下篇
        articleClickNum: {
            type: Number,
            default: 0,
        },
        // 添加文章选择或新增的文章类型
        newArticleName: {
            type: String,
            default: '',
        },
    });

    onMounted(async () => {
        await initArticleList();
        await handleData();
        initTitle();
        document.addEventListener('click', hiddenSearchHistory);
    });

    //点击类型列表以外的地方，隐藏类型列表
    const hiddenSearchHistory = (e) => {
        let div = document.getElementById('select-data');
        // 点击除类型列表以外的地方，类型列表隐藏
        if (div && !div.contains(e.target)) {
            isSelectData.value = false;
        }
    };
    onBeforeUnmount(() => {
        document.removeEventListener('click', hiddenSearchHistory);
    });

    // 对存储的选择数据 进行处理以及赋值
    function handleData() {
        // 获取选择数据的值
        let result = handleDataRangeStorage(topTitleData.value, 'search');
        // 赋值
        showRangeData.value = $deeploneObject(result);
        listDataSelect.value = $deeploneObject(result);
    }

    /***
     * 左侧文章标题部分
     */
    // 收缩图标控制切换 的变量属性
    let menuShrink = ref<boolean>(false);
    // 可选择的数据类型
    let selectDataList = ref<Array<topTitleType>>([]);

    function isEmptyOrNullOrUndefined(value) {
        return value === null || value === undefined || (typeof value === 'object' && Object.keys(value).length === 0);
    }

    // 获取文章类型列表
    async function initArticleList(assignment = false) {
        let result = await getArticleTypeList();
        let resultList = Object.keys(result.data);
        selectDataList.value = resultList?.map((item) => {
            return { name: item, num: result.data[item] };
        });
        if (assignment) return;
        let titleData = JSON.parse(localStorage.getItem('type'));
        isEmptyOrNullOrUndefined(titleData)
            ? (topTitleData.value = selectDataList.value[0])
            : (topTitleData.value = titleData);
        // 没有值就赋值
        if (!showRangeData.value.maxNum) showRangeData.value.maxNum = topTitleData.value.num;
        if (!listDataSelect.value.maxNum) listDataSelect.value.maxNum = topTitleData.value.num;
    }
    // 是否显示可选择的数据类型
    let isSelectData = ref<boolean>(false);

    // 分类数组
    let typeList = [
        {
            name: '全部',
            icon: '',
            key: null,
        },
        {
            name: '未标注',
            icon: 'ri-bookmark-line',
            key: '未标注',
        },
        {
            name: '标注中',
            icon: 'ri-bookmark-fill',
            key: '标注中',
        },
        {
            name: '已标注',
            icon: 'ri-bookmark-3-line',
            key: '已标注',
        },
    ];

    // 列表数据选择弹框
    const selectDialogConfig = ref({
        show: false,
        width: '35%',
        title: '列表数据选择',
        showHeaderClose: false,
        showHeaderFullscreen: false,
        showFooter: false,
        onOk: (config) => {},
    });
    const ruleFormRef = ref<FormInstance>();
    const validatePass = (rule: any, value: any, callback: any) => {
        if (value === '') {
            callback(new Error('请输入数据'));
        } else if (!isValidNumber(value)) {
            // 使用函数验证每个输入是否只包含数字
            callback(new Error('请输入有效数字'));
        } else if (value > topTitleData.value.num) {
            callback(new Error(`输入超过当前总量`));
        } else if (listDataSelect.value.maxNum !== '' && Number(value) > Number(listDataSelect.value.maxNum)) {
            callback(new Error('输入大于右侧数字'));
        } else {
            callback();
        }
    };
    const validatePass2 = (rule: any, value: any, callback: any) => {
        if (value === '') {
            callback(new Error('请输入数据'));
        } else if (!isValidNumber(value)) {
            // 使用函数验证每个输入是否只包含数字
            callback(new Error('请输入有效数字'));
        } else if (value > topTitleData.value.num) {
            callback(new Error(`输入超过当前总量`));
        } else if (listDataSelect.value.minNum !== '' && Number(value) < Number(listDataSelect.value.minNum)) {
            callback(new Error('输入小于左侧数字'));
        } else {
            callback();
        }
    };
    const rules = reactive<FormRules<typeof listDataSelect>>({
        minNum: [{ validator: validatePass, trigger: 'blur' }],
        maxNum: [{ validator: validatePass2, trigger: 'blur' }],
    });

    // title数组
    let articleTitleList = ref<Array<titleType>>([]);
    // 文章id
    let articleId = ref('');
    // 当前active的title index
    let activeTitleIndex = ref<number>(0);
    // title 部分的loading
    let titleLoading = ref(false);
    // 列表分类的 数值
    let titleListType = ref<string | null>(null);
    // title分类 内容
    let y9PaginationSquareBgConfig = ref({
        layout: 'prev, pager, next, jumper', //布局
        currentPage: 1, //当前页数，支持 v-model 双向绑定
        pageSize: 10, //每页显示条目个数，支持 v-model 双向绑定
        total: 0, //总条目数
        pagerCount: 5,
    });

    // 监听收缩展开，将值传递给父组件供其他区域宽度自适应
    watch(
        () => props.articleClickNum,
        (newVal) => {
            handleClickGOOrBack(props.goOrBack);
        }
    );

    // 输入框的双向绑定数据选择的两个数据
    const listDataSelect = ref<dataSelectType>({
        minNum: '1',
    });

    // 存储选择数据 显示页面
    const showRangeData = ref<dataSelectType>({
        minNum: 1,
    });

    // 数字 输入框正则
    function isValidNumber(input) {
        // 使用正则表达式验证只包含数字
        return /^[1-9]\d*$/.test(input);
    }

    // 判断存储的数组 是否有当前数据 有就更新/获取 没有就添加/不管
    function handleDataRangeStorage(titleType, handleType, range?) {
        let titleNumList = JSON.parse(localStorage.getItem('selectData'));
        if (!titleNumList) titleNumList = [];
        let rangeNum = ref({});
        // 筛选是否有当前的数据
        let matchNumData = titleNumList?.filter((item) => item.name == titleType.name);
        if (matchNumData?.length) {
            // 存在
            switch (handleType) {
                // 获取值
                case 'search':
                    rangeNum.value = matchNumData[0].range;
                    break;
                // 更新值
                default:
                    let newList = titleNumList?.filter((item) => item.name != titleType.name);
                    newList.push({ name: titleType.name, range });
                    localStorage.setItem('selectData', JSON.stringify(newList));
                    break;
            }
        } else {
            // 不存在
            switch (handleType) {
                // 获取值
                case 'search':
                    rangeNum.value = { minNum: 1, maxNum: titleType.num };
                    break;
                // 更新值
                default:
                    titleNumList.push({ name: titleType.name, range });
                    localStorage.setItem('selectData', JSON.stringify(titleNumList));
                    break;
            }
        }
        return rangeNum.value;
    }

    // 点击确认 提交
    function handleClickSubmit(formEl: FormInstance | undefined) {
        if (!formEl) return;
        formEl.validate((valid) => {
            if (valid) {
                loading.value = true;
                let params = {
                    minNum: Number(listDataSelect.value.minNum),
                    maxNum: Number(listDataSelect.value.maxNum),
                };
                // 请求接口
                setTimeout(() => {
                    initnite.value = true;
                    // 请求获取标题的接口
                    initTitle();
                    loading.value = false;
                    // 存储当前选择的类型范围
                    handleDataRangeStorage(topTitleData.value, 'storage', params);
                    // 关闭弹窗
                    selectDialogConfig.value.show = false;
                    showRangeData.value = $deeploneObject(listDataSelect.value);
                }, 300);
            } else {
                console.log('error submit!');
            }
        });
    }

    async function handleClickGOOrBack(type) {
        // 边界条件
        if (activeTitleIndex.value == 0 && type == 'back') return;
        if (activeTitleIndex.value == y9PaginationSquareBgConfig.value.total - 1 && type == 'next') return;
        if (type == 'back') {
            // 上篇 第二条及以上可以点击上篇
            activeTitleIndex.value -= 1;
        } else {
            // 下篇
            activeTitleIndex.value += 1;
        }
        // 当前页数的更新 请求对应页面的数据
        if (y9PaginationSquareBgConfig.value.currentPage !== Math.trunc(activeTitleIndex.value / 10) + 1) {
            // console.log('-----------------');
            y9PaginationSquareBgConfig.value.currentPage = Math.trunc(activeTitleIndex.value / 10) + 1;
            await initTitle();
        }
        // console.log('0000---------');
        // 模拟点击
        handlerClickTitle(
            articleTitleList.value[activeTitleIndex.value % 10].id,
            activeTitleIndex.value,
            articleTitleList.value[activeTitleIndex.value % 10].index
        );
    }

    // 监听标注状态
    watch(
        () => props.markStatus,
        (newStatus) => {
            if (newStatus) {
                handleUpdateMarkStatus(newStatus);
            }
        }
    );

    // 更新相应标题的标注状态
    function handleUpdateMarkStatus(status) {
        articleTitleList.value?.map((item) => {
            if (item.id == articleId.value) {
                item.mark = status;
            }
        });
    }

    // 导入文章与是否请求接口的逻辑判断
    watch(
        () => props.addNewArticle,
        (newArticle) => {
            if (newArticle) {
                if (props.newArticleName == topTitleData.value.name) {
                    // 新增文章类型与当前选择的类型一致  重新请求接口
                    y9PaginationSquareBgConfig.value.currentPage = 1;
                    titleListType.value = null;
                    activeTitleIndex.value = 0;
                    initnite.value = true;
                    initTitle();
                }
                let matchData = selectDataList.value.filter((item) => item.name == props.newArticleName);
                if (!matchData.length) {
                    // 重新请求 文章类型列表
                    initArticleList(true);
                }
                emits('update-addNewArticle');
            }
        }
    );

    // menu收缩 展开
    function handleChangeMenuShrink(value: boolean) {
        menuShrink.value = value;
        emits('change-shrink', value);
    }

    async function handleClickSelectData(info) {
        isSelectData.value = false;
        if (info.name == topTitleData.value.name) return;
        topTitleData.value = info;
        // 存储当前选择的类型
        localStorage.setItem('type', JSON.stringify(topTitleData.value));
        await handleData();
        initnite.value = true;
        initTitle();
    }

    // 请求title接口
    async function initTitle() {
        titleLoading.value = true;
        let params = {
            mark: titleListType.value,
            page: y9PaginationSquareBgConfig.value.currentPage,
            rows: 10,
            type: topTitleData.value.name,
            startIndex: Number(listDataSelect.value.minNum),
            endIndex: Number(listDataSelect.value.maxNum),
        };
        let result = await getArticleTitle(params);
        if (result.code == 0) {
            y9PaginationSquareBgConfig.value.total = result.data.total;
            articleTitleList.value = result.data.dataList;
            articleId.value = result.data.dataList[0]?.id;
            currNo.value = result.data.dataList[0]?.index;
        }
        titleLoading.value = false;
        if (initnite.value) {
            emits('change-article-id', articleId.value, y9PaginationSquareBgConfig.value.total, activeTitleIndex.value);
            initnite.value = false;
        }
    }

    // 点击 全部 未标注 已标注按钮
    async function handlerClickType(type) {
        // 查看keywordsList是否有值 有值的话可能就是用户自己没保存 需要自动保存
        await emits('auto-save');
        titleListType.value = type;
        activeTitleIndex.value = 0;
        y9PaginationSquareBgConfig.value.currentPage = 1;
        initnite.value = true;
        initTitle();
    }

    // 当前页数变化
    function onCurrentPageChange(currPage) {
        y9PaginationSquareBgConfig.value.currentPage = currPage;
        activeTitleIndex.value = (currPage - 1) * 10;
        initnite.value = true;
        initTitle();
    }

    // 点击title
    async function handlerClickTitle(id, index: number, activeNo) {
        // console.log(id, 'id--------');
        // 查看keywordsList是否有值 有值的话可能就是用户自己没保存 需要自动保存
        await emits('auto-save');
        activeTitleIndex.value = index;
        articleId.value = id;
        currNo.value = activeNo;
        await emits('change-article-id', id, y9PaginationSquareBgConfig.value.total, index);
    }
</script>

<style lang="scss" scoped>
    @import '@/theme/global.scss';
    .content {
        height: calc(100vh - 4vh - 40px);
        border: 1px solid #efefef;
        box-shadow: $boxShadow;
        border-radius: 4px;
        background-color: var(--el-bg-color);
    }
    .content-r {
        margin-right: 20px;
    }
    .content-title {
        position: relative;
        .ri-menu-fold-line,
        .ri-menu-unfold-line {
            position: absolute;
            right: 10px;
            font-size: 22px;
            top: 8px;
            cursor: pointer;
            color: var(--el-bg-color);
            // font-weight: 600;
        }
        .title-btns {
            margin-bottom: 18px;
            height: 35px;
            line-height: 35px;
            background: var(--el-bg-color);
            color: rgb(132, 133, 137);
            font-size: 14px;
            border-bottom: 1px solid #efefef;
            .btns {
                width: 100%;
                display: flex;
                > span {
                    padding: 0 10px;
                    cursor: pointer;
                    display: inline-flex;
                    width: calc((100% - 20%) / 3);
                    box-sizing: border-box;
                    justify-content: center;
                    align-items: center;
                    i {
                        margin-left: 1px;
                        margin-top: 1px;
                    }
                }
                > span:hover {
                    color: var(--el-color-primary);
                }
                > span:nth-child(1) {
                    width: 20%;
                    justify-content: left;
                }
                > span:nth-last-child(1) {
                    justify-content: right;
                }
            }
        }
        .title-btn {
            height: 70px;
            background: var(--el-color-primary);
            color: #fff;
            font-size: 16px;
            display: flex;
            // align-items: center;
            letter-spacing: 1px;
            border-radius: 4px 4px 0 0;
            .select-data {
                position: absolute;
                background: #fff;
                width: 75%;
                box-shadow: $boxShadow;
                max-height: 300px;
                font-size: 14px;
                color: rgb(132, 133, 137);
                padding: 5px 0;
                top: 40px;
                .select-data-item {
                    padding: 5px 0 5px 10px;
                    height: 22px;
                    line-height: 22px;
                }
                .selected-item {
                    color: var(--el-color-primary);
                }
                .select-data-item:hover {
                    color: var(--el-color-primary);
                    background: var(--el-color-primary-light-7);
                    cursor: pointer;
                }
            }
            .data-select {
                display: flex;
                margin: 12px 5px 0px 10px;
                font-size: 13px;
                > i {
                    margin-right: 5px;
                    cursor: pointer;
                    &:hover {
                        opacity: 0.8;
                    }
                }
            }
        }
        .title-btn-active {
            color: var(--el-color-primary);
            border-bottom: 2px solid var(--el-color-primary);
        }
        .title-list {
            // 132px
            height: calc(100% - 4vh - 198px);
            overflow: auto;
            margin-top: 10px;
            .title-item {
                margin: 0 10px 10px 10px;
                font-size: 16px;
                line-height: 20px;
                vertical-align: middle;
                span {
                    cursor: pointer;
                }
                i {
                    margin-left: 10px;
                    vertical-align: middle;
                }
            }
            .title-active {
                color: var(--el-color-primary);
            }
            .title-item:hover {
                span {
                    color: var(--el-color-primary-light-3);
                }
                i {
                    color: var(--el-color-primary-light-3);
                }
            }
            // 设置滚动条宽度
            // &::-webkit-scrollbar {
            // 	width: 5px;
            // }
            .empty-text {
                width: 100%;
                text-align: center;
                height: 200px;
                line-height: 200px;
                color: #999;
            }
        }
        :deep(.y9-pagination) {
            position: absolute;
            bottom: 60px;
            display: flex;
            width: 100%;
            justify-content: center;
            .custom-circular {
                padding: 0;
                .el-pagination__jump {
                    position: absolute;
                    bottom: -45px;
                    right: 10px;
                    .el-input {
                        height: 28px;
                        line-height: 28px;
                        .el-input__wrapper {
                            padding: 0 7px;
                            height: 28px;
                            .el-input__inner {
                                font-size: 14px;
                            }
                        }
                    }
                }
            }
            .total-div {
                position: absolute;
                bottom: -45px;
                left: 10px;
            }
        }
    }
    .content-icon {
        height: 30px;
    }
    .dialog-content {
        .content-tip {
            color: var(--el-color-primary);
            letter-spacing: 1px;
        }
        .content-box {
            margin-top: 20px;
            display: flex;
            align-items: center;
            .box-title {
                margin-right: 20px;
                font-size: 16px;
            }
            .box-line {
                display: inline-block;
                width: 18px;
                height: 2px;
                background: #d4d7de;
                margin: 0 12px;
            }
            :deep(.el-form-item__label) {
                font-size: 16px;
                letter-spacing: 1px;
            }
            :deep(.el-input__inner) {
                text-align: center;
            }
        }
        .content-option {
            width: 100%;
            text-align: center;
            > :deep(.el-button) {
                width: 68px;
            }
        }
    }
</style>
