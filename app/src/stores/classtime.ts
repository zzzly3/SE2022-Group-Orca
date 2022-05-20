import { defineStore } from 'pinia';
import {post, get} from 'boot/axios';
import {Notify} from 'quasar';
//
// const demo_list = [
//   {id: 1, begin: '08:00', end: '08:45'},
//   {id: 6, begin: '13:30', end: '14:15'}
// ]

export interface ClassTimeInfo{
  id: number,
  begin: string,
  end: string,
}

export const useClassTimeStore = defineStore('classTime', {
  state: () => ({
  }),
  actions: {
    async delete_classTime(id: number){
      console.log('in delete_classTime')
      if(await post('delete_classTime', {id}) !== false){
        Notify.create({type:'positive', message:'删除成功'})
        return true
      }
      return false;
    },
    async modify_classTime({id, begin, end}: {id:number, begin:string, end:string}){
      console.log('in modify_classTime')
      if(await post('modify_classTime', {id, begin, end}) !== false){
        Notify.create({type:'positive', message:'添加/修改成功'})
        return true
      }
      return false
    },
    async load_all_classTime() {
      console.log('in load_classTime')
      const r = await get('load_all_classTime',  true)
      if(r === false){
        Notify.create({type:'negative', message:'信息读取失败'})
      }
      return r
    },
    async select_classTime(id: number){
      console.log('in select_classTime')
      const r = await post('select_classTime', {id}, true)
      if(r === null)return false;
      return r
    }

  },
});

