package com.orca.back.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Year;


@Data
@TableName("course")
public class Course {
    private String courseId;
    private String courseName;
    private Year year;
    private Integer semester;
    private String allowedMajor;
    private Integer courseCapacity;
    private Integer selected;
    private String courseTime;
    private String courseTimeDay;
    private String courseTimeStart;
    private String courseTimeEnd;
    private String coursePlace;
    private String courseTeacher;
    private Integer courseCredit;
    private Integer courseCreditHour;
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

    public void teacherStr2Id(){
        this.courseTeacher = this.courseTeacher.split(":")[1].split("\\)")[0].strip();
    }

    public String teacherId2Str(String name, Integer number){
        this.courseTeacher = name + " (工号: " + number + ")";
        return this.courseTeacher;
    }
}


