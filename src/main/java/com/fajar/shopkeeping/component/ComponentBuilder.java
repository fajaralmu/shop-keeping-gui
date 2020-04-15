package com.fajar.shopkeeping.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.pages.BasePage;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shopkeeping.util.StringUtil;
import com.fajar.shopkeeping.util.ThreadUtil;

public class ComponentBuilder {

	
	public static int[] fillArray(int length, int valueForAllItem) {
		 
		int[] array = new int[length];
		for(int i =0 ;i< length; i++) {
			array[i] = valueForAllItem;
		}
		return array ;
	}
	
	/**
	 * build grid panel v1
	 * @param panelRequest
	 * @param components
	 * @return
	 */
	public static JPanel buildPanel(PanelRequest panelRequest, Component... components) {

		int column = panelRequest.column;
		int width = panelRequest.width;
		int height = panelRequest.height;
		int margin = panelRequest.margin;
		Color color = panelRequest.color;

		int panelX = panelRequest.panelX;
		int panelY = panelRequest.panelY;
		int panelW = panelRequest.panelW;
		int panelH = panelRequest.panelH;
		boolean autoScroll = panelRequest.autoScroll;

		JPanel panel = new JPanel();
		int currentColumn = 0;
		int currentRow = 0;
		int size = components.length;

		for (int i = 0; i < size; i++) {

			Component component = components[i];

			if (null != component) {

				// C.setBounds(CurrentCol * Margin + (W * CurrentCol), CurrentRow * Margin + H *
				// CurrentRow, W, H);

				component.setLocation(currentColumn * margin + (width * currentColumn),
						currentRow * margin + height * currentRow);
				component.setSize(width, height);

				if (component.getClass().equals(BlankComponent.class)) {
					BlankComponent blankComponent = (BlankComponent) component;

					switch (blankComponent.reserved) {

					case BEFORE_HOR:

						Component beforeContHor = components[i - 1];

						beforeContHor.setBounds(beforeContHor.getX(), beforeContHor.getY(),
								beforeContHor.getWidth() + blankComponent.getWidth(), beforeContHor.getHeight());

						panel.remove(beforeContHor);
						components[i] = beforeContHor;
						component = beforeContHor;
						break;

					case BEFORE_VER:
						Component beforeContVer = components[i - column];

						beforeContVer.setBounds(beforeContVer.getX(), beforeContVer.getY(), beforeContVer.getWidth(),
								beforeContVer.getHeight() + blankComponent.getHeight());

						panel.remove(beforeContVer);
						components[i] = beforeContVer;
						component = beforeContVer;
						break;

					case AFTER_HOR:
					case AFTER_VER:
					default:
						break;
					}
				}

			} else {
				component = new JLabel();
			}
			currentColumn++;

			if (currentColumn == column) {
				currentColumn = 0;
				currentRow++;

			}
			// printComponentLayout(C);
			panel.add(component);
		}

		final int X = panelX == 0 ? margin : panelX;
		final int Y = panelY == 0 ? margin : panelY;
		final int finalW = panelW != 0 ? panelW : column * width + column * margin;
		final int finalH = panelH != 0 ? panelH : (currentRow + 1) * height + (currentRow + 1) * margin;

		panel.setBackground(color);
		panel.setBounds(X, Y, finalW, finalH);
		panel.setLayout(null);
		panel.setBounds(X, Y, finalW, finalH);
		panel.setSize(finalW, finalH);

		if (autoScroll) {
			panel.setAutoscrolls(false);
			panel.setAutoscrolls(true);
		}
		System.out.println("Generated Panel x:" + X + ", y:" + Y + ", width:" + finalW + ", height:" + finalH);

		return panel;
	}

