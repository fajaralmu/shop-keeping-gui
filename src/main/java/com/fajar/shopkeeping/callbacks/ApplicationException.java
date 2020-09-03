package com.fajar.shopkeeping.callbacks;

public class ApplicationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6135484153587427442L;
	public ApplicationException(String msg) {
		super(msg);
	}
	public ApplicationException(Throwable e) {
		 super(e);
	}
}
