package com.fajar.shopkeeping.pages;

import static com.fajar.shopkeeping.component.builder.ComponentBuilder.*;
import java.awt.Color;

import javax.swing.JButton;

import com.fajar.shopkeeping.component.builder.ComponentBuilder;
import com.fajar.shopkeeping.handler.LauncherHandler;
import com.fajar.shopkeeping.model.PanelRequest;

public class LauncherPage extends BasePage {

	private JButton navigateLoginButton; 
 

	public LauncherPage() {
		super("Launcher", BASE_WIDTH, BASE_HEIGHT);
	} 

	@Override
	public void initComponent() {
		navigateLoginButton = button("Login"); 
		
		PanelRequest panelRequest = new PanelRequest(1, 670, 20, 15, Color.WHITE, 30, 30, 0, 0, false, true); 
		
		mainPanel = ComponentBuilder.buildPanelV3(panelRequest,

				title("BUMDES", 50),
				title("\"MAJU MAPAN\"", 50),
				title("DESA TRIKARSO", 50),
				title("SRUWENG", 50),
				title("KEBUMEN", 50),
				ComponentBuilder.label("Silakan Login Untuk Melanjutkan"), 
				navigateLoginButton); 

		parentPanel.add(mainPanel);
		exitOnClose();

	}

	@Override
	protected void initEvent() {
		super.initEvent();
		
		navigateLoginButton.addActionListener(((LauncherHandler) appHandler).showLoginPage()); 
		 
	}

}
