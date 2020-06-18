package com.fajar.shopkeeping.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import javax.swing.JFileChooser;

import org.springframework.http.ResponseEntity;

import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.constant.PageConstants;
import com.fajar.shopkeeping.pages.BasePage;
import com.fajar.shopkeeping.service.AccountService;
import com.fajar.shopkeeping.service.EntityService;
import com.fajar.shopkeeping.service.ReportService;
import com.fajar.shopkeeping.service.TransactionService;
import com.fajar.shopkeeping.util.FileUtil;
import com.fajar.shopkeeping.util.Log;

public class MainHandler<T extends BasePage> {

	protected T page;
	protected static final AppHandler APP_HANDLER = AppHandler.getInstance();
	protected final AccountService accountService = AccountService.getInstance();
	protected final ReportService reportService = ReportService.getInstance();
	protected final EntityService entityService = EntityService.getInstance();
	protected final TransactionService transactionService = TransactionService.getInstance();

	public MainHandler() {
		init();
	}
	
	public MainHandler(T page) {
		this.page = page;
		init();
	}
	
	protected T getPage() {
		return page;
	}

	protected void init() {  }

	public ActionListener navigationListener(final PageConstants pageCode) {
		return new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				APP_HANDLER.navigate(pageCode);
			}
		};
	}

	public void dismissPage() {
		if (null == page) {
			System.out.println("page is null");
			return;
		}
		page.dismiss();
	}

	public void start() {
		page.show();
	}

	/**
	 * set this class as the handler of the page
	 */
	public void setPageHandler() {

		if (page == null) {

			System.out.println(this.getClass().getCanonicalName() + "Page is null");
			return;
		}

		page.setAppHandler(this);
		page.onShow();
	}
	
	protected   void saveFile(byte[] byteArray, String reportName) throws Exception { 
		
		Dialogs.info("File:", reportName, "\ngenerated successfully!\nSelect folder location to save");
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fileChooser.showOpenDialog(page.getParentPanel());

		if (returnVal == JFileChooser.APPROVE_OPTION) {  
			
			File directory = fileChooser.getSelectedFile();
			String fullPath = directory.getCanonicalPath()+"/"+reportName;
			Log.log("Fullpath: ", fullPath);
			try (FileOutputStream fos = new FileOutputStream(fullPath)) {
	
				fos.write(byteArray);
				// fos.close(); There is no more need for this line since you had created the
				// instance of "fos" inside the try. And this will automatically close the
				// OutputStream 
				
				Dialogs.info("File saved at \n", fullPath); 

				File file = new File(fullPath);
				FileUtil.openFile(file);
			}
		}
	}

	
	protected String getFileName(ResponseEntity<?> responseEntity) {
		try {
			List<String> contentDisposition = responseEntity.getHeaders().get("Content-disposition"); 
			String fileName = contentDisposition.get(0).replace("attachment; filename=", "").trim();
			
			return fileName;
		}catch (Exception e) { 
			return "New_File_"+new Date().getTime();
		}
	}

}
