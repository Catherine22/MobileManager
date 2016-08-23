package com.itheima.mobilesafe.services.gcj02;

/**
 * Created by Catherine on 2016/8/23.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class PointDouble {
    double x, y;

    public PointDouble(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String toString() {
        return "x=" + x + ", y=" + y;
    }
}