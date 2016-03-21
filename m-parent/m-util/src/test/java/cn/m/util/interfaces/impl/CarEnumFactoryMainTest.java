package cn.m.util.interfaces.impl;

import cn.m.util.enums.example.CarEnumFactory;
import cn.m.util.interfaces.Car;

public class CarEnumFactoryMainTest {

	public static void main(String[] args) {
		Car car = CarEnumFactory.BuickCar.create();
		System.err.println(car);
	}

}
