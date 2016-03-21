package cn.m.util.enums.example;

import cn.m.util.interfaces.Car;

public enum CarEnumFactory {
	FordCar, BuickCar;

	public Car create() {
		switch (this) {
		case FordCar:
			return new cn.m.util.interfaces.impl.FordCar();
		case BuickCar:
			return new cn.m.util.interfaces.impl.BuickCar();
		default:
			throw new AssertionError("无效参数");
		}
	}
}
