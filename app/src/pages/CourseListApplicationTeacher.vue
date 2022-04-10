<template>
  <div class="q-pa-md column items-center">
    <q-table
      flat
      style="width: 80%"
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
  {
    name: 'applicationStatus',
    align: 'center',
    label: '申请状态',
    field: 'applicationStatus',
  },
];

export default defineComponent({
  name: 'CourseListApplication',
  components: {},
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
      .load_course_application_lists_page_teacher()
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
      //CourseEditor end
    };
  },
});
</script>
