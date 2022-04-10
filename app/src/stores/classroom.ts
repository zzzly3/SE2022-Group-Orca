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
    }
  }
  ,
});
