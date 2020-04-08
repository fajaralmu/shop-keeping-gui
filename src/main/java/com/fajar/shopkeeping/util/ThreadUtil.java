package com.fajar.shopkeeping.util;

public class ThreadUtil {

	public static void run(Runnable runnable) {
		
		Thread thread  = new Thread(runnable);
		Log.log("running thread: ", thread.getId());
		thread.start();
	}
}
