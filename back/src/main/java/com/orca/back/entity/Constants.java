package com.orca.back.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("constants")
public class Constants {
    @TableId("constant_name")
    private String constantName;
    private String constantValue;


}
