package com.orca.back.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.orca.back.entity.*;
import com.orca.back.mapper.*;
import com.orca.back.utils.common.Checker;
import com.orca.back.utils.common.Result;
import com.orca.back.utils.constants.ErrorCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Resource
    UserMapper userMapper;
    @Resource
    CourseMapper courseMapper;
    @Resource
    CourseApplicationMapper courseApplicationMapper;
    @Resource
    ConstantsMapper constantsMapper;
    @Resource
    ClassTimeMapper classTimeMapper;
    @Resource
    ClassroomMapper classroomMapper;
    @Resource
    CollegeMapper collegeMapper;
    @Resource
    MajorMapper majorMapper;
    @Resource
    CourseSelectionStateMapper courseSelectionStateMapper;

    Checker checker = new Checker();

    /*Load course constants*/
    @RequestMapping("/load_course_constants")
    public Result<?> loadCourseConstants(HttpServletRequest request){
        List<ClassTime> classTimeList = classTimeMapper.selectList(null);
        List<Classroom> classroomList = classroomMapper.selectList(null);
        List<User> teacherList = userMapper.selectList(Wrappers.<User>lambdaQuery().eq(User::getRole, 1).eq(User::getIsAdmin, 0).isNotNull(User::getCollege));
        List<College> collegeList = collegeMapper.selectList(null);
        List<Major> majorList = majorMapper.selectList(null);
        List<TeacherSelectInfo> teacherSelectInfoList = new ArrayList<>();

        for (User teacher : teacherList){
            TeacherSelectInfo teacherSelectInfo = new TeacherSelectInfo();
            teacherSelectInfo.setLabel(teacher.getName() + " (工号: " + teacher.getNumber() + ")");
            teacherSelectInfo.setValue(teacher.getName() + " (工号: " + teacher.getNumber() + ")");
            String major = majorMapper.selectOne(Wrappers.<Major>lambdaQuery().eq(Major::getId, teacher.getMajor())).getName();
            String college = collegeMapper.selectOne(Wrappers.<College>lambdaQuery().eq(College::getId, teacher.getCollege())).getName();
            teacherSelectInfo.setDescription("专业：" + major + "， 院系：" + college);
            teacherSelectInfoList.add(teacherSelectInfo);
        }

        CourseConstantsInfo courseConstantsInfo = new CourseConstantsInfo();
        courseConstantsInfo.setCourseTimeStartList(classTimeList.stream().map(ClassTime::getBegin).collect(Collectors.toList()));
        courseConstantsInfo.setCourseTimeEndList(classTimeList.stream().map(ClassTime::getEnd).collect(Collectors.toList()));
        courseConstantsInfo.setClassRoomList(classroomList.stream().map(Classroom::getName).collect(Collectors.toList()));
        courseConstantsInfo.setTeacherList(teacherSelectInfoList);
        courseConstantsInfo.setDepartmentList(collegeList.stream().map(College::getName).collect(Collectors.toList()));
        courseConstantsInfo.setMajorList(majorList.stream().map(Major::getName).collect(Collectors.toList()));
        return Result.success(courseConstantsInfo);
    }


    /*Admin*/
    @PostMapping("/get_course_all")
    public Result<?> getCourseAll(HttpServletRequest request){
        /*Check Admin*/
        ErrorCode err = checker.checkLogin(request);
        if (err != null){
            return Result.error(err);
        }
        Integer userId = (Integer) request.getSession().getAttribute("UserId");
        User user = userMapper.selectById(userId);
        if (user == null || user.getIsAdmin() == 0) return Result.error(ErrorCode.E_111);
        /*Check Pass*/
        Page<Course> page = courseMapper.selectPage(new Page<>(1, 10), null);
        List<Course> courseList = courseMapper.selectList(null);
        return Result.success(courseList);
    }

    //add course
    @PostMapping("/add_course")
    public Result<?> addCourse(@RequestBody Course course, HttpServletRequest request){
        /*Check Admin*/
        ErrorCode err = checker.checkLogin(request);
        if (err != null) return Result.error(err);

        Integer userId = (Integer) request.getSession().getAttribute("UserId");
        User user = userMapper.selectById(userId);
        if (user == null || user.getIsAdmin() == 0) return Result.error(ErrorCode.E_111);
        /*Check Pass*/
        /*Add major and Department to course*/
        Integer teacherNumber = Integer.parseInt(course.getCourseTeacher().split(":")[1].split("\\)")[0].strip());
        User teacher = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getNumber, teacherNumber));
        Major major = majorMapper.selectOne(Wrappers.<Major>lambdaQuery().eq(Major::getId, teacher.getMajor()));
        College college = collegeMapper.selectOne(Wrappers.<College>lambdaQuery().eq(College::getId, teacher.getCollege()));
        course.setCourseMajor(major.getName());
        course.setCourseDepartment(college.getName());

        err = checker.checkCourse(course);
        if (err != null) return Result.error(err);

        courseMapper.insert(course);
        return Result.success();
    }

    //edit course
    @PostMapping("/edit_course")
    public Result<?> editCourse(@RequestBody Course course, HttpServletRequest request){
        /*Check Admin*/
        ErrorCode err = checker.checkLogin(request);
        if (err != null){
            return Result.error(err);
        }
        Integer userId = (Integer) request.getSession().getAttribute("UserId");
        User user = userMapper.selectById(userId);
        if (user == null || user.getIsAdmin() == 0) return Result.error(ErrorCode.E_111);
        /*Check Pass*/
        err = checker.checkCourse(course);
        if (err != null) return Result.error(err);

        courseMapper.updateById(course);
        return Result.success();
    }

    //delete course by id
    @PostMapping("/delete_course")
    public Result<?> deleteCourse(@RequestBody Course course, HttpServletRequest request){
        /*Check Admin*/
        ErrorCode err = checker.checkLogin(request);
        if (err != null){
            return Result.error(err);
        }
        Integer userId = (Integer) request.getSession().getAttribute("UserId");
        User user = userMapper.selectById(userId);
        if (user == null || user.getIsAdmin() == 0) return Result.error(ErrorCode.E_111);
        /*Check Pass*/
        courseMapper.delete(Wrappers.<Course>lambdaQuery().eq(Course::getCourseId, course.getCourseId()).eq(Course::getCourseTeacher, course.getCourseTeacher()));
        return Result.success();
    }

    //get course application all
    @PostMapping("/get_course_application_all")
    public Result<?> getCourseApplicationAll(HttpServletRequest request){
        /*Check Admin*/
        ErrorCode err = checker.checkLogin(request);
        if (err != null){
            return Result.error(err);
        }
        Integer userId = (Integer) request.getSession().getAttribute("UserId");
        User user = userMapper.selectById(userId);
        if (user == null || user.getIsAdmin() == 0) return Result.error(ErrorCode.E_111);
        /*Check Pass*/
        List<CourseApplication> courseApplicationList = courseApplicationMapper.selectList(Wrappers.<CourseApplication>lambdaQuery().eq(CourseApplication::getApplicationStatus, 0));
        for (CourseApplication courseApplication : courseApplicationList) {
            courseApplication.translateApplicationType();
            courseApplication.translateApplicationStatus();
        }
        return Result.success(courseApplicationList);
    }

    //update course application status
    @PostMapping("/update_course_application_status")
    public Result<?> updateCourseApplicationStatus(@RequestBody CourseApplication courseApplication, HttpServletRequest request){
        /*Check Admin*/
        ErrorCode err = checker.checkLogin(request);
        if (err != null){
            return Result.error(err);
        }
        Integer userId = (Integer) request.getSession().getAttribute("UserId");
        User user = userMapper.selectById(userId);
        if (user == null || user.getIsAdmin() == 0) return Result.error(ErrorCode.E_111);
        /*Check Pass*/
        System.out.println(courseApplication);
        courseApplication.transBackApplicationType();
        courseApplicationMapper.updateById(courseApplication);
        return Result.success();
    }

    //batch import
    @PostMapping("/batch_import")
    public Result<?> batchImport(@RequestPart(value = "file") final MultipartFile uploadfile, HttpServletRequest request) throws IOException {
        System.out.println(uploadfile.getOriginalFilename());
        /*Check Admin*/
        ErrorCode err = checker.checkLogin(request);
        if (err != null){
            return Result.error(err);
        }
        Integer userId = (Integer) request.getSession().getAttribute("UserId");
        User user = userMapper.selectById(userId);
        if (user == null || user.getIsAdmin() == 0) return Result.error(ErrorCode.E_111);
        /*Check Pass*/
        InputStreamReader isr = new InputStreamReader(uploadfile.getInputStream(), "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line;
        List<Course> courseList = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            String[] str = line.split(",");
            Course course = new Course();
            course.setCourseId(str[0]);
            course.setCourseName(str[1]);
            course.setCourseTimeDay(str[2]);
            course.setCourseTimeStart(str[3]);
            course.setCourseTimeEnd(str[4]);
            course.setCourseTime(str[2] + " : " + str[3] + " - " + str[4]);
            course.setCoursePlace(str[5]);
            course.setCourseTeacher(str[6]);
            course.setCourseCredit(Integer.parseInt(str[7]));
            course.setCourseCreditHour(Integer.parseInt(str[8]));
            course.setCourseCapacity(Integer.parseInt(str[9]));
            course.setCourseDescription(str[10]);
            courseList.add(course);
        }
        for (Course course : courseList) {
            courseMapper.insert(course);
        }
        System.out.println(courseList);
        return Result.success();
    }

    /*Teacher*/
    //get course teacher
    @PostMapping("/get_course_teacher")
    public Result<?> getCourseTeacher(@RequestBody User user, HttpServletRequest request){
        /*Check Teacher*/
        ErrorCode err = checker.checkLogin(request);
        if (err != null){
            return Result.error(err);
        }
        Integer userId = (Integer) request.getSession().getAttribute("UserId");
        User user1 = userMapper.selectById(userId);
        if (user1 == null || user1.getRole() != 1) return Result.error(ErrorCode.E_111);
        /*Check Pass*/
        List<Course> courseList = courseMapper.selectList(Wrappers.<Course>lambdaQuery().eq(Course::getCourseTeacher, user.getName().concat(" (工号: ").concat(user.getNumber().toString()).concat(")")));
        for (Course course : courseList) {
            course.setCourseTeacher(user.getName());
        }
        return Result.success(courseList);
    }

    //send course application
    @PostMapping("/send_course_application")
    public Result<?> sendCourseApplication(@RequestBody CourseApplication courseApplication, HttpServletRequest request){
        /*Check Teacher*/
        ErrorCode err = checker.checkLogin(request);
        if (err != null){
            return Result.error(err);
        }
        Integer userId = (Integer) request.getSession().getAttribute("UserId");
        User user1 = userMapper.selectById(userId);
        if (user1 == null || user1.getRole() != 1) return Result.error(ErrorCode.E_111);
        /*add major and college*/
        User teacher = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getNumber, courseApplication.getApplicantNumber()));
        Major major = majorMapper.selectOne(Wrappers.<Major>lambdaQuery().eq(Major::getId, teacher.getMajor()));
        College college = collegeMapper.selectOne(Wrappers.<College>lambdaQuery().eq(College::getId, teacher.getCollege()));
        courseApplication.setCourseMajor(major.getName());
        courseApplication.setCourseDepartment(college.getName());
        /*Check Form*/
        err = checker.checkCourseApplication(courseApplication);
        if (err != null){
            return Result.error(err);
        }
        System.out.println(courseApplication);
        /*set application id*/
        Constants courseApplicationId = constantsMapper.selectOne(Wrappers.<Constants>lambdaQuery().eq(Constants::getConstantName, "course_application_id"));
        courseApplication.setApplicationId(courseApplicationId.getConstantValue());
        //set application time
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        courseApplication.setApplicationTime(simpleDateFormat.format(System.currentTimeMillis()));
        courseApplicationMapper.insert(courseApplication);
        //update constants by 1
        courseApplicationId.setConstantValue(new BigInteger(courseApplicationId.getConstantValue()).add(BigInteger.ONE).toString());
        constantsMapper.updateById(courseApplicationId);
        return Result.success();
    }

    //get course application teacher
    @PostMapping("/get_course_application_teacher")
    public Result<?> getCourseApplicationTeacher(@RequestBody User user, HttpServletRequest request){
        /*Check Teacher*/
        ErrorCode err = checker.checkLogin(request);
        if (err != null){
            return Result.error(err);
        }
        Integer userId = (Integer) request.getSession().getAttribute("UserId");
        User user1 = userMapper.selectById(userId);
        if (user1 == null || user1.getRole() != 1) return Result.error(ErrorCode.E_111);
        /*Check Pass*/
        List<CourseApplication> courseApplicationList = courseApplicationMapper.selectList(Wrappers.<CourseApplication>lambdaQuery().eq(CourseApplication::getApplicantName, user.getName()));
        for (CourseApplication courseApplication : courseApplicationList) {
            courseApplication.translateApplicationType();
            courseApplication.translateApplicationStatus();
        }
        return Result.success(courseApplicationList);
    }

    /*Student*/
    //get course list student
    @PostMapping("/get_course_student")
    public Result<?> getCourseListStudent(@RequestBody User user, HttpServletRequest request){
        /*Check Student*/
        ErrorCode err = checker.checkLogin(request);
        if (err != null){
            return Result.error(err);
        }
        Integer userId = (Integer) request.getSession().getAttribute("UserId");
        User user1 = userMapper.selectById(userId);
        if (user1 == null || user1.getRole() != 2) return Result.error(ErrorCode.E_111);
        /*选课是否开放*/
        if (!courseSelectionStateMapper.selectOne(Wrappers.<CourseSelectionState>lambdaQuery().eq(CourseSelectionState::getYear, 2022).eq(CourseSelectionState::getSemester, 1)).getOpen())
            return Result.error(ErrorCode.E_400);
        /*Check Pass*/
        Major major = majorMapper.selectOne(Wrappers.<Major>lambdaQuery().eq(Major::getId, user.getMajor()));
        List<Course> courseList = courseMapper.selectList(Wrappers.<Course>lambdaQuery().eq(Course::getCourseMajor, major.getName()));
        for (Course course : courseList) {
            course.setCourseTeacher(course.getCourseTeacher().split("\\(")[0]);
        }
        return Result.success(courseList);
    }
}
