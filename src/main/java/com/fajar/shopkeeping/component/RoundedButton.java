package com.fajar.shopkeeping.component;

import javax.swing.JButton;

public class RoundedButton extends JButton{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3443142824963343997L;
	
	int borderRadius = 0;
	
	public RoundedButton(String text, int borderRadius) {
		super(text);
		this.borderRadius = borderRadius;
		this.setBorder(new RoundedBorder(borderRadius));
	} 
}
