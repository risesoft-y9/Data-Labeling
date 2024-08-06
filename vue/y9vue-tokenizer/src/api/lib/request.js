/*
 * @Author: your name
 * @Date: 2021-04-15 10:16:53
 * @LastEditTime: 2022-08-02 11:11:33
 * @LastEditors: error: git config user.name && git config user.email & please set dead value or install git
 * @Description: In User Settings Edit
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-kernel-dcat-style\src\api\lib\request.js
 */
import settings from '@/settings';
import y9_storage from '@/utils/storage';
import axios from 'axios'; // 考虑CDN
import { ElMessage } from 'element-plus';
import i18n from "@/language/index"
const { t } = i18n.global;
// import { useI18n } from "vue-i18n"
// const { t } = useI18n();

// 创建一个axios实例
function y9Request(baseUrl = '') {
    let requestList = new Set();

    const service = axios.create({
        baseURL: import.meta.env.VUE_APP_CONTEXT,
        withCredentials: true,
        timeout: 0,
    });
    // 请求拦截器(发送请求的时候，携带一些信息)
    service.interceptors.request.use(
        (config) => {
            // config.cancelToken = new axios.CancelToken(e => {
            //     console.log(config.url);
            //     // 阻止重复请求
            //     requestList.has(config.url) ? e(`${location.host}${config.url}---重复请求被中断`) : requestList.add(config.url)
            // })
            config.headers['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';
            // 自定义
            if (config.cType) {
                config.headers['userLoginName'] = config.data.userLoginName;
            }
            const access_token = y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
            if (access_token) {
                // console.log("access_token = ",access_token);
                config.headers['Authorization'] = 'Bearer ' + access_token;
            }

            return config;
        },
        (error) => {
            // 处理请求错误
            console.log(error); // for debug
            return Promise.reject(error);
        }
    );

    // 响应拦截器(接收到数据的时候，进行数据过滤、对状态码判断，进行对应的操作)
    service.interceptors.response.use(
        (response) => {
            // 相同请求不得在600毫秒内重复发送，反之继续执行
            setTimeout(() => {
                requestList.delete(response.config.url);
            }, 600);
            let res;
            if (response.data) {
                res = response.data;
            } else {
                res = response;
            }
            const { code } = res;
            if (code !== 0) {
                // 获取替换后的字符串
                const reqUrl = response.config.url.split('?')[0].replace(response.config.baseURL, '');
                const noVerifyBool = settings.ajaxResponseNoVerifyUrl.includes(reqUrl);
                switch (code) {
                    case 40101:
                    case 40101:
                    case 40102:
                    case 40102:
                    case 401: // 未登陆
                        if (!noVerifyBool) {
                            ElMessageBox({
                                title: t('提示'),
                                showClose: false,
                                closeOnClickModal: false,
                                closeOnPressEscape: false,
                                message: t('当前用户登入信息已失效，请重新登入再操作'),
                                beforeClose: (action, instance, done) => {
                                    if (isExternal(settings.serverLoginUrl)) {
                                        window.location.href = settings.serverLoginUrl;
                                    } else {
                                        window.location.reload();
                                    }
                                    console.log(action, instance, done);
                                },
                            });
                        }

                        break;
                    case 40300:
                        window.location.href = import.meta.env.VUE_APP_PUBLIC_PATH + '/401';
                        break;
                    case 40400:
                        window.location.href = import.meta.env.VUE_APP_PUBLIC_PATH + '/404';
                        break;
                    case 50000:
                        return res;
                    default:
                        if (!noVerifyBool) {
                            console.error(res.msg);
                            // ElMessage({
                            //     message: res.msg || 'Errors',
                            //     type: 'error',
                            //     duration: 1500,
                            // });
                        }
                        break;
                }

                // 返回错误 走 catch
                return Promise.reject(res);
            } else {
                return res;
            }
        },
        (error) => {
            // 异常情况
            if (axios.isCancel(error)) {
                // 请求取消
                // console.warn(error)
                // console.table([error.message.split('---')[0]], 'cancel')
            } else {
                ElMessage({
                    message: error.message,
                    type: 'error',
                    duration: 5 * 1000,
                });
                // 请求失败
                requestList.delete(error.config.url);
            }

            return Promise.reject(error);
        }
    );

    return service;
}

export default y9Request;
