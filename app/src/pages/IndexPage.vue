<template>
  <q-page class="row items-center justify-evenly">
    <div style="background: rgba(100%,100%,100%,0.5); width: 800px; height: 600px;" class="q-pa-lg q-gutter-x-md">
      <q-btn @click="select(0)" v-if="user.type==='student'">选课</q-btn>
      <q-btn @click="select(1)" v-if="user.type==='student'">选课申请</q-btn>
      <q-btn @click="select(2)" v-if="user.type==='admin'">通过申请</q-btn>
      <q-btn @click="list">选课列表</q-btn>
      <p><br></p>
      <p v-for="it in data" :key="it">
        {{it}}
      </p>
    </div>
<!--    <div style="position: absolute; top: 0; left: 0; height: 100%; width: 100%; z-index: -2">-->
<!--      <q-img src="~assets/home-bg-1.png"/>-->
<!--    </div>-->
  </q-page>
</template>

<script lang="ts">
import {defineComponent, ref} from 'vue';
import {useQuasar} from 'quasar';
import {post} from 'boot/axios';
import {useUserStore} from 'stores/user';

export default defineComponent({
  name: 'IndexPage',
  setup() {
    const $q = useQuasar()
    const user = useUserStore()
    const data = ref([])
    function get_input(message: string) {
      return new Promise(function(resolve){
        $q.dialog({
          title: '请输入',
          message,
          prompt: {
            model: '',
            type: 'text' // optional
          },
          cancel: true
        }).onOk(async (model) => {
          resolve(model)
        }).onCancel(() => {
          resolve(null)
        })
      })
    }
    function success(msg = '成功') {
      $q.notify({
        type: 'positive',
        message: msg
      })
    }

    return {
      data, user,
      async select(state = 0) {
        const id = await get_input('请输入课程代码')
        const d = id && state ? await get_input(state == 1 ? '请输入申请理由' : '请输入学生ID') : null
        if (id && await post('selection/select', {courseId: id, state, description: d, studentId: Number(d)}) !== false) {
          success()
        }
      },
      async list() {
        if (user.type === 'student') {
          const r = await post('selection/list')
          if (r) {
            data.value = r.map((i: { courseId: string; state: number; }) =>
              `${i.courseId}\t${['预选','申请中','已选'][i.state]}`)
          }
        } else {
          const id = await get_input('请输入课程代码')
          if (id) {
            const r = await post('selection/list', {courseId: id})
            if (r) {
              data.value = r.map((i: { studentId: string; state: number; courseId: string;description:string}) =>
                `${i.studentId}\t${i.courseId}\t${['预选','申请中','已选'][i.state]}\t${i.state == 1 ? i.description : ''}`)
            }
          }
        }
      }
    };
  }
});
</script>
