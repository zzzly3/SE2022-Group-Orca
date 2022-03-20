package com.orca.back.utils.common;

import com.orca.back.utils.constants.CommonCode;
import com.orca.back.utils.constants.ErrorCode;

public class Result<T> {
    private String code;
    private String msg;
    private T data;

    public String getCode(){return code;}

    public void setCode(String code){this.code = code;}

    public String getMsg(){return msg;}

    public void setMsg(String msg){this.msg = msg;}

    public T getData(){return data;}

    public void setData(T data){this.data = data;}

    public Result(){}

    public Result(T data){this.data = data;}

    public static Result success(){
        Result result = new Result<>();
        result.setCode(CommonCode.SUCCESS_CODE);
        result.setMsg(CommonCode.SUCCESS_MSG);
        return result;
    }

    public static <T> Result<T> success(T data){
        Result<T> result = new Result<>(data);
        result.setCode(CommonCode.SUCCESS_CODE);
        result.setMsg(CommonCode.SUCCESS_MSG);
        return result;
    }

    public static Result error(ErrorCode errorCode){
        Result result = new Result();
        result.setCode(errorCode.getCode());
        result.setMsg(errorCode.getMsg());
        return result;
    }


}

