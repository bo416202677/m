package cn.m.http.test;


public class MainTest {

	public static void main(String[] args) {
		String str = "http://ruixuesoftpicturetest.oss-cn-beijing.aliyuncs.com/718f716d11c747f687fc88853db0c775/9f08b3e3-38f8-4e32-b89a-94a421c88a5a.jpg,null";
		String[] arr = str.split(",");
		System.err.println(arr[1]);
		
	}

}
