<template>
    <div>
        <y9Form ref="y9FromRef" :config="formConfig">
            <template #radioSelect>
                <el-radio-group v-model="formConfig.model.typeSelect" class="group-radio">
                    <div class="select-radio">
                        <el-radio value="select" size="large"> </el-radio>
                        <el-select
                            v-model="formData.selectValue"
                            placeholder="请选择文章类型"
                            @focus="handleClickRadio('select')"
                            @change="handleChange"
                        >
                            <el-option
                                v-for="item in typeSelectList"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value"
                            />
                        </el-select>
                    </div>
                    <el-radio value="add" size="large">
                        <el-input
                            v-model="formData.inputValue"
                            placeholder="添加文章类型"
                            @focus="handleClickRadio('add')"
                            @blur="handleBlur"
                        ></el-input>
                    </el-radio>
                </el-radio-group>
            </template>
            <template #fileList>
                <el-upload
                    class="upload-demo"
                    action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
                    :auto-upload="false"
                    v-model:file-list="formConfig.model.filesList"
                >
                    <i class="ri-add-circle-line"></i>
                </el-upload>
            </template>
            <template #slotSubmit>
                <div class="btn-style">
                    <el-button type="primary" class="submit-btn" @click="handleClickSubmit">确认</el-button>
                    <el-button @click="handleClickCancel">取消</el-button>
                </div>
            </template>
        </y9Form>
        <el-button style="display: none" v-loading.fullscreen.lock="loading"></el-button>
    </div>
</template>

<script setup lang="ts">
    import { ref, toRefs, reactive } from 'vue';
    import { articleUpload, getArticleTypeList } from '@/api/home/index';
    import { ElNotification } from 'element-plus';
    import { onMounted } from 'vue';

    const emits = defineEmits(['update-show', 'add-newArticle']);

    const validatePass = (rule: any, value: any, callback: any) => {
        if (formConfig.value.model.typeSelect == 'select') {
            if (formData.value.selectValue) {
                callback();
            } else {
                callback(new Error('请选择文章类型'));
            }
        } else {
            if (formData.value.inputValue) {
                callback();
            } else {
                callback(new Error('请输入新的文章类型'));
            }
        }
    };

    const state = reactive<any>({
        loading: false,
        formConfig: {
            //表单配置
            model: {
                typeSelect: 'select',
                filesList: [],
                selectValue: '',
            },
            rules: {
                //	表单验证规则。类型：FormRules
                selectValue: [
                    {
                        required: true,
                        validator: validatePass,
                    },
                ],
                filesList: [{ required: true, message: '请上传文件', trigger: 'blur' }],
            },
            itemList: [
                {
                    type: 'slot',
                    prop: 'selectValue',
                    label: '类型选择',
                    props: {
                        slotName: 'radioSelect',
                    },
                },
                {
                    type: 'slot',
                    label: '文件列表',
                    prop: 'filesList',
                    props: {
                        slotName: 'fileList',
                    },
                },
                {
                    type: 'slot',
                    props: {
                        slotName: 'slotSubmit',
                    },
                },
            ],
        },
        y9FromRef: '',
        formData: {
            inputValue: '',
            selectValue: '',
        },
        typeSelectList: [],
    });

    let { formConfig, loading, y9FromRef, formData, typeSelectList } = toRefs(state);

    onMounted(initArticleList);

    // 获取文章类型列表
    async function initArticleList() {
        let result = await getArticleTypeList();
        let resultList = Object.keys(result.data);
        typeSelectList.value = resultList?.map((item) => {
            return { label: item, value: item };
        });
    }

    // 点击选择框或输入框，radio值改变
    function handleClickRadio(value: string) {
        // 值改变
        formConfig.value.model.typeSelect = value;
    }

    function handleChange(value) {
        if (formConfig.value.model.typeSelect == 'select') {
            formConfig.value.model.selectValue = value;
        }
    }

    function handleBlur() {
        if (formConfig.value.model.typeSelect == 'add') {
            formConfig.value.model.selectValue = formData.value.inputValue;
        }
    }

    // 确认
    function handleClickSubmit() {
        y9FromRef.value.elFormRef.validate(async (valid) => {
            if (valid) {
                console.log('通过');
                loading.value = true;
                let fileList = formConfig.value.model.filesList.map((item) => item.raw);
                let typeVal =
                    formConfig.value.model.typeSelect == 'select'
                        ? formData.value.selectValue
                        : formData.value.inputValue;
                // console.log(fileList, 'list-------', typeVal, 'type-------');
                // 请求接口
                await articleUpload(fileList, typeVal)
                    .then((result) => {
                        ElNotification({
                            title: result.code == 0 ? '成功' : '失败',
                            message: result.code == 0 ? '导入成功' : '导入失败',
                            type: result.code == 0 ? 'success' : 'error',
                            duration: 2000,
                            offset: 80,
                        });
                        if (result.code == 0) {
                            emits('add-newArticle', typeVal);
                            emits('update-show');
                        }
                    })
                    .catch((err) => {});
                loading.value = false;
            } else {
                console.log('不通过');
            }
        });
    }

    // 取消
    function handleClickCancel() {
        emits('update-show');
    }
</script>

<style scoped lang="scss">
    .group-radio {
        display: flex;
        flex-direction: column;
        width: 100%;
        justify-content: center;
        align-items: flex-start;
        :deep(.el-radio.el-radio--large .el-radio__label) {
            width: 100%;
        }
        :deep(.el-radio.el-radio--large) {
            width: 100%;
        }
    }
    .ri-add-circle-line {
        position: absolute;
        top: 48px;
        left: -43px;
        font-size: 25px;
        transform: translate(-50%, -50%);
        color: var(--el-color-primary);
        cursor: pointer;
    }
    .btn-style {
        display: flex;
        justify-content: center;
        align-items: center;
        .submit-btn {
            margin-right: 20px;
        }
        > button {
            letter-spacing: 1px;
        }
    }
    .select-radio {
        display: flex;
        width: 100%;
        :deep(.el-radio) {
            width: 14px !important;
            margin-right: 8px;
        }
    }
    :deep(.upload-demo) {
        border: 2px solid var(--el-color-primary);
        border-radius: 4px;
        min-height: 100px;
        .el-upload-list {
            margin-top: -27px;
            .el-upload-list__item {
                padding: 0 10px 0 5px;
                .el-icon--close {
                    right: 15px;
                }
            }
            .el-upload-list__item-name {
                color: var(--el-color-primary);
            }
        }
    }
</style>
