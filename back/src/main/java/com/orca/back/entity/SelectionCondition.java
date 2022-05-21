package com.orca.back.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("selection_conditions")
public class SelectionCondition {
    private Integer studentId;
    private String courseId;
    private Integer state;
    private String description;
}
