<template>
    <q-dialog v-model="show" persistent transition-show="scale" transition-hide="scale">
      <q-card style="width: 400px">
        <q-card-section>
        <q-tabs v-model="tab">
          <q-tab name="add">
            <div class="text-subtitle1 row items-center">
              <q-icon name="add" size="sm" color="primary"></q-icon>
              <span>增加教室</span>
            </div>
          </q-tab>
          <q-tab name="change">
            <div class="text-subtitle1 row items-center">
              <q-icon name="corporate_fare" size="sm" color="green"></q-icon>
              <span>调整教室</span>
            </div>
          </q-tab>

        </q-tabs>
          </q-card-section>
        <q-card-section>
        <q-tab-panels v-model="tab">
          <q-tab-panel name="add">
            <q-form style="width: 300px" class="q-px-md q-gutter-y-xs">
              <q-input v-model="classroom1" label="教室名称" dense ref="classroomRef" maxlength="8"
                       clearable clear-icon="close" :rules="[val => !!val || '不能为空']"
              />
              <q-input v-model="building1" label="教学楼" dense ref="buildingRef" maxlength="4"
                       clearable clear-icon="close" :rules="[val => !!val || '不能为空']"
              />
              <q-select v-model="state1" :options="states" dense label="教室状态"
                        :rules="[val => !!val || '无效的类型']" />
            </q-form>
          </q-tab-panel>
          <q-tab-panel name="change">
            <q-form style="width: 300px" class="q-px-md q-gutter-y-xs">
              <q-select v-model="building0" :options="buildings" label="教学楼" dense
                         :rules="[val => !!val || '不能为空']"
              />
              <q-select v-model="classroom0" :options="classrooms" label="教室名称" dense
                         :rules="[val => !!val || '不能为空']"
              />
              <q-select v-model="state0" :options="states" label="教室状态" dense
                         :rules="[val => !!val || '无效的类型']" />
            </q-form>
            <q-inner-loading :showing="changeLoading" />
          </q-tab-panel>
        </q-tab-panels>
        </q-card-section>
        <q-card-actions align="right" class="q-pa-md">
          <q-btn flat label="取消" @click="clear" :disable="loading" v-close-popup color="negative" />
          <q-btn flat label="确定" @click="submit" :loading="loading" color="primary" />
        </q-card-actions>

      </q-card>
    </q-dialog>
</template>

<script lang="ts">
import {ref, watch, defineComponent} from 'vue';
import {useClassroomStore} from 'stores/classroom';
import {Notify} from 'quasar';

const states = [
  {label: '开放', value: true},
  {label: '关闭', value: false}
]

export default defineComponent({
  name: 'ClassroomAdjuster',
  setup(){
    const CR = useClassroomStore()
    const show = ref(false)
    const loading = ref(false)
    const changeLoading = ref(false)
    const tab = ref('add')

    const classroom0 = ref('')
    const building0 = ref('')
    const state0 = ref({label: '关闭', value: false} as {label: string, value:boolean})

    const classroom1 = ref('')
    const building1 = ref('')
    const state1 = ref({label: '关闭', value: false} as {label: string, value:boolean})
    const rows = ref([] as {name: string, building: string, open:boolean}[])

    const buildings = ref([] as string[]);
    const classrooms = ref([] as string[]);

    watch(building0, () => {
      if(building0.value === '')return
      classrooms.value = []
      classroom0.value = ''
      rows.value.forEach(row=>{
        if(row.building === building0.value)classrooms.value.push(row.name)
      })
    })

    watch(classroom0, ()=>{
      if(classroom0.value === '')return
      state0.value = states[0]
      rows.value.forEach(row=>{
        if(row.name === classroom0.value)state0.value = row.open ? states[0]:states[1]
      })
    })

    const clear = ()=>{
      show.value = false
      tab.value = 'add'
      building0.value = building1.value ='';
      classroom0.value = classroom1.value = '';
      state0.value = state1.value = states[1]
    }
    const loadAll = async ()=>{
      console.log('ClassroomAdjuster: in loadAll')
      changeLoading.value = true
      show.value = true
      const r = await CR.load_all_classroom()
      if(r !== false && r.length != 0){
        rows.value = r
        buildings.value = [r[0].building]
        r.forEach((row:{name:string; building:string; open:boolean})=>{
          if(row.building !== buildings.value[buildings.value.length-1])buildings.value.push(row.building)
        })
        //classrooms.value = r.map((item:{name:string; building:string; open:boolean}) => item.name)
      }
      changeLoading.value = false
    }

    watch(tab, ()=>{
      if(tab.value === 'change')loadAll()
    })

    const changeClassroom = async()=>{
      console.log('ClassroomAdjuster: changeClassroom')
      if (classroom0.value === '' || building0.value === '' || state0.value.label === '') {
        Notify.create({type: 'negative', message: '信息不能为空'})
        return
      }
      loading.value = true

      if (await CR.modify_classroom({name: classroom0.value, building: building0.value, open: state0.value.value})) {
        clear()
      }
      loading.value = false
    }
    const addClassroom = async() =>{
      console.log('ClassroomAdjuster: addClassroom')
      if (classroom1.value === '' || building1.value === '' || state1.value.label === '') {
        Notify.create({type: 'negative', message: '信息不能为空'})
        return
      }
      if(classroom1.value.includes(' ') || building1.value.includes(' ')){
        Notify.create({type: 'negative', message: '不能包含空格'})
        return
      }
      loading.value = true

      if (await CR.modify_classroom({name: classroom1.value, building: building1.value, open: state1.value.value})) {
        clear()
      }
      loading.value = false
    }
    return{
      show, loading, changeLoading, tab,
      classroom0, classroom1, classrooms,
      building0, building1, buildings,
      state0, state1, states,
      clear, loadAll,
      async submit() {
        console.log('ClassroomAdjuster: submit')
        if (tab.value === 'change') await changeClassroom()
        else if (tab.value === 'add') await addClassroom()
        else Notify.create({type: 'negative', message: '表单错误'})
      },
    };
  },
})
</script>
