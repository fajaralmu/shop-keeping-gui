package com.fajar.shopkeeping.service;

import static com.fajar.shopkeeping.constant.WebServiceConstants.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.fajar.dto.Filter;
import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.component.Loadings;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shopkeeping.util.MapUtil;

public class EntityService extends BaseService {

	private static EntityService app;

	public static EntityService getInstance() {
		if (null == app) {
			app = new EntityService();
		}
		return app;
	}

	private EntityService() {

	}

	public void getEntityList(final int page, final int limit, final Map<String, Object> fieldFilter, final Class entityClass, final MyCallback callback) {
		Loadings.start();

		Thread thread = new Thread(new Runnable() {

			public void run() {

				try { 
					ShopApiResponse response = getEntityList(page, limit, entityClass, fieldFilter);
					callback.handle(response);
				} catch (Exception e) {
					e.printStackTrace();
					Dialogs.showErrorDialog("Error getEntityList: " + e.getMessage());
				} finally {
					Loadings.end();
				}
			}

		});
		thread.start();
	} 
	
	/**
	 * the given response is hashmap
	 * @param filter
	 * @param entityClass
	 * @param callback
	 */
	public void getEntityListHashMapResponse(final Filter filter, final Class entityClass, final MyCallback callback) {
		Loadings.start();

		Thread thread = new Thread(new Runnable() {

			public void run() {

				try {
					HashMap response = callGetEntity(filter, entityClass);
					callback.handle(response);
				} catch (Exception e) {
					e.printStackTrace();
					Dialogs.showErrorDialog("Error getEntityList: " + e.getMessage());
				} finally {
					Loadings.end();
				}
			}

		});
		thread.start();
	}
	
	/**
	 * the given response is java object [ShopApiResponse]
	 * @param filter
	 * @param entityClass
	 * @param callback
	 */
	public void getEntityListJsonResponse(final Filter filter, final Class entityClass, final MyCallback callback) {
		Loadings.start();

		Thread thread = new Thread(new Runnable() {

			public void run() {

				try {
					ShopApiResponse response = getEntityListFullResponse(filter, entityClass);
					callback.handle(response);
				} catch (Exception e) {
					e.printStackTrace();
					Dialogs.showErrorDialog("Error getEntityList: " + e.getMessage());
				} finally {
					Loadings.end();
				}
			}

		});
		thread.start();
	}

	private ShopApiResponse getEntityListFullResponse(Filter filter, Class entityClass) {

		HashMap response = callGetEntity(filter, entityClass);

		if (response.get("code").equals("00") == false) {
			return ShopApiResponse.failed();
		}

		List rawEntityList = (List) response.get("entities");

		List<BaseEntity> resultList = MapUtil.convertMapList(rawEntityList, entityClass);

		ShopApiResponse jsonResponse = new ShopApiResponse();

		jsonResponse.setEntities(resultList);
		jsonResponse.setTotalData((Integer) response.get("totalData"));

		return jsonResponse;

	}
	
	public List< Map> getAllEntityOnlyList(Class entityClass) {

		HashMap response = callGetEntity(Filter.builder().page(1).limit(0).build(), entityClass); 
		List rawEntityList = (List) response.get("entities");

		return rawEntityList;

	}

	private ShopApiResponse getEntityList(int page, int limit, Class entityClass, Map<String, Object> fieldsFilter) { 
		
		return getEntityListFullResponse(Filter.builder().page(page).fieldsFilter(fieldsFilter).limit(limit).build(), entityClass);
	}
	
	public void addNewEntity( final Map entityObject, final Class entityClass, final MyCallback myCallback) { 
		Loadings.start();
		
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					HashMap response = callAddEntity(entityObject, entityClass);
					myCallback.handle(response);
					
				}catch (Exception e) {
					e.printStackTrace();
				}finally {
					Loadings.end();
				}
			}
		});
		
		thread.start();
	}

	
	/**
	 * 
	 * ==========================================
	 *             WEBSERVICE CALLS
	 * =========================================
	 * 
	 */
	
	/**
	 * call endpoint api/entity/get
	 * @param filter
	 * @param entityClass
	 * @return
	 */
	private HashMap callGetEntity(Filter filter, Class entityClass) {
		try {

			ShopApiRequest shopApiRequest = ShopApiRequest.builder().entity(entityClass.getSimpleName().toLowerCase())
					.filter(filter).build();
			ResponseEntity<HashMap> response = restTemplate.postForEntity(URL_ENTITY_GET,
					RestComponent.buildAuthRequest(shopApiRequest, true), HashMap.class);
			Log.log("response: ",response);
			return response.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private HashMap callAddEntity(Map entityObject, Class entityClass) {
		
		try {
			Map shopApiRequest = new HashMap<>();
			shopApiRequest.put("entity", entityClass.getSimpleName().toLowerCase());
			shopApiRequest.put(entityClass.getSimpleName().toLowerCase(), entityObject);
			
			ResponseEntity<HashMap> response = restTemplate.postForEntity(URL_ENTITY_ADD,
					RestComponent.buildAuthRequest(shopApiRequest , true), HashMap.class);
			Log.log("response: ",response);
			return response.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}

}
