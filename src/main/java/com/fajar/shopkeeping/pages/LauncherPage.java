package com.fajar.shopkeeping.pages;

import static com.fajar.shopkeeping.component.builder.ComponentBuilder.button;
import static com.fajar.shopkeeping.component.builder.ComponentBuilder.label;
import static com.fajar.shopkeeping.component.builder.ComponentBuilder.title;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;

import com.fajar.shopkeeping.component.builder.ComponentBuilder;
import com.fajar.shopkeeping.component.builder.ComponentModifier;
import com.fajar.shopkeeping.constant.UrlConstants;
import com.fajar.shopkeeping.handler.LauncherHandler; 
import com.fajar.shopkeeping.service.AppSession;

public class LauncherPage extends BasePage {

	private JButton navigateLoginButton;
	private JLabel labelAppName;
	private JLabel labelAppAddress;
	private JLabel labelApplicationIcon;
 

	public LauncherPage() {
		super("Launcher", 700, 450);
	} 

	@Override
	public void initComponent() {
		navigateLoginButton = button("Login"); 
		labelAppName = title(getApplicationName(), 50);
		labelAppAddress = label(getApplicationAddress()); 
		labelApplicationIcon = applicationIcon();
		ComponentModifier.changeSize(navigateLoginButton, 100, 50);
		
		mainPanel = ComponentBuilder.buildVerticallyInlineComponent(670,
				labelApplicationIcon,
				labelAppName,
				labelAppAddress,  
				label("Silakan Login Untuk Melanjutkan"), 
				(navigateLoginButton)); 

		parentPanel.add(mainPanel);
		exitOnClose();

	}
	
	private JLabel applicationIcon() { 
		return ComponentBuilder.imageLabel(getApplicationImageUrl(), 200, 200); 
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		
		navigateLoginButton.addActionListener(((LauncherHandler) appHandler).showLoginPage()); 
		 
	}
	
	@Override
	public void onShow() {
		 
		//update some contents of components
		labelAppName.setText(getApplicationName());
		labelAppAddress.setText(getApplicationAddress());
		labelApplicationIcon.setText("");
		labelApplicationIcon.setBorder(null);
		labelApplicationIcon.setIcon(ComponentBuilder.imageIcon(getApplicationImageUrl(), 200, 200));
	}
	
	private String getApplicationName() {
		return AppSession.getApplicationProfile().getName();
	}
	private String getApplicationAddress() {
		return AppSession.getApplicationProfile().getAddress();
	}
	private String getApplicationImageUrl() {
		String imageName = AppSession.getApplicationProfile().getIconUrl();
		String fullURL = UrlConstants.URL_IMAGE+"/"+imageName;
		System.out.println("Icon URL:"+fullURL);
		return fullURL;
	}

}
