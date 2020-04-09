package com.fajar.shopkeeping.pages;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.text.JTextComponent;

import com.fajar.entity.custom.CashFlow;
import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.component.MyCustomFrame;
import com.fajar.shopkeeping.component.MyCustomPanel;
import com.fajar.shopkeeping.handler.BlankActionListener;
import com.fajar.shopkeeping.handler.MainHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shopkeeping.util.MapUtil;
import com.toedter.calendar.JDateChooser;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public abstract class BasePage {
	
	public static final int BASE_HEIGHT = 700;
	public static final int BASE_WIDTH = 800; 
	protected static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public static final JLabel BLANK_LABEL = label("");

	public static final String REPORT_STUFF = "report-stuff";

	protected final MyCustomFrame frame;
	protected MyCustomFrame parentFrame;
	protected JPanel parentPanel = new JPanel();
	protected JPanel mainPanel;
	protected final JMenuBar menuBar = new JMenuBar();
	protected JMenuItem menuBack;
	
	protected boolean refreshing;
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
		constructMenu();
		//create menu if menu count is > 0
		if(menuBar.getMenuCount() > 0) {
			frame.setJMenuBar(this.menuBar);
		}
		
		parentPanel.revalidate();
		parentPanel.repaint();
	}
	
	protected void exitOnClose() {
		if(null != frame) {
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		}
	}
	
	protected void setDefaultValues() {
		
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
		setDefaultValues();
	}

	private void initMainComponent() {
		parentPanel.setBackground(Color.WHITE);
		parentPanel.setLayout(null);
		parentPanel.setBounds(10, 10, WIDTH, HEIGHT);
		parentPanel.setSize(WIDTH, HEIGHT); 
		frame.setContentPane(parentPanel);
		frame.addKeyListener(frameKeyListener());
		frame.setFocusable(true);
		frame.setFocusTraversalKeysEnabled(false);
		frame.setResizable(false);

	}
	
	protected void constructMenu() {
		Log.log("No menu present..", getClass().getSimpleName());
	}
	
	protected ActionListener pageNavigation(int pageCode) {
		return appHandler.navigationListener(pageCode);
	}

	private KeyListener frameKeyListener() { 
		return new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) { 
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) { 
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				int code = e.getKeyCode();
				switch (code) {
				case KeyEvent.VK_F5:
					Log.log("Refresh");
					refresh();
					break;

				default:
					break;
				}
				
			}
		};
	}

	public abstract void initComponent();

	public void show() {
		System.out.println("Show: " + title);
		this.frame.setVisible(true);
	}

	public void dismiss() {
		System.out.println("DISMISS :" + title);
		this.frame.setVisible(false);
		System.out.println(title + " visible: " + frame.isVisible());

	}
	
	protected void doNotCloseOtherPage() {
		setCloseOtherPage(false);
	}

	protected JPanel buildPanel(PanelRequest panelRequest, Component... components) {
		return ComponentBuilder.buildPanel(panelRequest, components);
	}
	
	protected JPanel buildPanelV2(PanelRequest panelRequest, Component... components) {
		return ComponentBuilder.buildPanelV2(panelRequest, components);
	}

	public void refresh() {
		Log.log("Refresh on super class does not affect anything");
	}

	public void onShow() {

	}

	protected ActionListener buttonRefreshListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) { 
				refresh();
			}
			
		};
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
	
	protected JButton button(Object text) {
		return ComponentBuilder.button(text);
	}
	protected JButton button(Object text, int width, ActionListener actionListener) { 
		
		return ComponentBuilder.button(text, width, actionListener);
	}
	
	

	protected static JLabel label(Object title) {
		
		return ComponentBuilder.label(title );
	}
	
	protected static JLabel label(Object title, int horizontalAligment) {
		
		return ComponentBuilder.label(title, horizontalAligment );
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
	
	protected JTextField textFieldDisabled(String string) { 
		
		JTextField label = new JTextField(string); 
		label.setSize(100, 20);
		label.setEditable(false);
		return label;
	}
	
	protected JColorChooser colorChooser() {
		
		JColorChooser colorChooser = new JColorChooser();
		colorChooser.setSize(100,20);
		return colorChooser;
	}
	
	protected JDateChooser dateChooser() { 
		
		return dateChooser(new Date()) ;
	}
	
	protected JDateChooser dateChooser(Date date) {
		
		JDateChooser dateChooser = new JDateChooser(date);
		dateChooser.setSize(100, 20);
		return dateChooser ;
	}
	 
	protected JTextField numberField(String text) {
		final JTextField textField = textField(text);
		textField.addKeyListener(new KeyAdapter() {
	         public void keyPressed(KeyEvent ke) {
	            String value = textField.getText();
	            final  int l = value.length();
	            if (ke.getKeyChar() >= '0' && ke.getKeyChar() <= '9') {
	            	 
	            } else {
	            	if(l == 1) {
	            		textField.setText("0");
	            	}else if(l > 1) {
	            		String newValue = value.replace(ke.getKeyChar()+"", "");
						textField.setText(newValue );
	            	}
	            }
	         }
	      });
		return textField;
	}
	
	protected JTextArea textArea(Object defaultValue) {
		return ComponentBuilder.textarea(defaultValue);
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

	/**
	 * change object field based on jTextfield's text
	 * @param inputComponent
	 * @param elementId
	 * @return
	 */
	protected KeyListener textFieldKeyListener(final JTextField inputComponent, final String fieldName) {
		
		try {
			final Field field = this.getClass().getDeclaredField(fieldName);
			final Object origin = this;
			final Class fieldType = field.getType();
			field.setAccessible(true);
			
			return new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) { }

				@Override
				public void keyPressed(KeyEvent e) { }

				@Override
				public void keyReleased(KeyEvent e) {
					Log.log("HELLO");
					Object value = inputComponent.getText();
					
					if(value == null) {
						return;
					}
					/**
					 * check type of field
					 */
					if(MapUtil.objectEquals(fieldType, int.class, Integer.class)) {
						value = Integer.valueOf(value.toString());
					}
					if(MapUtil.objectEquals(fieldType, double.class, Double.class)) {
						value = Double.valueOf(value.toString());
					}
					if(MapUtil.objectEquals(fieldType, long.class, Long.class)) {
						value = Long.valueOf(value.toString());
					}
					
					try {
						field.set(origin, value );
						log(field.getName(), ":" , value);
					} catch (IllegalArgumentException | IllegalAccessException e1) {
						log("Error setting value for field: ",field.getName()," the value is :",value);
						e1.printStackTrace();
					} 
					
				}  
			};
		} catch (Exception e1) { 
			Log.log("Error setting key listener");
			e1.printStackTrace();
			return null;
		}  
		
	}
	
	/**
	 * change object field based on comboBox's selected item
	 * @param comboBox
	 * @param fieldName
	 * @return
	 */
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
						log("Error setting value for field: ",field.getName()," the value :",value);
						e1.printStackTrace();
					} 
					
				}
			};
		} catch (NoSuchFieldException | SecurityException e1) {
			return new BlankActionListener();
		}
		
	}
	
	/**
	 * change object field based on dateChooser selected date
	 * @param dateChooser
	 * @param fieldName
	 * @return
	 */
	protected PropertyChangeListener dateChooserListener(final JDateChooser dateChooser, String fieldName) {
		try {
			final Field field = this.getClass().getDeclaredField(fieldName);
			final Object origin = this;
			field.setAccessible(true);
			return new PropertyChangeListener() {
 

				@Override
				public void propertyChange(PropertyChangeEvent evt) { 
					Date value = dateChooser.getDate();
					try {
						field.set(origin, value );
						log(field.getName(), ":" , value);
					} catch (IllegalArgumentException | IllegalAccessException e1) {
						log("Error setting value for field: ",field.getName()," the value :",value);
						e1.printStackTrace();
					} 
					
				}
			};
		} catch (NoSuchFieldException | SecurityException e1) {
			 return new PropertyChangeListener() { 
				@Override
				public void propertyChange(PropertyChangeEvent evt) {  }
			};
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
	
	protected Component[] toArrayOfComponent(List<Component> formComponents) {

		Component[] components = new Component[formComponents.size()];
		for (int i = 0; i < formComponents.size(); i++) {
			components[i] = formComponents.get(i);
		}
		return components;
	}
	
	protected Component[] toArrayOfComponentAdditionalComponentAfter(List<Component> formComponents, Component...additionalComponents) {

		Component[] components = new Component[formComponents.size() + additionalComponents.length];
		for (int i = 0; i < formComponents.size(); i++) {
			components[i] = formComponents.get(i);
		}
		for (int i = 0; i < additionalComponents.length; i++) {
			components[i + formComponents.size()] = additionalComponents[i];
		}
		return components;
	}
	
	protected Component[] toArrayOfComponentAdditionalComponentBefore(List<Component> formComponents, Component...additionalComponents) {

		Component[] components = new Component[formComponents.size() + additionalComponents.length];
		for (int i = 0; i < additionalComponents.length; i++) {
			components[i  ] = additionalComponents[i];
		}
		for (int i = 0; i < formComponents.size(); i++) {
			components[i + additionalComponents.length] = formComponents.get(i);
		}
		
		return components;
	}
	
	protected static void addActionListener(JButton button, ActionListener actionListener) {
		if(button.getActionListeners().length == 0) {
			button.addActionListener(actionListener);
		}
	}
	protected static void addActionListener(JMenuItem button, ActionListener actionListener) {
		if(button.getActionListeners().length == 0) { 
			button.addActionListener(actionListener);
		}
	} 
	protected static void addKeyListener(JTextField textfield, KeyListener actionListener, final boolean limitToOneListener) {
		
		if(!limitToOneListener) {
			textfield.addKeyListener(actionListener);
		}else if(limitToOneListener) {
			if(textfield.getKeyListeners().length == 0) { 
				textfield.addKeyListener(actionListener);
			} 
		}
	}
	protected static void addKeyListener(JTextField textfield, KeyListener actionListener ) {
		addKeyListener(textfield, actionListener, true);
	}
	protected static void addActionListener(JComboBox comboBox, ActionListener actionListener) {
		if( comboBox.getActionListeners().length == 0) {
			comboBox.addActionListener(actionListener);
		}
	}
	protected static void addActionListener(JDateChooser dateChooser, PropertyChangeListener actionListener) {
		if( dateChooser.getPropertyChangeListeners().length == 0) {
			dateChooser.addPropertyChangeListener(actionListener);
		}
	}
	
	
	public static JComboBox getComboBox(KeyEvent event) {
		return (JComboBox) ((Component)event.getSource()).getParent();
	}
	
	public static String getComboBoxText(JComboBox comboBox) {
		return ((JTextComponent) (comboBox).getEditor().getEditorComponent()).getText(); 
	}
}
