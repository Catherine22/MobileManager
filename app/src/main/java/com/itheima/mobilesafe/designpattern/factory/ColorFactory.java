package com.itheima.mobilesafe.designpattern.factory;

/**
 * Created by Catherine on 2016/10/4.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class ColorFactory {
    public final static int BLUE = 0;
    public final static int RED = 1;


    public Color getColor(int c) {
        Color color = null;
        switch (c) {
            case BLUE:
                color = new Blue();
                break;
            case RED:
                color = new Red();
                break;
        }
        return color;
    }


}
