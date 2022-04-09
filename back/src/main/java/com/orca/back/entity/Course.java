package com.orca.back.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("course")
public class Course {
    @TableId("course_id")
    private String courseId;
    private String courseName;
    private String courseTime;
    private String courseTimeDay;
    private String courseTimeStart;
    private String courseTimeEnd;
    private String coursePlace;
    private String courseTeacher;
    private String courseDepartment;
    private Integer courseCredit;
    private Integer courseCreditHour;
    private Integer courseCapacity;
    private String courseDescription;
}


