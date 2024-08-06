<template>
    <div class="home-contents" @click="handleClickShow">
        <!-- 标题 -->
        <div class="home-title">
            <div style="font-size: 18px; font-weight: bold; color: var(--el-color-primary)">政务分词器</div>
            <el-dropdown placement="bottom-start" popper-class="popper-menu" @command="handleClickCommand">
                <i class="ri-menu-line"></i>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item command="add"> 添加文章 </el-dropdown-item>
                        <el-dropdown-item command="export">导出分词</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
            <span class="shortcut-keys">
                <span>快捷键：Ctrl+k/Shift+s/enter</span>
                <span @click="handleLogout" class="logout"><i class="ri-logout-box-r-line"></i>退出</span>
            </span>
        </div>
        <!-- 内容 -->
        <div class="home-content">
            <!-- 左侧文章标题 -->
            <div :style="{ width: lastMenuShrink ? '62px' : '320px' }">
                <leftContent
                    @change-article-id="handleUpdateArticleId"
                    @change-shrink="handleUpdateShrink"
                    @auto-save="handleAutoSave"
                    @update-addNewArticle="addNewArticle = false"
                    :goOrBack="articleGoOrBack"
                    :markStatus="articleMarkStatus"
                    :addNewArticle="addNewArticle"
                    :newArticleName="newArticleName"
                    :articleClickNum="articleClickNum"
                ></leftContent>
            </div>
            <!-- 文章内容 -->
            <div :style="{ width: lastMenuShrink ? 'calc(100% - 362px)' : 'calc(100% - 620px)' }">
                <div class="content content-r content-article">
                    <div class="article-navigation" v-if="articleListLength">
                        <span :class="{ 'article-diabled': activeIn == 0 }" @click="handlerClickBtn('back')"
                            ><i class="ri-arrow-left-s-line" style="font-size: 18px"></i>上篇</span
                        >
                        <span
                            :class="{ 'article-diabled': activeIn == articleListLength - 1 }"
                            @click="handlerClickBtn('next')"
                            >下篇<i class="ri-arrow-right-s-line" style="font-size: 18px"></i
                        ></span>
                    </div>
                    <div class="article-content" id="article-content" v-loading="articleLoading">
                        <articleContent
                            :contentInfo="articleInfo"
                            :selectwordList="maxKeyWordsList"
                            :currArticleId="currArticleId"
                            @getkey="handlerInputChange"
                        />
                    </div>
                </div>
            </div>
            <!-- 新的关键词 -->
            <div style="width: 300px">
                <div class="content new-keyword">
                    <div class="newwords-title">
                        <div @click="handlerClickTab('new')" :class="{ 'title-tab-active': recordKeyTab == 'new' }"
                            >标注新关键词</div
                        >
                        <div
                            @click="handlerClickTab('record')"
                            :class="{ 'title-tab-active': recordKeyTab == 'record' }"
                            >关键词记录</div
                        >
                    </div>
                    <!-- 标注新关键词 -->
                    <div class="keywords" v-if="recordKeyTab == 'new'" v-loading="keyWordLoading">
                        <!-- 接口返回的已保存关键词 -->
                        <div
                            v-for="(item, index) in articleKeywords"
                            :key="index"
                            class="keywords-span"
                            style="background-color: rgba(132, 133, 137, 0.9)"
                        >
                            <span>{{ item.word }}</span>
                            <i @click="handlerDeleteWord(item.word, 'remote')" class="ri-close-line"></i>
                        </div>
                        <!-- 新关键词 -->
                        <div v-for="(item, index) in keywordsList" :key="index" class="keywords-span">
                            <span>{{ item }}</span>
                            <i @click="handlerDeleteWord(item, 'own')" class="ri-close-line"></i>
                        </div>
                        <el-input v-model="keyword" placeholder="" @change="handlerInputChange"></el-input>
                    </div>
                    <!-- 关键词记录 -->
                    <div
                        class="keywords"
                        v-if="recordKeyTab == 'record'"
                        style="text-align: left"
                        v-loading="keyWordLoading"
                    >
                        <div v-for="(item, index) in keywordRecordList" :key="index" class="keywords-record">
                            <div class="record-info">
                                <span>{{ item.time }}</span>
                                <span>{{ item.operation }}</span>
                            </div>
                            <span class="record-keyword">{{ item.word }}</span>
                        </div>
                    </div>

                    <div class="btn-style">
                        <div>
                            <el-button
                                @click="handlerNewKeywordSave('click')"
                                :disabled="keywordsList.length == 0"
                                class="global-btn-second"
                                >保存</el-button
                            >
                            <el-button style="margin-left: 10%" @click="handlerClickFinish" class="global-btn-second"
                                >标注完成</el-button
                            >
                        </div>
                        <span class="tips">*已保存的关键词会进入IK分词器中进行分词验证</span>
                    </div>
                </div>
            </div>
        </div>
        <el-button
            style="display: none"
            v-loading.fullscreen.lock="loading"
            :element-loading-text="loadingText"
        ></el-button>
    </div>
    <y9Dialog v-model:config="addArticleDialog" style="letter-spacing: 1px">
        <AddArticle @update-show="addArticleDialog.show = false" @add-newArticle="handleGetNewArticleName" />
    </y9Dialog>
