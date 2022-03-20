package com.orca.back.entity;

import com.orca.back.utils.common.Result;
import lombok.Data;

@Data
public class SessionInfo {
    private User user;
    private boolean login;
    private Result result;

    public boolean isLogin() {
        return login;
    }

    public Result getResult() {
        return result;
    }

    public User getUser() {
        return user;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
