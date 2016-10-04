package com.itheima.mobilesafe.designpattern.builder;

/**
 * Created by Catherine on 2016/10/4.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class OldStyleRobotBuilder implements RobotBuilder {
    Robot robot;

    public OldStyleRobotBuilder() {
        robot = new Robot();
    }

    @Override
    public void buildRobotArms() {
        robot.setArms("Blowtorch arms");
    }

    @Override
    public void buildRobotLegs() {
        robot.setLegs("Roller shakes");
    }

    @Override
    public void buildRobotHead() {
        robot.setHead("Tin head");
    }

    @Override
    public void buildRobotTorso() {
        robot.setTorso("Tin torso");
    }

    @Override
    public Robot getRobot() {
        return robot;
    }
}
