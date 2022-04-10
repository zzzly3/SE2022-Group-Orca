<template>
  <router-view v-if="init" />
</template>

<script lang="ts">
import {defineComponent, ref} from 'vue';
import {useUserStore} from 'stores/user';
import {useRoute, useRouter} from 'vue-router';
import {useQuasar} from 'quasar';

export default defineComponent({
  name: 'App',
  setup() {
    const user = useUserStore()
    const route = useRoute()
    const router = useRouter()
    const init = ref(false)
    const $q = useQuasar()
    $q.loadingBar.start()
    user.load_user_info().then(() => {
      $q.loadingBar.stop()
      init.value = true
      if (!user.login && route.path !== '/login') {
        $q.notify({type:'info', message:'请先登录'})
        user.login_redirect = route.fullPath
        router.replace('/login')
      }
    })

    return {init}
  }
})
</script>
