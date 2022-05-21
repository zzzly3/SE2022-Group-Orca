package com.orca.back.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orca.back.entity.Constants;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface ConstantsMapper extends BaseMapper<Constants> {
    @Select("select constant_value from constants where constant_name='course_selection_state'")
    String getCourseSelectionState();

    @Update("update constants set constant_value = #{open} where constant_name = 'course_selection_state'")
    void updateCourseSelectionState(boolean open);

}
