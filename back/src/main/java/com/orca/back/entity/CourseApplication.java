package com.orca.back.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("course_application")
public class CourseApplication {
    @TableId(value = "application_id", type = IdType.AUTO)
    private String applicationId;

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
    private String applicationType;

    private String applicationStatus;
    private String applicationTime;
    private String applicantName;
    private String applicantNumber;

    public void translateApplicationType() {
        if (this.applicationType.equals("1")) {
            this.applicationType = "新增";
        } else if (this.applicationType.equals("2")) {
            this.applicationType = "删除";
        } else if (this.applicationType.equals("3")) {
            this.applicationType = "修改";
        }
    }

    public void transBackApplicationType() {
        if (this.applicationType.equals("新增")) {
            this.applicationType = "1";
        } else if (this.applicationType.equals("删除")) {
            this.applicationType = "2";
        } else if (this.applicationType.equals("修改")) {
            this.applicationType = "3";
        }
    }

    public void translateApplicationStatus() {
        if (this.applicationStatus.equals("0")) {
            this.applicationStatus = "未审核";
        } else if (this.applicationStatus.equals("1")) {
            this.applicationStatus = "审核通过";
        } else if (this.applicationStatus.equals("2")) {
            this.applicationStatus = "审核未通过";
        }
    }

}
