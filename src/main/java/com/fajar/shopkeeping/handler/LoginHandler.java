package com.fajar.shopkeeping.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import com.fajar.shopkeeping.pages.LoginPage;
import com.fajar.shopkeeping.webservice.AccountService;

public class LoginHandler extends MainHandler {
	
	private AccountService accountService =  AccountService.getInstance();

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
		// TODO Auto-generated method stub
		accountService.doLogin(username, password);
	}
}
