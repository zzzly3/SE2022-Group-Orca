<template>
  <q-dialog v-model="show" transition-show="scale" transition-hide="scale">
    <q-card middle style="width: 400px">
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
                <th>课程节次</th>
                <th>上课时间</th>
                <th>下课时间</th>
                <th></th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="i in rows" :key="i.id">
                <td>{{i.id}}</td>
                <td>{{i.begin}}</td>
                <td>{{i.end}}</td>
                <td>
                  <q-btn color="primary" icon="edit" round flat size="sm"
                                        @click="classTime=i;showAdjuster=true">
                  <q-tooltip>修改时间</q-tooltip>
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
  <ClassTimeAdjuster v-model="showAdjuster" :oldTime="classTime" @update="update"></ClassTimeAdjuster>
</template>

<script lang="ts">
import {defineComponent, ref} from 'vue';
import {useClassTimeStore} from 'stores/classtime';
import ClassTimeAdjuster from 'components/ClassTimeAdjuster.vue';


export default defineComponent({
  name: 'ClassTimeViewer',
  components: {ClassTimeAdjuster},
  setup(){
    const CT = useClassTimeStore()
    const show = ref(false)
    const tableLoading = ref(false)
    const rows = ref([] as {id:number, begin:string, end:string}[])
    const classTime = ref({} as {id:number, begin:string, end:string})
    const showAdjuster = ref(false)

    //const buttonLoading = ref(false)
    const update = (newTime: {id:number, begin:string, end:string}) =>{
      console.log('ClassTimeViewer: in update')
      rows.value[newTime.id-1].begin = newTime.begin
      rows.value[newTime.id-1].end = newTime.end
    }
    const clear = ()=>{
      show.value = false
    }
    const load = async ()=>{
      console.log('ClassTimeViewer: in load')
      tableLoading.value = true
      show.value = true
      const r = await CT.load_all_classTime()
      if(r !== false){
        rows.value = r
      }else clear()
      tableLoading.value = false
    }

    return{
      update,
      show, tableLoading, clear, rows,
      classTime, showAdjuster,
      load
    }
  }
})

</script>
