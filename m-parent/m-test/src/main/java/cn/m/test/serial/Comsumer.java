package cn.m.test.serial;

public class Comsumer {

	public static void main(String[] args) {
		Person p = (Person) SerializationUtils.readObject();
		System.err.println(p);
	}
}
