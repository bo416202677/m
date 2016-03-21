package cn.m.util.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

@Component
public class ThreadPool {
	private int corePoolSize = 10;
	private int maxPoolSize = 100;
	ThreadPoolExecutor threadPool = null;

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public String getPoolStatus() {
		return this.threadPool.toString();
	}

	public ThreadPool() {
		// 构建线程池
		threadPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 0L,
				TimeUnit.MICROSECONDS,
				new ArrayBlockingQueue<Runnable>(100000),
				new ThreadPoolExecutor.DiscardPolicy());
	}

	public void execute(Runnable r) {
		threadPool.execute(r);
	}
}
