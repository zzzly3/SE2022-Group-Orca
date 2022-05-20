<template>
  <div class="q-pa-md column items-center">
    <q-table
      flat
      style="width: 80%"
      :rows="rows"
      :columns="columns"
      row-key="courseId"
    >
      <template v-slot:header="props">
        <q-tr :props="props">
          <q-th auto-width />
          <q-th v-for="col in props.cols" :key="col.name" :props="props">
            {{ col.label }}
          </q-th>
          <q-th />
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
          <q-td>
            <q-btn-group spread flat>
              <q-btn style="width: 5px" flat color="teal-10" size="sm" :icon="'edit'"
                @click="editShow[props.rowIndex] = true"
              />
              <q-btn flat style="width: 5px" color="red" size="sm" :icon="'close'"
                @click="deleteShow[props.rowIndex] = true"/>
            </q-btn-group>
            <!--CourseEditor-->
            <CourseDialog v-model="editShow[props.rowIndex]" :row="props.row" :isAdmin='false' :isAdd="false" :isEdit="true"/>
            <!-- courseDelete -->
            <q-dialog v-model="deleteShow[props.rowIndex]">
              <q-card style="width: 300px" class="q-pa-md">
                <q-card-section>
                  <div class="text-subtitle1 self-center full-width no-outline">
                    确定删除该课程？
                  </div>
                </q-card-section>
                <q-separator />
                <q-card-section align="right">
                  <q-btn color="red" flat label="取消"
                    @click="deleteShow[props.rowIndex] = false"/>
                  <q-btn color="teal-10" flat label="确定" v-close-popup
                    @click="deleteCourse(props.row)"/>
                </q-card-section>
              </q-card>
            </q-dialog>
            <!-- courseDelete -->
          </q-td>
        </q-tr>
        <q-tr v-show="props.expand" :props="props">
          <q-td></q-td>
          <q-td colspan="100%">
            <div class="align">{{ props.row.courseDescription }}.</div>
          </q-td>
        </q-tr>
      </template>

      <template v-slot:top-right>
        <!--CourseAdder-->
        <q-btn flat icon="add" @click="addShow = true" />
        <CourseDialog v-model="addShow" :isAdmin='false' :isAdd="true" :isEdit="false"/>
      </template>
    </q-table>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref} from 'vue';
import { CourseApplicationInfo, CourseInfo, useCourseStore } from 'stores/course';
import CourseDialog from 'src/components/CourseDialog.vue';

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
    name: 'courseDepartment',
    label: '开课院系',
    field: 'courseDepartment',
    align: 'center',
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
];

export default defineComponent({
  name: 'CourseList',
  components: { CourseDialog},
  setup() {
    const course = useCourseStore();
    const rows = ref([] as CourseInfo[]);
    course.load_course_lists_page_teacher().then((r) => (rows.value = r));
    course.load_course_constants()

    const addShow = ref(false);
    const editShow = ref([false] as boolean[]);
    const deleteShow = ref([false] as boolean[]);

    return {
      columns,
      rows,

      addShow,
      editShow,
      deleteShow,

      async deleteCourse(row: CourseInfo) {
        const applicationInfo: CourseApplicationInfo = {
          courseId: row.courseId,
          courseName: row.courseName,
          courseTime: row.courseTime,
          courseTimeDay: row.courseTimeDay,
          courseTimeStart: row.courseTimeStart,
          courseTimeEnd: row.courseTimeEnd,
          coursePlace: row.coursePlace,
          courseTeacher: row.courseTeacher,
          courseCredit: row.courseCredit,
          courseCreditHour: row.courseCreditHour,
          courseCapacity: row.courseCapacity,
          courseDescription: row.courseDescription,
          applicationType: '2',
          applicationTime: '',
          applicantName: '',
          applicantNumber: '',
          applicationStatus: '',
          applicationId: '',
        };
        await course.send_course_application({
          applicationInfo
        });
      },
      //CourseDelete end
    };
  },
});
</script>
