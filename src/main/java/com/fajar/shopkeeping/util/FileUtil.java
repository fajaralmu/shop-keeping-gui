package com.fajar.shopkeeping.util;

import java.awt.Desktop;
import java.io.File;

import javax.swing.JOptionPane;

import com.fajar.shopkeeping.component.Dialogs;

public class FileUtil {
	
	/**
	 * open file
	 * @param file
	 */
	public static void openFile(File file) {

		int confirm = JOptionPane.showConfirmDialog(null, "Open File ?");

		if (confirm != 0) {
			return;
		}

		try {
			// constructor of file class having file as argument
//		File file = new File("C:\\demo\\demofile.txt");   
			if (!Desktop.isDesktopSupported())// check if Desktop is supported by Platform or not
			{
				System.out.println("not supported");
				Dialogs.error("file not supported");
				return;
			}
			Desktop desktop = Desktop.getDesktop();
			if (file.exists()) // checks file exists or not
				desktop.open(file); // opens the specified file
		} catch (Exception e) {
			e.printStackTrace();
			Dialogs.error("Error Opening File:", e.getMessage());
		}
	}
}
