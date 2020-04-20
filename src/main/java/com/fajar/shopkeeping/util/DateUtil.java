package com.fajar.shopkeeping.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

	/**
	 * get current month starting from 1
	 * 
	 * @return
	 */
	public static int getCurrentMonth() {
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	public static String todayString() {
		return DateUtil.getCurrentDay() + " " + DateUtil.getCurrentMonthString() + " " + DateUtil.getCurrentYear();
	}

	public static int getCurrentDay() {
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}

	public static String getCurrentMonthString() {
		return StringUtil.months[getCurrentMonth() - 1];
	}

	public static int getCurrentYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	public static String dateString(int day, int month, int year) {

		return day + " " + StringUtil.months[month - 1] + " " + year;
	}

	public static String dateString(int month, int year) {

		return StringUtil.months[month - 1] + " " + year;
	}
	
	public static String formatDate(Date date, String pattern) {
		try {
			simpleDateFormat.applyPattern(pattern);
			return simpleDateFormat.format(date);
		}catch (Exception e) { 
			return date.toString();
		}
	}
 
}
