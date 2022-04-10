<template>
  <q-layout view="hhh lpR fFf">
    <q-page-container>
      <q-separator />
      <q-list class="row">
        <q-item clickable v-ripple class="icon-box" v-for="(icon, index) in icons"
               :key="icon.id" @click="onClick(index)">

          <q-item-section side>
            <q-img class="icon-bg" :src="icon.url"></q-img>
          </q-item-section>

          <q-item-section main>
            <q-item-label>{{icon.text}}</q-item-label>
          </q-item-section>
        </q-item>
      </q-list>
      <ClassroomAdjuster ref="ref0" class="q-ml-lg"></ClassroomAdjuster>
      <ClassTimeAdjuster ref="ref1" class="q-ml-lg"></ClassTimeAdjuster>
      <CourseSelectionStateAdjuster ref="ref2" class="q-ml-lg"></CourseSelectionStateAdjuster>
    </q-page-container>
  </q-layout>

</template>

<script lang="ts">
import {defineComponent, ref} from 'vue';
import ClassroomAdjuster from 'components/ClassroomAdjuster.vue';
import ClassTimeAdjuster from 'components/ClassTimeAdjuster.vue';
import {QValidate} from 'components/models';
import CourseSelectionStateAdjuster from 'components/CourseSelectionStateAdjuster.vue';

export default defineComponent({
  name: 'ManagePage',
  components: {CourseSelectionStateAdjuster, ClassroomAdjuster, ClassTimeAdjuster},
  setup(){
    const ref0 = ref<QValidate|null>(null)
    const ref1 = ref<QValidate|null>(null)
    const ref2 = ref<QValidate|null>(null)

    const icons = [
      {id:0, url:require('../assets/icon/classroom-adjust.png'),
        text:'上课教室调整'},
      {id:1, url:require('../assets/icon/classTime-adjust.png'),
        text:'上课时间调整'},
      {id:2, url:require('../assets/icon/open-course-selection.png'),
        text:'开/关选课功能'}
    ]
    const onClick = (id: number) =>{
      funcTable[id]();
    }

    const showClassroomAdjuster=()=> {
      console.log('in ClassroomAdjuster')
      if(!ref0.value) return
      (ref0.value as any).show = true
    }
    const showClassTimeAdjuster=()=> {
      console.log('in ClassTimeAdjuster')
      if(!ref1.value) return
      (ref1.value as any).show = true
    }
    const showCourseSelectionStateAdjuster=()=>{
      console.log('in CourseSelectionStateAdjuster')
      if(!ref2.value)return
      (ref2.value as any).show = true
    }
    const funcTable = [
      showClassroomAdjuster,
      showClassTimeAdjuster,
      showCourseSelectionStateAdjuster
    ]
    return {
      icons,
      onClick,
      ref0, ref1, ref2
    };
  },
});
</script>
