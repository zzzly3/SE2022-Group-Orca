package com.orca.back.entity;

import lombok.Data;

import java.util.List;

@Data
public class CourseConstantsInfo {
    private List<String> courseTimeStartList;
    private List<String> courseTimeEndList;
    private List<String> classRoomList;
    private List<TeacherSelectInfo> teacherList;
}
