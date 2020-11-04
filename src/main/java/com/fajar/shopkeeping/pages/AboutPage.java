package com.fajar.shopkeeping.pages;

import static com.fajar.shopkeeping.component.builder.ComponentBuilder.label;
import static com.fajar.shopkeeping.component.builder.ComponentBuilder.title;
import static com.fajar.shopkeeping.pages.LauncherPage.applicationIcon;
import static com.fajar.shopkeeping.pages.LauncherPage.getApplicationAddress;
import static com.fajar.shopkeeping.pages.LauncherPage.getApplicationName;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.fajar.shopkeeping.component.builder.ComponentBuilder;
import com.fajar.shopkeeping.component.builder.ComponentModifier;
import com.fajar.shopkeeping.model.PanelRequest;

public class AboutPage extends BasePage {

	private JButton navigateLoginButton;
	private JLabel labelAppName;
	private JTextArea labelAppAddress;
	private JLabel labelApplicationIcon;
	
	public AboutPage() {
		super("About Application", BASE_WIDTH, BASE_HEIGHT);
		doNotCloseOtherPage();
	}

	@Override
	public void initComponent() {
		
		labelAppName = label(getApplicationName());
		labelAppAddress = ComponentBuilder.textarea(getApplicationAddress(), true, Color.white); 
		labelApplicationIcon = applicationIcon();
		ComponentModifier.changeSize(labelAppAddress, BASE_WIDTH, 100);
		
		JPanel content = getApplicationInformation();
		
		mainPanel = ComponentBuilder.buildVerticallyInlineComponent(BASE_WIDTH,
				labelApplicationIcon,
				content);   

		parentPanel.add(mainPanel); 
	}
	
	private JPanel getApplicationInformation() {
		
		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(new int[]{100, 300}, 5, Color.white);
		return ComponentBuilder.buildPanelV2(panelRequest ,
				label("Name"),labelAppName,
				label("Address"),labelAppAddress,
				label("Java version"), label(getJavaVersion()));
	}

	private String getJavaVersion() {
		return  System.getProperty("java.version");
	}
	
	@Override
	public void onShow() {
		//update some contents of components
		labelAppName.setText(getApplicationName());
		labelAppAddress.setText(getApplicationAddress());
		
		labelApplicationIcon.setText("");
		labelApplicationIcon.setBorder(null);
		labelApplicationIcon.setIcon(ComponentBuilder.imageIcon(LauncherPage.getApplicationImageUrl(), 200, 200));
	}

}
