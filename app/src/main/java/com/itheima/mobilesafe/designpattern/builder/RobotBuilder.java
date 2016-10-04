package com.itheima.mobilesafe.designpattern.builder;

/**
 * Created by Catherine on 2016/10/4.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public interface RobotBuilder {
    void buildRobotArms();

    void buildRobotLegs();

    void buildRobotHead();

    void buildRobotTorso();

    Robot getRobot();
}
