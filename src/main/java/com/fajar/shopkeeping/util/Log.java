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
//		System.out.println("## "+sb.toString());
	}
	
	public static void log(String[] strings) {
		Object[] objects = new Object[strings.length];
		for (int i = 0; i < strings.length; i++) {
			objects[i] = strings[i];
		}
		
		log(objects);
	}
}
