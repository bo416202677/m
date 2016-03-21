package cn.m.test.thread.parallel.upload;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UploadPicTest {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		long start = System.currentTimeMillis();
		int num = 3;
		CountDownLatch end = new CountDownLatch(num);
		
		ExecutorService es = Executors.newFixedThreadPool(3);
		
		List<Future<String>> futures = new ArrayList<Future<String>>();
		
		futures.add(es.submit(new UploadPicCaller(end, "原图")));
		futures.add(es.submit(new ImageCutCaller(end, "中图")));
		futures.add(es.submit(new ImageCutCaller(end, "小图")));
		end.await();
		
		for(Future<String> future : futures){
			System.out.println(Thread.currentThread().getId()+"-->" +System.currentTimeMillis()+"-->"+future.get());
		}
		System.err.println(Thread.currentThread().getId()+"-->" +System.currentTimeMillis()+"-->" +(System.currentTimeMillis()-start));
		es.shutdown();
	}

}
