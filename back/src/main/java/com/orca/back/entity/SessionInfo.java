package com.orca.back.entity;

import lombok.Data;

@Data
public class SessionInfo {
    private User user;
    private boolean login;

    public void setUser(User user) {
        this.user = user;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public User getUser() {
        return user;
    }

    public boolean isLogin() {
        return login;
    }
}
