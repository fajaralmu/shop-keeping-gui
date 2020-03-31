package com.fajar.shopkeeping.pages;

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

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class BasePage {
	
	public static final int BASE_HEIGHT = 700;
	public static final int BASE_WIDTH = 800;

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
		preInitComponent();

	}
	
	protected void preInitComponent() {
		parentPanel.removeAll();
		initComponent();
		parentPanel.revalidate();
		parentPanel.repaint();
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

	
	
	
	protected PanelRequest rowPanelRequest(int col, int colSize) {
		PanelRequest panelRequestHeader = PanelRequest.autoPanelNonScroll(col, colSize, 1, Color.orange);
		panelRequestHeader.setCenterAligment(true);
		return panelRequestHeader;
	}
	
	/**
	 * panel (as table) header & footer
	 * @param col
	 * @param colSizes
	 * @param titles
	 * @return
	 */
	protected JPanel rowPanelHeader(int col, int colSizes, Object...titles) {

		PanelRequest panelRequestHeader = rowPanelRequest(col, colSizes );

		Component[] components = new Component[titles.length];
		
		for (int i = 0; i < titles.length; i++) {
			components[i]  = label(titles[i]);
		}
		
		JPanel panelHeader = buildPanelV2(panelRequestHeader, components);
		return panelHeader;
	}
	
	/**
	 * panel (as table) row
	 * @param col
	 * @param colSizes
	 * @param titles
	 * @return
	 */
	protected JPanel rowPanel(int col, int colSizes, Color color, Object...titles) {

		PanelRequest panelRequest = rowPanelRequest(col, colSizes );
		panelRequest.setColor(color);
		
		Component[] components = new Component[titles.length];
		
		for (int i = 0; i < titles.length; i++) {
			if(titles[i] == null) {
				titles[i] = "";
			}
			//check if a component
			try {
				components[i]  = (Component) titles[i];
			}catch (Exception e) {
				components[i]  = label(titles[i]);
			}
			
		}
		
		JPanel panel = buildPanelV2(panelRequest, components);
		return panel;
	}
	
	protected JPanel rowPanel(int col, int colSizes,   Object...titles) {
		return rowPanel(col, colSizes, Color.white, titles);
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
		jButton.setBackground(Color.LIGHT_GRAY);
		return jButton ;
	}

	protected JLabel label(Object title) {
		
		return ComponentBuilder.label(title);
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
