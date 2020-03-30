package com.fajar.shopkeeping.pages;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.handler.LoginHandler;
import com.fajar.shopkeeping.model.PanelRequest;

public class LoginPage extends BasePage {

	private JButton loginButton;
	private JTextField usernameField;
	private JPasswordField passwordField;

	public LoginPage() {
		super("Login", 400, 300);
	}

	@Override
	public void initComponent() {
		loginButton = button("Login");
		usernameField = textField("admin123");
		passwordField = passwordField("123");

		PanelRequest panelRequest = new PanelRequest(1, 300, 20, 15, Color.WHITE, 30, 30, 0, 0, true);
		panelRequest.setCenterAligment(true);

		JPanel loginFormPanel = getLoginFormPanel();

		JPanel mainPanel = ComponentBuilder.buildPanelV2(panelRequest,

				title("Please Login"), loginFormPanel);

		parentPanel.add(mainPanel);
		exitOnClose();

	}

	private JPanel getLoginFormPanel() {
		PanelRequest panelRequest = new PanelRequest(2, 100, 20, 15, Color.WHITE, 10, 10, 0, 0, true);
	 
		JPanel panel = ComponentBuilder.buildPanelV2(panelRequest, 
				label("Username"), usernameField, 
				label("Password") ,passwordField,
				null, loginButton);
		return panel;
	}

	@Override
	protected void initEvent() {
		super.initEvent();

		loginButton.addActionListener(((LoginHandler) appHandler).doLogin(usernameField, passwordField));
	}

}
