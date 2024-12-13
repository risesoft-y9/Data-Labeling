<p align="center">
 <img alt="logo" src="https://vue.youshengyun.com/files/img/qrCodeLogo.png">
<p align="center">
 <a href='https://gitee.com/risesoft-y9/y9-label/stargazers'><img src='https://gitee.com/risesoft-y9/y9-label/badge/star.svg?theme=dark' alt='star'></img></a>
    <img src="https://img.shields.io/badge/version-v9.6.6-yellow.svg">
    <img src="https://img.shields.io/badge/Spring%20Boot-2.7-blue.svg">
    <img alt="logo" src="https://img.shields.io/badge/Vue-3.3-red.svg">
    <img alt="" src="https://img.shields.io/badge/JDK-11-green.svg">
    <a href="https://gitee.com/risesoft-y9/y9-core/blob/master/LICENSE">
    <img src="https://img.shields.io/badge/license-GPL3-blue.svg"></a>
</p>

## 简介

数据标注是一款专门对文本数据进行处理和标注的工具，通过简化快捷的文本标注流程和动态的算法反馈，支持用户快速标注关键词并能通过算法持续减少人工标注的成本和时间。数据标注的过程先由人工标注构筑基础，再由自动标注反哺人工标注，最后由人工标注进行纠偏，从而大幅度提高标注的精准度和高效性。数据标注是一个完全开源的项目，无商业版，但是需要依赖开源的数字底座进行人员岗位管控。数据标注的各类词库结果会定期在本平台中公开。

## 源码目录

```
vue -- 前端工程
 ├── y9vue-tokenizer -- 数据标注前端工程
webapp -- 系统模块
 ├── risenet-y9boot-webapp-tokenizer -- 数据标注后端工程
```

## 数据标注功能架构图
<div><img src="https://vue.youshengyun.com/files/img/开源网站图片上传/数据标注功能架构图.png"><div/>

## 数据标注应用架构图
<div><img src="https://vue.youshengyun.com/files/img/开源网站图片上传/数据标注应用架构图.png"><div/>

## 产品特点

### 高效标注

针对文章和文本，数据标注利用各种标识、快捷键、组合键、分类方法来提高标注的效率并减少标注错误率。

### 全局算法校准

在多组同时标注的情况下，数据标注会汇聚全部的标注结果，从而减少重复标注的工作量。通过全局校准的方法，标注人员可以看到全局当前对于文章的自动标注结果，从而只需要对结果纠偏即可。

### 行业词库

针对数据标注的结果，本项目会定期释放各类、各行业以txt形式存储的经过人工纠偏的标注结果，从而帮助AI和分词器更精准地识别中文词汇里面不断的新生词汇和组合词汇。

## 功能描述

| 序&nbsp;号       | 特&nbsp;点&nbsp;&nbsp;名&nbsp;称        | 特点描述                       |
|--------|------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1      | 添加文章                |通过本地导入或者批量导入的方式添加文章，同时可以根据文章类型进行必要的分类。|
| 2      | 文章筛选                |根据文章类型和顺序编号进行筛选。|
| 3      | 导出分词                |支持将全部已经完成的分词和标注进行一次性的导出。本功能以压txt的形式导出文件。|
| 4      | 标注结果                |通过红色方框显示最终的人工标注结果。此结果为全局的人工标注的综合结果，而非单人单文章的标注结果。|
| 5      | 标注操作                |支持以快捷键、右键、组合键等方式确认标注，同时支持在页面或者快捷键的方式删除已经标注的词汇。|
| 6      | 标注状态                |包含“全部标注”、“标注中”、“未标注”、“已标注”共计四种状态，可以根据状态、序号、文章分类进行筛选。|
| 7      | 标注验证                |利用分词器和已具备的全局综合分词能力进行一次标注验证，检查是否有遗漏的标注语料，也可以检验全局综合分词能力是否有偏颇。标注验证以下划线和波浪线的方式区别邻近的名词。|
| 8      | 标注日志                |在单个文章中查看已经完成的标注操作日志。|


## 数据资产

### 1. 行业领域专有词汇
<div><img src="https://vue.youshengyun.com/files/行业领域专有词汇.png"><div/>

### 2. 政务公开文章词汇
<div><img src="https://vue.youshengyun.com/files/政务公开文章词汇.png"><div/>

### 3. 司法案件观点条例
<div><img src="https://vue.youshengyun.com/files/司法案件观点条例.png"><div/>

### 4. 数据库分类题目库
<div><img src="https://vue.youshengyun.com/files/数据库分类题目库.png"><div/>

### 5. 算法库（来自“山东国家应用数学中心”）
<div><img src="https://vue.youshengyun.com/files/算法库.jpg"><div/>

## 后端技术选型

