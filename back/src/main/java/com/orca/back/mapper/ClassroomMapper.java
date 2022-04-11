package com.orca.back.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orca.back.entity.Classroom;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ClassroomMapper extends BaseMapper<Classroom> {
    @Select("select * from classrooms where open=true")
    List<Classroom> findOpen();

    @Select("select * from classrooms")
    List<Classroom> findAll();
}