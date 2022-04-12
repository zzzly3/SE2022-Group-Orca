package com.orca.back.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.orca.back.entity.*;
import com.orca.back.mapper.ClassTimeMapper;
import com.orca.back.mapper.ClassroomMapper;
import com.orca.back.mapper.CourseSelectionStateMapper;
import com.orca.back.mapper.UserMapper;

import com.orca.back.utils.common.Checker;
import com.orca.back.utils.common.Result;

import com.orca.back.utils.constants.ErrorCode;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static com.orca.back.controller.UserController.getResult;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Resource
    UserMapper userMapper;
    @Resource
    ClassTimeMapper classTimeMapper;
    @Resource
    ClassroomMapper classroomMapper;
    @Resource
    CourseSelectionStateMapper courseSelectionStateMapper;

    Checker check = new Checker();

    private Result<?> checkAdmin(HttpServletRequest request) {
        return getResult(request, userMapper);
    }

    @PostMapping("/load_open_classroom")
    public Result<?> load_open_classroom(HttpServletRequest request){
        ErrorCode err;
        err = check.checkLogin(request);
        if(err != null)return Result.error(err);
        List<Classroom> res = classroomMapper.findOpen();
        return Result.success(res);
    }

    @PostMapping("/load_all_classroom")
    public Result<?> load_all_classroom(HttpServletRequest request){
        ErrorCode err;
        err = check.checkLogin(request);
        if(err != null)return Result.error(err);
        List<Classroom> res = classroomMapper.findAll();
        return Result.success(res);
    }

    @PostMapping("/load_classTime")
    public Result<?> load_classTime(@RequestBody ClassTime classTime, HttpServletRequest request){
        ErrorCode err;
        err = check.checkLogin(request);
        if(err != null)return Result.error(err);
        List<ClassTime> res = classTimeMapper.findAll();
        return Result.success(res);
    }

    @PostMapping("/load_course_selection_state")
    public Result<?> load_course_selection_state(HttpServletRequest request){
        System.out.print("in backend: load_course_selection_state\n");
        Result<?> err1 = checkAdmin(request);
        if(err1 != null)return err1;
        CourseSelectionState res = courseSelectionStateMapper.getState();
        return Result.success(res);
    }


    @PostMapping("/select_classTime")
    public Result<?> select_classTime(@RequestBody Map<String, Integer> pair, HttpServletRequest request){
        System.out.print("in backend: select_classTime\n");
        Result<?> err1 = checkAdmin(request);
        if(err1 != null)return err1;
        ClassTime res = classTimeMapper.selectById(pair.get("id"));
        if(res == null)return Result.error(ErrorCode.E_206);
        return Result.success(res);
    }

    @PostMapping("/modify_course_selection_state")
    public Result<?> modify_course_selection_state(@RequestBody CourseSelectionState courseSelectionState, HttpServletRequest request){
        System.out.print("in backend: modify_course_selection_state\n");
        System.out.print(courseSelectionState);
        Result<?> err1 = checkAdmin(request);
        if(err1 != null)return err1;
        courseSelectionStateMapper.updateById(courseSelectionState);
        return Result.success();
    }

    @PostMapping("/modify_classroom")
    public Result<?> modify_classroom(@RequestBody Classroom classroom, HttpServletRequest request){
        System.out.print("in backend: modify_classroom\n");
        System.out.print(classroom);
        Result<?> err1 = checkAdmin(request);
        if(err1 != null)return err1;
        /*非法输入*/
        ErrorCode err = check.checkClassroom(classroom);
        if(err != null)return Result.error(err);
        /*重复的教室*/
        Classroom res = classroomMapper.selectOne(
                Wrappers.<Classroom>lambdaQuery().eq(Classroom::getName, classroom.getName()));
        if(res != null)classroomMapper.updateById(classroom);
        else classroomMapper.insert(classroom);
        return Result.success();
    }

    @PostMapping("/modify_classTime")
    public Result<?> modify_classTime(@RequestBody ClassTime classTime, HttpServletRequest request){
        System.out.print("in backend: modify_classTime\n");
        Result<?> err1 = checkAdmin(request);
        if(err1 != null)return err1;
        /*非法输入*/
        ErrorCode err = check.checkClassTime(classTime);
        if(err != null)return Result.error(err);
            /*重复的课程节次*/
        ClassTime res = classTimeMapper.selectOne(
                Wrappers.<ClassTime>lambdaQuery().eq(ClassTime::getId, classTime.getId()));
        if(res != null){
            classTimeMapper.updateById(classTime);
        }else classTimeMapper.insert(classTime);
        return Result.success();
    }
}
