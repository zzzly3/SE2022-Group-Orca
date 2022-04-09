<template>
  <q-layout view="hhh lpR fff">
    <q-header class="text-basic bg-white q-mx-xl" style="z-index:100">
      <q-toolbar class="q-mx-xl">
        <q-input outlined v-model="search" label="Search (TODO)" dense style="margin-top: 1px" rounded disable>
          <template v-slot:append>
            <q-btn color="primary" icon="search" round flat></q-btn>
          </template>
        </q-input>
        <q-btn color="positive" class="q-ml-lg" icon="add" outline @click="adder_bind=undefined;show_adder=true">添加用户</q-btn>
      </q-toolbar>
    </q-header>

    <q-page-container>
      <q-page class="column items-center">
        <div style="width: 80%; ">
          <q-markup-table flat class="text-center">
            <thead class="text-basic">
              <tr>
                <th>学/工号</th>
                <th>姓名</th>
                <th>
                  <span>
                    类型
                  </span>
                  <span class="text-info" style="text-decoration: underline">
                    (?)
                    <q-tooltip>删除线表示用户已离校，不能使用系统。</q-tooltip>
                  </span>
                </th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="i in rows" :key="i.id">
                <td>{{i.id}}</td>
                <td>{{i.name}}</td>
                <td>
                  <span v-if="i.leave" style="text-decoration: teal line-through">
                    {{i.type}}
                  </span>
                  <span v-else>
                    {{i.type}}
                  </span>
                </td>
                <td>
                  <q-btn color="primary" icon="visibility" round flat size="sm"
                         @click="adder_bind=i;show_adder=true;">
                    <q-tooltip>查看信息</q-tooltip>
                  </q-btn>
                  <q-btn color="negative" icon="update" round flat size="sm" v-if="i.type !== 'admin'"
                         @click="chpwd_uid=i.id;show_chpwd=true;">
                    <q-tooltip>修改密码</q-tooltip>
                  </q-btn>
                </td>
              </tr>
            </tbody>
          </q-markup-table>
        </div>
        <q-pagination
          class="q-pa-sm"
          input
          v-model="current"
          :max="pcnt"
        />
        <q-inner-loading
          :showing="loading"
          color="teal"
        />
      </q-page>
    </q-page-container>

<!--    <q-footer class="bg-white">-->
<!--      <div class="row justify-center">-->
<!--        <q-pagination-->
<!--          input-->
<!--          v-model="current"-->
<!--          :max="pcnt"-->
<!--        />-->
<!--      </div>-->
<!--    </q-footer>-->

  </q-layout>

  <UserAdder v-model="show_adder" :old="adder_bind" @user_update="load"/>
  <PasswordUpdater v-model="show_chpwd" :admin="chpwd_uid"/>
</template>

<script lang="ts">
import {defineComponent, ref, watch} from 'vue';
import {UserInfo, useUserStore} from 'stores/user';
import UserAdder from 'components/UserAdder.vue';
import PasswordUpdater from 'components/PasswordUpdater.vue';

export default defineComponent({
  name: 'UserList',
  components: {PasswordUpdater, UserAdder},
  setup() {
    const user = useUserStore()
    const rows = ref([] as UserInfo[])
    const search = ref('')
    const show_adder = ref(false)
    const adder_bind = ref<UserInfo | undefined>(undefined)
    const show_chpwd = ref(false)
    const chpwd_uid = ref('')
    const current = ref(1)
    const pcnt = ref(1)
    const loading = ref(false)

    const load = async () => {
      loading.value = true
      const r = await user.load_user_list((current.value - 1) * 10, 10)
      if (r === false) {
        loading.value = false
      } else {
        pcnt.value = Math.floor(r.total / 10) + 1
        rows.value = r.list
        loading.value = false
      }
    }
    load()
    watch(current, load)

    return {
      load, current, pcnt, loading,
      show_adder, adder_bind,
      show_chpwd, chpwd_uid,
      rows,
      search
    };
  }
});
</script>
