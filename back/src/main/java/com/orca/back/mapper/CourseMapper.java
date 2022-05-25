package com.orca.back.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orca.back.entity.Course;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CourseMapper extends BaseMapper<Course> {

    @Select("select * from course where year=2022 and semester=0")
    List<Course> getCurrentCourses();

    @Select("select * from course where course_teacher=#{tid} and year=2022 and semester=0")
    List<Course> loadCoursesToTeach(Number tid);

    @Select("select * from course where (allowed_major =0 or allowed_major like CONCAT('%',#{major},'%')) and (year=2022 and semester=0)")
    List<Course> loadCoursesAllowedMajor(Number major);

    @Update("update course set course_capacity=#{capacity} where course_id=#{cid}")
    void updateCourseCapacity(@Param("cid") String cid, @Param("capacity") Number capacity);

    @Select("select course_capacity from course where course_id=#{cid}")
    Integer getCourseCapacity(String cid);

    @Select("select * from course where course_id=#{cid} and (allowed_major = 0 or allowed_major like CONCAT('%',#{major},'%'))")
    Course getCourseWithMajor(@Param("cid") String cid, @Param("major") Number major);

    @Update("update course set selected=selected+1 where course_id=#{cid}")
    void increaseSelected(String cid);

    @Update("update course set selected=selected-1 where course_id=#{cid}")
    void decreaseSelected(String cid);

    @Select("select * from course where selected > course_capacity")
    List<Course> getOverSelected();

    @Select("select * from course where " +
            "course_id like CONCAT('%',#{cid},'%') and" +
            "course_name like CONCAT('%',#{cname},'%') and" +
            "course_time like CONCAT('%',#{ctime},'%') and" +
            "course_place like CONCAT('%',#{classroom},'%') and" +
            "course_teacher like CONCAT('%',#{teacher},'%')")
    List<Course> search(@Param("cid")String cid, @Param("ctime")String ctime,
                        @Param("cname")String cname, @Param("classroom")String classroom, @Param("teacher")String teacher);
}
