package com.orca.back.entity;

import lombok.Data;

@Data
public class SessionInfo {
    private User user;
    private boolean login;

    public College getCollege() {
        return college;
    }

    public void setCollege(College college) {
        this.college = college;
    }

    public Major getMajor() {
        return major;
    }

    public void setMajor(Major major) {
        this.major = major;
    }

    private College college;
    private Major major;

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