	/**
	 * build grid panel v2
	 * @param panelRequest
	 * @param components
	 * @return
	 */
	public static MyCustomPanel buildPanelV2(PanelRequest panelRequest, Object... components) {

		boolean useColSizes = panelRequest.column == 0;
		Log.log("useColSizes: ",useColSizes);
		int column = useColSizes ? panelRequest.colSizes.length : panelRequest.column;
		
//		int height = panelRequest.height;
		int margin = panelRequest.margin;
		Color color = panelRequest.color;

		int panelX = panelRequest.panelX;
		int panelY = panelRequest.panelY;
		int panelW = panelRequest.panelW;
		int panelH = panelRequest.panelH;
		boolean autoScroll = panelRequest.autoScroll;

		/**
		 * set column sizes
		 */
		int[] colSizes = useColSizes ? panelRequest.colSizes : fillArray(column, panelRequest.width);
		 
		
		MyCustomPanel customPanel = new MyCustomPanel(colSizes);
		customPanel.setMargin(margin);
		customPanel.setCenterAlignment(panelRequest.isCenterAligment());

		int currentColumn = 0;
		int currentRow = 0;
		int Size = components.length;

		List<Component> tempComponents = new ArrayList<Component>();

		for (int i = 0; i < Size; i++) {

			Component currentComponent = null;
			
			if(components[i] == null) {
				components[i] = new JLabel();
			}
			
			try {
				currentComponent = (Component) components[i];
			} catch ( Exception  e) {
				currentComponent = components[i] != null ? new JLabel(String.valueOf(components[i])) : new JLabel();
			}

			currentColumn++;

			tempComponents.add(currentComponent);

			if (currentColumn == column) {
				currentColumn = 0;
				for (Component component : tempComponents) {
					customPanel.addComponent(component, currentRow);
				}
				currentRow++;

				tempComponents.clear();

			}
			printComponentLayout(currentComponent);
		}

		/**
		 * adding remaining components
		 */
		for (Component component : tempComponents) {
			customPanel.addComponent(component, currentRow);
		}

		customPanel.update();

		/**
		 * setting panel physical appearance
		 */

		final int xPos = panelX == 0 ? margin : panelX;
		final int yPos = panelY == 0 ? margin : panelY;

		final int finalWidth = customPanel.getCustomWidth();
		final int finalHeight = customPanel.getCustomHeight();
 
		customPanel.setLayout(null);
		customPanel.setBounds(xPos, yPos, finalWidth, finalHeight); 
		customPanel.setBackground(color);

		if (autoScroll && panelH > 0) {

			customPanel.setPreferredSize(new Dimension(customPanel.getCustomWidth(), customPanel.getCustomHeight())); 
			customPanel.setSize(new Dimension());
			BasePage.printSize(customPanel); 
			 
			MyCustomPanel panel  = buildScrolledPanel(customPanel, (panelW > 0? panelW : finalWidth), panelH);
			Log.log("scrollPane count: ",((JScrollPane)panel.getComponent(0)).getViewport().getView());
			printComponentLayout(panel);
			return panel;

		}
//		System.out.println(
//				"Generated Panel V2 x:" + xPos + ", y:" + yPos + ", width:" + finalWidth + ", height:" + finalHeight);

		return customPanel;
	}
	
	/**
	 * 
	 * @param component main component enabling scroll
	 * @param width
	 * @param height
	 * @return
	 */
	public static MyCustomPanel buildScrolledPanel(Component mainComponent, int width, int height) {
		
		JScrollPane scrollPane = new JScrollPane(mainComponent, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(width, height)); 
		
		MyCustomPanel panel = new MyCustomPanel();
		panel.setBounds(0, 0, width, height);
		panel.add(scrollPane);
		return panel;
	}

	public static void printComponentLayout(Component component) {
		if (null == component) {
			return;
		}
//		Log.log(component.getClass().getName() + " built--" +
//
//				StringUtil.buildString("x:", component.getX(), " y:", component.getY(), "width:", component.getWidth(),
//						"height:", component.getHeight()));
	}

	/**
	 * dynamic comboBox
	 * @param defaultValue
	 * @param values
	 * @param keyListener
	 * @param actionListener
	 * @return
	 */
	public static JComboBox buildEditableComboBox(Object defaultValue, KeyListener keyListener, ActionListener actionListener, Object... values) {
		
		JComboBox comboBox=  buildComboBox(defaultValue, actionListener, values);
		comboBox.setEditable(true);
		comboBox.getEditor().getEditorComponent() .addKeyListener(keyListener);
//		comboBox.addActionListener(actionListener);
		
		return comboBox;
	}
	
	/**
	 * common comboBox without action listener
	 * @param defaultValue
	 * @param values
	 * @return
	 */
	public static JComboBox buildComboBox(Object defaultValue, Object... values) {
		return buildComboBox(defaultValue, null, values);
	}
	
