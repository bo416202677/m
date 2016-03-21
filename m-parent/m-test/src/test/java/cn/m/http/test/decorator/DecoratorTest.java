package cn.m.http.test.decorator;

import cn.m.http.test.decorator.interfaces.Animal;
import cn.m.http.test.decorator.interfaces.impl.DigFeature;
import cn.m.http.test.decorator.interfaces.impl.FlyFeature;
import cn.m.http.test.decorator.objs.Rat;

public class DecoratorTest {

	public static void main(String[] args) {
		Animal jerry = new Rat();
		
		jerry = new DecorateAnimal(jerry, FlyFeature.class);
		
		jerry = new DecorateAnimal(jerry, DigFeature.class);
		
		jerry.doStuff();
	}

}
