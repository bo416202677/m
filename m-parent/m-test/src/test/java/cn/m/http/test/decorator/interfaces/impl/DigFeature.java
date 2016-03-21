package cn.m.http.test.decorator.interfaces.impl;

import cn.m.http.test.decorator.interfaces.Feature;

public class DigFeature implements Feature {

	@Override
	public void load() {
		System.out.println("增加钻地能力!");
	}

}
