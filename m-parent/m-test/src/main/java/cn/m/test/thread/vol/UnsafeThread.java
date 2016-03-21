package cn.m.test.thread.vol;

import java.util.concurrent.TimeUnit;

public class UnsafeThread implements Runnable {
	
	private volatile int count = 0;

	@Override
	public void run() {
		count++;
	}
	
	public int getCount(){
		return count;
	}
	
	public static void main(String[] args) throws InterruptedException {
		int value = 1000;
		
		int loops = 0;
		
		ThreadGroup tg = Thread.currentThread().getThreadGroup();
		
		
		while(loops++ < value){
			UnsafeThread ut = new UnsafeThread();
			
			for(int i = 0; i < value; i++){
				new Thread(ut).start();
			}
			
			do {
				TimeUnit.MILLISECONDS.sleep(15L);
			} while (tg.activeCount() != 1);
			
			if(ut.getCount() != value){
				System.err.println("循环到第" + loops + "遍, 出现线程不安全的情况");
				System.err.println("此时, count = " + ut.getCount());
				System.exit(0);
			}
		}
	}
}
