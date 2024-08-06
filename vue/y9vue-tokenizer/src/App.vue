<!--
 * @Author: your name
 * @Date: 2022-01-10 18:09:52
 * @LastEditTime: 2023-02-24 14:59:28
 * @LastEditors: mengjuhua
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: /sz- team-frontend-9.6.x/y9vue-home/src/App.vue
-->
<script lang="ts" setup>
	// import { useI18n } from 'vue-i18n';
	import { onMounted, onUnmounted, ref, provide, watch, computed } from 'vue'
	// 引入字体调整的方法
	import { getConcreteSize } from '@/utils/index';
	// 引入水印插件
	// import watermark from 'y9-watermark/lib/index';
	
	import { useSettingStore } from "@/store/modules/settingStore";
	const settingStore = useSettingStore();
	// const { t } = useI18n();
	
	// 定义⽔印⽂字变量
	// let watermarkValue = computed(() => t("重要信息请勿泄漏") );
	//监听语⾔变化，传⼊对应的⽔印语句
	// watch(
	// 	() =>useSettingStore().getWebLanguage,
	// 		(newLang) => {
	// 			setTimeout(() =>{
	// 				watermark(watermarkValue, sizeObjInfo.value.baseFontSize);
	// 			})  
	// 		},
	// 		{
	// 			immediate: true
	// 		}
	// )
	//监听⼤⼩变化，传⼊对应⽔印⽂字⼤⼩
	// watch(
	// 	() =>useSettingStore().getFontSize,
	// 	(newLang) => {
	// 		setTimeout(() =>{
	// 				watermark(watermarkValue, sizeObjInfo.value.baseFontSize);
	// 			})  
	// 		},
	// 		{
	// 			immediate: true
	// 		}
	// )

	// onMounted(() => {
	// 	// 执⾏⽔印⽅法
	// 	watermark(watermarkValue, sizeObjInfo.value.baseFontSize);
	// });
	// onUnmounted(() => {
	// 	watermark('');
	// })


	/***
	 *  字体大中小
	 * 定义变量
	 */

	 let sizeObjInfo = ref({
		baseFontSize: getConcreteSize(settingStore.getFontSize, 14) + 'px',
		mediumFontSize: getConcreteSize(settingStore.getFontSize, 16) + 'px',
		largeFontSize: getConcreteSize(settingStore.getFontSize, 18) + 'px',
		largerFontSize: getConcreteSize(settingStore.getFontSize, 19) + 'px',
		extraLargeFont: getConcreteSize(settingStore.getFontSize, 20) + 'px', 
		extraLargerFont: getConcreteSize(settingStore.getFontSize, 24) + 'px',
		moreLargeFont: getConcreteSize(settingStore.getFontSize, 26) + 'px',
		moreLargerFont: getConcreteSize(settingStore.getFontSize, 32) + 'px',
		biggerFontSize: getConcreteSize(settingStore.getFontSize, 40) + 'px',
		maximumFontSize: getConcreteSize(settingStore.getFontSize, 48) + 'px',
		buttonSize: settingStore.getFontSize,
		lineHeight: settingStore.getLineHeight

	 })
	// 监听 转换font-size值
	watch(() => settingStore.getFontSize, (newVal) => {
		sizeObjInfo.value.baseFontSize = getConcreteSize(newVal, 14) + 'px';
		sizeObjInfo.value.mediumFontSize = getConcreteSize(newVal, 16) + 'px';
		sizeObjInfo.value.largeFontSize = getConcreteSize(newVal, 18) + 'px';
		sizeObjInfo.value.largerFontSize = getConcreteSize(newVal, 19) + 'px';
		sizeObjInfo.value.extraLargeFont = getConcreteSize(newVal, 20) + 'px';
		sizeObjInfo.value.extraLargerFont = getConcreteSize(newVal, 24) + 'px';
		sizeObjInfo.value.moreLargeFont = getConcreteSize(newVal, 26) + 'px';
		sizeObjInfo.value.moreLargerFont = getConcreteSize(newVal, 32) + 'px';
		sizeObjInfo.value.biggerFontSize = getConcreteSize(newVal, 40) + 'px';
		sizeObjInfo.value.maximumFontSize = getConcreteSize(newVal, 48) + 'px';
		sizeObjInfo.value.buttonSize = newVal;
		sizeObjInfo.value.lineHeight = settingStore.getLineHeight;

	})

	// provide提供
	provide('sizeObjInfo', sizeObjInfo.value);

	
</script>

<template>
	<router-view></router-view>
</template>


