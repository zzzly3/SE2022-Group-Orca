<template>
  <q-layout view="hhh lpR fff">

    <q-page-container>
      <q-page class="column items-center">
        <q-splitter
          v-model="splitterModel"
          unit="%"
          style="width: 800px; min-height: 500px;"
        >
          <template v-slot:before>
            <div class="q-py-md">
              <p class="text-h6 text-center text-basic">
                学院
              </p>
              <q-separator/>
              <q-list class="q-ml-md">
                <q-item clickable v-ripple v-for="c in college_list" :key="c.id" @click="select_college=c.id"
                        :class="c.id === select_college ? 'right-rounded bg-teal-1' : 'right-rounded'">
                  <q-item-section>
                    <div class="row full-width" >
                      <span class="col self-center text-left q-pl-md">
                        {{c.id}} &nbsp;&nbsp; {{c.name}}
                        <q-popup-edit v-model="c.name" auto-save v-slot="scope" :ref="'college' + c.id"
                                      @before-show="show_edit||$refs['college' + c.id][0].hide()" @before-hide="show_edit=false"
                                      :validate="val => !!val" @save="v=>update_college(c, v)">
                          <q-input v-model.trim="scope.value" dense autofocus @keyup.enter="scope.set" maxlength="20" clearable clear-icon="close"/>
                        </q-popup-edit>
                      </span>
                      <span class="float-right">
                        <q-btn icon="edit" color="primary" round flat size="sm"
                               @click.stop="show_edit=true;$refs['college' + c.id][0].show();"/>
                        <q-btn icon="close" color="negative" round flat size="sm" @click.stop="delete_college(c.id)"/>
<!--                        <q-btn icon="arrow_forward_ios" color="primary" round flat size="sm"/>-->
                      </span>
                    </div>
                  </q-item-section>
                </q-item>
                <q-separator/>
                <q-item clickable v-ripple>
                  <q-item-section>
                    <div class="row full-width">
                      <span class="col text-center q-px-md text-body2 text-positive self-center">
                        <q-icon name="add"/>添加学院
                        <q-popup-edit v-model="add_name" auto-save v-slot="scope"
                                      @hide="add_name=''"
                                      :validate="val => !!val" @save="add_college">
                          <q-input v-model.trim="scope.value" dense autofocus @keyup.enter="scope.set" maxlength="20" clearable clear-icon="close"/>
                        </q-popup-edit>
                      </span>
                    </div>
                  </q-item-section>
                </q-item>
              </q-list>
              <q-inner-loading
                :showing="left_loading"
                color="teal"
              />
            </div>

          </template>

          <template v-slot:after>
            <div class="q-py-md">
              <p class="text-h6 text-center text-basic">
                专业
              </p>
              <q-separator/>
              <q-markup-table flat class="text-center q-mr-md">
                <tbody>
                  <tr v-for="m in major_list" :key="m.id">
                    <td class="row full-width">
                      <span class="col self-center text-left q-pl-md text-body2">
                        {{m.id}} &nbsp;&nbsp; {{m.name}}
                        <q-popup-edit v-model="m.name" auto-save v-slot="scope" :ref="'major' + m.id"
                                      @before-show="show_edit||$refs['major' + m.id][0].hide()" @before-hide="show_edit=false"
                                      :validate="val=>!!val" @save="v=>update_major(m,v)">
                          <q-input v-model.trim="scope.value" dense autofocus @keyup.enter="scope.set" maxlength="20" clearable clear-icon="close"/>
                        </q-popup-edit>
                      </span>
                      <span class="float-right">
                        <q-btn icon="edit" color="primary" round flat size="sm"
                               @click.stop="show_edit=true;$refs['major' + m.id][0].show();"/>
                        <q-btn icon="close" color="negative" round flat size="sm" @click.stop="delete_major(m.id)"/>
                      </span>
                    </td>
                  </tr>
                  <tr v-if="major_list.length === 0">
                    <td class="row full-width text-center">
                      <span class="col self-center text-center q-pl-md text-basic text-body2">
                        {{select_college === 0 ? '选择学院来查看其中的专业' : '选择的学院下无专业'}}
                      </span>
                    </td>
                  </tr>
                  <tr v-if="select_college !== 0">
                    <td class="row full-width text-center cursor-pointer" v-ripple @click.stop>
                      <span class="q-pl-md text-body2 text-positive self-center col">
                        <q-icon name="add"/>添加专业
                        <q-popup-edit v-model="add_name" auto-save v-slot="scope"
                                      @hide="add_name=''"
                                      :validate="val => !!val" @save="add_major">
                          <q-input v-model.trim="scope.value" dense autofocus @keyup.enter="scope.set" maxlength="20" clearable clear-icon="close"/>
                        </q-popup-edit>
                      </span>
                    </td>
                  </tr>
                </tbody>
              </q-markup-table>
              <q-inner-loading
                :showing="right_loading"
                color="teal"
              />
            </div>
          </template>
        </q-splitter>
      </q-page>
    </q-page-container>
  </q-layout>
