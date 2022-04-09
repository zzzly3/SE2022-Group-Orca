import { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', redirect: 'index' },
      { path: 'index', component: () => import('pages/IndexPage.vue') },
      { path: 'user_list', component: () => import('pages/UserList.vue') },
      {
        path: 'department',
        component: () => import('pages/DepartmentList.vue'),
      },
    ],
  },
  { path: '/login', component: () => import('pages/LoginPage.vue') },
  // Below are only used for testing.
  {
    path: '/course/course_list',
    component: () => import('pages/CourseList.vue'),
  },
  {
    path: '/course/course_list_teacher',
    component: () => import('pages/CourseListTeacher.vue'),
  },
  {
    path: '/course/course_list_application',
    component: () => import('pages/CourseListApplication.vue'),
  },
  {
    path: '/course/course_list_application_teacher',
    component: () => import('pages/CourseListApplicationTeacher.vue'),
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue'),
  },
];

export default routes;
