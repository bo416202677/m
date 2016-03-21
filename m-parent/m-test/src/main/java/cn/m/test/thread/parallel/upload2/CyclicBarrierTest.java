package cn.m.test.thread.parallel.upload2;

import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierTest {

	public static void main(String[] args) {
		final long start = System.currentTimeMillis();
		CyclicBarrier cb = new CyclicBarrier(3, new Runnable() {
			
			@Override
			public void run() {
				System.err.println("图片均已上传完毕!执行时间:" + (System.currentTimeMillis()-start));
			}
		});
		
		new Thread(new UploadPicRunner(cb, "原图")).start();
		new Thread(new ImageCutRunner(cb, "中图")).start();
		new Thread(new ImageCutRunner(cb, "小图")).start();
		
	}
}
