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

数据标注是构建智能应用过程中一项基础且关键的工作，数据标注提供专为处理文本数据设计的标注工具，简化文本数据标注流程，支持用户快速标注关键词并覆盖标注成果。系统可对输入文章进行自动分词，通过人工标注关键词纠正分词结果，逐步训练分词功能并获得大量关键数据，提升文本数据处理准确性，为构建智能应用奠定基础。

## 源码目录

```
vue -- 前端工程
 ├── y9vue-tokenizer -- 数据标注前端工程
webapp -- 系统模块
 ├── risenet-y9boot-webapp-tokenizer -- 数据标注后端工程
```

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

## 数据标注截图

<div><img src="https://vue.youshengyun.com/files/img/shujubiaozhu.jpg"><div/>
<div><img src="https://vue.youshengyun.com/files/img/biaozhuyanzheng.jpg"><div/>

## 同构开源项目

| 序&nbsp;号 | 项&nbsp;目&nbsp;名&nbsp;称&nbsp; | 项目介绍                                                                                                                               | 地&nbsp;址                                          |
|:---------|------------------------------|------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------|
| 1        | 数字底座                        | 数字底座是一款面向大型政府、企业数字化转型，基于身份认证、组织架构、岗位职务、应用系统、资源角色等功能构建的统一且安全的管理支撑平台。数字底座基于三员管理模式，具备微服务、多租户、容器化和国产化，支持用户利用代码生成器快速构建自己的业务应用，同时可关联诸多成熟且好用的内部生态应用。 | [码云地址](https://gitee.com/risesoft-y9/y9-core) |
| 2        | 工作流引擎                        | 工作流引擎对内提供单位/机关流程管理规则和内部业务流程的数字化落地实践；对外提供自动化地第三方业务驱动、接口接入和算法单元驱动能力；工作流引擎在提供底层驱动引擎的同时对全局透明监控、安全防御和国产化特色功能进行充分考虑，是内部流程管理和业务算法驱动的不二之选。 | [码云地址](https://gitee.com/risesoft-y9/y9-flowable) |
| 3        | 数据流引擎                        | 数据流引擎是一款面向数据集成、数据同步、数据交换、数据共享、任务配置、任务调度的底层数据驱动引擎。数据流引擎采用管执分离、多流层、插件库等体系应对大规模数据任务、数据高频上报、数据高频采集、异构数据兼容的实际数据问题。                      | [码云地址](https://gitee.com/risesoft-y9/y9-dataflow) |

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

## 咨询与合作

联系人：曲经理

微信号：qq349416828

备注：开源数据标注咨询-姓名
<div><img style="width: 40%" src="https://vue.youshengyun.com/files/img/曲经理-二维码.png"><div/>
联系人：有生博大-咨询热线

座机号：010-86393151
<div><img style="width: 45%" src="https://vue.youshengyun.com/files/img/有生博大-咨询热线.png"><div/>
