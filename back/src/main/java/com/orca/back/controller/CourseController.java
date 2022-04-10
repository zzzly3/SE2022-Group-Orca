package com.orca.back.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.orca.back.entity.Constants;
import com.orca.back.entity.Course;
import com.orca.back.entity.CourseApplication;
import com.orca.back.entity.User;
import com.orca.back.mapper.ConstantsMapper;
import com.orca.back.mapper.CourseApplicationMapper;
import com.orca.back.mapper.CourseMapper;
import com.orca.back.mapper.UserMapper;
import com.orca.back.utils.common.Checker;
import com.orca.back.utils.common.Result;
import com.orca.back.utils.constants.ErrorCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;

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

    Checker checker = new Checker();

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
        if (err != null){
            return Result.error(err);
        }
        Integer userId = (Integer) request.getSession().getAttribute("UserId");
        User user = userMapper.selectById(userId);
        if (user == null || user.getIsAdmin() == 0) return Result.error(ErrorCode.E_111);
        /*Check Pass*/
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
        courseMapper.deleteById(course.getCourseId());
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
    public Result<?> batchImport(@RequestParam(value = "file") final MultipartFile uploadfile, HttpServletRequest request) throws IOException {
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
        final byte[] bytes = uploadfile.getBytes();
        final Path path = Paths.get("~/Desktop/" + uploadfile.getOriginalFilename());
        Files.write(path, bytes);
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
        List<Course> courseList = courseMapper.selectList(Wrappers.<Course>lambdaQuery().eq(Course::getCourseTeacher, user.getName()));
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
        /*Check Pass*/
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
}
