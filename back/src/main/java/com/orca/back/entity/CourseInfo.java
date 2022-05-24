package com.orca.back.entity;

import lombok.Data;

@Data
public class CourseInfo {
    private String courseId;
    private String courseName;
    private String courseTime;
    private String courseTimeDay;
    private String courseTimeStart;
    private String courseTimeEnd;
    private String coursePlace;
    private String courseTeacher;
    private String courseMajor;
    private String courseDepartment;
    private Integer courseCredit;
    private Integer courseCreditHour;
    private Integer courseCapacity;
    private String courseDescription;
    private String allowMajor;
    private Boolean full;

    public void setCourse(Course course) {
        this.courseId = course.getCourseId();
        this.courseName = course.getCourseName();
        this.courseTime = course.getCourseTime();
        this.courseTimeDay = course.getCourseTimeDay();
        this.courseTimeStart = course.getCourseTimeStart();
        this.courseTimeEnd = course.getCourseTimeEnd();
        this.coursePlace = course.getCoursePlace();
        this.courseTeacher = course.getCourseTeacher();
        this.courseCredit = course.getCourseCredit();
        this.courseCreditHour = course.getCourseCreditHour();
        this.courseCapacity = course.getCourseCapacity();
        this.courseDescription = course.getCourseDescription();
        this.allowMajor = course.getAllowedMajor();
        this.full = course.getSelected() >= course.getCourseCapacity();
    }
}
