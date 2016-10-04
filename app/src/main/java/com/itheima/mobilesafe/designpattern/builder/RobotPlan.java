package com.itheima.mobilesafe.designpattern.builder;

/**
 * Created by Catherine on 2016/10/4.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

/**
 * 建造模式
 */
public interface RobotPlan {
    void setArms(String arms);
    void setTorso(String torso);
    void setHead(String head);
    void setLegs(String legs);
}
