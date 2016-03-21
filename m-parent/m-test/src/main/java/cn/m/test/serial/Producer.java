package cn.m.test.serial;


public class Producer {

	public static void main(String[] args) {
		Person p = new Person();
		p.setName("haha");
		SerializationUtils.writeObject(p);
	}
}
