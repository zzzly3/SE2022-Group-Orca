import { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', redirect: 'index' },
      { path: 'index', component: () => import('pages/IndexPage.vue') },
      { path: 'user_list', component: () => import('pages/UserList.vue') },
      { path: 'department', component: () => import('pages/DepartmentList.vue') }
    ],
  },
  { path: '/login', component: () => import('pages/LoginPage.vue')},

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue'),
  },
];

export default routes;
