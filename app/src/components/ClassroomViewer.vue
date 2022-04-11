<template>
  <q-dialog v-model="show" transition-show="scale" transition-hide="scale">
    <q-card style="width: 300px" :loading="tableLoading">
<!--      <q-card-section>-->
<!--        <div class="text-subtitle1 row items-center">-->
<!--          <q-icon name="query_builder" size="sm" class="primary"></q-icon>-->
<!--          <span>查看开放的教室</span>-->
<!--        </div>-->
<!--      </q-card-section>-->
      <q-card-section class="q-py-none">
        <div style="width: 100%">
          <q-markup-table flat class="text-center">
            <span>{{test}}</span>
            <thead class="text-basic">
            <tr>
              <th>教室</th>
              <th>所在教学楼</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="i in rows" :key="i.id">
              <td>{{i.name}}</td>
              <td>{{i.building}}</td>
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
</template>

<script>
import {defineComponent, ref} from 'vue';
import {useClassroomStore} from 'stores/classroom';

// const rows = [
//   {name: 'H2101', building: 'H2'},
//   {name: 'H3101', building: 'H3'},
//   {name: 'H4101', building: 'H4'},
//   {name: 'H5101', building: 'H5'},
//   {name: 'H6101', building: 'H6'}
// ]

export default defineComponent({
  name: 'ClassroomViewer',
  setup(){
    const CR = useClassroomStore()
    const show = ref(false)
    const tableLoading = ref(false)
    const rows = ref([])
    const test = ref('')

    const clear = ()=>{
      show.value = false
    }

    return{
      show, tableLoading, clear, rows, test,
      load: async function(){
        console.log('ClassroomViewer: in load')
        tableLoading.value = true
        show.value = true
        const r = await CR.load_open_classroom()
        if(r !== false){
          rows.value = r
        }else clear()
        tableLoading.value = false
      }
    }
  }
})

</script>

<style scoped>

</style>
