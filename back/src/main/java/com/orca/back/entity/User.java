package com.orca.back.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("user")
@Data
public class User {
    private Integer identifier;
    private Integer phone;
    private String password;
    private String email;
    private String name;
    private Integer role;
    @TableId("number")
    private Integer number;
    private Integer isFirst;
    private Integer isAdmin;
}
