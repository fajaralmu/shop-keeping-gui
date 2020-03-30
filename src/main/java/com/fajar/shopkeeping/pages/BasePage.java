package com.fajar.shopkeeping.pages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.math.BigDecimal;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.component.MyCustomFrame;
import com.fajar.shopkeeping.handler.MainHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.util.StringUtil;

import jxl.format.Border;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class BasePage {

	protected final MyCustomFrame frame;
	protected MyCustomFrame parentFrame;
	protected final JPanel parentPanel = new JPanel();
	protected JPanel mainPanel;
	private final int WIDTH;
	private final int HEIGHT;
	private final String title;
	
	private boolean authRequired;
	protected boolean beginPage;

	@Setter(value = AccessLevel.NONE)
	protected MainHandler appHandler;

	public BasePage(String title, int w, int h) {
		this.frame = new MyCustomFrame(title, w, h);
		this.WIDTH = w;
		this.HEIGHT = h;
		this.title = title;

		initMainComponent();
		initComponent();

	}
	
	protected void exitOnClose() {
		if(null != frame) {
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		}
	}

	protected void initEvent() { 

	}

	/**
	 * set the handler of the page
	 * 
	 * @param mainHandler
	 */
	public void setAppHandler(MainHandler mainHandler) {
		System.out.println("mainHandler: " + mainHandler);
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
		System.out.println("Show: " + title);
		this.frame.setVisible(true);
	}

	public void dismiss() {
		System.out.println("DISMISS :" + title);
		this.frame.setVisible(false);
		System.out.println(title + " visible: " + frame.isVisible());

	}

	protected JPanel buildPanel(PanelRequest panelRequest, Component... components) {
		return ComponentBuilder.buildPanel(panelRequest, components);
	}
	
	protected JPanel buildPanelV2(PanelRequest panelRequest, Component... components) {
		return ComponentBuilder.buildPanelV2(panelRequest, components);
	}

	public void refresh() {

	}

	public void onShow() {

	}

	/**
	 * ================================== COMPONENT INSTANCES
	 * ==================================
	 */

	protected JLabel title(String title, int fontSize) {
		int width = title.length() * (fontSize + 10);
		
		JLabel label = new JLabel(title, SwingConstants.CENTER);
		Font font = new Font("Arial", Font.BOLD, fontSize);
		label.setFont(font);
		label.setSize(width, new BigDecimal(fontSize * 1.5).intValue()); 
		return label;
	}
	
	protected JLabel title(String title) {
		return title(title, 20);
	}
	
	protected JButton button(String text) {
		int width = text.length() * 10 + 50;
		
		JButton jButton = new JButton(text);
		jButton.setSize(width, 20); 
		jButton.setBackground(Color.green);
		return jButton ;
	}

	protected JLabel label(Object title) {
		
		if(isNumber(title)) {
			title = StringUtil.beautifyNominal(Integer.parseInt(title.toString()));
		}
		
		int width = title.toString().length() * 10;
		
		JLabel label = new JLabel(title.toString(), SwingConstants.CENTER); 
		label.setSize(width, 20);
		return label;
	}
	
	public static boolean isNumber(Object o) {
		if( null == o) {
			return false;
		}
		
		Class objectType = o.getClass();
		
		if(objectType.equals(int.class)||objectType.equals(Integer.class)) {
			return true;
		}
		if(objectType.equals(double.class)||objectType.equals(Double.class)) {
			return true;
		}
		if(objectType.equals(long.class)||objectType.equals(Long.class)) {
			return true;
		}
		
		
		return false;
	}
	
	protected JPasswordField passwordField(String string) { 
		
		JPasswordField label = new JPasswordField(string); 
		label.setSize(100, 20);
		return label;
	}

	protected JTextField textField(String string) { 
		
		JTextField label = new JTextField(string); 
		label.setSize(100, 20);
		return label;
	}
	
	protected static void changeSize(Component component, int width, int height) {

		component.setBounds(component.getX(), component.getY(), width, height);
	}

	protected static void changeSizeHeight(Component component, int height) {

		component.setBounds(component.getX(), component.getY(), component.getWidth(), height);
	}

}
