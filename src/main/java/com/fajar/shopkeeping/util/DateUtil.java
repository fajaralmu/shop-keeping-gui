package com.fajar.shopkeeping.util;

import java.util.Calendar;

public class DateUtil {
	
	/**
	 * get current month starting from 1
	 * @return
	 */
	public static int getCurrentMonth() {
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	public static int getCurrentYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}
}
