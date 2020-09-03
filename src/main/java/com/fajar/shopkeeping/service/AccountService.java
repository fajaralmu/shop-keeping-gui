package com.fajar.shopkeeping.service;

import static com.fajar.shopkeeping.constant.WebServiceConstants.URL_LOGIN;
import static com.fajar.shopkeeping.constant.WebServiceConstants.URL_LOGOUT;
import static com.fajar.shopkeeping.constant.WebServiceConstants.URL_REQIEST_APP;
import static com.fajar.shopkeeping.util.ObjectUtil.getEmptyHashMapClass;

import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.callbacks.WebResponseCallback;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.constant.WebServiceConstants;
import com.fajar.shopkeeping.util.Log;
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

	public void getAppId(WebResponseCallback callback) {

		requestAppId(callback);

	}

	/**
	 * requesting new app ID
	 * @param callback parameter #1 : WebResponse.class
	 */
	private void requestAppId(final WebResponseCallback callback) {
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
	 * @param callback parameter #1 : Boolean.class
	 */
	public void doLogin(final String username, final String password, final MyCallback<Boolean> callback) {

		ThreadUtil.runWithLoading(new Runnable() {

			public void run() { 
				boolean success = false; 
				try {
					  
					ResponseEntity<HashMap<Object, Object>> response = callLogin(username, password);

					if (response == null || response.getBody() ==null || response.getBody().get("code").equals("00") == false) {
						throw new Exception("Invalid Response");
					}
 
					HttpHeaders responseHeaders = response.getHeaders();
					List<String> loginKey = responseHeaders.get(HEADER_LOGIN_KEY);
  
					AppSession.setLoginKey(loginKey.get(0));
					getLoggedUser();

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


	private void getLoggedUser() {
		 
		try {

			WebRequest shopApiRequest = new WebRequest();
			ResponseEntity<User> response = restTemplate.postForEntity(WebServiceConstants.URL_LOGGED_USER,
					RestComponent.buildAuthRequest(shopApiRequest, true), User.class);
			Log.log("response: ",response);
			 AppSession.setUser(response.getBody());
		} catch (Exception e) {
			Log.log("callGetEntity #ERROR");
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 
	 * @param myCallback parameter #1 :  successLogout (Boolean.class)
	 */
	public void logout(final MyCallback<Boolean> callback) {

		ThreadUtil.runWithLoading(new Runnable() {

			public void run() {

				boolean success = false;

				try { 
					WebResponse response  = callLogout();

					if (response. getCode().equals("00") == false) {
						throw new RuntimeException("Invalid Response: "+response. getCode());
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
			return WebResponse.failed(e.getMessage());
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
				RestComponent.buildAuthRequest(new WebRequest(), false), WebResponse.class);
		return response.getBody();
	}

}
