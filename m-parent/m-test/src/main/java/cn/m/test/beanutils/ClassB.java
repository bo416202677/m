package cn.m.test.beanutils;

public class ClassB {
	private Long id;

	private String name;

	private int sex;

	private int age;

	private String add;

	public String getAdd() {
		return add;
	}

	public void setAdd(String add) {
		this.add = add;
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

	@Override
	public String toString() {
		return "ClassB [id=" + id + ", name=" + name + ", sex=" + sex
				+ ", age=" + age + ", add=" + add + "]";
	}

}
