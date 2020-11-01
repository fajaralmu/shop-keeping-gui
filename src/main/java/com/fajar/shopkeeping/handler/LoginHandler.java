package com.fajar.shopkeeping.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.fajar.shopkeeping.callbacks.ApplicationException;
import com.fajar.shopkeeping.callbacks.BooleanCallback;
import com.fajar.shopkeeping.constant.PageConstants;
import com.fajar.shopkeeping.pages.LoginPage;

public class LoginHandler extends MainHandler<LoginPage> { 

	@Override
	protected void init() {
		super.init();
		page = new LoginPage();
	}

	public ActionListener doLogin() {
		return  (ActionEvent e)->{
			final String username = getPage().getTypedUsername();
			final String password = getPage().getTypedPassword();
			doLogin(username, password);
			System.out.println("LOGIN: " + username + " & " + password); 
		};
	} 
	
	private void doLogin(String username, String password) {
		
		accountService.doLogin(username, password,  
			
			 (Boolean success) -> { 
				if(success) {
					APP_HANDLER.navigate(PageConstants.PAGE_DASHBOARD);
				} 
		});
	}
}
