package com.orca.back.controller;

import com.orca.back.common.Result;
import com.orca.back.entity.Student;
import com.orca.back.mapper.StudentMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/register")
public class LoginController {

    @Resource
    StudentMapper studentMapper;

    @PostMapping
    public Result<?> onSubmit(@RequestBody Student student){
        studentMapper.insert(student);
        return Result.success();
    }
}
