package com.orca.back.entity;

import lombok.Data;

import java.util.List;

@Data
public class ListInfo<T> {
    public ListInfo(List<T> ulist, Long selectCount) {
        setList(ulist);
        setCount(selectCount.intValue());
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private int count;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    private List<T> list;
}
