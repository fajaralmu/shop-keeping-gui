package com.fajar.shopkeeping.callbacks;

public interface MyCallback<ResponseObject> {
	
	
	public void handle (ResponseObject responseObject) throws Exception;

}
