package cn.m.test.event.listener.impl;

import cn.m.test.event.listener.DemoEventListener;
import cn.m.test.event.object.DemoEvent;

public class FirstEventListener implements DemoEventListener{

	@Override
	public void processEvent(DemoEvent demoEvent) {
		System.out.println("FirstEventListener process event...");
	}

}
