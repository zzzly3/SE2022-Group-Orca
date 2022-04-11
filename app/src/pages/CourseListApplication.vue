<template>
  <div class="q-pa-md column items-center">
    <q-table
      flat
      style="width: 80%"
      :rows="rows"
      :columns="columns"
      row-key="applicationId"
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
              <q-btn color="primary" flat icon="visibility"
                @click="editShow[props.rowIndex] = true"
              />
              <q-dialog v-model="editShow[props.rowIndex]">
                <q-card style="width: 460px">
                  <q-card-section>
                    <div style="height: 50px" class="text-subtitle1 row items-center">
                      <q-icon name="visibility" color="primary" size="sm"></q-icon>
                      <span>查看申请</span>
                    </div>
                  </q-card-section>
                  <q-card-section class="q-py-none">
                    <q-form style="width: 400px" class="q-px-md q-gutter-y-sm">
                      <q-input style="height: 53px" class="col" dense disable label="课程编号" v-model="props.row.courseId"/>
                      <q-input style="height: 60px" class="col" dense disable label="课程名称" v-model="props.row.courseName"/>
                      <div class="row items-start q-gutter-md">
                        <q-select style="height: 60px" class="col" dense disable v-model="props.row.courseTimeDay" label="上课时间"/>
                        <q-select class="col" dense disable v-model="props.row.courseTimeStart" label="开始时间"/>
                        <q-field borderless disable dense>
                          <template v-slot:control>
                            <div class="self-center full-width no-outline">
                              至
                            </div>
                          </template>
                        </q-field>
                        <q-select class="col" dense disable v-model="props.row.courseTimeEnd" label="结束时间"/>
                      </div>
                      <div class="row items-start q-gutter-md">
                        <q-select style="height: 63px" class="col" dense disable v-model="props.row.coursePlace" label="上课教室"/>
                        <q-select class="col" dense disable v-model="props.row.courseTeacher" label="任课老师"/>
                      </div>
                      <div class="row items-start q-gutter-md">
                        <q-select style="height: 56px" class="col" dense disable v-model="props.row.courseCredit" label="学分"/>
                        <q-select class="col" dense disable v-model="props.row.courseCreditHour" label="学时"/>
                        <q-input class="col" dense disable v-model="props.row.courseCapacity" label="课程容量">
                          <template v-slot:append>
                            <q-icon name="arrow_drop_down" class="cursor-pointer" />
                          </template>
                        </q-input>
                      </div>
                      <q-input class="col" dense disable v-model="props.row.courseDescription" autogrow label="课程描述">
                      </q-input>
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
    field: 'applicantNumber',
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
