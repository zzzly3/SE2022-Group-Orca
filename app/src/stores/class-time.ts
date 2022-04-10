import { defineStore } from 'pinia';
import {post} from 'boot/axios';
import {Notify} from 'quasar';
//
// const demo_list = [
//   {id: 1, begin: '08:00', end: '08:45'},
//   {id: 6, begin: '13:30', end: '14:15'}
// ]

// export interface ClassTimeInfo{
//   id: number,
//   begin: string,
//   end: string,
// }

export const useClassTimeStore = defineStore('classTime', {
  state: () => ({
  }),
  actions: {
    async modify_classTime({id, begin, end}: {id:number, begin:string, end:string}){
      console.log('in modify_classTime')
      if(await post('modify_classTime', {id, begin, end}) !== false){
        Notify.create({type:'positive', message:'添加/修改成功'})
        return true
      }
      return false
    },
  },
});
