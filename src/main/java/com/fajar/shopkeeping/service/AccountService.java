package com.fajar.shopkeeping.service;

import static com.fajar.shopkeeping.constant.WebServiceConstants.URL_LOGIN;
import static com.fajar.shopkeeping.constant.WebServiceConstants.URL_LOGOUT;
import static com.fajar.shopkeeping.constant.WebServiceConstants.URL_REQIEST_APP;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activity.InvalidActivityException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.User;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.component.Loadings;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shopkeeping.util.MapUtil;

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
		Loadings.start();

		Thread thread = new Thread(new Runnable() {

			public void run() {

				try {
					ShopApiResponse response = callRequestAppId();
					callback.handle(response );
				} catch (ResourceAccessException | HttpClientErrorException e) {
					Dialogs.error("Error requesting app id: " + e.getMessage());
					Dialogs.info("App terminated");
					System.exit(1);
					
				} catch (Exception e) { 
					e.printStackTrace();
				} finally {
					Loadings.end();
				}
			}

		});
		thread.start();
	}

	/**
	 * 
	 * @param username
	 * @param password
	 * @param callback params : (boolean success)
	 */
	public void doLogin(final String username, final String password, final MyCallback callback) {

		Loadings.start();

		Thread thread = new Thread(new Runnable() {

			public void run() {

				boolean success = false;

				try {
					 

					ResponseEntity<HashMap> response = callLogin(username, password);

					if (response.getBody().get("code").equals("00") == false) {
						throw new InvalidActivityException("Invalid Response");
					}

					HashMap responseBody = response.getBody();
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
		thread.start();

	}

	/**
	 * 
	 * @param myCallback params: boolean successLogout
	 */
	public void logout(final MyCallback callback) {

		Loadings.start();

		Thread thread = new Thread(new Runnable() {

			public void run() {

				boolean success = false;

				try {

					ShopApiResponse response  = callLogout();

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

		thread.start();

	}
	
	/**
	 * =================================================
	 *                 WEBSERVICE CALL
	 * =================================================           
	 * 
	 */
	
	private ShopApiResponse callLogout() {
		try {
			ResponseEntity<ShopApiResponse> response = restTemplate.postForEntity(URL_LOGOUT,
				RestComponent.buildEmptyAuthRequest(true), ShopApiResponse.class);
			return response.getBody();
		}catch (Exception e) { 
			e.printStackTrace();
			return null;
		}
	}
	
	private ResponseEntity<HashMap> callLogin(String username, String password) {
		User user = User.builder().username(username).password(password).build();

		final ShopApiRequest loginRequest = ShopApiRequest.builder().user(user).build();

		try {
			ResponseEntity<HashMap> response = restTemplate.postForEntity(URL_LOGIN,
					RestComponent.buildAuthRequest(loginRequest, false), HashMap.class);
			
			return response;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	

	private ShopApiResponse callRequestAppId() {
		ResponseEntity<ShopApiResponse> response = restTemplate.postForEntity(URL_REQIEST_APP,
				new ShopApiRequest(), ShopApiResponse.class);
		return response.getBody();
	}

}
