package com.orca.back.entity;

import com.orca.back.utils.common.Result;
import com.orca.back.utils.constants.ErrorCode;
import lombok.Data;

@Data
public class SessionInfo {
    private User user;
    private boolean login;
    private String code;
    private String msg;

    public boolean isLogin() {
        return login;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public User getUser() {
        return user;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
