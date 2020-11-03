package com.fajar.shopkeeping.component.builder;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.fajar.shopkeeping.component.MyInfoLabel;
import com.fajar.shopkeeping.component.RoundedBorder;
import com.fajar.shopkeeping.component.RoundedButton;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shopkeeping.util.StringUtil;
import com.fajar.shopkeeping.util.ThreadUtil;

public class ComponentBuilder {

	private ComponentBuilder() {

	}

	public static int[] fillArray(int length, int valueForAllItem) {

		int[] array = new int[length];
		for (int i = 0; i < length; i++) {
			array[i] = valueForAllItem;
		}
		return array;
	}

	/**
	 * build grid panel v1
	 * 
	 * @param panelRequest
	 * @param components
	 * @return
	 */
	public static JPanel buildPanel(PanelRequest panelRequest, Component... components) {

		PanelBuilderv1 panelBuilderv1 = new PanelBuilderv1(panelRequest, components);
		return panelBuilderv1.buildPanel();
	}

	
	/**
	 * build grid panel v2
	 * 
	 * @param panelRequest
	 * @param components
	 * @return
	 */
	public static JPanel buildPanelV2(PanelRequest panelRequest, Object... components) {

		PanelBuilderv2 panelBuilder = new PanelBuilderv2(panelRequest, components);

		return panelBuilder.buildPanel();
	}
	public static JPanel buildPanelV3(PanelRequest panelRequest, Object... components) {
		
		PanelBuilderv3 panelBuilder = new PanelBuilderv3(panelRequest, components);
		
		return panelBuilder.buildPanel();
	}

