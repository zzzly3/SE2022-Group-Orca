package com.orca.back.utils.constants;

public enum ErrorCode {
    E_101("101", "用户已存在"),
    E_102("102", "用户名或密码错误"),
    E_103("103", "请填写正确的身份证号"),
    E_104("104", "请填写正确的手机号"),
    E_105("105", "请填写正确的邮箱"),
    E_106("106", "请填写正确的姓名"),
    E_107("107", "请填写正确的学号/工号"),
    E_108("108", "密码格式错误"),
    E_109("109", "用户未登录"),
    E_110("110", "非法角色"),

    E_111("111", "当前用户没有权限"),

    E_130("130", "用户已停用"),
    E_131("131", "用户不存在"),
    E_132("132", "学院不存在"),
    E_133("133", "专业不存在"),
    E_134("134", "无效的名字");



    private final String code;
    private final String msg;

    ErrorCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode(){
        return code;
    }

    public String getMsg(){
        return msg;
    }
}
