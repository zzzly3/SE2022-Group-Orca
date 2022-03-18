package com.orca.back.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("student")
@Data
public class Student {
    private String identifier;
    private String phone;
    private String password;
    private String email;
    private String name;
}
