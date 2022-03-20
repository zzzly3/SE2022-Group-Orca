package com.orca.back.entity;

import com.orca.back.utils.common.Result;
import lombok.Data;

@Data
public class ResetInfo {
    private String originPw;
    private String newPw;
}
