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
              <q-card style="width: 460px">
                <q-card-section>
                  <div style="height: 50px" class="text-subtitle1 row items-center">
                    <q-icon name="edit" size="sm"></q-icon>
                    <span>修改课程</span>
                  </div>
                </q-card-section>
                <q-card-section class="q-py-none">
                  <q-form style="width: 400px" class="q-px-md q-gutter-y-md">
                    <q-input style="height: 53px" class="col" dense disable label="课程编号" v-model="props.row.courseId"/>
                    <q-input style="height: 42px" class="col" dense label="课程名称" disable v-model="props.row.courseName"/>
                    <div class="row items-start q-gutter-md">
                      <q-select class="col" dense v-model="props.row.courseTimeDay" :options="weekdays" label="上课时间"/>
                      <q-select class="col" dense v-model="props.row.courseTimeStart" :options="courseTimeStarts" label="开始时间"/>
                      <q-field borderless disable dense>
                        <template v-slot:control>
                          <div class="self-center full-width no-outline">
                            至
                          </div>
                        </template>
                      </q-field>
                      <q-select class="col" dense v-model="props.row.courseTimeEnd" :options="courseTimeEnds" label="结束时间"/>
                    </div>
                    <div class="row items-start q-gutter-md">
                      <q-select class="col" dense v-model="props.row.coursePlace" :options="classrooms" label="上课教室"/>
                      <q-select class="col" dense v-model="props.row.courseTeacher" label="任课老师" disable/>
                    </div>
                    <div class="row items-start q-gutter-md">
                      <q-select class="col" dense disable v-model="props.row.courseMajor" :options="majors" label="所属专业"/>
                      <q-select class="col" dense disable v-model="props.row.courseDepartment" :options="departments" label="开课院系"/>
                    </div>
                    <div class="row items-start q-gutter-md">
                      <q-select style="height: 56px" class="col" dense disable v-model="props.row.courseCredit" label="学分"/>
                      <q-select class="col" dense disable v-model="props.row.courseCreditHour" label="学时"/>
                      <q-input class="col" dense disable v-model="props.row.courseCapacity" label="课程容量">
                        <template v-slot:append>
                          <q-icon name="arrow_drop_down" class="cursor-pointer" >
                          </q-icon>
                        </template>
                      </q-input>
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
            <q-card style="width: 460px">
              <q-card-section>
                <div style="height: 50px" class="text-subtitle1 row items-center">
                  <q-icon name="add" size="sm"></q-icon>
                  <span>新增课程</span>
                </div>
              </q-card-section>
              <q-card-section class="q-py-none">
                <q-form style="width: 400px" class="q-px-md q-gutter-y-md">
                  <q-input style="height: 53px" class="col" dense label="课程编号" v-model="addCourseId" autofocus
                  lazy-rules :rules="[val => rules.courseId.test(val) || '无效的课程编号']" ref="addCourseIdRef"/>
                  <q-input style="height: 53px" class="col" dense label="课程名称" v-model="addCourseName"
                  lazy-rules :rules="[val => rules.courseName.test(val) || '无效的课程名称']" ref="addCourseNameRef"/>
                  <div class="row items-start q-gutter-md">
                    <q-select class="col" dense v-model="addCourseTimeDay" :options="weekdays" label="上课时间"
                    lazy-rules :rules="[val => !!val || '上课时间不能为空']" ref="addCourseTimeDayRef"/>
                    <q-select class="col" dense v-model="addCourseTimeStart" :options="courseTimeStarts" label="开始时间"
                    lazy-rules :rules="[val => !!val || '课程开始时间不能为空']" ref="addCourseTimeStartRef"/>
                    <q-field borderless disable dense>
                      <template v-slot:control>
                        <div class="self-center full-width no-outline">至</div>
                      </template>
                    </q-field>
                    <q-select class="col" dense v-model="addCourseTimeEnd" :options="courseTimeEnds" label="结束时间"
                    lazy-rules :rules="[val => !!val || '课程结束时间不能为空']" ref="addCourseTimeEndRef"/>
                  </div>
                  <div class="row items-start q-gutter-md">
                    <q-select class="col" dense v-model="addCoursePlace" :options="classrooms" label="上课教室"
                    lazy-rules :rules="[val => !!val || '课程教室不能为空']" ref="addCoursePlaceRef"/>
                    <q-select class="col" dense v-model="addCourseTeacher" :options="teachers" label="任课老师"
                    lazy-rules :rules="[val => !!val || '任课教师不能为空']" ref="addCourseTeacherRef"/>
                  </div>
                  <div class="row items-start q-gutter-md">
                    <q-select class="col" dense v-model="addCourseMajor" :options="majors" label="所属专业"
                    lazy-rules :rules="[val => !!val || '课程所属专业不能为空']" ref="addCourseMajorRef"/>
                    <q-select class="col" dense v-model="addCourseDepartment" :options="departments" label="开课院系"
                    lazy-rules :rules="[val => !!val || '开课院系不能为空']" ref="addCourseDepartmentRef"/>
                  </div>
                  <div class="row items-start q-gutter-md">
                    <q-select style="height: 56px" class="col" dense v-model="addCourseCredit" :options="credits" label="学分"
                    lazy-rules :rules="[val => !!val || '学分不能为空']" ref="addCourseCreditRef"/>
                    <q-select class="col" dense v-model="addCourseCreditHour" :options="creditHours" label="学时"
                    lazy-rules :rules="[val => !!val || '学时不能为空']" ref="addCourseCreditHourRef"/>
                    <q-input class="col" dense v-model="addCourseCapacity" label="课程容量" ref="addCourseCapacityRef"
                    lazy-rules :rules="[val => rules.courseCapacity.test(val) || '无效的课程容量']" >
                      <template v-slot:append>
                        <q-icon name="arrow_drop_down" class="cursor-pointer" >
                        </q-icon>
                      </template>
                      <q-menu fit v-model="showMenu">
                        <q-list style="min-width: 100px">
                          <q-item clickable @click="menu1">
                            <q-item-section>10</q-item-section>
                          </q-item>
                          <q-item clickable @click="menu2">
                            <q-item-section>20</q-item-section>
                          </q-item>
                          <q-item clickable @click="menu3">
                            <q-item-section>30</q-item-section>
                          </q-item>
                          <q-item clickable @click="menu4">
                            <q-item-section>40</q-item-section>
                          </q-item>
                          <q-item clickable @click="menu5">
                            <q-item-section>50</q-item-section>
                          </q-item>
                        </q-list>
                      </q-menu>
                    </q-input>
                  </div>
                  <q-input class="col" dense v-model="addCourseDescription" autogrow label="课程描述"
                  lazy-rules :rules="[val => !!val || '课程描述不能为空']" ref="addCourseDescriptionRef"/>
                </q-form>
              </q-card-section>

              <q-card-section>
                <q-card-actions align="right">
                  <q-btn flat label="取消" @click="clear" color="red" v-close-popup/>
                  <q-btn flat label="确认增加" @click="addSubmit" color="primary"/>
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
import { defineComponent, ref, computed } from 'vue';
import { CourseInfo, useCourseStore } from 'stores/course';
import { QValidate } from 'src/components/models';
import { useQuasar } from 'quasar';

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

