import { defineStore } from 'pinia';
import {post} from 'boot/axios';
import {Notify} from 'quasar';

export const useCourseSelectStateStore = defineStore('courseSelectionState', {
  state: () => ({

  }),
  actions: {
    async modify_course_selection_state({open}:{open:boolean}){
      console.log('frontend: in modify_course_selection_state')
      if(await post('modify_course_selection_state', {year:'2022', semester:1, open}) !== false){
        Notify.create({type:'positive', message:'修改成功'})
        return true
      }
      return false
    },
    async load_course_selection_state(){
      console.log('frontend: in load_course_selection_state')
      const r = await post('load_course_selection_state', {}, true)
      if(r === false){
        Notify.create({type:'negative', message:'信息读取失败'})
      }
      return r
    }
  }
});
