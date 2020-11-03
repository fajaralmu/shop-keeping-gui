package com.fajar.shopkeeping.pages;

import static com.fajar.shopkeeping.component.builder.ComponentBuilder.button;
import static com.fajar.shopkeeping.component.builder.ComponentBuilder.textarea;
import static com.fajar.shopkeeping.component.builder.ComponentBuilder.title;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import com.fajar.shopkeeping.component.builder.ComponentBuilder;
import com.fajar.shopkeeping.component.builder.ComponentModifier;
import com.fajar.shopkeeping.handler.LauncherHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.service.AppSession;

public class LauncherPage extends BasePage {

	private JButton navigateLoginButton;
	private JLabel labelAppName;
	private JTextArea labelAppAddress;
 

	public LauncherPage() {
		super("Launcher", BASE_WIDTH, BASE_HEIGHT);
	} 

	@Override
	public void initComponent() {
		navigateLoginButton = button("Login"); 
		labelAppName = title(getApplicationName(), 50);
		labelAppAddress = textarea(getApplicationAddress());
		ComponentModifier.changeSize(navigateLoginButton, 100, 50);
		PanelRequest panelRequest = new PanelRequest(1, 670, 20, 15, Color.WHITE, 30, 30, 0, 0, false, true); 
		
		mainPanel = ComponentBuilder.buildPanelV3(panelRequest,

				title("BUMDES", 50),
				labelAppName,
				labelAppAddress,  
				ComponentBuilder.label("Silakan Login Untuk Melanjutkan"), 
				ComponentBuilder.buildVerticallyInlineComponent(100, navigateLoginButton)); 

		parentPanel.add(mainPanel);
		exitOnClose();

	}

	@Override
	protected void initEvent() {
		super.initEvent();
		
		navigateLoginButton.addActionListener(((LauncherHandler) appHandler).showLoginPage()); 
		 
	}
	
	@Override
	public void onShow() {
		 
		labelAppName.setText(getApplicationName());
		labelAppAddress.setText(getApplicationAddress());
	}
	
	private String getApplicationName() {
		return AppSession.getApplicationProfile().getName();
	}
	private String getApplicationAddress() {
		return AppSession.getApplicationProfile().getAddress();
	}

}
