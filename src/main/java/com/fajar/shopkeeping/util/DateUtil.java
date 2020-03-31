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
	
	public static String todayString() {
		return DateUtil.getCurrentDay()+" "+DateUtil.getCurrentMonthString()+" "+DateUtil.getCurrentYear();
	}
	
	public static int getCurrentDay() {
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}
	
	public static String getCurrentMonthString() {
		return StringUtil.months[getCurrentMonth()-1];
	}

	public static int getCurrentYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}
}
