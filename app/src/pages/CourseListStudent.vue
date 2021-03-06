<template>
  <div class="q-pa-md column items-center">
    <q-table
      flat
      style="width: 100%"
      :rows="rows"
      dense
      :columns="showState === 1 || showState === 2 ? columns: appliedColumns"
      :loading="loading"
      row-key="courseId"
    >
      <template v-slot:header="props">
        <q-tr :props="props">
          <q-th auto-width />
          <q-th v-for="col in props.cols" :key="col.name" :props="props">
            {{ col.label }}
          </q-th>
        </q-tr>
      </template>

      <template v-slot:body="props">
        <q-tr :props="props">
          <q-td auto-width>
            <q-btn
              size="sm"
              flat
              @click="props.expand = !props.expand"
              :icon="props.expand ? 'expand_less' : 'expand_more'"
            />
          </q-td>
          <q-td v-for="col in props.cols" :key="col.name" :props="props">
            {{ col.value }}
          </q-td>
        </q-tr>
        <q-tr v-show="props.expand" :props="props">
          <q-td></q-td>
          <q-td colspan="100%">
            <q-btn v-show="showState === 1 || showState === 2" size="4px" color="positive" flat label="选课" @click="selectCourse(props.row.courseId)"></q-btn>
            <q-btn v-show="showState === 1 || showState === 2" size="4px" color="warning" flat label="选课申请" @click="applyCourse(props.row.courseId)"></q-btn>
            <q-btn v-show="showState === 3" size="4px" color="negative" flat label="退课" @click="dropCourse(props.row.courseId)"></q-btn>
            <q-btn v-show="showState === 4" size="4px" color="negative" flat label="撤销选课申请" @click="cancelCourseApplied(props.row.courseId)"></q-btn>
          </q-td>
        </q-tr>
      </template>

      <template v-slot:top>
        <div class="row">
          <q-btn flat class="q-ml-sm" color="primary" :disable="loading" label="所有课程" @click="showCourses(1)" />
          <q-btn flat class="q-ml-sm" color="primary" :disable="loading" label="可选课程" @click="showCourses(2)" />
          <q-btn flat class="q-ml-sm" color="primary" :disable="loading" label="已选课程" @click="showCourses(3)" />
          <q-btn flat class="q-ml-sm" color="primary" :disable="loading" label="已申请课程" @click="showCourses(4)" />
          <q-btn flat class="q-ml-sm" color="positive" :disable="loading" label="已修课程" @click="showCourses(5)" />
        </div>
        <div class="row">
          <q-input dense rounded outlined v-model="cid" label="课程序号"></q-input>
          <q-input dense rounded outlined v-model="ctime" label="课程时间"></q-input>
          <q-input dense rounded outlined v-model="cname" label="课程名"></q-input>
          <q-input dense rounded outlined v-model="classroom" label="课程教室"></q-input>
          <q-input dense rounded outlined v-model="teacher" label="课程老师"></q-input>
        </div>

        <q-btn icon="search" color="primary" round flat @click="search"></q-btn>
      </template>
    </q-table>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';
import { CourseApplicationInfo} from 'stores/course';
import {useSelectionConditionsStore} from 'stores/selection-conditions';
import { useQuasar } from 'quasar'

