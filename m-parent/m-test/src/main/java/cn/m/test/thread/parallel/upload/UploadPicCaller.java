package cn.m.test.thread.parallel.upload;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class UploadPicCaller implements Callable<String> {

	private CountDownLatch end;

	private String pic;

	public UploadPicCaller(CountDownLatch end, String pic) {
		super();
		this.end = end;
		this.pic = pic;
	}

	@Override
	public String call() throws Exception {
		int time = new Random().nextInt(10);
		System.out.println(Thread.currentThread().getId() + "-->" + time);
		TimeUnit.SECONDS.sleep(time);
		end.countDown();
		return pic + "上传成功!";
	}

	public CountDownLatch getEnd() {
		return end;
	}

	public void setEnd(CountDownLatch end) {
		this.end = end;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

}
