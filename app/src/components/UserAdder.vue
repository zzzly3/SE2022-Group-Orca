<template>
  <div>
    <q-btn color="positive" class="q-ml-lg" icon="add" outline @click="show = true">添加用户</q-btn>
    <q-dialog v-model="show" persistent transition-show="scale" transition-hide="scale">
      <q-card style="width: 400px">
        <q-card-section>
          <div class="text-subtitle1 row items-center">
            <q-icon size="sm" color="primary"></q-icon>
            <span>添加用户</span>
          </div>
        </q-card-section>

        <q-card-section class="q-py-none">
          <q-form style="width: 300px" class="q-px-md q-gutter-y-xs">
            <q-select v-model="type" :options="types" label="用户类型" dense ref="typeRef"
                     lazy-rules :rules="[val => !!val || '无效的类型']" />
            <q-input :label="type.value==='teacher'?'工号':'学号'" v-model="id" dense ref="idRef"
                     clearable clear-icon="close" maxlength="8" lazy-rules
                     :rules="[val => (type.value==='teacher'?rules.id_t:rules.id_s).test(val) || '无效的学/工号']"
            />
            <q-input label="姓名" v-model="name" dense ref="nameRef"
                     clearable clear-icon="close" maxlength="12" lazy-rules
                     :rules="[val => rules.name.test(val) || '无效的姓名']"
            />
            <q-input label="身份证号" v-model="pid" dense ref="pidRef"
                     clearable clear-icon="close" maxlength="18" lazy-rules
                     :rules="[val => rules.pid.test(val) || '无效的身份证号']"
            />
            <q-input label="手机号（可选）" v-model="phone" dense ref="phoneRef"
                     clearable clear-icon="close" maxlength="11"
                     :rules="[val => rules.phone.test(val) || '无效的手机号']"
            />
            <q-input label="邮箱（可选）" v-model="email" dense ref="emailRef"
                     clearable clear-icon="close" maxlength="40"
                     :rules="[val => rules.email.test(val) || '无效的邮箱']"
            />
            <p class="text-grey-7">
              <q-icon name="info"/> 初始密码统一设置为123456
            </p>
          </q-form>
        </q-card-section>

        <q-card-actions align="right" class="q-pa-md">
          <q-btn flat label="取消" @click="clear" :disable="loading" v-close-popup color="negative" />
          <q-btn flat label="确定" @click="submit" :loading="loading" color="primary" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </div>
</template>

<script lang="ts">
import { ref } from 'vue';
import {QValidate} from 'components/models';
import {useUserStore} from 'stores/user';

const types = [
  {label: '学生', value: 'student'},
  {label: '教师', value: 'teacher'}
]

export default {
  name: 'UserAdder',
  setup() {
    const user = useUserStore()
    const loading = ref(false)
    const show = ref(false)
    const name = ref('')
    const id = ref('')
    const pid = ref('')
    const phone = ref('')
    const email = ref('')
    const type = ref({label: '', value: ''})
    const rules = {
      name: /^[^0-9]+$/,
      pid: /^\d{17}(?:\d|X)$/,
      phone: /^(?:1\d{10}|)$/,
      email: /^(?:\S+@\S+\.\S+|)$/,
      id_s: /^\d{6}$/,
      id_t: /^\d{8}$/
    }
    const idRef = ref<QValidate|null>(null)
    const typeRef = ref<QValidate|null>(null)
    const pidRef = ref<QValidate|null>(null)
    const phoneRef = ref<QValidate|null>(null)
    const emailRef = ref<QValidate|null>(null)
    const nameRef = ref<QValidate|null>(null)

    const clear = () => {
        show.value = false
        name.value = id.value = pid.value = phone.value = email.value = ''
    }

    return {
      name, id, pid, phone, email, type,
      nameRef, idRef, pidRef, phoneRef, emailRef, typeRef,
      types,
      show, loading,
      rules,
      clear,
      async submit() {
        console.log('UserAdder: in submit')
        console.log(typeRef, idRef, typeRef.value, idRef.value)
        if (!idRef.value || !nameRef.value || !pidRef.value || !phoneRef.value || !emailRef.value || !typeRef.value)
          return
        idRef.value.validate()
        nameRef.value.validate()
        pidRef.value.validate()
        phoneRef.value.validate()
        emailRef.value.validate()
        typeRef.value.validate()
        if (idRef.value.hasError || nameRef.value.hasError || pidRef.value.hasError || phoneRef.value.hasError || emailRef.value.hasError || typeRef.value.hasError)
          return
        loading.value = true
        if (await user.add_user({id:id.value, name:name.value, pid:pid.value, phone:phone.value, email:email.value, type:type.value.value})) {
          // TODO: emit 'user list update'
          clear()
        }
        loading.value = false
      }
    }
  }
}
</script>

<style scoped>

</style>