const appliedColumns = [
  {
    name: 'courseId',
    required: true,
    label: '课程编号',
    align: 'center',
    field: 'courseId',
  },
  {
    name: 'appliedState',
    align: 'center',
    label: '申请状态',
    field: 'state'
  },
  {
    name: 'appliedDescription',
    align: 'center',
    label: '描述',
    field: 'description'
  },
]
const columns = [
  {
    name: 'courseId',
    required: true,
    label: '课程编号',
    align: 'center',
    field: 'courseId',
  },
  {
    name: 'courseName',
    align: 'center',
    label: '课程名称',
    field: 'courseName',
  },
  {
    name: 'courseTime',
    align: 'center',
    label: '上课时间',
    field: 'courseTime',
  },
  {
    name: 'coursePlace',
    align: 'center',
    label: '上课教室',
    field: 'coursePlace',
  },
  {
    name: 'courseTeacher',
    align: 'center',
    label: '任课教师',
    field: 'courseTeacher',
  },
  {
    name: 'courseCredit',
    align: 'center',
    label: '学分',
    field: 'courseCredit',
  },
  {
    name: 'courseCreditHour',
    align: 'center',
    label: '学时',
    field: 'courseCreditHour',
  },
  {
    name: 'courseCapacity',
    align: 'center',
    label: '课程容量',
    field: 'courseCapacity',
  },
  {
    name: 'selected',
    align: 'center',
    label: '选课人数',
    field: 'selected',
  },
];

export default defineComponent({
  name: 'CourseListApplication',
  components: {},
  setup() {
    const selection = useSelectionConditionsStore();
    const rows = ref([] as CourseApplicationInfo[]);
    const loading = ref(false)
    const $q = useQuasar()
    const cid = ref('')
    const ctime = ref('')
    const cname = ref('')
    const classroom = ref('')
    const teacher = ref('')
    const showState = ref(1)

    selection.loadAllCourses().then((r) => (rows.value = r));
    const selectCourse = async (cid:string)=>{
      console.log('in selectCourse courseId is', cid)
      $q.dialog({
        title: '选课提交',
        message: '确认提交选课吗?',
        cancel: true,
        persistent: true,
      }).onOk(async () => {
        loading.value = true
        await selection.addSelection(cid)
        loading.value = false
      })
    }
    const applyCourse = async (cid:string)=>{
      console.log('in applyCourse courseId is', cid)
      $q.dialog({
        title: '选课申请',
        message: '请输入选课申请理由',
        prompt: {
          model: '',
          type: 'text' // optional
        },
        cancel: true,
        persistent: true
      }).onOk(async (des: string) => {
        console.log('>>>> OK ', des)
        loading.value = true
        await selection.applySelection({cid, des})
        loading.value = false
      })
    }
    const dropCourse = async (cid:string)=>{
      console.log('in dropCourse')
      $q.dialog({
        title: '退课',
        message: '确认退课吗?',
        cancel: true,
        persistent: true
      }).onOk(async () => {
        loading.value = true
        await selection.deleteSelection(cid)
        loading.value = false
      })
    }
    const cancelCourseApplied = async (cid:string)=>{
      console.log('in cancelCourseApplied')
      $q.dialog({
        title: '撤回选课申请',
        message: '确认撤回选课申请吗?',
        cancel: true,
        persistent: true
      }).onOk(async () => {
        loading.value = true
        await selection.deleteAppliedSelection(cid)
        loading.value = false
      })
    }
    const showCourses = async (idx:number)=>{
      console.log('in showCourses')
      showState.value = idx
      loading.value = true
      let r;
      switch (idx) {
        case 1: r = await selection.loadAllCourses(); console.log('in', idx); break;
        case 2: r = await selection.loadSelectableCourses(); console.log('in', idx); break;
        case 3: r = await selection.loadSelectedCourses(); console.log('in', idx); break;
        case 4: r = await selection.loadAppliedCourses(); console.log('in', idx); break;
        case 5: r = await selection.loadTakenCourses(); console.log('in', idx); break;
      }
      if(r !== false)rows.value = r
      loading.value = false
    }

    const search = async ()=>{
      console.log('in search')
      showState.value = 1
      loading.value = true
      //const r = selection.
      const r = await selection.search({cid:cid.value, ctime:ctime.value,
        cname:cname.value, classroom:classroom.value, teacher:teacher.value})
      if(r !== false)rows.value = r
      loading.value = false
    }

    return {
      cid, ctime, cname, classroom, teacher,
      showState, loading,
      applyCourse, selectCourse, dropCourse, cancelCourseApplied,
      showCourses, search,
      columns, appliedColumns,
      rows,
    };
  },
});
</script>
