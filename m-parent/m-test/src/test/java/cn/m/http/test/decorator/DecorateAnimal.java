package cn.m.http.test.decorator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

import cn.m.http.test.decorator.interfaces.Animal;
import cn.m.http.test.decorator.interfaces.Feature;

public class DecorateAnimal implements Animal{
	
	private Animal animal;
	
	private Class<? extends Feature> cls;
	
	public DecorateAnimal(Animal animal, Class<? extends Feature> cls) {
		super();
		this.animal = animal;
		this.cls = cls;
	}

	@Override
	public void doStuff() {
		InvocationHandler handler = new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				Object obj = null;
				if(Modifier.isPublic(method.getModifiers())){
					obj = method.invoke(cls.newInstance(), args);
				}
				animal.doStuff();
				return obj;
			}
		};
		
		ClassLoader cl = getClass().getClassLoader();
		
		Feature proxy = (Feature) Proxy.newProxyInstance(cl, cls.getInterfaces(), handler);
		
		proxy.load();
	}

}
