package com.fajar.shopkeeping.util;

import com.fajar.shopkeeping.component.Loadings;

public class ThreadUtil {

	public static void run(Runnable runnable) {
		
		Thread thread  = new Thread(runnable);
		Log.log("running thread: ", thread.getId());
		Log.log("active thread: ", Thread.activeCount());
		thread.start();
	}
	
	public static void runWithLoading(Runnable runnable) {
		Loadings.start();
		run(new Runnable() {
			
			@Override
			public void run() {
				try {
					runnable.run();
				}catch (Exception e) {
					// TODO: handle exception
				}finally {
					Loadings.end();
				}
				
			}
		});
	}
}
