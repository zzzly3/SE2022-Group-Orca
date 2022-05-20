package com.orca.back.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("classrooms")
public class Classroom {
    @TableId("name")
    private String name;
    private String building;
    private int capacity;
    private boolean open;

//    public String getName(){return name;}
//    public void setName(String name){this.name=name;}
//
//    public String getBuilding(){return building;}
//    public void setBuilding(String building){this.building=building;}
//
//    public int getCapacity(){return capacity;}
//    public void setCapacity(int capacity){this.capacity=capacity;}
//
//    public boolean getOpen(){return open;}
//    public void setOpen(boolean open){this.open=open;}
}
