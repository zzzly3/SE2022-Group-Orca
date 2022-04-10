<template>
  <div class="q-pa-md column items-center">
    <q-table
      flat
      style="width: 80%"
      title="课程信息"
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
            <div>
              <q-btn
                style="width: 10px"
                flat
                color="teal-10"
                size="sm"
                :icon="'edit'"
                @click="editShow[props.rowIndex] = true"
              />
              <q-dialog v-model="editShow[props.rowIndex]">
                <q-card style="width: 600px" class="q-pa-lg">
                  <q-card-section class="row">
                    <q-icon name="edit" size="md" />
                  </q-card-section>
                  <q-card-section>
                    <q-form class="q-gutter-md row items-start">
                      <q-field readonly style="width: 450px">
                        <template v-slot:control>
                          <div
                            class="text-subtitle1 self-center full-width no-outline"
                          >
                            课程编号：{{ props.row.courseId }}
                          </div>
                        </template>
                      </q-field>
                      <q-input
                        style="width: 450px"
                        label="课程名称"
                        v-model="props.row.courseName"
                      />
                      <q-select
                        style="width: 200px"
                        v-model="props.row.courseTimeDay"
                        :options="weekdays"
                        label="上课时间"
                      />
                      <q-input
                        v-model="props.row.courseTimeStart"
                        style="width: 105px"
                        type="time"
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
                        style="width: 105px"
                        type="time"
                      />
                      <q-input
                        v-model="props.row.coursePlace"
                        style="width: 450px"
                        label="上课教室"
                      />
                      <q-input
                        v-model="props.row.courseTeacher"
                        style="width: 180px"
                        label="任课老师"
                      />
                      <q-select
                        style="width: 250px"
                        v-model="props.row.courseDepartment"
                        :options="departments"
                        label="开课院系"
                      />
                      <q-input
                        v-model="props.row.courseCredit"
                        style="width: 140px"
                        label="学分"
                      />
                      <q-input
                        v-model="props.row.courseCreditHour"
                        style="width: 140px"
                        label="学时"
                      />
                      <q-input
                        v-model="props.row.courseCapacity"
                        style="width: 140px"
                        label="课程容量"
                      />
                      <q-input
                        v-model="props.row.courseDescription"
                        style="width: 450px"
                        autogrow
                        label="课程描述"
                      />
                    </q-form>
                  </q-card-section>
                  <q-card-section align="right">
                    <q-btn
                      color="red"
                      flat
                      @click="editShow[props.rowIndex] = false"
                      label="取消"
                    />
                    <q-btn
                      color="teal-10"
                      flat
                      @click="editSubmit(props.row)"
                      label="确定"
                      v-close-popup
                    />
                  </q-card-section>
                </q-card>
              </q-dialog>
            </div>
          </q-td>
          <!--CourseEditor-->
          <!-- courseDelete -->
          <q-td>
            <div>
              <q-btn
                flat
                color="red"
                size="sm"
                :icon="'close'"
                @click="deleteShow[props.rowIndex] = true"
              />
              <q-dialog v-model="deleteShow[props.rowIndex]">
                <q-card style="width: 300px" class="q-pa-md">
                  <q-card-section>
                    <div
                      class="text-subtitle1 self-center full-width no-outline"
                    >
                      确定删除该课程？
                    </div>
                  </q-card-section>
                  <q-separator />
                  <q-card-section align="right">
                    <q-btn
                      color="red"
                      flat
                      @click="deleteShow[props.rowIndex] = false"
                      label="取消"
                    />
                    <q-btn
                      color="teal-10"
                      flat
                      @click="deleteCourse(props.row.courseId)"
                      label="确定"
                      v-close-popup
                    />
                  </q-card-section>
                </q-card>
              </q-dialog>
            </div>
          </q-td>
          <!-- courseDelete -->
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
        <!--CourseAdder-->
        <div>
          <q-btn flat icon="add" @click="addShow = true" />
          <q-dialog v-model="addShow">
            <q-card style="width: 600px" class="q-pa-lg">
              <q-card-section>
                <q-icon name="add" size="md" />
              </q-card-section>
              <q-card-section>
                <q-form class="q-gutter-md row items-start">
                  <q-input
                    style="width: 500px"
                    label="课程编号"
                    v-model="addCourseId"
                  />
                  <q-input
                    style="width: 500px"
                    label="课程名称"
                    v-model="addCourseName"
                  />
                  <q-select
                    style="width: 200px"
                    v-model="addCourseTimeDay"
                    :options="weekdays"
                    label="上课时间"
                  />
                  <q-input
                    style="width: 105px"
                    v-model="addCourseTimeStart"
                    type="time"
                  />
                  <q-field borderless readonly>
                    <template v-slot:control>
                      <div class="self-center full-width no-outline">至</div>
                    </template>
                  </q-field>
                  <q-input
                    style="width: 105px"
                    v-model="addCourseTimeEnd"
                    type="time"
                  />
                  <q-input
                    v-model="addCoursePlace"
                    style="width: 500px"
                    label="上课教室"
                  />
                  <q-input
                    v-model="addCourseTeacher"
                    style="width: 180px"
                    label="任课老师"
                  />
                  <q-select
                    style="width: 280px"
                    v-model="addCourseDepartment"
                    :options="departments"
                    label="开课院系"
                  />
                  <q-input
                    v-model="addCourseCredit"
                    style="width: 140px"
                    label="学分"
                  />
                  <q-input
                    v-model="addCourseCreditHour"
                    style="width: 140px"
                    label="学时"
                  />
                  <q-input
                    v-model="addCourseCapacity"
                    style="width: 150px"
                    label="课程容量"
                  />
                  <q-input
                    v-model="addCourseDescription"
                    style="width: 500px"
                    autogrow
                    label="课程描述"
                  />
                </q-form>
              </q-card-section>

              <q-card-section>
                <q-card-actions align="right">
                  <q-btn
                    flat
                    label="取消"
                    @click="clear"
                    color="red"
                    v-close-popup
                  />
                  <q-btn
                    flat
                    label="添加"
                    @click="addSubmit"
                    color="primary"
                    v-close-popup
                  />
                </q-card-actions>
              </q-card-section>
            </q-card>
          </q-dialog>
        </div>
        <!--CourseAdder-->
        <!--BatchImport-->
        <div>
          <q-btn flat icon="ios_share" @click="importShow = true" />
          <q-dialog v-model="importShow">
            <q-uploader
              url="/api/course/batch_import"
              label="选择文件"
              auto-upload
              auto-expand
              :field-name="'file'"
            />
          </q-dialog>
        </div>
        <!--BatchImport-->
      </template>
    </q-table>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';
