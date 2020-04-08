package com.fajar.shopkeeping.handler;

import com.fajar.shopkeeping.pages.SupplyTransactionPage;

public class TransactionHandler extends MainHandler {

	public TransactionHandler() {
		super();
	}

	@Override
	protected void init() {
		super.init();  
		page = new SupplyTransactionPage();
	}
 

	 
}
