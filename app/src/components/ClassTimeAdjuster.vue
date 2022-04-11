<template>
    <q-dialog v-model="show" persistent transition-show="scale" transition-hide="scale">
      <q-card style="width: 400px">
        <q-card-section>
          <div class="text-subtitle1 row items-center">
            <q-icon name="today" size="sm" class="text-orange"></q-icon>
            <span>调整上课时间</span>
          </div>
        </q-card-section>
        <q-card-section class="q-py-none">
          <q-form style="width: 300px" class="q-px-md q-gutter-y-xs">
            <q-select v-model="classTime" :options="classTimes" label="选择课程节次" dense ref="classTimeRef"
                      lazy-rules :rules="[val => !!val || '无效的类型']"
            />
<!--            <q-input v-model="classTime" label="输入课程节次" ref="classTimeRef" dense-->
<!--                     clearable clear-icon="close" maxlength="2" lazy-rules-->
<!--                     :rules="[val => !!val || '不能为空']"-->
<!--            />-->
            <q-input v-model="beginTime" label="开始时间" mask="time" dense :rules="['time']">
              <template v-slot:append>
                <q-icon name="access_time" class="cursor-pointer">
                  <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                    <q-time v-model="beginTime">
                      <div class="row items-center justify-end">
                        <q-btn v-close-popup label="Close" color="primary" flat />
                      </div>
                    </q-time>
                  </q-popup-proxy>
                </q-icon>
              </template>
            </q-input>

            <q-input v-model="endTime" label="结束时间" mask="time" dense :rules="['time']">
              <template v-slot:append>
                <q-icon name="access_time" class="cursor-pointer">
                  <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                    <q-time v-model="endTime">
                      <div class="row items-center justify-end">
                        <q-btn v-close-popup label="Close" color="primary" flat />
                      </div>
                    </q-time>
                  </q-popup-proxy>
                </q-icon>
              </template>
            </q-input>
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
//import QValidate  from 'components/models';
import {defineComponent, ref, watch} from 'vue';
import {useClassTimeStore} from 'stores/class-time';
import {Notify} from 'quasar';
const classTimes = [
  1,2,3,4,5,6,7,8,9,10,11,12,13
]


export default defineComponent({
  name: 'ClassTimeAdjuster ',
  setup(){
    const CT = useClassTimeStore()
    const show = ref(false)
    const loading = ref(false)
    const tableLoading = ref(false)

    const classTime = ref('')
    const beginTime = ref('')
    const endTime = ref('')

    // const classTimeRef = ref<QValidate|null>(null)
    // const beginTimeRef = ref<QValidate|null>(null)
    // const endTimeRef = ref<QValidate|null>(null)

    const clear = ()=>{
      show.value = false
      classTime.value = beginTime.value = endTime.value = ''
    }
    const loadById = async ()=>{
      console.log('ClassTimeViewer: in load')
      if(classTime.value === '')return
      tableLoading.value = true
      const r = await CT.select_classTime(classTime.value)
      if(r !== false){
        beginTime.value = r.begin
        endTime.value = r.end
      }
      tableLoading.value = false
    }
    watch(classTime, loadById)

    return{
      show, loading, tableLoading, classTimes,
      classTime,
      beginTime,
      endTime,
      clear,
      submit: async function () {
        console.log('ClassTimeAdjuster: in submit')
        // classTimeRef.value.validate()
        // beginTimeRef.value.validate()
        // endTimeRef.value.validate()
        // if(classTimeRef.value.hasError || beginTimeRef.value.hasError || endTimeRef.value.hasError)
        //   return
        console.log('tag1')
        if (classTime.value === '' || beginTime.value === '' || endTime.value === ''){
          Notify.create({type:'negative', message:'信息不能为空'})
          return
        }
        console.log('tag2')
        loading.value = true
        if (await CT.modify_classTime({id: classTime.value, begin: beginTime.value, end: endTime.value}) !== false) {
          clear()
        }
        loading.value = false
      }
    }
  }
});
</script>
