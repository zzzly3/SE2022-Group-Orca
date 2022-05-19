package com.orca.back.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orca.back.entity.ClassTime;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ClassTimeMapper extends BaseMapper<ClassTime> {
    @Select("select * from classtime")
    List<ClassTime> findAll();

    @Select("select id from classtime where #{end}>begin && end>#{begin} && id != #{id}")
    List<Integer> findConflictTime(ClassTime classTime);
}