| 序号 | 依赖              | 版本      | 官网                                                                                                                 |
|----|-----------------|---------|--------------------------------------------------------------------------------------------------------------------|
| 1  | Spring Boot     | 2.7.10  | <a href="https://spring.io/projects/spring-boot" target="_blank">官网</a>                                            |
| 2  | SpringDataJPA   | 2.7.10  | <a href="https://spring.io/projects/spring-data-jpa" target="_blank">官网</a>                                        |
| 3  | nacos           | 2.2.1   | <a href="https://nacos.io/zh-cn/docs/v2/quickstart/quick-start.html" target="_blank">官网</a>                        |
| 4  | druid           | 1.2.16  | <a href="https://github.com/alibaba/druid/wiki/%E9%A6%96%E9%A1%B5" target="_blank">官网</a>                          |
| 5  | Jackson         | 2.13.5  | <a href="https://github.com/FasterXML/jackson-core" target="_blank">官网</a>                                         |
| 6  | javers          | 6.13.0  | <a href="https://github.com/javers/javers" target="_blank">官网</a>                                                  |
| 7  | lombok          | 1.18.26 | <a href="https://projectlombok.org/" target="_blank">官网</a>                                                        |
| 8  | logback         | 1.2.11  | <a href="https://www.docs4dev.com/docs/zh/logback/1.3.0-alpha4/reference/introduction.html" target="_blank">官网</a> |

## 前端技术选型

| 序号 | 依赖           | 版本      | 官网                                                                     |
|----|--------------|---------|------------------------------------------------------------------------|
| 1  | vue          | 3.3.2   | <a href="https://cn.vuejs.org/" target="_blank">官网</a>                 |
| 2  | vite2        | 2.9.13  | <a href="https://vitejs.cn/" target="_blank">官网</a>                    |
| 3  | vue-router   | 4.0.13  | <a href="https://router.vuejs.org/zh/" target="_blank">官网</a>          |
| 4  | pinia        | 2.0.11  | <a href="https://pinia.vuejs.org/zh/" target="_blank">官网</a>           |
| 5  | axios        | 0.24.0  | <a href="https://www.axios-http.cn/" target="_blank">官网</a>            |
| 6  | typescript   | 4.5.4   | <a href="https://www.typescriptlang.org/" target="_blank">官网</a>       |
| 7  | core-js      | 3.20.1  | <a href="https://www.npmjs.com/package/core-js" target="_blank">官网</a> |
| 8  | element-plus | 2.2.29  | <a href="https://element-plus.org/zh-CN/" target="_blank">官网</a>       |
| 9  | sass         | 1.58.0  | <a href="https://www.sass.hk/" target="_blank">官网</a>                  |
| 10 | animate.css  | 4.1.1   | <a href="https://animate.style/" target="_blank">官网</a>                |
| 11 | vxe-table    | 4.3.5   | <a href="https://vxetable.cn" target="_blank">官网</a>                   |
| 12 | echarts      | 5.3.2   | <a href="https://echarts.apache.org/zh/" target="_blank">官网</a>        |
| 13 | svgo         | 1.3.2   | <a href="https://github.com/svg/svgo" target="_blank">官网</a>           |
| 14 | lodash       | 4.17.21 | <a href="https://lodash.com/" target="_blank">官网</a>                   |

## 中间件选型

| 序号 | 工具               | 版本   | 官网                                                                        |
|----|------------------|------|---------------------------------------------------------------------------|
| 1  | JDK              | 11   | <a href="https://openjdk.org/" target="_blank">官网</a>                     |
| 2  | Tomcat           | 9.0+ | <a href="https://tomcat.apache.org/" target="_blank">官网</a>               |

## 数据库选型

| 序号 | 工具            | 版本         | 官网                                                                        |
|----|---------------|------------|---------------------------------------------------------------------------|
| 1  | elasticsearch | 7.9+       | <a href="https://www.elastic.co/cn/elasticsearch/" target="_blank">官网</a> |


## 信创兼容适配

| **序号** | 类型   | 对象                 |
|:-------|------|--------------------|
| 1      | 浏览器  | 奇安信、火狐、谷歌、360等     |
| 2      | 插件   | 金山、永中、数科、福昕等       |
| 3      | 中间件  | 东方通、金蝶、宝兰德等        |
| 4      | 数据库  | 人大金仓、达梦、高斯等        |
| 5      | 操作系统 | 统信、麒麟、中科方德等        |
| 6      | 芯片   | ARM体系、MIPS体系、X86体系 |

## 文档专区

开发文档：https://docs.youshengyun.com/

