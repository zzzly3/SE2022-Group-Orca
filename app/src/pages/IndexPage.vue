<template>
  <q-page class="row items-center justify-evenly">
    <div style="background: rgba(100%,100%,100%,0.5); width: 800px; height: 600px;" class="q-pa-lg q-gutter-x-md">
      <q-btn @click="list(0)" v-if="user.type==='student'">可选列表</q-btn>
      <q-btn @click="list(1)">已选列表</q-btn>
      <q-btn @click="list(2)" v-if="user.type==='student'">已修列表</q-btn>
      <q-btn @click="allow" v-if="user.type!=='student'">更改专业限制</q-btn>
      <q-btn @click="filter">筛选</q-btn>
      <p><br></p>
      <p v-for="(it, i) in data" :key="i">
        {{it.text}} &#9; <q-btn flat @click="it.func1" color="primary" v-if="it.opt1">{{it.opt1}}</q-btn> &#9; <q-btn flat color="primary" @click="it.func2" v-if="it.opt2">{{it.opt2}}</q-btn>
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
import {useUserStore} from 'stores/user';
import {CourseInfo, useCourseStore} from 'stores/course';
import {post} from 'boot/axios';

export default defineComponent({
  name: 'IndexPage',
  setup() {
    const $q = useQuasar()
    const user = useUserStore()
    const data = ref([] as {text: string, opt1: string, func1: ()=>void, opt2: string, func2: ()=>void}[])
    const course = useCourseStore()

    const all = ref([] as CourseInfo[])
    post('/course/get_course_all', {}).then(r => all.value = r)

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
        }).onOk((model: string) => {
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
    function to_text(course_id: string) {
      const c = all.value.find(it => it.courseId === course_id)
      function major2text(major: string) {
        if (major === '0')
          return '不限'
        return user.majors.find(it => it.id === Number(major))?.name
      }
      if (!c)
        return '[未知课程]'
      return `课程代码：${c.courseId} \t 课程名：${c.courseName} \t 任课教师：${c.courseTeacher} \t 上课时间：${c.courseTime} \t 上课地点：${c.coursePlace} \t 专业限制：${c.allowMajor.split(',').map(major2text).join(',')} \t 课程描述：${c.courseDescription}`
    }
    function null_func() {
      return
    }

    return {
      data, user,
      async list(type = 0) {
        data.value = []
        if (user.type === 'student') {
          const r:{studentId: string, courseId: string, state:number, description:string}[] = await course.load_selection('')
          switch (type) {
            case 0: {
              for (const cc of all.value) {
                if (r.find(it => it.courseId === cc.courseId))
                  continue
                const a = cc.allowMajor.split(',')
                if (!a.includes('0') && !a.includes(String(user.major.id)))
                  continue
                data.value.push({
                  text: to_text(cc.courseId),
                  opt1: cc.full ? '选课申请' : '选课',
                  func1: async () => {
                    if (await course.select_course(cc.courseId, cc.full ? 1 : 0, cc.full ? String(await get_input('申请理由')) : ''))
                      success()
                  },
                  opt2: '',
                  func2: null_func
                })
              }
            } break
            case 1: {
              for (const sc of r) {
                if (sc.state === 4)
                  continue
                data.value.push({
                  text: `(${['预选','申请中，理由：','已选','驳回'][sc.state]}${sc.state===1?sc.description:''}) \t ${to_text(sc.courseId)}`,
                  opt1: sc.state === 0 || sc.state === 2 ? '退课' : '',
                  func1: async () => {
                    if (await course.drop_course(sc.courseId, '')) {
                      all.value  = await post('/course/get_course_all', {})
                      success()
                    }
                  },
                  opt2: '',
                  func2: null_func
                })
              }
            } break
            case 2: {
              for (const sc of r) {
                if (sc.state === 4) {
                  data.value.push({
                    text: `(学期：${sc.description}) \t ${to_text(sc.courseId)}`,
                    opt1: '',
                    func1: null_func,
                    opt2: '',
                    func2: null_func
                  })
                }
              }
            } break
          }
        } else {
          const id = await get_input('课程代码')
          if (id) {
            const r:{studentId: string, courseId: string, state:number, description:string}[] = await course.load_selection(String(id))
            if (r) {
              for (const sc of r) {
                if (sc.state === 4 || sc.state === 3)
                  continue
                data.value.push({
                  text: `学号：${sc.studentId} \t 状态：${['预选','申请中，理由：','已选'][sc.state]}${sc.state===1?sc.description:''}`,
                  opt1: user.type==='admin'&&sc.state===1 ? '同意' : '',
                  func1: async () => {
                    if (await course.select_course(sc.courseId, 2, sc.studentId))
                      success()
                  },
                  opt2: user.type==='admin'&&sc.state===1 ? '驳回' : '',
                  func2: async () => {
                    if (await course.drop_course(sc.courseId, sc.studentId))
                      success()
                  }
                })
              }
            }
          }
        }
      },
      async filter() {
        const id = await get_input('筛选条件')
        if (id) data.value = data.value.filter(i => i.text.includes(String(id)))
      },
      async allow() {
        const id = await get_input('课程代码')
        if (id) {
          const m = await get_input('允许的专业ID，用英文逗号分隔，0表示允许所有专业')
          if (m && await course.update_allow(String(id), String(m))) {
            all.value = await course.load_course_lists_page_admin()
            success()
          }
        }
      },
    };
  }
});
</script>
