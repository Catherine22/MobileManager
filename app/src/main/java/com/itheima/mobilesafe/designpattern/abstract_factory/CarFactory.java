package com.itheima.mobilesafe.designpattern.abstract_factory;

/**
 * Created by Catherine on 2016/10/4.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

import com.itheima.mobilesafe.designpattern.abstract_factory.brand_factory.BMW;
import com.itheima.mobilesafe.designpattern.abstract_factory.brand_factory.Bentley;
import com.itheima.mobilesafe.designpattern.abstract_factory.brand_factory.Brand;
import com.itheima.mobilesafe.designpattern.abstract_factory.color_factory.Blue;
import com.itheima.mobilesafe.designpattern.abstract_factory.color_factory.Color;
import com.itheima.mobilesafe.designpattern.abstract_factory.color_factory.Red;

/**
 * 抽象工厂用在有大量工厂时,大致上与工厂模式一样,可理解成超级工厂模式
 */
public class CarFactory extends CarAbstractFactory {
    public final static int BLUE = 0;
    public final static int RED = 1;
    public final static int _BMW = 0;
    public final static int BENTLEY = 1;

    @Override
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

    @Override
    public Brand getBrand(int b) {
        Brand brand = null;
        switch (b) {
            case _BMW:
                brand = new BMW();
                break;
            case BENTLEY:
                brand = new Bentley();
                break;
        }
        return brand;
    }
}
