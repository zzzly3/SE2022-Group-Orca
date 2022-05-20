<template>
    <q-dialog :model-value="show" @update:model-value="$event || hide()" persistent transition-show="scale" transition-hide="scale">
      <q-card style="width: 400px">
        <q-card-section>
          <div class="text-subtitle1 row items-center">
            <q-icon name="today" size="sm" class="text-orange"></q-icon>
            <span>调整上课时间</span>
          </div>
        </q-card-section>
        <q-card-section class="q-py-none">
            <q-form style="width: 300px" class="q-px-md q-gutter-y-xs">
              <q-input v-model="classTimeId" label="选择课程节次" dense ref="classTimeRef" readonly
                        lazy-rules :rules="[val => !!val || '无效的类型']"
              />

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
        </q-card-section>

        <q-card-actions align="right" class="q-pa-md">
<!--          <q-btn flat align="left" label="删除时间" @click="deleteClassTime" :disable="loading" color="warning"></q-btn>-->
          <q-btn flat label="取消" @click="clear" :disable="loading" v-close-popup color="negative" />
          <q-btn flat label="确定" @click="submit" :loading="loading" color="primary" />
        </q-card-actions>
      </q-card>
    </q-dialog>
</template>

<script lang="ts">
//import QValidate  from 'components/models';
import {computed, defineComponent, ref, watch, PropType} from 'vue';
import {useClassTimeStore, ClassTimeInfo} from 'stores/classtime';
import {Notify} from 'quasar';


export default defineComponent({
  name: 'ClassTimeAdjuster',
  props:{
    oldTime: {
      type: Object as PropType<ClassTimeInfo>,
      required: true
    },
    modelValue: Boolean
  },
  setup(props, {emit}){
    const CT = useClassTimeStore()
    const loading = ref(false)
    const classTimeId = ref(0)
    const beginTime = ref('')
    const endTime = ref('')
    const show = computed(() => props.modelValue)

    const hide = () =>{
      emit('update:modelValue', false)
    }
    const update = () => {
      let newTime = {
        id: classTimeId.value,
        begin: beginTime.value,
        end: endTime.value
      }
      emit('update', newTime)
    }
    const clear = ()=>{
      hide()
      classTimeId.value = 0
      beginTime.value = endTime.value = ''
    }

    const autofill = ()=>{
      if(props.oldTime){
        classTimeId.value = props.oldTime.id
        beginTime.value = props.oldTime.begin
        endTime.value = props.oldTime.end
      }
    }

    watch(show, val => {
      if(val)autofill()
    })

    // const deleteClassTime = async () =>{
    //   console.log('in deleteClassTime')
    //   if (classTime.value === '') {
    //     Notify.create({type: 'negative', message: '请选择上课的节次'})
    //     return
    //   }
    //   loading.value = true
    //   if (await CT.delete_classTime(classTime.value)) {
    //     clear()
    //   }
    //   loading.value = false
    // }

    return{
      show, loading,
      classTimeId,
      beginTime,
      endTime,
      clear, hide,
      submit: async function () {
        console.log('ClassTimeAdjuster: in submit')
        if (classTimeId.value === 0 || beginTime.value === '' || endTime.value === ''){
          Notify.create({type:'negative', message:'信息不能为空'})
          return
        }
        loading.value = true
        if (await CT.modify_classTime({id: classTimeId.value, begin: beginTime.value, end: endTime.value}) !== false) {
          update()
          clear()
        }
        loading.value = false
      }
    }
  }
});
</script>
