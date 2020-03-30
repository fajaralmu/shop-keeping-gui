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
		
		PanelRequest panelRequest = new PanelRequest(1, 670, 20, 15, Color.WHITE, 30, 30, 0, 0, true);
		panelRequest.setCenterAligment(true);
		
		JPanel mainPanel = ComponentBuilder.buildPanelV2(panelRequest,

				title("BUMDES", 50),
				title("\"MAJU MAPAN\"", 50),
				title("DESA TRIKARSO", 50),
				title("SRUWENG", 50),
				title("KEBUMEN", 50),
				label("Silakan Login Untuk Melanjutkan"), 
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
