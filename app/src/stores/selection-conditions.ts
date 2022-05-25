import { defineStore } from 'pinia';
import { post, get } from 'boot/axios';
import { Notify } from 'quasar';

export const useSelectionConditionsStore = defineStore('selectionConditions', {
  state: ()=>({

  }),
  actions: {
    async search({cid, ctime, cname, classroom, teacher}:
                   {cid:string, ctime:string, cname:string, classroom:string, teacher:string}){
      console.log('in search')
      const r = await post('/selection/search', {cid, ctime, cname, classroom, teacher})
      if(r === false){
        Notify.create({type:'negative', message:'加载失败'})
        return false
      }
      return r
    },
    async loadAllCourses(){
      console.log('in loadAllCourses')
      const r = await get('/selection/load_all_courses')
      if(r === false){
        Notify.create({type:'negative', message:'加载失败'})
        return false
      }
      return r
    },
    async loadCourseSelection(cid: string){
      console.log('in loadCourseSelection')
      const r = await post('/selection/load_course_selection', {cid})
      if(r === false){
        Notify.create({type:'negative', message:'加载失败'})
        return false
      }
      return r
    },
    async addSelection(cid:string){
      console.log('in addSeletion')
      const r = await post('/selection/add_selection', {cid})
      if(r !== false){
        Notify.create({type:'positive', message:'成功'})
        return true
      }
      return false
    },
    async loadSelectedCourses(){
      console.log('in loadSelectedCourses')
      const r = await get('/selection/load_selected_courses')
      if(r === false){
        Notify.create({type:'negative', message:'加载失败'})
        return false
      }
      return r
    },
    async loadAppliedCourses(){
      console.log('in loadAppliedCourses')
      const r = await get('/selection/load_applied_courses')
      if(r === false){
        Notify.create({type:'negative', message:'加载失败'})
        return false
      }
      return r
    },
    async loadTakenCourses() {
      console.log('in loadTakenCourses')
      const r = await get('/selection/load_taken_courses')
      if(r === false){
        Notify.create({type:'negative', message:'加载失败'})
        return false
      }
      return r
    },
    async loadCoursesToTeach(){
      console.log('in loadCoursesToTeach')
      const r = await get('/selection/load_courses_to_teach')
      if(r === false){
        Notify.create({type:'negative', message:'加载失败'})
        return false
      }
      return r
    },
    async deleteSelection(cid:string){
      console.log('in deleteSelection')
      const r = await post('/selection/delete_selection', {cid})
      if(r !== false){
        Notify.create({type:'positive', message:'成功'})
        return true
      }
      return false
    },
    async applySelection({cid, des}: {cid:string, des:string}){
      console.log('in applySeleciton')
      const r = await post('/selection/apply_selection', {cid, des})
      if(r !== false){
        Notify.create({type:'positive', message:'成功'})
        return true
      }
      return false
    },
    async deleteAppliedSelection(cid:string){
      console.log('in deleteSelection')
      const r = await post('/selection/delete_applied_selection', {cid})
      if(r !== false){
        Notify.create({type:'positive', message:'成功'})
        return true
      }
      return false
    },
    async loadAppliedSelection(){
      console.log('in loadAppliedSelection')
      const r = await get('/selection/load_applied_selection')
      if(r === false){
        Notify.create({type:'negative', message:'加载失败'})
        return false
      }
      return r
    },
    async passAppliedSelection({sid, cid}: {sid:string, cid:string}){
      console.log('in passAppliedSelection')
      const r = await post('/selection/pass_applied_selection', {sid, cid})
      if(r !== false){
        Notify.create({type:'positive', message:'成功'})
        return true
      }
      return false
    },
    async rejectAppliedSelection({sid, cid, des}: {sid:string, cid:string, des:string}){
      console.log('in rejectAppliedSelection')
      const r = await post('/selection/reject_applied_selection', {sid, cid, des})
      if(r !== false){
        Notify.create({type:'positive', message:'成功'})
        return true
      }
      return false
    },
    async loadSelectableCourses(){
      console.log('in loadSelectableCourses')
      const r = await get('/selection/load_selectable_courses')
      if(r === false){
        Notify.create({type:'negative', message:'成功'})
        return false
      }
      return r
    },
    async updateCourseCapacity(cid:string, capacity:string){
      console.log('in updateCourseCapacity')
      const r = await post('/course/update_course_capacity', {cid, capacity})
      if(r !== false){
        Notify.create({type:'positive', message:'成功'})
        return true
      }
      return false
    }
  }
})
