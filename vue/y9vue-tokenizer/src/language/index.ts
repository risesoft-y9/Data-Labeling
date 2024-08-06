

import { createI18n } from 'vue-i18n'
//本地语言包
import zh from './zh.json'
import en from './en.json'

// 获取本地框架配置
const userSettingData = JSON.parse(localStorage.getItem('userSettingData'));

const messages = {
    zh: {
        ...zh,
    },
    en: {
        ...en,
    }
}

const i18n = createI18n({
    locale: userSettingData?.webLanguage || 'zh',
    legacy: false,// 使用 Composition API 模式，则需要将其设置为false
    globalInjection: true,// 全局注入 $t 函数
    messages,
})

export default i18n