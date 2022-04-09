package com.orca.back.entity;

import lombok.Data;

@Data
public class ResetInfo {
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String uid;
    private String originPw;
    private String newPw;

    public String getNewPw() {
        return newPw;
    }

    public String getOriginPw() {
        return originPw;
    }

    public void setNewPw(String newPw) {
        this.newPw = newPw;
    }

    public void setOriginPw(String originPw) {
        this.originPw = originPw;
    }
}
