package com.orca.back.entity;

import lombok.Data;

@Data
public class PageInfo {
    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    private int start;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private int count;
}