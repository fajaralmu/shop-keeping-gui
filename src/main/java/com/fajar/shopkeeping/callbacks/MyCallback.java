package com.fajar.shopkeeping.callbacks;

public interface MyCallback<R> {
	
	
	public void handle (R responseObject) throws ApplicationException;

}
