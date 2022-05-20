package com.orca.back.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.orca.back.controller.UserController.getResult;

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

    private Result<?> checkAdmin(HttpServletRequest request) {
        return getResult(request, userMapper);
    }

    /*Load course constants*/
    @RequestMapping("/load_course_constants")
    public Result<?> loadCourseConstants(){
        List<ClassTime> classTimeList = classTimeMapper.selectList(null);
        List<Classroom> classroomList = classroomMapper.selectList(null);
        List<User> teacherList = userMapper.selectList(Wrappers.<User>lambdaQuery().eq(User::getRole, 1).eq(User::getIsAdmin, 0).isNotNull(User::getCollege));
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
        courseConstantsInfo.setCourseTimeStartList(classTimeList.stream().map(ClassTime::getId).collect(Collectors.toList()));
        courseConstantsInfo.setCourseTimeEndList(classTimeList.stream().map(ClassTime::getId).collect(Collectors.toList()));
        courseConstantsInfo.setClassRoomList(classroomList.stream().map(Classroom::getName).collect(Collectors.toList()));
        courseConstantsInfo.setTeacherList(teacherSelectInfoList);
        return Result.success(courseConstantsInfo);
    }


    /*Admin*/
    @PostMapping("/get_course_all")
    public Result<?> getCourseAll(HttpServletRequest request){
        /*Check Admin*/
        Result<?> result = checkAdmin(request);
        if (result != null) return result;
        /*Check Pass*/
        List<Course> courseList = courseMapper.selectList(null);
        List<CourseInfo> courseInfoList = new ArrayList<>();
        for (Course course : courseList){
            CourseInfo courseInfo = new CourseInfo();
            courseInfo.setCourse(course);
            User teacher = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getNumber, course.getCourseTeacher()));
            courseInfo.setCourseMajor(majorMapper.selectOne(Wrappers.<Major>lambdaQuery().eq(Major::getId, teacher.getMajor())).getName());
            courseInfo.setCourseDepartment(collegeMapper.selectOne(Wrappers.<College>lambdaQuery().eq(College::getId, teacher.getCollege())).getName());
            courseInfo.setCourseTeacher(teacher.getName() + " (工号: " + teacher.getNumber() + ")");
            courseInfoList.add(courseInfo);
        }
        return Result.success(courseInfoList);
    }

    //add course
    @PostMapping("/add_course")
    public Result<?> addCourse(@RequestBody Course course, HttpServletRequest request){
        /*Check Admin*/
        Result<?> result = checkAdmin(request);
        if (result != null) return result;
        /*Check Pass*/
        ErrorCode err = checker.checkCourse(course);
        if (err != null) return Result.error(err);
        course.setCourseTeacher(course.getCourseTeacher().split(":")[1].split("\\)")[0].strip());
        /*check if course exists*/
        Course repeatCourse = courseMapper.selectOne(Wrappers.<Course>lambdaQuery().eq(Course::getCourseId, course.getCourseId()).eq(Course::getCourseTeacher, course.getCourseTeacher()));
        if (repeatCourse != null) return Result.error(ErrorCode.E_314);
        /*check whether exists conflicts between coursePlace and courseTime*/
        List<Course> courseList1 = courseMapper.selectList(Wrappers.<Course>lambdaQuery().eq(Course::getCoursePlace, course.getCoursePlace()));
        int courseTimeStart = Integer.parseInt(course.getCourseTimeStart());
        int courseTimeEnd = Integer.parseInt(course.getCourseTimeEnd());
        for (Course course1 : courseList1){
            if (course1.getCourseId().equals(course.getCourseId())) continue;
            if (!course1.getCourseTimeDay().equals(course.getCourseTimeDay()))continue;
            int courseTimeStart1 = Integer.parseInt(course1.getCourseTimeStart());
            int courseTimeEnd1 = Integer.parseInt(course1.getCourseTimeEnd());
            if (courseTimeEnd < courseTimeStart1 || courseTimeStart > courseTimeEnd1) continue;
            else return Result.error(ErrorCode.E_318);
        }
        /*check the course list with the same courseId*/
        List<Course> courseList = courseMapper.selectList(Wrappers.<Course>lambdaQuery().eq(Course::getCourseId, course.getCourseId()));
        User teacher = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getNumber, course.getCourseTeacher()));
        for (Course c : courseList){
            User t = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getNumber, c.getCourseTeacher()));
            if (t.getMajor() != teacher.getMajor()) return Result.error(ErrorCode.E_315);
            c.updateCourse(course);
            courseMapper.update(c, Wrappers.<Course>lambdaQuery().eq(Course::getCourseId, c.getCourseId()).eq(Course::getCourseTeacher, c.getCourseTeacher()));
        }
        courseMapper.insert(course);
        return Result.success();
    }

    //edit course
    @PostMapping("/edit_course")
    public Result<?> editCourse(@RequestBody Course course, HttpServletRequest request){
        /*Check Admin*/
        Result<?> result = checkAdmin(request);
        if (result != null) return result;
        /*Check Pass*/
        ErrorCode err = checker.checkCourse(course);
        if (err != null) return Result.error(err);
        course.setCourseTeacher(course.getCourseTeacher().split(":")[1].split("\\)")[0].strip());
        /*check whether exists conflicts between coursePlace and courseTime*/
        List<Course> courseList1 = courseMapper.selectList(Wrappers.<Course>lambdaQuery().eq(Course::getCoursePlace, course.getCoursePlace()));
        int courseTimeStart = Integer.parseInt(course.getCourseTimeStart());
        int courseTimeEnd = Integer.parseInt(course.getCourseTimeEnd());
        for (Course course1 : courseList1){
            if (course1.getCourseId().equals(course.getCourseId())) continue;
            if (!course1.getCourseTimeDay().equals(course.getCourseTimeDay()))continue;
            int courseTimeStart1 = Integer.parseInt(course1.getCourseTimeStart());
            int courseTimeEnd1 = Integer.parseInt(course1.getCourseTimeEnd());
            if (courseTimeEnd < courseTimeStart1 || courseTimeStart > courseTimeEnd1) continue;
            else return Result.error(ErrorCode.E_318);
        }
        /*check the course list with the same courseId*/
        List<Course> courseList = courseMapper.selectList(Wrappers.<Course>lambdaQuery().eq(Course::getCourseId, course.getCourseId()));
        for (Course c : courseList){
            c.updateCourse(course);
            courseMapper.update(c, Wrappers.<Course>lambdaUpdate().eq(Course::getCourseId, c.getCourseId()).eq(Course::getCourseTeacher, c.getCourseTeacher()));
        }
        return Result.success();
    }

    //delete course by id
    @PostMapping("/delete_course")
    public Result<?> deleteCourse(@RequestBody Course course, HttpServletRequest request){
        /*Check Admin*/
        Result<?> result = checkAdmin(request);
        if (result != null) return result;
        /*Check Pass*/
        course.setCourseTeacher(course.getCourseTeacher().split(":")[1].split("\\)")[0].strip());
        courseMapper.delete(Wrappers.<Course>lambdaQuery().eq(Course::getCourseId, course.getCourseId()).eq(Course::getCourseTeacher, course.getCourseTeacher()));
        return Result.success();
    }

    //get course application all
    @PostMapping("/get_course_application_all")
    public Result<?> getCourseApplicationAll(HttpServletRequest request){
        /*Check Admin*/
        Result<?> result = checkAdmin(request);
        if (result != null) return result;
        /*Check Pass*/
        //use courseTeacher to present name:(工号: number)
        List<CourseApplication> courseApplicationList = courseApplicationMapper.selectList(Wrappers.<CourseApplication>lambdaQuery().eq(CourseApplication::getApplicationStatus, 0));
        for (CourseApplication courseApplication : courseApplicationList) {
            courseApplication.translateApplicationType();
            courseApplication.translateApplicationStatus();
            User teacher = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getNumber, courseApplication.getCourseTeacher()));
            courseApplication.setCourseTeacher(teacher.getName().concat(" (工号: ").concat(teacher.getNumber().toString()).concat(")"));
        }
        return Result.success(courseApplicationList);
    }

    //update course application status
    @PostMapping("/update_course_application_status")
    public Result<?> updateCourseApplicationStatus(@RequestBody CourseApplication courseApplication, HttpServletRequest request){
        /*Check Admin*/
        Result<?> result = checkAdmin(request);
        if (result != null) return result;
        /*Check Pass*/
        courseApplication.transBackApplicationType();
        courseApplication.setCourseTeacher(courseApplication.getCourseTeacher().split(":")[1].split("\\)")[0].strip());
        courseApplicationMapper.updateById(courseApplication);
        return Result.success();
    }

    //batch import
    @PostMapping("/batch_import")
    public Result<?> batchImport(@RequestPart(value = "file") final MultipartFile uploadfile, HttpServletRequest request) throws IOException {
        /*Check Admin*/
        Result<?> result = checkAdmin(request);
        if (result != null) return result;
        /*Check Pass*/
        try(InputStreamReader isr = new InputStreamReader(uploadfile.getInputStream(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr)) {
            String line;
            List<Course> courseList = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                String[] str = line.split(",");
                //if any of the 10 field is empty, skip
                if (str[0].isEmpty() || str[1].isEmpty() || str[2].isEmpty() || str[3].isEmpty() || str[4].isEmpty() || str[5].isEmpty() || str[6].isEmpty() || str[7].isEmpty() || str[8].isEmpty() || str[9].isEmpty()) return Result.error(ErrorCode.E_316);
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
                //check whether courseTeacher and coursePlace exist
                //check coursePlace whether exist
                Classroom classroom = classroomMapper.selectOne(Wrappers.<Classroom>lambdaQuery().eq(Classroom::getName, course.getCoursePlace()));
                User teacher = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getNumber, course.getCourseTeacher()));
                if (classroom == null || teacher == null) return Result.error(ErrorCode.E_317);
                courseList.add(course);
            }
            for (Course course : courseList) {
                courseMapper.insert(course);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        List<Course> courseList = courseMapper.selectList(Wrappers.<Course>lambdaQuery().eq(Course::getCourseTeacher, user.getNumber()));
        List<CourseInfo> courseInfoList = new ArrayList<>();
        for (Course course : courseList){
            CourseInfo courseInfo = new CourseInfo();
            courseInfo.setCourse(course);
            User teacher = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getNumber, course.getCourseTeacher()));
            courseInfo.setCourseMajor(majorMapper.selectOne(Wrappers.<Major>lambdaQuery().eq(Major::getId, teacher.getMajor())).getName());
            courseInfo.setCourseDepartment(collegeMapper.selectOne(Wrappers.<College>lambdaQuery().eq(College::getId, teacher.getCollege())).getName());
            courseInfo.setCourseTeacher(teacher.getName().concat(" (工号: ").concat(teacher.getNumber().toString()).concat(")"));
            courseInfoList.add(courseInfo);
        }
        return Result.success(courseInfoList);
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
        //The teacher can only send courses concerning his/her own department
        courseApplication.setCourseTeacher(courseApplication.getApplicantNumber());
        /*Check Form*/
        err = checker.checkCourseApplication(courseApplication);
        if (err != null){
            return Result.error(err);
        }
        /*check whether classtime has been occupied*/
        if (courseApplication.getApplicationType().equals("1") || courseApplication.getApplicationType().equals("3")){
            /*check whether exists conflicts between coursePlace and courseTime*/
            List<Course> courseList1 = courseMapper.selectList(Wrappers.<Course>lambdaQuery().eq(Course::getCoursePlace, courseApplication.getCoursePlace()));
            int courseTimeStart = Integer.parseInt(courseApplication.getCourseTimeStart());
            int courseTimeEnd = Integer.parseInt(courseApplication.getCourseTimeEnd());
            for (Course course1 : courseList1){
                if (course1.getCourseId().equals(courseApplication.getCourseId())) continue;
                if (!course1.getCourseTimeDay().equals(courseApplication.getCourseTimeDay()))continue;
                int courseTimeStart1 = Integer.parseInt(course1.getCourseTimeStart());
                int courseTimeEnd1 = Integer.parseInt(course1.getCourseTimeEnd());
                if (courseTimeEnd < courseTimeStart1 || courseTimeStart > courseTimeEnd1) continue;
                else return Result.error(ErrorCode.E_318);
            }
        }
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
        List<CourseApplication> courseApplicationList = courseApplicationMapper.selectList(Wrappers.<CourseApplication>lambdaQuery().eq(CourseApplication::getApplicantNumber, user.getNumber()));
        for (CourseApplication courseApplication : courseApplicationList) {
            courseApplication.translateApplicationType();
            courseApplication.translateApplicationStatus();
            courseApplication.setCourseTeacher(user1.getName() + " (工号: " + user1.getNumber() + ")");
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
        List<User> teacherList = userMapper.selectList(Wrappers.<User>lambdaQuery().eq(User::getMajor, major.getId()).eq(User::getRole, 1).eq(User::getIsAdmin, 0).isNotNull(User::getMajor));
        List<Course> courseList = new ArrayList<>();
        for (User teacher : teacherList) {
            List<Course> courseList1 = courseMapper.selectList(Wrappers.<Course>lambdaQuery().eq(Course::getCourseTeacher, teacher.getNumber()));
            //add courseList1 to courseList
            courseList.addAll(courseList1);
        }
        List<CourseInfo> courseInfoList = new ArrayList<>();
        for (Course course : courseList){
            CourseInfo courseInfo = new CourseInfo();
            courseInfo.setCourse(course);
            User teacher = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getNumber, course.getCourseTeacher()));
            courseInfo.setCourseMajor(majorMapper.selectOne(Wrappers.<Major>lambdaQuery().eq(Major::getId, teacher.getMajor())).getName());
            courseInfo.setCourseDepartment(collegeMapper.selectOne(Wrappers.<College>lambdaQuery().eq(College::getId, teacher.getCollege())).getName());
            courseInfo.setCourseTeacher(teacher.getName());
            courseInfoList.add(courseInfo);
        }
        return Result.success(courseInfoList);
    }
}
