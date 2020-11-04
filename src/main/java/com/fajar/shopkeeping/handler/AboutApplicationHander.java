package com.fajar.shopkeeping.handler;

import com.fajar.shopkeeping.pages.AboutPage;

public class AboutApplicationHander extends MainHandler<AboutPage> {

	public AboutApplicationHander() {
		super();
	}

	@Override
	protected void init() {
		super.init();  
		page = new AboutPage();
	} 
 
}
