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
<!--      <ClassroomAdjuster ref="ref0" class="q-ml-lg"></ClassroomAdjuster>-->
<!--      <ClassTimeAdjuster ref="ref1" class="q-ml-lg"></ClassTimeAdjuster>-->
      <CourseSelectionStateAdjuster ref="ref2" class="q-ml-lg"></CourseSelectionStateAdjuster>
      <ClassTimeViewer ref="ref3"></ClassTimeViewer>
      <ClassroomViewer ref="ref4"></ClassroomViewer>
    </q-page-container>
  </q-layout>

</template>

<script lang="ts">
import {defineComponent, ref} from 'vue';

import CourseSelectionStateAdjuster from 'components/CourseSelectionStateAdjuster.vue';
import ClassTimeViewer from 'components/ClassTimeViewer.vue';
import ClassroomViewer from 'components/ClassroomViewer.vue';

export default defineComponent({
  name: 'ManagePage',
  components: {ClassroomViewer, ClassTimeViewer, CourseSelectionStateAdjuster},
  setup(){
    const ref2 = ref<componentRef|null>(null)
    const ref3 = ref<componentRef|null>(null)
    const ref4 = ref<componentRef|null>(null)

    const icons = [
      {id:2, url:require('../assets/icon/open-close.png'),
        text:'开/关选课功能'},
      {id:3, url:require('../assets/icon/classroom-view.svg'),
        text:'上课时间查询'},
      {id:4, url:require('../assets/icon/open-classroom-search.png'),
        text:'教室查询'}
    ]

    interface componentRef{
      show: boolean;
      load: ()=>void;
    }

    const onClick = (index: number) =>{
      funcTable[index]();
    }

    const showCourseSelectionStateAdjuster=()=>{
      console.log('in showCourseSelectionStateAdjuster')
      //ref2.value.show = true
      if(!ref2.value)return
      ref2.value.load()
    }
    const loadClassTimeTable=()=>{
      console.log('in loadClassTimeTable')
      if(!ref3.value)return
      ref3.value.load()
    }
    const loadClassroom=()=>{
      console.log('in loadClassroom')
      if(!ref4.value)return
      ref4.value.load()
    }
    const funcTable = [
      showCourseSelectionStateAdjuster,
      loadClassTimeTable,
      loadClassroom
    ]
    return {
      icons,
      onClick,
      ref2, ref3, ref4
    };
  },
});
</script>
