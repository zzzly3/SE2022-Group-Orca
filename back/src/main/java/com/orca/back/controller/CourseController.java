package com.orca.back.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.orca.back.entity.*;
import com.orca.back.mapper.*;
import com.orca.back.utils.common.CourseChecker;
import com.orca.back.utils.common.CourseUtils;
import com.orca.back.utils.common.Result;
import com.orca.back.utils.constants.ErrorCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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

    CourseChecker courseChecker = new CourseChecker();
    CourseUtils courseUtils = new CourseUtils();

    /*Load course constants*/
    @RequestMapping("/load_course_constants")
    public Result<?> loadCourseConstants(){
        CourseConstantsInfo courseConstantsInfo = courseUtils.getCourseConstantsInfo(classTimeMapper, classroomMapper, userMapper, majorMapper, collegeMapper);
        return Result.success(courseConstantsInfo);
    }

    /*Admin*/
    @PostMapping("/get_course_all")
    public Result<?> getCourseAll(HttpServletRequest request){
        /*Check Admin*/
        ErrorCode err = courseChecker.checkAuthAdmin(request, userMapper);
        if (err != null) return Result.error(err);
        /*Check Pass*/
        List<Course> courseList = courseMapper.selectList(null);
        List<CourseInfo> courseInfoList = courseUtils.getCourseInfoList(courseList, false, userMapper, majorMapper, collegeMapper);
        return Result.success(courseInfoList);
    }

    //add course
    @PostMapping("/add_course")
    public Result<?> addCourse(@RequestBody Course course, HttpServletRequest request){
        /*Check Admin*/
        ErrorCode err = courseChecker.checkAuthAdmin(request, userMapper);
        if (err != null) return Result.error(err);
        /*Check Course*/
        course.teacherStr2Id();
        err = courseChecker.checkCourse(course, true, courseMapper, userMapper);
        if (err != null) return Result.error(err);
        /*Check pass*/
        courseUtils.updateCourseBatch(course, courseMapper);
        courseMapper.insert(course);
        return Result.success();
    }

    //edit course
    @PostMapping("/edit_course")
    public Result<?> editCourse(@RequestBody Course course, HttpServletRequest request){
        /*Check Admin*/
        ErrorCode err = courseChecker.checkAuthAdmin(request, userMapper);
        if (err != null) return Result.error(err);
        /*Check Course*/
        course.teacherStr2Id();
        err = courseChecker.checkCourse(course, false, courseMapper, userMapper);
        if (err != null) return Result.error(err);
        /*Check pass*/
        courseUtils.updateCourseBatch(course, courseMapper);
        return Result.success();
    }

    //delete course by id
    @PostMapping("/delete_course")
    public Result<?> deleteCourse(@RequestBody Course course, HttpServletRequest request){
        /*Check Admin*/
        ErrorCode err = courseChecker.checkAuthAdmin(request, userMapper);
        if (err != null) return Result.error(err);
        /*Check Pass*/
        course.teacherStr2Id();
        courseMapper.delete(Wrappers.<Course>lambdaQuery().eq(Course::getCourseId, course.getCourseId()).eq(Course::getCourseTeacher, course.getCourseTeacher()));
        return Result.success();
    }

    //get course application all
    @PostMapping("/get_course_application_all")
    public Result<?> getCourseApplicationAll(HttpServletRequest request){
        /*Check Admin*/
        ErrorCode err = courseChecker.checkAuthAdmin(request, userMapper);
        if (err != null) return Result.error(err);
        /*Check Pass*/
        List<CourseApplication> courseApplicationList = courseUtils.getCourseApplicationList(courseApplicationMapper, userMapper);
        return Result.success(courseApplicationList);
    }

    //update course application status
    @PostMapping("/update_course_application_status")
    public Result<?> updateCourseApplicationStatus(@RequestBody CourseApplication courseApplication, HttpServletRequest request){
        /*Check Admin*/
        ErrorCode err = courseChecker.checkAuthAdmin(request, userMapper);
        if (err != null) return Result.error(err);
        /*Check Pass*/
        courseApplication.transBackApplicationType();
        courseApplication.teacherStr2Id();
        courseApplicationMapper.updateById(courseApplication);
        return Result.success();
    }

    //batch import
    @PostMapping("/batch_import")
    public Result<?> batchImport(@RequestPart(value = "file") final MultipartFile uploadfile, HttpServletRequest request){
        /*Check Admin*/
        ErrorCode err = courseChecker.checkAuthAdmin(request, userMapper);
        if (err != null) return Result.error(err);
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
        ErrorCode err = courseChecker.checkAuthTeacher(request, userMapper);
        if (err != null) return Result.error(err);
        /*Check Pass*/
        List<Course> courseList = courseMapper.selectList(Wrappers.<Course>lambdaQuery().eq(Course::getCourseTeacher, user.getNumber()));
        List<CourseInfo> courseInfoList = courseUtils.getCourseInfoList(courseList, false, userMapper, majorMapper, collegeMapper);
        return Result.success(courseInfoList);
    }

    //send course application
    @PostMapping("/send_course_application")
    public Result<?> sendCourseApplication(@RequestBody CourseApplication courseApplication, HttpServletRequest request){
        /*Check Teacher*/
        ErrorCode err = courseChecker.checkAuthTeacher(request, userMapper);
        if (err != null) return Result.error(err);
        /*Check Form*/
        err = courseChecker.checkCourseApplication(courseApplication, courseMapper);
        if (err != null) return Result.error(err);
        /*Check Pass*/
        courseApplication.initApplication(constantsMapper);
        courseApplicationMapper.insert(courseApplication);
        /*update constants by 1*/
        courseUtils.incCourseApplicationId(constantsMapper);
        return Result.success();
    }

    //get course application teacher
    @PostMapping("/get_course_application_teacher")
    public Result<?> getCourseApplicationTeacher(@RequestBody User user, HttpServletRequest request){
        /*Check Teacher*/
        ErrorCode err = courseChecker.checkAuthTeacher(request, userMapper);
        if (err != null) return Result.error(err);
        /*Check Pass*/
        List<CourseApplication> courseApplicationList = courseUtils.getCourseApplicationListTeacher(courseApplicationMapper, user);
        return Result.success(courseApplicationList);
    }

    /*Student*/
    //get course list student
    @PostMapping("/get_course_student")
    public Result<?> getCourseListStudent(@RequestBody User user, HttpServletRequest request){
        /*Check Student*/
        ErrorCode err = courseChecker.checkAuthStudent(request, userMapper);
        if (err != null) return Result.error(err);
        /*Whether in Selection state*/
        err = courseChecker.checkCourseSelectionState(constantsMapper);
        if (err != null) return Result.error(err);
        /*Check Pass*/
        List<Course> courseList = courseUtils.getCourseListStudent(user, userMapper, courseMapper, majorMapper);
        List<CourseInfo> courseInfoList = courseUtils.getCourseInfoList(courseList, true, userMapper, majorMapper, collegeMapper);
        return Result.success(courseInfoList);
    }

    boolean update_course_capacity(String cid, Integer capacity){
        Course course = courseMapper.selectById(cid);
        String classroom = course.getCoursePlace();
        Integer maxCap = classroomMapper.getCapacity(classroom);
        if(maxCap < capacity)return false;
        courseMapper.updateCourseCapacity(cid, capacity);
        return true;
    }

    boolean increase_course_capacity(String cid, Integer change){
        assert(change > 0);
        return update_course_capacity(cid, courseMapper.getCourseCapacity(cid) + change);
    }


    @PostMapping("/update_course_capacity")
    private Result<?> update_course_capacity(@RequestBody Map<String, String> pair, HttpServletRequest request){
        ErrorCode err = courseChecker.checkAuthAdmin(request, userMapper);
        if (err != null) return Result.error(err);
        String cid = pair.get("cid"); Integer capacity = Integer.parseInt(pair.get("capacity"));
        if(courseMapper.selectById(cid) == null)return Result.error(ErrorCode.E_302);
        Course course = courseMapper.selectById(cid);
        //bigger than selected
        if(capacity < 0)return Result.error(ErrorCode.E_408);
        if(capacity < course.getCourseCapacity() && capacity < course.getSelected())return Result.error(ErrorCode.E_407);
        if(!update_course_capacity(cid, capacity))return Result.error(ErrorCode.E_212);
        return Result.success();
    }
}
