<template>
  <div>
    <q-dialog v-model="show" persistent transition-show="scale" transition-hide="scale">
      <q-card style="width: 400px">
        <q-card-section>
          <div class="text-subtitle1 row items-center">
            <q-icon name="add" size="sm" color="primary"></q-icon>
            <span>调整教室</span>
          </div>
        </q-card-section>
        <q-card-section class="q-py-none">
          <q-form style="width: 300px" class="q-px-md q-gutter-y-xs">
            <q-input v-model="classroom" label="教室名称" ref="classroomRef" maxlength="8"
                     clearable clear-icon="close" :rules="[val => !!val || '不能为空']"
            />
            <q-input v-model="building" label="教学楼" ref="buildingRef" maxlength="4"
                     clearable clear-icon="close" :rules="[val => !!val || '不能为空']"
            />
<!--            <q-select v-model="building" :options="buildings" label="选择教学楼" dense ref="buildingRef"-->
<!--                      lazy-rules :rules="[val => !!val || '无效的类型']" />-->
            <q-select v-model="state" :options="states" label="选择教室状态" dense ref="stateRef"
                      lazy-rules :rules="[val => !!val || '无效的类型']" />
          </q-form>
        </q-card-section>

        <q-card-actions align="right" class="q-pa-md">
          <q-btn flat label="取消" @click="clear" :disable="loading" v-close-popup color="negative" />
          <q-btn flat label="确定" @click="submit" :loading="loading" color="primary" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </div>
</template>

<script>
import {ref} from 'vue';
import {useClassroomStore} from 'stores/classroom';

const states = [
  {label: '开放', value: 1},
  {label: '关闭', value: 0}
]

export default {
  name: 'ClassroomAdjuster',
  setup(){
    const CR = useClassroomStore()
    const show = ref(false)
    const loading = ref(false)

    const classroom = ref('')
    const building = ref('')
    const state = ref({label: '', value: ''})

    // const buildingRef = ref<QValidate|null>(null)
    // const stateRef = ref<QValidate|null>(null)
    const classroomRef = ref(null)
    const buildingRef = ref(null)
    const stateRef = ref(null)

    const clear = ()=>{
      show.value = false
    }
    return{
      show, loading,
      classroom, classroomRef,
      building, buildingRef,
      state, states, stateRef,
      clear,
      async submit(){
        console.log('ClassroomAdjuster: submit')
        if(!classroomRef.value || !buildingRef.value || !stateRef.value)
          return
        if(classroom.value === '' || building.value === '' || state.value === {label:'', value:''})
          return
        loading.value = true

        if(await CR.modify_classroom({name:classroom.value, building:building.value, open:state.value.value})){
          clear()
        }
        loading.value = false
      }
    };
  },
}
</script>
