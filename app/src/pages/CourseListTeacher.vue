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
                @click="deleteCourse(props.row)"/>
            </q-btn-group>
            <!--CourseEditor-->
            <q-dialog v-model="editShow[props.rowIndex]">
              <q-card style="width: 450px" class="q-px-md q-gutter-y-md">
                <q-card-section class="row">
                </q-card-section>
                <q-card-section class="q-py-none">
                  <q-form style="width: 350px" class="q-gutter-y-md">
                    <q-input class="col" dense disable label="课程编号" v-model="props.row.courseId"/>
                    <q-input class="col" dense label="课程名称" disable v-model="props.row.courseName"/>
                    <div class="row items-start q-gutter-md">
                      <q-select class="col" dense v-model="props.row.courseTimeDay" :options="weekdays" label="上课时间"/>
                      <q-input class="col" dense v-model="props.row.courseTimeStart" type="time"/>
                      <q-field borderless readonly dense>
                        <template v-slot:control>
                          <div class="self-center full-width no-outline">
                            至
                          </div>
                        </template>
                      </q-field>
                      <q-input class="col" dense v-model="props.row.courseTimeEnd" type="time"/>
                    </div>
                    <div class="row items-start q-gutter-md">
                      <q-input class="col" dense v-model="props.row.coursePlace" label="上课教室"/>
                      <q-input class="col" dense v-model="props.row.courseTeacher" label="任课老师" disable/>
                    </div>
                    <div class="row items-start q-gutter-md">
                      <q-select class="col" dense disable v-model="props.row.courseMajor" :options="majors" label="所属专业"/>
                      <q-select class="col" dense disable v-model="props.row.courseDepartment" :options="departments" label="开课院系"/>
                    </div>
                    <div class="row items-start q-gutter-md">
                      <q-input class="col" dense disable v-model="props.row.courseCredit" label="学分"/>
                      <q-input class="col" dense disable v-model="props.row.courseCreditHour" label="学时"/>
                      <q-input class="col" dense disable v-model="props.row.courseCapacity" label="课程容量"/>
                    </div>
                    <q-input class="col" dense disable v-model="props.row.courseDescription" autogrow label="课程描述"/>
                  </q-form>
                </q-card-section>
                <q-card-section align="right">
                  <q-btn color="red" flat label="取消"
                    @click="editShow[props.rowIndex] = false"/>
                  <q-btn color="teal-10" flat label="确认修改" v-close-popup
                    @click="editSubmit(props.row)"/>
                </q-card-section>
              </q-card>
            </q-dialog>
            <!--CourseEditor-->
            <!-- courseDelete -->
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
        <div>
          <q-btn flat icon="add" @click="addShow = true" />
          <q-dialog v-model="addShow">
            <q-card style="width: 450px" class="q-px-md q-gutter-y-md">
              <q-card-section/>
              <q-card-section class="q-py-none">
                <q-form style="width: 350px" class="q-gutter-y-md">
                  <q-input class="col" dense label="课程编号" v-model="addCourseId" />
                  <q-input class="col" dense label="课程名称" v-model="addCourseName"/>
                  <div class="row items-start q-gutter-md">
                    <q-select class="col" dense v-model="addCourseTimeDay" :options="weekdays" label="上课时间"/>
                    <q-input class="col" dense v-model="addCourseTimeStart" type="time"/>
                    <q-field borderless readonly dense>
                      <template v-slot:control>
                        <div class="self-center full-width no-outline">至</div>
                      </template>
                    </q-field>
                    <q-input class="col" dense v-model="addCourseTimeEnd" type="time"/>
                  </div>
                  <div class="row items-start q-gutter-md">
                    <q-input class="col" dense v-model="addCoursePlace" label="上课教室"/>
                    <q-input class="col" dense v-model="addCourseTeacher" label="任课老师"/>
                  </div>
                  <div class="row items-start q-gutter-md">
                    <q-select class="col" dense v-model="addCourseMajor" :options="majors" label="所属专业"/>
                    <q-select class="col" dense v-model="addCourseDepartment" :options="departments" label="开课院系"/>
                  </div>
                  <div class="row items-start q-gutter-md">
                    <q-input class="col" dense v-model="addCourseCredit" label="学分"/>
                    <q-input class="col" dense v-model="addCourseCreditHour" label="学时"/>
                    <q-input class="col" dense v-model="addCourseCapacity" label="课程容量"/>
                  </div>
                  <q-input class="col" dense v-model="addCourseDescription" autogrow label="课程描述"/>
                </q-form>
              </q-card-section>

              <q-card-section>
                <q-card-actions align="right">
                  <q-btn flat label="取消" @click="clear" color="red" v-close-popup/>
                  <q-btn flat label="确认增加" @click="addSubmit" color="primary" v-close-popup/>
                </q-card-actions>
              </q-card-section>
            </q-card>
          </q-dialog>
        </div>
        <!--CourseAdder-->
      </template>
    </q-table>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';
import { CourseInfo, useCourseStore } from 'stores/course';
import { useUserStore } from 'stores/user';

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

