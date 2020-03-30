package com.fajar.shopkeeping.pages;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.fajar.shopkeeping.component.BlankComponent;
import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.handler.LauncherHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.model.ReservedFor;

public class LauncherPage extends BasePage {

	private JButton navigateLoginButton; 
 

	public LauncherPage() {
		super("Launcher", 800, 700);
	} 

	@Override
	public void initComponent() {
		navigateLoginButton = button("Login"); 
		
		PanelRequest panelRequest = new PanelRequest(2, 150, 20, 15, Color.ORANGE, 30, 30, 0, 0, true);

		JPanel mainPanel = ComponentBuilder.buildPanelV2(panelRequest,

				title("Shop Keeping App!"), new BlankComponent(ReservedFor.BEFORE_HOR, 150, 20),
				label("Welcome to mart app..."), new BlankComponent(ReservedFor.BEFORE_HOR, 150, 20), 
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
