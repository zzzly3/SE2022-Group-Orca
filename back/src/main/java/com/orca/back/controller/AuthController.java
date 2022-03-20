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
@RequestMapping
@CrossOrigin
public class AuthController {

    @Resource
    UserMapper userMapper;

    Checker check = new Checker();

    @PostMapping("/register")
    public Result<?> onSubmit(@RequestBody User user, HttpServletRequest request) {
        ErrorCode err;
        /*检查管理员权限*/
        err = check.checkAdmin(request);
        if (err != null)
            return Result.error(err);
        /*非法输入*/
        err = check.checkRegistry(user);
        if (err != null)
            return Result.error(err);
        /*重复用户(仅筛选学号/工号)*/
        User res = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getNumber, user.getNumber()));
        if (res != null){
            return Result.error(ErrorCode.E_101);
        }
        /*OK*/
        user.setPassword("123456");
        userMapper.insert(user);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody User user, HttpServletRequest request, HttpServletResponse response){
        /*检查用户名和密码*/
        User res = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getNumber, user.getNumber()).eq(User::getPassword, user.getPassword()));
        if (res == null){
            return Result.error(ErrorCode.E_102);
        }
        /*session*/
        request.getSession().setAttribute("UserId", res.getNumber());
        /*初次登录重设密码*/
        if (res.getIsFirst() == 1){
            return Result.error(ErrorCode.E_100);
        }
        return Result.success();
    }


    @PostMapping("/resetpw")
    public Result<?> resetpw(@RequestBody ResetInfo info, HttpServletRequest request){
        ErrorCode err = null;
        /*用户是否登入*/
        err = check.checkLogin(request);
        if (err != null)
            return Result.error(err);
        /*原密码是否匹配*/
        Integer u_id = (Integer) request.getSession().getAttribute("UserId");
        User user = userMapper.selectById(u_id);
        if (!info.getOriginPw().equals(user.getPassword()))
            return Result.error(ErrorCode.E_102);
        /*密码格式错误*/
        String newPw = info.getNewPw();
        err = check.checkPassword(newPw);
        if (err != null)
            return Result.error(err);
        /*OK*/
        user.setPassword(user.getPassword());
        user.setIsFirst(0);
        userMapper.updateById(user);
        return Result.success();
    }


    @RequestMapping("/getinfo")
    public SessionInfo getInfo(HttpServletRequest request){
        SessionInfo res = new SessionInfo();
        ErrorCode err = null;
        /*用户是否登录*/
        err = check.checkLogin(request);
        if (err != null){
            res.setLogin(false);
            res.setResult(Result.error(err));
            return res;
        }
        /*checked*/
        Integer u_id = (Integer) request.getSession().getAttribute("UserId");
        User user = userMapper.selectById(u_id);
        user.setPassword(null);
        res.setUser(user);
        res.setLogin(true);
        res.setResult(Result.success());
        return res;
    }

    @RequestMapping("/logout")
    public Result<?> logout (HttpServletRequest request){
        /*检查用户登入*/
        ErrorCode err = null;
        err = check.checkLogin(request);
        if (err != null)
            return Result.error(err);
        /*logout*/
        request.getSession().removeAttribute("UserId");
        return Result.success();
    }
}