const credits = [1,2,3,4,5,6];
const creditHours = [1,2,3,4,5,6];

const rules = {
  courseId: /^[A-Z]+[0-9]+$/,
  courseName: /^[^0-9]+$/,
  courseCapacity: /^[0-9]+$/,
}

export default defineComponent({
  name: 'CourseList',
  components: {},
  setup() {
    const $q = useQuasar();
    const course = useCourseStore();
    // const user = useUserStore();
    const rows = ref([] as CourseInfo[]);
    course.load_course_lists_page_teacher().then((r) => (rows.value = r));
    course.load_course_constants()
    const courseTimeStarts = computed(() => {
      return course.courseTimeStartList
    })
    const courseTimeEnds = computed(() => {
      return course.courseTimeEndList
    })
    const classrooms = computed(() => {
      return course.classroomList
    })
    const teachers = computed(() => {
      return course.teacherList
    })
    const departments = computed(() => {
      return course.departmentList
    })
    const majors = computed(() => {
      return course.majorList
    })

    //CourseAdder start
    const showMenu = ref(false);
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

    const addCourseIdRef = ref<QValidate|null>(null);
    const addCourseNameRef = ref<QValidate|null>(null);
    const addCourseTimeRef = ref<QValidate|null>(null);
    const addCourseTimeDayRef = ref<QValidate|null>(null);
    const addCourseTimeStartRef = ref<QValidate|null>(null);
    const addCourseTimeEndRef = ref<QValidate|null>(null);
    const addCoursePlaceRef = ref<QValidate|null>(null);
    const addCourseTeacherRef = ref<QValidate|null>(null);
    const addCourseMajorRef = ref<QValidate|null>(null);
    const addCourseDepartmentRef = ref<QValidate|null>(null);
    const addCourseCreditRef = ref<QValidate|null>(null);
    const addCourseCreditHourRef = ref<QValidate|null>(null);
    const addCourseCapacityRef = ref<QValidate|null>(null);
    const addCourseDescriptionRef = ref<QValidate|null>(null);


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
    const menu1 = () => {
      addCourseCapacity.value = '10';
      showMenu.value =false
    }
    const menu2 = () => {
      addCourseCapacity.value = '20';
      showMenu.value =false
    }
    const menu3 = () => {
      addCourseCapacity.value = '30';
      showMenu.value =false
    }
    const menu4 = () => {
      addCourseCapacity.value = '40';
      showMenu.value =false
    }
    const menu5 = () => {
      addCourseCapacity.value = '50';
      showMenu.value =false
    }
    //CourseAdder end

    //CourseEditor start
    const editShow = ref([false] as boolean[]);
    //CourseEditor end

    return {
      rules,

      columns,
      rows,
      weekdays,
      credits,
      creditHours,
      courseTimeStarts,
      courseTimeEnds,
      classrooms,
      teachers,
      departments,
      majors,

      //CourseAdder start
      showMenu,
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

      addCourseIdRef,
      addCourseNameRef,
      addCourseTimeRef,
      addCourseTimeDayRef,
      addCourseTimeStartRef,
      addCourseTimeEndRef,
      addCoursePlaceRef,
      addCourseTeacherRef,
      addCourseMajorRef,
      addCourseDepartmentRef,
      addCourseCreditRef,
      addCourseCreditHourRef,
      addCourseCapacityRef,
      addCourseDescriptionRef,


      clear,
      menu1,
      menu2,
      menu3,
      menu4,
      menu5,

      async addSubmit() {
        /*
        if(!addCourseIdRef.value || !addCourseNameRef.value || !addCourseTimeRef.value || !addCourseTimeDayRef.value || !addCourseTimeStartRef.value || !addCourseTimeEndRef.value || !addCoursePlaceRef.value || !addCourseTeacherRef.value || !addCourseMajorRef.value || !addCourseDepartmentRef.value || !addCourseCreditRef.value || !addCourseCreditHourRef.value || !addCourseCapacityRef.value || !addCourseDescriptionRef.value) {
          console.log(addCourseIdRef.value)
          console.log(addCourseNameRef.value)
          $q.notify({
            color: 'negative',
            message: '请正确填写所有信息',
          });
          return;
        }

        addCourseIdRef.value.validate();
        addCourseNameRef.value.validate();
        addCourseTimeDayRef.value.validate();
        addCourseTimeStartRef.value.validate();
        addCourseTimeEndRef.value.validate();
        addCoursePlaceRef.value.validate();
        addCourseTeacherRef.value.validate();
        addCourseMajorRef.value.validate();
        addCourseDepartmentRef.value.validate();
        addCourseCreditRef.value.validate();
        addCourseCreditHourRef.value.validate();
        addCourseCapacityRef.value.validate();
        addCourseDescriptionRef.value.validate();
        if (addCourseIdRef.value.hasError || addCourseNameRef.value.hasError || addCourseTimeDayRef.value.hasError || addCourseTimeStartRef.value.hasError || addCourseTimeEndRef.value.hasError || addCoursePlaceRef.value.hasError || addCourseTeacherRef.value.hasError || addCourseMajorRef.value.hasError || addCourseDepartmentRef.value.hasError || addCourseCreditRef.value.hasError || addCourseCreditHourRef.value.hasError || addCourseCapacityRef.value.hasError || addCourseDescriptionRef.value.hasError) {
          return;
        }
        */

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
