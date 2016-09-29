package com.itheima.mobilesafe.utils.objects;

/**
 * Created by Catherine on 2016/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class MobileQuery {
    private String chgmobile;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getChgmobile() {
        return chgmobile;
    }

    public void setChgmobile(String chgmobile) {
        this.chgmobile = chgmobile;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getRetcode() {
        return retcode;
    }

    public void setRetcode(String retcode) {
        this.retcode = retcode;
    }

    public String getRetmsg() {
        return retmsg;
    }

    public void setRetmsg(String retmsg) {
        this.retmsg = retmsg;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    private String city;
    private String province;
    private String retcode;
    private String retmsg;
    private String supplier;

    @Override
    public String toString() {
        return "MobileQuery{" +
                "chgmobile='" + chgmobile + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", retcode='" + retcode + '\'' +
                ", retmsg='" + retmsg + '\'' +
                ", supplier='" + supplier + '\'' +
                '}';
    }
}
