package com.fajar.shopkeeping.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.constant.PageConstants;
import com.fajar.shopkeeping.pages.LoginPage;

public class LoginHandler extends MainHandler { 

	@Override
	protected void init() {
		super.init();
		page = new LoginPage();
	}

	public ActionListener doLogin(final JTextField usernameField, final JTextField passwordField) {
		return new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				final String username = usernameField.getText();
				final String password = passwordField.getText();
				doLogin(username, password);
				System.out.println("LOGIN: " + username + " & " + password);

			} 
		};
	}

	private void doLogin(String username, String password) {
		
		accountService.doLogin(username, password, new MyCallback() {
			
			public void handle(Object... params) throws Exception {
				 
				boolean success = (Boolean) params[0];
				if(success) {
					APP_HANDLER.navigate(PageConstants.PAGE_DASHBOARD);
				}
			}
		});
	}
}
