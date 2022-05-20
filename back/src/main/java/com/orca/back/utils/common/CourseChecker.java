package com.orca.back.utils.common;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.orca.back.entity.Course;
import com.orca.back.entity.CourseApplication;
import com.orca.back.entity.User;
import com.orca.back.mapper.ConstantsMapper;
import com.orca.back.mapper.CourseMapper;
import com.orca.back.mapper.UserMapper;
import com.orca.back.utils.constants.ErrorCode;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

public class CourseChecker {

    Checker checker = new Checker();
    CourseUtils courseUtils = new CourseUtils();

    public ErrorCode checkAuthTeacher(HttpServletRequest request, UserMapper userMapper){
        ErrorCode err = checker.checkLogin(request);
        if (err == null){
            Integer userId = (Integer) request.getSession().getAttribute("UserId");
            User user = userMapper.selectById(userId);
            if (user == null || user.getRole() != 1) err = ErrorCode.E_111;
        }
        return err;
    }

    public ErrorCode checkAuthStudent(HttpServletRequest request, UserMapper userMapper){
        ErrorCode err = checker.checkLogin(request);
        if (err == null){
            Integer userId = (Integer) request.getSession().getAttribute("UserId");
            User user = userMapper.selectById(userId);
            if (user == null || user.getRole() != 2) err = ErrorCode.E_111;
        }
        return err;
    }

    public ErrorCode checkAuthAdmin(HttpServletRequest request, UserMapper userMapper){
        ErrorCode err = checker.checkLogin(request);
        if (err == null){
            Integer userId = (Integer) request.getSession().getAttribute("UserId");
            User user = userMapper.selectById(userId);
            if (user == null || user.getIsAdmin() != 1) err = ErrorCode.E_111;
        }
        return err;
    }

    public ErrorCode checkCourse(Course course, Boolean isAdd, CourseMapper courseMapper, UserMapper userMapper){
        ErrorCode err = checkCourseForm(course);
        if (err == null) err = isAdd ? checkNewCourse(course, courseMapper, userMapper) : null;
        if (err == null) err = checkTimePlaceConflict(course, courseMapper);
        return err;
    }

    public ErrorCode checkCourseApplication(CourseApplication courseApplication, CourseMapper courseMapper){
        ErrorCode err = checkCourseApplicationForm(courseApplication);
        Course course = courseUtils.extractCourseFromApplication(courseApplication);
        if (err == null && (courseApplication.getApplicationType().equals("1") || courseApplication.getApplicationType().equals("3"))){
            err = checkTimePlaceConflict(course, courseMapper);
        }
        return err;
    }

    public ErrorCode checkCourseApplicationForm(CourseApplication courseApplication){
        ErrorCode err = checkCourseId(courseApplication.getCourseId());
        if (err == null) err = checker.checkName(courseApplication.getCourseName()) == null ? null : ErrorCode.E_302;
        if(err == null) err = checkCourseFormPre5(courseApplication.getCourseTimeDay(), courseApplication.getCourseTimeStart(), courseApplication.getCourseTimeEnd(), courseApplication.getCoursePlace(), courseApplication.getCourseTeacher());
        if (err == null) err = checkCourseFormLast3(courseApplication.getCourseCredit(), courseApplication.getCourseCreditHour(), courseApplication.getCourseCapacity());
        if (err == null) err = checkCourseDescription(courseApplication.getCourseDescription());
        return err;
    }

    public ErrorCode checkCourseForm(Course course){
        ErrorCode err = checkCourseId(course.getCourseId());
        if(err == null)err = checker.checkName(course.getCourseName()) == null ? null : ErrorCode.E_302;
        if(err == null)err = checkCourseFormPre5(course.getCourseTimeDay(), course.getCourseTimeStart(), course.getCourseTimeEnd(), course.getCoursePlace(), course.getCourseTeacher());
        if(err == null)err = checkCourseFormLast3(course.getCourseCredit(), course.getCourseCreditHour(), course.getCourseCapacity());
        if(err == null)err = checkCourseDescription(course.getCourseDescription());
        return err;
    }

