import { defineStore } from 'pinia';
import { post} from 'boot/axios';
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
  }),
  actions: {
    async load_course_constants() {
      const r = await post('/course/load_course_constants', {}, false);
      this.courseTimeStartList = r.courseTimeStartList
      this.courseTimeEndList = r.courseTimeEndList
      this.classroomList = r.classRoomList
      this.teacherList = r.teacherList
      return r
    },
    async load_course_lists_page_admin() {

      this.type = user.type;
      if (this.type === 'admin') {
        const r = await post('/course/get_course_all', {}, false);
        if (r != false) {
          return r;
        }
      } else {
        Notify.create({ type: 'negative', message: '您没有权限访问该页面' });
      }
      return [];
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
    async load_course_lists_page_teacher() {
      this.type = user.type;
      if (this.type === 'teacher') {
        const r = await post(
          '/course/get_course_teacher',
          { name: user.name, number: user.id },
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
          { number: user.id, name: user.name },
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
    async load_course_lists_page_student(){
      this.type = user.type;
      if (this.type === 'student') {
        const r = await post(
          '/course/get_course_student',
          { major: user.major.id },
          false
        );
        if (r != false) {
          return r;
        }else {
          Notify.create({ type: 'info', message: '当前选课未开放' });
        }
      } else {
        Notify.create({ type: 'negative', message: '您没有权限访问该页面' });
      }
      return [];
    },
    async add_course({
      courseInfo,
    }: {
      courseInfo: CourseInfo;
    }) {
      if (
        (await post('/course/add_course', {
          courseId: courseInfo.courseId,
          courseName: courseInfo.courseName,
          courseTime: courseInfo.courseTime,
          courseTimeDay: courseInfo.courseTimeDay,
          courseTimeStart: courseInfo.courseTimeStart,
          courseTimeEnd: courseInfo.courseTimeEnd,
          coursePlace: courseInfo.coursePlace,
          courseTeacher: courseInfo.courseTeacher,
          courseCredit: courseInfo.courseCredit,
          courseCreditHour: courseInfo.courseCreditHour,
          courseCapacity: courseInfo.courseCapacity,
          courseDescription: courseInfo.courseDescription,
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
      courseInfo,
    }: {
      courseInfo: CourseInfo;
    }) {
      if (
        (await post('/course/edit_course', {
          courseId: courseInfo.courseId,
          courseName: courseInfo.courseName,
          courseTime: courseInfo.courseTime,
          courseTimeDay: courseInfo.courseTimeDay,
          courseTimeStart: courseInfo.courseTimeStart,
          courseTimeEnd: courseInfo.courseTimeEnd,
          coursePlace: courseInfo.coursePlace,
          courseTeacher: courseInfo.courseTeacher,
          courseCredit: courseInfo.courseCredit,
          courseCreditHour: courseInfo.courseCreditHour,
          courseCapacity: courseInfo.courseCapacity,
          courseDescription: courseInfo.courseDescription,
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
    async delete_course(courseId: string, courseTeacher: string) {
      if ((await post('/course/delete_course', { courseId, courseTeacher })) != false) {
        Notify.create({
          message: '删除成功',
          color: 'positive',
        });
      }
    },
    async update_course_application_status({
      applicationInfo,
    }: {
      applicationInfo: CourseApplicationInfo;
    }) {
      if (
        (await post('/course/update_course_application_status', {
          courseId: applicationInfo.courseId,
          courseName: applicationInfo.courseName,
          courseTime: applicationInfo.courseTime,
          courseTimeDay: applicationInfo.courseTimeDay,
          courseTimeStart: applicationInfo.courseTimeStart,
          courseTimeEnd: applicationInfo.courseTimeEnd,
          coursePlace: applicationInfo.coursePlace,
          courseTeacher: applicationInfo.courseTeacher,
          courseCredit: applicationInfo.courseCredit,
          courseCreditHour: applicationInfo.courseCreditHour,
          courseCapacity: applicationInfo.courseCapacity,
          courseDescription: applicationInfo.courseDescription,
          applicationType: applicationInfo.applicationType,
          applicationTime: applicationInfo.applicationTime,
          applicantName: applicationInfo.applicantName,
          applicantNumber: applicationInfo.applicantNumber,
          applicationStatus: applicationInfo.applicationStatus,
          applicationId: applicationInfo.applicationId,
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
    async send_course_application({
      applicationInfo
    }: {
      applicationInfo: CourseApplicationInfo
    }) {
      user.load_user_info();
      if (
        (await post('/course/send_course_application', {
          courseId: applicationInfo.courseId,
          courseName: applicationInfo.courseName,
          courseTime: applicationInfo.courseTime,
          courseTimeDay: applicationInfo.courseTimeDay,
          courseTimeStart: applicationInfo.courseTimeStart,
          courseTimeEnd: applicationInfo.courseTimeEnd,
          coursePlace: applicationInfo.coursePlace,
          courseTeacher: user.id,
          courseCredit: applicationInfo.courseCredit,
          courseCreditHour: applicationInfo.courseCreditHour,
          courseCapacity: applicationInfo.courseCapacity,
          courseDescription: applicationInfo.courseDescription,
          applicationType: applicationInfo.applicationType,
          applicantName: user.name,
          applicantNumber: user.id,
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
    async modifyCapacity({cid, capacity}:{cid:string, capacity:number}){
      console.log('in modifyCapacity')
      const r = await post('/course/update_course_capacity', {cid, capacity})
      if(r !== false){
        Notify.create({type:'positive', message:'成功'})
        return true
      }
      return false
    }
  },
});