</template>

<script lang="ts">
import {defineComponent, ref, nextTick, computed} from 'vue';
import {useUserStore} from 'stores/user';
import {useQuasar} from 'quasar';

export default defineComponent( {
  name: 'DepartmentList',
  setup() {
    const user = useUserStore()
    const $q = useQuasar()
    const splitterModel = ref(40)
    const select_college = ref(0)
    const show_edit = ref(false)
    const left_loading = ref(false)
    const right_loading = ref(false)
    const add_name = ref('')
    const college_list = computed(() => user.colleges)
    const major_list = computed(() => user.majors.filter(m => m.college === select_college.value))

    left_loading.value = true
    Promise.all([user.load_college(), user.load_major()]).then(() => {
      left_loading.value = false
    })

    return {
      left_loading, right_loading,
      show_edit,
      splitterModel,
      college_list, major_list,
      select_college,
      add_name,
      async update_college(college: {id: number, name: string}, new_name: string) {
        left_loading.value = true
        const old_name = college.name
        const res = await user.update_college(college.id, new_name)
        if (res === false) {
          // rollback
          await nextTick()
          college.name = old_name
        }
        left_loading.value = false
      },
      async update_major(major: {id: number, name: string}, new_name: string) {
        right_loading.value = true
        const old_name = major.name
        const res = await user.update_major(major.id, new_name)
        if (res === false) {
          // rollback
          await nextTick()
          major.name = old_name
        }
        right_loading.value = false
      },
      async add_college(new_name: string) {
        left_loading.value = true
        const res = await user.add_college(new_name)
        if (res !== false)
          await user.load_college()
        left_loading.value = false
      },
      async add_major(new_name: string) {
        right_loading.value = true
        console.log(new_name)
        const res = await user.add_major(select_college.value, new_name)
        if (res !== false)
          await user.load_major()
        right_loading.value = false
      },
      async delete_college(cid: number) {
        $q.dialog({
          title: '删除学院',
          message: '删除学院后，该学院下的所有专业也会被删除，所有学生会被设置为未分配状态。确定要删除吗？',
          ok: {
            label: '确定',
            color: 'red',
          },
          cancel: {
            label: '取消',
            color: 'blue-grey',
            flat: true
          }
        }).onOk(async () => {
          left_loading.value = true
          const res = await user.delete_college(cid)
          if (res !== false) {
            await user.load_college()
            if (select_college.value === cid) select_college.value = 0
          }
          left_loading.value = false
        })
      },
      async delete_major(mid: number) {
        $q.dialog({
          title: '删除专业',
          message: '删除专业后，该专业下的所有学生会被设置为未分配状态。确定要删除吗？',
          ok: {
            label: '确定',
            color: 'red'
          },
          cancel: {
            label: '取消',
            color: 'blue-grey',
            flat: true
          }
        }).onOk(async () => {
          right_loading.value = true
          const res = await user.delete_major(mid)
          if (res !== false)
            await user.load_major()
          right_loading.value = false
        })
      }
    }
  }
})
</script>

<style scoped lang="scss">
@import '../css/app.scss';

table.q-table tbody tr {
  @extend .right-rounded;
}
</style>
