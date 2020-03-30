package com.fajar.shopkeeping.pages;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.fajar.shopkeeping.component.BlankComponent;
import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.handler.LoginHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.model.ReservedFor;

public class LoginPage extends BasePage {

	private JButton loginButton; 
	private JTextField usernameField;
	private JTextField passwordField;
 

	public LoginPage() {
		super("Login", 400, 400);
	} 

	@Override
	public void initComponent() {
		loginButton = button("Login"); 
		usernameField = textField("admin123");
		passwordField = textField("123");
		
		PanelRequest panelRequest = new PanelRequest(2, 150, 20, 15, Color.ORANGE, 30, 30, 0, 0, true);

		JPanel mainPanel = ComponentBuilder.buildPanelV2(panelRequest,

				title("Please Login"), new BlankComponent(ReservedFor.BEFORE_HOR, 150, 20),
				label("Username"), usernameField, 
				label("Password"), passwordField,
				loginButton);

		parentPanel.add(mainPanel);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

	}

	

	@Override
	protected void initEvent() {
		super.initEvent();
		
		loginButton.addActionListener(((LoginHandler) appHandler).doLogin(usernameField, passwordField)); 
	}

}
