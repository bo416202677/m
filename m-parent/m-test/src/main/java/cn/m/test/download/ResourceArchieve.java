package cn.m.test.download;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ResourceArchieve {

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		String urlStr = "http://p2.so.qhimg.com/t01b7514f987e319087.jpg";
		InputStream is = null;
		RandomAccessFile raf = null;
		try {
			URL url = new URL(urlStr);
			URLConnection connection = url.openConnection();
			long size = connection.getContentLengthLong();
			System.err.println("file size is " + size);
			String fileName = url.getFile().replace("/", "");
			is = url.openStream();
			raf = new RandomAccessFile(fileName, "rw");
			byte[] buff = new byte[1];
			int result = is.read(buff);
			while(result>0){
				raf.write(buff, 0, result);
				result = is.read(buff);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(is != null){
					is.close();
				}
				if(raf != null){
					raf.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.err.println("下载图片执行时间: " + (System.currentTimeMillis()-start));
	}

}
