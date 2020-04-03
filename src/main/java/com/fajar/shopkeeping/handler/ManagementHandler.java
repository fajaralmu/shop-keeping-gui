package com.fajar.shopkeeping.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import com.fajar.dto.Filter;
import com.fajar.dto.ShopApiResponse;
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

		List<Map> resultList = entityService.getAllEntityOnlyList(fieldType);
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
		entityService.getEntityListHashMapResponse(filter, entityClass, callback);
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
				
				submitEntity();
				
			} 
			
		};
	}
	
	private ManagementPage getPage() {
		return (ManagementPage) this.page;
	}
	
	private void submitEntity() {
		int confirm = JOptionPane.showConfirmDialog(null, "Continue submit?");
		
		if(confirm != 0) {
			Log.log("Operation aborted");
			return;
		}
		
		Map<String, Object> managedObject = getPage().getManagedObject();
		String idField = getPage().getIdFieldName();
		
		Log.log("Submit managedObject: ", managedObject); 
		
		entityService.addNewEntity(managedObject, getPage().getEntityClass(), new MyCallback() {
			
			@Override
			public void handle(Object... params) throws Exception {
				HashMap response = (HashMap) params[0]; 
				
				getPage().callbackUpdateEntity(response);
			}
		});
		
	}
	
	public ActionListener filterEntity() {
		
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getEntities();				
			}
		};
	}
	
	/**
	 * get entities for populating data table
	 * @param fieldFilter
	 * @param callback
	 */
	public void getEntities( ) {	
		
		Log.log("Page: ",getPage().getSelectedPage(), "Limit: ", getPage().getSelectedLimit());
		
		entityService.getEntityList(
				Integer.parseInt(getPage().getSelectedPage()), 
				Integer.parseInt(getPage().getSelectedLimit()), 
				getPage().getFieldsFiler(), 
				getPage().getEntityClass(),
				
				new MyCallback() {
					
					@Override
					public void handle(Object... params) throws Exception {
						ShopApiResponse response = (ShopApiResponse) params[0];
						getPage().handleGetFilteredEntities(response);
					}
				});
	}


}
