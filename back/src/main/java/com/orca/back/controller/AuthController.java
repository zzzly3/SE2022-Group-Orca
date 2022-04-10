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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @PostMapping("/modify_course_selection_state")
    public Result<?> modify_course_selection_state(@RequestBody CourseSelectionState courseSelectionState, HttpServletRequest request){
        System.out.print("in backend: modify_course_selection_state\n");
        System.out.print(courseSelectionState);
        ErrorCode err = null;
        /*检查管理员权限*/
        Integer u_id = (Integer) request.getSession().getAttribute("UserId");
        if (u_id == null) err = ErrorCode.E_109;
        else{
            User admin = userMapper.selectById(u_id);
            if (admin.getIsAdmin() == 0) err = ErrorCode.E_111;
        }
        if (err != null) return Result.error(err);
        courseSelectionStateMapper.updateById(courseSelectionState);
        return Result.success();
    }

    @PostMapping("/modify_classroom")
    public Result<?> modify_classTime(@RequestBody Classroom classroom, HttpServletRequest request){
        System.out.print("in backend: modify_classroom\n");
        System.out.print(classroom);
        ErrorCode err = null;
        /*检查管理员权限*/
        Integer u_id = (Integer) request.getSession().getAttribute("UserId");
        if (u_id == null) err = ErrorCode.E_109;
        else{
            User admin = userMapper.selectById(u_id);
            if (admin.getIsAdmin() == 0) err = ErrorCode.E_111;
        }
        if (err != null) return Result.error(err);
        /*非法输入*/
        err = check.checkClassroom(classroom);
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
        System.out.print(classTime);
        ErrorCode err = null;
        /*检查管理员权限*/
        Integer u_id = (Integer) request.getSession().getAttribute("UserId");
        if (u_id == null) err = ErrorCode.E_109;
        else{
            User admin = userMapper.selectById(u_id);
            if (admin.getIsAdmin() == 0) err = ErrorCode.E_111;
        }
        if (err != null) return Result.error(err);
        /*非法输入*/
        err = check.checkClassTime(classTime);
        if(err != null)return Result.error(err);
            /*重复的课程节次*/
        ClassTime res = classTimeMapper.selectOne(
                Wrappers.<ClassTime>lambdaQuery().eq(ClassTime::getId, classTime.getId()));
        System.out.print(res);
        if(res != null){
            classTimeMapper.updateById(classTime);
        }else classTimeMapper.insert(classTime);
        return Result.success();
    }
}