	/**
	 * common comboBox with action listener
	 * @param defaultValue
	 * @param values
	 * @return
	 */
	public static JComboBox buildComboBox(Object defaultValue, ActionListener actionListener, Object... values) {

		ComboBoxModel model = new DefaultComboBoxModel<>();
		JComboBox comboBox = new JComboBox();

		int maxSize = 0;

		for (Object object : values) {

			
			comboBox.addItem(object);
			
			JLabel label = label(object);
			if (label.getWidth() > maxSize) {
				maxSize = label.getWidth();
			}
		}

		comboBox.setSize(maxSize + 20, 20);
		
		if(null != actionListener) {
			comboBox.addActionListener(actionListener);
		}
		
		if(null != defaultValue) {
			comboBox.setSelectedItem(defaultValue);
		}
		return comboBox;
	}

	/**
	 * common jLabel
	 * @param text
	 * @param horizontalAligment SwingConstants
	 * @return
	 */
	public static JLabel label(Object text, int horizontalAligment) {
		if(null == text) {
			text = "";
		}

		if (isNumber(text)) {
			try {
				text = StringUtil.beautifyNominal(Long.parseLong(text.toString()));
			}catch (Exception e) {
				text = StringUtil.beautifyNominal(Long.parseLong(text.toString()));
			}
		}
		 
		

		JLabel label = new JLabel(text.toString(), horizontalAligment);
		label.setFont(new Font("Arial", Font.PLAIN, 15));
		int width = text.toString().length() * (label.getFont().getSize() * 2/3);
		label.setSize(width, label.getFont().getSize());
		return label;
	}
	
	/**
	 * JLabel for info only
	 * @param title
	 * @param horizontalAligment
	 * @return
	 */
	public static MyInfoLabel infoLabel(Object title, int horizontalAligment) {
		if(null == title) {
			title = "";
		}

		if (isNumber(title)) {
			try {
				title = StringUtil.beautifyNominal(Long.parseLong(title.toString()));
			}catch (Exception e) {
				title = StringUtil.beautifyNominal(Long.parseLong(title.toString()));
			}
		}
		 
		int width = title.toString().length() * 10;

		MyInfoLabel label = new MyInfoLabel(title.toString(), horizontalAligment);
		
		label.setSize(width, 20);
		return label;
	}
	
	public static JLabel label(Object title) {
		return label(title, SwingConstants.CENTER);
	}

	/**
	 * is the given object integer, double, long ?
	 * @param o
	 * @return
	 */
	public static boolean isNumber(Object o) {
		if (null == o) {
			return false;
		}

		Class objectType = o.getClass();

		if (objectType.equals(int.class) || objectType.equals(Integer.class)) {
			return true;
		}
		if (objectType.equals(double.class) || objectType.equals(Double.class)) {
			return true;
		}
		if (objectType.equals(long.class) || objectType.equals(Long.class)) {
			return true;
		}

		return false;
	}

	/**
	 * jLabel with specified fontSize
	 * @param title
	 * @param fontSize
	 * @return
	 */
	public static JLabel title(String title, int fontSize) {
		 
		int width = title.length() * (fontSize + 10);
		
		JLabel label = new JLabel(title, SwingConstants.CENTER);
		Font font = new Font("Arial", Font.BOLD, fontSize);
		label.setFont(font);
		label.setSize(width, new BigDecimal(fontSize * 1.5).intValue()); 
		return label;
	}
	
	/**
	 * build horizontally aligned components wrapped in JPanel
	 * @param colWidth
	 * @param components
	 * @return
	 */
	public static JPanel buildInlineComponent(int colWidth, Object...components) {
		for (Object object : components) {
			try {
				Component component = (Component) object;
				if(component.getWidth() > colWidth) {
					component.setSize(colWidth, component.getHeight());
				}
			} catch (Exception e) { }
		}
		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(components.length, colWidth, 3, null);
		Object[] componentsClone = components;
		return buildPanelV2(panelRequest, componentsClone );
	}
	
