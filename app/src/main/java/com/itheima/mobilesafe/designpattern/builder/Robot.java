package com.itheima.mobilesafe.designpattern.builder;

/**
 * Created by Catherine on 2016/10/4.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class Robot implements RobotPlan {
    private String robotArms;
    private String robotTorso;
    private String robotLegs;
    private String robotHead;


    @Override
    public void setArms(String arms) {
        robotArms = arms;
    }

    public String getArms() {
        return robotArms;
    }

    @Override
    public void setTorso(String torso) {
        robotTorso = torso;
    }

    public String getTorso() {
        return robotTorso;
    }

    @Override
    public void setHead(String head) {
        robotHead = head;
    }

    public String getHead() {
        return robotHead;
    }

    @Override
    public void setLegs(String legs) {
        robotLegs = legs;
    }

    public String getLegs() {
        return robotLegs;
    }
}
