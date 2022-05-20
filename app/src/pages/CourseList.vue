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
                <q-btn flat  color="teal-10" style="width: 5px" size="sm" :icon="'edit'" @click="editShow[props.rowIndex] = true; dialog_bind=props.row"/>
                <q-btn flat color="red" style="width: 5px" size="sm" :icon="'close'"
                @click="deleteShow[props.rowIndex] = true"/>
            </q-btn-group>
            <!--CourseEditor-->
            <CourseDialog v-model="editShow[props.rowIndex]" :row="dialog_bind" @user_update="load" :isAdmin='true' :isAdd="false" :isEdit="true"/>
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
        <q-btn flat icon="add" @click="addShow = true" />
        <CourseDialog v-model="addShow" :isAdmin='true' :isAdd="true" @user_update="load" />
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
import { defineComponent, ref} from 'vue';
import { CourseInfo, useCourseStore } from 'stores/course';
import { QUploader, useQuasar } from 'quasar';
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

export default defineComponent({
  name: 'CourseList',
  components: { CourseDialog},

  setup() {
    const $q = useQuasar();
    const course = useCourseStore();
    const rows = ref([] as CourseInfo[]);
    course.load_course_lists_page_admin().then((r) => (rows.value = r));
    course.load_course_constants()

    //BatchImport start
    const importShow = ref(false);
    const file = ref<File|null>(null);
    const uploader = ref<QUploader|null>(null);
    //BatchImport end

    const dialog_bind = ref<CourseInfo | undefined>(undefined);

    const editShow = ref([false] as boolean[]);
    const addShow = ref(false);
    const deleteShow = ref([false] as boolean[]);
    const load = async () => {
      const r = await course.load_course_lists_page_admin();
      rows.value = r;
    };
    load();
    return {
      columns,
      rows,

      dialog_bind,

      editShow,
      addShow,
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
