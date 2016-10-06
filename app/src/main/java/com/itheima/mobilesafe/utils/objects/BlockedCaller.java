package com.itheima.mobilesafe.utils.objects;

/**
 * Created by Catherine on 2016/9/29.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class BlockedCaller implements BaseContact {
    private String name;
    private String number;

    public int getMODE() {
        return MODE;
    }

    public void setMODE(int MODE) {
        this.MODE = MODE;
    }

    private int MODE;

    @Override
    public String toString() {
        return "BlockedCaller{" +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", MODE=" + MODE +
                '}';
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNumber() {
        return number;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setNumber(String number) {
        this.number = number;
    }
}
