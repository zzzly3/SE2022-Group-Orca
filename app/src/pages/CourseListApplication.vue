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
          <q-td>
              <q-btn color="primary" flat icon="visibility" @click="editShow[props.rowIndex] = true"/>
              <CourseDialog v-model="editShow[props.rowIndex]" @user_update="load" :application="props.row" :isAdmin="true" :isAdd="false" :isEdit="false"/>
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
  components: { CourseDialog },
  setup() {
    const course = useCourseStore();
    const rows = ref([] as CourseApplicationInfo[]);

    const editShow = ref([false] as boolean[]);
    const load = async () => {
      const r = await course.load_course_application_lists_page_admin();
      rows.value = r;
    };
    load()

    return {
      columns,
      rows,
      editShow,
      load,
    };
  },
});
</script>
