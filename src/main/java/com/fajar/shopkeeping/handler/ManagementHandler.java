package com.fajar.shopkeeping.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fajar.dto.Filter;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.pages.ManagementPage;
import com.fajar.shopkeeping.util.Log;

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
	
	public static Map getMapFromList(String key, Object selectedValue, List<Map> list) {
		if(null == list) {
			Log.log("list is null");
			return null;
		}
		for (Map map : list) {
			if(map.get(key).equals(selectedValue)) {
				return map;
			}
		}
		
		return null;
	}

	public ActionListener submit() {
		 
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Map<String, Object> managedObject = getPage().getManagedObject();
				String idField = getPage().getIdFieldName();
				
				Log.log("Submit managedObject: ", managedObject);
				Log.log("id field:"+idField+",  value: ", managedObject.get(idField));
				
			}
		};
	}
	
	private ManagementPage getPage() {
		return (ManagementPage) this.page;
	}


}
