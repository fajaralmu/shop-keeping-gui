package com.fajar.shopkeeping.component.builder;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JTextField;

import com.toedter.calendar.JDateChooser;

public class ComponentActionListeners {
	
	public static void addActionListener(JButton button, ActionListener actionListener) {
		if (button.getActionListeners().length == 0) {
			button.addActionListener(actionListener);
		}
	}

	public static void addActionListener(JMenuItem button, ActionListener actionListener) {
		if (button.getActionListeners().length == 0) {
			button.addActionListener(actionListener);
		}
	}

	public static void addKeyListener(JTextField textfield, KeyListener actionListener,
			final boolean limitToOneListener) {

		if (!limitToOneListener) {
			textfield.addKeyListener(actionListener);
		} else if (limitToOneListener) {
			if (textfield.getKeyListeners().length == 0) {
				textfield.addKeyListener(actionListener);
			}
		}
	}

	public static void addKeyListener(JTextField textfield, KeyListener actionListener) {
		addKeyListener(textfield, actionListener, true);
	}

	public static void addActionListener(JComboBox<?> comboBox, ActionListener actionListener) {
		if (comboBox.getActionListeners().length == 0) {
			comboBox.addActionListener(actionListener);
		}
	}

	public static void addActionListener(JDateChooser dateChooser, PropertyChangeListener actionListener) {
		if (dateChooser.getPropertyChangeListeners().length == 0) {
			dateChooser.addPropertyChangeListener(actionListener);
		}
	}

}
