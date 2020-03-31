package com.fajar.shopkeeping.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import com.fajar.dto.ShopApiResponse;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.pages.DashboardPage;

public class DashboardHandler extends MainHandler { 

	public DashboardHandler() {
		super();
	}

	@Override
	protected void init() {
		super.init();  
		page = new DashboardPage();
	}

	public ActionListener logout() { 
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				 
				accountService.logout(new MyCallback() {
					
					@Override
					public void handle(Object... params) throws Exception {
						 
						boolean success = (Boolean) params[0];
						if(success) {
							APP_HANDLER.navigate(APP_HANDLER.PAGE_LOGIN);
						}
					}
				});
			}
		};
	}
	
	public void getTodayMonthlyCashflow(MyCallback callback) {
		
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH) + 1; //January is 1
		int year = calendar.get(Calendar.YEAR);
		
		reportService.getMonthlyCashflowDetail(month, year, callback);
		
	}

	public ActionListener getDailyCashflow(final int day, final int month, final int year) {
	 
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				 Dialogs.showInfoDialog(day, "-", month, "-", year);
				 reportService.getDailyCashflowDetail(day, month, year, new MyCallback() {
					
					@Override
					public void handle(Object... params) throws Exception {

						ShopApiResponse response = (ShopApiResponse) params[0];
						System.out.println("Response DAILY: "+response);
					}
				});
			}
		};
	}
 
}
