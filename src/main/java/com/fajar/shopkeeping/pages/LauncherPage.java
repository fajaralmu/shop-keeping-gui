package com.fajar.shopkeeping.pages;

import static com.fajar.shopkeeping.component.builder.ComponentBuilder.button;
import static com.fajar.shopkeeping.component.builder.ComponentBuilder.label;
import static com.fajar.shopkeeping.component.builder.ComponentBuilder.title;

import javax.swing.JButton;
import javax.swing.JLabel;

import com.fajar.shopkeeping.component.builder.ComponentBuilder;
import com.fajar.shopkeeping.component.builder.ComponentModifier;
import com.fajar.shopkeeping.constant.UrlConstants;
import com.fajar.shopkeeping.handler.LauncherHandler;
import com.fajar.shopkeeping.service.AppSession;

public class LauncherPage extends BasePage<LauncherHandler> {

	private JButton navigateLoginButton;
	private JLabel labelAppName;
	private JLabel labelAppAddress;
	private JLabel labelApplicationIcon;
 

	public LauncherPage() {
		super("Launcher", 700, 450);
	} 

	@Override
	public void initComponent() {
		createComponents();
		
		mainPanel = ComponentBuilder.buildVerticallyInlineComponent(670,
				labelApplicationIcon,
				labelAppName,
				labelAppAddress,  
				label("Silakan Login Untuk Melanjutkan"), 
				(navigateLoginButton)); 

		parentPanel.add(mainPanel);
		exitOnClose();

	}
	
	private void createComponents() {
		navigateLoginButton = button("Login"); 
		labelAppName = title(getApplicationName(), 50);
		labelAppAddress = label(getApplicationAddress()); 
		labelApplicationIcon = applicationIcon();
		ComponentModifier.changeSize(navigateLoginButton, 100, 50);
	}
	
	public static JLabel applicationIcon() { 
		return ComponentBuilder.imageLabel(getApplicationImageUrl(), 200, 200); 
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		
		navigateLoginButton.addActionListener(((LauncherHandler) handler).showLoginPage()); 
		 
	}
	
	@Override
	public void onShow() {
		 
		//update some contents of components
		labelAppName.setText(getApplicationName());
		labelAppAddress.setText(getApplicationAddress());
		labelApplicationIcon.setText("");
		labelApplicationIcon.setBorder(null);
		labelApplicationIcon.setIcon(ComponentBuilder.imageIcon(getApplicationImageUrl(), 200, 200));
		super.onShow();
	}
	
	

}
