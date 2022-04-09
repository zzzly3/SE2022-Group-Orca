<template>
  <div class="q-pa-md">
    <q-table
      title="申请列表"
      :rows="rows"
      :columns="columns"
      row-key="courseId"
      v-model:pagination="pagination"
    >
      <template v-slot:header="props">
        <q-tr :props="props">
          <q-th auto-width />
          <q-th v-for="col in props.cols" :key="col.name" :props="props">
            {{ col.label }}
          </q-th>
          <q-th auto-width />
          <q-th auto-width />
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
          <!--CourseEditor-->
          <q-td>
            <div class="row">
              <q-btn
                label="查看"
                color="primary"
                @click="editShow[props.rowIndex] = true"
              ></q-btn>

              <q-dialog v-model="editShow[props.rowIndex]">
                <q-card style="width: 600px" class="q-pa-lg">
                  <q-card-section>
                    <q-form class="q-gutter-md row items-start">
                      <q-field disable style="width: 450px">
                        <template v-slot:control>
                          <div class="text-subtitle1 self-center full-width">
                            课程编号：{{ props.row.courseId }}
                          </div>
                        </template>
                      </q-field>

                      <q-input
                        style="width: 450px"
                        label="课程名称"
                        v-model="props.row.courseName"
                        disable
                      />
                      <q-select
                        style="width: 200px"
                        v-model="props.row.courseTimeDay"
                        label="上课时间"
                        disable
                      />
                      <q-input
                        v-model="props.row.courseTimeStart"
                        type="time"
                        disable
                      />
                      <q-field borderless readonly>
                        <template v-slot:control>
                          <div class="self-center full-width no-outline">
                            至
                          </div>
                        </template>
                      </q-field>
                      <q-input
                        v-model="props.row.courseTimeEnd"
                        type="time"
                        disable
                      />
                      <q-input
                        v-model="props.row.coursePlace"
                        style="width: 450px"
                        label="上课教室"
                        disable
                      />
                      <q-input
                        v-model="props.row.courseTeacher"
                        style="width: 180px"
                        label="任课教师"
                        disable
                      />

                      <q-select
                        disable
                        v-model="props.row.courseDepartment"
                        style="width: 260px"
                        label="开课院系"
                      />
                      <q-input
                        v-model="props.row.courseCredit"
                        style="width: 140px"
                        label="学分"
                        disable
                      />
                      <q-input
                        v-model="props.row.courseCreditHour"
                        style="width: 140px"
                        label="学时"
                        disable
                      />

                      <q-input
                        v-model="props.row.courseCapacity"
                        style="width: 140px"
                        label="课程容量"
                        disable
                      />

                      <q-input
                        v-model="props.row.courseDescription"
                        style="width: 450px"
                        label="课程描述"
                        disable
                      />
                    </q-form>
                  </q-card-section>
                  <q-card-section align="right">
                    <q-btn
                      color="primary"
                      flat
                      @click="agree(props.row)"
                      label="同意申请"
                      v-close-popup
                    />
                    <q-btn
                      color="red"
                      flat
                      @click="decline(props.row)"
                      label="拒绝申请"
                      v-close-popup
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

      <template v-slot:top-right>
        <CourseSearcher></CourseSearcher>
      </template>
    </q-table>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';
import { CourseApplicationInfo, useCourseStore } from 'stores/course';
//import CourseAdder from 'components/course/CourseAdder.vue';
//import CourseEditor from 'components/course/CourseEditor.vue';
import CourseSearcher from 'components/course/CourseSearcher.vue';

const columns = [
  {
    name: 'courseId',
    required: true,
    label: '课程编号',
    align: 'left',
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
  components: { /*CourseAdder, CourseEditor, */ CourseSearcher },
  setup() {
    const course = useCourseStore();
    const rows = ref([] as CourseApplicationInfo[]);

    const pagination = ref({
      sortBy: 'courseId',
      descending: false,
      page: 5,
      rowsPerPage: 5,
      rowsNumber: 1,
    });
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
      pagination,

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
