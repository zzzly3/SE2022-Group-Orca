package com.orca.back.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Time;

@Data
@TableName("classtime")
public class ClassTime {
    @TableId("id")
    private Number id;
    private String begin;
    private String end;

    public Number getId(){return id;}
    public void setId(Number id){this.id=id;}

    public String getBegin(){return begin;}
    public void setBegin(String begin){this.begin=begin;}

    public String getEnd(){return end;}
    public void setEnd(String end){this.end=end;}
}
