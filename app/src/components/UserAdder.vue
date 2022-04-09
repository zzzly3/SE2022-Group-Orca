<template>
    <q-dialog :model-value="show" @update:model-value="$event || hide()" :persistent="!update" transition-show="scale" transition-hide="scale">
      <q-card style="width: 400px">
        <q-card-section>
          <div class="text-subtitle1 row items-center">
            <q-icon :name="update ? 'edit' : 'add'" size="sm" color="primary"></q-icon>
            <span v-if="update">用户信息</span>
            <span v-else>添加用户</span>
          </div>
        </q-card-section>

        <q-card-section class="q-py-none">
          <q-form style="width: 300px" class="q-px-md q-gutter-y-xs">
            <div class="q-gutter-md row items-start">
              <q-select v-model="type" :options="types" label="用户类型" dense ref="typeRef" class="col"
                        :disable="update"
                        lazy-rules :rules="[val => !!val || '无效的类型']" />
              <q-select v-model="leave" label="状态" dense ref="leaveRef" class="col"
                        :options="type.value==='student'?leave_type_student:(type.value==='teacher'?leave_type_teacher:[])"
                        :disable="(update && user.type !== 'admin') || type.value === 'admin'"
                        lazy-rules :rules="[val => !!val || '无效的状态']" />
            </div>
            <q-input :label="type.value==='student'?'学号':'工号'" v-model="id" dense ref="idRef" class="col"
                     :maxlength="type.value==='student'?6:8" lazy-rules
                     :disable="update"
                     :rules="[val => (type.value==='teacher'?rules.id_t:rules.id_s).test(val) || '无效的学/工号']"
            />
            <div class="q-gutter-md row items-start">
              <q-select v-model="college" :options="colleges" label="学院" dense ref="collegeRef" class="col"
                        :disable="(update && user.type !== 'admin') || type.value === 'admin'"
                        lazy-rules :rules="[val => !!val || '无效的学院']" />
              <q-select v-model="major" label="专业" dense ref="majorRef" class="col"
                        :options="majors"
                        :disable="(update && user.type !== 'admin') || type.value === 'admin'"
                        lazy-rules :rules="[val => !!val || '无效的专业']" />
              <q-inner-loading
                :showing="load_list"
                color="teal"
              />
            </div>
            <q-input label="姓名" v-model="name" dense ref="nameRef"
                     maxlength="12" lazy-rules
                     :disable="update && user.type !== 'admin'"
                     :rules="[val => rules.name.test(val) || '无效的姓名']"
            ><template v-if="name && !(update && user.type !== 'admin')" v-slot:append>
              <q-icon name="close" @click.stop="name = ''" class="cursor-pointer" />
            </template></q-input>
            <q-input label="身份证号" v-model="pid" dense ref="pidRef"
                     maxlength="18" lazy-rules
                     :disable="update && user.type !== 'admin'"
                     :rules="[val => rules.pid.test(val) || '无效的身份证号']"
            ><template v-if="pid && !(update && user.type !== 'admin')" v-slot:append>
              <q-icon name="close" @click.stop="pid = ''" class="cursor-pointer" />
            </template></q-input>
            <q-input label="手机号（可选）" v-model="phone" dense ref="phoneRef"
                     maxlength="11"
                     :rules="[val => rules.phone.test(val) || '无效的手机号']"
            ><template v-if="phone" v-slot:append>
              <q-icon name="close" @click.stop="phone = ''" class="cursor-pointer" />
            </template></q-input>
            <q-input label="邮箱（可选）" v-model="email" dense ref="emailRef"
                     maxlength="40"
                     :rules="[val => rules.email.test(val) || '无效的邮箱']"
            ><template v-if="email" v-slot:append>
              <q-icon name="close" @click.stop="email = ''" class="cursor-pointer" />
            </template></q-input>
            <p class="text-grey-7" v-if="!update">
              <q-icon name="info"/> 初始密码统一设置为123456
            </p>
          </q-form>
        </q-card-section>

        <q-card-actions align="right" class="q-pa-md">
          <q-btn flat label="取消" @click="clear" :disable="loading" v-close-popup color="negative" />
          <q-btn flat v-if="update" label="修改" @click="submit" :loading="loading" :disable="load_list" color="primary" />
          <q-btn flat v-else label="确定" @click="submit" :loading="loading" :disable="load_list" color="primary" />
        </q-card-actions>
      </q-card>
    </q-dialog>
</template>

<script lang="ts">
import {computed, defineComponent, PropType, ref, watch} from 'vue';
import {QValidate} from 'components/models';
import {UserInfo, useUserStore} from 'stores/user';

const types = [
  {label: '学生', value: 'student'},
  {label: '教师', value: 'teacher'},
]

const leave_type_student = [
  {label: '在校', value: 0},
  {label: '毕业', value: 1},
  {label: '肄业', value: 2}
]

const leave_type_teacher = [
  {label: '在职', value: 0},
  {label: '退休', value: 1},
  {label: '离职', value: 2}
]

const rules = {
  name: /^[^0-9]+$/,
  pid: /^\d{17}(?:\d|X)$/,
  phone: /^(?:1\d{10}|)$/,
  email: /^(?:\S+@\S+\.\S+|)$/,
  id_s: /^\d{6}$/,
  id_t: /^\d{8}$/
}

