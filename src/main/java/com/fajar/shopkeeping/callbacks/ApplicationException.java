package com.fajar.shopkeeping.callbacks;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6135484153587427442L;
	public ApplicationException(String msg) {
		
		super(msg);
		log.error("ERROR OCCURED: {}", msg);
	}
	public ApplicationException(Throwable e) {
		
		 super(e);
		 log.error("ERROR OCCURED: {}", e);
	}
}
