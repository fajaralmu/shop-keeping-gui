package com.fajar.shopkeeping.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import javax.swing.JFileChooser;

import org.springframework.http.ResponseEntity;

import com.fajar.dto.Filter;
import com.fajar.dto.ShopApiRequest;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.component.Loadings;
import com.fajar.shopkeeping.constant.ReportType;
import com.fajar.shopkeeping.pages.PeriodicReportPage;
import com.fajar.shopkeeping.util.FileUtil;
import com.fajar.shopkeeping.util.Log;

public class PeriodicReportHandler extends MainHandler {

	public PeriodicReportHandler() {
		super();
	}

	@Override
	protected void init() {
		super.init();
		page = new PeriodicReportPage();
	}

	public void getPeriodicCashflow(Filter filter, MyCallback callback) {
		Log.log("filter: ", filter);
		reportService.getPeriodicCashflow(filter, callback);

	}

	public void generateExcelReportDaily(final int month, final int year) {

		Filter filter = Filter.builder().month(month).year(year).build();
		ShopApiRequest shopApiRequest = ShopApiRequest.builder().filter(filter).build(); 

		MyCallback myCallback = new MyCallback() {

			@Override
			public void handle(Object... params) throws Exception {
				Log.log("Response daily excel: ", params[0]);
				ResponseEntity<byte[]> response = (ResponseEntity<byte[]>) params[0];
				Loadings.end();
				 
				String fileName = getFileName(response);
				saveFile(response.getBody(), fileName);
			}
		};
		reportService.downloadReportExcel(shopApiRequest, myCallback, ReportType.DAILY);

	}
	
	
 

	
	private PeriodicReportPage getPage() {

		return (PeriodicReportPage) page;
	}
	
	

}