</template>
<script lang="ts" setup>
    import { onMounted, ref } from 'vue';
    import axios from 'axios';
    import { $y9_SSO } from '@/main';
    import { ElNotification, ElMessageBox, ElMessage } from 'element-plus';
    import { getArticleContent, addKeyWords, recordFinished, DeleteKeyWords } from '@/api/home/index';
    // 文章
    import articleContent from './comps/articleContent.vue';
    // 左侧标题
    import leftContent from './comps/leftContent.vue';
    // 添加文章
    import AddArticle from './comps/addArticle.vue';

    // 全局loading
    let loading = ref(false);
    // loading的文本

    let loadingText = ref('');

    // 是否手动保存
    let clickSave = ref(false);

    interface amendmentRecordType {
        time?: string;
        operation?: string;
        word: string;
    }

    /***
     * 标题相关变量
     */
    // 文章id
    let currArticleId = ref('');
    // 收缩 展开
    let lastMenuShrink = ref(false);
    // 文章的标注状态
    let articleMarkStatus = ref('');
    // 文章的活跃下标
    let activeIn = ref(0);
    // 是否新增文章
    let addNewArticle = ref(false);
    // 文章数组的长度
    let articleListLength = ref(0);
    // 标题的下拉菜单显示
    let showSelectList = ref(false);

    async function handleUpdateArticleId(newId, listLength, activeIndex) {
        if (listLength) {
            articleListLength.value = listLength;
        }
        currArticleId.value = newId;
        clickSave.value = false;
        activeIn.value = activeIndex;
        articleMarkStatus.value = '';
        await initArticleContent(newId);
    }

    function handleUpdateShrink(newValue) {
        lastMenuShrink.value = newValue;
    }

    // 查看keywordsList是否有值 有值的话可能就是用户自己没保存 需要自动保存
    async function handleAutoSave() {
        if (keywordsList.value.length) {
            await handlerNewKeywordSave('auto');
        }
    }

    function handleClickShow() {
        // showSelectList.value = false;
    }

    /***
     * 文章内容
     */
    // 上篇 下篇的变量
    let articleGoOrBack = ref('');
    // 上篇下篇的点击变量
    let articleClickNum = ref(0);
    // 文章的内容对象
    let articleInfo = ref({});
    // 关键词操作记录
    let keywordRecordList = ref<Array<amendmentRecordType>>([]);
    // 本文关键词
    let articleKeywords = ref<Array<amendmentRecordType>>([]);
    // 本文关键词与新增关键词的总和数组
    let allKeyWordsList = ref<Array<string>>([]);
    // 本文关键词和新增关键词以及所有文章的关键词的整合数组
    let maxKeyWordsList = ref<Array<string>>([]);
    // 单个所有文章关键词的数组
    let allArticleKeysList = ref<Array<string>>([]);
    // 文章的loading
    let articleLoading = ref(false);
    // 文章内容接口
    async function initArticleContent(id) {
        // console.log(id, 'id');
        articleLoading.value = true;
        keyWordLoading.value = true;
        let params = {
            id,
        };
        let result = await getArticleContent(params);
        if (result.code == 0) {
            articleInfo.value = result.data;
            keywordRecordList.value = result.data.amendmentRecord;
            articleKeywords.value = result.data.words;
            allKeyWordsList.value = result.data.words?.map((item) => {
                return item.word;
            });
            // 将全部关键词与本篇关键词合并 并去重
            allArticleKeysList.value = result.data.dic;
            maxKeyWordsList.value = Array.from(new Set(allKeyWordsList.value.concat(result.data.dic)));
        }
        articleLoading.value = false;
        keyWordLoading.value = false;
        //console.log(articleLoading.value)
    }

    // 点击上篇 下篇按钮
    function handlerClickBtn(type) {
        // 临界条件
        if (activeIn.value == 0 && type == 'back') return;
        if (activeIn.value == articleListLength.value - 1 && type == 'next') return;
        switch (type) {
            case 'back':
                articleClickNum.value--;
                break;

            default:
                articleClickNum.value++;
                break;
        }
        articleGoOrBack.value = type;
    }

    /***
     * 新的关键词
     */
    // 输入框值
    let keyword = ref('');
    // 新增关键词的数组
    let keywordsList = ref<Array<string>>([]);

    // 输入框值改变
    function handlerInputChange(value) {
        let errorLength = value.match(/[^A-Za-z0-9\u4e00-\u9fa5]/g);
        if (errorLength && errorLength.length) {
            ElNotification({
                title: '提示',
                message: '关键词不可包含空格和符号',
                type: 'error',
            });
            return;
        }
        if (value && value.trim()?.length > 0) {
            let matchList = allKeyWordsList.value?.filter((item) => item == value);
            if (matchList?.length) {
                // 存在提示已有该数据 并不push到数组中
                ElNotification({
                    title: '提示',
                    message: '新的关键词中已有该数据',
                    type: 'error',
                });
            } else {
                // 不存在 push进数组 且输入框值清空
                keywordsList.value.push(value);
                allKeyWordsList.value.push(value);
                maxKeyWordsList.value.push(value);
                keyword.value = '';
                scrollTopInfo();
            }
        }
    }

    // 保存右边的列表的滚动条 在最下面
    function scrollTopInfo() {
        // 保证输入框在滚动时一直显示在可视范围的最下方不被隐藏
        let inputElement = document.getElementsByClassName('keywords');
        if (inputElement[0]) {
            setTimeout(() => {
                let spanElements: any = document.getElementsByClassName('keywords-span');
                spanElements = [].slice.apply(spanElements);
                let top = 0;
                spanElements?.map((item) => {
                    top += item.offsetHeight;
                });
                inputElement[0].scrollTop = top;
            }, 300);
        }
    }

    // 新关键词，关键词记录
    let recordKeyTab = ref('new');
    let keyWordLoading = ref(false);
    // 新关键词和关键词记录的点击切换
    function handlerClickTab(type) {
        keyWordLoading.value = true;
        recordKeyTab.value = type;
        scrollTopInfo();
        keyWordLoading.value = false;
    }

    // keyword 值点击删除
    function handlerDeleteWord(word: string, type: string) {
        if (type == 'remote') {
            // 请求接口
            ElMessageBox.confirm('是否确认删除该关键词?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'info',
            })
                .then(async () => {
                    loadingText.value = '';
                    loading.value = true;
                    await DeleteKeyWords({ id: currArticleId.value, keyword: word })
                        .then((result) => {
                            ElNotification({
                                title: result.code == 0 ? '成功' : '失败',
                                message: result.code == 0 ? '成功' : '失败',
                                type: result.code == 0 ? 'success' : 'error',
                                duration: 2000,
                                offset: 80,
                            });
                            // 删除后 接口关键词，所有关键词 和记录都更新
                            if (result.code == 0) {
                                articleKeywords.value = result.data.keyword?.map((item) => {
                                    return { word: item };
                                });
                                allKeyWordsList.value = result.data.keyword;
                                allKeyWordsList.value = allKeyWordsList.value.concat(keywordsList.value);
                                allArticleKeysList.value = allArticleKeysList.value?.filter((item) => item !== word);
                                maxKeyWordsList.value = Array.from(
                                    new Set(allKeyWordsList.value.concat(allArticleKeysList.value))
                                );
                                keywordRecordList.value = result.data.record;
                            }
                        })
                        .catch((err) => console.log(err));
                    loading.value = false;
                })
                .catch(() => {
                    ElMessage({
                        type: 'info',
                        message: '已取消删除操作',
                        offset: 65,
                    });
                });
        } else {
            keywordsList.value = keywordsList.value?.filter((item) => item !== word);
            allKeyWordsList.value = allKeyWordsList.value?.filter((item) => item !== word);
            maxKeyWordsList.value = Array.from(new Set(allKeyWordsList.value.concat(allArticleKeysList.value)));
        }
    }

    // save保存  添加关键词
    async function handlerNewKeywordSave(type, status = true) {
        loadingText.value = '正在保存中...';
        loading.value = true;
        if (type == 'click') clickSave.value = true;
        // 重新请求 保存关键词接口
        let params = {
            id: currArticleId.value,
            keywords: keywordsList.value.length > 0 ? keywordsList.value.join(' ') : null,
        };
        await addKeyWords(params)
            .then(async (result) => {
                // 提示信息
                if (type == 'click') {
                    ElNotification({
                        title: result.code == 0 ? '成功' : '失败',
                        message: result.code == 0 ? '保存关键词成功' : result.msg,
                        type: result.code == 0 ? 'success' : 'error',
                        duration: 2000,
                        offset: 80,
                    });
                }
                // 保存成功 标注状态改为标注中
                if (result.code == 0) {
                    if (status) {
                        articleMarkStatus.value = '标注中';
                    }
                    if (type == 'click' || !status) {
                        // 过滤出空格的
                        let filterDataList = result.data.keyword?.filter((item) => item && item.trim()?.length > 0);
                        articleKeywords.value = filterDataList?.map((item) => {
                            return { word: item };
                        });
                        keywordRecordList.value = result.data.record;
                    }
                    // 清空新关键词的数组
                    keywordsList.value = [];
                }
            })
            .catch((err) => {
                ElNotification({
                    title: '失败',
                    message: err.msg,
                    type: 'error',
                    duration: 2000,
                    offset: 80,
                });
            });
        loading.value = false;
    }

    // 标注完成
    async function handlerClickFinish() {
        loadingText.value = '';
        loading.value = true;
        let params = {
            id: currArticleId.value,
        };
        !clickSave.value && (await handlerNewKeywordSave('auto', false));
        await recordFinished(params)
            .then(async (result) => {
                // 提示信息
                ElNotification({
                    title: result.code == 0 ? '成功' : '失败',
                    message: result.code == 0 ? '标注成功' : result.msg,
                    type: result.code == 0 ? 'success' : 'error',
                    duration: 2000,
                    offset: 80,
                });
                // 标注完成 标注状态改变
                if (result.code == 0) {
                    articleMarkStatus.value = '已标注';
                }
            })
            .catch(() => {});
        loading.value = false;
    }

    // 添加文章 导出分词
    function handleClickCommand(command) {
        switch (command) {
            case 'add':
                addArticleDialog.value.show = true;
                break;
            default:
                loadingText.value = '';
                loading.value = true;
                const url = import.meta.env.VUE_APP_CONTEXT + 'rest/articles/exportData';
                axios
                    .get(url, { responseType: 'blob' })
                    .then((response) => {
                        const blob = new Blob([response.data]);
                        const a = document.createElement('a'); //创建a标签
                        a.href = window.URL.createObjectURL(blob); // 创建下载的链接
                        a.download = '导出分词'; //下载文件名称
                        a.style.display = 'none';
                        document.body.appendChild(a); //a标签追加元素到body内
                        a.click(); //模拟点击下载
                        document.body.removeChild(a); // 下载完成移除元素
                        window.URL.revokeObjectURL(a.href); // 释放掉blob对象
                    })
                    .catch(console.error);
                loading.value = false;
                break;
        }
    }

    // 添加文章的弹窗
    const addArticleDialog = ref({
        show: false,
        width: '35%',
        title: '添加文章',
        showFooter: false,
    });

    // 添加文章选择或新增的文章类型赋值 字段
    let newArticleName = ref('');

    // 得到添加文章选择或新增的文章类型
    function handleGetNewArticleName(name) {
        newArticleName.value = name;
        addNewArticle.value = true;
    }

    // 退出
    function handleLogout() {
        try {
            // const loginOut = await this.$store.dispatch("user/logout");
            const params = {
                to: { path: window.location.pathname },
                logoutUrl: import.meta.env.VUE_APP_SSO_LOGOUT_URL + import.meta.env.VUE_APP_NAME + '/',
                __y9delete__: () => {
                    // 删除前执行的函数
                    console.log('删除前执行的函数');
                },
            };
            $y9_SSO.ssoLogout(params);
        } catch (error) {
            ElMessage({
                message: error?.message || 'Has Error',
                type: 'error',
                appendTo: '#right-top',
                offset: 60,
            });
        }
    }
</script>

<style lang="scss" scoped src="./home-style.scss"></style>
