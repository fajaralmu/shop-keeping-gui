package com.fajar.shopkeeping.service;

import java.util.HashMap;
import java.util.Map;

import com.fajar.shopkeeping.constant.ContextConstants;
import com.fajar.shopkeeping.model.SharedContext;

public class AppContext {

	private static final Map<ContextConstants, SharedContext> appContextMap = new HashMap<>();

	public static SharedContext getContext(ContextConstants key) {
		if( appContextMap.get(key) == null) {
			setContext(key, new SharedContext());
		}
		return appContextMap.get(key);
	}

	public static void setContext(ContextConstants key, SharedContext value) {
		appContextMap.put(key, value);
	}

	public static void clear() {
		appContextMap.clear();
	}

}
