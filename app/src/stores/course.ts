import { defineStore } from 'pinia';
import { post } from 'boot/axios';
import { Notify } from 'quasar';
import { useUserStore } from './user';

export interface CourseInfo {
  courseId: string;
  courseName: string;
  courseTime: string;
  courseTimeDay: string;
  courseTimeStart: string;
  courseTimeEnd: string;
  coursePlace: string;
  courseTeacher: string;
  courseMajor: string;
  courseDepartment: string;
  courseCredit: string;
  courseCreditHour: string;
  courseCapacity: string;
  courseDescription: string;
}

export interface CourseApplicationInfo {
  courseId: string;
  courseName: string;
  courseTime: string;
  courseTimeDay: string;
  courseTimeStart: string;
  courseTimeEnd: string;
  coursePlace: string;
  courseTeacher: string;
  courseMajor: string;
  courseDepartment: string;
  courseCredit: string;
  courseCreditHour: string;
  courseCapacity: string;
  courseDescription: string;
  applicationType: string;
  applicationTime: string;
  applicantName: string;
  applicantNumber: string;
  applicationStatus: string;
  applicationId: string;
}

const user = useUserStore();

export const useCourseStore = defineStore('course', {
  state: () => ({
    name: user.name,
    login: false,
    type: user.type,
    courseTimeStartList: [],
    courseTimeEndList: [],
    classroomList: [],
    teacherList: [],
    departmentList: [],
    majorList: [],
  }),
  actions: {
    async load_course_constants() {
      const r = await post('/course/load_course_constants', {}, false);
      this.courseTimeStartList = r.courseTimeStartList
      this.courseTimeEndList = r.courseTimeEndList
      this.classroomList = r.classRoomList
      this.teacherList = r.teacherList
      this.departmentList = r.departmentList
      this.majorList = r.majorList
      return r
    },
    async load_course_lists_page_admin() {
      console.log(user.type);

      this.type = user.type;
      console.log(this.type);
      if (this.type === 'admin') {
        const r = await post('/course/get_course_all', {}, false);
        if (r != false) {
          console.log(r);
          return r;
        }
      } else {
        Notify.create({ type: 'negative', message: '您没有权限访问该页面' });
      }
      return [];
    },
    async add_course({
      courseId,
      courseName,
      courseTime,
      courseTimeDay,
      courseTimeStart,
      courseTimeEnd,
      coursePlace,
      courseTeacher,
      courseMajor,
      courseDepartment,
      courseCredit,
      courseCreditHour,
      courseCapacity,
      courseDescription,
    }: {
      courseId: string;
      courseName: string;
      courseTime: string;
      courseTimeDay: string;
      courseTimeStart: string;
      courseTimeEnd: string;
      coursePlace: string;
      courseTeacher: string;
      courseMajor: string;
      courseDepartment: string;
      courseCredit: string;
      courseCreditHour: string;
      courseCapacity: string;
      courseDescription: string;
    }) {
      if (
        (await post('/course/add_course', {
          courseId,
          courseName,
          courseTime,
          courseTimeDay,
          courseTimeStart,
          courseTimeEnd,
          coursePlace,
          courseTeacher,
          courseMajor,
          courseDepartment,
          courseCredit,
          courseCreditHour,
          courseCapacity,
          courseDescription,
        })) != false
      ) {
        Notify.create({
          message: '添加成功',
          color: 'positive',
          position: 'top',
        });
        return true;
      }
      return false;
    },
    async edit_course({
      courseId,
      courseName,
      courseTime,
      courseTimeDay,
      courseTimeStart,
      courseTimeEnd,
      coursePlace,
      courseTeacher,
      courseMajor,
      courseDepartment,
      courseCredit,
      courseCreditHour,
      courseCapacity,
      courseDescription,
    }: {
      courseId: string;
      courseName: string;
      courseTime: string;
      courseTimeDay: string;
      courseTimeStart: string;
      courseTimeEnd: string;
      coursePlace: string;
      courseTeacher: string;
      courseMajor: string;
      courseDepartment: string;
      courseCredit: string;
      courseCreditHour: string;
      courseCapacity: string;
      courseDescription: string;
    }) {
      if (
        (await post('/course/edit_course', {
          courseId,
          courseName,
          courseTime,
          courseTimeDay,
          courseTimeStart,
          courseTimeEnd,
          coursePlace,
          courseTeacher,
          courseMajor,
          courseDepartment,
          courseCredit,
          courseCreditHour,
          courseCapacity,
          courseDescription,
        })) != false
      ) {
        Notify.create({
          message: '修改成功',
          color: 'positive',
          position: 'top',
        });
        return true;
      }
      return false;
    },
    async delete_course(courseId: string) {
      if ((await post('/course/delete_course', { courseId })) != false) {
        Notify.create({
          message: '删除成功',
          color: 'positive',
        });
      }
    },
    async load_course_application_lists_page_admin() {
      this.type = user.type;
      if (this.type === 'admin') {
        const r = await post('/course/get_course_application_all', {}, false);
        return r;
      } else {
        Notify.create({
          type: 'negative',
          message: '您没有权限访问该页面',
        });
      }
      return [];
    },
    async update_course_application_status({
      courseId,
      courseName,
      courseTime,
      courseTimeDay,
      courseTimeStart,
      courseTimeEnd,
      coursePlace,
      courseTeacher,
      courseMajor,
      courseDepartment,
      courseCredit,
      courseCreditHour,
      courseCapacity,
      courseDescription,
      applicationType,
      applicationTime,
      applicantName,
      applicantNumber,
      applicationStatus,
      applicationId,
    }: {
      courseId: string;
      courseName: string;
      courseTime: string;
      courseTimeDay: string;
      courseTimeStart: string;
      courseTimeEnd: string;
      coursePlace: string;
      courseTeacher: string;
      courseMajor: string;
      courseDepartment: string;
      courseCredit: string;
      courseCreditHour: string;
      courseCapacity: string;
      courseDescription: string;
      applicationType: string;
      applicationTime: string;
      applicantName: string;
      applicantNumber: string;
      applicationStatus: string;
      applicationId: string;
    }) {
      if (
        (await post('/course/update_course_application_status', {
          courseId,
          courseName,
          courseTime,
          courseTimeDay,
          courseTimeStart,
          courseTimeEnd,
          coursePlace,
          courseTeacher,
          courseMajor,
          courseDepartment,
          courseCredit,
          courseCreditHour,
          courseCapacity,
          courseDescription,
          applicationType,
          applicationTime,
          applicantName,
          applicantNumber,
          applicationStatus,
          applicationId,
        })) != false
      ) {
        Notify.create({
          message: '操作成功',
          color: 'positive',
          position: 'top',
        });
        return true;
      }
      return false;
    },
    async batch_import(file: File) {
      await post('/course/batch_import', { file }, false);
    },
    async load_course_lists_page_teacher() {
      console.log(user.type);

      this.type = user.type;
      console.log(this.type);
      if (this.type === 'teacher') {
        const r = await post(
          '/course/get_course_teacher',
          { name: this.name },
          false
        );
        if (r != false) {
          return r;
        }
      } else {
        Notify.create({ type: 'negative', message: '您没有权限访问该页面' });
      }
      return [];
    },
    async load_course_application_lists_page_teacher() {
      this.type = user.type;
      if (this.type === 'teacher') {
        const r = await post(
          '/course/get_course_application_teacher',
          { name: this.name },
          false
        );
        return r;
      } else {
        Notify.create({
          type: 'negative',
          message: '您没有权限访问该页面',
        });
      }
      return [];
    },
    async send_course_application({
      courseId,
      courseName,
      courseTime,
      courseTimeDay,
      courseTimeStart,
      courseTimeEnd,
      coursePlace,
      courseTeacher,
      courseMajor,
      courseDepartment,
      courseCredit,
      courseCreditHour,
      courseCapacity,
      courseDescription,
      applicationType,
    }: {
      courseId: string;
      courseName: string;
      courseTime: string;
      courseTimeDay: string;
      courseTimeStart: string;
      courseTimeEnd: string;
      coursePlace: string;
      courseTeacher: string;
      courseMajor: string;
      courseDepartment: string;
      courseCredit: string;
      courseCreditHour: string;
      courseCapacity: string;
      courseDescription: string;
      applicationType: string;
    }) {
      if (
        (await post('/course/send_course_application', {
          courseId,
          courseName,
          courseTime,
          courseTimeDay,
          courseTimeStart,
          courseTimeEnd,
          coursePlace,
          courseTeacher,
          courseMajor,
          courseDepartment,
          courseCredit,
          courseCreditHour,
          courseCapacity,
          courseDescription,
          applicationType,
          applicantName: this.name,
        })) != false
      ) {
        Notify.create({
          message: '申请已发送',
          color: 'positive',
          position: 'top',
        });
        return true;
      }
      return false;
    },
  },
});
