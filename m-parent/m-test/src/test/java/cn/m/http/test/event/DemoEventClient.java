package cn.m.http.test.event;

import cn.m.test.event.listener.impl.FirstEventListener;
import cn.m.test.event.listener.impl.SecondDemoEventListener;
import cn.m.test.event.source.EventSource;

public class DemoEventClient {

	public static void main(String[] args) {
		EventSource eventSource = new EventSource();
		
		FirstEventListener firstEventListener = new FirstEventListener();
		eventSource.addDemoListener(firstEventListener);
		
		SecondDemoEventListener secondDemoEventListener = new SecondDemoEventListener();
		eventSource.addDemoListener(secondDemoEventListener);
		
		eventSource.notifyDemoEvent();
	}
}
