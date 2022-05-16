package com.orca.back.utils.common;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.orca.back.entity.*;
import com.orca.back.mapper.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CourseUtils {

    public CourseConstantsInfo getCourseConstantsInfo(ClassTimeMapper classTimeMapper,
                                                      ClassroomMapper classroomMapper,
                                                      UserMapper userMapper,
                                                      MajorMapper majorMapper,
                                                      CollegeMapper collegeMapper) {
        List<ClassTime> classTimeList = classTimeMapper.selectList(null);
        List<Classroom> classroomList = classroomMapper.selectList(null);
        List<User> teacherList = userMapper.selectList(Wrappers.<User>lambdaQuery().eq(User::getRole, 1).eq(User::getIsAdmin, 0).isNotNull(User::getCollege));
        List<TeacherSelectInfo> teacherSelectInfoList = getTeacherSelectInfoList(teacherList, majorMapper, collegeMapper);
        CourseConstantsInfo courseConstantsInfo = new CourseConstantsInfo();
        courseConstantsInfo.setCourseTimeStartList(classTimeList.stream().map(ClassTime::getId).collect(Collectors.toList()));
        courseConstantsInfo.setCourseTimeEndList(classTimeList.stream().map(ClassTime::getId).collect(Collectors.toList()));
        courseConstantsInfo.setClassRoomList(classroomList.stream().map(Classroom::getName).collect(Collectors.toList()));
        courseConstantsInfo.setTeacherList(teacherSelectInfoList);
        return courseConstantsInfo;
    }

    public List<TeacherSelectInfo> getTeacherSelectInfoList(List<User> teacherList,
                                                            MajorMapper majorMapper,
                                                            CollegeMapper collegeMapper) {
        List<TeacherSelectInfo> teacherSelectInfoList = new ArrayList<>();
        for (User teacher : teacherList){
            TeacherSelectInfo teacherSelectInfo = new TeacherSelectInfo();
            teacherSelectInfo.setLabel(teacherId2Str(teacher.getName(), teacher.getNumber()));
            teacherSelectInfo.setValue(teacherId2Str(teacher.getName(), teacher.getNumber()));
            String major = majorMapper.selectOne(Wrappers.<Major>lambdaQuery().eq(Major::getId, teacher.getMajor())).getName();
            String college = collegeMapper.selectOne(Wrappers.<College>lambdaQuery().eq(College::getId, teacher.getCollege())).getName();
            teacherSelectInfo.setDescription("专业：" + major + "， 院系：" + college);
            teacherSelectInfoList.add(teacherSelectInfo);
        }
        return teacherSelectInfoList;
    }

    public List<CourseInfo> getCourseInfoList(List<Course> courseList,
                                              Boolean isStudent,
                                              UserMapper userMapper,
                                              MajorMapper majorMapper,
                                              CollegeMapper collegeMapper) {
        List<CourseInfo> courseInfoList = new ArrayList<>();
        for (Course course : courseList){
            CourseInfo courseInfo = new CourseInfo();
            courseInfo.setCourse(course);
            User teacher = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getNumber, course.getCourseTeacher()));
            courseInfo.setCourseMajor(majorMapper.selectOne(Wrappers.<Major>lambdaQuery().eq(Major::getId, teacher.getMajor())).getName());
            courseInfo.setCourseDepartment(collegeMapper.selectOne(Wrappers.<College>lambdaQuery().eq(College::getId, teacher.getCollege())).getName());
            if (isStudent) courseInfo.setCourseTeacher(teacher.getName());
            else courseInfo.setCourseTeacher(teacherId2Str(teacher.getName(), teacher.getNumber()));
            courseInfoList.add(courseInfo);
        }
        return courseInfoList;
    }

    public List<Course> getCourseListStudent(User user,
                                             UserMapper userMapper,
                                             CourseMapper courseMapper,
                                             MajorMapper majorMapper){
        Major major = majorMapper.selectOne(Wrappers.<Major>lambdaQuery().eq(Major::getId, user.getMajor()));
        List<User> teacherList = userMapper.selectList(Wrappers.<User>lambdaQuery().eq(User::getMajor, major.getId()).eq(User::getRole, 1).eq(User::getIsAdmin, 0).isNotNull(User::getMajor));
        List<Course> courseList = new ArrayList<>();
        for (User teacher : teacherList) {
            List<Course> courseList1 = courseMapper.selectList(Wrappers.<Course>lambdaQuery().eq(Course::getCourseTeacher, teacher.getNumber()));
            //add courseList1 to courseList
            courseList.addAll(courseList1);
        }
        return courseList;
    }

    public List<CourseApplication> getCourseApplicationList(CourseApplicationMapper courseApplicationMapper, UserMapper userMapper){
        List<CourseApplication> courseApplicationList = courseApplicationMapper.selectList(Wrappers.<CourseApplication>lambdaQuery().eq(CourseApplication::getApplicationStatus, 0));
        for (CourseApplication courseApplication : courseApplicationList) {
            courseApplication.translateApplicationType();
            courseApplication.translateApplicationStatus();
            User teacher = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getNumber, courseApplication.getCourseTeacher()));
            courseApplication.teacherId2Str(teacher.getName(), teacher.getNumber());
        }
        return courseApplicationList;
    }

    public List<CourseApplication> getCourseApplicationListTeacher(CourseApplicationMapper courseApplicationMapper, User user){
        List<CourseApplication> courseApplicationList = courseApplicationMapper.selectList(Wrappers.<CourseApplication>lambdaQuery().eq(CourseApplication::getApplicantNumber, user.getNumber()));
        for (CourseApplication courseApplication : courseApplicationList) {
            courseApplication.translateApplicationType();
            courseApplication.translateApplicationStatus();
            courseApplication.teacherId2Str(user.getName(), user.getNumber());
        }
        return courseApplicationList;
    }

    public void updateCourseBatch(Course course, CourseMapper courseMapper){
        List<Course> courseList = courseMapper.selectList(Wrappers.<Course>lambdaQuery().eq(Course::getCourseId, course.getCourseId()));
        for (Course c : courseList){
            c.updateCourse(course);
            courseMapper.update(c, Wrappers.<Course>lambdaUpdate().eq(Course::getCourseId, c.getCourseId()).eq(Course::getCourseTeacher, c.getCourseTeacher()));
        }
    }

    public void incCourseApplicationId(ConstantsMapper constantsMapper){
        Constants courseApplicationId = constantsMapper.selectOne(Wrappers.<Constants>lambdaQuery().eq(Constants::getConstantName, "course_application_id"));
        courseApplicationId.setConstantValue(new BigInteger(courseApplicationId.getConstantValue()).add(BigInteger.ONE).toString());
        constantsMapper.updateById(courseApplicationId);
    }

    public Course extractCourseFromApplication(CourseApplication courseApplication) {
        Course course = new Course();
        course.setCourseId(courseApplication.getCourseId());
        course.setCourseName(courseApplication.getCourseName());
        course.setCourseTime(courseApplication.getCourseTime());
        course.setCourseTimeDay(courseApplication.getCourseTimeDay());
        course.setCourseTimeStart(courseApplication.getCourseTimeStart());
        course.setCourseTimeEnd(courseApplication.getCourseTimeEnd());
        course.setCourseTeacher(courseApplication.getCourseTeacher());
        course.setCoursePlace(courseApplication.getCoursePlace());
        course.setCourseCredit(courseApplication.getCourseCredit());
        course.setCourseCreditHour(courseApplication.getCourseCreditHour());
        course.setCourseCapacity(courseApplication.getCourseCapacity());
        course.setCourseDescription(courseApplication.getCourseDescription());
        return course;
    }

    public String teacherId2Str(String name, Integer number){
        return name + " (工号: " + number + ")";
    }
}
