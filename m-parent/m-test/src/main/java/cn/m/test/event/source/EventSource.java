package cn.m.test.event.source;

import java.util.ArrayList;
import java.util.List;

import cn.m.test.event.listener.DemoEventListener;
import cn.m.test.event.object.DemoEvent;

public class EventSource {

	private List<DemoEventListener> listeners = new ArrayList<DemoEventListener>();

	public EventSource() {
	}
	
	public void addDemoListener(DemoEventListener demoListener){
		listeners.add(demoListener);
	}
	
	public void notifyDemoEvent(){
		for (DemoEventListener eventListener : listeners){
			DemoEvent demoEvent = new DemoEvent(this);
			eventListener.processEvent(demoEvent);
		}
	}
}
