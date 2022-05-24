package com.orca.back.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.orca.back.entity.Constants;
import com.orca.back.entity.Course;
import com.orca.back.entity.SelectionCondition;
import com.orca.back.entity.User;
import com.orca.back.mapper.ConstantsMapper;
import com.orca.back.mapper.CourseMapper;
import com.orca.back.mapper.SelectionConditionMapper;
import com.orca.back.mapper.UserMapper;
import com.orca.back.utils.common.CourseUtils;
import com.orca.back.utils.common.Result;
import com.orca.back.utils.constants.ErrorCode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.orca.back.controller.UserController.getUser;

@RestController
@RequestMapping("/api/selection")
public class SelectionController {
    @Resource
    UserMapper userMapper;
    @Resource
    ConstantsMapper constantsMapper;
    @Resource
    SelectionConditionMapper selectionConditionMapper;
    @Resource
    CourseMapper courseMapper;

    CourseUtils courseUtils = new CourseUtils();

    private User getStudent(HttpServletRequest request) {
        User u = getUser(request, userMapper);
        if (u == null) return null;
        return u.getRole() == 2 ? u : null;
    }

    @PostMapping("/select")
    private Result<?> selectCourse(@RequestBody SelectionCondition condition, HttpServletRequest request) {
        User u = getUser(request, userMapper);
        if (u != null && u.getIsAdmin() == 1) {
            // update the selection
            Course course = courseMapper.selectById(condition.getCourseId());
            if (course == null) return Result.error(ErrorCode.E_206);
            SelectionCondition sc = selectionConditionMapper.selectOne(
                    Wrappers.<SelectionCondition>lambdaQuery().eq(SelectionCondition::getStudentId, condition.getStudentId())
                            .eq(SelectionCondition::getCourseId, condition.getCourseId()));
            if (sc == null) {
                selectionConditionMapper.insert(condition);
            } else {
                selectionConditionMapper.update(condition, Wrappers.<SelectionCondition>lambdaUpdate().eq(SelectionCondition::getStudentId, condition.getStudentId())
                        .eq(SelectionCondition::getCourseId, condition.getCourseId()));
            }
            course.setSelected(course.getSelected() + 1);
            if (course.getSelected() >= course.getCourseCapacity())
                course.setCourseCapacity(course.getCourseCapacity() + 1);
            courseUtils.updateCourseBatch(course, courseMapper);
            return Result.success();
        }
        User student = getStudent(request);
        if (student == null) return Result.error(ErrorCode.E_111);
        Course course = courseMapper.selectById(condition.getCourseId());
        if (course == null) return Result.error(ErrorCode.E_206);
        // check course allowed major
        List<String> allowedMajor = List.of(course.getAllowedMajor().split(","));
        if (student.getMajor() == null || !allowedMajor.contains(student.getMajor().toString()) && !allowedMajor.contains("0"))
            return Result.error(ErrorCode.E_424);
        // check if the selection already exists
        SelectionCondition sc = selectionConditionMapper.selectOne(
                Wrappers.<SelectionCondition>lambdaQuery()
                        .eq(SelectionCondition::getStudentId, student.getNumber())
                        .eq(SelectionCondition::getCourseId, condition.getCourseId()));
        if (sc != null) return Result.error(ErrorCode.E_420);
        // get the course_selection_state
        String state = constantsMapper.selectOne(
                Wrappers.<Constants>lambdaQuery()
                        .eq(Constants::getConstantName, "course_selection_state")).getConstantValue();
        switch (state) {
            case "0":
                return Result.error(ErrorCode.E_400);
            case "1":
                condition.setState(0);
                break;
            case "2":
                if (condition.getState() == 0) {
                    if (course.getSelected() >= course.getCourseCapacity()) return Result.error(ErrorCode.E_421);
                    // update the course
                    course.setSelected(course.getSelected() + 1);
                    courseUtils.updateCourseBatch(course, courseMapper);
                    condition.setState(2);
                } else
                    condition.setState(1);
                break;
        }
        // add the selection
        condition.setStudentId(student.getNumber());
        selectionConditionMapper.insert(condition);
        return Result.success();
    }

    @PostMapping("/list")
    private Result<?> listSelection(@RequestBody SelectionCondition condition, HttpServletRequest request) {
        User u = getUser(request, userMapper);
        if (u == null) return Result.error(ErrorCode.E_111);
        if (u.getRole() == 2) {
            List<SelectionCondition> list = selectionConditionMapper.selectList(
                    Wrappers.<SelectionCondition>lambdaQuery()
                            .eq(SelectionCondition::getStudentId, u.getNumber()));
            return Result.success(list);
        } else {
            if (u.getIsAdmin() == 0) {
                // check if the user is the teacher of the course
                Course course = courseMapper.selectOne(
                        Wrappers.<Course>lambdaQuery()
                                .eq(Course::getCourseId, condition.getCourseId())
                                .eq(Course::getCourseTeacher, u.getNumber()));
                if (course == null) return Result.error(ErrorCode.E_422);
            }
            List<SelectionCondition> list = selectionConditionMapper.selectList(
                    Wrappers.<SelectionCondition>lambdaQuery()
                            .eq(SelectionCondition::getCourseId, condition.getCourseId()));
            return Result.success(list);
        }
    }

    @PostMapping("/drop")
    private Result<?> dropSelection(@RequestBody SelectionCondition condition, HttpServletRequest request) {
        String state = constantsMapper.selectOne(
                Wrappers.<Constants>lambdaQuery()
                        .eq(Constants::getConstantName, "course_selection_state")).getConstantValue();
        if (state.equals("0"))
            return Result.error(ErrorCode.E_400);
        User u = getUser(request, userMapper);
        if (u == null) return Result.error(ErrorCode.E_111);
        if (u.getIsAdmin() == 1) {
            // update the selection
            SelectionCondition sc = selectionConditionMapper.selectOne(
                    Wrappers.<SelectionCondition>lambdaQuery().eq(SelectionCondition::getStudentId, condition.getStudentId())
                            .eq(SelectionCondition::getCourseId, condition.getCourseId()));
            if (sc == null) return Result.error(ErrorCode.E_423);
            if (sc.getState() == 1) {
                sc.setState(3);
                selectionConditionMapper.update(sc, Wrappers.<SelectionCondition>lambdaUpdate().eq(SelectionCondition::getStudentId, sc.getStudentId())
                        .eq(SelectionCondition::getCourseId, sc.getCourseId()));
                return Result.success();
            } else {
                return Result.error(ErrorCode.E_423);
            }
        } else {
            Course course = courseMapper.selectOne(
                    Wrappers.<Course>lambdaQuery()
                            .eq(Course::getCourseId, condition.getCourseId()));
            selectionConditionMapper.delete(
                    Wrappers.<SelectionCondition>lambdaQuery()
                            .eq(SelectionCondition::getStudentId, u.getNumber())
                            .eq(SelectionCondition::getCourseId, condition.getCourseId()));
            course.setSelected(course.getSelected() - 1);
            courseUtils.updateCourseBatch(course, courseMapper);
            return Result.success();
        }
    }
}
