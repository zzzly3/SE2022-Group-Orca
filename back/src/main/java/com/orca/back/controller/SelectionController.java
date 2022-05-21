package com.orca.back.controller;

import com.orca.back.mapper.ConstantsMapper;
import com.orca.back.mapper.CourseMapper;
import com.orca.back.mapper.SelectionConditionMapper;
import com.orca.back.mapper.UserMapper;
import com.orca.back.utils.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.Map;

import static com.orca.back.controller.UserController.getResult;

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

    private Result<?> checkAdmin(HttpServletRequest request) {
        return getResult(request, userMapper);
    }
//    private Result<?> checkStudent(HttpServletRequest request) {
//        return getResult(request, userMapper);
//    }
//   private Result<?> checkTreacher(HttpServletRequest request) {
//        return getResult(request, userMapper);
//    }

//    @PostMapping("/show_course_selection")
//    public Result<?> show_course_selection(@RequestBody Map<String, String> pair, HttpServletRequest request){
//        System.out.print("in backend: show_course_selection\n");
//        Result<?> err1 = checkAdmin(request);
//        if(err1 != null)return err1;
//    }
}
