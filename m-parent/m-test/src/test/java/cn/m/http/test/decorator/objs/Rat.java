package cn.m.http.test.decorator.objs;

import cn.m.http.test.decorator.interfaces.Animal;

public class Rat implements Animal {

	@Override
	public void doStuff() {
		System.out.println("Jerry will play with Tom!");
	}

}
