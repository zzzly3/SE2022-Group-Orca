package com.orca.back.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Year;

@Data
@TableName("course_selection_state")
public class CourseSelectionState {
    @TableId("year")
    private Year year;

    private int semester;

    private boolean open;

    public Year getYear(){return year;}
    public void setYear(Year year){this.year=year;}

    public int getSemester(){return semester;}
    public void setSemester(int semester){this.semester=semester;}

    public boolean getOpen(){return open;}
    public void setOpen(boolean open){this.open=open;}
}
