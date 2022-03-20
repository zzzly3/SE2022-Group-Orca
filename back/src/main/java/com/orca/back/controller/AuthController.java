package com.orca.back.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.orca.back.entity.Info;
import com.orca.back.utils.common.InputCheck;
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
public class AuthController {

    @Resource
    UserMapper userMapper;

    InputCheck check = new InputCheck();

    @PostMapping("/register")
    public Result<?> onSubmit(@RequestBody User user) {
        /*非法输入*/
        ErrorCode err = check.checkRegistry(user);
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
    public Result<?> resetpw(@RequestBody User user){
        /*密码格式错误*/
        String pw = user.getPassword();
        ErrorCode err = check.checkPassword(pw);
        if (err != null)
            return Result.error(err);
        /*OK，重设密码*/
        User res = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getNumber, user.getNumber()));
        res.setPassword(user.getPassword());
        res.setIsFirst(0);
        userMapper.updateById(res);
        return Result.success();
    }


    @RequestMapping("/getinfo")
    public Info getInfo(HttpServletRequest request){
        Info res = new Info();
        Integer u_id = (Integer) request.getSession().getAttribute("UserId");
        if (u_id == null){
            res.setLogin(false);
            res.setResult(Result.error(ErrorCode.E_109));
            return res;
        }
        User user = userMapper.selectById(u_id);
        user.setPassword(null);
        res.setUser(user);
        res.setLogin(true);
        res.setResult(Result.success());
        return res;
    }

    @RequestMapping("/logout")
    public Result<?> logout (HttpServletRequest request){
        request.getSession().removeAttribute("UserId");
        return Result.success();
    }
}
