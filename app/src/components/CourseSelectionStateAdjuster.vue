<template>
  <q-dialog v-model="show" persistent transition-show="scale" transition-hide="scale">
    <q-card style="width: 400px">
      <q-card-section>
        <div class="text-subtitle1 row items-center">
          <q-icon name="info" size="sm" color="orange"></q-icon>
          <span>开/关选课</span>
        </div>
      </q-card-section>
      <q-card-section class="q-py-none">
        <q-form style="width: 300px" class="q-px-md q-gutter-y-xs">
          <q-select v-model="state" :options="states" label="是否开放选课" dense ref="stateRef"
                    lazy-rules :rules="[val => !!val || '无效的类型']"
          />
        </q-form>
        <q-inner-loading :showing="tableLoading"/>
      </q-card-section>

      <q-card-actions align="right" class="q-pa-md">
        <q-btn flat label="取消" @click="clear" :disable="loading" v-close-popup color="negative" />
        <q-btn flat label="确定" @click="submit" :loading="loading" color="primary" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>


<script>
import {ref} from 'vue';
import {useCourseSelectStateStore} from 'stores/course-selection-state';
import {Notify} from 'quasar';

const states = [
  {label: '关闭选课', value: 0},
  {label: '一轮选课', value: 1},
  {label: '二轮选课', value: 2}
]

export default {
  name: 'CourseSelectionStateAdjuster',
  setup(){
    const CSS = useCourseSelectStateStore()
    const show = ref(false)
    const tableLoading = ref(false)
    const loading = ref(false)
    const state = ref({label: '', value: ''})

    const stateRef = ref(null)

    const clear = ()=>{
      show.value = false
    }
    const load = async ()=>{
      console.log('CourseSelectionStateAdjuster: in load')
      tableLoading.value = true
      show.value = true
      const r = await CSS.load_course_selection_state()
      if(r !== false){
        state.value = states[Number(r)]
      }
      tableLoading.value = false
    }

    return{
      show, loading, tableLoading,
      state, states, stateRef,
      clear, load,
      async submit(){
        console.log('CourseSelectionStateAdjuster: submit')
        if(!stateRef.value)
          return
        if(state.value === {label:'', value:''}){
          Notify.create({type:'negative', message:'信息不能为空'})
          return
        }
        loading.value = true

        if(await CSS.modify_course_selection_state({open:state.value.value})){
          clear()
        }
        loading.value = false
      }
    };
  },
}
</script>


