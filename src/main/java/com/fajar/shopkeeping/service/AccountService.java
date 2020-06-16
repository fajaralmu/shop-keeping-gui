package com.fajar.shopkeeping.service;

import static com.fajar.shopkeeping.constant.WebServiceConstants.URL_LOGIN;
import static com.fajar.shopkeeping.constant.WebServiceConstants.URL_LOGOUT;
import static com.fajar.shopkeeping.constant.WebServiceConstants.URL_REQIEST_APP;
import static com.fajar.shopkeeping.util.ObjectUtil.getEmptyHashMapClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activity.InvalidActivityException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shopkeeping.util.MapUtil;
import com.fajar.shopkeeping.util.ThreadUtil;
import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.User;

public class AccountService extends BaseService{ 

	private static AccountService app;

	public static AccountService getInstance() {
		if (null == app) {
			app = new AccountService();
		}
		return app;
	}

	private AccountService() {

	}

	public void getAppId(MyCallback callback) {

		requestAppId(callback);

	}

	/**
	 * requesting new app ID
	 * @param callback params : JsonResponse
	 */
	private void requestAppId(final MyCallback callback) {
		ThreadUtil.runWithLoading(new Runnable() {

			public void run() {

				try {
					WebResponse response = callRequestAppId();
					callback.handle(response );
				} catch (ResourceAccessException | HttpClientErrorException e) {
					Dialogs.error("Error requesting app id: " + e.getMessage());
					Dialogs.info("App terminated");
					System.exit(1);
					
				} catch (Exception e) { 
					e.printStackTrace();
				} finally {  }
			}

		}); 
	}

	/**
	 * 
	 * @param username
	 * @param password
	 * @param callback params : (boolean success)
	 */
	public void doLogin(final String username, final String password, final MyCallback callback) {

		ThreadUtil.runWithLoading(new Runnable() {

			public void run() { 
				boolean success = false; 
				try {
					  
					ResponseEntity<HashMap<Object, Object>> response = callLogin(username, password);

					if (response.getBody().get("code").equals("00") == false) {
						throw new Exception("Invalid Response");
					}

					HashMap<Object, Object> responseBody = response.getBody();
					HttpHeaders responseHeaders = response.getHeaders();
					List<String> loginKey = responseHeaders.get(HEADER_LOGIN_KEY);
 
					User responseUser = (User) MapUtil.mapToObject((Map) responseBody.get("entity"), User.class);

					AppSession.setLoginKey(loginKey.get(0));
					AppSession.setUser(responseUser);

					Dialogs.info("Login Success!");

					success = true;
				} catch (Exception e) {

					e.printStackTrace();
					Dialogs.error("Login Error: " + e.getMessage());
				} finally {

					Log.log("Login success: " + success);

					try {
						callback.handle(success);
					} catch (Exception e) {
						Log.log("Error calling back login");
						e.printStackTrace();
					}
				}
			}
		});

	}

	/**
	 * 
	 * @param myCallback params: boolean successLogout
	 */
	public void logout(final MyCallback callback) {

		ThreadUtil.runWithLoading(new Runnable() {

			public void run() {

				boolean success = false;

				try { 
					WebResponse response  = callLogout();

					if (response. getCode().equals("00") == false) {
						throw new InvalidActivityException("Invalid Response: "+response. getCode());
					}
  
					AppSession.removeLoginKey(); 
					Dialogs.info("Logout Success!");

					success = true;
				} catch (Exception e) {

					e.printStackTrace();
					Dialogs.error("Logout Error: " + e.getMessage());
				} finally {

					Log.log("Logout success: " + success); 
					try {
						callback.handle(success);
					} catch (Exception e) {
						Log.log("Error calling back Logout");
						e.printStackTrace();
					}
				}
			} 

		}); 

	}
	
	/**
	 * =================================================
	 *                 WEBSERVICE CALL
	 * =================================================           
	 * 
	 */
	
	private WebResponse callLogout() {
		try {
			ResponseEntity<WebResponse> response = restTemplate.postForEntity(URL_LOGOUT,
				RestComponent.buildEmptyAuthRequest(true), WebResponse.class);
			return response.getBody();
		}catch (Exception e) { 
			e.printStackTrace();
			return null;
		}
	}
	
	private ResponseEntity<HashMap<Object, Object>> callLogin(String username, String password) {
		User user = User.builder().username(username).password(password).build();

		final WebRequest loginRequest = WebRequest.builder().user(user).build();

		try {
			ResponseEntity<HashMap<Object, Object>> response = restTemplate.postForEntity(URL_LOGIN,
					RestComponent.buildAuthRequest(loginRequest, false), getEmptyHashMapClass());
			
			return response;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	

	private WebResponse callRequestAppId() {
		ResponseEntity<WebResponse> response = restTemplate.postForEntity(URL_REQIEST_APP,
				new WebRequest(), WebResponse.class);
		return response.getBody();
	}

}
