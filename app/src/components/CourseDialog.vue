<template>
    <q-dialog :model-value="show" @update:model-value="$emit('update:modelValue', $event)" @close="clear">
      <q-card style="width: 460px">
        <q-card-section>
          <div style="height: 50px" class="text-subtitle1 row items-center">
            <q-icon v-if="adminEdit || teacherEdit" name="edit" size="sm"></q-icon>
            <q-icon v-else-if="adminAdd || teacherAdd" name="add" size="sm"></q-icon>
            <q-icon v-else name="visibility" color="blue" size="sm"></q-icon>
            <span v-if="adminEdit || teacherEdit">修改课程</span>
            <span v-else-if="adminAdd || teacherAdd">添加课程</span>
            <span v-else>查看课程</span>
          </div>
        </q-card-section>

        <q-card-section class="q-py-none">
          <q-form style="width: 400px" class="q-px-md q-gutter-y-sm">
            <q-input style="height: 53px" class="col" dense :disable="adminEdit||teacherEdit||adminCheck" label="课程编号" v-model="courseId"
            lazy-rules :rules="[val => rules.courseId.test(val) || '无效的课程编号']" ref="courseIdRef"/>
            <q-input style="height: 60px" class="col" dense :disable="adminCheck" label="课程名称" v-model="courseName"
            lazy-rules :rules="[val => rules.courseName.test(val) || '无效的课程名称']" ref="courseNameRef"/>
            <div class="row items-start q-gutter-md">
              <q-select style="height: 60px" class="col" :disable="adminCheck" dense v-model="courseTimeDay" :options="weekdays" label="上课时间"
              lazy-rules :rules="[val => !!val || '上课时间不能为空']" ref="courseTimeDayRef"/>
              <q-select class="col" dense v-model="courseTimeStart" :disable="adminCheck" :options="courseTimeStarts" label="开始时间"
              lazy-rules :rules="[val => !!val || '课程开始时间不能为空']" ref="courseTimeStartRef"/>
              <q-field borderless disable dense>
                <template v-slot:control>
                  <div class="self-center full-width no-outline">至</div>
                </template>
              </q-field>
              <q-select class="col" dense v-model="courseTimeEnd" :disable="adminCheck" :options="courseTimeEnds" label="结束时间"
              lazy-rules :rules="[val => !!val || '课程结束时间不能为空']" ref="courseTimeEndRef"/>
            </div>
            <div class="row items-start q-gutter-md">
              <q-select :disable="adminEdit || teacherEdit || teacherAdd || adminCheck" class="col" dense v-model="courseTeacher" :options="teachers" label="任课老师"
              lazy-rules :rules="[val => !!val || '课程任课老师不能为空']" ref="courseTeacherRef">
                <template v-slot:option="scope">
                  <q-item v-bind="scope.itemProps">
                    <q-item-section>
                      <q-item-label>{{ scope.opt.label }}</q-item-label>
                      <q-item-label caption>{{ scope.opt.description }}</q-item-label>
                    </q-item-section>
                  </q-item>
                </template>
              </q-select>
              <q-select style="height: 63px" class="col-5" :disable="adminCheck" dense v-model="coursePlace" :options="classrooms" label="上课教室"
              lazy-rules :rules="[val => !!val || '课程教室不能为空']" ref="coursePlaceRef"/>
            </div>
            <div class="row items-start q-gutter-md">
              <q-select style="height: 56px" class="col" :disable="adminCheck" dense v-model="courseCredit" :options="credits" label="学分"
              lazy-rules :rules="[val => !!val || '课程学分不能为空']" ref="courseCreditRef"/>
              <q-select class="col" dense v-model="courseCreditHour" :disable="adminCheck" :options="creditHours" label="学时"
              lazy-rules :rules="[val => !!val || '课程学时不能为空']" ref="courseCreditHourRef"/>
              <q-input class="col" dense v-model="courseCapacity" :disable="adminCheck" label="课程容量"
              lazy-rules :rules="[val => rules.courseCapacity.test(val) || '无效的课程容量']" ref="courseCapacityRef">
                <template v-slot:append>
                  <q-icon name="arrow_drop_down" class="cursor-pointer" >
                    </q-icon>
                </template>
                <q-menu fit v-model="showMenuEdit" >
                  <q-list style="min-width: 100px">
                    <q-item clickable v-for="i in [10, 20, 30, 40, 50]" :key="i" @click="courseCapacity = i;showMenuEdit=false">
                      {{i}}
                    </q-item>
                  </q-list>
                </q-menu>
              </q-input>
            </div>
            <q-input class="col" dense v-model="courseDescription" :disable="adminCheck" autogrow label="课程描述"
            lazy-rules :rules="[val => !!val || '课程描述不能为空']" ref="courseDescriptionRef"/>
          </q-form>
        </q-card-section>
        <q-card-section>
          <q-card-actions align="right">
            <q-btn v-if="!adminCheck" color="red" flat @click="clear" label="取消"/>
            <q-btn v-else flat color="red" @click="applicationStatus='2';submit()" label="拒绝申请"/>
            <q-btn v-if="adminEdit || teacherEdit" color="teal-10" flat @click="submit" label="修改"/>
            <q-btn v-else-if="adminAdd || teacherAdd" color="teal-10" flat @click="submit" label="添加"/>
            <q-btn v-else color="teal-10" flat @click="applicationStatus='1';submit()" label="同意申请"/>
          </q-card-actions>
        </q-card-section>
      </q-card>
    </q-dialog>
