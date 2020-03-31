package com.fajar.shopkeeping.util;

public class Log {

	public static void log(Object ...objects) {
		StringBuilder sb = new StringBuilder();
		for (Object object : objects) {
			if(object == null) {
				object = "";
			}
			sb.append(" ").append(object);
		}
		System.out.println("## "+sb.toString());
	}
}
