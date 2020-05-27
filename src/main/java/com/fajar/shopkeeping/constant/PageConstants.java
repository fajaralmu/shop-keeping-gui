package com.fajar.shopkeeping.constant;

public enum PageConstants {
	PAGE_LOGIN(1), PAGE_DASHBOARD(2), PAGE_LAUNCHER(3), PAGE_PERIODIC_REPORT(4), PAGE_MANAGEMENT(5),
	PAGE_TRAN_SUPPLY(6), PAGE_TRAN_SELLING(7);

	public final int value;

	private PageConstants(int value) {
		this.value = (value);
	}

}
