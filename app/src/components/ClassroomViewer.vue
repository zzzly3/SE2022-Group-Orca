<template>
  <q-dialog v-model="show" transition-show="scale" transition-hide="scale">
    <q-card style="width: 500px" :loading="tableLoading">
      <q-card-section class="q-py-none" items-center>
        <div style="width: 100%" >
          <q-markup-table flat class="text-center">
            <thead class="text-basic">
            <tr>
              <th>教室名称</th>
              <th>教学楼</th>
              <th>教室容量</th>
              <th>教室状态</th>
              <th>
                <q-btn color="positive" icon="add" round flat size="md"
                       @click="showAdder">
                  <q-tooltip>添加教室</q-tooltip>
                </q-btn>
              </th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="i in rows" :key="i.name">
              <td>{{i.name}}</td>
              <td>{{i.building}}</td>
              <td>{{i.capacity}}</td>
              <td>{{i.open? states[1].label : states[0].label}}</td>
              <td>
                <q-btn color="negative" icon="update" round flat size="sm"
                       @click="showAdjuster(i)">
                  <q-tooltip>修改状态/删除教室</q-tooltip>
                </q-btn>
              </td>
            </tr>
            </tbody>
          </q-markup-table>
        </div>
      </q-card-section>
      <q-inner-loading
        :showing="tableLoading"
        color="teal"
      />
    </q-card>
  </q-dialog>
  <ClassroomAdjuster ref="ref0" class="q-ml-lg"></ClassroomAdjuster>
</template>

<script lang="ts">
import {defineComponent, ref} from 'vue';
import {useClassroomStore} from 'stores/classroom';
import ClassroomAdjuster from 'components/ClassroomAdjuster.vue';

const states = [
  {label: '关闭', value: false},
  {label: '开放', value: true},
]

export default defineComponent({
  name: 'ClassroomViewer',
  components: {ClassroomAdjuster},
  setup(){
    const CR = useClassroomStore()
    const show = ref(false)
    const tableLoading = ref(false)
    const rows = ref([] as {name:string, building:string, capacity:number, open:boolean}[])
    const ref0 = ref<componentRef|null>(null)
    const oldClassroom = ref({} as {name:string, building:string, capacity:number, open:boolean})

    const clear = ()=>{
      show.value = false
    }

    interface componentRef{
      show: boolean;
      building0: string;
      classroom0: string;
      capacity0: number;
      state0: {label:string, value:boolean};
      tab: string
      load: () =>void;
    }

    const showAdjuster=(oldClassroom: {name:string, building:string, capacity:number, open:boolean})=> {
      console.log('in ClassroomAdjuster')
      if(!ref0.value)return
      ref0.value.show = true;
      ref0.value.building0 = oldClassroom.building
      ref0.value.classroom0 = oldClassroom.name
      ref0.value.capacity0 = oldClassroom.capacity
      ref0.value.state0 = oldClassroom.open ? states[1] :states[0]
      ref0.value.tab = 'change'
    }

    const showAdder=()=>{
      if(!ref0.value)return
      ref0.value.show = true;
      ref0.value.tab = 'add'
    }

    return{
      ref0,
      show, tableLoading, clear, rows,
      states, oldClassroom,
      showAdjuster, showAdder,
      load: async function(){
        console.log('ClassroomViewer: in load')
        tableLoading.value = true
        show.value = true
        const r = await CR.load_all_classroom()
        if(r !== false){
          rows.value = r
        }else clear()
        tableLoading.value = false
      }
    }
  }
})

</script>
