package com.fajar.shopkeeping.handler;

import java.util.HashMap;
import java.util.Map;

public class AppHandler {

	private static AppHandler handler;

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

		startActiveHandler();
	}

	public void startActiveHandler() {
		activeHandler.start();
		activeHandler.setPageHandler();
	}

}
