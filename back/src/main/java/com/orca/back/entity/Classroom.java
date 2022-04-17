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
    private boolean open;
    private boolean old;

    public String getName(){return name;}
    public void setName(String name){this.name=name;}

    public String getBuilding(){return building;}
    public void setBuilding(String building){this.building=building;}

    public boolean getOpen(){return open;}
    public void setOpen(boolean open){this.open=open;}

    public boolean getOld(){return old;}
    public void setOld(boolean old){this.old=old;}
}
