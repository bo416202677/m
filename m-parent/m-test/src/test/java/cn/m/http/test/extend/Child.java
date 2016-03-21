package cn.m.http.test.extend;

public class Child extends Parent{
	
	public static void say(String mess){
		System.err.println("Child say : " + mess);
	}

	public static void main(String[] args) {
		say("haha");
	}
}
