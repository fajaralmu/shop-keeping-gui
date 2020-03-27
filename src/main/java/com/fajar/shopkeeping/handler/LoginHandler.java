package com.fajar.shopkeeping.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

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

				System.out.println("LOGIN: " + username + " & " + password);

			}
		};
	}

}
