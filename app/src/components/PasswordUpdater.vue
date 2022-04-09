<template>
  <q-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" :persistent="force" transition-show="scale" transition-hide="scale" @close="clear">
    <q-card style="width: 350px">
      <q-card-section>
        <div class="text-subtitle1 row items-center">
          <q-icon name="update" size="sm" color="primary"></q-icon>
          <span>修改密码</span>
        </div>
      </q-card-section>

      <q-card-section class="q-py-none">
        <q-form style="width: 300px" class="q-px-md q-gutter-y-xs">
          <q-input label="输入旧密码" v-model="pwd.oldpwd" dense type="password" v-if="!admin"
                   clearable clear-icon="close" maxlength="32" lazy-rules
                   :rules="[val => true]"
          />
          <q-input label="学/工号" :model-value="admin" dense v-else disable :rules="[val => true]" />
          <q-input label="输入新密码" v-model="pwd.newpwd" dense ref="pwdRef1" type="password"
                   clearable clear-icon="close" maxlength="32" lazy-rules
                   :rules="[val => testpwd(val) || '密码应包括数字、字母和特殊符号中的至少两种，且不少于6位']"
          />
          <q-input label="确认新密码" v-model="pwd.newpwd2" dense ref="pwdRef2" type="password"
                   clearable clear-icon="close" maxlength="32" lazy-rules
                   :rules="[val => val === pwd.newpwd || '两次输入的密码不一致']"
          />
          <p class="text-warning" v-if="force">
            <q-icon name="info"/> 请修改您的初始密码
          </p>
        </q-form>
      </q-card-section>

      <q-card-actions align="right" class="q-pa-md">
        <q-btn flat label="取消" :disable="pwd.loading" v-close-popup v-if="!force" />
        <q-btn flat label="确定" @click="chpwd" :loading="pwd.loading" color="primary" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script lang="ts">
import {ref, defineComponent} from 'vue';
import {QValidate} from 'components/models';
import {useUserStore} from 'stores/user';

export default defineComponent({
  name: 'PasswordUpdater',
  props: {
    force: Boolean,
    modelValue: Boolean,
    admin: String,
  },
  setup(props, {emit}) {
    const user = useUserStore()
    const pwd = ref({oldpwd: '', newpwd: '', newpwd2: '', loading: false})
    const pwdRef1 = ref<QValidate|null>(null)
    const pwdRef2 = ref<QValidate|null>(null)
    const clear = () => {
      emit('update:modelValue', false)
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
        if (await user.chpwd(props.admin ? props.admin : '', pwd.value.oldpwd, pwd.value.newpwd))
          clear()
        pwd.value.loading = false
      },
      clear
    }
  }
})
</script>

<style scoped>

</style>
