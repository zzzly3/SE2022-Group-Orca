package com.orca.back.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.orca.back.entity.*;
import com.orca.back.mapper.*;

import com.orca.back.utils.common.Checker;
import com.orca.back.utils.common.Result;

import com.orca.back.utils.constants.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static com.orca.back.controller.UserController.getResult;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Resource
    UserMapper userMapper;
    @Resource
    ClassTimeMapper classTimeMapper;
    @Resource
    ClassroomMapper classroomMapper;
    @Resource
    ConstantsMapper constantsMapper;

    @Autowired
    SelectionController selectionController = new SelectionController();
    Checker check = new Checker();

    private Result<?> checkAdmin(HttpServletRequest request) {
        return getResult(request, userMapper);
    }

    @PostMapping("/delete_classroom")
    public Result<?> delete_classroom(@RequestBody Map<String, String> pair, HttpServletRequest request){
        System.out.print("in backend: delete_classroom\n");
        Result<?> err1 = checkAdmin(request);
        if(err1 != null)return err1;
        Classroom cr = classroomMapper.selectById(pair.get("name"));
        if(cr == null)return Result.error(ErrorCode.E_207);
        classroomMapper.deleteById(pair.get("name"));
        return Result.success();
    }

    @PostMapping("/add_classroom")
    public Result<?> add_classroom(@RequestBody Classroom classroom, HttpServletRequest request){
        System.out.print("in backend: add_classroom\n");
        Result<?> err1 = checkAdmin(request);
        if(err1 != null)return err1;
        ErrorCode err = check.checkClassroom(classroom);
        if(err != null)return Result.error(err);
        Classroom res = classroomMapper.selectOne(
                Wrappers.<Classroom>lambdaQuery().eq(Classroom::getName, classroom.getName()));
        if(res != null)return Result.error(ErrorCode.E_211);
        classroomMapper.insert(classroom);
        return Result.success();
    }

    @PostMapping("/modify_classroom")
    public Result<?> modify_classroom(@RequestBody Classroom classroom, HttpServletRequest request){
        System.out.print("in backend: modify_classroom_state\n");
        Result<?> err1 = checkAdmin(request);
        if(err1 != null)return err1;
        /*非法输入*/
        ErrorCode err = check.checkClassroom(classroom);
        if(err != null)return Result.error(err);
        /*重复的教室*/
        Classroom res = classroomMapper.selectOne(
                Wrappers.<Classroom>lambdaQuery().eq(Classroom::getName, classroom.getName()));
        if(res == null)return Result.error(ErrorCode.E_207);
        classroomMapper.updateById(classroom);
        return Result.success();
    }

    @GetMapping("/load_open_classroom")
    public Result<?> load_open_classroom(HttpServletRequest request){
        ErrorCode err;
        err = check.checkLogin(request);
        if(err != null)return Result.error(err);
        List<Classroom> res = classroomMapper.findOpen();
        return Result.success(res);
    }

    @GetMapping("/load_all_classroom")
    public Result<?> load_all_classroom(HttpServletRequest request){
        ErrorCode err;
        err = check.checkLogin(request);
        if(err != null)return Result.error(err);
        List<Classroom> res = classroomMapper.findAll();
        return Result.success(res);
    }

    @GetMapping("/load_all_classTime")
    public Result<?> load_classTime(HttpServletRequest request){
        ErrorCode err;
        err = check.checkLogin(request);
        if(err != null)return Result.error(err);
        List<ClassTime> res = classTimeMapper.findAll();
        return Result.success(res);
    }

    @PostMapping("/select_classTime")
    public Result<?> select_classTime(@RequestBody Map<String, Integer> pair, HttpServletRequest request){
        System.out.print("in backend: select_classTime\n");
        Result<?> err1 = checkAdmin(request);
        if(err1 != null)return err1;
        ClassTime res = classTimeMapper.selectById(pair.get("id"));
        if(res == null)return Result.success();  //currently, not found is normal
        return Result.success(res);
    }

    @PostMapping("/modify_classTime")
    public Result<?> modify_classTime(@RequestBody ClassTime classTime, HttpServletRequest request){
        System.out.print("in backend: modify_classTime\n");
        Result<?> err1 = checkAdmin(request);
        if(err1 != null)return err1;
        /*非法输入*/
        ErrorCode err = check.checkClassTime(classTime);
        if(err != null)return Result.error(err);
        /*重复的课程节次*/
        List<Integer> intL = classTimeMapper.findConflictTime(classTime);
        if(intL.size() > 0)return Result.error(ErrorCode.E_210);

        ClassTime res = classTimeMapper.selectOne(
                Wrappers.<ClassTime>lambdaQuery().eq(ClassTime::getId, classTime.getId()));
        if(res != null){
            classTimeMapper.updateById(classTime);
        }else classTimeMapper.insert(classTime);
        return Result.success();
    }

    @PostMapping("/delete_classTime")
    public Result<?> delete_classTime(@RequestBody Map<String, Integer> pair , HttpServletRequest request) {
        System.out.print("in backend: delete_classTime\n");
        Result<?> err1 = checkAdmin(request);
        if(err1 != null)return err1;
        ClassTime res = classTimeMapper.selectById(pair.get("id"));
        if(res == null)return Result.error(ErrorCode.E_206);
        classTimeMapper.deleteById(pair.get("id"));
        return Result.success();
    }

    @GetMapping("/load_course_selection_state")
    public Result<?> load_course_selection_state(HttpServletRequest request){
        System.out.print("in backend: load_course_selection_state\n");
        Result<?> err1 = checkAdmin(request);
        if(err1 != null)return err1;
        String res = constantsMapper.getCourseSelectionState();
        return Result.success(res);
    }

    @PostMapping("/modify_course_selection_state")
    public Result<?> modify_course_selection_state(@RequestBody Map<String, Integer> pair, HttpServletRequest request){
        System.out.print("in backend: modify_course_selection_state\n");
        Result<?> err1 = checkAdmin(request);
        if(err1 != null)return err1;
        int state = pair.get("value");
        constantsMapper.updateCourseSelectionState(state);
        if(state == 2)return selectionController.first_round_end(request);
        else return Result.success();
    }
}
