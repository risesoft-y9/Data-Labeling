import Request from '@/api/lib/request';
import qs from 'qs';
const platformRequest = Request();

/**
 * 获取多条文章标题
 * @param params
 * @returns
 */
export const getArticleTitle = async (params) => {
    return await platformRequest({
        url: '/rest/articles/title',
        method: 'GET',
        cType: false,
        params,
    });
};

/**
 * 文本分析接口
 * @param params
 * @returns
 */
export const getArticleAnalyse = async (params) => {
    return await platformRequest({
        url: '/rest/Ik/analyse',
        method: 'GET',
        cType: false,
        params,
    });
};

/***
 * 文章具体内容
 * @param params
 * @returns
 */
export const getArticleContent = async (params) => {
    return await platformRequest({
        url: '/rest/articles/artcle',
        method: 'get',
        cType: false,
        params,
    });
};

/**
 * 添加新关键词接口
 * @param params
 * @returns
 */
export const addKeyWords = async (params) => {
    let data = qs.stringify(params);
    return await platformRequest({
        url: '/rest/articles/saveKeyword',
        method: 'POST',
        cType: false,
        data,
    });
};

/**
 * 删除关键词接口
 * @param params
 * @returns
 */
export const DeleteKeyWords = async (params) => {
    let data = qs.stringify(params);
    return await platformRequest({
        url: '/rest/articles/deleteKeyword',
        method: 'POST',
        cType: false,
        data,
    });
};

/**
 * 添加文章
 * @param file
 * @returns
 */
export const articleUpload = async (files, type) => {
    var formData = new FormData();
    //formData.append('files', file);
    for(let i=0;i<files.length;i++){
        formData.append('files',files[i]);
    }
    formData.append('type', type);
    return await platformRequest({
        url: '/rest/articles/upload',
        method: 'POST',
        cType: false,
        data: formData,
    });
};

/**
 * 标注完成按钮
 * @param params
 * @returns
 */
export const recordFinished = async (params) => {
    let data = qs.stringify(params);
    return await platformRequest({
        url: '/rest/articles/label',
        method: 'POST',
        cType: false,
        data,
    });
};

/**
 * 获取文章类型
 * @param params
 * @returns
 */
export const getArticleTypeList = async () => {
    return await platformRequest({
        url: '/rest/articles/artcleType',
        method: 'get',
        cType: false,
    });
};
