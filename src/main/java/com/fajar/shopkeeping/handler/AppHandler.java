package com.fajar.shopkeeping.handler;

import java.util.HashMap;
import java.util.Map;

import com.fajar.dto.ShopApiResponse;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.service.AccountService;
import com.fajar.shopkeeping.service.AppSession;
import com.fajar.shopkeeping.util.Log;
import static com.fajar.shopkeeping.constant.PageConstants.*;

public class AppHandler {

	private static AppHandler handler;
	private AccountService accountService = AccountService.getInstance();


	

	private static MainHandler activeHandler;

	private final Map<Integer, MainHandler> handlers = new HashMap<Integer, MainHandler>();

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

		activeHandler = handlers.get(PAGE_LAUNCHER);
	}

	/**
	 * goto other page
	 * 
	 * @param handlerCode
	 */
	public void navigate(int handlerCode) {

		System.out.println("navigating to: " + handlerCode);

		MainHandler nextHandler = handlers.get(handlerCode);
		
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

	public void beginApp() {
		activeHandler = handlers.get(PAGE_LAUNCHER);

		try {
			getAppId();
		} catch (Exception e) {
			e.printStackTrace();
			Dialogs.showErrorDialog("Error Occured: " + e.getMessage());
			return;
		}

		
	}

	private void getAppId() throws Exception {
		accountService.getAppId(new MyCallback() {

			public void handle(Object... params) throws Exception {
				// TODO Auto-generated method stub
				try {
					ShopApiResponse response = (ShopApiResponse) params[0];
					String applicationId = response.getMessage();
				
					AppSession.setApplicationID(applicationId);
					startActiveHandler();
				} catch (Exception e) {
					// TODO: handle exception
					throw new Exception("App id not generated");
					 
				}
			}
		});

	}

	public void startActiveHandler() {
		activeHandler.start();
		activeHandler.setPageHandler();
	}

}