    public ErrorCode checkTimePlaceConflict(Course course, CourseMapper courseMapper){
        List<Course> courseList = courseMapper.selectList(Wrappers.<Course>lambdaQuery().eq(Course::getCoursePlace, course.getCoursePlace()));
        int courseTimeStart = Integer.parseInt(course.getCourseTimeStart());
        int courseTimeEnd = Integer.parseInt(course.getCourseTimeEnd());
        for (Course course1 : courseList){
            if (course1.getCourseId().equals(course.getCourseId())) continue;
            if (!course1.getCourseTimeDay().equals(course.getCourseTimeDay()))continue;
            int courseTimeStart1 = Integer.parseInt(course1.getCourseTimeStart());
            int courseTimeEnd1 = Integer.parseInt(course1.getCourseTimeEnd());
            if (courseTimeEnd < courseTimeStart1 || courseTimeStart > courseTimeEnd1) continue;
            return ErrorCode.E_318;
        }
        return null;
    }

    public ErrorCode checkCourseMajorMatch(Course course,
                                           CourseMapper courseMapper,
                                           UserMapper userMapper){
        Course course1 = courseMapper.selectOne(Wrappers.<Course>lambdaQuery().eq(Course::getCourseId, course.getCourseId()));
        if (course1 != null){
            User teacher = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getNumber, course.getCourseTeacher()));
            User teacher1 = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getNumber, course1.getCourseTeacher()));
            if (!teacher.getMajor().equals(teacher1.getMajor())) return ErrorCode.E_315;
        }
        return null;
    }

    public ErrorCode checkNewCourse(Course course, CourseMapper courseMapper, UserMapper userMapper){
        //检查课程是否已经存在
        Course repeatCourse = courseMapper.selectOne(Wrappers.<Course>lambdaQuery().eq(Course::getCourseId, course.getCourseId()).eq(Course::getCourseTeacher, course.getCourseTeacher()));
        ErrorCode err = repeatCourse == null ? null : ErrorCode.E_314;
        //检查课程专业与已有同ID课程是否匹配
        if (err == null) err = checkCourseMajorMatch(course, courseMapper, userMapper);
        return err;
    }

    public ErrorCode checkCourseSelectionState(ConstantsMapper constantsMapper){
        if (Objects.equals(constantsMapper.getCourseSelectionState().getConstantValue(), "0"))
            return ErrorCode.E_400;
        return null;
    }

    /*common checkers*/
    public ErrorCode checkCourseId(String courseId){
        return (courseId == null || courseId.length() == 0 || !courseId.matches("[A-Z]+[0-9]+")) ? ErrorCode.E_301 : null;
    }

    public ErrorCode checkCourseFormPre5(String courseTimeDay,
                                         String courseTimeStart,
                                         String courseTimeEnd,
                                         String classroom,
                                         String teacher){
        ErrorCode err = courseTimeDay == null || courseTimeDay.length() == 0 ? ErrorCode.E_303 : null;
        if(err == null)
            err = courseTimeStart == null || courseTimeStart.length() == 0 ? ErrorCode.E_304 : null;
        if(err == null)
            err = courseTimeEnd == null || courseTimeEnd.length() == 0 ? ErrorCode.E_305 : null;
        if(err == null)
            err = classroom == null || classroom.length() == 0 ? ErrorCode.E_306 : null;
        if(err == null)
            err = teacher == null || teacher.length() == 0 ? ErrorCode.E_307 : null;
        if(err == null)
            err = courseTimeStart.compareTo(courseTimeEnd) >= 0 ? ErrorCode.E_203 : null;
        return err;
    }

    public ErrorCode checkCourseFormLast3(Integer credit,
                                          Integer creditHour,
                                          Integer capacity){
        ErrorCode err = credit == null || credit == 0 ? ErrorCode.E_310 : null;
        if(err == null)
            err = creditHour == null || creditHour == 0 ? ErrorCode.E_311 : null;
        if(err == null)
            err = capacity == null || capacity == 0 ? ErrorCode.E_312 : null;
        return err;
    }

    public ErrorCode checkCourseDescription(String courseDescription){
        return (courseDescription == null || courseDescription.length() == 0) ? ErrorCode.E_313 : null;
    }
}
