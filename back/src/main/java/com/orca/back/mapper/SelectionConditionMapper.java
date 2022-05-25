package com.orca.back.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orca.back.entity.SelectionCondition;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface SelectionConditionMapper extends BaseMapper<SelectionCondition> {
    @Select("select * from selection_conditions where student_id=#{sid} and course_id=#{cid}")
    SelectionCondition selectSelection(@Param("sid") Number sid, @Param("cid") String cid);

    @Insert("insert into selection_conditions (student_id, course_id, state) values (#{sid}, #{cid}, 0)")
    void addSelection(@Param("sid")Number sid,@Param("cid") String cid);

    @Delete("delete from selection_conditions where (student_id=#{sid} and course_id=#{cid})")
    void deleteSelection(@Param("sid")Number sid, @Param("cid")String cid);

    @Update("update selection_conditions set state=#{state} where (student_id=#{sid} and course_id=#{cid})")
    void updateSelection(@Param("sid")Number sid, @Param("cid")String cid, @Param("state")Number state);

    @Update("update selection_conditions set state=#{state}, description=#{des} where (student_id=#{sid} and course_id=#{cid})")
    void updateAppliedSelection(@Param("sid")Number sid, @Param("cid")String cid,@Param("state") Number state ,@Param("des")String des);

    @Insert("insert into selection_conditions (student_id, course_id, state, description) values (#{sid}, #{cid}, 2, #{des})")
    void addAppliedSelection(@Param("sid")Number sid,@Param("cid") String cid,@Param("des") String des);

    @Select("select student_id from selection_conditions where course_id=#{cid} and state = 0")
    List<Integer> loadCourseSelection(String cid);

    @Select("select * from selection_conditions where student_id=#{sid} and state = 0")
    List<SelectionCondition> loadSelectedCourses(Number sid);

    @Select("select * from selection_conditions where student_id=#{sid} and state >= 2")
    List<SelectionCondition> loadAppliedCourses(Number sid);

    @Select("select * from selection_conditions where student_id=#{sid} and state = 1")
    List<SelectionCondition> loadTakenCourses(Number sid);

    @Select("select * from selection_conditions where state = 2")
    List<SelectionCondition> loadAppliedSelection();
}
