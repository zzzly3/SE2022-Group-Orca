package com.orca.back.utils.common;

import com.orca.back.entity.User;
import com.orca.back.mapper.UserMapper;
import com.orca.back.utils.constants.CommonCode;
import com.orca.back.utils.constants.ErrorCode;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

public class Checker {

    @Resource
    UserMapper userMapper;

    public ErrorCode checkRegistry(User user){
        String success = CommonCode.SUCCESS_CODE;

        /*身份证号*/
        if (user.getIdentifier() == null)
            return ErrorCode.E_103;
        else{
            String id = Integer.toString(user.getIdentifier());
            if (id.length() != 18)
                return ErrorCode.E_103;
        }
        /*手机号(选填)*/
        if (user.getPhone() != null)
        {
            String phone = Integer.toString(user.getPhone());
            if (phone.length() != 11 || phone.charAt(0) != '1')
                return ErrorCode.E_104;
        }
        /*邮箱（选填）*/
        if (user.getEmail() != null)
        {
            String email = user.getEmail();
            Integer apos = email.indexOf('@');
            Integer dotpos = email.indexOf('.');
            if (apos < 1 || dotpos - apos < 2)
                return ErrorCode.E_105;
        }
        /*姓名*/
        if (user.getName() == null)
            return ErrorCode.E_106;
        else {
            String name = user.getName();
            char c[] = name.toCharArray();
            for(int i = 0; i < c.length; i++)
                if (!isAlpha(c[i]) && !isHan(c[i]))
                    return ErrorCode.E_106;
        }
        /*学号*/
        if (user.getNumber() == null)
            return ErrorCode.E_107;
        else{
            String num = Integer.toString(user.getNumber());
            /*学生*/
            if (user.getRole() == 1 && num.length() != 6)
                return ErrorCode.E_107;
            /*老师*/
            if (user.getRole() == 2 && num.length() != 8)
                return ErrorCode.E_107;
        }
        /*角色*/
        if (user.getRole() == null)
            return ErrorCode.E_110;
        else{
            Integer role = user.getRole();
            if (role < 1 || role > 2)
                return ErrorCode.E_110;
        }

        return null;
    }


    public ErrorCode checkPassword(String pw){
        if (pw.length() > 32 || pw.length() < 6)
            return ErrorCode.E_108;
        Integer alpha = 0, digit = 0, special = 0;
        for (int i = 0; i < pw.length(); i++){
            char c = pw.charAt(i);
            if (isAlpha(c)) alpha = 1;
            if (isDigit(c)) digit = 1;
            if (isSpecial(c)) special = 1;
        }
        Integer vary = alpha + digit + special;
        if (vary < 2) return ErrorCode.E_108;
        return null;
    }


    public ErrorCode checkAdmin(HttpServletRequest request){
        Integer u_id = (Integer) request.getSession().getAttribute("UserId");
        if (u_id == null){
            return ErrorCode.E_109;
        }
        User admin = userMapper.selectById(u_id);
        if (admin.getIsAdmin() == 0)
            return ErrorCode.E_111;
        return null;
    }


    public ErrorCode checkLogin(HttpServletRequest request){
        Integer u_id = (Integer) request.getSession().getAttribute("UserId");
        if (u_id == null){
            return ErrorCode.E_109;
        }
        return null;
    }


    public boolean isAlpha(char c){
        if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z')
            return true;
        return false;
    }

    public boolean isHan(char c){
        if (c >= 19968 && c <= 40869)
            return true;
        return false;
    }

    public boolean isDigit(char c){
        if (c >= '0' && c <= '9')
            return true;
        return false;
    }

    public boolean isSpecial(char c){
        if (c == '-' || c == '_')
            return true;
        return false;
    }
}
