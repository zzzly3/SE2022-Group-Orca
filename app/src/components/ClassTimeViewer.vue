<template>
  <q-dialog v-model="show" persistent transition-show="scale" transition-hide="scale">
    <q-card style="width: 400px">
      <q-card-section>
        <div class="text-subtitle1 row items-center">
          <q-icon name="today" size="sm" class="text-orange"></q-icon>
          <span>查看上课时间</span>
        </div>
      </q-card-section>
      <q-card-section class="q-py-none">
        <div style="width: 80%">
          <q-markup-table :loading="tableLoading" flat class="text-center">
            <thead class="text-basic">
            <tr>
              <th>课程节次</th>
              <th>上课时间</th>
              <th>下课时间</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="i in rows" :key="i.id">
              <td>{{i.id}}</td>
              <td>{{i.begin}}</td>
              <td>{{i.end}}</td>
            </tr>
            </tbody>
          </q-markup-table>
        </div>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script>
import {defineComponent, ref} from 'vue';
import {useClassTimeStore, ClassTimeInfo} from 'stores/class-time';

export default defineComponent({
  name: 'ClassTimeViewer',
  setup(){
    const CT = useClassTimeStore()
    const show = ref(false)
    const tableLoading = ref(false)
    const rows = ref([])

    //const buttonLoading = ref(false)
    const clear = ()=>{
      show.value = false
    }
    return{
      show, tableLoading, clear, rows,
      load: async function(){
        console.log('ClassTimeViewer: in load')
        tableLoading.value = true
        show.value = true
        const r = await CT.load_classTime()
        if(r !== false){
            r.then(val => rows.value = val.data)
        }else clear()
        tableLoading.value = false
      }
    }
  }
})
</script>

<style scoped>

</style>
