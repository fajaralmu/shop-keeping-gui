package com.fajar.shopkeeping.service;

import static com.fajar.shopkeeping.constant.WebServiceConstants.URL_ENTITY_ADD;
import static com.fajar.shopkeeping.constant.WebServiceConstants.URL_ENTITY_GET;
import static com.fajar.shopkeeping.constant.WebServiceConstants.URL_ENTITY_UPDATE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shopkeeping.util.MapUtil;
import com.fajar.shopkeeping.util.ThreadUtil;
import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.BaseEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	public void getEntityList(final Filter filter, final Class<?> entityClass, final MyCallback callback) {
		ThreadUtil.runWithLoading(new Runnable() {

			public void run() {

				try { 
					WebResponse response = getEntityListFullResponse(filter, entityClass);
					callback.handle(response);
				} catch (Exception e) {
					e.printStackTrace();
					Dialogs.error("getEntityList Error getEntityList: " + e.getMessage());
				} finally { }
			} 
		}); 
	} 
	
	/**
	 * get single entity by id, returning hashmap response
	 * @param idField
	 * @param id
	 * @param entityClass
	 * @param callback
	 */
	public void getSingleEntityByID(final String idField, final Object id, final Class<?> entityClass, final MyCallback callback) {
		 
		final Filter filter = new Filter();
		filter.setLimit(1);
		filter.setPage(0);
		filter.setExacts(true);
		filter.setContains(false);
		filter.setFieldsFilter(new HashMap<String, Object>(){ 
			private static final long serialVersionUID = -681085436003560728L;

			{
				put(idField, id);
			}
		});
		
		ThreadUtil.runWithLoading(new Runnable() {

			public void run() {

				try {
					
					Map<Object, Object> response = callGetEntity(filter , entityClass);
					List<Map> theEntities  = (List) response.get("entities");
					Map theEntity = theEntities.get(0);
					callback.handle(theEntity);
				} catch (Exception e) {
					e.printStackTrace();
					Dialogs.error("getSingleEntityByID Error getEntityList: " + e.getMessage());
				} finally { }
			} 
		});
		 
	}
	
	/**
	 * the given response is hashmap
	 * @param filter
	 * @param entityClass
	 * @param callback
	 */
	public void getEntityListHashMapResponse(final Filter filter, final Class<?> entityClass, final MyCallback callback) {
		ThreadUtil.runWithLoading(new Runnable() {

			public void run() {

				try {
					Map<Object, Object> response = callGetEntity(filter, entityClass);
					callback.handle(response);
				} catch (Exception e) {
					e.printStackTrace();
					Dialogs.error("getEntityListHashMapResponse Error getEntityList: " + e.getMessage());
				} finally { 	}
			}

		}); 
	}
	
	/**
	 * the given response is java object [WebResponse]
	 * @param filter
	 * @param entityClass
	 * @param callback
	 */
	public void getEntityListJsonResponse(final Filter filter, final Class<?> entityClass, final MyCallback callback) {
		ThreadUtil.runWithLoading(new Runnable() {

			public void run() {

				try {
					WebResponse response = getEntityListFullResponse(filter, entityClass);
					callback.handle(response);
				} catch (Exception e) {
					e.printStackTrace();
					Dialogs.error("getEntityListJsonResponse Error getEntityList: " + e.getMessage());
				} finally { }
			}

		}); 
	}

	public List< Map<Object, Object>> getAllEntityOnlyList(Class<?> entityClass) {

		Map<Object, Object> response = callGetEntity(Filter.builder().page(1).limit(0).build(), entityClass); 
		List rawEntityList = (List) response.get("entities");

		return rawEntityList;

	}

	
	public void updateEntity( final Map<String, Object> entityObject, final boolean editMode, final Class<?> entityClass, final MyCallback myCallback) { 
		ThreadUtil.runWithLoading(new Runnable() {
			
			@Override
			public void run() {
				try {
					Map<Object, Object> response = new HashMap<Object, Object>();
					
					if(editMode) {
						response = callModifyEntity(entityObject, entityClass);
					}
					else {
						response = callAddEntity(entityObject, entityClass);
					}
					myCallback.handle(response);
					
				}catch (Exception e) {
					e.printStackTrace();
				}finally { }
			}
		});
		 
	}

	private WebResponse getEntityListFullResponse(Filter filter, Class<?> entityClass) {

		Map<Object, Object> response = callGetEntity(filter, entityClass);

		if (response.get("code").equals("00") == false) {
			return WebResponse.failed();
		}

		List rawEntityList = (List) response.get("entities");

		List<BaseEntity> resultList = MapUtil.convertMapList(rawEntityList, entityClass);

		WebResponse jsonResponse = new WebResponse();

		jsonResponse.setEntities(resultList);
		jsonResponse.setTotalData((Integer) response.get("totalData"));

		return jsonResponse;

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
	private Map<Object, Object> callGetEntity(Filter filter, Class<?> entityClass) {
		try {

			WebRequest shopApiRequest = WebRequest.builder().entity(entityClass.getSimpleName().toLowerCase())
					.filter(filter).build();
			ResponseEntity<Map> response = restTemplate.postForEntity(URL_ENTITY_GET,
					RestComponent.buildAuthRequest(shopApiRequest, true), Map.class);
			Log.log("response: ",response);
			return response.getBody();
		} catch (Exception e) {
			log.error("callGetEntity #ERROR");
			e.printStackTrace();
			throw e;
		}
	}
	
	private HashMap<Object, Object> callAddEntity(Map<String, Object> entityObject, Class<?> entityClass) {
		
		try {
			Map<Object, Object> shopApiRequest = new HashMap<>();
			shopApiRequest.put("entity", entityClass.getSimpleName().toLowerCase());
			shopApiRequest.put(entityClass.getSimpleName().toLowerCase(), entityObject);
			
			ResponseEntity<HashMap> response = restTemplate.postForEntity(URL_ENTITY_ADD,
					RestComponent.buildAuthRequest(shopApiRequest , true), HashMap.class);
			Log.log("response: ",response);
			return response.getBody();
		} catch (Exception e) {
			log.error("callAddEntity #ERROR");
			e.printStackTrace();
			throw e;
		}
		
	}
	
	private HashMap<Object, Object> callModifyEntity(Map<String, Object> entityObject, Class<?> entityClass) {
		
		try {
			Map<Object, Object> shopApiRequest = new HashMap<>();
			shopApiRequest.put("entity", entityClass.getSimpleName().toLowerCase());
			shopApiRequest.put(entityClass.getSimpleName().toLowerCase(), entityObject);
			
			ResponseEntity<HashMap> response = restTemplate.postForEntity(URL_ENTITY_UPDATE,
					RestComponent.buildAuthRequest(shopApiRequest , true), HashMap.class);
			Log.log("response: ",response);
			return response.getBody();
		} catch (Exception e) {
			log.error("callModifyEntity #ERROR");
			e.printStackTrace();
			throw e;
		}
		
	}

}
