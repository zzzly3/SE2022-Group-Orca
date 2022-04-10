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

    @PostMapping("/register")
    public Result<?> onSubmit(@RequestBody User user, HttpServletRequest request) {
        ErrorCode err = null;
        System.out.print("in backend: onSubmit");
        System.out.print(user);
        /*检查管理员权限*/
        Integer u_id = (Integer) request.getSession().getAttribute("UserId");
        if (u_id == null) err = ErrorCode.E_109;
        else{
            User admin = userMapper.selectById(u_id);
            if (admin.getIsAdmin() == 0) err = ErrorCode.E_111;
        }
        if (err != null) return Result.error(err);
        /*非法输入*/
        err = check.checkRegistry(user);
        if (err == null) {
            /*重复用户(仅筛选学号/工号)*/
            User res = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getNumber, user.getNumber()));
            if (res != null) err = ErrorCode.E_101;
        }
        if (err != null) return Result.error(err);
        /*OK*/
        user.setPassword("123456");
        userMapper.insert(user);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody User user, HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Set-Cookie", "SameSite=None");
        /*检查用户名和密码*/
        User res = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getNumber, user.getNumber()).eq(User::getPassword, user.getPassword()));
        if (res == null){
            return Result.error(ErrorCode.E_102);
        }
        /*session*/
        request.getSession().setAttribute("UserId", res.getNumber());
        return Result.success();
    }

    @PostMapping("/resetpw")
    public Result<?> resetpw(@RequestBody ResetInfo info, HttpServletRequest request){
        ErrorCode err;
        /*用户是否登入*/
        err = check.checkLogin(request);
        if (err != null)
            return Result.error(err);
        /*原密码是否匹配, 新密码格式是否错误*/
        Integer u_id = (Integer) request.getSession().getAttribute("UserId");
        User user = userMapper.selectById(u_id);
        if (!info.getOriginPw().equals(user.getPassword()))
            err = ErrorCode.E_102;
        else {
            err = check.checkPassword(info.getNewPw());
        }
        if (err != null)
            return Result.error(err);
        /*OK*/
        user.setPassword(info.getNewPw());
        user.setIsFirst(0);
        userMapper.updateById(user);
        return Result.success();
    }

    @RequestMapping("/getinfo")
    public Result<SessionInfo> getInfo(HttpServletRequest request){

        SessionInfo res = new SessionInfo();
        ErrorCode err;
        /*用户是否登录*/
        err = check.checkLogin(request);
        if (err != null){
            res.setLogin(false);
            return Result.success(res);
        }
        /*checked*/
        Integer u_id = (Integer) request.getSession().getAttribute("UserId");
        User user = userMapper.selectById(u_id);
        user.setPassword(null);
        res.setUser(user);
        res.setLogin(true);
        return Result.success(res);
    }

    @CrossOrigin(origins = "http://localhost:9876", allowCredentials = "true")
    @RequestMapping("/logout")
    public Result<?> logout (HttpServletRequest request){
        /*检查用户登入*/
        ErrorCode err;
        err = check.checkLogin(request);
        if (err != null)
            return Result.error(err);
        /*logout*/
        request.getSession(false).removeAttribute("UserId");
        return Result.success();
    }
}
