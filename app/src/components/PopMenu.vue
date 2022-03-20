<template>
  <q-menu v-if="!hidden">
    <q-list style="min-width: 100px">
      <q-item clickable v-close-popup class="text-negative" @click="pwd.show = true">
        <q-item-section>
          <span><q-icon name="update" class="q-pr-sm"/>修改密码</span>
        </q-item-section>
      </q-item>
      <q-separator />
      <q-item clickable v-close-popup class="text-negative" @click="logout">
        <q-item-section>
          <span><q-icon name="logout" class="q-pr-sm"/>退出</span>
        </q-item-section>
      </q-item>
    </q-list>
  </q-menu>
  <q-dialog v-model="pwd.show" :persistent="forceChpwd" transition-show="scale" transition-hide="scale">
    <q-card style="width: 350px">
      <q-card-section>
        <div class="text-subtitle1 row items-center">
          <q-icon name="update" size="sm" color="primary"></q-icon>
          <span>修改密码</span>
        </div>
      </q-card-section>

      <q-card-section class="q-py-none">
        <q-form style="width: 300px" class="q-px-md q-gutter-y-xs">
          <q-input label="输入旧密码" v-model="pwd.oldpwd" dense type="password"
                   clearable clear-icon="close" maxlength="32" lazy-rules
                   :rules="[val => true || '']"
          />
          <q-input label="输入新密码" v-model="pwd.newpwd" dense ref="pwdRef1" type="password"
                   clearable clear-icon="close" maxlength="32" lazy-rules
                   :rules="[val => testpwd(val) || '密码应包括数字、字母和特殊符号中的至少两种，且不少于6位']"
          />
          <q-input label="确认新密码" v-model="pwd.newpwd2" dense ref="pwdRef2" type="password"
                   clearable clear-icon="close" maxlength="32" lazy-rules
                   :rules="[val => val === pwd.newpwd || '两次输入的密码不一致']"
          />
          <p class="text-warning" v-if="forceChpwd">
            <q-icon name="info"/> 请修改您的初始密码
          </p>
        </q-form>
      </q-card-section>

      <q-card-actions align="right" class="q-pa-md">
        <q-btn flat label="取消" @click="clear" :disable="pwd.loading" v-close-popup v-if="!forceChpwd" />
        <q-btn flat label="确定" @click="chpwd" :loading="pwd.loading" color="primary" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script lang="ts">

import {ref} from 'vue'
import {useUserStore} from 'stores/user';
import {useRouter} from 'vue-router';
import {QValidate} from './models'

export default {
  name: 'PopMenu',
  props: {
    forceChpwd: Boolean,
    hidden: Boolean
  },
  setup(props: { forceChpwd: boolean; hidden: boolean; }) {
    const pwd = ref({show: props.forceChpwd, oldpwd: '', newpwd: '', newpwd2: '', loading: false})
    const pwdRef1 = ref<QValidate|null>(null)
    const pwdRef2 = ref<QValidate|null>(null)
    const user = useUserStore()
    const router = useRouter()

    const clear = () => {
      pwd.value.show = false
      pwd.value.oldpwd = pwd.value.newpwd = pwd.value.newpwd2 = ''
    }
    return {
      pwd,
      pwdRef1, pwdRef2,
      testpwd(val: string) {
        let c = 0
        if (/\d/.test(val)) c++
        if (/[a-zA-Z]/.test(val)) c++
        if (/[^\da-zA-Z]/.test(val)) c++
        return c >= 2 && val.length >= 6 && val.length <= 32
      },
      async chpwd() {
        if (!pwdRef1.value || !pwdRef2.value)
          return
        pwdRef1.value.validate()
        pwdRef2.value.validate()
        if (pwdRef1.value.hasError || pwdRef2.value.hasError)
          return
        pwd.value.loading = true
        if (await user.chpwd(pwd.value.oldpwd, pwd.value.newpwd))
          clear()
        pwd.value.loading = false
      },
      clear,
      async logout() {
        if (await user.do_logout() || 1) {
          user.login = false
          await router.replace('/login')
        }
      }
    }
  }
}
</script>

<style scoped>

</style>
