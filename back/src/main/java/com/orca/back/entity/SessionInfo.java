package com.orca.back.entity;

import com.orca.back.utils.common.Result;
import lombok.Data;

@Data
public class SessionInfo {
    private User user;
    private boolean login;
    private Result result;
}
