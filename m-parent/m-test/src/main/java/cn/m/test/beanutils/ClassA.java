package cn.m.test.beanutils;

public class ClassA {

	private Long id;

	private String name;

	private int sex;

	private int age;

	private ClassC c;

	public ClassA(Long id, String name, int sex, int age) {
		super();
		this.id = id;
		this.name = name;
		this.sex = sex;
		this.age = age;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public ClassC getC() {
		return c;
	}

	public void setC(ClassC c) {
		this.c = c;
	}

	@Override
	public String toString() {
		return "ClassA [id=" + id + ", name=" + name + ", sex=" + sex
				+ ", age=" + age + ", c=" + c + "]";
	}

}