	/**
	 * build vertically in line components wrapped in JPanel
	 * @param colWidth
	 * @param components
	 * @return
	 */
	public static JPanel buildVerticallyInlineComponent(int colWidth, Object...components) {
		for (Object object : components) {
			try {
				Component component = (Component) object;
				if(component.getWidth() > colWidth) {
					component.setSize(colWidth, component.getHeight());
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(1, colWidth, 5, null);
		panelRequest.setCenterAligment(true);
		Object[] components_ = components;
		return buildPanelV2(panelRequest, components_ );
	}
	
	/**
	 * build vertically in line components wrapped in JPanel scroll enabled
	 * @param colWidth
	 * @param components
	 * @return
	 */
	public static JPanel buildVerticallyInlineComponentScroll(int colWidth, int height, Object...components) {
		for (Object object : components) {
			try {
				Component component = (Component) object;
				if(component.getWidth() > colWidth) {
					component.setSize(colWidth, component.getHeight());
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		PanelRequest panelRequest = PanelRequest.autoPanelScroll(1, colWidth, 5, null, height);
		Object[] components_ = components;
		return buildPanelV2(panelRequest, components_ );
	}
	
	/**
	 * common textArea, size: 100 x 100
	 * @param defaultValue
	 * @return
	 */
	public static JTextArea textarea(Object defaultValue) {
		if(null == defaultValue) {
			defaultValue = "";
		}
		
		JTextArea textArea = new JTextArea(defaultValue.toString());
		textArea.setSize(100, 50);
		textArea.setColumns(10);
		textArea.setRows(3); 
		textArea.setBackground(Color.LIGHT_GRAY);
		return textArea ;
	}
	
	/**
	 * label with image from website
	 * @param url
	 * @param width
	 * @param height
	 * @return
	 */
	public static JLabel imageLabel(final String url, final int width, final int height) {
		
		final JLabel label = new JLabel("No Preview");
		label.setSize(width, height);
		label.setBorder(BorderFactory.createLineBorder(Color.green));
		ThreadUtil.run(new Runnable() {

			@Override
			public void run() {
				Icon icon = imageIcon(url, width, height);
				label.setIcon(icon ); 
			}
			
		});
		
		return label;
	}
	
	 
	
	/**
	 * image icon from website
	 * @param url
	 * @param width
	 * @param height
	 * @return
	 */
	public static ImageIcon imageIcon(String url, int width, int height) {
		 
		URL location;
		try {
			location = new URL(url);
			ImageIcon imageIcon = new ImageIcon(location);
			Image image = imageIcon.getImage(); // transform it 
			Image newimg = image.getScaledInstance(width, height,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
			imageIcon = new ImageIcon(newimg);  // transform it back
		 
			return imageIcon;
		} catch (Exception e) {
			Log.log("Error creating image icon");
			e.printStackTrace();
		}
		return new ImageIcon();
	}
	
	/**
	 * build imageicon
	 * @param fileName
	 * @param width
	 * @param height
	 * @return
	 */
	public static ImageIcon imageIconFromFile(String fileName, int width, int height) {  
		 
		try {
			ImageIcon imageIcon = new ImageIcon(fileName);
			Image image = imageIcon.getImage(); // transform it 
			Image newimg = image.getScaledInstance(width, height,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
			imageIcon = new ImageIcon(newimg);  // transform it back
		 
			return imageIcon;
		}catch (Exception e) { 
			return new ImageIcon();
		}
		 
	}
	
	public static JButton button(Object text) { 
		return button(text, 0, 0, null) ;
	}
	
	public static JButton button(Object text, int width,  ActionListener actionListener) {
		return button(text, width, 0, actionListener);
	}
	
	public static JButton button(Object text, int width, int height, ActionListener actionListener) { 
			
		if(width == 0) {
			width = String.valueOf(text).length() * 10 + 30;
		}
		if(height == 0) {
			height =25;
		}
		
		JButton jButton = new JButton(String.valueOf(text));
		jButton.setSize(width, height); 
		jButton.setBackground(Color.LIGHT_GRAY);
		jButton.setFont(new Font("Arial", Font.PLAIN, 14));
		jButton.setBorder(new RoundedBorder(10));
		
		if(null != actionListener)
			jButton.addActionListener(actionListener);
		
		return jButton ;
	}
	
	/**
	 * build JButton with specified width & height
	 * @param text
	 * @param width
	 * @param height
	 * @return
	 */
	public static JButton button(Object text, int width, int height) {  
		return button(text, width, height, null) ;
	}

	public static JPanel blankPanel(int i, int j) {
		JPanel panel = new JPanel();
		panel.setSize(i, j);
//		panel.setBackground(Color.green);
		return panel ;
	}
}
