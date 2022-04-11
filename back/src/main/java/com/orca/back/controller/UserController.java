package com.orca.back.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.orca.back.entity.*;
import com.orca.back.mapper.CollegeMapper;
import com.orca.back.mapper.MajorMapper;
import com.orca.back.utils.common.Checker;
import com.orca.back.utils.common.Result;
import com.orca.back.mapper.UserMapper;
import com.orca.back.utils.constants.ErrorCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    UserMapper userMapper;
    @Resource
    CollegeMapper collegeMapper;
    @Resource
    MajorMapper majorMapper;

    Checker check = new Checker();

    @PostMapping("/register")
    public Result<?> onSubmit(@RequestBody User user, HttpServletRequest request) {
        Result<?> err1 = checkAdmin(request);
        if (err1 != null) return err1;
        Result<?> err2 = register(user);
        if (err2 != null) return err2;
        return Result.success();
    }

    private Result<?> register(User user) {
        /*非法输入*/
        ErrorCode err = check.checkRegistry(user);
        if (err == null) {
            /*重复用户(仅筛选学号/工号)*/
            User res = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getNumber, user.getNumber()));
            if (res != null) err = ErrorCode.E_101;
        }
        if (err != null) return Result.error(err);
        Result<?> err2 = checkCollege(user);
        if (err2 != null) return err2;
        /*OK*/
        user.setPassword("123456");
        user.setIsAdmin(0);
        user.setIsFirst(1);
        userMapper.insert(user);
        return null;
    }

    // check college and major
    private Result<?> checkCollege(User user) {
        ErrorCode err = null;
        // if college or major is 0, then set to null
        if (user.getCollege() != null && user.getCollege() == 0) user.setCollege(null);
        if (user.getMajor() != null && user.getMajor() == 0) user.setMajor(null);
        // check whether the college and major exist if they are not null
        if (user.getCollege() != null) {
            College college = collegeMapper.selectById(user.getCollege());
            if (college == null) err = ErrorCode.E_132;
        }
        if (user.getMajor() != null) {
            Major major = majorMapper.selectById(user.getMajor());
            if (major == null) err = ErrorCode.E_133;
                // check whether the major and college are consistent
            else if (user.getCollege() == null || user.getCollege() != major.getCollege()) err = ErrorCode.E_134;
        }
        if (err != null) return Result.error(err);
        return null;
    }

    @PostMapping("/update")
    public Result<?> uu(@RequestBody User user, HttpServletRequest request) {
        ErrorCode err = null;
        User res = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getNumber, user.getNumber()));
        if (res == null) err = ErrorCode.E_111;
        if (err != null) return Result.error(err);
        user.setIsAdmin(res.getIsAdmin());
        /*检查管理员权限*/
        Integer u_id = (Integer) request.getSession().getAttribute("UserId");
        if (u_id == null) err = ErrorCode.E_109;
        if (err != null) return Result.error(err);
        User admin = userMapper.selectById(u_id);
        if (admin.getIsAdmin() == 0 && !Objects.equals(user.getNumber(), u_id)) err = ErrorCode.E_111;
        if (err != null) return Result.error(err);
        /*非法输入*/
        err = check.checkRegistry(user);
        if (err != null) return Result.error(err);
        /*OK*/
        if (admin.getIsAdmin() == 0) {
            res.setEmail(user.getEmail());
            res.setPhone(user.getPhone());
        } else {

            Result<?> err2 = checkCollege(user);
            if (err2 != null) return err2;
            res.setEmail(user.getEmail());
            res.setPhone(user.getPhone());
            res.setIdentifier(user.getIdentifier());
            res.setName(user.getName());
            if (res.getIsAdmin() == 0) {
                res.setIsLeave(user.getIsLeave());
                res.setCollege(user.getCollege());
                res.setMajor(user.getMajor());
                // change college of res in database to null
                if (user.getCollege() == null)
                    userMapper.update(null, Wrappers.<User>lambdaUpdate().set(User::getCollege, null).eq(User::getNumber, res.getNumber()));
                // change major of res in database to null
                if (user.getMajor() == null)
                    userMapper.update(null, Wrappers.<User>lambdaUpdate().set(User::getMajor, null).eq(User::getNumber, res.getNumber()));
            }
        }
        userMapper.updateById(res);
        return Result.success();
    }

    @PostMapping("/list")
    public Result<?> userList(@RequestBody PageInfo page, HttpServletRequest request) {
        Result<?> err1 = checkAdmin(request);
        if (err1 != null) return err1;
        int from = page.getStart();
        int to = from + page.getCount();
        List<User> ulist = userMapper.selectList(Wrappers.<User>lambdaQuery().orderByAsc(User::getNumber).select(User::getNumber, User::getEmail, User::getPhone, User::getRole, User::getIdentifier, User::getName, User::getIsAdmin, User::getIsLeave, User::getMajor, User::getCollege).last(String.format("limit %d, %d", from, to - from)));
        ListInfo<User> list = new ListInfo<>(ulist, userMapper.selectCount(Wrappers.lambdaQuery()));
        return Result.success(list);
    }

    private Result<?> checkAdmin(HttpServletRequest request) {
        ErrorCode err = null;
        Integer u_id = (Integer) request.getSession().getAttribute("UserId");
        if (u_id == null) err = ErrorCode.E_109;
        else{
            User admin = userMapper.selectById(u_id);
            if (admin.getIsAdmin() == 0) err = ErrorCode.E_111;
        }
        if (err != null) return Result.error(err);
        return null;
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody User user, HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Set-Cookie", "SameSite=None");
        /*检查用户名和密码*/
        User res = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getNumber, user.getNumber()).eq(User::getPassword, user.getPassword()));
        if (res == null){
            return Result.error(ErrorCode.E_102);
        }
        if (res.getIsLeave() > 0) {
            return Result.error(ErrorCode.E_130);
        }
        /*session*/
        request.getSession().setAttribute("UserId", res.getNumber());
        return Result.success();
    }

    @PostMapping("/resetpw")
    public Result<?> resetpw(@RequestBody ResetInfo info, HttpServletRequest request){
        ErrorCode err;
        /*用户是否登入*/
        err = check.checkLogin(request);
        if (err != null)
            return Result.error(err);
        /*原密码是否匹配, 新密码格式是否错误*/
        Integer u_id = (Integer) request.getSession().getAttribute("UserId");
        User user = userMapper.selectById(u_id);
        if (info.getUid() != null) {
            if (user.getIsAdmin() != 0) {
                User target = userMapper.selectById(info.getUid());
                if (target == null) {
                    return Result.error(ErrorCode.E_131);
                }
                if (target.getIsAdmin() != 0) {
                    return Result.error(ErrorCode.E_111);
                }
                // change the password of target
                target.setPassword(info.getNewPw());
                userMapper.updateById(target);
                return Result.success();
            }
        }
        if (!info.getOriginPw().equals(user.getPassword()))
            err = ErrorCode.E_102;
        else {
            err = check.checkPassword(info.getNewPw());
        }
        if (err != null)
            return Result.error(err);
        /*OK*/
        user.setPassword(info.getNewPw());
        user.setIsFirst(0);
        userMapper.updateById(user);
        return Result.success();
    }

    @RequestMapping("/getinfo")
    public Result<SessionInfo> getInfo(HttpServletRequest request){

        SessionInfo res = new SessionInfo();
        ErrorCode err;
        /*用户是否登录*/
        err = check.checkLogin(request);
        if (err != null){
            res.setLogin(false);
            return Result.success(res);
        }
        /*checked*/
        Integer u_id = (Integer) request.getSession().getAttribute("UserId");
        User user = userMapper.selectById(u_id);
        user.setPassword(null);
        // get the college
        College college = collegeMapper.selectById(user.getCollege());
        res.setCollege(college);
        // get the major
        Major major = majorMapper.selectById(user.getMajor());
        res.setMajor(major);
        res.setUser(user);
        res.setLogin(true);
        return Result.success(res);
    }

    @CrossOrigin(origins = "http://localhost:9876", allowCredentials = "true")
    @RequestMapping("/logout")
    public Result<?> logout (HttpServletRequest request){
        /*检查用户登入*/
        ErrorCode err;
        err = check.checkLogin(request);
        if (err != null)
            return Result.error(err);
        /*logout*/
        request.getSession(false).removeAttribute("UserId");
        return Result.success();
    }

    // list colleges
    @PostMapping("/colleges")
    public Result<List<College>> colleges(){
        List<College> list = collegeMapper.selectList(null);
        return Result.success(list);
    }

    // list majors with the given college id through post request
    @PostMapping("/majors")
    public Result<List<Major>> majors(@RequestBody College college){
        List<Major> list = majorMapper.selectList(Wrappers.<Major>lambdaQuery().eq(Major::getCollege, college.getId()));
        return Result.success(list);
    }

    // add college if the user is admin
    @PostMapping("/add_college")
    public Result<?> addCollege(@RequestBody College college, HttpServletRequest request){
        Result<?> err1 = checkAdmin(request);
        if (err1 != null) return err1;
        /*OK*/
        college.setId(0);
        collegeMapper.insert(college);
        return Result.success();
    }

    // add major if the user is admin
    @PostMapping("/add_major")
    public Result<?> addMajor(@RequestBody Major major, HttpServletRequest request){
        Result<?> err1 = checkAdmin(request);
        if (err1 != null) return err1;
        // check if the college exists
        College college = collegeMapper.selectById(major.getCollege());
        if (college == null)
            return Result.error(ErrorCode.E_132);
        /*OK*/
        major.setId(0);
        majorMapper.insert(major);
        return Result.success();
    }

    // change college name if the user is admin
    @PostMapping("/update_college")
    public Result<?> updateCollege(@RequestBody College college, HttpServletRequest request){
        Result<?> err1 = checkAdmin(request);
        if (err1 != null) return err1;
        // check if the new name is empty
        if (college.getName().equals(""))
            return Result.error(ErrorCode.E_134);
        // check if the college exists
        College target = collegeMapper.selectById(college.getId());
        if (target == null)
            return Result.error(ErrorCode.E_132);
        /*OK*/
        target.setName(college.getName());
        collegeMapper.updateById(target);
        return Result.success();
    }

    // change major name if the user is admin
    @PostMapping("/update_major")
    public Result<?> updateMajor(@RequestBody Major major, HttpServletRequest request){
        Result<?> err1 = checkAdmin(request);
        if (err1 != null) return err1;
        if (major.getName().equals(""))
            return Result.error(ErrorCode.E_134);
        Major target = majorMapper.selectById(major.getId());
        if (target == null)
            return Result.error(ErrorCode.E_133);
        /*OK*/
        target.setName(major.getName());
        majorMapper.updateById(target);
        return Result.success();
    }

    // delete college if the user is admin
    @PostMapping("/delete_college")
    public Result<?> deleteCollege(@RequestBody College college, HttpServletRequest request){
        Result<?> err1 = checkAdmin(request);
        if (err1 != null) return err1;
        /*OK*/
        collegeMapper.deleteById(college.getId());
        return Result.success();
    }

    // delete major if the user is admin
    @PostMapping("/delete_major")
    public Result<?> deleteMajor(@RequestBody Major major, HttpServletRequest request){
        Result<?> err1 = checkAdmin(request);
        if (err1 != null) return err1;
        /*OK*/
        majorMapper.deleteById(major.getId());
        return Result.success();
    }

    @PostMapping("/import")
    public Result<?> handleFileUpload(@RequestPart(value = "file") final MultipartFile uploadfile, HttpServletRequest request){
        // check if the user is admin
        Result<?> err1 = checkAdmin(request);
        if (err1 != null) return err1;
        // get the file name
        String filename = uploadfile.getOriginalFilename();
        // check if the file name is null
        if (filename == null || filename.equals(""))
            return Result.error(ErrorCode.E_135);
        // check if the file extension is .csv
        if (!uploadfile.getOriginalFilename().endsWith(".csv"))
            return Result.error(ErrorCode.E_135);
        // get the file content
        String content = null;
        try {
            content = new String(uploadfile.getBytes());
        }
        catch (IOException e) {
            e.printStackTrace();
            return Result.error(ErrorCode.E_135);
        }
        // parse the .csv file
        String[] lines = content.split("\n");
        if (lines.length > 10000)
            return Result.success("行数过多，请尝试分批导入");
        for (int i = 0; i < lines.length; i++) {
            String[] items = lines[i].split(",", -1);
            String err = null;
            try {
                if (items.length != 8) {
                    err = "格式错误";
                    throw new Exception();
                }
                User user = new User();
                user.setNumber(Integer.valueOf(items[0]));
                if (items[1].equals("student"))
                    user.setRole(2);
                else if (items[1].equals("teacher"))
                    user.setRole(1);
                else {
                    err = "无效的角色";
                    throw new Exception();
                }
                user.setName(items[2]);
                user.setIdentifier(items[3]);
                user.setCollege(Objects.equals(items[4], "") ? null : Integer.valueOf(items[4]));
                user.setMajor(Objects.equals(items[5], "") ? null : Integer.valueOf(items[5]));
                user.setPhone(items[6]);
                user.setEmail(items[7]);
                user.setIsLeave(0);
                Result<?> r = register(user);
                if (r != null)
                    err = r.getMsg();
            } catch (Exception ignored) {
//                ignored.printStackTrace();
                if (err == null)
                    err = "数据无效";
            }
            if (err != null) {
                return Result.success("[已导入" + i + "/" + lines.length + "] 导入中断：第" + (i + 1) + "行" + err);
            }
        }
        return Result.success("[已导入" + lines.length + "/" + lines.length + "] 导入成功");
    }
}
