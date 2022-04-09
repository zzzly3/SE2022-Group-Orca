package com.orca.back.utils.common;

import com.orca.back.entity.User;
import com.orca.back.utils.constants.ErrorCode;

import javax.servlet.http.HttpServletRequest;

public class Checker {

    public ErrorCode checkRegistry(User user){
        /*身份证号*/
        ErrorCode err;
        err = checkIdentifier(user.getIdentifier());
        if (err == null)
            err = checkName(user.getName());
        if (err == null && (user.getIsAdmin() == null || user.getIsAdmin().equals(0)))
            err = checkRoleAndNumber(user.getRole(), user.getNumber());
        if (err == null)
            err = (user.getPhone() != null && user.getPhone().length() > 0) ? checkPhone(user.getPhone()) : null;
        if (err == null)
            err = (user.getEmail() != null && user.getEmail().length() > 0) ? checkEmail(user.getEmail()) : null;
        return err;
    }

    public ErrorCode checkIdentifier(String id){
        return (id == null || id.length() != 18) ? ErrorCode.E_103 : null;
    }

    public ErrorCode checkPhone(String phone){
        return (phone.length() != 11 || phone.charAt(0) != '1') ? ErrorCode.E_104 : null;
    }

    public ErrorCode checkEmail(String email){
        int aPos = email.indexOf('@');
        int dotPos = email.indexOf('.');
        return (aPos < 1 || dotPos - aPos < 2) ? ErrorCode.E_105 : null;
    }

    public ErrorCode checkName(String name){
        if (name == null)
            return ErrorCode.E_106;
        else {
            for(int i = 0; i < name.length(); i++)
                if (!isAlpha(name.charAt(i)) && !isHan(name.charAt(i)))
                    return ErrorCode.E_106;
        }
        return null;
    }

    public ErrorCode checkRoleAndNumber(Integer role, Integer number){
        ErrorCode err = null;
        if (role == null || role < 1 || role > 2) return ErrorCode.E_110;
        if (number == null)
            err = ErrorCode.E_107;
        else{
            String num = Integer.toString(number);
            /*学生*/
            if (role == 2 && num.length() != 6 || role == 1 && num.length() != 8)
                err = ErrorCode.E_107;
        }
        return err;
    }

    public ErrorCode checkPassword(String pw){
        if (pw.length() > 32 || pw.length() < 6)
            return ErrorCode.E_108;
        int alpha = 0, digit = 0, special = 0;
        for (int i = 0; i < pw.length(); i++){
            char c = pw.charAt(i);
            if (isAlpha(c)) alpha = 1;
            if (isDigit(c)) digit = 1;
            if (isSpecial(c)) special = 1;
        }
        int vary = alpha + digit + special;
        if (vary < 2) return ErrorCode.E_108;
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
        return (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z');
    }

    public boolean isHan(char c){
        return (c >= 19968 && c <= 40869);
    }

    public boolean isDigit(char c){
        return (c >= '0' && c <= '9');
    }

    public boolean isSpecial(char c){
        return  (c == '-' || c == '_');
    }
}
