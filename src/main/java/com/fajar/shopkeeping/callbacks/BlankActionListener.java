package com.fajar.shopkeeping.callbacks;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.fajar.shopkeeping.util.Log;

public class BlankActionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		Log.log("NO action....");
		
	}

}
