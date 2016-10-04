package com.itheima.mobilesafe.designpattern.builder;

/**
 * Created by Catherine on 2016/10/4.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class RobotDirector {
    private RobotBuilder builder;

    public RobotDirector(RobotBuilder builder) {
        this.builder = builder;
    }

    public void makeRobot() {
        builder.buildRobotArms();
        builder.buildRobotHead();
        builder.buildRobotLegs();
        builder.buildRobotTorso();
    }

    public Robot getRobot() {
        return builder.getRobot();
    }
}