	/**
	 * 
	 * @param component main component enabling scroll
	 * @param width
	 * @param height
	 * @return
	 */
	public static JPanel buildScrolledPanel(Component mainComponent, int width, int height) {

		JScrollPane scrollPane = new JScrollPane(mainComponent, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(width, height));

		JPanel panel = new JPanel();
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
	 * 
	 * @param defaultValue
	 * @param values
	 * @param keyListener
	 * @param actionListener
	 * @return
	 */
	public static <T> JComboBox<T> buildEditableComboBox(Object defaultValue, KeyListener keyListener,
			ActionListener actionListener, Object... values) {

		JComboBox<T> comboBox = buildComboBox(defaultValue, actionListener, values);
		comboBox.setEditable(true);
		comboBox.getEditor().getEditorComponent().addKeyListener(keyListener);
//		comboBox.addActionListener(actionListener);

		return comboBox;
	}

	/**
	 * common comboBox without action listener
	 * 
	 * @param defaultValue
	 * @param values
	 * @return
	 */
	public static <T> JComboBox<T> buildComboBox(Object defaultValue, Object... values) {
		return buildComboBox(defaultValue, null, values);
	}

	/**
	 * common comboBox with action listener
	 * 
	 * @param defaultValue
	 * @param values
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> JComboBox<T> buildComboBox(Object defaultValue, ActionListener actionListener, Object... values) {

		// ComboBoxModel<T> model = new DefaultComboBoxModel<T>();
		JComboBox<T> comboBox = new JComboBox<T>();

		int maxSize = 0;

		for (Object object : values) {
			try {
				comboBox.addItem((T) object);

				JLabel label = label(object);
				if (label.getWidth() > maxSize) {
					maxSize = label.getWidth();
				}
			} catch (Exception e) {
				continue;
			}
		}

		comboBox.setSize(maxSize + 20, 20);

		if (null != actionListener) {
			comboBox.addActionListener(actionListener);
		}

		if (null != defaultValue) {
			comboBox.setSelectedItem(defaultValue);
		}
		return comboBox;
	}

	/**
	 * common jLabel
	 * 
	 * @param text
	 * @param horizontalAligment SwingConstants
	 * @return
	 */
	public static JLabel label(Object text, int horizontalAligment) {
		if (null == text) {
			text = "";
		}

		if (isNumber(text)) {
			try {
				text = StringUtil.beautifyNominal(Long.parseLong(text.toString()));
			} catch (Exception e) {
				text = StringUtil.beautifyNominal(Long.parseLong(text.toString()));
			}
		}

		JLabel label = new JLabel(text.toString(), horizontalAligment);
		label.setFont(new Font("Arial", Font.PLAIN, 15));
		int width = text.toString().length() * (label.getFont().getSize() * 2 / 3);
		label.setSize(width, label.getFont().getSize());
		return label;
	}

	/**
	 * JLabel for info only
	 * 
	 * @param title
	 * @param horizontalAligment
	 * @return
	 */
	public static MyInfoLabel infoLabel(Object title, int horizontalAligment) {
		if (null == title) {
			title = "";
		}

		if (isNumber(title)) {
			try {
				title = StringUtil.beautifyNominal(Long.parseLong(title.toString()));
			} catch (Exception e) {
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
	 * 
	 * @param o
	 * @return
	 */
	public static boolean isNumber(Object o) {
		if (null == o) {
			return false;
		}

		Class<?> objectType = o.getClass();

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

	public static JLabel title(String title) {
		return title(title, 20);
	}
	public static JLabel title(String title, int fontSize) {

		int width = title.length() * (fontSize + 10);

		JLabel label = new JLabel(title, SwingConstants.CENTER);
		Font font = new Font("Arial", Font.BOLD, fontSize);
		label.setFont(font);
		label.setSize(width, BigDecimal.valueOf(fontSize * 1.5).intValue());
		return label;
	}

	/**
	 * build horizontally aligned components wrapped in JPanel
	 * 
	 * @param colWidth
	 * @param components
	 * @return
	 */
	public static JPanel buildInlineComponent(int colWidth, Object... components) {
		for (Object object : components) {
			try {
				Component component = (Component) object;
				if (component.getWidth() > colWidth) {
					component.setSize(colWidth, component.getHeight());
				}
			} catch (Exception e) {
			}
		}
		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(components.length, colWidth, 3, null);
		Object[] componentsClone = components;
		return buildPanelV2(panelRequest, componentsClone);
	}

	/**
	 * build vertically in line components wrapped in JPanel
	 * 
	 * @param colWidth
	 * @param components
	 * @return
	 */
	public static JPanel buildVerticallyInlineComponent(int colWidth, Object... components) {
		for (Object object : components) {
			try {
				Component component = (Component) object;
				if (component.getWidth() > colWidth) {
					component.setSize(colWidth, component.getHeight());
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(1, colWidth, 5, null);
		panelRequest.setCenterAligment(true);
		Object[] components_ = components;
		return buildPanelV2(panelRequest, components_);
	}

	/**
	 * build vertically in line components wrapped in JPanel scroll enabled
	 * 
	 * @param colWidth
	 * @param components
	 * @return
	 */
	public static JPanel buildVerticallyInlineComponentScroll(int colWidth, int height, Object... components) {
		for (Object object : components) {
			try {
				Component component = (Component) object;
				if (component.getWidth() > colWidth) {
					component.setSize(colWidth, component.getHeight());
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		PanelRequest panelRequest = PanelRequest.autoPanelScroll(1, colWidth, 5, null, height);
		Object[] components_ = components;
		return buildPanelV2(panelRequest, components_);
	}

	/**
	 * common textArea, size: 100 x 100
	 * 
	 * @param defaultValue
	 * @return
	 */
	public static JTextArea textarea(Object defaultValue) {
		return textarea(defaultValue, false, null);
	}
	public static JTextArea textareaDisabled(Object defaultValue) {
		return textarea(defaultValue, true, null);
	}
	
	public static JTextArea textarea(Object defaultValue, boolean disabled, Color color) {
		if (null == defaultValue) {
			defaultValue = "";
		}

		JTextArea textArea = new JTextArea(defaultValue.toString());
		textArea.setSize(100, 50);
		textArea.setColumns(10);
		textArea.setRows(3);
		textArea.setEditable(!disabled);
		if(color!=null)
			textArea.setBackground(color);
		return textArea;
	}

	/**
	 * label with image from website
	 * 
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
				label.setIcon(icon);
			}

		});

		return label;
	}

	/**
	 * image icon from website
	 * 
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
			Image newimg = image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH); // scale it the smooth
																								// way
			imageIcon = new ImageIcon(newimg); // transform it back

			return imageIcon;
		} catch (Exception e) {
			Log.log("Error creating image icon");
			e.printStackTrace();
		}
		return new ImageIcon();
	}

	/**
	 * build imageicon
	 * 
	 * @param fileName
	 * @param width
	 * @param height
	 * @return
	 */
	public static ImageIcon imageIconFromFile(String fileName, int width, int height) {

		try {
			ImageIcon imageIcon = new ImageIcon(fileName);
			Image image = imageIcon.getImage(); // transform it
			Image newimg = image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH); // scale it the smooth
																								// way
			imageIcon = new ImageIcon(newimg); // transform it back

			return imageIcon;
		} catch (Exception e) {
			return new ImageIcon();
		}

	}

	public static JButton button(Object text) {
		return button(text, Color.LIGHT_GRAY);
	}

	public static JButton button(Object text, Color color) {
		return button(text, 0, 0, color, null);
	}

	public static JButton button(Object text, int width, ActionListener actionListener) {
		return button(text, width, Color.LIGHT_GRAY, actionListener);
	}

	public static JButton button(Object text, int width, Color color, ActionListener actionListener) {
		return button(text, width, 0, color, actionListener);
	}

	public static JButton button(Object text, int width, int height, Color color, ActionListener actionListener) {

		if (width == 0) {
			width = String.valueOf(text).length() * 10 + 30;
		}
		if (height == 0) {
			height = 25;
		}

		JButton jButton = new RoundedButton(String.valueOf(text), 10);
		jButton.setSize(width, height);
		jButton.setBackground(color);
		jButton.setFont(new Font("Arial", Font.PLAIN, 14));
		jButton.setBorder(new RoundedBorder(10));

		if (null != actionListener)
			jButton.addActionListener(actionListener);

		return jButton;
	}

	/**
	 * build JButton with specified width & height
	 * 
	 * @param text
	 * @param width
	 * @param height
	 * @return
	 */
	public static JButton button(Object text, int width, int height, Color color) {
		return button(text, width, height, color, null);
	}

	public static JButton button(Object text, int width, int height) {
		return button(text, width, height, Color.LIGHT_GRAY);
	}

	public static JPanel blankPanel(int i, int j) {
		JPanel panel = new JPanel();
		panel.setSize(i, j);
//		panel.setBackground(Color.green);
		return panel;
	}

	public static JButton editButton(String text) {
		// TODO Auto-generated method stub
		return button(text, Color.orange);
	}
}
