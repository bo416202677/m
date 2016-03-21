package cn.m.test.thread.deadlock;

/**
 * @author Josh Wang(Sheng)
 * 
 * @email  josh_wang23@hotmail.com
 * 
 */
public class VisualVMDeadLock {
	
	public static void main(String[] args) {
		VisualVMDeadLock lock = new VisualVMDeadLock();
		Resource r1 = lock.new Resource();
		Resource r2 = lock.new Resource();
		
		Thread lockThread1 = lock.new LockThread1(r1, r2);
		Thread lockThread2 = lock.new LockThread2(r1, r2);
		
		lockThread1.setName("DeadLock-1");
		lockThread2.setName("DeadLock-2");
		
		lockThread1.start();
		lockThread2.start();
	}
	
	
	class Resource {
		private int i;

		public int getI() {
			return i;
		}

		public void setI(int i) {
			this.i = i;
		}
		
		
	}
	
	class LockThread1 extends Thread {
		private Resource r1, r2;
		
		public LockThread1(Resource r1, Resource r2) {
			this.r1 = r1;
			this.r2 = r2;
		}
		
		
		@Override
		public void run() {
			int j = 0;
			while (true) {
				synchronized (r1) {
					System.out.println("The first thread got r1's lock " + j);
					synchronized (r2) {
						System.out.println("The first thread got r2's lock " + j);
					}
				}
				j++;
			}
		}
	}
	
	class LockThread2 extends Thread {
		private Resource r1, r2;
		
		public LockThread2(Resource r1, Resource r2) {
			this.r1 = r1;
			this.r2 = r2;
		}
		
		@Override
		public void run() {
			int j = 0;
			while (true) {
				synchronized (r2) {
					System.out.println("The second thread got r2's lock " + j);
					synchronized (r1) {
						System.out.println("The second thread got r1's lock " + j);
					}
				}
				j ++;
			}
		}
	}

}
