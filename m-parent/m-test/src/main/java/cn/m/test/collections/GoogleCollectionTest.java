package cn.m.test.collections;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

public class GoogleCollectionTest {

	public static void main(String[] args) {
		ImmutableList<String> list = ImmutableList.of("A");
		System.err.println(list);
		Multimap<String, String> phoneBook = ArrayListMultimap.create();
		phoneBook.put("张三", "110");
		phoneBook.put("张三", "119");
		System.err.println(phoneBook.get("张三"));
	}
}
