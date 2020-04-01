package com.fajar.shopkeeping.pages;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.fajar.entity.custom.CashFlow;
import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.component.MyCustomFrame;
import com.fajar.shopkeeping.component.MyCustomPanel;
import com.fajar.shopkeeping.handler.BlankActionListener;
import com.fajar.shopkeeping.handler.MainHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.util.Log;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class BasePage {
	
	public static final int BASE_HEIGHT = 700;
	public static final int BASE_WIDTH = 800; 

	public static final JLabel BLANK_LABEL = label("");

	public static final String REPORT_STUFF = "report-stuff";

	protected final MyCustomFrame frame;
	protected MyCustomFrame parentFrame;
	protected JPanel parentPanel = new JPanel();
	protected JPanel mainPanel;
	
	private final int WIDTH;
	private final int HEIGHT;
	private final String title;
	
	protected boolean authRequired;
	protected boolean beginPage;
	protected boolean closeOtherPage = true;

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
	 * @param objects (Component or other will converted to label)
	 * @return
	 */
	protected JPanel rowPanelHeader(int col, int colSizes, Object...objects) {

		PanelRequest panelRequestHeader = rowPanelRequest(col, colSizes );

		Component[] components = new Component[objects.length];
		
		for (int i = 0; i < objects.length; i++) {
			try {
				components[i] = (Component) objects[i];
			}catch (Exception e) {
				components[i]  = label(objects[i]);
			}
			
		}
		
		JPanel panelHeader = buildPanelV2(panelRequestHeader, components);
		return panelHeader;
	}
	
	/**
	 * panel (as table) row
	 * @param col
	 * @param colSizes
	 * @param objects (Component or other will converted to label)
	 * @return
	 */
	protected JPanel rowPanel(int col, int colSizes, Color color, Object...objects) {

		PanelRequest panelRequest = rowPanelRequest(col, colSizes );
		panelRequest.setColor(color);
		
		Component[] components = new Component[objects.length];
		
		for (int i = 0; i < objects.length; i++) {
			if(objects[i] == null) {
				objects[i] = "";
			}
			//check if a component
			try {
				components[i]  = (Component) objects[i];
			}catch (Exception e) {
				components[i]  = label(objects[i]);
			}
			
		}
		
		JPanel panel = buildPanelV2(panelRequest, components);
		return panel;
	}
	
	/**
	 * 
	 * @param col
	 * @param colSizes
	 * @param objects objects (Component or other will converted to label)
	 * @return
	 */
	protected JPanel rowPanel(int col, int colSizes,   Object...objects) {
		return rowPanel(col, colSizes, Color.white, objects);
	}
	
	/**
	 * ================================== COMPONENT INSTANCES
	 * ==================================
	 */

	protected JLabel title(String title, int fontSize) {
		return ComponentBuilder.title(title, fontSize);
	}
	
	protected JLabel title(String title) {
		return title(title, 20);
	}
	
	protected JButton button(String text) {
		int width = text.length() * 10 + 20;
		
		JButton jButton = new JButton(text);
		jButton.setSize(width, 20); 
		jButton.setBackground(Color.LIGHT_GRAY);
		return jButton ;
	}

	protected static JLabel label(Object title) {
		
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
	
	protected Object[] buildArray(int i, int i2) {

		Object[] array = new Object[i2 - i + 1];
		for (int j = i; j <= i2 ; j++) {
			array[j-i] = j; 
		}
		return array;
	}
	
	protected static void log(Object ...objects) {
		Log.log(objects);
	}

	protected ActionListener comboBoxListener(final JComboBox comboBox, String fieldName) {
		try {
			final Field field = this.getClass().getDeclaredField(fieldName);
			final Object origin = this;
			field.setAccessible(true);
			return new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Object value = comboBox.getSelectedItem();
					try {
						field.set(origin, value );
						log(field.getName(), ":" , value);
					} catch (IllegalArgumentException | IllegalAccessException e1) {
						log("Error stting valu for field: ",field.getName()," and value :",value);
						e1.printStackTrace();
					} 
					
				}
			};
		} catch (NoSuchFieldException | SecurityException e1) {
			return new BlankActionListener();
		}
		
	}
	
	public static void printSize(Component component) {
		log("Component size: ",component.getClass()," w: ",component.getWidth()," h: ",component.getHeight());
		try {
			JPanel panel = (MyCustomPanel) component;
			log("Panel child component: ", panel.getComponentCount());
		} catch (ClassCastException e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * increment count and amount
	 * 
	 * @param totalCashflow
	 * @param flow
	 */
	protected static void updateCountAndAmount(CashFlow totalCashflow, CashFlow flow) {
		if (null == totalCashflow) {
			totalCashflow = new CashFlow();
		}
		totalCashflow.setAmount(flow.getAmount() + totalCashflow.getAmount());
		totalCashflow.setCount(flow.getCount() + totalCashflow.getCount());

	}
}
