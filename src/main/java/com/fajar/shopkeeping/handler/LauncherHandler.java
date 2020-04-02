package com.fajar.shopkeeping.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.fajar.shopkeeping.constant.PageConstants;
import com.fajar.shopkeeping.pages.LauncherPage;

public class LauncherHandler extends MainHandler {

	public LauncherHandler() {
		super();
	}

	@Override
	protected void init() {
		super.init();  
		page = new LauncherPage();
	}

	

	public ActionListener showLoginPage() { 
		
		return new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				APP_HANDLER.navigate(PageConstants.PAGE_LOGIN);
			}
		};
	}
}
