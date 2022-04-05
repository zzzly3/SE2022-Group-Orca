<template>
  <q-layout view="hhh lpR fFf">
    <q-header class="text-basic bg-white q-mx-xl" style="z-index:100">
      <q-toolbar class="q-mx-xl">
        <q-input outlined v-model="search" label="Search" dense style="margin-top: 1px" rounded>
          <template v-slot:append>
            <q-btn color="primary" icon="search" round flat></q-btn>
          </template>
        </q-input>
        <UserAdder class="q-ml-lg"></UserAdder>
      </q-toolbar>
    </q-header>

    <q-page-container>
      <q-page class="column items-center">
        <div style="width: 80%">
          <q-markup-table flat class="text-center">
            <thead class="text-basic">
              <tr>
                <th>学号</th>
                <th>姓名</th>
                <th>类型</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="i in rows" :key="i.id">
                <td>{{i.id}}</td>
                <td>{{i.name}}</td>
                <td>{{i.type}}</td>
                <td></td>
              </tr>
            </tbody>
          </q-markup-table>
        </div>
      </q-page>
    </q-page-container>
  </q-layout>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';
import {UserInfo, useUserStore} from 'stores/user';
import UserAdder from 'components/UserAdder.vue';

export default defineComponent({
  name: 'UserList',
  components: {UserAdder},
  setup() {

    const user = useUserStore()
    const rows = ref([] as UserInfo[])
    const search = ref('')

    user.load_user_list(0,10).then(r => rows.value = r.data)

    return {
      rows,
      search
    };
  }
});
</script>
