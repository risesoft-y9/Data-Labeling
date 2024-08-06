/*
 * @Author: chensiwen cikl777@163.com
 * @Date: 2024-07-15 15:52:39
 * @LastEditors: chensiwen cikl777@163.com
 * @LastEditTime: 2024-07-15 16:06:02
 * @FilePath: \y9vue-tokenizer\src\views\home\comps\data.ts
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import { ref } from 'vue';
interface topTitleType {
    name?: string;
    num?: number;
}
// 类型数组
export let topTitleData = ref<topTitleType>({
    name: '--',
    num: 0,
});
// 当前文章的序号
export const currNo = ref('');
