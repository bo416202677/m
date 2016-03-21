package cn.m.http.test.decorator.interfaces.impl;

import cn.m.http.test.decorator.interfaces.Feature;

public class FlyFeature implements Feature {

	@Override
	public void load() {
		System.out.println("增加一只翅膀!");
	}

}
