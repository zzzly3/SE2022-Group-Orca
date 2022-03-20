package com.orca.back.entity;

import com.orca.back.utils.common.Result;
import lombok.Data;

@Data
public class Info {
    private User user;
    private boolean login;
    private Result result;
}
