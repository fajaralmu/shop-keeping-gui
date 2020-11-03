package com.fajar.shopkeeping.handler;

import static com.fajar.shopkeeping.constant.PageConstants.PAGE_DASHBOARD;
import static com.fajar.shopkeeping.constant.PageConstants.PAGE_LAUNCHER;
import static com.fajar.shopkeeping.constant.PageConstants.PAGE_LOGIN;
import static com.fajar.shopkeeping.constant.PageConstants.PAGE_MANAGEMENT;
import static com.fajar.shopkeeping.constant.PageConstants.PAGE_PERIODIC_REPORT;
import static com.fajar.shopkeeping.constant.PageConstants.PAGE_TRAN_SELLING;
import static com.fajar.shopkeeping.constant.PageConstants.PAGE_TRAN_SUPPLY;

import java.util.HashMap;
import java.util.Map;

import com.fajar.shopkeeping.callbacks.ApplicationException;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.constant.PageConstants;
import com.fajar.shopkeeping.pages.PurchasingTransactionPage;
import com.fajar.shopkeeping.pages.SellingTransactionPage;
import com.fajar.shopkeeping.service.AccountService;
import com.fajar.shopkeeping.service.AppSession;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shoppingmart.dto.WebResponse;

public class AppHandler {

	private static AppHandler handler;
	private AccountService accountService = AccountService.getInstance();
 

	private static MainHandler<?> activeHandler;

	private final Map<PageConstants, MainHandler<?>> handlers = new HashMap<PageConstants, MainHandler<?>>();

	public static AppHandler getInstance() {

		if (handler == null) {
			handler = new AppHandler();
		}

		return handler;
	}

	private AppHandler() {
		init();
	} 

	private void init() {
		handlers.put(PAGE_LAUNCHER, new LauncherHandler());
		handlers.put(PAGE_LOGIN, new LoginHandler());
		handlers.put(PAGE_DASHBOARD, new DashboardHandler());
		handlers.put(PAGE_PERIODIC_REPORT, new PeriodicReportHandler());
		handlers.put(PAGE_MANAGEMENT, new ManagementHandler());
		handlers.put(PAGE_TRAN_SUPPLY, new TransactionHandler(new PurchasingTransactionPage()));
		handlers.put(PAGE_TRAN_SELLING, new TransactionHandler(new SellingTransactionPage()));
		
		activeHandler = handlers.get(PAGE_LAUNCHER);
	}

	/**
	 * goto other page
	 * 
	 * @param handlerCode
	 */
	public void navigate(PageConstants handlerCode) {

		System.out.println("navigating to: " + handlerCode);

		MainHandler<?> nextHandler = handlers.get(handlerCode);
		
		if(null == nextHandler) {
			Log.log("Handler is NULL");
			return;
		}
		
		boolean closePrevHandler = nextHandler.page.isCloseOtherPage();
		
		if (null != activeHandler && closePrevHandler) {
			activeHandler.dismissPage();
		}
		activeHandler = handlers.get(handlerCode);

		startActiveHandler();
	}

	public void start() {
		activeHandler = handlers.get(PAGE_LAUNCHER);

		try {
			getAppId();
		  
		} catch (Exception e) {
			e.printStackTrace();
			Dialogs.error("Error Occured: " + e.getMessage());
			System.exit(1);
			return;
		}

		
	}

	private void getAppId() throws Exception {
		accountService.getAppId( (WebResponse params) -> { 
			try {
				WebResponse response = (WebResponse) params ; 
				String applicationId = response.getMessage();
			
				AppSession.setApplicationID(applicationId);
				AppSession.setApplicationProfile(response.getApplicationProfile());
				startActiveHandler();
			} catch (Exception e) {
				 
				throw new ApplicationException("App id not generated");
				 
			} 
		});

	}

	public void startActiveHandler() {
		activeHandler.start();
		activeHandler.setPageHandler();
	}

}
