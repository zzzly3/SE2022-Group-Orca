package com.orca.back.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orca.back.entity.CourseSelectionState;
import org.apache.ibatis.annotations.Select;

public interface CourseSelectionStateMapper extends BaseMapper<CourseSelectionState> {
    @Select("select * from course_selection_state where year=2022")
    CourseSelectionState getState();
}