<template>
  <q-menu v-if="!hidden">
    <q-list style="min-width: 100px">
      <q-item clickable v-close-popup @click="info_show = true">
        <q-item-section>
          <span><q-icon name="person_outline" class="q-pr-sm"/>用户信息</span>
        </q-item-section>
      </q-item>
      <q-separator />
      <q-item clickable v-close-popup class="text-negative" @click="chpwd_show = true">
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

  <UserAdder v-model="info_show" :old="user_info" />
  <PasswordUpdater v-model="chpwd_show" />

</template>

<script lang="ts">

import {computed, defineComponent, ref} from 'vue'
import {UserInfo, useUserStore} from 'stores/user';
import {useRouter} from 'vue-router';
import UserAdder from 'components/UserAdder.vue';
import PasswordUpdater from 'components/PasswordUpdater.vue';

export default defineComponent({
  name: 'PopMenu',
  components: {
    UserAdder,
    PasswordUpdater
  },
  props: {
    hidden: Boolean
  },
  setup() {
    const user = useUserStore()
    const router = useRouter()
    const info_show = ref(false)
    const chpwd_show = ref(false)
    const user_info = computed(() => ({
      name: user.name,
      pid: user.pid,
      id: user.id,
      email: user.email,
      phone: user.phone,
      type: user.type,
      leave: user.leave,
      college: user.college ? user.college.id : 0,
      major: user.major ? user.major.id : 0
    }) as UserInfo)

    return {
      info_show, user_info, chpwd_show,
      user,
      async logout() {
        await user.do_logout()
        user.login = false
        await router.replace('/login')
      }
    }
  }
})
</script>

<style scoped>

</style>
