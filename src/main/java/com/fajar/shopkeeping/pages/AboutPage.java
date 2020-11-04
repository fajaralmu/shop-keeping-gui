package com.fajar.shopkeeping.pages;

import static com.fajar.shopkeeping.component.builder.ComponentBuilder.labelLeftAligment;
import static com.fajar.shopkeeping.pages.LauncherPage.applicationIcon;
import static com.fajar.shopkeeping.pages.LauncherPage.getApplicationAddress;
import static com.fajar.shopkeeping.pages.LauncherPage.getApplicationName;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.fajar.shopkeeping.component.builder.ComponentBuilder;
import com.fajar.shopkeeping.component.builder.ComponentModifier;
import com.fajar.shopkeeping.handler.AboutApplicationHander;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.service.AppSession;

public class AboutPage extends BasePage<AboutApplicationHander> {
 
	private JLabel labelAppName;
	private JLabel labelAppAddress;
	private JLabel labelApplicationIcon;
	
	public AboutPage() {
		super("About Application", BASE_WIDTH*3/4, BASE_HEIGHT*3/4);
		doNotCloseOtherPage();
	}

	@Override
	public void initComponent() {
		
		labelAppName = labelLeftAligment(getApplicationName());
		labelAppAddress = labelLeftAligment(getApplicationAddress());//, true, Color.white); 
		labelApplicationIcon = applicationIcon();
		ComponentModifier.changeSizeWidth(labelAppAddress, 400);
		JPanel content = getApplicationInformation();
		
		mainPanel = ComponentBuilder.buildVerticallyInlineComponent(WIDTH,
				labelApplicationIcon,
				content);   

		parentPanel.add(mainPanel); 
	}
	
	private JPanel getApplicationInformation() {
		
		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(new int[]{150, 400}, 5, Color.white);
		labelAppAddress.setBackground(Color.green);
		return ComponentBuilder.buildPanelV2(panelRequest,
				("Name"),labelAppName,
				("Address"),labelAppAddress,
				("Java version"), labelLeftAligment(getJavaVersion()),
				("Contact Developer"), AppSession.getDeveloperContact());
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
