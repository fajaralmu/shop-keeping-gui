package com.fajar.shopkeeping.pages;

import static com.fajar.shopkeeping.model.PanelRequest.intArray;
import static com.fajar.shopkeeping.component.builder.ComponentActionListeners.*;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import static com.fajar.shopkeeping.component.builder.ComponentBuilder.*;
import com.fajar.shopkeeping.component.builder.ComponentBuilder;
import com.fajar.shopkeeping.component.builder.InputComponentBuilder;
import com.fajar.shopkeeping.handler.LoginHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.util.Log;

import lombok.Data;

@Data
public class LoginPage extends BasePage {

	private JButton loginButton  ;
	private JTextField usernameField ;
	private JPasswordField passwordField ;
	
	private String typedUsername;
	private String typedPassword;

	public LoginPage() {
		super("Login", 400, 300);
	}

	@Override
	public void initComponent() { 

		PanelRequest panelRequest = new PanelRequest(1, 300, 20, 15, Color.WHITE, 30, 30, 0, 0, true);
		panelRequest.setCenterAligment(true);

		JPanel loginFormPanel = getLoginFormPanel();

		mainPanel = ComponentBuilder.buildPanelV2(panelRequest,
				title("Please Login"), loginFormPanel);

		parentPanel.add(mainPanel);
		exitOnClose();

	}

	private JPanel getLoginFormPanel() {
		loginButton  = ComponentBuilder.button("Login", Color.YELLOW);
		usernameField = InputComponentBuilder.textField("admin");
		passwordField = InputComponentBuilder.passwordField("123");
		
		
		PanelRequest panelRequest = new PanelRequest(intArray(64, 100), 20, 15, Color.WHITE, 10, 10, 0, 0, true);
		Log.log("LOGIN PAGE**");
		JPanel panel = ComponentBuilder.buildPanelV3(panelRequest, 
				ComponentBuilder.label("Username"), usernameField, 
				ComponentBuilder.label("Password") ,passwordField,
				null, loginButton);
		return panel;
	}

	@Override
	protected void initEvent() { 
		addActionListener(loginButton,((LoginHandler) appHandler).doLogin( ));
		addKeyListener(usernameField, super.textFieldKeyListener(usernameField, "typedUsername"));
		addKeyListener(passwordField, super.textFieldKeyListener(passwordField, "typedPassword"));  
		super.initEvent();

	}
	
	@Override
	protected void setDefaultValues() {
		setTypedUsername (usernameField.getText());
		setTypedPassword (String.valueOf(passwordField.getPassword()));
		super.setDefaultValues();
	}

}
