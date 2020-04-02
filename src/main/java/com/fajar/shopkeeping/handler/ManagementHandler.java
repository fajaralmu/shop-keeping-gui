package com.fajar.shopkeeping.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fajar.dto.Filter;
import com.fajar.shopkeeping.callbacks.MyCallback;
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

		List<Map> resultList = entityService.getAllEntityList(fieldType);
		return resultList;
	}

	public void getEnitiesFormDynamicDropdown(Class<?> entityClass, final String key, final Object value,
			MyCallback callback) {

		Map<String, Object> fieldsFilter = new HashMap<String, Object>() {
			{
				put(key, value);
			}
		};
		Filter filter = Filter.builder().page(0).limit(10).fieldsFilter(fieldsFilter).build();
		entityService.getEntityList(filter, entityClass, callback);
	}

}
