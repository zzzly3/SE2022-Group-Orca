package com.orca.back.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("course_application")
public class CourseApplication {
    @TableId(value = "application_id")
    private String applicationId;

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
    private String applicationType;

    private String applicationStatus;
    private String applicationTime;
    private String applicantName;
    private String applicantNumber;

    public void translateApplicationType() {
        this.applicationType = switch (this.applicationType) {
            case "1" -> "新增";
            case "2" -> "删除";
            case "3" -> "修改";
            default -> "未知";
        };
    }

    public void transBackApplicationType() {
        this.applicationType = switch (this.applicationType) {
            case "新增" -> "1";
            case "删除" -> "2";
            case "修改" -> "3";
            default -> "0";
        };
    }

    public void translateApplicationStatus() {
        this.applicationStatus = switch (this.applicationStatus) {
            case "0" -> "未审核";
            case "1" -> "审核通过";
            case "2" -> "审核未通过";
            default -> "未知";
        };
    }

}
