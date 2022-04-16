<template>
  <div class="q-pa-md column items-center">
    <q-table
      flat
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
          <q-th  />
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
          <q-td auto-width>
            <q-btn-group spread flat>
                <q-btn flat  color="teal-10" style="width: 5px" size="sm" :icon="'edit'" @click="editShow[props.rowIndex] = true"/>
                <q-btn flat color="red" style="width: 5px" size="sm" :icon="'close'"
                @click="deleteShow[props.rowIndex] = true"/>
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
                  <q-form style="width: 400px" class="q-px-md q-gutter-y-sm">
                    <q-input style="height: 53px" class="col" dense disable label="课程编号" v-model="props.row.courseId"/>
                    <q-input style="height: 60px" class="col" dense label="课程名称" v-model="props.row.courseName"
                    lazy-rules :rules="[val => rules.courseName.test(val) || '无效的课程名称']" ref="editCourseNameRef"/>
                    <div class="row items-start q-gutter-md">
                      <q-select style="height: 60px" class="col" dense v-model="props.row.courseTimeDay" :options="weekdays" label="上课时间"/>
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
                      <q-select disable class="col" dense v-model="props.row.courseTeacher" label="任课老师"/>
                      <q-select style="height: 63px" class="col-5" dense v-model="props.row.coursePlace" :options="classrooms" label="上课教室"/>
                    </div>
                    <div class="row items-start q-gutter-md">
                      <q-select style="height: 56px" class="col" dense v-model="props.row.courseCredit" :options="credits" label="学分"/>
                      <q-select class="col" dense v-model="props.row.courseCreditHour" :options="creditHours" label="学时"/>
                      <q-input class="col" dense v-model="props.row.courseCapacity" label="课程容量"
                      lazy-rules :rules="[val => rules.courseCapacity.test(val) || '无效的课程容量']" ref="editCourseCapacityRef">
                        <template v-slot:append>
                          <q-icon name="arrow_drop_down" class="cursor-pointer" >
                          </q-icon>
                        </template>
                        <q-menu fit v-model="showMenuEdit">
                          <q-list style="min-width: 100px">
                            <q-item clickable @click="menuE1(props.rowIndex)">
                              <q-item-section>10</q-item-section>
                            </q-item>
                            <q-item clickable @click="menuE2(props.rowIndex)">
                              <q-item-section>20</q-item-section>
                            </q-item>
                            <q-item clickable @click="menuE3(props.rowIndex)">
                              <q-item-section>30</q-item-section>
                            </q-item>
                            <q-item clickable @click="menuE4(props.rowIndex)">
                              <q-item-section>40</q-item-section>
                            </q-item>
                            <q-item clickable @click="menuE5(props.rowIndex)">
                              <q-item-section>50</q-item-section>
                            </q-item>
                          </q-list>
                        </q-menu>
                      </q-input>
                    </div>
                    <q-input class="col" dense v-model="props.row.courseDescription" autogrow label="课程描述"
                    lazy-rules :rules="[val => !!val || '课程描述不能为空']" ref="editCourseDescriptionRef"/>
                  </q-form>
                </q-card-section>
                <q-card-section>
                  <q-card-actions align="right">
                    <q-btn color="red" flat @click="editShow[props.rowIndex] = false" label="取消"/>
                    <q-btn color="teal-10" flat @click="editSubmit(props.row, props.rowIndex)" label="确认修改"/>
                  </q-card-actions>
                </q-card-section>
              </q-card>
            </q-dialog>
            <!--CourseEditor-->
            <!--CourseDelete-->
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
                    @click="deleteCourse(props.row.courseId, props.row.courseTeacher)"/>
                </q-card-section>
              </q-card>
            </q-dialog>
            <!--CourseDelete-->
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
            <q-card style="width: 460px" >
              <q-card-section>
                <div style="height: 50px" class="text-subtitle1 row items-center">
                  <q-icon name="add" size="sm"></q-icon>
                  <span>新增课程</span>
                </div>
              </q-card-section>
              <q-card-section class="q-py-none">
                <q-form style="width: 400px" class="q-px-md q-gutter-y-sm">
                  <q-input style="height: 53px" class="col" label="课程编号" dense v-model="addCourseId"
                  lazy-rules :rules="[val => rules.courseId.test(val) || '无效的课程编号']" ref="addCourseIdRef"/>
                  <q-input style="height: 53px" class="col" label="课程名称" dense v-model="addCourseName"
                  lazy-rules :rules="[val => rules.courseName.test(val) || '无效的课程名称']" ref="addCourseNameRef"/>
                  <div class="row items-start q-gutter-md">
                    <q-select  class="col" dense v-model="addCourseTimeDay" :options="weekdays" label="上课时间"
                    lazy-rules :rules="[val => !!val || '上课时间不能为空']" ref="addCourseTimeDayRef"/>
                    <q-select  class="col" dense v-model="addCourseTimeStart" :options="courseTimeStarts" label="开始时间"
                    lazy-rules :rules="[val => !!val || '课程开始时间不能为空']" ref="addCourseTimeStartRef"/>
                    <q-field  borderless disable dense>
                      <template v-slot:control>
                        <div class="self-center full-width no-outline">至</div>
                      </template>
                    </q-field>
                    <q-select  class="col" dense v-model="addCourseTimeEnd" :options="courseTimeEnds" label="结束时间"
                    lazy-rules :rules="[val => !!val || '课程结束时间不能为空']" ref="addCourseTimeEndRef"/>
                  </div>
                  <div class="row items-start q-gutter-md">
                    <q-select  class="col" dense v-model="addCourseTeacher" :options="teachers" label="任课老师"
                    lazy-rules :rules="[val => !!val || '课程任课老师不能为空']" ref="addCourseTeacherRef">
                      <template v-slot:option="scope">
                        <q-item v-bind="scope.itemProps">
                          <q-item-section>
                            <q-item-label>{{ scope.opt.label }}</q-item-label>
                            <q-item-label caption>{{ scope.opt.description }}</q-item-label>
                          </q-item-section>
                        </q-item>
                      </template>
                    </q-select>
                    <q-select  class="col-5" dense v-model="addCoursePlace" :options="classrooms" label="上课教室"
                    lazy-rules :rules="[val => !!val || '课程教室不能为空']" ref="addCoursePlaceRef"/>
                  </div>
                  <div class="row items-start q-gutter-md">
                    <q-select style="height: 56px" class="col" dense v-model="addCourseCredit" :options="credits" label="学分"
                    lazy-rules :rules="[val => !!val || '课程学分不能为空']" ref="addCourseCreditRef"/>
                    <q-select class="col" dense v-model="addCourseCreditHour" :options="creditHours" label="学时"
                    lazy-rules :rules="[val => !!val || '课程学时不能为空']" ref="addCourseCreditHourRef"/>
                    <q-input class="col" dense v-model="addCourseCapacity" label="课程容量"
                    lazy-rules :rules="[val => rules.courseCapacity.test(val) || '无效的课程容量']" ref="addCourseCapacityRef">
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
                  <q-btn flat label="取消"  @click="clear" color="red" v-close-popup/>
                  <q-btn flat label="确认添加"  @click="addSubmit" color="primary"/>
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
            <q-card>
              <q-card-section>
                <div style="height: 50px" class="text-subtitle1 row items-center q-gutter-md">
                    <q-icon name="ios_share" size="sm"></q-icon>
                    <div class="text-h6">文件上传</div>
                </div>
              </q-card-section>
              <q-separator />

              <q-card-section style="max-height: 50vh" class="scroll">
                <p>接受以csv文件批量导入课程数据。文件中应包括课程编号(大写字母+数字如EM499)，课程名称（只包含英文中文），上课时间（如周日），课程开始时间（如08:00:00），课程结束时间(如09:40:00)，上课教室，任课教师工号，学分，学时，课程容量，课程描述。各项之间以逗号分隔，各项应按上述顺序排列且不为空。文件无需包含标题行。
                </p>
              </q-card-section>
              <q-card-section>
                <q-form class="q-px-md" style="width: 350px">
                  <div class="row">
                    <p class="col-auto q-pr-md" style="line-height: 40px">
                      选择csv文件
                    </p>
                    <q-file
                      class="col"
                      v-model="file"
                      clearable
                      outlined
                      dense
                      accept=".csv"
                      color="primary"
                    />
                  </div>
                </q-form>
              </q-card-section>
              <q-separator />

              <q-card-actions align="right">
                <q-btn flat label="取消" color="negative" @click="file=null" v-close-popup />
                <q-btn flat label="确定" color="primary" @click="upload_csv" />
              </q-card-actions>
            </q-card>
          </q-dialog>
          <q-uploader
            url="/api/course/batch_import"
            label="选择文件"
            :field-name="'file'"
            class = "hidden"
            ref="uploader"
            @uploaded="onuploaded"
          />
        </div>
        <!--BatchImport-->
      </template>
    </q-table>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed } from 'vue';
