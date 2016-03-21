package cn.m.test.event.listener;

import cn.m.test.event.object.DemoEvent;

public interface DemoEventListener {

	/**
	 * 
	 * @param demoEvent
	 */
	void processEvent(DemoEvent demoEvent);
}
