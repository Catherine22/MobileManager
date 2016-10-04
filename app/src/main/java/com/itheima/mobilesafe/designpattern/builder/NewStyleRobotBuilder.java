package com.itheima.mobilesafe.designpattern.builder;

/**
 * Created by Catherine on 2016/10/4.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class NewStyleRobotBuilder implements RobotBuilder {
    Robot robot;

    public NewStyleRobotBuilder() {
        robot = new Robot();
    }

    @Override
    public void buildRobotArms() {
        robot.setArms("human Arms");
    }

    @Override
    public void buildRobotLegs() {
        robot.setLegs("human legs");
    }

    @Override
    public void buildRobotHead() {
        robot.setHead("human head");
    }

    @Override
    public void buildRobotTorso() {
        robot.setTorso("human torso");
    }

    @Override
    public Robot getRobot() {
        return robot;
    }
}