| 序号 | 名称                                                                                                               |
|:---|------------------------------------------------------------------------------------------------------------------|
| 1  | <a href="https://vue.youshengyun.com/files/内部Java开发规范手册.pdf" target="_blank">内部Java开发规范手册</a>                    |
| 2  | <a href="https://vue.youshengyun.com/files/日志组件使用文档.pdf" target="_blank">日志组件使用文档</a>                            |
| 3  | <a href="https://vue.youshengyun.com/files/文件组件使用文档.pdf" target="_blank">文件组件使用文档</a>                            |
| 4  | <a href="https://vue.youshengyun.com/files/代码生成器使用文档.pdf" target="_blank">代码生成器使用文档</a>                          |
| 5  | <a href="https://vue.youshengyun.com/files/配置文件说明文档.pdf" target="_blank">配置文件说明文档</a>                            |
| 6  | <a href="https://vue.youshengyun.com/files/常用工具类使用示例文档.pdf" target="_blank">常用工具类使用示例文档</a>                      |
| 7  | <a href="https://vue.youshengyun.com/files/有生博大Vue开发手册v1.0.pdf" target="_blank">前端开发手册</a>                       |
| 8  | <a href="https://vue.youshengyun.com/files/开发规范.pdf" target="_blank">前端开发规范</a>                                  |
| 9  | <a href="https://vue.youshengyun.com/files/代码格式化.pdf" target="_blank">前端代码格式化</a>                                |
| 10 | <a href="https://vue.youshengyun.com/files/系统组件.pdf" target="_blank">前端系统组件</a>                                  |
| 11 | <a href="https://vue.youshengyun.com/files/通用方法.pdf" target="_blank">前端通用方法</a>                                  |
| 12 | <a href="https://vue.youshengyun.com/files/国际化.pdf" target="_blank">前端国际化</a>                                    |
| 13 | <a href="https://vue.youshengyun.com/files/Icon图标.pdf" target="_blank">前端Icon图标</a>                              |
| 14 | <a href="https://vue.youshengyun.com/files/单点登录对接文档.pdf" target="_blank">单点登录对接文档</a>                            |
| 15 | <a href="https://vue.youshengyun.com/files/分词器安装部署文档.pdf" target="_blank">分词器安装部署文档</a>                                               |
| 16 | <a href="https://vue.youshengyun.com/files/分词器操作手册.pdf" target="_blank">分词器操作手册</a>                                               |

## 数据标注截图

<div><img src="https://vue.youshengyun.com/files/img/shujubiaozhu.jpg"><div/>
<div><img src="https://vue.youshengyun.com/files/img/biaozhuyanzheng.jpg"><div/>

## 依赖开源项目

| 序&nbsp;号 | 项&nbsp;目&nbsp;&nbsp;名&nbsp;称          | 项目介绍           | 地&nbsp;址                                                                                                                                                          |
| ----- | ----------- | ----------------------------------------- |-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1    | 数字底座 | 数字底座是一款面向大型政府、企业数字化转型，基于身份认证、组织架构、岗位职务、应用系统、资源角色等功能构建的统一且安全的管理支撑平台。数字底座基于三员管理模式，具备微服务、多租户、容器化和国产化，支持用户利用代码生成器快速构建自己的业务应用，同时可关联诸多成熟且好用的内部生态应用      | <a href="https://gitee.com/risesoft-y9/y9-core" target="_blank">码云</a> <a href="https://github.com/risesoft-y9/Digital-Infrastructure" target="_blank">GitHub</a> |

## 赞助与支持

### 中关村软件和信息服务产业创新联盟

官网：<a href="https://www.zgcsa.net" target="_blank">https://www.zgcsa.net</a>

### 北京有生博大软件股份有限公司

官网：<a href="https://www.risesoft.net/" target="_blank">https://www.risesoft.net/</a>

### 统一标识代码注册管理中心

官网：<a href="https://www.idcode.org.cn/" target="_blank">https://www.idcode.org.cn/</a>


>
数字底座已经全面接入统一标识码（MA码），具体使用说明请查看：<a href="https://gitee.com/risesoft-y9/y9-core/tree/main/y9-digitalbase-idcode" target="_blank">https://gitee.com/risesoft-y9/y9-core/tree/main/y9-digitalbase-idcode</a>
>

### 中国城市发展研究会

官网：<a href="https://www.china-cfh.com/" target="_blank">https://www.china-cfh.com/</a>

### 济南亚跃信息技术有限公司

官网：<a href="https://www.yayueyun.com/yayueOwe" target="_blank">https://www.yayueyun.com/yayueOwe</a>

### 北京京畿法律咨询有限公司

联系人：邱先生

邮箱：bjbj7@qq.com

### 山东国家应用数学中心

官网：<a href="http://www.sdam.sdu.edu.cn/" target="_blank">http://www.sdam.sdu.edu.cn/</a>

## 咨询与合作

联系人：曲经理

微信号：qq349416828

备注：开源咨询-姓名
<div><img style="width: 40%" src="https://vue.youshengyun.com/files/img/开源网站图片上传/曲经理-微信二维码.png"><div/>
联系人：有生博大-咨询热线

座机号：010-86393151
<div><img style="width: 45%" src="https://vue.youshengyun.com/files/img/有生博大-咨询热线.png"><div/>
