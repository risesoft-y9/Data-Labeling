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
        url: 'https://mock.apifox.cn/m2/2565050-0-default/106192312',
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
        url: 'https://mock.apifox.cn/m2/2565050-0-default/107045200',
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
        url: 'https://mock.apifox.cn/m2/2565050-0-default/107022973?apifoxApiId=107022973',
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
        url: 'https://mock.apifox.cn/m2/2565050-0-default/106568693',
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
        url: 'https://mock.apifox.cn/m2/2565050-0-default/106672158',
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
export const articleUpload = async (file, type) => {
    var formData = new FormData();
    formData.append('file', file);
    formData.append('type', type);
    return await platformRequest({
        url: 'https://mock.apifox.cn/m2/2565050-0-default/106143874',
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
        url: 'https://mock.apifox.cn/m2/2565050-0-default/106782450',
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
        url: 'https://mock.apifox.cn/m2/2565050-0-default/179419095',
        method: 'get',
        cType: false,
    });
};
