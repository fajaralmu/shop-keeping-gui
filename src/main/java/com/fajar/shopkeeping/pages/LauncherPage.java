package com.fajar.shopkeeping.pages;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.fajar.shopkeeping.component.BlankComponent;
import com.fajar.shopkeeping.handler.LauncherHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.model.ReservedFor;

public class LauncherPage extends BasePage {

	private JButton navigateLoginButton; 
 

	public LauncherPage() {
		super("Launcher", 400, 400);
	} 

	@Override
	public void initComponent() {
		navigateLoginButton = new JButton("Login"); 
		
		PanelRequest panelRequest = new PanelRequest(2, 150, 20, 15, Color.GRAY, 30, 30, 0, 0, true);

		JPanel mainPanel = buildPanel(panelRequest,

				title("Shop Keeping App!"), new BlankComponent(ReservedFor.BEFORE_HOR, 150, 20),
				label("Welcome to mart app..."), null, 
				navigateLoginButton);

		parentPanel.add(mainPanel);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

	}

	@Override
	protected void initEvent() {
		super.initEvent();
		
		navigateLoginButton.addActionListener(((LauncherHandler) appHandler).showLoginPage());
		 
	}

}
