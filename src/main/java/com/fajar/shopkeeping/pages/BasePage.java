package com.fajar.shopkeeping.pages;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.component.MyCustomFrame;
import com.fajar.shopkeeping.handler.MainHandler;
import com.fajar.shopkeeping.model.PanelRequest;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class BasePage {

	protected final MyCustomFrame frame;
	protected MyCustomFrame parentFrame;
	protected final JPanel parentPanel = new JPanel();
	private final int WIDTH;
	private final int HEIGHT;
	private final String title;
	
	@Setter(value = AccessLevel.NONE)
	protected MainHandler appHandler;

	public BasePage(String title, int w, int h ) {
		this.frame = new MyCustomFrame(title, w, h);
		this.WIDTH = w;
		this.HEIGHT = h; 
		this.title = title;
		
		initMainComponent();
		initComponent();
		
	}

	protected void initEvent() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * set the handler of the page
	 * @param mainHandler
	 */
	public void setAppHandler(MainHandler mainHandler) {
		System.out.println("mainHandler: "+mainHandler);
		this.appHandler = mainHandler;
		initEvent();
	}
	
	private void initMainComponent() {
		parentPanel.setBackground(Color.WHITE);
		parentPanel.setLayout(null);
		parentPanel.setBounds(10, 10, WIDTH, HEIGHT);
		parentPanel.setSize(WIDTH, HEIGHT);
		frame.setContentPane(parentPanel);
		
	}

	public void initComponent() {
		System.out.println("METHOD NOT IMPLEMENTED");
	}

	public void show() {
		System.out.println("Show: "+title);
		this.frame.setVisible(true);
	}

	public void dismiss() {
		System.out.println("DISMISS :"+title);
		this.frame.setVisible(false);
		System.out.println(title+" visible: "+frame.isVisible());
		 
	}

	protected JPanel buildPanel(PanelRequest panelRequest, Component... components) {
		return ComponentBuilder.buildPanel(panelRequest, components);
	}

	/**
	 * ================================== COMPONENT INSTANCES
	 * ==================================
	 */

	protected Component title(String title) {

		JLabel label = new JLabel(title);
		Font font = new Font("Arial", Font.BOLD, 20);
		label.setFont(font);
		return label;
	}
	
	protected Component label(String title) {
		
		JLabel label = new JLabel(title); 
		return label;
	}


}
