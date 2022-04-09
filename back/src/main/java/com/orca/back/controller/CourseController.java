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
import com.orca.back.utils.common.Checker;
import com.orca.back.utils.common.Result;
import com.orca.back.utils.constants.ErrorCode;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
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
    CourseMapper courseMapper;
    @Resource
    CourseApplicationMapper courseApplicationMapper;
    @Resource
    ConstantsMapper constantsMapper;

    Checker checker = new Checker();

    /*Admin*/
    @PostMapping("/get_course_all")
    public Result<?> getCourseAll(HttpServletRequest request){
        ErrorCode err = checker.checkAdmin(request);
        if(err != null) return Result.error(err);
        Page<Course> page = courseMapper.selectPage(new Page<>(1, 10), null);

        List<Course> courseList = courseMapper.selectList(null);
        return Result.success(courseList);
    }

    //add course
    @PostMapping("/add_course")
    public Result<?> addCourse(@RequestBody Course course, HttpServletRequest request){
        ErrorCode err = checker.checkAdmin(request);
        if(err != null) return Result.error(err);
        courseMapper.insert(course);
        return Result.success();
    }

    //edit course
    @PostMapping("/edit_course")
    public Result<?> editCourse(@RequestBody Course course, HttpServletRequest request){
        ErrorCode err = checker.checkAdmin(request);
        if(err != null) return Result.error(err);
        courseMapper.updateById(course);
        return Result.success();
    }

    //delete course by id
    @PostMapping("/delete_course")
    public Result<?> deleteCourse(@RequestBody Course course, HttpServletRequest request){
        ErrorCode err = checker.checkAdmin(request);
        if(err != null) return Result.error(err);
        courseMapper.deleteById(course.getCourseId());
        return Result.success();
    }

    //get course application all
    @PostMapping("/get_course_application_all")
    public Result<?> getCourseApplicationAll(HttpServletRequest request){
        ErrorCode err = checker.checkAdmin(request);
        if(err != null) return Result.error(err);
        //if teacher, return course application
        //select course application where application_status = 0
        List<CourseApplication> courseApplicationList = courseApplicationMapper.selectList(Wrappers.<CourseApplication>lambdaQuery().eq(CourseApplication::getApplicationStatus, 0));
        for (CourseApplication courseApplication : courseApplicationList) {
            courseApplication.translateApplicationType();
            courseApplication.translateApplicationStatus();
        }
        return Result.success(courseApplicationList);
    }

    //batch import
    @PostMapping("/batch_import")
    public Result<?> batchImport(@RequestPart(value = "file") final MultipartFile file, HttpServletRequest request) throws IOException {
        final byte[] bytes = file.getBytes();
        final Path path = Paths.get("~/Desktop/" + file.getOriginalFilename());
        Files.write(path, bytes);
        return Result.success();
    }

    /*Teacher*/
    //get course teacher
    @PostMapping("/get_course_teacher")
    public Result<?> getCourseTeacher(@RequestBody User user, HttpServletRequest request){
        ErrorCode err = checker.checkTeacher(request);
        if(err != null) return Result.error(err);

        List<Course> courseList = courseMapper.selectList(Wrappers.<Course>lambdaQuery().eq(Course::getCourseTeacher, user.getName()));
        return Result.success(courseList);
    }

    //send course application
    @PostMapping("/send_course_application")
    public Result<?> sendCourseApplication(@RequestBody CourseApplication courseApplication, HttpServletRequest request){
        ErrorCode err = checker.checkTeacher(request);
        if(err != null) return Result.error(err);

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
        ErrorCode err = checker.checkTeacher(request);
        if(err != null) return Result.error(err);
        //if teacher, return course application
        //select course application where application_teacher = user.name
        List<CourseApplication> courseApplicationList = courseApplicationMapper.selectList(Wrappers.<CourseApplication>lambdaQuery().eq(CourseApplication::getApplicantName, user.getName()));
        for (CourseApplication courseApplication : courseApplicationList) {
            courseApplication.translateApplicationType();
            courseApplication.translateApplicationStatus();
        }
        return Result.success(courseApplicationList);
    }

    //update course application status
    @PostMapping("/update_course_application_status")
    public Result<?> updateCourseApplicationStatus(@RequestBody CourseApplication courseApplication, HttpServletRequest request){
        ErrorCode err = checker.checkTeacher(request);
        if(err != null) return Result.error(err);
        //if teacher, update course application status
        System.out.println(courseApplication);
        courseApplication.transBackApplicationType();
        courseApplicationMapper.updateById(courseApplication);
        return Result.success();
    }
}
