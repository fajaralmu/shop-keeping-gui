package com.fajar.shopkeeping.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fajar.shopkeeping.pages.ManagementPage;

public class ManagementHandler extends MainHandler {

	public ManagementHandler() {
		super();
	}

	@Override
	protected void init() {
		super.init();  
		page = new ManagementPage();
	}

	public List<Map> getAllEntity(Class<?> fieldType) { 
		
		List< Map> resultList = entityService.getAllEntityList(fieldType);
		return resultList;
	}

	

	 
}
