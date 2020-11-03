package com.fajar.shopkeeping.pages;

import static com.fajar.shopkeeping.component.builder.ComponentBuilder.fillArray;
import static com.fajar.shopkeeping.component.builder.ComponentBuilder.label;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.fajar.shopkeeping.callbacks.BlankActionListener;
import com.fajar.shopkeeping.callbacks.Listeners;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.component.MyCustomFrame;
import com.fajar.shopkeeping.component.MyCustomPanel;
import com.fajar.shopkeeping.component.builder.ComponentBuilder;
import com.fajar.shopkeeping.component.builder.InputComponentBuilder;
import com.fajar.shopkeeping.constant.PageConstants;
import com.fajar.shopkeeping.handler.MainHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shopkeeping.util.MapUtil;
import com.fajar.shoppingmart.entity.custom.CashFlow;
import com.fajar.shoppingmart.util.EntityUtil;
import com.toedter.calendar.JDateChooser;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public abstract class BasePage {

	public static final int BASE_HEIGHT = 700;
	public static final int BASE_WIDTH = 800;
	public final SimpleDateFormat dateFormat = getDateFormat();

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
	protected MainHandler<? extends BasePage> appHandler;

	public BasePage(String title, int w, int h) {
		this.frame = new MyCustomFrame(title, w, h);
		this.WIDTH = w;
		this.HEIGHT = h;
		this.title = title;

		initMainComponent();
		preInitComponent(); 
	}

	private static SimpleDateFormat getDateFormat() {
		 
		return new SimpleDateFormat("yyyy-MM-dd");
	}

	protected void preInitComponent() {
		parentPanel.removeAll();
		initComponent();
		constructMenu();
		// create menu if menu count is > 0
		if (menuBar.getMenuCount() > 0) {
			frame.setJMenuBar(this.menuBar);
		}

		parentPanel.revalidate();
		parentPanel.repaint();
	}

	protected void exitOnClose() {
		if (null != frame) {
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		}
	}

	protected void setDefaultValues() { }

	protected void initEvent() { } 

	/**
	 * set the handler of the page
	 * 
	 * @param mainHandler
	 */
	public void setAppHandler(MainHandler<? extends BasePage> mainHandler) {
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

	protected ActionListener pageNavigation(PageConstants pageCode) {
		return appHandler.navigationListener(pageCode);
	}

	private KeyListener frameKeyListener() {
		return Listeners.keyPressedOnlyListener((KeyEvent e)->{
			int code = e.getKeyCode();
			switch (code) {
			case KeyEvent.VK_F5:
				Log.log("Refresh");
				refresh();
				break;

			default:
				break;
			}
		});
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
		return ComponentBuilder.buildPanelV2(panelRequest, (Object[]) components);
	}

	public void refresh() {
		Log.log("Refresh on super class does not affect anything");
	}

	public void onShow() {}

	protected ActionListener buttonRefreshListener() {
		return  (ActionEvent e) -> { refresh(); };
	}

	/**
	 * 
	 * @param col     column count
	 * @param colSize size for each column
	 * @return
	 */
	protected PanelRequest rowPanelRequest(int col, int colSize) {
		PanelRequest panelRequestHeader = PanelRequest.autoPanelNonScroll(col, colSize, 1, Color.orange);
		panelRequestHeader.setCenterAligment(true);
		return panelRequestHeader;
	}

	/**
	 * 
	 * @param colSizes array of column sizes
	 * @return
	 */
	protected PanelRequest rowPanelRequest(int[] colSizes) {
		PanelRequest panelRequestHeader = PanelRequest.autoPanelNonScroll(colSizes, 5, Color.orange);
		panelRequestHeader.setCenterAligment(true);
		return panelRequestHeader;
	}

	/**
	 * 
	 * @param colCount
	 * @param colSize  size of each column
	 * @param objects
	 * @return
	 */
	protected JPanel rowPanelHeader(int colCount, int colSize, Object... objects) {
		JPanel rowPanel = rowPanel(fillArray(colCount, colSize), objects);
		rowPanel.setBackground(Color.yellow);
		return rowPanel;
	}

	/**
	 * panel (as table) row
	 * 
	 * @param colSizes array of column sizes
	 * @param objects  (Component or other will converted to label)
	 * @return
	 */
	protected JPanel rowPanel(int[] colSizes, Color color, Object... objects) {

		PanelRequest panelRequest = rowPanelRequest(colSizes);
		panelRequest.setColor(color);

		Component[] components = new Component[objects.length];

		int colIndex = 0;
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] == null) {
				objects[i] = "";
			}
			// check if a component
			try {
				components[i] = (Component) objects[i];
			} catch (Exception e) {
				components[i] = InputComponentBuilder.textFieldDisabled(objects[i], colSizes[colIndex]);
				((JTextField) components[i]).setBackground(null);
			}
			colIndex++;

			if (colIndex >= colSizes.length) {
				colIndex = 0;
			}
		}
		Log.log("ROW PANEL--");
		JPanel panel = buildPanelV2(panelRequest, components);
		return panel;
	}

	/**
	 * 
	 * @param col     column count
	 * @param colSize size for each column
	 * @param color
	 * @param objects
	 * @return
	 */
	protected JPanel rowPanel(int col, int colSize, Color color, Object... objects) {
		return rowPanel(fillArray(col, colSize), color, objects);
	}

	/**
	 * 
	 * @param col     count
	 * @param colSize for each column
	 * @param objects objects (Component or other will converted to label)
	 * @return
	 */
	protected JPanel rowPanel(int col, int colSizes, Object... objects) {
		return rowPanel(col, colSizes, Color.white, objects);
	}

	/**
	 * 
	 * @param colSizes array of column sizes
	 * @param objects
	 * @return
	 */
	protected JPanel rowPanel(int[] colSizes, Object... objects) {
		return rowPanel(colSizes, Color.white, objects);
	}

	/////////////////////////////COMPONENT INSTANCES///////////////////////////////////	

	protected Object[] buildArray(int i, int i2) {

		Object[] array = new Object[i2 - i + 1];
		for (int j = i; j <= i2; j++) {
			array[j - i] = j;
		}
		return array;
	}

	protected static void log(Object... objects) {
		Log.log(objects);
	}

	/**
	 * change object field based on jTextfield's text
	 * 
	 * @param inputComponent
	 * @param elementId
	 * @return
	 */
	protected KeyListener textFieldKeyListener(final Component notUsed, final String fieldName) {
 
			final Field field = EntityUtil.getDeclaredField(getClass(), fieldName);
			final Object origin = this;
			final Class<?> fieldType = field.getType();
			field.setAccessible(true);

			return Listeners.keyReleasedOnlyListener((KeyEvent e)->{
					
					final JTextField inputComponent = (JTextField) e.getSource();
					Object value = inputComponent.getText();

					if (value == null) {
						return;
					}
					/**
					 * check type of field
					 */
					try {
						if (MapUtil.objectEquals(fieldType, int.class, Integer.class)) {
							value = Integer.valueOf(value.toString());
						}
						if (MapUtil.objectEquals(fieldType, double.class, Double.class)) {
							value = Double.valueOf(value.toString());
						}
						if (MapUtil.objectEquals(fieldType, long.class, Long.class)) {
							value = Long.valueOf(value.toString());
						}

						field.set(origin, value);
						log(field.getName(), ":", value);
					} catch (Exception e1) {
						//Dialogs.error("Error setting value for field: ", field.getName(), " the value is :", value);
						e1.printStackTrace();
						inputComponent.setText("0");
					}

				});

	}

	/**
	 * change object field based on comboBox's selected item
	 * 
	 * @param comboBox
	 * @param fieldName
	 * @return
	 */
	protected ActionListener comboBoxListener(final JComboBox<?> comboBox, String fieldName) {
		try {
			final Field field = this.getClass().getDeclaredField(fieldName);
			final Object origin = this;
			field.setAccessible(true);
			return  (ActionEvent e)->{
				Object value = comboBox.getSelectedItem();
				try {
					field.set(origin, value);
					log(field.getName(), ":", value);
				} catch (IllegalArgumentException | IllegalAccessException e1) {
					log("Error setting value for field: ", field.getName(), " the value :", value);
					e1.printStackTrace();
				} 
			};
		} catch (NoSuchFieldException | SecurityException e1) {
			return new BlankActionListener();
		}

	}

	/**
	 * change object field based on dateChooser selected date
	 * 
	 * @param dateChooser
	 * @param fieldName
	 * @return
	 */
	protected PropertyChangeListener dateChooserListener(final JDateChooser dateChooser, String fieldName) {
		try {
			final Field field = this.getClass().getDeclaredField(fieldName);
			final Object origin = this;
			field.setAccessible(true);
			return  (PropertyChangeEvent evt)->{
				Date value = dateChooser.getDate();
				try {
					field.set(origin, value);
					log(field.getName(), ":", value);
				} catch (IllegalArgumentException | IllegalAccessException e1) {
					log("Error setting value for field: ", field.getName(), " the value :", value);
					e1.printStackTrace();
				}
			};
		} catch (NoSuchFieldException | SecurityException e1) {
			return  (PropertyChangeEvent evt)->{ };
		}

	}

	public static void printSize(Component component) {
		log("Component size: ", component.getClass(), " w: ", component.getWidth(), " h: ", component.getHeight());
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

	protected Component[] toArrayOfComponentAdditionalComponentAfter(List<Component> formComponents,
			Component... additionalComponents) {

		Component[] components = new Component[formComponents.size() + additionalComponents.length];
		for (int i = 0; i < formComponents.size(); i++) {
			components[i] = formComponents.get(i);
		}
		for (int i = 0; i < additionalComponents.length; i++) {
			components[i + formComponents.size()] = additionalComponents[i];
		}
		return components;
	}

	protected Component[] toArrayOfComponentAdditionalComponentBefore(List<Component> formComponents,
			Component... additionalComponents) {

		Component[] components = new Component[formComponents.size() + additionalComponents.length];
		for (int i = 0; i < additionalComponents.length; i++) {
			components[i] = additionalComponents[i];
		}
		for (int i = 0; i < formComponents.size(); i++) {
			components[i + additionalComponents.length] = formComponents.get(i);
		}

		return components;
	}
	
}
