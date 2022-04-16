package com.orca.back.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.Entity;

@Data
@TableName("course")
public class Course {
    private String courseId;
    private String courseName;
    private String courseTime;
    private String courseTimeDay;
    private String courseTimeStart;
    private String courseTimeEnd;
    private String coursePlace;
    private String courseTeacher;
    private Integer courseCredit;
    private Integer courseCreditHour;
    private Integer courseCapacity;
    private String courseDescription;

    public void updateCourse(Course course){
        this.courseName = course.getCourseName();
        this.courseTime = course.getCourseTime();
        this.courseTimeDay = course.getCourseTimeDay();
        this.courseTimeStart = course.getCourseTimeStart();
        this.courseTimeEnd = course.getCourseTimeEnd();
        this.coursePlace = course.getCoursePlace();
        this.courseCredit = course.getCourseCredit();
        this.courseCreditHour = course.getCourseCreditHour();
        this.courseCapacity = course.getCourseCapacity();
        this.courseDescription = course.getCourseDescription();
    }
}


