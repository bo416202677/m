package cn.m.test.thread.parallel.upload2;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class UploadPicRunner implements Runnable {

	private CyclicBarrier cb;

	private String pic;

	public UploadPicRunner(CyclicBarrier cb, String pic) {
		super();
		this.cb = cb;
		this.pic = pic;
	}

	@Override
	public void run() {
		int time = new Random().nextInt(10);
		System.out.println(pic + "-->" + Thread.currentThread().getId() + "-->" + time);
		try {
			TimeUnit.SECONDS.sleep(time);
			System.err.println(pic + "上传成功!");
			cb.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
	}

	public CyclicBarrier getCb() {
		return cb;
	}

	public void setCb(CyclicBarrier cb) {
		this.cb = cb;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

}
