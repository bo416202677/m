package cn.m.util.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class PropertyFileLoadUtil {

	public static void loadPropertyFile(String fileName){
		Properties props = new Properties();
		InputStream inStream;
		try {
			
			inStream = new FileInputStream(fileName);
			props.load(inStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		loadPropertyFile("classpath:test.properties");
	}
}