import { CourseInfo, useCourseStore } from 'stores/course';
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

export default defineComponent({
  name: 'CourseList',
  components: { /*CourseAdder, CourseEditor, */ CourseSearcher },

  setup() {
    const course = useCourseStore();
    const rows = ref([] as CourseInfo[]);
    const pagination = ref({
      sortBy: 'courseId',
      descending: false,
      page: 5,
      rowsPerPage: 5,
      rowsNumber: 1,
    });

    course.load_course_lists_page_admin().then((r) => (rows.value = r));

    //BatchImport start
    const importShow = ref(false);
    const file = ref(null);
    //BatchImport end

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

    const deleteShow = ref([false] as boolean[]);
    return {
      columns,
      rows,
      pagination,

      //CourseAdder start
      addShow,
      weekdays,
      departments,

      addCourseId,
      addCourseName,
      addCourseTime,
      addCourseTimeDay,
      addCourseTimeStart,
      addCourseTimeEnd,
      addCoursePlace,
      addCourseTeacher,
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
          await course.add_course({
            courseId: addCourseId.value,
            courseName: addCourseName.value,
            courseTime: addTime,
            courseTimeDay: addCourseTimeDay.value,
            courseTimeStart: addCourseTimeStart.value,
            courseTimeEnd: addCourseTimeEnd.value,
            coursePlace: addCoursePlace.value,
            courseTeacher: addCourseTeacher.value,
            courseDepartment: addCourseDepartment.value,
            courseCredit: addCourseCredit.value,
            courseCreditHour: addCourseCreditHour.value,
            courseCapacity: addCourseCapacity.value,
            courseDescription: addCourseDescription.value,
          })
        ) {
          await course.load_course_lists_page_admin();
          rows.value = course.course_list;
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
          await course.edit_course({
            courseId: row.courseId,
            courseName: row.courseName,
            courseTime: editTime,
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
          })
        ) {
          await course.load_course_lists_page_admin();
          rows.value = course.course_list;
          clear();
        }
      },
      //CourseEditor end
      //CourseDeleter start
      deleteShow,

      async deleteCourse(courseId: string) {
        await course.delete_course(courseId);
        await course.load_course_lists_page_admin();
        rows.value = course.course_list;
      },
      //CourseDeleter end
      //BatchImport start
      importShow,
      file,

      //BatchImport end
    };
  },
});
</script>
