package com.orca.back.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.orca.back.entity.ResetInfo;
import com.orca.back.entity.SessionInfo;
import com.orca.back.utils.common.Checker;
import com.orca.back.utils.common.Result;
import com.orca.back.entity.User;
import com.orca.back.mapper.UserMapper;
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

    Checker check = new Checker();

    @PostMapping("/register")
    public Result<?> onSubmit(@RequestBody User user, HttpServletRequest request) {
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
