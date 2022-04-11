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
      <ClassTimeViewer  ref="ref3" class="q-ml-lg"></ClassTimeViewer>
      <ClassroomViewer ref="ref4" class="q-ml-lg"></ClassroomViewer>
    </q-page-container>
  </q-layout>

</template>

<script lang="ts">
import {defineComponent, ref} from 'vue';
import ClassroomAdjuster from 'components/ClassroomAdjuster.vue';
import ClassTimeAdjuster from 'components/ClassTimeAdjuster.vue';
import CourseSelectionStateAdjuster from 'components/CourseSelectionStateAdjuster.vue';
import ClassTimeViewer from 'components/ClassTimeViewer.vue';
import ClassroomViewer from 'components/ClassroomViewer.vue';

export default defineComponent({
  name: 'ManagePage',
  components: {ClassroomViewer, ClassTimeViewer, CourseSelectionStateAdjuster, ClassroomAdjuster, ClassTimeAdjuster},
  setup(){
    const ref0 = ref<componentRef|null>(null)
    const ref1 = ref<componentRef|null>(null)
    const ref2 = ref<componentRef|null>(null)
    const ref3 = ref<componentRef|null>(null)
    const ref4 = ref<componentRef|null>(null)

    const icons = [
      {id:0, url:require('../assets/icon/classroom-adjust.png'),
        text:'上课教室调整'},
      {id:1, url:require('../assets/icon/classTime-adjust.png'),
        text:'上课时间调整'},
      {id:2, url:require('../assets/icon/open-close.png'),
        text:'开/关选课功能'},
      {id:3, url:require('../assets/icon/classroom-view.svg'),
        text:'上课时间查询'},
      {id:4, url:require('../assets/icon/open-classroom-search.png'),
        text:'开放教室查询'}
    ]

    interface componentRef{
      show: boolean;
      load: ()=>void;
    }

    const onClick = (id: number) =>{
      funcTable[id]();
    }

    const showClassroomAdjuster=()=> {
      console.log('in ClassroomAdjuster')
      if(!ref0.value)return
      ref0.value.show = true;
    }
    const showClassTimeAdjuster=()=> {
      console.log('in ClassTimeAdjuster');
      if(!ref1.value)return
      ref1.value.show = true
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
    const loadOpenClassroom=()=>{
      console.log('in loadOpenClassroom')
      if(!ref4.value)return
      ref4.value.load()
    }
    const funcTable = [
      showClassroomAdjuster,
      showClassTimeAdjuster,
      showCourseSelectionStateAdjuster,
      loadClassTimeTable,
      loadOpenClassroom
    ]
    return {
      icons,
      onClick,
      ref0, ref1, ref2, ref3, ref4
    };
  },
});
</script>
