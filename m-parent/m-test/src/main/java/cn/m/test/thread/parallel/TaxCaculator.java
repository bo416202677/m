package cn.m.test.thread.parallel;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class TaxCaculator implements Callable<Integer> {

	private int seedMoney;

	public TaxCaculator(int seedMoney) {
		super();
		this.seedMoney = seedMoney;
	}

	public int getSeedMoney() {
		return seedMoney;
	}

	public void setSeedMoney(int seedMoney) {
		this.seedMoney = seedMoney;
	}

	@Override
	public Integer call() throws Exception {
		TimeUnit.MILLISECONDS.sleep(1000);
		return seedMoney/10;
	}

}
