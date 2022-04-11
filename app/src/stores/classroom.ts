import { defineStore } from 'pinia';
import {post} from 'boot/axios';
import {Notify} from 'quasar';
//
// const demo_list = [
//   {id: 'H2101', building: 'H2', open: true}
// ]

export interface ClassroomInfo{
  id: string,
  building: string,
  open: boolean,
}

export const useClassroomStore = defineStore('classroom', {
  state: () => ({

  }),
  actions: {
    async modify_classroom({name, building, open}: {name: string, building: string, open:boolean}){
      console.log('frontend: in modify_classroom')
      if(await post('modify_classroom', {name, building, open}) !== false){
        Notify.create({type:'positive', message:'添加/修改成功'})
        return true
      }
      return false
    },
    async load_open_classroom() {
      console.log('in load_open_classroom')
      const r = await post('load_open_classroom', {}, true)
      if(r === false){
        Notify.create({type:'negative', message:'信息读取失败'})
      }
      return r
    },
    async load_all_classroom() {
      console.log('in load_all_classroom')
      const r = await post('load_all_classroom', {}, true)
      if(r === false){
        Notify.create({type:'negative', message:'信息读取失败'})
      }
      return r
    }
  }
  ,
});
