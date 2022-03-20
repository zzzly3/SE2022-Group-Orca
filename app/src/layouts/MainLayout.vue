<template>

  <q-img src="~assets/home-bg-2.png" class="fullscreen" style="z-index: -2" v-if="is_home"/>
<!--  <div class="bg-blur-white fullscreen" style="z-index: -1"/>-->

  <q-layout view="lHh lpR fFf">

    <q-header :class="is_home ? 'bg-my-white text-basic' : 'bg-white text-basic'">
      <q-toolbar>
        <q-btn dense flat round icon="menu" @click="toggleLeftDrawer" class="lt-md text-primary" />
        <q-breadcrumbs style="font-size: 16px" class="lt-md q-ml-md">
          <q-breadcrumbs-el :label="page_path[page_path.length - 1].name" :icon="page_path[page_path.length - 1].icon"/>
        </q-breadcrumbs>
        <q-breadcrumbs style="font-size: 16px" class="q-ml-xl gt-sm">
          <template v-slot:separator>
            <q-icon size="1.5em" name="chevron_right"/>
          </template>
          <q-breadcrumbs-el v-for="i in page_path" :key="i.name"
                            :label="i.name" :icon="i.icon"
                            :class="i === page_path[page_path.length-1] ? 'text-primary' : ''"
          />
        </q-breadcrumbs>
        <q-toolbar-title class="text-body1 text-right q-mr-sm" style="line-height: 4em;">
          {{user.name}}
        </q-toolbar-title>
        <q-btn flat round icon="o_settings" class="text-basic q-mr-md">
          <PopMenu :force-chpwd="user.force_chpwd"></PopMenu>
        </q-btn>
<!--        <q-btn color="negative" flat round icon="logout" class="q-mr-md"/>-->
      </q-toolbar>
    </q-header>

    <q-drawer :width="250" show-if-above v-model="leftDrawerOpen" side="left" :class="is_home ? 'bg-my-white' : 'bg-white'">
      <div class="text-center q-pr-xl q-py-lg">
        <q-img src="~assets/title.png" width="55%" style="opacity: 0.9"></q-img>
      </div>
      <q-list padding class="text-basic">
        <q-item
          clickable
          active-class="text-primary bg-light-blue-1"
          :inset-level="0.1"
          class="overflow-hidden"
          style="border-radius: 0 30px 30px 0"
          to="index"
        >
          <q-item-section avatar>
            <q-icon name="home" />
          </q-item-section>
          <q-item-section>主页</q-item-section>
        </q-item>
        <q-expansion-item
          v-for="i in menu" :key="i.name"
          :header-inset-level="0.1"
          group="menu"
          :icon="i.icon"
          :label="i.name"
          :default-opened="i.name === page_path[0].name"
        >
          <q-item
            v-for="j in i.submenu" :key="j.name"
            clickable
            :inset-level="0.2"
            class="overflow-hidden"
            style="border-radius: 0 30px 30px 0"
            active-class="text-primary bg-light-blue-1"
            :to="j.page"
          >
            <q-item-section avatar>
              <q-icon :name="j.icon" />
            </q-item-section>
            <q-item-section>{{j.name}}</q-item-section>
          </q-item>
        </q-expansion-item>
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>

</template>

<script>
import { ref, computed } from 'vue'
import {useRoute} from 'vue-router'
import {useUserStore} from '../stores/user'
import PopMenu from '../components/PopMenu'

const menu_data = [
  {name: '用户管理', icon: 'manage_accounts', allow: [1], submenu: [
      {name: '用户列表', icon: 'list_alt', page: 'user_list'},
    ]}
]

export default {
  components: {PopMenu},
  setup () {
    const leftDrawerOpen = ref(false)
    const user = useUserStore()
    const $route = useRoute()

    const menu = computed(() => {
      return menu_data.filter(i => user.login && user.type in i.allow)
    })

    const page = computed(() => {
      const t = $route.path.split('/')
      return t[t.length - 1]
    })
    const page_path = computed(() => {
      for (const i of menu_data) {
        for (const j of i.submenu) {
          if (j.page === page.value)
            return [i, j]
        }
      }
      return [{'name': '主页', icon: 'home'}]
    })
    const is_home = computed(() => {
      return page_path.value[0].name === '主页'
    })

    console.log(page.value)

    return {
      leftDrawerOpen,
      user,
      menu,
      page_path,
      is_home,
      toggleLeftDrawer () {
        leftDrawerOpen.value = !leftDrawerOpen.value
      }
    }
  }
}
</script>

<style>
aside.q-drawer {
  background-color: transparent;
}
</style>
