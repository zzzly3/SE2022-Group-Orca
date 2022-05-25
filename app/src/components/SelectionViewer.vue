<template>
  <q-dialog v-model="show" transition-show="scale" transition-hide="scale">
    <q-card middle style="width: 200px">
      <!--      <q-card-section>-->
      <!--        <div class="text-subtitle1 row items-center">-->
      <!--          <q-icon name="query_builder" size="sm" class="primary"></q-icon>-->
      <!--          <span>查看上课时间</span>-->
      <!--        </div>-->
      <!--      </q-card-section>-->
      <q-card-section class="q-py-none">
        <div style="width: 100%"  >
          <q-markup-table  flat class="text-center">
            <thead class="text-basic">
            <tr>
              <th>学生学号</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="i in rows" :key="i">
              <td>{{i}}</td>
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

<script lang="ts">
import {useSelectionConditionsStore} from 'stores/selection-conditions';
import {ref} from 'vue';

export default {
  name: 'SelectionViewer.vue',
  setup(){
    const selection = useSelectionConditionsStore()
    const show = ref(false)
    const tableLoading = ref(false)
    const rows = ref([] as number[])
    const clear = ()=>{
      show.value = false
    }
    const load = async (cid:string)=>{
      console.log('selectionViewer: in load, cid is ', cid)
      tableLoading.value = true
      show.value = true
      const r = await selection.loadCourseSelection(cid)
      console.log(r)
      if(r !== false){
        rows.value = r
      }else clear()
      tableLoading.value = false
    }
    return{
      show, tableLoading, rows,
      clear, load
    }
  }
}
</script>

<style scoped>

</style>
