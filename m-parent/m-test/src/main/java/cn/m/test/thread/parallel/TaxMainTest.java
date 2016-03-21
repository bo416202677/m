package cn.m.test.thread.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TaxMainTest {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ExecutorService es = Executors.newFixedThreadPool(50);
		
		List<TaxCaculator> list = new ArrayList<TaxCaculator>(5);
		list.add(new TaxCaculator(1000));
		list.add(new TaxCaculator(50));
		list.add(new TaxCaculator(24821));
		list.add(new TaxCaculator(318324));
		es.submit(new TaxCaculator(121));
		List<Future<Integer>> result = es.invokeAll(list);
		
		StringBuilder builder = new StringBuilder("\n");
		
		while(!es.isTerminated()){
			TimeUnit.MILLISECONDS.sleep(200);
			System.out.print(".");
		}
		
		for(Future<Integer> future : result) {
			builder.append("税款分别为:").append(future.get()).append(",");
		}
		System.err.println(builder.deleteCharAt(builder.lastIndexOf(",")).toString());
		es.shutdown();
	}

}
