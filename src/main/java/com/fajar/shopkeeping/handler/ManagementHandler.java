package com.fajar.shopkeeping.handler;

import com.fajar.shopkeeping.pages.ManagementPage;

public class ManagementHandler extends MainHandler {

	public ManagementHandler() {
		super();
	}

	@Override
	protected void init() {
		super.init();  
		page = new ManagementPage();
	}

	

	 
}