</template>


<script lang="ts">
import { defineComponent, ref, computed, PropType, watch } from 'vue';
import { CourseApplicationInfo, CourseInfo, useCourseStore } from 'stores/course';
import { QValidate } from 'src/components/models';
import { useUserStore } from 'src/stores/user';

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

interface TeacherInfo {
  value: string;
  label: string;
  description: string;
}

export default defineComponent({
  name: 'CourseDialog',
  props: {
    row: {
      type: Object as PropType<CourseInfo>,
      required: false,
    },
    application: {
      type: Object as PropType<CourseApplicationInfo>,
      required: false,
    },
    modelValue: Boolean,
    isAdd: Boolean,
    isEdit: Boolean,
    isAdmin: Boolean,
  },

  setup(props, {emit}) {
    const user = useUserStore();
    const course = useCourseStore();
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
    /*Dialog Type*/
    const adminAdd = ref(false);
    const adminEdit = ref(false);
    const adminCheck = ref(false);
    const teacherAdd = ref(false);
    const teacherEdit = ref(false);

    /*Element Refs */
    const courseIdRef = ref<QValidate|null>(null);
    const courseNameRef = ref<QValidate|null>(null);
    const courseTimeRef = ref<QValidate|null>(null);
    const courseTimeDayRef = ref<QValidate|null>(null);
    const courseTimeStartRef = ref<QValidate|null>(null);
    const courseTimeEndRef = ref<QValidate|null>(null);
    const coursePlaceRef = ref<QValidate|null>(null);
    const courseTeacherRef = ref<QValidate|null>(null);
    const courseCreditRef = ref<QValidate|null>(null);
    const courseCreditHourRef = ref<QValidate|null>(null);
    const courseCapacityRef = ref<QValidate|null>(null);
    const courseDescriptionRef = ref<QValidate|null>(null);



    /*Dialog Elements*/
    const courseId = ref('');
    const courseName = ref('');
    const courseTimeDay = ref('');
    const courseTimeStart = ref('');
    const courseTimeEnd = ref()
    const courseTeacher = ref<string|TeacherInfo>();
    const coursePlace = ref('');
    const courseCredit = ref('');
    const courseCreditHour = ref('');
    const courseCapacity = ref('');
    const courseDescription = ref('');

    const applicationTime = ref('');
    const applicantName = ref('');
    const applicantNumber = ref('');
    const applicationStatus = ref('');
    const applicationId = ref('');

    const autofill = async () => {
      adminAdd.value = props.isAdd && props.isAdmin;
      adminEdit.value = props.isEdit && props.isAdmin;
      teacherAdd.value = props.isAdd && !props.isAdmin;
      teacherEdit.value = props.isEdit && !props.isAdmin;
      adminCheck.value = !props.isAdd && !props.isEdit && props.isAdmin;

      /*Fill Course Info */
      if (props.row) {
        courseId.value = props.row.courseId;
        courseName.value = props.row.courseName;
        courseTimeDay.value = props.row.courseTimeDay;
        courseTimeStart.value = props.row.courseTimeStart;
        courseTimeEnd.value = props.row.courseTimeEnd;
        courseTeacher.value = props.row.courseTeacher;
        coursePlace.value = props.row.coursePlace;
        courseCredit.value = props.row.courseCredit;
        courseCreditHour.value = props.row.courseCreditHour;
        courseCapacity.value = props.row.courseCapacity;
        courseDescription.value = props.row.courseDescription;
        applicationTime.value = '';
        applicantName.value = '';
        applicantNumber.value = '';
        applicationStatus.value = '';
        applicationId.value = '';
      }
      /*Fill Course Application Info */
      else if(props.application) {
        courseId.value = props.application.courseId;
        courseName.value = props.application.courseName;
        courseTimeDay.value = props.application.courseTimeDay;
        courseTimeStart.value = props.application.courseTimeStart;
        courseTimeEnd.value = props.application.courseTimeEnd;
        courseTeacher.value = props.application.courseTeacher;
        coursePlace.value = props.application.coursePlace;
        courseCredit.value = props.application.courseCredit;
        courseCreditHour.value = props.application.courseCreditHour;
        courseCapacity.value = props.application.courseCapacity;
        courseDescription.value = props.application.courseDescription;
        applicationTime.value = props.application.applicationTime;
        applicantName.value = props.application.applicantName;
        applicantNumber.value = props.application.applicantNumber;
        applicationStatus.value = props.application.applicationStatus;
        applicationId.value = props.application.applicationId;
      }
      else {
        courseId.value = '';
        courseName.value = '';
        courseTimeDay.value = '';
        courseTimeStart.value = '';
        courseTimeEnd.value = '';
        courseTeacher.value = teacherAdd.value ? user.name.concat(' (工号: ', user.id, ')') : '';
        coursePlace.value = '';
        courseCredit.value = '';
        courseCreditHour.value = '';
        courseCapacity.value = '';
        courseDescription.value = '';
        applicationTime.value = '';
        applicantName.value = '';
        applicantNumber.value = '';
        applicationStatus.value = '';
        applicationId.value = '';
      }
    }

    const show = computed(() => props.modelValue)
    if (show.value) {
      autofill();
    }
    watch(show, (val) => {
      if (val) {
        autofill();
      }
    })
    const clear = () => {
      emit('update:modelValue', false);
      if (adminAdd.value || teacherAdd.value) {
        courseId.value = '';
        courseName.value = '';
        courseTimeDay.value = '';
        courseTimeStart.value = '';
        courseTimeEnd.value = '';
        coursePlace.value = '';
        courseTeacher.value = '';
        courseCredit.value = '';
        courseCreditHour.value = '';
        courseCapacity.value = '';
        courseDescription.value = '';
      }
    }
    const showMenuEdit = ref(false)
    const updateDialog = async () => {
      emit('update:modelValue', false);
      emit('user_update')
    }

    return {
      /*Dialog Constants*/
      rules,
      weekdays,
      credits,
      creditHours,
      courseTimeStarts,
      courseTimeEnds,
      classrooms,
      teachers,

      /*Dialog Expand Controllers */
      show,
      showMenuEdit,

      /*Dialog Type*/
      adminAdd,
      adminEdit,
      adminCheck,
      teacherAdd,
      teacherEdit,

      /*Dialog Elements*/
      courseId,
      courseName,
      courseTimeDay,
      courseTimeStart,
      courseTimeEnd,
      courseTeacher,
      coursePlace,
      courseCredit,
      courseCreditHour,
      courseCapacity,
      courseDescription,

      applicationTime,
      applicantName,
      applicantNumber,
      applicationStatus,
      applicationId,

      courseIdRef,
      courseNameRef,
      courseTimeRef,
      courseTimeDayRef,
      courseTimeStartRef,
      courseTimeEndRef,
      coursePlaceRef,
      courseTeacherRef,
      courseCreditRef,
      courseCreditHourRef,
      courseCapacityRef,
      courseDescriptionRef,

      /*Dialog Methods*/
      updateDialog,
      async submit() {
        /*Validate */
        if (!courseIdRef.value || !courseNameRef.value || !courseTimeDayRef.value || !courseTimeStartRef.value || !courseTimeEndRef.value || !coursePlaceRef.value || !courseTeacherRef.value || !courseCreditRef.value || !courseCreditHourRef.value || !courseCapacityRef.value || !courseDescriptionRef.value) {
          return;
        }
        courseIdRef.value.validate();
        courseNameRef.value.validate()
        courseTimeDayRef.value.validate()
        courseTimeStartRef.value.validate()
        courseTimeEndRef.value.validate()
        coursePlaceRef.value.validate()
        courseTeacherRef.value.validate()
        courseCreditRef.value.validate()
        courseCreditHourRef.value.validate()
        courseCapacityRef.value.validate()
        courseDescriptionRef.value.validate()
        if (courseIdRef.value.hasError || courseNameRef.value.hasError || courseTimeDayRef.value.hasError || courseTimeStartRef.value.hasError || courseTimeEndRef.value.hasError || coursePlaceRef.value.hasError || courseTeacherRef.value.hasError || courseCreditRef.value.hasError || courseCreditHourRef.value.hasError || courseCapacityRef.value.hasError || courseDescriptionRef.value.hasError) {
          return;
        }

        /*Data cleansing */
        const editTime = courseTimeDay.value.concat(
          ' : ',
          courseTimeStart.value,
          ' - ',
          courseTimeEnd.value
        );

        const teacher = computed(() => {
          if (adminAdd.value) {
            const teacherInfo = courseTeacher.value as TeacherInfo;
            console.log(teacherInfo.label);
            return teacherInfo.label;
          } else {
            return courseTeacher.value;
          }
        })

        const applicationType = computed(() => {
          if (teacherAdd.value) {
            return '1';
          } else if(teacherEdit.value) {
            return '3';
          } else if (adminCheck.value) {
            return props.application?.applicationType
          } else {
            return '';
          }
        })
        const courseInfo: CourseInfo = {
          courseId: courseId.value,
          courseName: courseName.value,
          courseTime: editTime,
          courseTimeDay: courseTimeDay.value,
          courseTimeStart: courseTimeStart.value,
          courseTimeEnd: courseTimeEnd.value,
          coursePlace: coursePlace.value,
          courseTeacher: String(teacher.value),
          courseMajor: '',
          courseDepartment: '',
          courseCredit: courseCredit.value,
          courseCreditHour: courseCreditHour.value,
          courseCapacity: courseCapacity.value,
          courseDescription: courseDescription.value,
          allowMajor: '',
          full: false
        };

        const applicationInfo: CourseApplicationInfo = {
          courseId: courseId.value,
          courseName: courseName.value,
          courseTime: editTime,
          courseTimeDay: courseTimeDay.value,
          courseTimeStart: courseTimeStart.value,
          courseTimeEnd: courseTimeEnd.value,
          coursePlace: coursePlace.value,
          courseTeacher: String(teacher.value),
          courseCredit: courseCredit.value,
          courseCreditHour: courseCreditHour.value,
          courseCapacity: courseCapacity.value,
          courseDescription: courseDescription.value,
          applicationType: String(applicationType.value),
          applicationTime: applicationTime.value,
          applicantName: applicantName.value,
          applicantNumber: applicantNumber.value,
          applicationStatus: applicationStatus.value,
          applicationId: applicationId.value,
        }

        /*Data submit */
        //CourseList
        if (adminAdd.value) {
          if(await course.add_course({courseInfo})) updateDialog();
        }else if (adminEdit.value) {
          if(await course.edit_course({courseInfo})) updateDialog();
        }
        //CourseListTeacher
        if (teacherAdd.value || teacherEdit.value) {
          if(await course.send_course_application({applicationInfo})) updateDialog();
        }
        //CourseListApplication

        /*Handle CourseApplication */
        if (adminCheck.value) {
          if (applicationStatus.value === '1') {
            if (applicationType.value === '新增'){
              await course.add_course({courseInfo});
            }
            else if (applicationType.value === '修改'){
              await course.edit_course({courseInfo});
            }
            else if (applicationType.value === '删除'){
              await course.delete_course(courseId.value, String(courseTeacher.value));

            }
          }
          await course.update_course_application_status({applicationInfo});
          updateDialog();
        }




      },
      autofill,
      clear,
    };
  },
});
</script>
