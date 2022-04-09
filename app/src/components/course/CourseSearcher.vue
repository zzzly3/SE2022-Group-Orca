<template>
  <div>
    <q-btn flat icon="search" @click="show = true" />
    <q-dialog v-model="show">
      <q-card style="width: 600px">
        <q-card-section style="max-height: 50vh" class="scroll">
          <q-form class="q-gutter-lg row items-start">
            <q-field borderless readonly>
              <template v-slot:control>
                <div class="self-center full-width no-outline">至</div>
              </template>
            </q-field>
          </q-form>
        </q-card-section>

        <q-separator />

        <q-card-actions align="right">
          <q-btn flat label="Decline" color="primary" v-close-popup />
          <q-btn flat label="Accept" color="primary" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';

const weekdays = [
  '星期一',
  '星期二',
  '星期三',
  '星期四',
  '星期五',
  '星期六',
  '星期日',
];

export default defineComponent({
  name: 'CourseSearcher',
  setup() {
    const show = ref(false);
    return {
      show,
      weekdays,
      courseId: ref(''),
    };
  },
});
</script>

<style scoped></style>
