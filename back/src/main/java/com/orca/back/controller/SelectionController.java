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
import com.orca.back.utils.common.Result;
import com.orca.back.utils.constants.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.time.Year;
import java.util.*;

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
    @Autowired
    CourseController courseController = new CourseController();

    private User getStudent(HttpServletRequest request) {
        User u = getUser(request, userMapper);
        if (u == null) return null;
        return u.getRole() == 2 ? u : null;
    }
    private User getTeacher(HttpServletRequest request) {
        User u = getUser(request, userMapper);
        if (u == null) return null;
        return u.getRole() == 1 ? u : null;
    }
    private User getAdmin(HttpServletRequest request) {
        User u = getUser(request, userMapper);
        if (u == null) return null;
        return u.getIsAdmin() == 1 ? u : null;
    }

    private boolean currentCourse(String cid){
        Course course = courseMapper.selectById(cid);
        //checkYearAndSemester
        System.out.println("in currentCourse: cid is "); System.out.println(cid);
        Year y1 = course.getYear(); Year y2 = constantsMapper.getYear();
        Integer s1 = course.getSemester();  Integer s2 = constantsMapper.getSemester();
        return Objects.equals(y1, y2) && Objects.equals(s1, s2);
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
            courseMapper.updateById(course);
            return Result.success();
        }
        User student = getStudent(request);
        if (student == null) return Result.error(ErrorCode.E_111);
        Course course = courseMapper.selectById(condition.getCourseId());
        if (course == null) return Result.error(ErrorCode.E_206);
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
                    courseMapper.updateById(course);
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
    private Result<List<SelectionCondition>> listSelection(@RequestBody SelectionCondition condition, HttpServletRequest request) {
        User u = getUser(request, userMapper);
        if (u == null) return Result.success(null);
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
                if (course == null) return Result.success(null);
            }
            List<SelectionCondition> list = selectionConditionMapper.selectList(
                    Wrappers.<SelectionCondition>lambdaQuery()
                            .eq(SelectionCondition::getCourseId, condition.getCourseId()));
            return Result.success(list);
        }
    }

    @GetMapping("/load_all_courses")
    private Result<?> load_all_courses(HttpServletRequest request){
        if(getUser(request, userMapper) == null)return Result.error(ErrorCode.E_111);
        List<Course> res = courseMapper.getCurrentCourses();
        return Result.success(res);
    }

    @PostMapping("/load_course_selection")
    private Result<?> load_course_selection(@RequestBody Map<String, String> pair, HttpServletRequest request){
        System.out.println("in load_course_selection");
        User user = getUser(request, userMapper);
        if(user == null || (user.getIsAdmin() != 1 && user.getRole() != 1))
            return Result.error(ErrorCode.E_111);
        List<Integer> sids = selectionConditionMapper.loadCourseSelection(pair.get("cid"));
        System.out.println(sids);
        return Result.success(sids);
    }

    @PostMapping("/add_selection")
    private Result<?> add_selection(@RequestBody Map<String, String> pair, HttpServletRequest request){
        //checkStudent
        User student = getStudent(request);
        if(student == null)return Result.error(ErrorCode.E_401);
        //checkSelectionState
        Integer state = Integer.parseInt(constantsMapper.getCourseSelectionState());
        if(state == 0)return Result.error(ErrorCode.E_400);
        Number sid = student.getNumber(); String cid = pair.get("cid");
        Course course = courseMapper.selectById(cid);
        //checkCapacity
        if(Objects.equals(course.getSelected(), course.getCourseCapacity()) && state == 2)return Result.error(ErrorCode.E_421);

        //checkAllowedMajor
        User user = userMapper.selectById(sid);
        Number major = user.getMajor();
        if(courseMapper.getCourseWithMajor(cid, major) == null)return Result.error(ErrorCode.E_404);

        //checkYearAndSemester
        if(!currentCourse(cid))return Result.error(ErrorCode.E_405);

        //checkDuplicate
        SelectionCondition sc = selectionConditionMapper.selectSelection(sid, cid);
        if(sc != null){
            if(sc.getState() < 2)return Result.error(ErrorCode.E_420);
            else selectionConditionMapper.updateSelection(sid, cid, 0);
        }else{
            selectionConditionMapper.addSelection(sid, cid);
        }
        courseMapper.increaseSelected(cid);
        return Result.success();
    }

    @GetMapping("/load_selected_courses")
    private Result<?> load_selected_courses(HttpServletRequest request){
        User student = getStudent(request);
        if(student == null)return Result.error(ErrorCode.E_401);
        List<SelectionCondition> res = selectionConditionMapper.loadSelectedCourses(student.getNumber());
        return Result.success(res);
    }

    @GetMapping("/load_applied_courses")
    private Result<?> load_applied_courses(HttpServletRequest request){
        User student = getStudent(request);
        if(student == null)return Result.error(ErrorCode.E_401);
        List<SelectionCondition> res = selectionConditionMapper.loadAppliedCourses(student.getNumber());
        return Result.success(res);
    }

    @GetMapping("/load_taken_courses")
    private Result<?> load_taken_courses(HttpServletRequest request){
        User student = getStudent(request);
        if(student == null)return Result.error(ErrorCode.E_401);
        List<SelectionCondition> res = selectionConditionMapper.loadTakenCourses(student.getNumber());
        return Result.success(res);
    }

    @GetMapping("/load_courses_to_teach")
    private Result<?> load_courses_to_teach(HttpServletRequest request){
        User teacher = getTeacher(request);
        if(teacher == null)return Result.error(ErrorCode.E_402);
        Number tid = teacher.getNumber();
        List<Course> res = courseMapper.loadCoursesToTeach(tid);
        return Result.success(res);
    }

    @PostMapping("/delete_selection")
    private Result<?> delete_selection(@RequestBody Map<String, String> pair, HttpServletRequest request){
        User student = getStudent(request);
        if(student == null)return Result.error(ErrorCode.E_401);
        Number sid = student.getNumber(); String cid = pair.get("cid");
        SelectionCondition res = selectionConditionMapper.selectSelection(sid, cid);
        if(!currentCourse(cid))return Result.error(ErrorCode.E_405);
        if(res == null || res.getState() != 0)
            return Result.error(ErrorCode.E_410);
        selectionConditionMapper.deleteSelection(sid, cid);
        courseMapper.decreaseSelected(cid);
        return Result.success();
    }

    @PostMapping("/apply_selection")
    private Result<?> apply_selection(@RequestBody Map<String, String> pair, HttpServletRequest request){
        User student = getStudent(request);
        if(student == null)return Result.error(ErrorCode.E_401);
        //checkSelectionState
        String state = constantsMapper.getCourseSelectionState();
        if(!Objects.equals(state, "2"))return Result.error(ErrorCode.E_406);
        Number sid = student.getNumber(); String cid = pair.get("cid"); String description = pair.get("description");
        //checkAllowedMajor
        User user = userMapper.selectById(sid);
        Number major = user.getMajor();
        if(courseMapper.getCourseWithMajor(cid, major) == null)return Result.error(ErrorCode.E_404);
        //checkYandS
        if(!currentCourse(cid))return Result.error(ErrorCode.E_405);
        SelectionCondition res = selectionConditionMapper.selectSelection(sid, cid);
        if(res != null){  //update if previous one is rejected
            if(res.getState() == 3)selectionConditionMapper.updateAppliedSelection(sid, cid, 2,description);
            else return Result.error(ErrorCode.E_411);
        }else{
            selectionConditionMapper.addAppliedSelection(sid, cid, description);
        }
        return Result.success();
    }

    @PostMapping("/delete_applied_selection")
    private Result<?> delete_applied_selection(@RequestBody Map<String, String> pair, HttpServletRequest request){
        User student = getStudent(request);
        if(student == null)return Result.error(ErrorCode.E_401);
        Number sid = student.getNumber(); String cid = pair.get("cid");
        SelectionCondition res = selectionConditionMapper.selectSelection(sid, cid);
        if(!currentCourse(cid))return Result.error(ErrorCode.E_405);
        if(res == null || res.getState() != 2)
            return Result.error(ErrorCode.E_410);
        selectionConditionMapper.deleteSelection(sid, cid);
        return Result.success();
    }

    //for admin to check all the applied selections
    @GetMapping("/load_applied_selection")
    private Result<?> load_applied_selection(HttpServletRequest request){
        if(getAdmin(request) == null)return Result.error(ErrorCode.E_111);
        List<SelectionCondition> res = selectionConditionMapper.loadAppliedSelection();
        return Result.success(res);
    }

    @PostMapping("/pass_applied_selection")
    private Result<?> pass_applied_selection(@RequestBody Map<String, String> pair, HttpServletRequest request){
        if(getAdmin(request) == null)return Result.error(ErrorCode.E_111);
        Number sid = Integer.parseInt(pair.get("sid"));  String cid = pair.get("cid");
        SelectionCondition res = selectionConditionMapper.selectSelection(sid, cid);
        if(res == null || res.getState() != 2)
            return Result.error(ErrorCode.E_403);
        if(!courseController.increase_course_capacity(cid, 1))return Result.error(ErrorCode.E_212);
        selectionConditionMapper.updateSelection(sid, cid, 0);
        courseMapper.increaseSelected(cid);
        return Result.success();
    }

    @PostMapping("/reject_applied_selection")
    private Result<?> reject_applied_selection(@RequestBody Map<String, String> pair, HttpServletRequest request){
        if(getAdmin(request) == null)return Result.error(ErrorCode.E_111);
        Number sid = Integer.parseInt(pair.get("sid")); String cid = pair.get("cid");
        SelectionCondition res = selectionConditionMapper.selectSelection(sid, cid);
        if(res == null || res.getState() != 2)
            return Result.error(ErrorCode.E_403);
        selectionConditionMapper.updateAppliedSelection(sid, cid, 3, pair.get("des"));
        return Result.success();
    }

    @GetMapping("/load_selectable_courses")
    private Result<?> load_selectable_courses(HttpServletRequest request){
        User user = getStudent(request);
        if(user == null)return Result.error(ErrorCode.E_401);
        Number major = user.getMajor();
        if(major == null)return Result.error(ErrorCode.E_999);
        List<Course> res = courseMapper.loadCoursesAllowedMajor(major);
        return Result.success(res);
    }

    private void removeOverSelected(Course course){
        Integer selected = course.getSelected(); Integer capacity = course.getCourseCapacity();
        String cid = course.getCourseId();
        System.out.println(cid);
        List<Integer> sids = selectionConditionMapper.loadCourseSelection(course.getCourseId());
        System.out.println(sids);
        Vector<Vector<Number>> grades = new Vector<Vector<Number>>(4);
        for(int i = 0; i <4; i++)grades.add(new Vector<Number>());
        for(Number sid: sids){
            grades.elementAt(userMapper.getGrade(sid)-1).add(sid);
        }
        System.out.println(grades.elementAt(0));
        System.out.println(grades.elementAt(1));
        System.out.println(grades.elementAt(2));
        System.out.println(grades.elementAt(3));
        int grade = 0;
        while(grade <= 3 && selected > capacity){
            for(Number sid:grades.elementAt(grade)){
                selectionConditionMapper.deleteSelection(sid, cid);
                if(--selected <= capacity)break;
            }
            grade++;
        }
        course.setSelected(selected);
        courseMapper.updateById(course);
    }

    public Result<?> first_round_end(HttpServletRequest request){
        System.out.println("in first_round_end");
        if(getAdmin(request) == null)return Result.error(ErrorCode.E_401);
        int state = Integer.parseInt(constantsMapper.getCourseSelectionState());
        if(state != 2)return Result.error(ErrorCode.E_999);
        List<Course> courses = courseMapper.getOverSelected();
        for (Course course: courses) {
            removeOverSelected(course);
        }
        return Result.success();
    }

    @PostMapping("/search")
    private Result<?> search(@RequestBody Map<String, String> pair, HttpServletRequest request){
        User user = getStudent(request);
        if(user == null)return Result.error(ErrorCode.E_401);
        String cid = pair.get("cid"); String ctime = pair.get("ctime"); String cname = pair.get("cname");
        String classroom = pair.get("classroom"); String teacher = pair.get("teacher");
        List<Course> res = courseMapper.search(cid, ctime, cname, classroom, teacher);
        return Result.success(res);
    }
}
