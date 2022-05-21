import { defineStore } from 'pinia';
import {post, get} from 'boot/axios';
import {Notify} from 'quasar';

export const useCourseSelectStateStore = defineStore('courseSelectionState', {
  state: () => ({

  }),
  actions: {
    async modify_course_selection_state({open}:{open:boolean}){
      console.log('frontend: in modify_course_selection_state', 'open is', open)
      if(await post('modify_course_selection_state', {value:open}) !== false){
        Notify.create({type:'positive', message:'修改成功'})
        return true
      }
      return false
    },
    async load_course_selection_state(){
      const r = await get('load_course_selection_state', true)
      console.log('frontend: in load_course_selection_state', 'res is ', r)
      if(r === false){
        Notify.create({type:'negative', message:'信息读取失败'})
        return r
      }
      return r
    }
  }
});
