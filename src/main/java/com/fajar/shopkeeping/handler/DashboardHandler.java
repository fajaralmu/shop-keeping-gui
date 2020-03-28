package com.fajar.shopkeeping.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.pages.DashboardPage;
import com.fajar.shopkeeping.webservice.AccountService;

public class DashboardHandler extends MainHandler { 

	public DashboardHandler() {
		super();
	}

	@Override
	protected void init() {
		super.init();  
		page = new DashboardPage();
	}

	public ActionListener logout() { 
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				 
				accountService.logout(new MyCallback() {
					
					@Override
					public void handle(Object... params) throws Exception {
						 
						boolean success = (Boolean) params[0];
						if(success) {
							APP_HANDLER.navigate(APP_HANDLER.PAGE_LOGIN);
						}
					}
				});
			}
		};
	}
 
}
