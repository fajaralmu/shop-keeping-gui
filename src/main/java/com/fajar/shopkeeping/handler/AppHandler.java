package com.fajar.shopkeeping.handler;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import com.fajar.shopkeeping.webservice.AccountService;

public class AppHandler {

	private static AppHandler handler;
	private AccountService accountService  = AccountService.getInstance();
	private static String applicationId = "";
	private static String loginKey = "";

	public static final int PAGE_LOGIN = 1;
	public static final int PAGE_HOME = 2;
	public static final int PAGE_LAUNCHER = 3;

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
	
	public static String getApplicationID() {
		return applicationId;
	}

	public static void setLoginKey(String loginKey2) {
		System.out.println("[Logn Key] = "+loginKey2);
		loginKey = loginKey2;
	}
	
	public static String getLoginKey() {
		return loginKey;
	}
	
	private void init() {
		handlers.put(PAGE_LAUNCHER, new LauncherHandler());
		handlers.put(PAGE_LOGIN, new LoginHandler());

		activeHandler = handlers.get(PAGE_LAUNCHER);
	}

	public void navigate(int handlerCode) {

		System.out.println("navigating to: " + handlerCode);

		if (null != activeHandler) {
			activeHandler.dismissPage();
		}
		activeHandler = handlers.get(handlerCode);

		startActiveHandler(); 
	}

	public void beginApp() {
		activeHandler = handlers.get(PAGE_LAUNCHER);

		try {
			getAppId();
		}catch (Exception e) { 
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error Occured: "+e.getMessage());
			return;
		}
		
		startActiveHandler();
	}
	
	private String getAppId() throws Exception {
		String appId = accountService.getAppId();
		System.out.println("APP ID: "+appId);
		
		if(null == appId) {
			throw new Exception("App id not generated");
		}
		
		applicationId = appId;
		
		return appId;
	}

	public void startActiveHandler() {
		activeHandler.start();
		activeHandler.setPageHandler();
	}

}