import { CourseInfo, useCourseStore } from 'stores/course';
import { QValidate } from 'src/components/models';
import { QUploader, useQuasar } from 'quasar';

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
    field: (rows: { courseTeacher: string; }) => rows.courseTeacher.split('(')[0],
  },
  {
    name: 'courseMajor',
    label: '课程专业',
    field: 'courseMajor',
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
    const rows = ref([] as CourseInfo[]);
    course.load_course_lists_page_admin().then((r) => (rows.value = r));
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

    //BatchImport start
    const importShow = ref(false);
    const file = ref<File|null>(null);
    const uploader = ref<QUploader|null>(null);
    //BatchImport end

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
    const addCourseTeacher = ref({label:'',value:'',description:''});
    const addCourseCredit = ref('');
    const addCourseCreditHour = ref('');
    const addCourseCapacity = ref('');
    const addCourseDescription = ref('');

    const addCourseIdRef = ref<QValidate|null>(null);
    const addCourseNameRef = ref<QValidate|null>(null);
    const addCourseTimeDayRef = ref<QValidate|null>(null);
    const addCourseTimeStartRef = ref<QValidate|null>(null);
    const addCourseTimeEndRef = ref<QValidate|null>(null);
    const addCoursePlaceRef = ref<QValidate|null>(null);
    const addCourseTeacherRef = ref<QValidate|null>(null);
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
      addCourseTeacher.value = {label:'',value:'',description:''};
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
    const editCourseNameRef = ref<QValidate|null>(null);
    const editCourseCapacityRef = ref<QValidate|null>(null);
    const editCourseDescriptionRef = ref<QValidate|null>(null);

    const editShow = ref([false] as boolean[]);
    const showMenuEdit = ref(false)
    const menuE1 = (index:number) => {
      rows.value[index].courseCapacity = '10'
      showMenuEdit.value = false
    }
    const menuE2 = (index:number) => {
      rows.value[index].courseCapacity = '20'
      showMenuEdit.value = false
    }
    const menuE3 = (index:number) => {
      rows.value[index].courseCapacity = '30'
      showMenuEdit.value = false
    }
    const menuE4 = (index:number) => {
      rows.value[index].courseCapacity = '40'
      showMenuEdit.value = false
    }
    const menuE5 = (index:number) => {
      rows.value[index].courseCapacity = '50'
      showMenuEdit.value = false
    }
    //CourseEditor end

    const deleteShow = ref([false] as boolean[]);
    const load = async () => {
      const r = await course.load_course_lists_page_admin();
      rows.value = r;
    };
    load();
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
      addCourseCredit,
      addCourseCreditHour,
      addCourseCapacity,
      addCourseDescription,

      addCourseIdRef,
      addCourseNameRef,
      addCourseTimeDayRef,
      addCourseTimeStartRef,
      addCourseTimeEndRef,
      addCoursePlaceRef,
      addCourseTeacherRef,
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
        if(!addCourseIdRef.value || !addCourseNameRef.value || !addCourseTimeDayRef.value || !addCourseTimeStartRef.value || !addCourseTimeEndRef.value || !addCoursePlaceRef.value || !addCourseTeacherRef.value ||  !addCourseCreditRef.value || !addCourseCreditHourRef.value || !addCourseCapacityRef.value || !addCourseDescriptionRef.value) {
          return;
        }

        addCourseIdRef.value.validate();
        addCourseNameRef.value.validate();
        addCourseTimeDayRef.value.validate();
        addCourseTimeStartRef.value.validate();
        addCourseTimeEndRef.value.validate();
        addCoursePlaceRef.value.validate();
        addCourseTeacherRef.value.validate();
        addCourseCreditRef.value.validate();
        addCourseCreditHourRef.value.validate();
        addCourseCapacityRef.value.validate();
        addCourseDescriptionRef.value.validate();
        if (addCourseIdRef.value.hasError || addCourseNameRef.value.hasError || addCourseTimeDayRef.value.hasError || addCourseTimeStartRef.value.hasError || addCourseTimeEndRef.value.hasError || addCoursePlaceRef.value.hasError || addCourseTeacherRef.value.hasError ||  addCourseCreditRef.value.hasError || addCourseCreditHourRef.value.hasError || addCourseCapacityRef.value.hasError || addCourseDescriptionRef.value.hasError) {
          return;
        }

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
            courseTeacher: addCourseTeacher.value.value,
            courseCredit: addCourseCredit.value,
            courseCreditHour: addCourseCreditHour.value,
            courseCapacity: addCourseCapacity.value,
            courseDescription: addCourseDescription.value,
          })
        ) {
          await course.load_course_lists_page_admin().then((r) => (rows.value = r));
          clear();
        }
      },
      //CourseAdder end

      //CourseEditor start
      editShow,
      showMenuEdit,
      editCourseNameRef,
      editCourseCapacityRef,
      editCourseDescriptionRef,
      menuE1,
      menuE2,
      menuE3,
      menuE4,
      menuE5,

      async editSubmit(row: CourseInfo, rowIndex: number) {
        if(!editCourseNameRef.value || !editCourseCapacityRef.value || !editCourseDescriptionRef.value) {
          return;
        }
        editCourseNameRef.value.validate()
        editCourseCapacityRef.value.validate()
        editCourseDescriptionRef.value.validate()
        if (editCourseNameRef.value.hasError || editCourseCapacityRef.value.hasError || editCourseDescriptionRef.value.hasError) {
          return;
        }

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
            courseCredit: row.courseCredit,
            courseCreditHour: row.courseCreditHour,
            courseCapacity: row.courseCapacity,
            courseDescription: row.courseDescription,
          })
        ) {
          await course.load_course_lists_page_admin().then((r) => (rows.value = r));
          editShow.value[rowIndex] = false;
          clear();
        }
      },
      //CourseEditor end
      //CourseDeleter start
      deleteShow,

      async deleteCourse(courseId: string, courseTeacher: string) {
        await course.delete_course(courseId, courseTeacher);
        await course.load_course_lists_page_admin();
        await course.load_course_lists_page_admin().then((r) => (rows.value = r));
      },
      //CourseDeleter end
      //BatchImport start
      importShow,
      file,
      uploader,
      load,
      upload_csv() {
        if (file.value === null){
          $q.notify({
            message: '请选择文件',
            color: 'negative',
          });
          return
        }
        if (uploader.value === null) {
          importShow.value = false;
          return
        }
        uploader.value.reset()
        uploader.value.addFiles([file.value])
        uploader.value.upload()
      },
      onuploaded({xhr}: {xhr: XMLHttpRequest}) {
        console.log(xhr)
        if (xhr.status === 200) {
          const r = JSON.parse(xhr.responseText)
          if (Number(r.code) === 200) {
            importShow.value=false;file.value=null;
            $q.notify({
              message: '导入成功',
              color: 'positive',
            });
            load()
          } else {
            $q.notify({color:'negative',message:r.msg})
          }
        } else {
          $q.notify({color:'negative',message:'网络异常'})
        }
      },

      //BatchImport end
    };
  },
});
</script>
