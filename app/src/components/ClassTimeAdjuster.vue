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
<!--            <q-select v-model="classTime" :options="classTimes" label="课程节次" dense ref="classTimeRef"-->
<!--                      lazy-rules :rules="[val => !!val || '无效的类型']"-->
<!--            />-->
            <q-input v-model="classTime" label="输入课程节次" ref="classTimeRef" dense
                     clearable clear-icon="close" maxlength="2" lazy-rules
                     :rules="[val => !!val || '不能为空']"
            />

            <q-input v-model="beginTime" label="开始时间" ref="beginTimeRef" type="time"
                     :rules="[val => !!val || '不能为空']"
            />
            <q-input v-model="endTime" label="结束时间" ref="endTimeRef" type="time"
                     :rules="[val => !!val || '不能为空']"
            />
          </q-form>
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
import {defineComponent, ref, onMounted} from 'vue';
//import * as QValidate from 'components/QValidate.ts';
import {useClassTimeStore} from 'stores/class-time';

export default defineComponent({
  name: 'ClassTimeAdjuster ',
  setup(){
    const CT = useClassTimeStore()
    const show = ref(false)
    const loading = ref(false)

    const classTime = ref('')
    const beginTime = ref('')
    const endTime = ref('')

    // const classTimeRef = ref<QValidate|null>(null)
    // const beginTimeRef = ref<QValidate|null>(null)
    // const endTimeRef = ref<QValidate|null>(null)
    const classTimeRef = ref(null)
    const beginTimeRef = ref(null)
    const endTimeRef = ref(null)

    const clear = ()=>{
      show.value = false
    }
    onMounted(()=>{
      console.log(classTimeRef)
      console.log(classTimeRef.value)
    })
    return{
      show, loading,
      classTime, classTimeRef,
      beginTime, beginTimeRef,
      endTime, endTimeRef,
      clear,
      submit: async function () {
        console.log('ClassTimeAdjuster: in submit')
        if (!classTimeRef.value || !beginTimeRef.value || !endTimeRef.value)
          return
        // classTimeRef.value.validate()
        // beginTimeRef.value.validate()
        // endTimeRef.value.validate()
        // if(classTimeRef.value.hasError || beginTimeRef.value.hasError || endTimeRef.value.hasError)
        //   return
        if (classTime.value === '' || beginTime.value === '' || endTime.value === ''){
          return
        }
        loading.value = true
        if (await CT.modify_classTime({id: classTime.value, begin: beginTime.value, end: endTime.value})) {
          clear()
        }
        loading.value = false
      }
    }
  }
});
</script>
