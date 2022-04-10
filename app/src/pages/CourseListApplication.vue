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
          <q-th auto-width />
        </q-tr>
      </template>

      <template v-slot:body="props">
        <q-tr :props="props">
          <q-td auto-width>
            <q-btn size="sm" flat
              @click="props.expand = !props.expand"
              :icon="props.expand ? 'expand_less' : 'expand_more'"
            />
          </q-td>
          <q-td v-for="col in props.cols" :key="col.name" :props="props">
            {{ col.value }}
          </q-td>
          <!--CourseEditor-->
          <q-td>
            <div class="row">
              <q-btn label="查看" color="primary"
                @click="editShow[props.rowIndex] = true"
              />
              <q-dialog v-model="editShow[props.rowIndex]">
                <q-card style="width: 450px" class="q-px-md q-gutter-y-md">
                  <q-card-section>
                    <q-form style="width: 350px" class="q-gutter-y-md">
                      <q-input class="col" disable label="课程编号" v-model="props.row.courseId"/>
                      <q-input class="col" disable label="课程名称" v-model="props.row.courseName"/>
                      <div class="row items-start q-gutter-md">
                        <q-select class="col" disable v-model="props.row.courseTimeDay" label="上课时间"/>
                        <q-input class="col" disable v-model="props.row.courseTimeStart" type="time"/>
                        <q-field borderless readonly>
                          <template v-slot:control>
                            <div class="self-center full-width no-outline">
                              至
                            </div>
                          </template>
                        </q-field>
                        <q-input class="col" disable v-model="props.row.courseTimeEnd" type="time"/>
                      </div>
                      <div class="row items-start q-gutter-md">
                        <q-input class="col" disable v-model="props.row.coursePlace" label="上课教室"/>
                        <q-input class="col" disable v-model="props.row.courseTeacher" label="任课老师"/>
                      </div>
                      <div class="row items-start q-gutter-md">
                        <q-select class="col" disable v-model="props.row.courseMajor" label="所属专业"/>
                        <q-select class="col" disable v-model="props.row.courseDepartment" label="开课院系"/>
                      </div>
                      <div class="row items-start q-gutter-md">
                        <q-input class="col" disable v-model="props.row.courseCredit" label="学分"/>
                        <q-input class="col" disable v-model="props.row.courseCreditHour" label="学时"/>
                        <q-input class="col" disable v-model="props.row.courseCapacity" label="课程容量"/>
                      </div>
                      <q-input class="col" disable v-model="props.row.courseDescription" autogrow label="课程描述"/>
                    </q-form>
                  </q-card-section>
                  <q-card-section align="right">
                    <q-btn color="primary" flat label="同意申请" v-close-popup
                      @click="agree(props.row)"
                    />
                    <q-btn color="red" flat label="拒绝申请" v-close-popup
                      @click="decline(props.row)"
                    />
                  </q-card-section>
                </q-card>
              </q-dialog>
            </div>
          </q-td>
          <!--CourseEditor-->
        </q-tr>
        <q-tr v-show="props.expand" :props="props">
          <q-td></q-td>
          <q-td colspan="100%">
            <div class="align">{{ props.row.courseDescription }}.</div>
          </q-td>
        </q-tr>
      </template>

      <template v-slot:top-right> </template>
    </q-table>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';
import { CourseApplicationInfo, useCourseStore } from 'stores/course';

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
    name: 'applicantName',
    align: 'center',
    label: '申请者姓名',
    field: 'applicantName',
  },
  {
    name: 'applicantNumber',
    align: 'center',
    label: '申请者工号',
    field: 'applicationNumber',
  },
  {
    name: 'applicationTime',
    align: 'center',
    label: '申请时间',
    field: 'applicationTime',
  },
  {
    name: 'applicationType',
    align: 'center',
    label: '申请类型',
    field: 'applicationType',
  },
];

export default defineComponent({
  name: 'CourseListApplication',
  components: {},
  setup() {
    const course = useCourseStore();
    const rows = ref([] as CourseApplicationInfo[]);
    course
      .load_course_application_lists_page_admin()
      .then((r) => (rows.value = r));

    //CourseAdder end

    //CourseEditor start
    const editShow = ref([false] as boolean[]);
    //CourseEditor end

    return {
      columns,
      rows,

      //CourseEditor start
      editShow,
      async agree(row: CourseApplicationInfo) {
        if (row.applicationType === '新增') {
          await course.add_course({
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
          });
        } else if (row.applicationType === '修改') {
          await course.edit_course({
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
          });
        } else if (row.applicationType === '删除') {
          await course.delete_course(row.courseId);
        }

        await course.update_course_application_status({
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
          applicationType: row.applicationType,
          applicationTime: row.applicationTime,
          applicantName: row.applicantName,
          applicantNumber: row.applicantNumber,
          applicationId: row.applicationId,
          applicationStatus: '1',
        });
        await course
          .load_course_application_lists_page_admin()
          .then((r) => (rows.value = r));
      },
      async decline(row: CourseApplicationInfo) {
        console.log(row);
        await course.update_course_application_status({
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
          applicationType: row.applicationType,
          applicationTime: row.applicationTime,
          applicantName: row.applicantName,
          applicantNumber: row.applicantNumber,
          applicationStatus: '2',
          applicationId: row.applicationId,
        });
        await course
          .load_course_application_lists_page_admin()
          .then((r) => (rows.value = r));
      },
    };
  },
});
</script>
