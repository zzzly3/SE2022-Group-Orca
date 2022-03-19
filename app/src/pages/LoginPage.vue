<template>
` <q-layout view="hhh lpR fFf">
  <q-header class="q-px-lg bg-white text-basic">
    <q-toolbar>
      <q-img src="~assets/title.png" width="150px"></q-img>
      <q-toolbar-title>
        <br/>教务管理系统
      </q-toolbar-title>
    </q-toolbar>
    <q-separator/>
  </q-header>

  <q-drawer show-if-above :breakpoint="1250" :width="800" side="left">
    <div class="row items-center full-height q-ml-xl">
      <q-img src="~assets/login-bg.png" class="q-ml-xl" style="opacity: 0.7"></q-img>
    </div>
  </q-drawer>

  <q-page-container>
    <q-page class="column items-center justify-evenly">
      <q-card style="width: 350px">
        <q-card-section>
          <div class="text-h6 q-px-md">登录</div>
        </q-card-section>
        <q-separator />
        <q-card-section>
          <q-form class="q-pt-md q-px-md q-gutter-y-lg">
            <q-input label="学/工号" v-model="id" maxlength="8" dense outlined/>
            <q-input label="密码" v-model="pwd" maxlength="32" dense outlined/>
          </q-form>
        </q-card-section>
        <q-card-actions align="center" class="q-pb-xl">
          <q-btn label="登 录" style="width: 85%" size="md" icon="login" color="blue-12"/>
        </q-card-actions>
      </q-card>
    </q-page>
  </q-page-container>
</q-layout>
</template>

<script lang="ts">
import {ref} from 'vue'
export default {
  name: 'LoginPage',
  setup() {
    const id = ref('')
    const pwd = ref('')
    return {
      id, pwd
    }
  }
}

</script>
