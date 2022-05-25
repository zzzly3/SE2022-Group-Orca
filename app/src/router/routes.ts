import { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', redirect: 'index' },
      { path: 'index', component: () => import('pages/IndexPage.vue') },
      { path: 'user_list', component: () => import('pages/UserList.vue') },
      { path: 'department', component: () => import('pages/DepartmentList.vue') },
      { path: 'course_list', component: () => import('pages/CourseList.vue') },
      { path: 'course_application', component: () => import('pages/CourseListApplication.vue') },
      { path: 'my_course_list', component: () => import('pages/CourseListTeacher.vue') },
      { path: 'my_course_application', component: () => import('pages/CourseListApplicationTeacher.vue') },
      { path: 'selectable_course_list', component: () => import('pages/CourseListStudent.vue') },
      { path: 'course_list_admin', component: () => import('pages/CourseListAdmin.vue') },
      { path: 'manage', component: () => import('pages/ManagePage.vue') },
    ],
  },
  { path: '/login', component: () => import('pages/LoginPage.vue') },
  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue'),
  },
];

export default routes;