export default defineComponent({
  name: 'UserAdder',
  props: {
    old: {
      type: Object as PropType<UserInfo>,
      required: false
    },
    modelValue: Boolean
  },
  setup(props, {emit}) {
    const user = useUserStore()
    const load_list = ref(false)
    const update = ref(false)
    const loading = ref(false)
    const name = ref('')
    const id = ref('')
    const pid = ref('')
    const phone = ref('')
    const email = ref('')
    const type = ref(types[0])
    const leave = ref(leave_type_student[0])
    const colleges = ref([] as {value: number, label: string}[])
    const college = ref({} as {value: number, label: string})
    const majors = ref([] as {value: number, label: string}[])
    const major = ref({} as {value: number, label: string})

    const autofill = async () => {
      load_list.value = true
      const cl = await user.load_college()
      colleges.value = cl.map((c: { id: number; name: string; }) => ({value: c.id, label: c.name}))
      colleges.value.unshift({value: 0, label: '未分配'})
      load_list.value = false
      college.value = colleges.value[0]
      if (props.old) {
        update.value = true
        name.value = props.old.name
        id.value = props.old.id
        pid.value = props.old.pid
        phone.value = props.old.phone
        email.value = props.old.email
        type.value = {label: 'unknown', value: ''}
        leave.value = {label:'正常', value:0}
        for (const t of types)
          if (t.value === props.old.type) {
            type.value = t
            break
          }
        if (props.old.type === 'admin') {
          type.value = {label: '管理员', value: 'admin'}
        }
        if (props.old.type === 'student') {
          for (const t of leave_type_student)
            if (t.value === props.old.leave) {
              leave.value = t
              break
            }
        } else if (props.old.type === 'teacher') {
          for (const t of leave_type_teacher)
            if (t.value === props.old.leave) {
              leave.value = t
              break
            }
        }
        for (const c of colleges.value)
          if (c.value === props.old.college) {
            college.value = c
            break
          }
      } else {
        update.value = false
        name.value = ''
        id.value = ''
        pid.value = ''
        phone.value = ''
        email.value = ''
        type.value = types[0]
        leave.value = leave_type_student[0]
      }
    }

    const show = computed(() => props.modelValue)
    if (show.value)
      autofill()
    watch(show, val => {
      if (val)
        autofill()
    })
    watch(type, val => {
      if (val.value === 'student')
        leave.value = leave_type_student[0]
      else if (val.value === 'teacher')
        leave.value = leave_type_teacher[0]
    })
    watch(college, async val => {
      if (val.value === 0) {
        majors.value = [{value: 0, label: '未分配'}]
      } else {
        load_list.value = true
        const ml = await user.load_major(val.value)
        majors.value = ml.map((m: { id: number; name: string; }) => ({value: m.id, label: m.name}))
        majors.value.unshift({value: 0, label: '未分配'})
        load_list.value = false
      }
      major.value = majors.value[0]
      if (props.old)
        for (const m of majors.value)
          if (m.value === props.old.major) {
            major.value = m
            break
          }
    })

    const idRef = ref<QValidate|null>(null)
    const typeRef = ref<QValidate|null>(null)
    const pidRef = ref<QValidate|null>(null)
    const phoneRef = ref<QValidate|null>(null)
    const emailRef = ref<QValidate|null>(null)
    const nameRef = ref<QValidate|null>(null)
    const leaveRef = ref<QValidate|null>(null)
    const collegeRef = ref<QValidate|null>(null)
    const majorRef = ref<QValidate|null>(null)

    const hide = () => {
      emit('update:modelValue', false)
    }

    const clear = () => {
        hide()
        name.value = id.value = pid.value = phone.value = email.value = ''
    }

    return {
      user,
      update,
      college, colleges, major, majors,
      name, id, pid, phone, email, type, leave,
      nameRef, idRef, pidRef, phoneRef, emailRef, typeRef, leaveRef, collegeRef, majorRef,
      types, leave_type_student, leave_type_teacher,
      show, loading, load_list,
      rules,
      clear, hide,
      async submit() {
        if (!idRef.value || !nameRef.value || !pidRef.value || !phoneRef.value || !emailRef.value || !typeRef.value || !leaveRef.value || !collegeRef.value || !majorRef.value) {
          return
        }
        idRef.value.validate()
        nameRef.value.validate()
        pidRef.value.validate()
        phoneRef.value.validate()
        emailRef.value.validate()
        typeRef.value.validate()
        leaveRef.value.validate()
        collegeRef.value.validate()
        majorRef.value.validate()
        if (idRef.value.hasError || nameRef.value.hasError || pidRef.value.hasError || phoneRef.value.hasError || emailRef.value.hasError || typeRef.value.hasError || leaveRef.value.hasError || collegeRef.value.hasError || majorRef.value.hasError) {
          return
        }
        loading.value = true
        if (await user.add_user({id:id.value, name:name.value, pid:pid.value, phone:phone.value?phone.value:'', email:email.value?email.value:'', type:type.value.value, update: update.value, leave: leave.value.value, college: college.value.value, major: major.value.value})) {
          emit('user_update')
          clear()
        }
        loading.value = false
      }
    }
  }
})
</script>

<style scoped>

</style>
