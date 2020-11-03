package com.fajar.shopkeeping.component.builder;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import com.toedter.calendar.JDateChooser;

public class InputComponentBuilder {

	public static JButton submitButton(Object text) {
		JButton button = ComponentBuilder.button(text);
		button.setBackground(Color.green); 
		return button;
	}
   

	public static JPasswordField passwordField(String string) {

		JPasswordField label = new JPasswordField(string);
		label.setSize(100, 20);
		return label;
	}

	public static JTextField textField(String string) {

		JTextField textField = new JTextField(string);
		textField.setSize(100, 20);
		textField.setFont(new Font("Arial", Font.PLAIN, 15));
		return textField;
	} 

	public static JTextField textFieldDisabled(Object string, int maxWidth) {
		if (null == string) {
			string = "";
		}
		JTextField textfield = textFieldDisabled(string, 100, 20);
		int characterLength = string.toString().trim().length();
		int fontSize = textfield.getFont().getSize();
		int width = characterLength * fontSize * 2 / 3;

		if (width > maxWidth) {
			width = maxWidth;
		}

		textfield.setSize(width, fontSize);
		textfield.setBorder(null);
		return textfield;
	}

	public static JTextField textFieldDisabled(Object string, int width, int height) {

		if (null == string) {
			string = "";
		}

		JTextField textField = textField(string.toString());
		ComponentModifier.changeSize(textField, width, height);
		textField.setEditable(false);
		return textField;
	}

	public static JTextField textFieldDisabledBlank(Object string, int width, int height) {

		JTextField textField = textFieldDisabled(string, width, height);
		textField.setBackground(null);
		textField.setBorder(null);
		return textField;

	}

	/**
	 * color chooser
	 * 
	 * @return
	 */
	public static JColorChooser colorChooser() {

		JColorChooser colorChooser = new JColorChooser();
		colorChooser.setSize(100, 20);
		return colorChooser;
	}

	/**
	 * date chooser with nowDate as default value
	 * 
	 * @return
	 */
	public static JDateChooser dateChooser() {

		return dateChooser(new Date());
	}

	/**
	 * date chooser with specified default value
	 * 
	 * @param date
	 * @return
	 */
	public static JDateChooser dateChooser(Date date) {

		JDateChooser dateChooser = new JDateChooser(date);
		dateChooser.setSize(100, 20);
		return dateChooser;
	}

	public static JTextField numberField(String text) {
		final JTextField textField = textField(text);
		textField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				String value = textField.getText();
				final int l = value.length();
				if (ke.getKeyChar() >= '0' && ke.getKeyChar() <= '9') {

				} else {
					if (l == 1) {
						textField.setText("0");
					} else if (l > 1) {
						String newValue = value.replace(ke.getKeyChar() + "", "");
						textField.setText(newValue);
					}
				}
			}
		});
		return textField;
	}
 
	
	//////////////////////////////// modifier /////////////////////////////
	
	public static void setText(JTextField textField, Object text) {
		if (null == text) {
			text = "";
		}
		textField.setText(text.toString());
	}

	public static void setText(JLabel label, Object text) {
		if (null == text) {
			text = "";
		}
		label.setText(text.toString());
	}

	/**
	 * get comboBox component from KeyEvent
	 * 
	 * @param event
	 * @return
	 */
	public static JComboBox<?> getComboBoxFromEvent(KeyEvent event) {
		return (JComboBox<?>) ((Component) event.getSource()).getParent();
	}

	/**
	 * get typed text from JComboBox
	 * 
	 * @param comboBox
	 * @return
	 */
	public static String getComboBoxText(JComboBox<?> comboBox) {
		return ((JTextComponent) (comboBox).getEditor().getEditorComponent()).getText();
	}

	/**
	 * set label text to ""
	 * @param label
	 */
	public static void clearLabel(JLabel label) {
		label.setText("");
	}
	
	/**
	 * set date chooser to now
	 * @param dateChooser
	 */
	public static void clearDateChooser(JDateChooser dateChooser) {
		dateChooser.setDate(new Date());
	}
	
	/**
	 * set value to ""
	 * @param textField
	 */
	public static void clearTextField(JTextField textField) {
		textField.setText("");
	}
	
	/**
	 * remove all items and set selected value to "";
	 * @param comboBox
	 */
	public static void clearComboBox(JComboBox<?> comboBox) {
		comboBox.removeAllItems();
		comboBox.setSelectedItem("");
	}
}