const weekdays = [
  '星期一',
  '星期二',
  '星期三',
  '星期四',
  '星期五',
  '星期六',
  '星期日',
];

const departments = ['计算机学院', '经济学院', '数学学院'];
const majors = ['计算机科学与技术', '软件工程', '经济学'];

export default defineComponent({
  name: 'CourseList',
  components: {},
  setup() {
    const course = useCourseStore();
    // const user = useUserStore();
    const rows = ref([] as CourseInfo[]);
    course.load_course_lists_page_teacher().then((r) => (rows.value = r));

    //CourseAdder start
    const addShow = ref(false);

    const addCourseId = ref('');
    const addCourseName = ref('');
    const addCourseTime = ref('');
    const addCourseTimeDay = ref('');
    const addCourseTimeStart = ref();
    const addCourseTimeEnd = ref();
    const addCoursePlace = ref('');
    const addCourseTeacher = ref('');
    const addCourseMajor = ref('');
    const addCourseDepartment = ref('');
    const addCourseCredit = ref('');
    const addCourseCreditHour = ref('');
    const addCourseCapacity = ref('');
    const addCourseDescription = ref('');

    const clear = () => {
      addShow.value = false;
      addCourseId.value = '';
      addCourseName.value = '';
      addCourseTime.value = '';
      addCourseTimeDay.value = '';
      addCourseTimeStart.value = '';
      addCourseTimeEnd.value = '';
      addCoursePlace.value = '';
      addCourseTeacher.value = '';
      addCourseMajor.value = '';
      addCourseDepartment.value = '';
      addCourseCredit.value = '';
      addCourseCreditHour.value = '';
      addCourseCapacity.value = '';
      addCourseDescription.value = '';
    };
    //CourseAdder end

    //CourseEditor start
    const editShow = ref([false] as boolean[]);
    //CourseEditor end

    return {
      columns,
      rows,
      weekdays,
      departments,
      majors,

      //CourseAdder start
      addShow,

      addCourseId,
      addCourseName,
      addCourseTime,
      addCourseTimeDay,
      addCourseTimeStart,
      addCourseTimeEnd,
      addCoursePlace,
      addCourseTeacher,
      addCourseMajor,
      addCourseDepartment,
      addCourseCredit,
      addCourseCreditHour,
      addCourseCapacity,
      addCourseDescription,
      clear,
      async addSubmit() {
        const addTime = addCourseTimeDay.value.concat(
          ' : ',
          addCourseTimeStart.value,
          ' - ',
          addCourseTimeEnd.value
        );
        if (
          //apply add course
          await course.send_course_application({
            courseId: addCourseId.value,
            courseName: addCourseName.value,
            courseTime: addTime,
            courseTimeDay: addCourseTimeDay.value,
            courseTimeStart: addCourseTimeStart.value,
            courseTimeEnd: addCourseTimeEnd.value,
            coursePlace: addCoursePlace.value,
            courseTeacher: addCourseTeacher.value,
            courseMajor: addCourseMajor.value,
            courseDepartment: addCourseDepartment.value,
            courseCredit: addCourseCredit.value,
            courseCreditHour: addCourseCreditHour.value,
            courseCapacity: addCourseCapacity.value,
            courseDescription: addCourseDescription.value,
            applicationType: '1',
          })
        ) {
          clear();
        }
      },
      //CourseAdder end

      //CourseEditor start
      editShow,
      async editSubmit(row: CourseInfo) {
        const editTime = row.courseTimeDay.concat(
          ' : ',
          row.courseTimeStart,
          ' - ',
          row.courseTimeEnd
        );
        if (
          await course.send_course_application({
            courseId: row.courseId,
            courseName: row.courseName,
            courseTime: editTime,
            courseTimeDay: row.courseTimeDay,
            courseTimeStart: row.courseTimeStart,
            courseTimeEnd: row.courseTimeEnd,
            coursePlace: row.coursePlace,
            courseTeacher: row.courseTeacher,
            courseMajor: row.courseMajor,
            courseDepartment: row.courseDepartment,
            courseCredit: row.courseCredit,
            courseCreditHour: row.courseCreditHour,
            courseCapacity: row.courseCapacity,
            courseDescription: row.courseDescription,
            applicationType: '3',
          })
        ) {
          clear();
        }
      },
      //CourseEditor end
      async deleteCourse(row: CourseInfo) {
        await course.send_course_application({
          courseId: row.courseId,
          courseName: row.courseName,
          courseTime: row.courseTime,
          courseTimeDay: row.courseTimeDay,
          courseTimeStart: row.courseTimeStart,
          courseTimeEnd: row.courseTimeEnd,
          coursePlace: row.coursePlace,
          courseTeacher: row.courseTeacher,
          courseMajor: row.courseMajor,
          courseDepartment: row.courseDepartment,
          courseCredit: row.courseCredit,
          courseCreditHour: row.courseCreditHour,
          courseCapacity: row.courseCapacity,
          courseDescription: row.courseDescription,
          applicationType: '2',
        });
      },
    };
  },
});
</script>
